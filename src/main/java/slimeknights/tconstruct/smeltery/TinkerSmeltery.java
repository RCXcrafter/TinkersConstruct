package slimeknights.tconstruct.smeltery;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.registration.object.WallBuildingBlockObject;
import slimeknights.mantle.util.SupplierItemGroup;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.recipe.casting.ContainerFillingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ContainerFillingRecipeSerializer;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeSerializer;
import slimeknights.tconstruct.library.recipe.casting.MaterialCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.MaterialCastingRecipeSerializer;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.shared.block.ClearGlassPaneBlock;
import slimeknights.tconstruct.smeltery.block.CastingBasinBlock;
import slimeknights.tconstruct.smeltery.block.CastingTableBlock;
import slimeknights.tconstruct.smeltery.block.FaucetBlock;
import slimeknights.tconstruct.smeltery.block.MelterBlock;
import slimeknights.tconstruct.smeltery.block.SearedBlock;
import slimeknights.tconstruct.smeltery.block.SearedGlassBlock;
import slimeknights.tconstruct.smeltery.block.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.SearedTankBlock.TankType;
import slimeknights.tconstruct.smeltery.data.SmelteryRecipeProvider;
import slimeknights.tconstruct.smeltery.inventory.MelterContainer;
import slimeknights.tconstruct.smeltery.item.TankItem;
import slimeknights.tconstruct.smeltery.tileentity.AbstractCastingTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryComponentTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Contains logic for the multiblocks in the mod
 */
@SuppressWarnings("unused")
public final class TinkerSmeltery extends TinkerModule {
  /** Tab for all blocks related to the smeltery */
  public static final ItemGroup TAB_SMELTERY = new SupplierItemGroup(TConstruct.modID, "smeltery", () -> new ItemStack(TinkerSmeltery.searedTank.get(TankType.TANK)));
  public static final Logger log = Util.getLogger("tinker_smeltery");

  /* Bricks */
  /* Crafting related items */

