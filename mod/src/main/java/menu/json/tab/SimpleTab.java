package menu.json.tab;

import com.github.argon.sos.mod.sdk.util.Clipboard;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.Spacer;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.writer.JsonWriters;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import menu.json.JsonUi;
import menu.json.factory.JsonUiElementSingle;
import menu.json.factory.JsonUiTemplate;
import menu.json.icon.JsonUiTemplateIcon;
import menu.ui.MenuInput;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.text.StringInputSprite;

import java.nio.file.Path;
import java.util.function.Consumer;

public class SimpleTab extends AbstractTab {

    private JsonUiTemplate jsonUiTemplate;

    private Table<JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>> table;

    public SimpleTab(Path path, int availableHeight, JsonUiTemplate jsonUiTemplate) {
        super(path, availableHeight);
        this.jsonUiTemplate = jsonUiTemplate;

        GuiSection bar = new GuiSection();
        StringInputSprite search = new StringInputSprite(16, UI.FONT().M).placeHolder("Search");
        MenuInput searchField = new MenuInput(search);
        Button resetButton = new Button(SPRITES.icons().m.arrow_left);
        resetButton.hoverInfoSet("Reset " + getTitle() + " to initially loaded from file.");
        resetButton.clickActionSet(jsonUiTemplate::reset);

        Button toggleOrphansButton = new Button(SPRITES.icons().m.clear_structure);
        toggleOrphansButton.hoverInfoSet("Hide elements without config present in files.");
        toggleOrphansButton.clickActionSet(toggleOrphansButton::toggle);
        toggleOrphansButton.toggleAction(enabled -> {
            hideOrphans(jsonUiTemplate, enabled);
        });

        Button exportButton = new Button(SPRITES.icons().m.for_muster);
        exportButton.hoverInfoSet("Export settings to clipboard.");
        exportButton.clickActionSet(() -> {
            Json json = new Json(jsonUiTemplate.getConfig(), JsonWriters.jsonEPretty());
            Clipboard.write(json.write());
        });

        this.table = JsonUi.builder()
            .template(jsonUiTemplate)
            .build()
            .table(this.availableHeight - 10, search);

        // hide elements not present in game files per default
        hideOrphans(jsonUiTemplate, false);

        bar.addRightC(0, searchField);
        bar.addRightC(100, resetButton);
        bar.addRightC(10, toggleOrphansButton);
        bar.addRightC(10, exportButton);
        bar.addRightC(10, new JsonUiTemplateIcon(jsonUiTemplate));
        addDownC(5, bar);
        addDownC(10, table);
    }

    private void hideOrphans(JsonUiTemplate jsonUiTemplate, Boolean enabled) {
        jsonUiTemplate.getAll().forEach(jsonUiElement -> {
            if (jsonUiElement.isOrphan()) {
                table.display(jsonUiElement, !enabled);
            } else if (jsonUiElement.getElement() != null && jsonUiElement.getElement() instanceof Spacer) {
                table.display(jsonUiElement, !enabled);
            }
        });
    }

    public SimpleTab(Path path, int availableHeight, Consumer<JsonUiTemplate> factoryConsumer) {
        super(path, availableHeight);
        Table<JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>> table = JsonUi.builder()
            .template(path, factoryConsumer)
            .build().table(this.availableHeight - 10);
        addDownC(10, table);
    }

    @Override
    public String getTitle() {
        return path.getFileName().toString();
    }

    @Override
    public boolean isDirty() {
        return jsonUiTemplate.isDirty();
    }
}
