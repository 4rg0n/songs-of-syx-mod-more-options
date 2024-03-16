package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV3Config;
import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.moreoptions.ui.panel.boosters.BoosterPresetsPanel;
import com.github.argon.sos.moreoptions.ui.panel.boosters.BoostersPanel;
import game.faction.Faction;
import snake2d.util.misc.STRING_RECIEVER;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BoostersPanelController extends AbstractUiController<BoostersPanel> {
    public BoostersPanelController(BoostersPanel boostersPanel) {
        super(boostersPanel);
        boostersPanel.refreshAction(panel -> refreshBoosters());

        boostersPanel.getLoadPresetButton().clickActionSet(this::loadBoostersPreset);
        boostersPanel.getSavePresetButton().clickActionSet(this::saveBoostersPreset);
        boostersPanel.getCopyButton().clickActionSet(this::copyBoostersConfig);
        boostersPanel.getPasteButton().clickActionSet(this::pasteBoostersConfig);
    }

    public void copyBoostersConfig() {
        Faction copiedFaction = element.copyBoostersConfig();
        notificator.notifySuccess(i18n.t("notification.boosters.copy", copiedFaction.name.toString()));
    }

    public void pasteBoostersConfig() {
        if (element.pasteBoostersConfig()) {
            notificator.notifySuccess(i18n.t("notification.boosters.paste"));
        } else {
            notificator.notifyError(i18n.t("notification.boosters.not.paste"));
        }
    }

    public void saveBoostersPreset() {
        Map<String, Range> preset = element.getCurrentBoosterSection().getValue();
        STRING_RECIEVER r = presetName -> {
            if (presetName != null && presetName.length() > 0) {
                element.getPresets().put(presetName.toString(), preset);
                notificator.notifySuccess(i18n.t("notification.boosters.preset.save", presetName.toString()));
            }
        };

        gameApis.ui().inters().input.requestInput(r, i18n.t("BoostersPanel.input.preset.name"));
    }

    public void loadBoostersPreset() {
        Map<String, Map<String, Range>> presets = element.getPresets();
        BoosterPresetsPanel presetsPanel = BoosterPresetsPanel.builder()
            .presets(presets)
            .clickAction(key -> {
                Map<String, Range> boostersPreset = presets.get(key);
                element.getCurrentBoosterSection().setValue(boostersPreset);
                notificator.notifySuccess(i18n.t("notification.boosters.preset.load", key));
            })
            .deleteAction((key, panel) -> {
                gameApis.ui().inters().yesNo.activate(i18n.t("BoostersPanel.text.yesNo.preset.delete"), () -> {
                    presets.remove(key);
                    panel.disableButtons(key);
                }, () -> {}, true);
            }).build();

        gameApis.ui().popup().show(presetsPanel, element.getLoadPresetButton());
    }

    public void refreshBoosters() {
        configDefaults.newBoostersConfig();
        Map<Faction, List<BoostersPanel.Entry>> boosterEntries = uiMapper.toBoosterPanelEntries(configDefaults.newBoostersConfig());
        element.refresh(boosterEntries);
        MoreOptionsV3Config currentConfig = configStore.getCurrentConfig();
        Objects.requireNonNull(currentConfig);
        element.setValue(currentConfig.getBoosters());
    }
}
