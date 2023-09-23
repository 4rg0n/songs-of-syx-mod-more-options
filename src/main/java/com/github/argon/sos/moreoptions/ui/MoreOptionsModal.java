package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.HorizontalLine;
import com.github.argon.sos.moreoptions.ui.panel.EventsPanel;
import com.github.argon.sos.moreoptions.ui.panel.SoundsPanel;
import com.github.argon.sos.moreoptions.ui.panel.WeatherPanel;
import init.C;
import lombok.Getter;
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

    private final ConfigStore configStore;
    private final EventsPanel eventsPanel;
    private final SoundsPanel soundsPanel;
    private final WeatherPanel weatherPanel;

    @Getter
    private Button resetButton;

    @Getter
    private Button applyButton;

    @Getter
    private Button undoButton;

    @Getter
    private Button okButton;

    private double updateTimerSeconds = 0d;
    private final static int UPDATE_INTERVAL_SECONDS = 1;



    public MoreOptionsModal(ConfigStore configStore) {
        this.section = new GuiSection();
        this.configStore = configStore;
        MoreOptionsConfig config = configStore.getCurrentConfig()
            .orElse(MoreOptionsConfig.builder().build());

        GPanelL pan = new GPanelL();
        pan.setTitle("More Options");
        pan.setCloseAction(this::hide);
        section.add(pan);

        eventsPanel = new EventsPanel(config.getEvents());
        soundsPanel = new SoundsPanel(config.getSounds());
        weatherPanel = new WeatherPanel(config.getWeather());

        panels.put("Events", eventsPanel);
        panels.put("Sounds", soundsPanel);
        panels.put("Weather", weatherPanel);

        GuiSection header = header();
        section.add(header);

        switcher = new CLICKABLE.Switcher(eventsPanel);
        section.add(switcher);

        HorizontalLine horizontalLine = new HorizontalLine(eventsPanel.body().width(), 14, 1);
        section.add(horizontalLine);

        GuiSection footer = footer();
        section.add(footer);

        int width = eventsPanel.body().width() + 25;
        int height = eventsPanel.body().height() + header.body().height() + horizontalLine.body().height() + footer.body().height() + 45;

        pan.body.setDim(width, height);
        pan.body().centerIn(C.DIM());

        header.body().moveY1(pan.getInnerArea().y1());
        header.body().centerX(pan);

        eventsPanel.body().moveY1(header.body().y2() + 15);
        eventsPanel.body().centerX(header);

        horizontalLine.body().moveY1(eventsPanel.body().y2() + 15);
        horizontalLine.body().centerX(header);

        footer.body().moveY1(horizontalLine.body().y2() + 10);
        footer.body().centerX(header);
    }

    public void show() {
        show(VIEW.inters().manager);
    }
    public void hide() {
        super.hide();
    }

    public MoreOptionsConfig getConfig() {
        return MoreOptionsConfig.builder()
            .events(eventsPanel.getConfig())
            .sounds(soundsPanel.getConfig())
            .weather(weatherPanel.getConfig())
            .build();
    }

    public void applyConfig(MoreOptionsConfig config) {
        eventsPanel.applyConfig(config.getEvents());
        soundsPanel.applyConfig(config.getSounds());
        weatherPanel.applyConfig(config.getWeather());
    }

    /**
     * @return whether panel configuration is different from {@link ConfigStore#getCurrentConfig()} ()}
     */
    private boolean isDirty() {
        return configStore.getCurrentConfig()
            .map(currentConfig -> !getConfig().equals(currentConfig))
            // no current config in memory
            .orElse(true);
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

        this.undoButton = new Button("Undo", COLOR.WHITE25);
        undoButton.hoverInfoSet("Undo made changes");
        undoButton.setEnabled(false);
        section.addRight(10, undoButton);

        this.applyButton = new Button("Apply", COLOR.WHITE15);
        applyButton.hoverInfoSet("Apply and save options");
        applyButton.setEnabled(false);
        section.addRight(150, applyButton);

        this.okButton = new Button("OK", COLOR.WHITE15);
        okButton.hoverInfoSet("Apply and exit");
        section.addRight(10, okButton);

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
    protected boolean update(float seconds) {
        updateTimerSeconds += seconds;

        // check for changes in config
        if (updateTimerSeconds >= UPDATE_INTERVAL_SECONDS) {
            updateTimerSeconds = 0d;

            applyButton.setEnabled(isDirty());
            undoButton.setEnabled(isDirty());
        }

        return false;
    }
}
