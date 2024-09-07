package com.github.argon.sos.moreoptions.ui.tab.boosters;

import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.action.BiAction;
import com.github.argon.sos.mod.sdk.game.ui.Button;
import com.github.argon.sos.mod.sdk.game.ui.ColumnRow;
import com.github.argon.sos.mod.sdk.game.ui.Table;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.i18n.I18n;
import com.github.argon.sos.moreoptions.config.domain.Range;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.NonNull;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GTextR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * For selecting or removing a saved boosters preset
 */
public class BoostersPresetsSection extends GuiSection {

    private final static I18n i18n = I18n.get(BoostersTab.class);

    private final Map<String, Button> presetButtons = new HashMap<>();
    private final Map<String, Button> deleteButtons = new HashMap<>();
    private final Map<String, Button> shareButtons = new HashMap<>();

    @Builder
    public BoostersPresetsSection(
        @NonNull Map<String, Map<String, Range>> presets,
        @NonNull Action<String> clickAction,
        @NonNull BiAction<String, BoostersPresetsSection> deleteAction,
        @NonNull Action<String> shareAction
    ) {
        if (presets.isEmpty()) {
            add(new GTextR(UI.FONT().S, i18n.t("BoostersTab.text.empty.preset")));
            return;
        }

        Map<String, Button> presetButtons = presets.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> new Button(entry.getKey())
        ));

        // all buttons get the same width
        int presetButtonsWidth = UiUtil.getMaxWidth(presetButtons.values());
        List<ColumnRow<String>> rows = presetButtons.entrySet().stream().map(entry -> {
            String key = entry.getKey();
            Button presetButton = entry.getValue();
            presetButton.hoverInfoSet(i18n.t("BoostersTab.button.preset.desc", key));
            Button deleteButton = new Button(SPRITES.icons().m.trash, i18n.t("BoostersTab.button.preset.delete.desc", key));
            Button shareButton = new Button(SPRITES.icons().m.openscroll, i18n.t("BoostersTab.button.preset.share.desc", key));
            presetButton.body().setWidth(presetButtonsWidth);

            deleteButton.bg(COLOR.RED50);
            shareButton.bg(COLOR.WHITE65);

            this.presetButtons.put(key, presetButton);
            this.deleteButtons.put(key, deleteButton);
            this.shareButtons.put(key, shareButton);

            presetButton.clickActionSet(() -> clickAction.accept(key));
            deleteButton.clickActionSet(() -> deleteAction.accept(key, this));
            shareButton.clickActionSet(() -> shareAction.accept(key));

            return ColumnRow.<String>builder()
                .column(presetButton)
                .column(shareButton)
                .column(deleteButton)
                .build();
        }).collect(Collectors.toList());

        Table<String> buttonTable = Table.<String>builder()
            .rows(rows)
            .backgroundColor(COLOR.WHITE10)
            .displayHeight(300)
            .evenOdd(false)
            .build();

        add(buttonTable);
    }

    public void disableButtons(String key) {
        if (presetButtons.containsKey(key)) {
            presetButtons.get(key).activeSet(false);
        }

        if (deleteButtons.containsKey(key)) {
            deleteButtons.get(key).activeSet(false);
        }

        if (shareButtons.containsKey(key)) {
            shareButtons.get(key).activeSet(false);
        }
    }
}
