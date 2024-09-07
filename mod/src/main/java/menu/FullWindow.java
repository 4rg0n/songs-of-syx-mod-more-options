package menu;

import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.action.BiAction;
import com.github.argon.sos.mod.sdk.game.action.Refreshable;
import com.github.argon.sos.mod.sdk.game.action.Renderable;
import com.github.argon.sos.mod.sdk.game.ui.ColorBox;
import com.github.argon.sos.mod.sdk.game.ui.Label;
import com.github.argon.sos.mod.sdk.game.ui.Switcher;
import init.C;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import menu.ui.MenuInput;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GButt;

public class FullWindow<Section extends GuiSection> extends ColorBox implements
    Refreshable,
    Renderable,
    SC
{
    public static final int TOP_HEIGHT = 42;

    public static final int AVAILABLE_HEIGHT = C.HEIGHT() - (TOP_HEIGHT * 2) - 10;

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
    public FullWindow(@Nullable String name, Section section, @Nullable Switcher<String> topMenu, @Nullable StringInputSprite searchInput) {
        super(COLOR.WHITE20);
        body().setDim(C.DIM());
        this.section = section;
        top.body().setHeight(TOP_HEIGHT);
        top.body().setWidth(C.WIDTH());

        GButt.ButtPanel exit = new GButt.ButtPanel(SPRITES.icons().m.exit) {
            @Override
            protected void clickA() {
                Menu menu = Ui.getInstance().getMenu();
                menu.switchScreen(menu.main);
            }
        };

        int width = 0;
        if (name != null) {
            Label title = Label.builder()
                .name(name)
                .font(UI.FONT().H2)
                .style(Label.Style.LABEL)
                .build();

            title.body().moveX1(8);
            title.body().centerY(top);
            top.add(title);
            width = title.body().width() + 8;
        }

        if (searchInput != null) {
            MenuInput searchField = new MenuInput(searchInput);
            searchField.body().moveX1(width + 8);
            searchField.body().centerY(top);
            top.add(searchField);
            width = searchField.body().width();
        }

        exit.body.moveX2(C.WIDTH() - 8);
        exit.body.centerY(top);

        if (topMenu != null) {
            topMenu.body().centerX(width, exit.body.x1());
            topMenu.body().centerY(top);
            top.add(topMenu);
        }

        add(top);
        add(section);

        top.add(exit);
        top.body().moveY1(0);
        top.body().centerX(C.DIM());
        section.body().moveY1(TOP_HEIGHT + 10);
        section.body().centerX(C.DIM());
    }

    @Override
    public boolean back(Menu menu) {
        menu.switchScreen(menu.main);
        return true;
    }
}
