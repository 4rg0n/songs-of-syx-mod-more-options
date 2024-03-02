package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.game.ui.Valuable;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.element.SliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.TableBuilder;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.UiUtil;
import init.race.Race;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import snake2d.util.gui.GuiSection;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 */
public class RacesPanel extends GuiSection implements Valuable<MoreOptionsV2Config.Races, RacesPanel> {
    private static final Logger log = Loggers.getLogger(RacesPanel.class);
    public RacesPanel(List<Entry> raceEntries) {

        // todo sort?
        List<ColumnRow> rows = raceEntries.stream().map(entry -> {
            Race race = entry.getRace();
            Race otherRace = entry.getOtherRace();

            GuiSection raceIcon = UiUtil.toGuiSection(race.appearance().icon);
            GuiSection otherRaceIcon = UiUtil.toGuiSection(otherRace.appearance().icon);
            // todo register sliders
            BuildResult<Slider, Slider> likingsSliderResult = SliderBuilder.builder().definition(SliderBuilder.Definition
                .fromRange(entry.getRange())
                .maxWidth(300)
                .build()
            ).build();

            List<GuiSection> columns = Lists.of(raceIcon, likingsSliderResult.getResult(), otherRaceIcon);

            return ColumnRow.builder()
                .columns(columns)
                .searchTerm(race.info.name + "~" + otherRace.info.name)
                .highlight(true)
                .columns(columns)
                .build();
        }).collect(Collectors.toList());

        BuildResult<Table, Table> raceLikingsTable = TableBuilder.builder()
            .evenOdd(true)
            .scrollable(true)
            .columnRows(rows)
            .displayHeight(600)
            .build();

        addDownC(0, raceLikingsTable.getResult());
    }

    @Override
    public MoreOptionsV2Config.Races getValue() {
        return null;
    }

    @Override
    public void setValue(MoreOptionsV2Config.Races config) {

    }

    @Data
    @Builder
    @EqualsAndHashCode
    public static class Entry {
        private String raceKey;
        private String otherRaceKey;
        private Race race;
        private Race otherRace;
        private MoreOptionsV2Config.Range range;
    }
}
