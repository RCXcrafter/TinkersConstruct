package slimeknights.tconstruct.library.recipe.partbuilder;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

import javax.annotation.Nullable;

public class PartRecipeSerializer extends RecipeSerializer<PartRecipe> {
  @Override
  public PartRecipe read(ResourceLocation recipeId, JsonObject json) {
    String group = JSONUtils.getString(json, "group", "");
    ResourceLocation pattern = new ResourceLocation(JSONUtils.getString(json, "pattern"));
    int cost = JSONUtils.getInt(json, "cost");

    // output fetch as a material item, its an error if it does not implement that interface
    JsonObject output = JSONUtils.getJsonObject(json, "result");
    IMaterialItem item = RecipeHelper.deserializeItem(JSONUtils.getString(output, "item"), "result", IMaterialItem.class);
    int count = JSONUtils.getInt(output, "count", 1);

    return new PartRecipe(recipeId, group, pattern, cost, item, count);
  }

  @Nullable
  @Override
  public PartRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
    try {
      String group = buffer.readString(32767);
      ResourceLocation pattern = new ResourceLocation(buffer.readString(32767));
      int cost = buffer.readInt();
      // output must be a material item
      IMaterialItem item = RecipeHelper.readItem(buffer, IMaterialItem.class);
      int count = buffer.readByte();
      return new PartRecipe(recipeId, group, pattern, cost, item, count);
    } catch (Exception e) {
      TConstruct.log.error("Error reading material recipe from packet.", e);
      throw e;
    }
  }

  @Override
  public void write(PacketBuffer buffer, PartRecipe recipe) {
    try {
      buffer.writeString(recipe.group);
      buffer.writeString(recipe.pattern.toString());
      buffer.writeInt(recipe.cost);
      RecipeHelper.writeItem(buffer, recipe.output);
      buffer.writeByte(recipe.outputCount);
    } catch (Exception e) {
      TConstruct.log.error("Error writing material recipe to packet.", e);
      throw e;
    }
  }
}
