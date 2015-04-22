/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.metadata.tools;

import edu.jhu.hlt.concrete.Digest;
import edu.jhu.hlt.concrete.safe.metadata.SafeAnnotationMetadata;

/**
 * Interface that extends the functionality of {@link SafeAnnotationMetadata},
 * requiring implementation a {@link MetadataTool}.
 * <br>
 * <br>
 * Additionally, contains a default implementation of {@link SafeAnnotationMetadata#getTool()}
 * that uses the provided MetadataTool to create a well-formed Tool string.
 */
public interface SafeTooledAnnotationMetadata extends SafeAnnotationMetadata, MetadataTool {

  @Override
  default String getToolName() {
    return this.getClass().getName();
  }
  
  /**
   * By default, use {@link NewlineMetadataToolBuilder#generateToolString(MetadataTool)}
   * to generate a parseable Tool string. 
   * <br>
   * <br>
   * Implementers should generally not override this implementation.
   */
  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.concrete.safe.metadata.SafeAnnotationMetadata#getTool()
   */
  @Override
  default String getTool() {
    final StringBuilder sb = new StringBuilder();
    sb.append(this.getToolName());
    sb.append(" ");
    sb.append(this.getToolVersion());
    return sb.toString();
  }
  
  /**
   * @return a {@link Digest} with the string list field populated
   * with notes about the tool.
   */
  default Digest getToolNotesDigest() {
    return new Digest().setStringList(this.getToolNotes());
  }
}