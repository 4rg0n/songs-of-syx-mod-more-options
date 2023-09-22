package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.ui.panel.Events;
import com.github.argon.sos.moreoptions.ui.panel.Particles;
import com.github.argon.sos.moreoptions.ui.panel.Sounds;
import init.C;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import util.gui.misc.GBox;
import util.gui.misc.GButt;
import util.gui.panel.GPanelL;
import view.interrupter.Interrupter;
import view.main.VIEW;

import java.util.LinkedHashMap;
import java.util.Map;

public class MoreOptionsModal extends Interrupter {

    private final GuiSection section;

    private final CLICKABLE.Switcher switcher;

    private final Map<String, GuiSection> panels = new LinkedHashMap<>();

    public final static int WIDTH = 800;
    public final static int HEIGHT = 600;

    public MoreOptionsModal(MoreOptionsConfig config) {
        this.section = new GuiSection();

        GPanelL pan = new GPanelL();
        pan.body.setDim(WIDTH, HEIGHT);
        pan.setTitle("More Options");
        pan.setCloseAction(this::hide);
        pan.body().centerIn(C.DIM());
        section.add(pan);

        Events events = new Events(config.getEvents());
        Sounds sounds = new Sounds(config.getAmbienceSounds());
        Particles particles = new Particles();

        panels.put("Events", events);
        panels.put("Sounds", sounds);
        panels.put("Particles", particles);

        GuiSection header = header();
        header.body().moveY1(pan.getInnerArea().y1());
        header.body().centerX(pan);
        section.add(header);

        events.body().moveY1(header.body().y2() + 16);
        events.body().centerX(header);

        switcher = new CLICKABLE.Switcher(events);
        section.add(switcher);
    }

    public void activate() {
        show(VIEW.inters().manager);
    }



    private GuiSection header() {
        GuiSection section = new GuiSection();

        panels.forEach((title, panel) -> {
            section.addRightC(0, new GButt.ButtPanel(title) {
                @Override
                protected void clickA() {
                    switcher.set(panel, DIR.N);
                }

                @Override
                protected void renAction() {
                    selectedSet(switcher.get() == panel);
                }

            }.setDim(136, 32));
        });

        return section;
    }

    @Override
    protected boolean hover(COORDINATE coordinate, boolean b) {
        return section.hover(coordinate);
    }

    @Override
    protected void mouseClick(MButt mButt) {
        if (mButt == MButt.RIGHT)
            hide();
        else if (mButt == MButt.LEFT)
            section.click();
    }

    @Override
    protected void hoverTimer(GBox gBox) {
        section.hoverInfoGet(gBox);
    }

    @Override
    protected boolean render(Renderer renderer, float v) {
        section.render(renderer, v);
        return true;
    }

    @Override
    protected boolean update(float v) {
        return true;
    }
}
