package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.MoreOptionsConfigurator;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import init.sprite.SPRITES;
import lombok.RequiredArgsConstructor;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;
import view.interrupter.IDebugPanel;
import view.ui.UIPanelTop;

import java.util.stream.Collectors;

import static com.github.argon.sos.moreoptions.MoreOptionsScript.MOD_INFO;

/**
 * Most UI elements are generated dynamically dictated by the given config {@link MoreOptionsConfig}.
 * So when a new entry is added, a new UI element like e.g. an additional slider will also be visible.
 */
@RequiredArgsConstructor
public class UIGameConfig {

    private final static Logger log = Loggers.getLogger(UIGameConfig.class);

    private final MoreOptionsModal moreOptionsModal;

    private final GameApis gameApis;

    private final MoreOptionsConfigurator configurator;

    private final ConfigStore configStore;

    /**
     * Debug commands are executable via the in game debug panel
     */
    public void initDebug() {
        log.debug("Initialize %s Debug Commands", MOD_INFO.name);
        IDebugPanel.add(MOD_INFO.name + ":show", moreOptionsModal::show);
        IDebugPanel.add(MOD_INFO.name + ":log.stats", () -> {
            log.info("Events Status:\n%s", gameApis.eventsApi().readEventsEnabledStatus()
                .entrySet().stream().map(entry -> entry.getKey() + " enabled: " + entry.getValue() + "\n")
                .collect(Collectors.joining()));
        });
    }

    /**
     * Generates UI with available config.
     * Adds config modal buttons functionality.
     *
     * @param currentConfig used to generate the UI
     */
    public void initUi(MoreOptionsConfig currentConfig) {
        log.debug("Initialize %s UI", MOD_INFO.name);
        moreOptionsModal.init(currentConfig);

        GButt.ButtPanel settlementButton = new GButt.ButtPanel(SPRITES.icons().s.cog) {
            @Override
            protected void clickA() {
                moreOptionsModal.show();
            }
        };

        settlementButton.hoverInfoSet(MOD_INFO.name);
        settlementButton.setDim(32, UIPanelTop.HEIGHT);

        // inject button for opening modal into game UI
        gameApis.uiApi().findUIElementInSettlementView(UIPanelTop.class)
            .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredField("right", uiPanelTop))
            .ifPresent(o -> {
                log.debug("Injecting into UIPanelTop#right in settlement view");
                GuiSection right = (GuiSection) o;
                right.addRelBody(8, DIR.W, settlementButton);
            });

        // Cancel
        moreOptionsModal.getCancelButton().clickActionSet(() -> {
            configStore.getCurrentConfig().ifPresent(moreOptionsModal::applyConfig);

            moreOptionsModal.hide();
        });

        // Apply & Save
        moreOptionsModal.getApplyButton().clickActionSet(() -> {
            MoreOptionsConfig config = moreOptionsModal.getConfig();
            configurator.applyConfig(config);
            configStore.setCurrentConfig(config);
            configStore.saveConfig(config);
        });

        // Reset to default
        moreOptionsModal.getResetButton().clickActionSet(() -> {
            MoreOptionsConfig defaultConfig = MoreOptionsConfig.builder().build();
            configurator.applyConfig(defaultConfig);
            configStore.setCurrentConfig(defaultConfig);
            configStore.saveConfig(defaultConfig);
            moreOptionsModal.applyConfig(defaultConfig);
        });

        // Undo changes
        moreOptionsModal.getUndoButton().clickActionSet(() ->
            configStore.getCurrentConfig().ifPresent(moreOptionsModal::applyConfig)
        );

        //Ok: Apply & Save & Exit
        moreOptionsModal.getOkButton().clickActionSet(() -> {
            MoreOptionsConfig config = moreOptionsModal.getConfig();
            configurator.applyConfig(config);
            configStore.setCurrentConfig(config);
            configStore.saveConfig(config);

            moreOptionsModal.hide();
        });
    }

    public void applyConfig(MoreOptionsConfig config) {
        moreOptionsModal.applyConfig(config);
    }
}
