package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.action.*;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.C;
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

    @Getter
    protected final Section section;

    @Getter
    protected final GPanel panel;

    protected final GuiSection panelSection;

    private double hideUpdateTimerSeconds = 0d;

    @Setter
    @Accessors(fluent = true)
    private int showSeconds = 0;

    private boolean hide = false;
    private boolean isModal = false;

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

    public Window(@Nullable String title, Section section) {
        this(title, section, false);
    }

    /**
     * @param title of the window
     * @param section to display in the window as content
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
     * Centers in middle of screen
     */
    public void center() {
        panelSection.body().centerIn(C.DIM());
        panel.body().centerIn(C.DIM());

        section.body().centerX(panel.body());
        section.body().moveY1(panel.body().y1());
    }

    public void upLeft() {
        panelSection.body().moveX1(C.DIM().x1());
        panel.body().moveX1(C.DIM().x1());

        section.body().centerX(panel.body());
        section.body().moveY1(panel.body().y1());
    }

    public void hide() {
        hide = true;
        hideAction.accept();
        super.hide();
    }

    @Override
    protected boolean hover(COORDINATE coordinate, boolean b) {
        panelSection.hover(coordinate);
        return true; // disable background interactions
    }

    @Override
    protected void mouseClick(MButt mButt) {
        if (mButt == MButt.LEFT) {
            panelSection.click();
        }
    }

    @Override
    protected void hoverTimer(GBox gBox) {
        panelSection.hoverInfoGet(gBox);
    }

    @Override
    protected boolean render(Renderer renderer, float v) {
        if (showSeconds > 0 && !hide) {
            if (hideUpdateTimerSeconds >= showSeconds) {
                hideUpdateTimerSeconds = 0d;
                hide();
            } else {
                hideUpdateTimerSeconds += v;
            }
        }

        panelSection.render(renderer, v);
        renderAction.accept(v);
        return !isModal;
    }

    @Override
    public void refresh() {
       refreshAction.accept();
    }

    public void show() {
        hide = false;
        show(VIEW.inters().manager);
        showAction.accept();
    }

    @Override
    protected boolean update(float v) {
        return false;
    }

}
