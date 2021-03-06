/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.analytics.simple;

import java.util.ArrayList;
import java.util.List;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.analytics.base.Analytic;
import edu.jhu.hlt.concrete.analytics.base.AnalyticException;
import edu.jhu.hlt.concrete.communications.SuperCommunication;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.SuperTextSpan;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.tift.Tokenizer;

/**
 * A wrapper around Tift that provides {@link Tokenization} objects
 * for each {@link Sentence} in each {@link Section}.
 */
public class TiftTokenizer implements Analytic {

  private final Tokenizer tokenizer;

  /**
   * @param tokenizer the {@link Tokenizer} to use
   * @see Tokenizer
   */
  public TiftTokenizer(Tokenizer tokenizer) {
    this.tokenizer = tokenizer;
  }

  /**
   * Default to a {@link Tokenizer#PTB} tokenization.
   */
  public TiftTokenizer() {
    this.tokenizer = Tokenizer.PTB;
  }

  @Override
  public Communication annotate(Communication comm) throws AnalyticException {
    Communication cp = new Communication(comm);
    SuperCommunication sc = new SuperCommunication(cp);
    // backing map is a LinkedHashMap - ordering should be OK
    List<Sentence> sentences = new ArrayList<>(sc.generateSentenceIdToSentenceMap().values());
    for (Sentence st : sentences) {
      SuperTextSpan sts = new SuperTextSpan(st.getTextSpan(), cp);
      String sentenceText = sts.getText();
      Tokenization t = this.tokenizer.tokenizeSentence(sentenceText, 0, st.getUuid());
      // override metadata (should be patched later)
      t.setMetadata(TooledMetadataConverter.convert(this));
      st.setTokenization(t);
    }

    return cp;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.safe.metadata.SafeAnnotationMetadata#getTimestamp()
   */
  @Override
  public long getTimestamp() {
    return Timing.currentLocalTime();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolName()
   */
  @Override
  public String getToolName() {
    return TiftTokenizer.class.getSimpleName();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolVersion()
   */
  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolNotes()
   */
  @Override
  public List<String> getToolNotes() {
    return new ArrayList<String>();
  }
}
