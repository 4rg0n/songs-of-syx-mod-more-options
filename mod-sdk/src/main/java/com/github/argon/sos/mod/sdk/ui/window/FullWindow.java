package com.github.argon.sos.mod.sdk.ui.window;

import com.github.argon.sos.mod.sdk.game.action.*;
import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.ui.text.Label;
import com.github.argon.sos.mod.sdk.ui.switcher.Switcher;
import game.GAME;
import init.constant.C;
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

/**
 * A window the size of the full screen.
 *
 * @param <Section> the type of the displayed ui element in the window
 */
public class FullWindow<Section extends GuiSection> extends Interrupter implements
    Showable,
    Hideable,
    Refreshable,
    Renderable
{
    private final static Logger log = Loggers.getLogger(FullWindow.class);

    /**
     * Height of the tob bar.
     */
    public static final int TOP_HEIGHT = 40;

    @Getter
    private final FullView<Section> view;
    private final GuiSection top = new GuiSection();

    /**
     * Optional action to be executed when the window is shown.
     */
    @Setter
    @Accessors(fluent = true, chain = false)
    protected VoidAction showAction = () -> {};
    /**
     * Optional action to be executed when the window is hidden.
     */
    @Setter
    @Accessors(fluent = true, chain = false)
    protected VoidAction hideAction = () -> {};
    /**
     * Optional action to be executed when the window refreshes.
     */
    @Setter
    @Accessors(fluent = true, chain = false)
    protected VoidAction refreshAction = () -> {};
    /**
     * Optional action to be executed when the window is rendered.
     */
    @Setter
    @Accessors(fluent = true, chain = false)
    protected Action<Float> renderAction = o -> {};

    /**
     * Displays any {@link GuiSection} as a full screen element.
     * Provides a header with the window name, an optional menu bar and a close button.
     *
     * @param name of the window
     * @param section to display
     * @param menu optionally rendered menu in the top header
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

    /**
     * Returns the contained section displayed in the window.
     *
     * @return contained section displayed in the window
     */
    public Section getSection() {
       return view.getSection();
    }

    /**
     * Displays the window.
     * Will execute the {@link FullWindow#showAction}.
     */
    public void show() {
        view.getSection().body().moveY1(FullView.TOP_HEIGHT);
        view.getSection().body().centerX(C.DIM());
        showAction.accept();

        if (view.getSection() instanceof Showable) {
            ((Showable) view.getSection()).show();
        }

        super.show(VIEW.inters().manager);
    }

    /**
     * Hides the window.
     * Will execute the {@link FullWindow#hideAction}.
     */
    public void hide() {
        hideAction.accept();

        if (view.getSection() instanceof Hideable) {
            ((Hideable) view.getSection()).hide();
        }

        super.hide();
    }

    /**
     * Executed when the window is hovered.
     *
     * @param mouseCoordinates x and y coordinates of the mouse pointer
     * @param mouseHasMoved whether the mouse has moved since the last update loop
     * @return whether hovering is allowed
     */
    @Override
    protected boolean hover(COORDINATE mouseCoordinates, boolean mouseHasMoved) {
        view.getSection().hover(mouseCoordinates);
        top.hover(mouseCoordinates);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void mouseClick(MButt button) {
        if (button == MButt.RIGHT) {
                hide();
        }else if(button == MButt.LEFT) {
            view.getSection().click();
            top.click();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void hoverTimer(GBox text) {
        view.getSection().hoverInfoGet(text);
        top.hoverInfoGet(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean update(float deltaSeconds) {
        GAME.SPEED.tmpPause();
        return false;
    }

    /**
     * Executed when the {@link FullWindow} is rendered.
     *
     * @param renderer to use
     * @param deltaSeconds since last render loop
     * @return whether it should render next time
     */
    @Override
    protected boolean render(Renderer renderer, float deltaSeconds) {
        GCOLOR.UI().bg().render(renderer, C.DIM());
        view.getSection().render(renderer, deltaSeconds);
        GCOLOR.UI().panBG.render(renderer, top.body());
        GCOLOR.UI().border().render(renderer, top.body().x1(), top.body().x2(), top.body().y2(), top.body().y2() + 1);
        top.render(renderer, deltaSeconds);
        renderAction.accept(deltaSeconds);
        return false;
    }

    /**
     * The view holding the {@link GuiSection} with the content of the window.
     *
     * @param <Section> type of the section contained in the full view
     */
    public static class FullView<Section extends GuiSection> {

        /**
         * Height of the top bar,
         */
        public final static int TOP_HEIGHT = FullWindow.TOP_HEIGHT+8;
        /**
         * Width of the view.
         */
        public static final int WIDTH = C.WIDTH();
        /**
         * Height of the view
         */
        public static final int HEIGHT = C.HEIGHT() - TOP_HEIGHT;

        /**
         * Title displayed in the top bar.
         */
        public final String title;

        @Getter
        private final Section section;

        /**
         * Creates a new {@link FullView} with given title and section.
         *
         * @param title displayed in the top bar
         * @param section to display in the view
         */
        public FullView(String title, Section section) {
            this.title = title;
            this.section = section;
        }
    }
}
