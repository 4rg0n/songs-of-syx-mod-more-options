package com.github.argon.sos.moreoptions.ui.panel.boosters;

import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.BiAction;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import com.github.argon.sos.moreoptions.i18n.I18n;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.NonNull;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GTextR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BoosterPresetsPanel extends GuiSection {

    private final static I18n i18n = I18n.get(BoostersPanel.class);

    private final Map<String, Button> presetButtons = new HashMap<>();
    private final Map<String, Button> deleteButtons = new HashMap<>();

    @Builder
    public BoosterPresetsPanel(
        @NonNull Map<String, Map<String, Range>> presets,
        @NonNull Action<String> clickAction,
        @NonNull BiAction<String, BoosterPresetsPanel> deleteAction
    ) {
        if (presets.isEmpty()) {
            add(new GTextR(UI.FONT().S, i18n.t("BoostersPanel.text.empty.preset")));
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
            Button deleteButton = new Button(SPRITES.icons().m.trash);
            presetButton.body().setWidth(presetButtonsWidth);

            this.presetButtons.put(key, presetButton);
            this.deleteButtons.put(key, deleteButton);

            presetButton.clickActionSet(() -> {
                clickAction.accept(key);
            });

            deleteButton.clickActionSet(() -> {
                deleteAction.accept(key, this);
            });

            return ColumnRow.<String>builder()
                .column(presetButton)
                .column(deleteButton)
                .build();
        }).collect(Collectors.toList());

        Table<String> buttonTable = Table.<String>builder()
            .rows(rows)
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
    }
}
