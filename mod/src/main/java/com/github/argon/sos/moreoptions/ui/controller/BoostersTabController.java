package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.json.Json;
import com.github.argon.sos.mod.sdk.json.JsonMapper;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.mapper.TypeInfo;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriters;
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

public class BoostersTabController extends AbstractUiController<BoostersTab> {

    public BoostersTabController(BoostersTab boostersTab) {
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

        getElement().getBoostersSections().forEach((faction, boostersSection) -> {
            if (gameApis.faction().isPlayer(faction)) {
                return;
            }

            boostersSection.setValue(boosterValues);
        });

        messages.notifySuccess("notification.boosters.factions.reset");
    }

    public void resetCurrentBoosters() {
        BoostersSection currentBoosterSection = getElement().getCurrentBoosterSection();
        Map<String, Range> boosterValues = configDefaults.newBoosters().values().stream().collect(Collectors.toMap(
            BoostersConfig.Booster::getKey,
            BoostersConfig.Booster::getRange
        ));
        currentBoosterSection.setValue(boosterValues);

        messages.notifySuccess("notification.boosters.reset", currentBoosterSection.getFaction().name.toString());
    }

    public void copyBoostersConfig() {
        Faction copiedFaction = getElement().copyBoosters();
        messages.notifySuccess("notification.boosters.copy", copiedFaction.name.toString());
    }

    public void pasteBoostersConfigToAllNPCFactions() {
        int amount = getElement().pasteBoostersToNPCFactions();

        if (amount > 0) {
            messages.notifySuccess("notification.boosters.factions.paste", amount);
        } else {
            messages.notifyError("notification.boosters.factions.not.paste");
        }
    }

    public void pasteBoostersConfig() {
        if (getElement().pasteBoosters()) {
            messages.notifySuccess("notification.boosters.paste");
        } else {
            messages.notifyError("notification.boosters.not.paste");
        }
    }

    public void saveBoostersPreset() {
        Map<String, Range> preset = getElement().getCurrentBoosterSection().getValue();
        STRING_RECIEVER r = presetName -> {
            if (presetName != null && presetName.length() > 0) {
                getElement().getBoosterPresets().put(presetName.toString(), preset);
                MoreOptionsV5Config currentConfig = configStore.getCurrentConfig();

                if (currentConfig != null) {
                    currentConfig.getBoosters().setPresets(getElement().getValue().getPresets());
                    configStore.save(currentConfig);
                }

                messages.notifySuccess("notification.boosters.preset.save", presetName.toString());
            }
        };

        gameApis.ui().inters().input.requestInput(r, i18n.t("BoostersTab.input.preset.name"));
    }

    public void loadBoostersPreset() {
        Map<String, Map<String, Range>> presets = getElement().getBoosterPresets();
        BoostersPresetsSection presetsPanel = BoostersPresetsSection.builder()
            .presets(presets)
            .clickAction(key -> {
                Map<String, Range> boostersPreset = presets.get(key);
                getElement().getCurrentBoosterSection().setValue(boostersPreset);
                messages.notifySuccess("notification.boosters.preset.load", key);
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
                    messages.notifySuccess("notification.boosters.preset.copy", key);
                } catch (Exception e) {
                    messages.errorDialog(e, "notification.boosters.preset.not.copy");
                }
            })
            .build();

        gameApis.ui().popup().show(presetsPanel, getElement().getLoadPresetButton());
    }

    public void refreshBoosters() {
        configDefaults.newBoostersConfig();
        Map<Faction, List<BoostersTab.Entry>> boosterEntries = uiMapper.toBoostersTabEntries(configDefaults.newBoostersConfig());
        getElement().refresh(boosterEntries);
        MoreOptionsV5Config currentConfig = configStore.getCurrentConfig();
        Objects.requireNonNull(currentConfig);
        getElement().setValue(currentConfig.getBoosters());
    }
}
