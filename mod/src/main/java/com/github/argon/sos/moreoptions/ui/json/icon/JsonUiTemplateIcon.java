package com.github.argon.sos.moreoptions.ui.json.icon;

import com.github.argon.sos.mod.sdk.game.ui.Section;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.moreoptions.ui.json.factory.JsonUiTemplate;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import lombok.Getter;
import lombok.Setter;
import com.github.argon.sos.moreoptions.ui.json.factory.JsonUiElementSingle;
import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GUI_BOX;
import util.gui.misc.GBox;
import util.info.GFORMAT;

import java.nio.file.Path;


@Getter
public class JsonUiTemplateIcon extends Section {

    private final JsonUiTemplate jsonUiTemplate;

    @Setter
    private boolean visible = true;

    public JsonUiTemplateIcon(JsonUiTemplate jsonUiTemplate) {
        this.jsonUiTemplate = jsonUiTemplate;
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
        box.title("Ui Template Info");

        String name = jsonUiTemplate.getName();
        if (name != null) {
            text.textLL("Name");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), name).warnify());
            text.NL(5);
        }
        String filename = jsonUiTemplate.getFileName();
        if (filename != null) {
            text.textLL("Description");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), filename).warnify());
            text.NL(5);
        }
        Path path = jsonUiTemplate.getPath();
        if (path != null) {
            text.textLL("File Path");
            text.tab(6);
            text.add(GFORMAT.text(text.text(), path.toString()).warnify());
        }
        text.NL(10);

        long dirtyCount = jsonUiTemplate.getAll().stream().filter(JsonUiElementSingle::isDirty).count();
        text.add(UI.icons().s.money);
        text.textLL("Dirty");
        text.tab(6);
        text.add(GFORMAT.i(text.text(), dirtyCount).lablifySub());
        text.NL(5);

        int elementCount = jsonUiTemplate.getAll().size();
        text.add(UI.icons().s.money);
        text.textLL("Ui Elements");
        text.tab(6);
        text.add(GFORMAT.i(text.text(), elementCount).lablifySub());
        text.NL(5);

        int orphanCount = jsonUiTemplate.getOrphans().size();
        text.add(UI.icons().s.money);
        text.textLL("Orphans");
        text.tab(6);
        text.add(GFORMAT.i(text.text(), orphanCount).lablifySub());

        super.hoverInfoGet(box);
    }
}
