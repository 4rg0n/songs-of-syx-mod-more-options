package com.github.argon.sos.moreoptions.ui.tab.boosters;

import com.github.argon.sos.mod.sdk.game.action.Valuable;
import com.github.argon.sos.mod.sdk.game.ui.*;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.util.Maps;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.game.ui.Slider;
import com.github.argon.sos.moreoptions.ui.UiMapper;
import game.boosting.BOOSTABLE_O;
import game.boosting.Boostable;
import game.faction.FACTIONS;
import game.faction.Faction;
import game.faction.npc.FactionNPC;
import init.sprite.UI.UI;
import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GHeader;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BoostersSection extends GuiSection implements Valuable<Map<String, Range>> {
    private static final I18nTranslator i18n = ModModule.i18n().get(BoostersTab.class);

    private final Table<Range> boosterTable;

    @Getter
    private final Faction faction;

    public BoostersSection(Faction faction, List<BoostersTab.Entry> boosterEntries, int availableHeight) {
        this.faction = faction;

        Map<String, List<BoostersTab.Entry>> groupedBoosterEntries = UiMapper.toBoosterPanelEntriesCategorized(boosterEntries);
        Map<String, List<ColumnRow<Range>>> boosterRows = groupedBoosterEntries.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .sorted(Comparator.comparing(boosterEntry -> boosterEntry.getBoosters().getAdd().getOrigin().name.toString()))
                    .map(this::boosterRow)
                    .collect(Collectors.toList())
            ));

        StringInputSprite searchInput = new StringInputSprite(16, UI.FONT().M).placeHolder(i18n.t("BoostersTab.search.input.name"));
        Input search = new Input(searchInput);

        GHeader titleHeader = new GHeader(faction.name, UI.FONT().H1);
        GuiSection header = new GuiSection();
        header.addRightC(0, faction.banner().HUGE);
        header.addRightC(20, titleHeader);

        int tableHeight = availableHeight - header.body().height() - search.body().height() - 30;
        this.boosterTable = Table.<Range>builder()
            .rowsCategorized(boosterRows)
            .evenOdd(true)
            .scrollable(true)
            .highlight(true)
            .search(searchInput)
            .backgroundColor(COLOR.WHITE10)
            .displayHeight(tableHeight)
            .build();

        addDownC(0, header);
        addDownC(10, search);
        addDownC(10, boosterTable);
    }

    private ColumnRow<Range> boosterRow(BoostersTab.Entry boosterEntry) {
        Boostable boostable = boosterEntry.getBoosters().getAdd().getOrigin();
        Range rangePerc;
        Range rangeAdd;
        Range.ApplyMode activeKey;

        BOOSTABLE_O bonus;
        if (faction instanceof FactionNPC) {
            bonus = ((FactionNPC) faction).bonus;
        } else {
            bonus = FACTIONS.player();
        }

        // Label with hover
        Label boosterLabel = Label.builder()
            .name(boostable.name.toString())
            .hoverGuiAction(guiBox -> {
                guiBox.title(faction.name + ": " + boostable.name);
                guiBox.text(boostable.desc);
                guiBox.NL(8);
                boostable.hoverDetailed(guiBox, bonus, null, true);
            })
            .build();

        // Icon
        GuiSection icon = UiUtil.toGuiSection(new RENDEROBJ.Sprite(boostable.icon));
        icon.pad(5);

        if (boosterEntry.getRange().getApplyMode().equals(Range.ApplyMode.PERCENT)) {
            rangePerc = boosterEntry.getRange();
            rangeAdd = ConfigDefaults.boosterAdd();
            activeKey = Range.ApplyMode.PERCENT;
        } else {
            rangePerc = ConfigDefaults.boosterPercent();
            rangeAdd = boosterEntry.getRange();
            activeKey = Range.ApplyMode.ADD;
        }

        Slider multiSlider = Slider.SliderBuilder.fromRange(rangePerc)
            .controls(true)
            .input(true)
            .lockScroll(true)
            .threshold((int) (0.10 * rangeAdd.getMax()), COLOR.YELLOW100.shade(0.7d))
            .threshold((int) (0.50 * rangeAdd.getMax()), COLOR.ORANGE100.shade(0.7d))
            .threshold((int) (0.75 * rangeAdd.getMax()), COLOR.RED100.shade(0.7d))
            .threshold((int) (0.90 * rangeAdd.getMax()), COLOR.RED2RED)
            .width(300)
            .build();
        Slider additiveSlider = Slider.SliderBuilder.fromRange(rangeAdd)
            .controls(true)
            .input(true)
            .lockScroll(true)
            .threshold((int) (0.10 * rangeAdd.getMax()), COLOR.YELLOW100.shade(0.7d))
            .threshold((int) (0.50 * rangeAdd.getMax()), COLOR.ORANGE100.shade(0.7d))
            .threshold((int) (0.75 * rangeAdd.getMax()), COLOR.RED100.shade(0.7d))
            .threshold((int) (0.90 * rangeAdd.getMax()), COLOR.RED2RED)
            .width(300)
            .build();

        // Booster toggle
        Tabulator<Range.ApplyMode, Slider, Integer> slidersWithToggle = Tabulator.<Range.ApplyMode, Slider, Integer>builder()
            .tabs(Maps.ofLinked(
                Range.ApplyMode.PERCENT, multiSlider,
                Range.ApplyMode.ADD, additiveSlider
            ))
            .tabMenu(Switcher.<Range.ApplyMode>builder()
                .menu(ButtonMenu.<Range.ApplyMode>builder()
                    .button(Range.ApplyMode.ADD, new Button(i18n.t("BoostersTab.booster.toggle.add.name"), i18n.t("BoostersTab.booster.toggle.add.desc")))
                    .button(Range.ApplyMode.PERCENT, new Button(i18n.t("BoostersTab.booster.toggle.perc.name"), i18n.t("BoostersTab.booster.toggle.perc.desc")))
                    .horizontal(true)
                    .sameWidth(true)
                    .build())
                .aktiveKey(activeKey)
                .highlight(true)
                .build())
            .resetOnToggle(true)
            .direction(DIR.W)
            .build();

        return ColumnRow.<Range>builder()
            .key(boosterEntry.getKey())
            .column(boosterLabel)
            .column(icon)
            .column(slidersWithToggle)
            .searchTerm(boostable.name.toString())
            .valueConsumer(range -> {
                slidersWithToggle.tab(range.getApplyMode());
                slidersWithToggle.setValue(range.getValue());
            })
            .valueSupplier(() -> Range.fromSlider(slidersWithToggle.getActiveTab()))
            .build();
    }

    @Override
    public Map<String, Range> getValue() {
        return boosterTable.getValue();
    }

    @Override
    public void setValue(Map<String, Range> boosterValues) {
        boosterTable.setValue(boosterValues);
    }
}
