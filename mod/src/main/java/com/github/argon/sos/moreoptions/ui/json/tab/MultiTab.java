package com.github.argon.sos.moreoptions.ui.json.tab;

import com.github.argon.sos.mod.sdk.game.ui.Button;
import com.github.argon.sos.mod.sdk.game.ui.ButtonMenu;
import com.github.argon.sos.mod.sdk.game.ui.Switcher;
import com.github.argon.sos.mod.sdk.game.ui.Tabulator;
import init.paths.PATH;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiTab<Tab extends AbstractTab> extends AbstractTab {

    private final Tabulator<String, Tab, Void> tabulator;
    private Map<String, Tab> tabMap;

    public MultiTab(PATH path, int availableHeight, Function<Path, Tab> tabSupplier) {
        this(path, availableHeight, tabSupplier, null);
    }

    public MultiTab(PATH path,  int availableHeight, List<Tab> tabs) {
        this(path, availableHeight, null, tabs);
    }

    private MultiTab(PATH path, int availableHeight, @Nullable Function<Path, Tab> tabSupplier, @Nullable List<Tab> tabs) {
        super(path, availableHeight, false);

        if (tabs == null) {
            Objects.requireNonNull(path);
            Objects.requireNonNull(tabSupplier);

            List<Path> paths = new ArrayList<>();
            for (String file : path.getFiles()) {
                paths.add(path.get(file));
            }
            tabs = paths.stream().map(tabSupplier).collect(Collectors.toList());
        }

        tabMap = tabs.stream().collect(Collectors.toMap(
            AbstractTab::getTitle,
            tab -> tab
        ));

        Map<String, Button> buttons = tabs.stream().collect(Collectors.toMap(
            AbstractTab::getTitle,
            tab -> {
                Button button = new Button(tab.getTitle(), tab.getPath().toString());
                button.searchTerm(tab.getTitle());
                return button;
            }
        ));

        tabulator = Tabulator.<String, Tab, Void>builder()
            .tabs(new TreeMap<>(tabMap)) // sort by title
            .tabMenu(Switcher.<String>builder()
                .menu(ButtonMenu.<String>builder()
                    .displaySearch(true)
                    .maxHeight(availableHeight)
                    .buttons(new TreeMap<>(buttons)) // sort by title
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

    public List<Tab> tabs() {
        return new ArrayList<>(tabMap.values());
    }

    public String getTitle() {
        String parent = path.getParent().getFileName().toString();
        return parent + "/" + path.getFileName().toString();
    }

    @Override
    public boolean isDirty() {
        return tabMap.values().stream()
            .anyMatch(AbstractTab::isDirty);
    }
}
