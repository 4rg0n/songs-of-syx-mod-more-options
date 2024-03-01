package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.BiAction;
import init.C;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GBox;
import util.gui.panel.GPanel;
import view.interrupter.Interrupter;
import view.main.VIEW;

/**
 * For displaying a {@link GuiSection} in a modal window.
 *
 * @param <Section> ui element to display
 */
public class Window<Section extends GuiSection> extends Interrupter implements
    Showable<Window<Section>>,
    Hideable<Window<Section>>,
    Refreshable<Window<Section>>,
    Renderable<Window<Section>>
{
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

    protected Action<Window<Section>> showAction = o -> {};
    protected Action<Window<Section>> hideAction = o -> {};
    protected Action<Window<Section>> refreshAction = o -> {};

    protected BiAction<Window<Section>, Float> renderAction = (o1, o2) -> {};

    public Window(String title, Section section) {
        this.section = section;
        this.panel = new GPanel();
        this.panel.setTitle(title);
        this.panel.setCloseAction(this::hide);
        this.panelSection = new GuiSection();

        panelSection.add(panel);
        panelSection.add(section);
        section.pad(10, 20);

        panelSection.body().setDim(section.body());
        panel.body().setDim(section.body());
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
        hideAction.accept(this);
        super.hide();
    }

    @Override
    protected boolean hover(COORDINATE coordinate, boolean b) {
        panelSection.hover(coordinate);
        return false;
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
        renderAction.accept(this, v);
        return true;
    }

    @Override
    public void refresh() {
       refreshAction.accept(this);
    }

    @Override
    public void onRefresh(Action<Window<Section>> refreshAction) {
        this.refreshAction = refreshAction;
    }

    public void show() {
        hide = false;
        show(VIEW.inters().manager);
        showAction.accept(this);
    }

    @Override
    public void onShow(Action<Window<Section>> showAction) {
        this.showAction = showAction;
    }

    @Override
    protected boolean update(float v) {
        return false;
    }

    @Override
    public void onRender(BiAction<Window<Section>, Float> renderAction) {
        this.renderAction = renderAction;
    }

    @Override
    public void onHide(Action<Window<Section>> hideAction) {
        this.hideAction = hideAction;
    }
}