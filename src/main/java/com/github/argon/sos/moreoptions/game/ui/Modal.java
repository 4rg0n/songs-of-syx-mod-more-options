package com.github.argon.sos.moreoptions.game.ui;

import init.C;
import lombok.Getter;
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
 * @param <T> ui element to display
 */
public class Modal<T extends GuiSection> extends Interrupter implements Showable<Modal<T>>, Refreshable<Modal<T>> {
    @Getter
    protected final T section;

    @Getter
    protected final GPanel panel;

    protected final GuiSection panelSection;

    private Action<Modal<T>> showAction = o -> {};
    private Action<Modal<T>> refreshAction = o -> {};

    public Modal(String title, T section) {
        this.section = section;
        this.panel = new GPanel();
        this.panel.setTitle(title);
        this.panel.setCloseAction(this::hide);
        this.panelSection = new GuiSection();

        panelSection.add(panel);
        panelSection.add(section);

        center();
    }

    public void hide() {
        super.hide();
    }

    public void center() {
        section.pad(20);
        panelSection.body().setDim(section.body());
        panel.body().setDim(section.body());

        panelSection.body().centerIn(C.DIM());
        panel.body().centerIn(C.DIM());

        section.body().centerX(panel.body());
        section.body().moveY1(panel.body().y1());
    }

    @Override
    protected boolean hover(COORDINATE coordinate, boolean b) {
        panelSection.hover(coordinate);

        // disable background interaction returning true
        return true;
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
        panelSection.render(renderer, v);
        return false;
    }

    @Override
    public void refresh() {
       refreshAction.accept(this);

        if (section instanceof Refreshable) {
            Refreshable<T> refreshable = (Refreshable<T>) section;
            refreshable.refresh();
        }
    }

    @Override
    public void onRefresh(Action<Modal<T>> refreshAction) {
        this.refreshAction = refreshAction;
    }

    public void show() {
        showAction.accept(this);

        if (section instanceof Showable) {
            Showable<T> showable = (Showable<T>) section;
            showable.show();
        }
        show(VIEW.inters().manager);
    }

    @Override
    public void onShow(Action<Modal<T>> showAction) {
        this.showAction = showAction;
    }

    @Override
    protected boolean update(float v) {
        return false;
    }
}
