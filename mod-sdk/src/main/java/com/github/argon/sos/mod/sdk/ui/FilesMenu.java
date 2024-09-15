package com.github.argon.sos.mod.sdk.ui;

import com.github.argon.sos.mod.sdk.data.TreeNode;
import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.asset.GameFolder;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.text.StringInputSprite;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class FilesMenu extends Section {
    @Getter
    private final GameFolder root;

    @Getter
    private final Map<Path, GameFolder> folders = new TreeMap<>();
    @Getter
    private final Map<Path, FileNode> files = new HashMap<>();

    private final Map<Path, ColumnRow<FileNode>> rows = new HashMap<>();

    @Getter
    private final TreeNode<Path> fileTree;

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<FileNode> fileClickAction = o -> {};

    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Function<FileNode, RENDEROBJ> iconsProvider;

    /**
     * A menu with a file tree showing folders and files.
     *
     * @param root base path of the folder to display in the menu
     * @param paths which paths shall be displayed in the menu
     * @param availableHeight max height for the menu
     * @param availableWidth max width for the menu
     * @param collapsed whether all folders shall be rendered collapsed as default
     * @param search an optional search bar to search for file and folder names
     * @param selectedPath an optional path, which shall be open as default
     * @param iconsProvider a way to mark certain entries with icons
     */
    @Builder
    public FilesMenu(
        GameFolder root,
        List<Path> paths,
        int availableHeight,
        int availableWidth,
        boolean collapsed,
        @Nullable StringInputSprite search,
        @Nullable Path selectedPath,
        @Nullable Function<FileNode, RENDEROBJ> iconsProvider
    ) {
        Path rootPath = root.path().get();
        this.fileTree = new TreeNode<>(rootPath);
        this.root = root;
        this.iconsProvider = iconsProvider;

        for (Path path : paths) {
            folders.put(path, root.folder(path));
            Path relativePath = rootPath.relativize(path);

            TreeNode<Path> currentPathNode = this.fileTree;
            String currentAbsolutePath = "";
            for (Path segment : relativePath) {
                if (currentAbsolutePath.isEmpty()) {
                    currentAbsolutePath = segment.toString();
                } else {
                    currentAbsolutePath = currentAbsolutePath + "/" + segment.toString();
                }

                Path absolutePath = rootPath.resolve(currentAbsolutePath);
                if (currentPathNode.has(absolutePath)) {
                    currentPathNode = currentPathNode.get(absolutePath);
                    continue;
                }

                currentPathNode = currentPathNode.node(absolutePath);
                add(currentPathNode);
            }
        }

        List<ColumnRow<FileNode>> rows = new ArrayList<>();
        for (TreeNode<Path> node : fileTree) {
            Path path = node.get();
            FileNode fileNode = files.get(path);

            if (fileNode == null) {
                continue;
            }

            if (selectedPath != null && selectedPath.equals(path)) {
                selectNode(fileNode, true);
            }

            int depth = node.depth();
            int indents = depth - 1;
            ColumnRow<FileNode> buttonRow = buildRow(indents, fileNode, availableWidth);
            buttonRow.init();
            rows.add(buttonRow);

            if (depth > 1) {
                buttonRow.visableSet(!collapsed);
            }

            // do not collapse root
            if (!node.equals(fileTree)) {
                fileNode.setCollapsed(collapsed);
            }

            this.rows.put(path, buttonRow);
        }

        ScrollRows scrollRows = ScrollRows.builder()
            .height(availableHeight)
            .slide(true)
            .search(search)
            .rows(rows)
            .width(availableWidth)
            .build();

        addDownC(0, scrollRows.view());
    }

    private void collapse(FileNode fileNode) {
        collapse(fileNode, !fileNode.isCollapsed());
    }

    public void collapse(FileNode fileNode, boolean collapse) {
        TreeNode<Path> toCollapse = fileNode.getPath();
        fileNode.setCollapsed(collapse);

        if (collapse) {
            for (TreeNode<Path> pathTreeNode : toCollapse) {
                ColumnRow<FileNode> row = rows.get(pathTreeNode.get());

                if (toCollapse.equals(pathTreeNode)) {
                    continue;
                }

                if (row == null) {
                    continue;
                }
                row.getValue().setCollapsed(false);
                row.visableSet(false);
            }
        } else {
            for (TreeNode<Path> pathTreeNode : toCollapse.nodes()) {
                ColumnRow<FileNode> row = rows.get(pathTreeNode.get());
                if (row == null) {
                    continue;
                }
                row.getValue().setCollapsed(true);
                row.visableSet(true);
            }
        }
    }

    private void add(TreeNode<Path> pathNode) {
        files.put(pathNode.get(), buildFileNode(pathNode));
    }

    private void selectNode(FileNode node, boolean select) {
        if (node.isFolder()) {
            return;
        }
        Button button = node.getButton();
        button.selectedSet(select);
    }

    @NonNull
    private FileNode buildFileNode(TreeNode<Path> pathNode) {
        Path path = pathNode.get();
        String name = path.getFileName().toString();
        COLOR color = COLOR.WHITE10;
        boolean isFolder = !name.contains(".");

        FileNode.FileNodeBuilder fileNodeBuilder = FileNode.builder();

        if (isFolder) {
            color =  COLOR.WHITE50;
        }

        Button button = new Button(UI.FONT().S.getText(name));
        button.bg(color);
        button.hoverInfoSet(path.toString());

        FileNode fileNode = fileNodeBuilder
            .name(name)
            .path(pathNode)
            .folder(isFolder)
            .button(button)
            .build();

        if (isFolder) {
            button.clickActionSet(() -> {
                collapse(fileNode);
            });
        } else {
            button.clickActionSet(() -> {
                fileClickAction.accept(fileNode);
                button.selectedSet(true);
                files.values().forEach(node -> {
                    Button otherButton = node.getButton();
                    if (button.equals(otherButton)) {
                        return;
                    }
                    selectNode(node, false);
                });
            });
        }

        return fileNode;
    }

    @NotNull
    private ColumnRow<FileNode> buildRow(int indents, FileNode fileNode, int availableWidth) {
        GuiSection row = new GuiSection();
        COLOR bgColor = COLOR.WHITE15;
        Button button = fileNode.getButton();
        int buttonHeight = button.body.height();

        for (int i = 0; i < indents; i++) {
            row.addRightC(0, new ColorBox(buttonHeight, bgColor));
        }

        // set folder or file icon
        ColorBox colorBox;
        if (!fileNode.isFolder()) {
            colorBox = ColorBox.of(UiUtil.toRender(SPRITES.icons().m.minus), buttonHeight, bgColor);
        } else {
            colorBox = ColorBox.of(UiUtil.toRender(SPRITES.icons().m.menu2), buttonHeight, bgColor);
        }

        row.addRightC(0, colorBox);
        RENDEROBJ iconsWithBackground = null;
        if (iconsProvider != null) {
            RENDEROBJ icons = iconsProvider.apply(fileNode);

            if (icons != null) {
                iconsWithBackground = ColorBox.of(icons, buttonHeight, COLOR.WHITE10);
            }
        }

        if (iconsWithBackground != null) {
            // set expand button
            button.body().setWidth(availableWidth - row.body().width() - iconsWithBackground.body().width());
            row.addRightC(0, button);
            row.addRightC(0, iconsWithBackground);
        } else {
            // set expand button
            button.body().setWidth(availableWidth - row.body().width());
            row.addRightC(0, button);
        }

        ColumnRow<FileNode> columnRow = ColumnRow.<FileNode>builder()
            .key(fileNode.getName())
            .valueSupplier(() -> fileNode)
            .searchTerm(fileNode.getPath().toString())
            .column(row)
            .build();

        return columnRow;
    }
    
    @Getter
    @Builder
    public static class FileNode {
        private final String name;
        private final TreeNode<Path> path;
        private final Button button;
        private final boolean folder;
        private boolean collapsed;

        void setCollapsed(boolean collapsed) {
            this.collapsed = collapsed;
        }
    }
}
