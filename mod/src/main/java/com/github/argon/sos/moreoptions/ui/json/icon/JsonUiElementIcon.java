package com.github.argon.sos.moreoptions.ui.json.icon;

import com.github.argon.sos.mod.sdk.game.ui.Section;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import lombok.Getter;
import lombok.Setter;
import com.github.argon.sos.moreoptions.ui.json.factory.JsonUiElementSingle;
import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.misc.GBox;
import util.info.GFORMAT;

import java.nio.file.Path;


@Getter
public class JsonUiElementIcon<Value extends JsonElement, Element extends RENDEROBJ> extends Section {

    private final JsonUiElementSingle<Value, Element> jsonUiElement;

    @Setter
    private boolean visible = true;

    public JsonUiElementIcon(JsonUiElementSingle<Value, Element> jsonUiElement) {
        this.jsonUiElement = jsonUiElement;
        add(UiUtil.toRender(SPRITES.icons().m.cog_big));
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        if (!visible) return;
        super.render(r, ds);
    }

    @Override
    public void hoverInfoGet(GUI_BOX box) {
        GBox text = (GBox) box;
        box.title("Ui Element Info");

        String jsonPath = jsonUiElement.getJsonPath();
        if (jsonPath != null) {
            text.textLL("Json Path");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), jsonPath).warnify());
            text.NL(5);
        }
        Path path = jsonUiElement.getPath();
        if (path != null) {
            text.textLL("File Path");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), path.toString()).warnify());
            text.NL(5);
        }
        String description = jsonUiElement.description();
        if (description != null) {
            text.textLL("Description");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), description).warnify());
            text.NL(5);
        }

        Element element = jsonUiElement.getElement();
        if (element != null) {
            text.textLL("Ui Element Class");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), element.getClass().getSimpleName()).warnify());
            text.NL(5);
        }

        boolean isOrphan = jsonUiElement.isOrphan();
        text.textLL("Orphan");
        text.tab(6);
        text.add((isOrphan) ? UI.icons().s.allRight : UI.icons().s.cancel);
        text.NL(5);

        boolean isDirty = jsonUiElement.isDirty();
        text.textLL("Dirty");
        text.tab(6);
        text.add((isDirty) ? UI.icons().s.allRight : UI.icons().s.cancel);
        text.NL(10);

        Value value = jsonUiElement.getValue();
        if (value != null) {
            text.add(UI.icons().s.money);
            text.textLL("Value");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), value.toString()).lablifySub());
            text.NL(4);
            text.add(UI.icons().s.money);
            text.textLL("Value Class");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), value.getClass().getSimpleName()).lablifySub());
            text.NL(10);
        }

        jsonUiElement.getRawConfigValue().ifPresent(configValue -> {
            text.add(UI.icons().s.money);
            text.textLL("Raw Config Value");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), configValue.toString()).lablifySub());
            text.NL(4);
            text.add(UI.icons().s.money);
            text.textLL("Raw Config Value Class");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), configValue.getClass().getSimpleName()).lablifySub());
            text.NL(10);
        });

        jsonUiElement.getConfigValue().ifPresent(configValue -> {
            text.add(UI.icons().s.money);
            text.textLL("Config Value");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), configValue.toString()).lablifySub());
            text.NL(4);
            text.add(UI.icons().s.money);
            text.textLL("Config Value Class");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), configValue.getClass().getSimpleName()).lablifySub());
            text.NL(10);
        });


        Value defaultValue = jsonUiElement.getDefaultValue();
        if (defaultValue != null) {
            text.add(UI.icons().s.money);
            text.textLL("Default Value");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), defaultValue.toString()).lablifySub());
            text.NL(4);
            text.add(UI.icons().s.money);
            text.textLL("Default Value Class");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), defaultValue.getClass().getSimpleName()).lablifySub());
            text.NL(10);
        }

        Value initValue = jsonUiElement.getInitValue();
        if (initValue != null) {
            text.add(UI.icons().s.money);
            text.textLL("Init Value");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), initValue.toString()).lablifySub());
            text.NL(4);
            text.add(UI.icons().s.money);
            text.textLL("Init Value Class");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), initValue.getClass().getSimpleName()).lablifySub());
        }

        super.hoverInfoGet(box);
    }
}
