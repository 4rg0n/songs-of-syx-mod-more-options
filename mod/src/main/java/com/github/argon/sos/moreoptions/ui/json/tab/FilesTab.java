package com.github.argon.sos.moreoptions.ui.json.tab;

import com.github.argon.sos.mod.sdk.game.asset.GameFolder;
import com.github.argon.sos.mod.sdk.game.asset.GameResources;
import com.github.argon.sos.mod.sdk.game.ui.*;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import init.paths.PATH;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import menu.ui.MenuInput;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class FilesTab<Tab extends AbstractTab> extends AbstractTab {

    private final Tabulator<Path, Tab, Void> tabulator;
    private final Map<Path, Tab> tabMap;

    public FilesTab(
        PATH root, int
        availableHeight,
        List<Tab> tabs
    ) {
        super(root, availableHeight, false);

        tabMap = tabs.stream().collect(Collectors.toMap(
            AbstractTab::getPath,
            tab -> tab,
            (tab1, tab2) -> { throw new IllegalStateException(
                String.format("Duplicate path %s in tabs %s and %s", tab1.getPath(), tab1.getTitle(), tab2.getTitle())); }
        ));

        GuiSection searchBar = new GuiSection();
        StringInputSprite search = new StringInputSprite(24, UI.FONT().S).placeHolder("Search");
        MenuInput searchField = new MenuInput(search);

        Button collapseButton = new Button(SPRITES.icons().m.menu2);
        collapseButton.hoverInfoSet("Collapse / Expand all");

        searchBar.addRightC(0, searchField);
        searchBar.addRightC(20, collapseButton);
        availableHeight -= searchBar.body().height();

        GuiSection menuWithSearch = new GuiSection();
        GameFolder rootFolder = GameResources.get(root);
        boolean collapsed = true;
        FilesMenu filesMenu = FilesMenu.builder()
            .root(rootFolder)
            .search(search)
            .collapsed(collapsed)
            .paths(tabs.stream()
                .map(AbstractTab::getPath)
                .collect(Collectors.toList()))
            .availableHeight(availableHeight)
            .availableWidth(400)
            .iconsProvider(fileNode -> {
                if (fileNode.isFolder()) {
                    return null;
                }

                Tab tab = tabMap.get(fileNode.getPath().get());

                Section section = UiUtil.toSection(SPRITES.icons().m.cancel);
                section.hoverInfoSet("Settings changed.");
                // only show when values has changed
                section.renderAction(ds -> {
                    if (tab != null) section.visableSet(tab.isDirty());
                });

                return section;
            })
            .build();

        menuWithSearch.addDownC(0, searchBar);
        menuWithSearch.addDownC(10, filesMenu);
        collapseButton.selectedSet(!collapsed);
        collapseButton.clickActionSet(() -> {
            collapseButton.toggle();

            for (FilesMenu.FileNode pathTreeNode : filesMenu.getFiles().values()) {
                filesMenu.collapse(pathTreeNode, !collapseButton.selectedIs());
            }
        });

        tabulator = Tabulator.<Path, Tab, Void>builder()
            .tabs(new TreeMap<>(tabMap)) // sort by title
            .direction(DIR.E)
            .margin(10)
            .center(true)
            .build();

        filesMenu.fileClickAction(fileNode -> tabulator.tab(fileNode.getPath().get()));

        addRightC(0, menuWithSearch);
        addRightC(20, new VerticalLine(1, availableHeight, 1));
        addRightC(20, tabulator);
    }

    @Override
    public boolean isDirty() {
        return tabMap.values().stream()
            .anyMatch(AbstractTab::isDirty);
    }
}
