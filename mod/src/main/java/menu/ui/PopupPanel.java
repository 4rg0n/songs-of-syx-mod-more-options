package menu.ui;

import com.github.argon.sos.mod.sdk.game.action.Hideable;
import com.github.argon.sos.mod.sdk.game.action.Showable;
import init.C;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.misc.ACTION;
import util.gui.panel.GPanel;

public class PopupPanel extends GuiSection implements Hideable, Showable {
    private final GuiSection section = new GuiSection();

    private final GPanel panel = new GPanel();

    @Getter
    @Nullable
    private CLICKABLE trigger;

    @Getter
    @Nullable
    private RENDEROBJ content;

    @Setter
    @Accessors(fluent = true, chain = false)
    private ACTION closeAction = this::hide;

    public PopupPanel() {
        panel.setButt();
        panel.clickActionSet(closeAction);
        add(panel);
        add(section);
        visableSet(false);
    }

    @Override
    public void show() {
        show(content, trigger);
    }

    @Override
    public void hide() {
        visableSet(false);
        if (trigger != null) {
            trigger.selectedSet(false);
            trigger = null;
        }
        content = null;
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        super.render(r, ds);
        if (trigger != null) {
            trigger.selectedSet(true);
        }
    }

    public void show(@Nullable RENDEROBJ content, @Nullable  CLICKABLE trigger) {
        if (content == null || trigger == null) {
            return;
        }

        section.clear();
        section.add(content);
        this.content = content;
        this.trigger = trigger;
        int M = C.SG*32;
        int x = trigger.body().cX();
        int y = trigger.body().cY();

        section.body().moveCX(x);
        if (y > C.HEIGHT()/2){
            section.body().moveY2(y-M);
        }else {
            section.body().moveY1(y+M);
        }

        if (section.body().x2()+M >= C.WIDTH()) {
            section.body().moveX2(C.WIDTH()-M);
        }

        if (section.body().x1() - M < 0) {
            section.body().moveX1(x+M);
        }

        if (section.body().y2()+M >= C.HEIGHT()) {
            section.body().moveY2(C.HEIGHT()-M);
        }

        if (section.body().y1() - M < 0) {
            section.body().moveY1(M);
        }

        panel.inner().set(section);
        visableSet(true);
    }
}
