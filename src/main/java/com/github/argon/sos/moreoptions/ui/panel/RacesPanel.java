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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 */
public class RacesPanel extends GuiSection implements Valuable<MoreOptionsV2Config.RacesConfig, RacesPanel> {
    private static final Logger log = Loggers.getLogger(RacesPanel.class);

    private final Map<String, Slider> likingsSliders = new HashMap<>();

    private final static String RACE_KEY_SEPARATOR = "~";

    public RacesPanel(Map<String, List<Entry>> raceEntries) {

        Map<String, List<ColumnRow>> rowMap = raceEntries.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
            mapEntry -> mapEntry.getValue().stream().map(entry -> {
                Race race = entry.getRace();
                Race otherRace = entry.getOtherRace();
                MoreOptionsV2Config.Range range = entry.getRange();

                GuiSection raceIcon = UiUtil.toGuiSection(race.appearance().icon);
                GuiSection otherRaceIcon = UiUtil.toGuiSection(otherRace.appearance().icon);
                BuildResult<Slider, Slider> likingsSliderResult = SliderBuilder.builder().definition(SliderBuilder.Definition
                    .fromRange(range)
                    .maxWidth(300)
                    .threshold(0, COLOR.RED100.shade(0.5d))
                    .threshold((int) (0.25 * range.getMax()), COLOR.YELLOW100.shade(0.5d))
                    .threshold((int) (0.50 * range.getMax()), COLOR.ORANGE100.shade(0.5d))
                    .threshold((int) (0.75 * range.getMax()), COLOR.GREEN100.shade(0.5d))
                    .build()
                ).build();

                Slider slider = likingsSliderResult.getResult();
                likingsSliders.put(key(race, otherRace), slider);
                List<GuiSection> columns = Lists.of(raceIcon, slider, otherRaceIcon);

                return ColumnRow.builder()
                    .columns(columns)
                    .searchTerm(term(race, otherRace))
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
        List<MoreOptionsV2Config.RacesConfig.Liking> likings = likingsSliders.entrySet().stream().map(entry -> {
            String[] split = entry.getKey().split(RACE_KEY_SEPARATOR);
            String race = split[0];
            String otherRace = split[1];

            return MoreOptionsV2Config.RacesConfig.Liking.builder()
                .race(race)
                .otherRace(otherRace)
                .range(MoreOptionsV2Config.Range.fromSlider(entry.getValue()))
                .build();
        }).collect(Collectors.toList());

        return MoreOptionsV2Config.RacesConfig.builder()
            .likings(likings)
            .build();
    }

    @Override
    public void setValue(MoreOptionsV2Config.RacesConfig config) {
        config.getLikings().forEach(liking -> {
            String key = key(liking);
            Slider slider = likingsSliders.get(key);

            if (slider != null) {
                slider.setValue(liking.getRange().getValue());
            }
        });
    }

    private String key(MoreOptionsV2Config.RacesConfig.Liking liking) {
        return liking.getRace() + RACE_KEY_SEPARATOR + liking.getOtherRace();
    }

    private String key(Race race, Race otherRace) {
        return race.key + RACE_KEY_SEPARATOR + otherRace.key;
    }

    private String term(Race race, Race otherRace) {
        return race.info.name + RACE_KEY_SEPARATOR + otherRace.info.name;
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
