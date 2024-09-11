package menu;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
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
public class MenuUi {
    private final static Logger log = Loggers.getLogger(MenuUi.class);

    @Getter(lazy = true)
    private final static MenuUi instance = new MenuUi();

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
        MoreOptionsEditor moreOptionsEditor = new MoreOptionsEditor(null);
        return new FullWindow<>("More Options (experimental)",
            moreOptionsEditor,
            moreOptionsEditor.getTabulator().getMenu(),
            null
        );
    }
}
