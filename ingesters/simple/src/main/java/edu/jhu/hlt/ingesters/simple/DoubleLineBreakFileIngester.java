/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.ingesters.simple;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.communications.CommunicationFactory;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.UTF8FileIngester;
import edu.jhu.hlt.concrete.ingesters.base.util.ExistingNonDirectoryFile;
import edu.jhu.hlt.concrete.ingesters.base.util.NotFileException;
import edu.jhu.hlt.concrete.metadata.AnnotationMetadataFactory;
import edu.jhu.hlt.concrete.section.SectionFactory;
import edu.jhu.hlt.concrete.section.TextSpanKindTuple;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * Implementation of {@link UTF8FileIngester} whose {@link UTF8FileIngester#fromCharacterBasedFile(Path)}
 * implementation converts the contents of a
 * character-based file to a {@link Communication} object.
 * <ul>
 *  <li>
 *   The file name is used as the ID of the Communication.
 *  </li>
 *  <li>
 *   The Communication will contain one {@link Section} for each double-newline
 *   in the document. For example, on *nix systems, if the contents contain one
 *   instance of '\n\n', the Communication will have two Sections.
 *  </li>
 * </ul>
 */
public class DoubleLineBreakFileIngester implements UTF8FileIngester {

  private static final Logger logger = LoggerFactory.getLogger(DoubleLineBreakFileIngester.class);

  private final String sectionKindLabel;
  private final String lineSep = System.lineSeparator();
  private final String doubleLineSep = lineSep + lineSep;

  private final String commKind;
  private final AnnotationMetadata md;

  /**
   * Expect UTF-8 documents.
   */
  public DoubleLineBreakFileIngester(String commKind, String sectionKindLabel) {
    this.commKind = commKind;
    this.sectionKindLabel = sectionKindLabel;
    this.md = AnnotationMetadataFactory.fromCurrentLocalTime()
        .setTool("CompleteFileIngester (Kind: " + commKind + ")");
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.ingesters.base.FileIngester#fromCharacterBasedFile(java.nio.file.Path, java.nio.charset.Charset)
   */
  @Override
  public Communication fromCharacterBasedFile(Path path) throws IngestException {
    try {
      ExistingNonDirectoryFile f = new ExistingNonDirectoryFile(path);
      try(InputStream is = Files.newInputStream(path);) {
        String content = IOUtils.toString(is, StandardCharsets.UTF_8);
        Communication c = CommunicationFactory.create(f.getName(), content);
        c.setType(this.commKind);
        c.setMetadata(this.getMetadata());

        String[] split2xNewline = content.split(doubleLineSep);
        Stream.Builder<TextSpanKindTuple> stream = Stream.builder();
        int charCtr = 0;
        for (String s : split2xNewline) {
          final int len = s.length();
          TextSpan ts = new TextSpan(charCtr, len + charCtr);
          charCtr = len + 2;
          stream.add(new TextSpanKindTuple(ts, this.sectionKindLabel));
        }

        Stream<Section> sections = SectionFactory.fromTextSpanStream(stream.build());
        sections.forEach(s -> c.addToSectionList(s));
        return c;
      } catch (IOException e) {
        throw new IngestException("Caught exception reading in document.", e);
      } catch (ConcreteException e) {
        throw new IngestException(e);
      }
    } catch (NoSuchFileException | NotFileException e) {
      throw new IngestException("Path did not exist or was a directory.", e);
    }
  }

  @Override
  public String getKind() {
    return this.commKind;
  }

  @Override
  public AnnotationMetadata getMetadata() {
    return this.md;
  }
}
