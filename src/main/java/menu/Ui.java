package menu;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import menu.ui.Popups;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.Coo;

import java.util.Objects;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Ui {
    private final static Logger log = Loggers.getLogger(Ui.class);

    @Getter(lazy = true)
    private final static Ui instance = new Ui();

    @Getter(lazy = true)
    @Accessors(fluent = true, chain = false)
    private final Popups popups = new Popups();

    public final static Supplier<Coo> MOUSE_COO_SUPPLIER = () -> getInstance().getMouseCoo();

    @Nullable
    private Menu menu;

    public Menu getMenu() {
        Objects.requireNonNull(menu);
        return menu;
    }

    public Coo getMouseCoo() {
        return getMenu().getMCoo();
    }

    public void init(Menu menu) {
        log.debug("init");
        this.menu = menu;
    }

    public static FullWindow<MoreOptionsEditor> moreOptionsFullsWindow() {
//        StringInputSprite searchInput = new StringInputSprite(8, UI.FONT().S).placeHolder("Search");
        MoreOptionsEditor moreOptionsEditor = new MoreOptionsEditor(null);
        return new FullWindow<>("More Options",
            moreOptionsEditor,
            moreOptionsEditor.getTabulator().getMenu(),
            null
        );
    }
}
