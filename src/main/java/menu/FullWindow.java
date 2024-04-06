package menu;

import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.BiAction;
import com.github.argon.sos.moreoptions.game.ui.*;
import init.C;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;

public class FullWindow<Section extends GuiSection> extends ColorBox implements
    Refreshable<FullWindow<Section>>,
    Renderable<FullWindow<Section>>,
    SC
{
    public static final int TOP_HEIGHT = 40;

    public static final int AVAILABLE_HEIGHT = C.HEIGHT() - TOP_HEIGHT - 10;

    @Getter
    private final GuiSection top = new ColorBox(COLOR.WHITE15);

    @Getter
    private final Section section;

    @Setter
    @Accessors(fluent = true, chain = false)
    protected Action<FullWindow<Section>> showAction = o -> {};
    @Setter
    @Accessors(fluent = true, chain = false)
    protected Action<FullWindow<Section>> hideAction = o -> {};
    @Setter
    @Accessors(fluent = true, chain = false)
    protected Action<FullWindow<Section>> refreshAction = o -> {};
    @Setter
    @Accessors(fluent = true, chain = false)
    protected BiAction<FullWindow<Section>, Float> renderAction = (o1, o2) -> {};

    @Builder
    public FullWindow(String name, Section section, @Nullable Toggle<String> topMenu) {
        super(COLOR.WHITE20);
        body().setDim(C.DIM());
        this.section = section;
        top.body().setHeight(TOP_HEIGHT);
        top.body().setWidth(C.WIDTH());

        GButt.ButtPanel exit = new GButt.ButtPanel(SPRITES.icons().m.exit) {
            @Override
            protected void clickA() {
                Menu menu = Ui.getInstance().getMenu();
                menu.switchScreen(menu.sandbox);
            }
        };

        Label title = Label.builder()
            .name(name)
            .font(UI.FONT().H2)
            .style(Label.Style.LABEL)
            .build();

        exit.body.moveX2(C.WIDTH() - 8);
        exit.body.centerY(top);
        title.body().moveX1(8);
        title.body().centerY(top);

        if (topMenu != null) {
            topMenu.body().centerX(title.body().x1(), exit.body.x1());
            topMenu.body().centerY(top);
            top.add(topMenu);
        }

        add(top);
        add(section);

        top.add(title);
        top.add(exit);
        top.body().moveY1(0);
        top.body().centerX(C.DIM());
        section.body().moveY1(TOP_HEIGHT + 10);
        section.body().centerX(C.DIM());
    }

    @Override
    public boolean back(Menu menu) {
        menu.switchScreen(menu.sandbox);
        return true;
    }
}
