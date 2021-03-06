/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.analytics.base;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.metadata.tools.SafeTooledAnnotationMetadata;

/**
 * Top level interface for Concrete analytics. Loosely defined as
 * taking in a {@link Communication} and outputting a {@link Communication}
 * with some additions.
 */
public interface Analytic extends SafeTooledAnnotationMetadata {
  /**
   * @param c a {@link Communication} to annotate. It is up to the analytic to determine
   * if this communication is valid for annotation.
   * @return a {@link Communication} object with the analytic's annotations
   * @throws AnalyticException on analytic error
   */
  public Communication annotate(Communication c) throws AnalyticException;
}
