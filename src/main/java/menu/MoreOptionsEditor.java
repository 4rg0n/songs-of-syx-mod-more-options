package menu;

import com.github.argon.sos.moreoptions.game.GameResources;
import com.github.argon.sos.moreoptions.game.Wildcard;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.ButtonMenu;
import com.github.argon.sos.moreoptions.game.ui.Tabulator;
import com.github.argon.sos.moreoptions.game.ui.Toggle;
import init.C;
import init.paths.PATHS;
import lombok.Getter;
import menu.json.JsonUi;
import menu.json.tab.AbstractTab;
import menu.json.tab.MultiTab;
import menu.json.tab.SimpleTab;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MoreOptionsEditor extends GuiSection {

    @Getter
    private final Tabulator<String, AbstractTab, Void> tabulator;

    public MoreOptionsEditor() {
        int availableHeight = FullWindow.AVAILABLE_HEIGHT - 100;

        List<AbstractTab> tabs = new ArrayList<>();
        tabs.add(world(availableHeight));
        tabs.add(settlement(availableHeight));
        tabs.add(races(availableHeight));
        tabs.add(rooms(availableHeight));
        tabs.add(diseases(availableHeight));

        Map<String, AbstractTab> tabsMap = tabs.stream()
            .collect(Collectors.toMap(
                AbstractTab::getTitle,
                tab -> tab));
        Map<String, Button> buttonMap = tabs.stream()
            .collect(Collectors.toMap(
                AbstractTab::getTitle,
                tab -> new Button(tab.getTitle(), tab.getPath().toString())));

        tabulator = Tabulator.<String, AbstractTab, Void>builder()
            .tabs(tabsMap)
            .tabMenu(Toggle.<String>builder()
                .menu(ButtonMenu.<String>builder()
                    .maxHeight(FullWindow.TOP_HEIGHT)
                    .maxWidth(C.WIDTH() - 200)
                    .buttons(buttonMap)
                    .sameWidth(true)
                    .horizontal(true)
                    .margin(21)
                    .spacer(true)
                    .buttonColor(COLOR.WHITE25)
                    .build())
                .highlight(true)
                .aktiveKey("config")
                .build())
            .center(true)
            .build();

        addDownC(0, tabulator);
    }

    private static MultiTab<SimpleTab> races(int availableHeight) {
        return JsonUi.builder(PATHS.RACE().init)
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.checkbox("PLAYABLE");

                jsonUiTemplate.slider("PROPERTIES.HEIGHT", 0, 200);
                jsonUiTemplate.slider("PROPERTIES.WIDTH", 5, 15);
                jsonUiTemplate.slider("PROPERTIES.ADULT_AT_DAY", 0, 100);
                jsonUiTemplate.checkbox("PROPERTIES.CORPSE_DECAY");
                jsonUiTemplate.checkbox("PROPERTIES.SLEEPS");
                jsonUiTemplate.separator();

                jsonUiTemplate.sliderD("BEHAVIOUR.SKINNY_DIPS", 0, 100);
                jsonUiTemplate.sliderD("BEHAVIOUR.USES_BENCH", 0, 100);
                jsonUiTemplate.separator();

                jsonUiTemplate.selectS("PREFERRED.FOOD", GameResources.getEdibles());
                jsonUiTemplate.selectS("PREFERRED.DRINK", GameResources.getDrinkables());
                jsonUiTemplate.separator();

                Wildcard.from(GameResources.getStructures()).forEach(s -> {
                    jsonUiTemplate.sliderD("PREFERRED.STRUCTURE." + s, 0, 100);
                });
                jsonUiTemplate.separator();

                Wildcard.from(GameResources.getFloors()).forEach(s -> {
                    jsonUiTemplate.sliderD("PREFERRED.ROAD." + s, 0, 100);
                });
                jsonUiTemplate.separator();

                Wildcard.from(GameResources.getRooms()).forEach(s -> {
                    jsonUiTemplate.sliderD("PREFERRED.WORK." + s, 0, 100);
                });
                jsonUiTemplate.separator();

                Wildcard.from(GameResources.getRaces()).forEach(s -> {
                    jsonUiTemplate.sliderD("PREFERRED.OTHER_RACES." + s, 0, 100);
                });
                jsonUiTemplate.separator();

                Wildcard.from(GameResources.getRaces()).forEach(s -> {
                    jsonUiTemplate.sliderD("PREFERRED.OTHER_RACES_REVERSE." + s, 0, 100);
                });
                jsonUiTemplate.separator();

                Wildcard.from(GameResources.getTraits()).forEach(s -> {
                    jsonUiTemplate.sliderD("PREFERRED.TRAITS." + s, 0, 100);
                });
                jsonUiTemplate.separator();

                Wildcard.from(GameResources.getResources()).forEach(s -> {
                    jsonUiTemplate.slider("RESOURCE." + s, 0, 100);
                });
                jsonUiTemplate.separator();

                Wildcard.from(GameResources.getResources()).forEach(s -> {
                    jsonUiTemplate.slider("RESOURCE_GROOMING." + s, 0, 100);
                });
                jsonUiTemplate.separator();
            }).build().folder(availableHeight);
    }

    private static MultiTab<SimpleTab> world(int availableHeight) {
        return JsonUi.builder(PATHS.INIT_WORLD().getFolder("config"))
            .template("General", jsonUiTemplate -> {
                jsonUiTemplate.slider("POPULATION_MAX_CAPITOL", 101, 256000);
                jsonUiTemplate.slider("REGION_TRIBUTE_AMOUNT", 0, 100);
                jsonUiTemplate.slider("TRADE_COST_PER_TILE", 0, 1000);
                jsonUiTemplate.sliderD("FOREST_AMOUNT", 0, 100);
                jsonUiTemplate.slider("POPULATION_LOYALTY_PENALTY", 0, 10000);
            }).build().folder(availableHeight);
    }

    private static MultiTab<SimpleTab> diseases(int availableHeight) {
        return JsonUi.builder(PATHS.INIT().getFolder("disease"))
            .template("_CONFIG", jsonUiTemplate -> {
                jsonUiTemplate.sliderD("DISEASE_PER_YEAR", 0, 1000);
                jsonUiTemplate.sliderD("EPIDEMIC_CHANCE", 0, 1000);
                jsonUiTemplate.slider("SQUALOR_POPULATION_MIN", 0, 8000);
                jsonUiTemplate.slider("SQUALOR_POPULATION_DELTA", 0, 8000);
            })
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.checkbox("EPIDEMIC");
                jsonUiTemplate.checkbox("REGULAR");
                jsonUiTemplate.sliderD("FATALITY_RATE", 0, 100);
                jsonUiTemplate.sliderD("SPREAD", 0, 100);
                jsonUiTemplate.slider("INFECTION_DAYS", 0, 100);
                Wildcard.from(GameResources.getClimates()).forEach(s -> {
                    jsonUiTemplate.sliderD("OCCURRENCE_CLIMATE." + s, 0, 100);
                });
            }).build().folder(availableHeight);
    }

    private static MultiTab<SimpleTab> rooms(int availableHeight) {
        return JsonUi.builder(PATHS.INIT().getFolder("room"))
            .templates( "WORKSHOP_", jsonUiTemplate -> {
                jsonUiTemplate.color("MINI_COLOR");
                jsonUiTemplate.selectS("RESOURCES", GameResources.getResources());
                jsonUiTemplate.dropDown("FLOOR", GameResources.getFloors());
                jsonUiTemplate.sliderD("WORK.SHIFT_OFFSET", 0, 100);
                jsonUiTemplate.sliderD("WORK.FULFILLMENT", 0, 100);
                jsonUiTemplate.sliderD("WORK.ACCIDENTS_PER_YEAR", 0, 100, 4);

                GameResources.getResources().forEach(s -> {
                    jsonUiTemplate.sliderD("INDUSTRY.IN." + s, 0, 1000);
                });

                GameResources.getResources().forEach(s -> {
                    jsonUiTemplate.sliderD("INDUSTRY.OUT." + s, 0, 1000);
                });

                jsonUiTemplate.sliderD("ENVIRONMENT_EMI._NOISE.VALUE", 0, 100);
                jsonUiTemplate.sliderD("ENVIRONMENT_EMI._NOISE.RADIUS", 0, 100);
            }).build().folder(availableHeight);
    }

    private static MultiTab<SimpleTab> settlement(int availableHeight) {
        return JsonUi.builder(PATHS.CONFIG())
            .template("Sett", jsonUiTemplate -> {
                jsonUiTemplate.sliderD("HAPPINESS_EXPONENT", 0, 100);
                jsonUiTemplate.slider("TOURIST_PER_YEAR_MAX", 0, 4096, 32);
                jsonUiTemplate.slider("TOURIST_CRETIDS", 0, 10000);
                jsonUiTemplate.slider("DIMENSION", 0, 2048, 32);
            })
            .template("Battle", jsonUiTemplate -> {
                jsonUiTemplate.sliderD("DAMAGE", 0, 1000000);
                jsonUiTemplate.sliderD("BLOCK_CHANCE", 0, 1000000);
                jsonUiTemplate.slider("TRAINING_DEGRADE", 0, 50);
                jsonUiTemplate.slider("MEN_PER_DIVISION", 1, 255);
                jsonUiTemplate.slider("DIVISIONS_PER_ARMY", 1, 126);
                jsonUiTemplate.slider("REGION_MAX_DIV", 0, 127);
            })
            .template("LandingParty", jsonUiTemplate -> {
                jsonUiTemplate.slider("RESOURCES.STONE", 0, 10000);
                jsonUiTemplate.slider("RESOURCES.WOOD", 0, 10000);
                jsonUiTemplate.slider("RESOURCES.RATION", 0, 1000);
                jsonUiTemplate.slider("RESOURCES.LIVESTOCK", 0, 10000);
            })
            .template("LEAVE_CAUSE", jsonUiTemplate -> {
                jsonUiTemplate.sliderD("EMMIGRATED", 0, 100);
                jsonUiTemplate.sliderD("STARVED", 0, 100);
                jsonUiTemplate.sliderD("SLAYED", 0, 100);
                jsonUiTemplate.sliderD("ANIMAL", 0, 100);
                jsonUiTemplate.sliderD("AGE", 0, 100);
                jsonUiTemplate.sliderD("HEAT", 0, 100);
                jsonUiTemplate.sliderD("COLD", 0, 100);
                jsonUiTemplate.sliderD("MURDER", 0, 100);
                jsonUiTemplate.sliderD("EXECUTED", 0, 100);
                jsonUiTemplate.sliderD("DESERTED", 0, 100);
                jsonUiTemplate.sliderD("DROWNED", 0, 100);
                jsonUiTemplate.sliderD("ARMY", 0, 100);
                jsonUiTemplate.sliderD("OTHER", 0, 100);
                jsonUiTemplate.sliderD("PUNISHED", 0, 100);
                jsonUiTemplate.sliderD("EXILED", 0, 100);
                jsonUiTemplate.sliderD("SACRIFICED", 0, 100);
                jsonUiTemplate.sliderD("DISEASE", 0, 100);
                jsonUiTemplate.sliderD("OTHER", 0, 100);
                jsonUiTemplate.sliderD("ACCIDENT", 0, 100);
                jsonUiTemplate.sliderD("BRAWL", 0, 10);
            }).build().folder(availableHeight);
    }

}
