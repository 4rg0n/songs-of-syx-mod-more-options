package com.github.argon.sos.moreoptions.game.ui;

import init.C;
import lombok.Getter;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GBox;
import util.gui.panel.GPanelL;
import view.interrupter.Interrupter;
import view.main.VIEW;

public class Modal<T extends GuiSection> extends Interrupter {
    @Getter
    protected final T section;

    @Getter
    protected final GPanelL panel;

    protected final GuiSection panelSection;

    /**
     * Will render the modal without the game in the background
     */
    private final boolean overlay = false;


    public Modal(String title, T section) {
        this(title, section, false);
    }

    public Modal(String title, T section, boolean overlay) {
        this.section = section;
        this.panel = new GPanelL();
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
        int space = 50;
        int width = section.body().width() + space * 2;
        int height = section.body().height() + space * 2;

        panel.body().setDim(width, height);
        panel.body().centerIn(C.DIM());
        section.body().centerX(panel.body());
        section.body().moveY1(panel.body().y1() + space);
    }

    @Override
    protected boolean hover(COORDINATE coordinate, boolean b) {
        return panelSection.hover(coordinate);
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
        return overlay;
    }

    @Override
    protected boolean update(float v) {
        return false;
    }
}
