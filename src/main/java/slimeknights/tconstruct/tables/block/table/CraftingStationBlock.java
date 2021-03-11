package slimeknights.tconstruct.tables.block.table;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import slimeknights.tconstruct.tables.block.TableSortKeys;
import slimeknights.tconstruct.tables.block.TinkerTableBlock;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

import javax.annotation.Nonnull;

public class CraftingStationBlock extends TinkerTableBlock {

  public CraftingStationBlock(Properties builder) {
    super(builder);
  }

  @Nonnull
  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new CraftingStationTileEntity();
  }

  @Override
  public int getSortKey() {
    return TableSortKeys.CRAFTING_STATION;
  }
}
