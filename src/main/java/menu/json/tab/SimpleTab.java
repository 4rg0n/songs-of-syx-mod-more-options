package menu.json.tab;

import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.Table;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import menu.json.JsonUiSection;
import menu.json.JsonUiTemplate;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GInput;

import java.nio.file.Path;
import java.util.function.Consumer;

public class SimpleTab extends AbstractTab {

    public SimpleTab(Path path, int availableHeight, JsonUiTemplate jsonUiTemplate) {
        super(path, availableHeight);

        GuiSection bar = new GuiSection();
        StringInputSprite search = new StringInputSprite(16, UI.FONT().M).placeHolder("Search");
        GInput searchField = new GInput(search);
        Button resetButton = new Button(SPRITES.icons().m.arrow_left);
        resetButton.clickActionSet(jsonUiTemplate::reset);

        Table<Void> table = JsonUiSection.builder()
            .template(jsonUiTemplate)
            .build().table(this.availableHeight - 10, search);

        bar.addRightC(0, searchField);
        bar.addRightC(50, resetButton);
        addDownC(5, bar);
        addDownC(10, table);
    }

    public SimpleTab(Path path, int availableHeight, Consumer<JsonUiTemplate> factoryConsumer) {
        super(path, availableHeight);
        Table<Void> table = JsonUiSection.builder()
            .template(path, factoryConsumer)
            .build().table(this.availableHeight - 10);
        addDownC(10, table);
    }
}
