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
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GInput;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 */
public class RacesPanel extends GuiSection implements Valuable<MoreOptionsV2Config.RacesConfig, RacesPanel> {
    private static final Logger log = Loggers.getLogger(RacesPanel.class);
    public RacesPanel(Map<String, List<Entry>> raceEntries) {

        Map<String, List<ColumnRow>> rowMap = raceEntries.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
            mapEntry -> mapEntry.getValue().stream().map(entry -> {
                Race race = entry.getRace();
                Race otherRace = entry.getOtherRace();
                MoreOptionsV2Config.Range range = entry.getRange();

                GuiSection raceIcon = UiUtil.toGuiSection(race.appearance().icon);
                GuiSection otherRaceIcon = UiUtil.toGuiSection(otherRace.appearance().icon);
                // todo register sliders
                BuildResult<Slider, Slider> likingsSliderResult = SliderBuilder.builder().definition(SliderBuilder.Definition
                    .fromRange(range)
                    .maxWidth(300)
                    .threshold(0, COLOR.RED100.shade(0.5d))
                    .threshold((int) (0.25 * range.getMax()), COLOR.YELLOW100.shade(0.5d))
                    .threshold((int) (0.50 * range.getMax()), COLOR.ORANGE100.shade(0.5d))
                    .threshold((int) (0.75 * range.getMax()), COLOR.GREEN100.shade(0.5d))
                    .build()
                ).build();

                Slider sliderResultResult = likingsSliderResult.getResult();
                List<GuiSection> columns = Lists.of(raceIcon, sliderResultResult, otherRaceIcon);

                return ColumnRow.builder()
                    .columns(columns)
                    .searchTerm(race.info.name + "~" + otherRace.info.name)
                    .highlight(true)
                    .columns(columns)
                    .build();
            }).collect(Collectors.toList())));

        StringInputSprite searchInput = new StringInputSprite(16, UI.FONT().M).placeHolder("Search");
        BuildResult<Table, Table> raceLikingsTable = TableBuilder.builder()
            .evenOdd(true)
            .scrollable(true)
            .search(searchInput)
            .rowPadding(3)
            .rowsCategorized(rowMap)
            .displayHeight(600)
            .build();

        addDownC(0, new GInput(searchInput));
        addDownC(10, raceLikingsTable.getResult());
    }

    @Override
    public MoreOptionsV2Config.RacesConfig getValue() {
        // todo
        return null;
    }

    @Override
    public void setValue(MoreOptionsV2Config.RacesConfig config) {
        // todo
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
