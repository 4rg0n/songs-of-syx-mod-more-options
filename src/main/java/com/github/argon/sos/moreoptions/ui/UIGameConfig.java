package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.Configurator;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.api.GameUiApi;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import init.sprite.SPRITES;
import lombok.RequiredArgsConstructor;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;
import view.ui.UIPanelTop;

import static com.github.argon.sos.moreoptions.MoreOptionsScript.MOD_INFO;

@RequiredArgsConstructor
public class UIGameConfig {

    private final static Logger log = Loggers.getLogger(UIGameConfig.class);

    private final MoreOptionsModal moreOptionsModal;

    private final GameUiApi gameUiApi;

    private final ConfigStore configStore;

    private final Configurator configurator;


    public void init() {
        log.debug("Initialize More Options UI");

        GButt.ButtPanel settlementButton = new GButt.ButtPanel(SPRITES.icons().s.cog) {
            @Override
            protected void clickA() {
                moreOptionsModal.activate();
            }
        };

        settlementButton.hoverInfoSet(MOD_INFO.name);
        settlementButton.setDim(32, UIPanelTop.HEIGHT);

        // inject into game UI
        gameUiApi.findUIElementInSettlementView(UIPanelTop.class)
            .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredField("right", uiPanelTop))
            .ifPresent(o -> {
                log.debug("Injecting into UIPanelTop#right in settlement view");
                GuiSection right = (GuiSection) o;
                right.addRelBody(8, DIR.W, settlementButton);
            });

        // Apply & Save
        moreOptionsModal.getApplyButton().clickActionSet(() -> {
            MoreOptionsConfig config = moreOptionsModal.getConfig();
            configurator.applyConfig(config);
            configStore.setCurrentConfig(config);
            configStore.saveProfileConfig(config);
        });

        // Reset to default
        moreOptionsModal.getResetButton().clickActionSet(() -> {
            MoreOptionsConfig defaultConfig = MoreOptionsConfig.builder().build();
            configurator.applyConfig(defaultConfig);
            configStore.setCurrentConfig(defaultConfig);
            configStore.saveProfileConfig(defaultConfig);
            moreOptionsModal.applyConfig(defaultConfig);
        });

        // Undo changes
        moreOptionsModal.getUndoButton().clickActionSet(() ->
            configStore.getCurrentConfig().ifPresent(config -> {
                configurator.applyConfig(config);
                moreOptionsModal.applyConfig(config);
            })
        );
    }

}
