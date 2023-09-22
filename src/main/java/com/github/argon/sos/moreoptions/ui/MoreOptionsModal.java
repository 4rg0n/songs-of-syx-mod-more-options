package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.HorizontalLine;
import com.github.argon.sos.moreoptions.ui.panel.Events;
import com.github.argon.sos.moreoptions.ui.panel.Sounds;
import com.github.argon.sos.moreoptions.ui.panel.Weather;
import init.C;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.color.COLOR;
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

    private Button resetButton;
    private Button applyButton;

    public final static int WIDTH = 800;
    public final static int HEIGHT = 600;

    public MoreOptionsModal(MoreOptionsConfig config) {
        this.section = new GuiSection();

        GPanelL pan = new GPanelL();
        pan.setTitle("More Options");
        pan.setCloseAction(this::hide);
        section.add(pan);

        Events events = new Events(config.getEvents());
        Sounds sounds = new Sounds(config.getAmbienceSounds());
        Weather weather = new Weather(config.getWeather());

        panels.put("Events", events);
        panels.put("Sounds", sounds);
        panels.put("Weather", weather);

        GuiSection header = header();
        section.add(header);

        switcher = new CLICKABLE.Switcher(events);
        section.add(switcher);

        HorizontalLine horizontalLine = new HorizontalLine(events.body().width(), 14, 1);
        section.add(horizontalLine);

        GuiSection footer = footer();
        section.add(footer);

        int width = events.body().width() + 25;
        int height = events.body().height() + header.body().height() + horizontalLine.body().height() + footer.body().height() + 45;

        pan.body.setDim(width, height);
        pan.body().centerIn(C.DIM());

        header.body().moveY1(pan.getInnerArea().y1());
        header.body().centerX(pan);

        events.body().moveY1(header.body().y2() + 15);
        events.body().centerX(header);

        horizontalLine.body().moveY1(events.body().y2() + 15);
        horizontalLine.body().centerX(header);

        footer.body().moveY1(horizontalLine.body().y2() + 10);
        footer.body().centerX(header);
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

    private GuiSection footer() {
        GuiSection section = new GuiSection();

        this.resetButton = new Button("Default", COLOR.WHITE25);
        resetButton.hoverInfoSet("Resets to default options");
        section.addRight(0, resetButton);

        this.applyButton = new Button("Apply", COLOR.WHITE15);
        applyButton.hoverInfoSet("Apply and save options");
        section.addRight(150, applyButton);

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
