package slimeknights.tconstruct.tables.client.inventory.library.slots;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import slimeknights.tconstruct.library.tools.item.ToolCore;

import java.util.List;

public class SlotInformation {

  @Getter
  private final List<SlotPosition> points;
  @Getter
  private final SlotPosition toolSlot;
  @Getter
  private final ItemStack itemStack;
  @Getter
  private final int sortIndex;
  public ItemStack toolForRendering;

  public SlotInformation(List<SlotPosition> points, SlotPosition toolSlot, ItemStack itemStack, int sortIndex) {
    this.points = points;
    this.toolSlot = toolSlot;
    this.itemStack = itemStack;
    this.sortIndex = sortIndex;
  }

  /**
   * Creates a new instance of SlotInformation from a json
   *
   * @param json the json object
   * @return a instance of SlotInformation that contains all the points, sort index and tool
   */
  public static SlotInformation fromJson(JsonObject json) {
    List<SlotPosition> slots = SlotPosition.listFromJson(json, "slots");
    ItemStack stack = ItemStack.EMPTY;

    if (json.has("item")) {
      stack = new ItemStack(JSONUtils.getItem(json, "item"));
    }

    SlotPosition slotPosition = new SlotPosition(-1, -1);

    if (json.has("tool")) {
      slotPosition = SlotPosition.fromJson(json.get("tool").getAsJsonObject());
    }

    int sortIndex = JSONUtils.getInt(json, "sortIndex");

    return new SlotInformation(slots, slotPosition, stack, sortIndex);
  }

  /**
   * Gets the item to use for rendering in the client's screen
   *
   * @return the itemstack to use for rendering
   */
  public ItemStack getToolForRendering() {
    if (this.toolForRendering == null || this.toolForRendering.isEmpty()) {
      if (this.itemStack.getItem() instanceof ToolCore) {
        this.toolForRendering = ((ToolCore) this.itemStack.getItem()).buildToolForRendering();
      }
      else {
        this.toolForRendering = this.itemStack;
      }
    }

    return this.toolForRendering;
  }
}
