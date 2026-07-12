package com.github.argon.sos.mod.sdk.ui.window;

import com.github.argon.sos.mod.sdk.game.action.*;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import init.constant.C;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GBox;
import util.gui.panel.GPanel;
import view.interrupter.Interrupter;
import view.main.VIEW;

/**
 * For displaying a {@link GuiSection} in a floating window.
 * Background interactions are disabled.
 *
 * @param <Section> ui element to display
 */
public class Window<Section extends GuiSection> extends Interrupter implements
    Showable,
    Hideable,
    Refreshable,
    Renderable
{
    private final static Logger log = Loggers.getLogger(Window.class);

    /**
     * The ui element displayed as content of the window.
     */
    @Getter
    protected final Section section;

    /**
     * The panel used as frame around the window content.
     */
    @Getter
    protected final GPanel panel;

    /**
     * Contains {@link Window#panel} and {@link Window#section} together.
     */
    protected final GuiSection panelSection;

    private double hideUpdateTimerSeconds = 0d;

    @Setter
    @Accessors(fluent = true)
    private int showSeconds = 0;

    private boolean hide = false;
    private boolean isModal = false;

    /**
     * Executed when the window is shown.
     */
    @Setter
    @Accessors(fluent = true, chain = false)
    protected VoidAction showAction = () -> {};

    /**
     * Executed when the window is hidden.
     */
    @Setter
    @Accessors(fluent = true, chain = false)
    protected VoidAction hideAction = () -> {};

    /**
     * Executed when the window is refreshed.
     */
    @Setter
    @Accessors(fluent = true, chain = false)
    protected VoidAction refreshAction = () -> {};

    /**
     * Executed when the window is rendered.
     */
    @Setter
    @Accessors(fluent = true, chain = false)
    protected Action<Float> renderAction = o -> {};

    /**
     * Creates a new {@link Window}.
     *
     * @param title of the window
     * @param section to display in the window as content
     */
    public Window(@Nullable String title, Section section) {
        this(title, section, false);
    }

    /**
     * Creates a new {@link Window}.
     *
     * @param title of the window
     * @param section to display in the window as content
     * @param isModal whether the area around the window shall be blacked out and is not clickable
     */
    public Window(@Nullable String title, Section section, boolean isModal) {
        this.section = section;
        this.panel = new GPanel();
        this.panel.setTitle(title);
        this.panel.setCloseAction(this::hide);
        this.panelSection = new GuiSection();
        this.isModal = isModal;

        panelSection.add(panel);
        panelSection.add(section);
        section.pad(10, 20);

        panelSection.body().setDim(section.body());
        panel.body().setDim(section.body());

        log.debug("'%s' dimensions: %sx%s", title,
            section.body().width(),
            section.body().height()
        );

        if (isModal) {
            center();
        }
    }

    /**
     * Centers the window in the middle of the screen.
     */
    public void center() {
        panelSection.body().centerIn(C.DIM());
        panel.body().centerIn(C.DIM());

        section.body().centerX(panel.body());
        section.body().moveY1(panel.body().y1());
    }

    /**
     * Moves the window into the upper left-hand corner of the screen.
     */
    public void upLeft() {
        panelSection.body().moveX1(C.DIM().x1());
        panel.body().moveX1(C.DIM().x1());

        section.body().centerX(panel.body());
        section.body().moveY1(panel.body().y1());
    }

    /**
     * Hides the window.
     * Executes {@link Window#hideAction}.
     */
    @Override
    public void hide() {
        hide = true;
        hideAction.accept();
        super.hide();
    }

    /**
     * Executed when the window is hovered.
     *
     * @param mouseCoordinate coordinates of the mouse pointer
     * @param mouseMoved whether the mouse has moved since the last render loop
     * @return whether the window is hover-able
     */
    @Override
    protected boolean hover(COORDINATE mouseCoordinate, boolean mouseMoved) {
        panelSection.hover(mouseCoordinate);
        return true; // disable background interactions
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void mouseClick(MButt mButt) {
        if (mButt == MButt.LEFT) {
            panelSection.click();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void hoverTimer(GBox gBox) {
        panelSection.hoverInfoGet(gBox);
    }

    /**
     * Executed when the window is rendered.
     *
     * @param renderer to use
     * @param deltaSeconds since the last render loop
     * @return whether it should render in the next loop
     */
    @Override
    protected boolean render(Renderer renderer, float deltaSeconds) {
        if (showSeconds > 0 && !hide) {
            if (hideUpdateTimerSeconds >= showSeconds) {
                hideUpdateTimerSeconds = 0d;
                hide();
            } else {
                hideUpdateTimerSeconds += deltaSeconds;
            }
        }

        panelSection.render(renderer, deltaSeconds);
        renderAction.accept(deltaSeconds);
        return !isModal;
    }

    /**
     * Executes the {@link Window#refreshAction}.
     */
    @Override
    public void refresh() {
       refreshAction.accept();
    }

    /**
     * Shows the window.
     * Executes the {@link Window#showAction}.
     */
    @Override
    public void show() {
        hide = false;
        show(VIEW.inters().manager);
        showAction.accept();
    }

    /**
     * Executed by the game update loop.
     *
     * @param deltaSeconds since last update loop
     * @return whether the next update shall be executed
     */
    @Override
    protected boolean update(float deltaSeconds) {
        return false;
    }
}
