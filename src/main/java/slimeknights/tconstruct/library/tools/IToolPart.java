package slimeknights.tconstruct.library.tools;

import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

/**
 * Any Class that's used as a tool part needs to implement this.
 */
public interface IToolPart extends IMaterialItem {
  /**
   * Gets the stat type for the given item, limits which materials are supported
   * @return  Stat type for the given item
   */
  MaterialStatsId getStatType();
}
