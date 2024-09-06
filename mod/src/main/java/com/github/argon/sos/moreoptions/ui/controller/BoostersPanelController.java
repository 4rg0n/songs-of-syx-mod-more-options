package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.JsonMapper;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.mapper.TypeInfo;
import com.github.argon.sos.moreoptions.json.writer.JsonWriters;
import com.github.argon.sos.moreoptions.ui.msg.Message;
import com.github.argon.sos.moreoptions.ui.tab.boosters.BoostersPresetsSection;
import com.github.argon.sos.moreoptions.ui.tab.boosters.BoostersSection;
import com.github.argon.sos.moreoptions.ui.tab.boosters.BoostersTab;
import com.github.argon.sos.mod.sdk.util.Clipboard;
import game.faction.Faction;
import snake2d.util.misc.STRING_RECIEVER;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BoostersPanelController extends AbstractUiController<BoostersTab> {


    public BoostersPanelController(BoostersTab boostersTab) {
        super(boostersTab);
        boostersTab.refreshAction(panel -> refreshBoosters());

        boostersTab.getLoadPresetButton().clickActionSet(this::loadBoostersPreset);
        boostersTab.getSavePresetButton().clickActionSet(this::saveBoostersPreset);
        boostersTab.getCopyButton().clickActionSet(this::copyBoostersConfig);
        boostersTab.getPasteButton().clickActionSet(this::pasteBoostersConfig);
        boostersTab.getPasteFactionsButton().clickActionSet(this::pasteBoostersConfigToAllNPCFactions);
        boostersTab.getResetCurrentButton().clickActionSet(this::resetCurrentBoosters);
        boostersTab.getResetFactionsButton().clickActionSet(this::resetNPCFactionsBoosters);
    }

    public void resetNPCFactionsBoosters() {
        Map<String, Range> boosterValues = configDefaults.newBoosters().values().stream().collect(Collectors.toMap(
            BoostersConfig.Booster::getKey,
            BoostersConfig.Booster::getRange
        ));

        element.getBoostersSections().forEach((faction, boostersSection) -> {
            if (gameApis.faction().isPlayer(faction)) {
                return;
            }

            boostersSection.setValue(boosterValues);
        });

        Message.notifySuccess("notification.boosters.factions.reset");
    }

    public void resetCurrentBoosters() {
        BoostersSection currentBoosterSection = element.getCurrentBoosterSection();
        Map<String, Range> boosterValues = configDefaults.newBoosters().values().stream().collect(Collectors.toMap(
            BoostersConfig.Booster::getKey,
            BoostersConfig.Booster::getRange
        ));
        currentBoosterSection.setValue(boosterValues);

        Message.notifySuccess("notification.boosters.reset", currentBoosterSection.getFaction().name.toString());
    }

    public void copyBoostersConfig() {
        Faction copiedFaction = element.copyBoosters();
        Message.notifySuccess("notification.boosters.copy", copiedFaction.name.toString());
    }

    public void pasteBoostersConfigToAllNPCFactions() {
        int amount = element.pasteBoostersToNPCFactions();

        if (amount > 0) {
            Message.notifySuccess("notification.boosters.factions.paste", amount);
        } else {
            Message.notifyError("notification.boosters.factions.not.paste");
        }
    }

    public void pasteBoostersConfig() {
        if (element.pasteBoosters()) {
            Message.notifySuccess("notification.boosters.paste");
        } else {
            Message.notifyError("notification.boosters.not.paste");
        }
    }

    public void saveBoostersPreset() {
        Map<String, Range> preset = element.getCurrentBoosterSection().getValue();
        STRING_RECIEVER r = presetName -> {
            if (presetName != null && presetName.length() > 0) {
                element.getBoosterPresets().put(presetName.toString(), preset);
                MoreOptionsV5Config currentConfig = configStore.getCurrentConfig();

                if (currentConfig != null) {
                    currentConfig.getBoosters().setPresets(element.getValue().getPresets());
                    configStore.save(currentConfig);
                }

                Message.notifySuccess("notification.boosters.preset.save", presetName.toString());
            }
        };

        gameApis.ui().inters().input.requestInput(r, i18n.t("BoostersTab.input.preset.name"));
    }

    public void loadBoostersPreset() {
        Map<String, Map<String, Range>> presets = element.getBoosterPresets();
        BoostersPresetsSection presetsPanel = BoostersPresetsSection.builder()
            .presets(presets)
            .clickAction(key -> {
                Map<String, Range> boostersPreset = presets.get(key);
                element.getCurrentBoosterSection().setValue(boostersPreset);
                Message.notifySuccess("notification.boosters.preset.load", key);
            })
            .deleteAction((key, panel) -> {
                gameApis.ui().inters().yesNo.activate(i18n.t("BoostersTab.text.yesNo.preset.delete", key), () -> {
                    presets.remove(key);
                    panel.disableButtons(key);
                }, () -> {}, true);
            })
            .shareAction(key -> {
                try {
                    Map<String, Range> boostersPreset = presets.get(key);
                    JsonElement jsonElement = JsonMapper.mapObject(boostersPreset, new TypeInfo<Map<String, Range>>(){});
                    Json json = new Json(jsonElement, JsonWriters.jsonEPretty());

                    Clipboard.write(json.write());
                    Message.notifySuccess("notification.boosters.preset.copy", key);
                } catch (Exception e) {
                    Message.errorDialog(e, "notification.boosters.preset.not.copy");
                }
            })
            .build();

        gameApis.ui().popup().show(presetsPanel, element.getLoadPresetButton());
    }

    public void refreshBoosters() {
        configDefaults.newBoostersConfig();
        Map<Faction, List<BoostersTab.Entry>> boosterEntries = uiMapper.toBoosterPanelEntries(configDefaults.newBoostersConfig());
        element.refresh(boosterEntries);
        MoreOptionsV5Config currentConfig = configStore.getCurrentConfig();
        Objects.requireNonNull(currentConfig);
        element.setValue(currentConfig.getBoosters());
    }
}
