package menu.json.tab;

import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.ButtonMenu;
import com.github.argon.sos.moreoptions.game.ui.Tabulator;
import com.github.argon.sos.moreoptions.game.ui.Toggle;
import init.paths.PATH;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiTab<Tab extends AbstractTab> extends AbstractTab {

    private final Tabulator<String, Tab, Void> tabulator;

    public MultiTab(PATH path, Function<Path, Tab> tabSupplier) {
        this(path, tabSupplier, null);
    }

    public MultiTab(PATH path, List<Tab> tabs) {
        this(path, null, tabs);
    }

    private MultiTab(PATH path, @Nullable Function<Path, Tab> tabSupplier, @Nullable List<Tab> tabs) {
        super(path, 0, false);

        if (tabs == null) {
            Objects.requireNonNull(path);
            Objects.requireNonNull(tabSupplier);

            List<Path> paths = new ArrayList<>();
            for (String file : path.getFiles()) {
                paths.add(path.get(file));
            }
            tabs = paths.stream().map(tabSupplier).collect(Collectors.toList());
        }

        Map<String, Tab> tabMap = tabs.stream().collect(Collectors.toMap(
            AbstractTab::getTitle,
            tab -> tab
        ));

        Map<String, Button> buttons = tabs.stream().collect(Collectors.toMap(
            AbstractTab::getTitle,
            tab -> new Button(tab.getTitle(), tab.getPath().toString())
        ));

        tabulator = Tabulator.<String, Tab, Void>builder()
            .tabs(tabMap)
            .tabMenu(Toggle.<String>builder()
                .menu(ButtonMenu.<String>builder()
                    .buttons(buttons)
                    .sameWidth(true)
                    .buttonColor(COLOR.WHITE25)
                    .build())
                .highlight(true)
                .build())
            .direction(DIR.E)
            .margin(10)
            .center(true)
            .build();

        addDownC(0, tabulator);
    }

    public String getTitle() {
        String parent = path.getParent().getFileName().toString();
        return parent + "/" + path.getFileName().toString();
    }
}
