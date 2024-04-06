package menu.json.tab;

import init.paths.PATH;
import init.sprite.UI.UI;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GHeader;

import java.nio.file.Path;

public abstract class AbstractTab extends GuiSection {

    @Getter
    protected final Path path;
    protected final int availableHeight;

    public AbstractTab(PATH path, int availableHeight) {
        this(path.get(), availableHeight, true);
    }

    public AbstractTab(Path path, int availableHeight) {
        this(path, availableHeight, true);
    }

    public AbstractTab(PATH path, int availableHeight, boolean showPath) {
        this(path.get(), availableHeight, showPath);
    }

    public AbstractTab(Path path, int availableHeight, boolean showPath) {
        this.path = path;

        if (showPath) {
            GHeader header = new GHeader(path.toString(), UI.FONT().H2);
            addDownC(0, header);
            this.availableHeight = availableHeight - header.body().height();
        } else {
            this.availableHeight = availableHeight;
        }
    }

    public String getTitle() {
        String parent = path.getParent().getFileName().toString();
        return parent + "/" + path.getFileName().toString();
    }
}
