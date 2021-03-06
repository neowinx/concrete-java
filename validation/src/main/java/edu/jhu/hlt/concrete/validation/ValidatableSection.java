/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;

/**
 * @author max
 *
 */
public class ValidatableSection extends AbstractAnnotation<Section> {

  /**
   * @param annotation
   */
  public ValidatableSection(Section annotation) {
    super(annotation);
  }

  @Override
  protected boolean isValidWithComm(Communication c) {
    // No text --> false
    if (!this.printStatus("Text must be set in the comm", c.isSetText())) return false;
    // For Sections: need to ensure that this TextSpan
    // is valid in the context of the communication.
    return this.printStatus("TextSpan must be valid wrt comm", new ValidatableTextSpan(this.annotation.getTextSpan()).isValidWithComm(c));
  }

  @Override
  public boolean isValid() {
    return
        this.validateUUID(this.annotation.getUuid())
        // Hard-coded to text validity.
        // TODO: update to Audio if ever is used.
        && this.printStatus("SectionKind must be set", this.annotation.isSetKind())
        && this.printStatus("TextSpan must be set", this.annotation.isSetTextSpan())
        && this.printStatus("TextSpan must be valid", new ValidatableTextSpan(this.annotation.getTextSpan()).isValid());
  }
}
