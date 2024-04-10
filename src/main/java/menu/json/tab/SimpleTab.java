package menu.json.tab;

import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.writer.JsonWriters;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.Clipboard;
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

    private final static Logger log = Loggers.getLogger(SimpleTab.class);

    private Table<Void> table;

    public SimpleTab(Path path, int availableHeight, JsonUiTemplate jsonUiTemplate) {
        super(path, availableHeight);

        GuiSection bar = new GuiSection();
        StringInputSprite search = new StringInputSprite(16, UI.FONT().M).placeHolder("Search");
        GInput searchField = new GInput(search);
        Button resetButton = new Button(SPRITES.icons().m.arrow_left);
        resetButton.hoverInfoSet("Reset " + getTitle());
        resetButton.clickActionSet(jsonUiTemplate::reset);

        Button toggleOrphansButton = new Button(SPRITES.icons().m.clear_structure);
        toggleOrphansButton.hoverInfoSet("Hide elements without config present in files.");
        toggleOrphansButton.selectedSet(true);
        toggleOrphansButton.clickActionSet(toggleOrphansButton::toggle);
        toggleOrphansButton.toggleAction(enabled -> {
            displayOrphans(jsonUiTemplate, !enabled);
        });

        Button exportButton = new Button(SPRITES.icons().m.for_muster);
        exportButton.hoverInfoSet("Export settings to clipboard.");
        exportButton.clickActionSet(() -> {
            Json json = new Json(jsonUiTemplate.getConfig(), JsonWriters.jsonEPretty());
            Clipboard.write(json.write());
        });

        this.table = JsonUiSection.builder()
            .template(jsonUiTemplate)
            .build().table(this.availableHeight - 10, search);

        // hide elements not present in game files per default
        displayOrphans(jsonUiTemplate, false);

        bar.addRightC(0, searchField);
        bar.addRightC(100, resetButton);
        bar.addRightC(10, toggleOrphansButton);
        bar.addRightC(10, exportButton);
        addDownC(5, bar);
        addDownC(10, table);
    }

    private void displayOrphans(JsonUiTemplate jsonUiTemplate, Boolean enabled) {
        jsonUiTemplate.getOrphans().forEach(jsonUiElement -> {
            String jsonPath = jsonUiElement.getJsonPath();
            if (jsonPath != null) {
                table.display(jsonPath, enabled);
            }
        });
    }

    public SimpleTab(Path path, int availableHeight, Consumer<JsonUiTemplate> factoryConsumer) {
        super(path, availableHeight);
        Table<Void> table = JsonUiSection.builder()
            .template(path, factoryConsumer)
            .build().table(this.availableHeight - 10);
        addDownC(10, table);
    }

    @Override
    public String getTitle() {
        return path.getFileName().toString();
    }
}
