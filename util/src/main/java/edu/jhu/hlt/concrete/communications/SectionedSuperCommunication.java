/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.communications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.UUID;

/**
 * A {@link SuperCommunication} with a {@link Map} of Section UUIDs
 * to {@link Section}s.
 * 
 * @author max
 *
 */
public class SectionedSuperCommunication extends SuperCommunication {

  protected final Map<UUID, Section> sectionIdToSectionMap;
  
  /**
   * 
   */
  public SectionedSuperCommunication(Communication comm) {
    super(comm);
    this.sectionIdToSectionMap = this.sectionIdToSectionMap();
  }
  
  /**
   * Iterate over all {@link SectionSegmentation}s and create a {@link Map} of [SectionID, Section].
   * 
   * @return a {@link Map} whose keys contain {@link Section} {@link UUID} strings, and whose values contain {@link Section} objects with that id string.
   */
  private final Map<UUID, Section> sectionIdToSectionMap() {
    final Map<UUID, Section> toRet = new HashMap<>();

    for (Section ss : this.comm.getSectionList())
      toRet.put(ss.getUuid(), ss);

    return toRet;
  }
  
  public final Map<UUID, Section> getSectionIdToSectionMap() {
    return new HashMap<>(this.sectionIdToSectionMap);
  }
  
  public final Set<UUID> getSectionIds() {
    return new HashSet<>(this.sectionIdToSectionMap.keySet());
  }
  
  public Set<String> enumerateSectionKinds() {
    Set<String> ss = new HashSet<>();
    List<Section> sectList = new ArrayList<>(this.getSectionIdToSectionMap().values());
    for (Section s : sectList)
      ss.add(s.getKind());
    
    return ss;
  }
}
