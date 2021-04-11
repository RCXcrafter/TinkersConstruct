package slimeknights.tconstruct.library.book;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.TextElement;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public abstract class TinkerPage extends PageContent {

  public static final transient int TITLE_HEIGHT = 28;

  public void addTitle(ArrayList<BookElement> list, String titleText, boolean dropShadow) {
    this.addTitle(list, titleText, dropShadow, 0, 0);
  }

  public void addTitle(ArrayList<BookElement> list, String titleText, boolean dropShadow, int color) {
    this.addTitle(list, titleText, dropShadow, color, 0);
  }

  public void addTitle(ArrayList<BookElement> list, String titleText, boolean dropShadow, int color, int y) {
    TextData title = new TextData(titleText);

    title.scale = 1.2f;
    title.underlined = true;
    title.dropshadow = dropShadow;

    if (color != 0) {
      title.useOldColor = false;
      title.rgbColor = color;
    }

    int w = (int) Math.ceil(this.parent.parent.parent.fontRenderer.getStringWidth(titleText) * title.scale);
    int x = (BookScreen.PAGE_WIDTH - w) / 2;

    list.add(new TextElement(x, y, w, 24, title));
  }
}
