package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.action.*;
import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import game.GAME;
import init.C;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.gui.GuiSection;
import util.colors.GCOLOR;
import util.gui.misc.GBox;
import util.gui.misc.GButt;
import view.interrupter.Interrupter;
import view.main.VIEW;

public class FullWindow<Section extends GuiSection> extends Interrupter implements
    Showable,
    Hideable,
    Refreshable,
    Renderable
{
    private final static Logger log = Loggers.getLogger(FullWindow.class);

    public static final int TOP_HEIGHT = 40;

    @Getter
    private final FullView<Section> view;
    private final GuiSection top = new GuiSection();

    @Setter
    @Accessors(fluent = true, chain = false)
    protected VoidAction showAction = () -> {};
    @Setter
    @Accessors(fluent = true, chain = false)
    protected VoidAction hideAction = () -> {};
    @Setter
    @Accessors(fluent = true, chain = false)
    protected VoidAction refreshAction = () -> {};
    @Setter
    @Accessors(fluent = true, chain = false)
    protected Action<Float> renderAction = o -> {};

    /**
     * Displays any {@link GuiSection} as a full screen element.
     * Provides a header with the window name, an optional menu bar and a close button.
     *
     * @param name of the window
     * @param section to display
     * @param menu optionally rendered menu in the header
     */
    @Builder
    public FullWindow(String name, Section section, @Nullable Switcher<String> menu) {
        this.view = new FullView<>(name, section);
        top.body().setHeight(TOP_HEIGHT);
        top.body().setWidth(C.WIDTH());

        GButt.ButtPanel exit = new GButt.ButtPanel(SPRITES.icons().m.exit) {
            @Override
            protected void clickA() {
                hide();
            }
        };

        Label title = Label.builder()
            .name(name)
            .font(UI.FONT().H2)
            .style(Label.Style.LABEL)
            .build();

        exit.body.moveX2(C.WIDTH() - 8);
        exit.body.centerY(top);
        title.body().moveX1(8);
        title.body().centerY(top);

        if (menu != null) {
            menu.body().centerX(title.body().x1(), exit.body.x1());
            menu.body().centerY(top);
            top.add(menu);
        }

        top.add(title);
        top.add(exit);

        if (log.isLevel(Level.DEBUG)) log.debug("'%s' dimensions: %sx%s", name,
            C.WIDTH(),
            section.body().height() + FullView.TOP_HEIGHT
        );
    }

    public Section getSection() {
       return view.getSection();
    }

    public void show() {
        view.getSection().body().moveY1(FullView.TOP_HEIGHT);
        view.getSection().body().centerX(C.DIM());
        showAction.accept();

        if (view.getSection() instanceof Showable) {
            ((Showable) view.getSection()).show();
        }

        super.show(VIEW.inters().manager);
    }

    public void hide() {
        hideAction.accept();

        if (view.getSection() instanceof Hideable) {
            ((Hideable) view.getSection()).hide();
        }

        super.hide();
    }

    @Override
    protected boolean hover(COORDINATE mCoo, boolean mouseHasMoved) {
        view.getSection().hover(mCoo);
        top.hover(mCoo);
        return true;
    }

    @Override
    protected void mouseClick(MButt button) {
        if (button == MButt.RIGHT) {
            if (!view.back())
                hide();
        }else if(button == MButt.LEFT) {
            view.getSection().click();
            top.click();
        }
    }

    @Override
    protected void hoverTimer(GBox text) {
        view.getSection().hoverInfoGet(text);
        top.hoverInfoGet(text);
    }

    @Override
    protected boolean update(float ds) {
        GAME.SPEED.tmpPause();
        return false;
    }

    @Override
    protected boolean render(Renderer r, float ds) {
        GCOLOR.UI().bg().render(r, C.DIM());
        view.getSection().render(r, ds);
        GCOLOR.UI().panBG.render(r, top.body());
        GCOLOR.UI().border().render(r, top.body().x1(), top.body().x2(), top.body().y2(), top.body().y2() + 1);
        top.render(r, ds);
        renderAction.accept(ds);
        return false;
    }

    public static class FullView<Section extends GuiSection> {

        public final static int TOP_HEIGHT = FullWindow.TOP_HEIGHT+8;
        public static final int WIDTH = C.WIDTH();
        public static final int HEIGHT = C.HEIGHT() - TOP_HEIGHT;

        public final String title;

        @Getter
        private final Section section;

        public FullView(String title, Section section) {
            this.title = title;
            this.section = section;
        }

        public boolean back() {
            return false;
        }
    }
}
