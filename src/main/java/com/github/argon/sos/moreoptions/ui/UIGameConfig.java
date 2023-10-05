package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.MoreOptionsConfigurator;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.panel.BoostersPanel;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import init.paths.PATHS;
import init.sprite.SPRITES;
import lombok.RequiredArgsConstructor;
import snake2d.util.datatypes.DIR;
import snake2d.util.file.FileManager;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;
import view.interrupter.IDebugPanel;
import view.ui.UIPanelTop;

import java.util.List;
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

        List<BoostersPanel.Entry> boosterEntries = currentConfig.getBoosters().entrySet().stream().map(entry ->
            BoostersPanel.Entry.builder()
                .key(entry.getKey())
                .value(entry.getValue())
                .enemy(gameApis.boosterApi().isEnemyBooster(entry.getKey()))
                .player(gameApis.boosterApi().isPlayerBooster(entry.getKey()))
                .build()
        ).collect(Collectors.toList());

        moreOptionsModal.init(currentConfig, boosterEntries);

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

        // Cancel & Undo
        moreOptionsModal.getCancelButton().clickActionSet(() -> {
            undo();
            moreOptionsModal.hide();
        });

        // Apply & Save
        moreOptionsModal.getApplyButton().clickActionSet(() -> {
            MoreOptionsConfig config = moreOptionsModal.getConfig();
            applyAndSave(config);
        });

        // Reset UI to default
        moreOptionsModal.getResetButton().clickActionSet(() -> {
            MoreOptionsConfig defaultConfig = configStore.getDefaults().get();
            moreOptionsModal.applyConfig(defaultConfig);
        });

        // Undo changes
        moreOptionsModal.getUndoButton().clickActionSet(this::undo
        );

        //Ok: Apply & Save & Exit
        moreOptionsModal.getOkButton().clickActionSet(() -> {
            MoreOptionsConfig config = moreOptionsModal.getConfig();
            applyAndSave(config);
            moreOptionsModal.hide();
        });

        // opens folder with mod configuration
        moreOptionsModal.getFolderButton().clickActionSet(() -> {
            FileManager.openDesctop(PATHS.local().SETTINGS.get().toString());
        });
    }

    private void applyAndSave(MoreOptionsConfig config) {
        // only save when changes were made
        if (moreOptionsModal.isDirty()) {
            configurator.applyConfig(config);
            configStore.setCurrentConfig(config);
            configStore.saveConfig(config);
        }
    }

    private void undo() {
        configStore.getCurrentConfig().ifPresent(moreOptionsModal::applyConfig);
    }
}
