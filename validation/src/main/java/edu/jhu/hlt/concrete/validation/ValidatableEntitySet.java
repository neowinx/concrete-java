/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.validation;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.communications.SuperCommunication;

/**
 * @author max
 *
 */
public class ValidatableEntitySet extends AbstractAnnotation<EntitySet> {

  /**
   * @param annotation
   */
  public ValidatableEntitySet(EntitySet annotation) {
    super(annotation);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValidWithComm(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  protected boolean isValidWithComm(Communication c) {
    SuperCommunication sc = new SuperCommunication(c);
    Map<UUID, EntityMention> idToMenMap = sc.generateEntityMentionIdToEntityMentionMap();
    Set<UUID> emsIdSet = idToMenMap.keySet();
    
    boolean valid = true;
    Iterator<Entity> entIter = this.annotation.getEntityListIterator();
    while (valid && entIter.hasNext()) {
      Entity e = entIter.next();
      UUID eId = e.getUuid();
      valid = this.printStatus("Entity " + eId.toString() + " must be valid.", new ValidatableEntity(e, emsIdSet).validate(c));
    }

    return valid;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValid()
   */
  @Override
  public boolean isValid() {
    boolean init = this.validateUUID(this.annotation.getUuid())
        && this.printStatus("Metadata must be valid", new ValidatableMetadata(this.annotation.getMetadata()).isValid())
        && this.printStatus("EntityList must be set", this.annotation.isSetEntityList());
    if (init) {
      boolean valid = true;
      Iterator<Entity> entIter = this.annotation.getEntityListIterator();
      while (valid && entIter.hasNext()) {
        Entity e = entIter.next();
        UUID eId = e.getUuid();
        valid = this.printStatus("Entity " + eId.toString() + " must be valid", new ValidatableEntity(e).isValid()); 
      }
      
      return valid;
    } else
      return false;
  }

}