  /*
   * Block base properties
   */
  private static final Item.Properties SMELTERY_PROPS = new Item.Properties().group(TAB_SMELTERY);
  private static final Function<Block,? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, SMELTERY_PROPS);

  /*
   * Blocks
   */
  private static final Block.Properties SMELTERY_GLASS = builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).hardnessAndResistance(3.0F, 9.0F).notSolid().setSuffocates((s,w,p) -> false);
  public static final ItemObject<Block> grout = BLOCKS.register("grout", GENERIC_SAND_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<SearedGlassBlock> searedGlass = BLOCKS.register("seared_glass", () -> new SearedGlassBlock(SMELTERY_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ClearGlassPaneBlock> searedGlassPane = BLOCKS.register("seared_glass_pane", () -> new ClearGlassPaneBlock(SMELTERY_GLASS), TOOLTIP_BLOCK_ITEM);

  // seared
  /** Properties for all smeltery blocks */
  private static final Block.Properties SMELTERY = builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).hardnessAndResistance(3.0F, 9.0F);
  /** Constructor to create a seared block */
  private static final Supplier<SearedBlock> SEARED_BLOCK = () -> new SearedBlock(SMELTERY);
  public static final BuildingBlockObject searedStone = BLOCKS.registerBuilding("seared_stone", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final WallBuildingBlockObject searedCobble = BLOCKS.registerWallBuilding("seared_cobble", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedPaver = BLOCKS.registerBuilding("seared_paver", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final WallBuildingBlockObject searedBricks = BLOCKS.registerWallBuilding("seared_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedCrackedBricks = BLOCKS.register("seared_cracked_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedFancyBricks = BLOCKS.register("seared_fancy_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedTriangleBricks = BLOCKS.register("seared_triangle_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);

  /** Properties for a faucet block */
  private static final Block.Properties FAUCET = builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).hardnessAndResistance(3.0F, 9.0F).notSolid();
  public static final EnumObject<TankType,SearedTankBlock> searedTank = BLOCKS.registerEnum("seared", SearedTankBlock.TankType.values(), (type) -> new SearedTankBlock(SMELTERY_GLASS), (b) -> new TankItem(b, SMELTERY_PROPS));
  public static final ItemObject<FaucetBlock> searedFaucet = BLOCKS.register("faucet", () -> new FaucetBlock(FAUCET), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<CastingBasinBlock> castingBasin = BLOCKS.register("casting_basin", () -> new CastingBasinBlock(SMELTERY), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<CastingTableBlock> castingTable = BLOCKS.register("casting_table", () -> new CastingTableBlock(SMELTERY), TOOLTIP_BLOCK_ITEM);

  // controllers
  private static final Block.Properties MELTER = builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).hardnessAndResistance(3.0F, 9.0F).setLightLevel(s -> s.get(MelterBlock.ACTIVE) ? 13 : 0).notSolid();
  public static final ItemObject<MelterBlock> searedMelter = BLOCKS.register("melter", () -> new MelterBlock(MELTER), TOOLTIP_BLOCK_ITEM);

  /*
   * Tile entities
   */
  public static final RegistryObject<TileEntityType<SmelteryComponentTileEntity>> smelteryComponent = TILE_ENTITIES.register("smeltery_component", SmelteryComponentTileEntity::new, (set) -> {
    set.addAll(searedStone.values());
    set.addAll(searedCobble.values());
    set.addAll(searedBricks.values());
    set.addAll(searedPaver.values());
    set.add(searedCrackedBricks.get(), searedFancyBricks.get(), searedTriangleBricks.get());
  });
  public static final RegistryObject<TileEntityType<TankTileEntity>> tank = TILE_ENTITIES.register("tank", TankTileEntity::new, (set) -> set.addAll(searedTank.values()));
  public static final RegistryObject<TileEntityType<FaucetTileEntity>> faucet = TILE_ENTITIES.register("faucet", FaucetTileEntity::new, searedFaucet);
  public static final RegistryObject<TileEntityType<AbstractCastingTileEntity>> basin = TILE_ENTITIES.register("basin", AbstractCastingTileEntity.Basin::new, castingBasin);
  public static final RegistryObject<TileEntityType<AbstractCastingTileEntity>> table = TILE_ENTITIES.register("table", AbstractCastingTileEntity.Table::new, castingTable);
  public static final RegistryObject<TileEntityType<MelterTileEntity>> melter = TILE_ENTITIES.register("melter", MelterTileEntity::new, searedMelter);

  /*
   * Items
   */
  public static final ItemObject<Item> searedBrick = ITEMS.register("seared_brick", SMELTERY_PROPS);
  public static final ItemObject<Item> blankCast = ITEMS.register("blank_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> ingotCast = ITEMS.register("ingot_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> nuggetCast = ITEMS.register("nugget_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> gemCast = ITEMS.register("gem_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> pickaxeHeadCast = ITEMS.register("pickaxe_head_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> smallBindingCast = ITEMS.register("small_binding_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> toolRodCast = ITEMS.register("tool_rod_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> toughToolRodCast = ITEMS.register("tough_tool_rod_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> largePlateCast = ITEMS.register("large_plate_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> swordBladeCast = ITEMS.register("sword_blade_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> hammerHeadCast = ITEMS.register("hammer_head_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> wideGuardCast = ITEMS.register("wide_guard_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> shovelHeadCast = ITEMS.register("shovel_head_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> axeHeadCast = ITEMS.register("axe_head_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> kamaHeadCast = ITEMS.register("kama_head_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> toughBindingCast = ITEMS.register("tough_binding_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> excavatorHeadCast = ITEMS.register("excavator_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> scytheHeadCast = ITEMS.register("scythe_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> sharpeningKitCast = ITEMS.register("sharpening_kit_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> largeSwordBladeCast = ITEMS.register("large_sword_blade_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> knifeBladeCast = ITEMS.register("knife_blade_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> bowLimbCast = ITEMS.register("bow_limb_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> handGuardCast = ITEMS.register("hand_guard_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> crossGuardCast = ITEMS.register("cross_guard_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> gearCast = ITEMS.register("gear_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> broadAxeHeadCast = ITEMS.register("broad_axe_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> arrowHeadCast = ITEMS.register("arrow_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> panHeadCast = ITEMS.register("pan_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> signHeadCast = ITEMS.register("sign_head_cast", SMELTERY_PROPS);


  /*
   * Recipe
   */
  // casting
  public static final RegistryObject<ItemCastingRecipeSerializer<ItemCastingRecipe.Basin>> basinRecipeSerializer = RECIPE_SERIALIZERS.register("casting_basin", () -> new ItemCastingRecipeSerializer<>(ItemCastingRecipe.Basin::new));
  public static final RegistryObject<ItemCastingRecipeSerializer<ItemCastingRecipe.Table>> tableRecipeSerializer = RECIPE_SERIALIZERS.register("casting_table", () -> new ItemCastingRecipeSerializer<>(ItemCastingRecipe.Table::new));
  public static final RegistryObject<MaterialCastingRecipeSerializer<MaterialCastingRecipe.Basin>> basinMaterialSerializer = RECIPE_SERIALIZERS.register("basin_casting_material", () -> new MaterialCastingRecipeSerializer<>(MaterialCastingRecipe.Basin::new));
  public static final RegistryObject<MaterialCastingRecipeSerializer<MaterialCastingRecipe.Table>> tableMaterialSerializer = RECIPE_SERIALIZERS.register("table_casting_material", () -> new MaterialCastingRecipeSerializer<>(MaterialCastingRecipe.Table::new));
  public static final RegistryObject<ContainerFillingRecipeSerializer<ContainerFillingRecipe.Basin>> basinFillingRecipeSerializer = RECIPE_SERIALIZERS.register("basin_filling", () -> new ContainerFillingRecipeSerializer<>(ContainerFillingRecipe.Basin::new));
  public static final RegistryObject<ContainerFillingRecipeSerializer<ContainerFillingRecipe.Table>> tableFillingRecipeSerializer = RECIPE_SERIALIZERS.register("table_filling", () -> new ContainerFillingRecipeSerializer<>(ContainerFillingRecipe.Table::new));
  // melting
  public static final RegistryObject<IRecipeSerializer<MeltingRecipe>> meltingSerializer = RECIPE_SERIALIZERS.register("melting", MeltingRecipe.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<MaterialMeltingRecipe>> materialMeltingSerializer = RECIPE_SERIALIZERS.register("material_melting", MaterialMeltingRecipe.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<MeltingFuel>> fuelSerializer = RECIPE_SERIALIZERS.register("melting_fuel", MeltingFuel.Serializer::new);

  /*
   * Inventory
   */
  public static final RegistryObject<ContainerType<MelterContainer>> melterContainer = CONTAINERS.register("melter", MelterContainer::new);

  /*
   * Smeltery block lists
   */
  public static Set<Block> validSmelteryBlocks;
  public static Set<Block> searedStairsSlabs;
  public static Set<Block> validTinkerTankBlocks;
  public static Set<Block> validTinkerTankFloorBlocks;
  @SubscribeEvent
  void registerBlockLists(final FMLCommonSetupEvent event) {
    ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
    builder.add(TinkerSmeltery.searedStone.get());
    builder.add(TinkerSmeltery.searedCobble.get());
    builder.add(TinkerSmeltery.searedBricks.get());
    builder.add(TinkerSmeltery.searedCrackedBricks.get());
    builder.add(TinkerSmeltery.searedFancyBricks.get());
    builder.add(TinkerSmeltery.searedTriangleBricks.get());
    builder.add(TinkerSmeltery.searedPaver.get());
    ImmutableSet<Block> searedBlocks = builder.build();

    // smeltery adds in tank, glass and drains
    builder = ImmutableSet.builder();
    builder.addAll(searedBlocks);
    builder.addAll(TinkerSmeltery.searedTank.values());
    //builder.add(smelteryIO);
    builder.add(TinkerSmeltery.searedGlass.get());

    // same blocks right now for smeltery and tinker tank
    validSmelteryBlocks = builder.build();
    validTinkerTankBlocks = builder.build();
    // tinker tank floor disallows tanks
    builder = ImmutableSet.builder();
    builder.addAll(searedBlocks);
    builder.add(TinkerSmeltery.searedGlass.get());
    //builder.add(smelteryIO);
    validTinkerTankFloorBlocks = builder.build();

    // seared furnace ceiling blocks, no smelteryIO or seared glass
    // does not affect sides, those are forced to use seared blocks/tanks where relevant
    builder = ImmutableSet.builder();
    builder.addAll(TinkerSmeltery.searedStone.values());
    builder.addAll(TinkerSmeltery.searedCobble.values());
    builder.addAll(TinkerSmeltery.searedBricks.values());
    builder.add(searedCrackedBricks.get(), searedFancyBricks.get(), searedTriangleBricks.get());
    builder.addAll(TinkerSmeltery.searedPaver.values());
    searedStairsSlabs = builder.build();
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new SmelteryRecipeProvider(datagenerator));
    }
  }
}
