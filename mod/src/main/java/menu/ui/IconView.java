package menu.ui;

import com.github.argon.sos.mod.sdk.game.action.Valuable;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import init.sprite.UI.Icon;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.file.Json;
import snake2d.util.gui.GuiSection;

public class IconView extends GuiSection implements Valuable<Icon> {
    @Getter
    @Nullable
    private final Json json;
    @Getter
    @Nullable
    private final JsonObject jsonObject;
    private Icon icon;
    @Getter
    @Nullable
    private final String iconPath;

    public IconView(Icon icon, @Nullable String iconPath, @Nullable Json json, @Nullable JsonObject jsonObject) {
        setValue(icon);
        this.iconPath = iconPath;
        this.json = json;
        this.jsonObject = jsonObject;
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        super.render(r, ds);
        icon.render(r, body());
    }

    @Override
    public Icon getValue() {
        return icon;
    }

    @Override
    public void setValue(Icon icon) {
        this.icon = icon;
    }
}
