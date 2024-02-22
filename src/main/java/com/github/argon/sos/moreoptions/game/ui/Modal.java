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

public class Modal<T extends GuiSection> extends Interrupter {
    @Getter
    protected final T section;

    @Getter
    protected final GPanel panel;

    protected final GuiSection panelSection;

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

    public void show() {
        show(VIEW.inters().manager);
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
    protected boolean update(float v) {
        return false;
    }
}
