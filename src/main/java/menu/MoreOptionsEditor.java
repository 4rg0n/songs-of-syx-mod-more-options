package menu;

import com.github.argon.sos.moreoptions.game.Wildcard;
import com.github.argon.sos.moreoptions.game.data.GameResources;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.ButtonMenu;
import com.github.argon.sos.moreoptions.game.ui.Tabulator;
import com.github.argon.sos.moreoptions.game.ui.Switcher;
import init.C;
import init.paths.PATHS;
import lombok.Getter;
import menu.json.JsonUi;
import menu.json.tab.AbstractTab;
import menu.json.JsonUITemplates;
import menu.json.tab.MultiTab;
import menu.json.tab.SimpleTab;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;

import java.util.*;
import java.util.stream.Collectors;

public class MoreOptionsEditor extends GuiSection {

    @Getter
    private final Tabulator<String, AbstractTab, Void> tabulator;
    public MoreOptionsEditor() {
        int availableHeight = FullWindow.AVAILABLE_HEIGHT - 100;
        
        List<AbstractTab> tabs = new ArrayList<>();
        tabs.add(world(availableHeight));
        tabs.add(structures(availableHeight));
        tabs.add(fortifications(availableHeight));
        tabs.add(floors(availableHeight));
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
            .tabs(new TreeMap<>(tabsMap))
            .tabMenu(Switcher.<String>builder()
                .menu(ButtonMenu.<String>builder()
                    .maxHeight(FullWindow.TOP_HEIGHT)
                    .maxWidth(C.WIDTH() - 50)
                    .buttons(new TreeMap<>(buttonMap))
                    .sameWidth(true)
                    .horizontal(true)
                    .margin(3)
                    .buttonColor(COLOR.WHITE25)
                    .build())
                .highlight(true)
                .aktiveKey("config")
                .build())
            .center(true)
            .build();

        addDownC(0, tabulator);
    }

    private AbstractTab environments(int availableHeight) {
        return JsonUi.builder(PATHS.SETT().init.getFolder("environment"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.sliderD("DECLINE_VALUE", 0, 100);
                JsonUITemplates.standing(jsonUiTemplate);
            }).build().folder(availableHeight);
    }

    private AbstractTab floors(int availableHeight) {
        return JsonUi.builder(PATHS.SETT().init.getFolder("floor"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.header("GAME_TEXTURE");
                jsonUiTemplate.dropDown("GAME_TEXTURE.FILE", GameResources.sprite().textures().fileTitles());
                jsonUiTemplate.slider("GAME_TEXTURE.ROW", 0, 7);

                jsonUiTemplate.header("ROAD");
                jsonUiTemplate.dropDown("ROAD.RESOURCE", GameResources.getResources());
                jsonUiTemplate.slider("ROAD.RESOURCE_AMOUNT", 0, 1000);
                jsonUiTemplate.slider("ROAD.SPEED", 0, 4);
                jsonUiTemplate.sliderD("ROAD.DURABILITY", 0, 100);

                jsonUiTemplate.header("ROAD.ENVIRONMENT");
                Wildcard.from(GameResources.getEnvironments()).forEach(s -> {
                    jsonUiTemplate.sliderD("ROAD.ENVIRONMENT." + s, 0, 100);
                });

                jsonUiTemplate.header("ROAD.PREFERENCE");
                Wildcard.from(GameResources.init().race().fileTitles()).forEach(s -> {
                    jsonUiTemplate.sliderD("ROAD.PREFERENCE." + s, 0, 100);
                });
            }).build().folder(availableHeight);
    }

    private AbstractTab fortifications(int availableHeight) {
        return JsonUi.builder(PATHS.SETT().init.getFolder("fortification"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.dropDown("SPRITE", GameResources.sprite().settlement().fortification().fileTitles());
                jsonUiTemplate.color("COLOR");
                jsonUiTemplate.color("MINIMAP_COLOR");
                jsonUiTemplate.text("BUILD_SOUND");
                jsonUiTemplate.dropDown("RESOURCE", GameResources.getResources());
                jsonUiTemplate.slider("RESOURCE_AMOUNT", 1, 1000);
                jsonUiTemplate.slider("HEIGHT", 1, 20);
                jsonUiTemplate.sliderD("BUILD_TIME", 0, 100);
                jsonUiTemplate.sliderD("DURABILITY", 0, 100);
            }).build().folder(availableHeight);
    }

    private AbstractTab structures(int availableHeight) {
        return JsonUi.builder(PATHS.SETT().init.getFolder("structure"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.dropDown("SPRITE", GameResources.sprite().settlement().structure().fileTitles());
                jsonUiTemplate.color("COLOR");
                jsonUiTemplate.color("MINIMAP_COLOR");
                jsonUiTemplate.text("BUILD_SOUND");
                jsonUiTemplate.dropDown("RESOURCE", GameResources.getResources());
                jsonUiTemplate.slider("RESOURCE_AMOUNT", 1, 1000);
                jsonUiTemplate.sliderD("BUILD_TIME", 0, 100);
                jsonUiTemplate.sliderD("DURABILITY", 0, 100);

                jsonUiTemplate.header("PREFERENCE");
                Wildcard.from(GameResources.getRaces()).forEach(s -> {
                    jsonUiTemplate.sliderD("PREFERENCE." + s, 0, 100);
                });
            }).build().folder(availableHeight);
    }

    private static MultiTab<SimpleTab> races(int availableHeight) {
        return JsonUi.builder(PATHS.RACE().init)
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.checkbox("PLAYABLE");
                jsonUiTemplate.dropDown("SPRITE_FILE", GameResources.getRaces());
                jsonUiTemplate.text("ICON_SMALL");
                jsonUiTemplate.text("ICON_BIG");

                jsonUiTemplate.header("PROPERTIES");
                jsonUiTemplate.slider("PROPERTIES.HEIGHT", 0, 200);
                jsonUiTemplate.slider("PROPERTIES.WIDTH", 5, 15);
                jsonUiTemplate.slider("PROPERTIES.ADULT_AT_DAY", 0, 100);
                jsonUiTemplate.checkbox("PROPERTIES.CORPSE_DECAY");
                jsonUiTemplate.checkbox("PROPERTIES.SLEEPS");

                jsonUiTemplate.header("BEHAVIOUR");
                jsonUiTemplate.sliderD("BEHAVIOUR.SKINNY_DIPS", 0, 100);
                jsonUiTemplate.sliderD("BEHAVIOUR.USES_BENCH", 0, 100);

                jsonUiTemplate.header("PREFERRED");
                jsonUiTemplate.multiDropDown("PREFERRED.FOOD", GameResources.getEdibles());
                jsonUiTemplate.multiDropDown("PREFERRED.DRINK", GameResources.getDrinkables());

                jsonUiTemplate.header("PREFERRED.STRUCTURE");
                Wildcard.from(GameResources.getStructures()).forEach(s -> {
                    jsonUiTemplate.sliderD("PREFERRED.STRUCTURE." + s, 0, 100);
                });
                jsonUiTemplate.header("PREFERRED.ROAD");
                Wildcard.from(GameResources.getFloors()).forEach(s -> {
                    jsonUiTemplate.sliderD("PREFERRED.ROAD." + s, 0, 100);
                });
                jsonUiTemplate.header("PREFERRED.WORK");
                Wildcard.from(GameResources.getRooms()).forEach(s -> {
                    jsonUiTemplate.sliderD("PREFERRED.WORK." + s, 0, 100);
                });
                jsonUiTemplate.header("PREFERRED.OTHER_RACES");
                Wildcard.from(GameResources.getRaces()).forEach(s -> {
                    jsonUiTemplate.sliderD("PREFERRED.OTHER_RACES." + s, 0, 100);
                });
                jsonUiTemplate.header("PREFERRED.OTHER_RACES_REVERSE");
                Wildcard.from(GameResources.getRaces()).forEach(s -> {
                    jsonUiTemplate.sliderD("PREFERRED.OTHER_RACES_REVERSE." + s, 0, 100);
                });
                jsonUiTemplate.header("PREFERRED.TRAITS");
                Wildcard.from(GameResources.init().race().trait().fileTitles()).forEach(s -> {
                    jsonUiTemplate.sliderD("PREFERRED.TRAITS." + s, 0, 100);
                });

                jsonUiTemplate.header("MILITARY_EQUIPMENT_EFFICIENCY");
                Wildcard.from(GameResources.init().stats().folder("equip").fileTitles()).forEach(s -> {
                    jsonUiTemplate.slider("MILITARY_EQUIPMENT_EFFICIENCY." + s, 0, 100);
                });
                jsonUiTemplate.header("MILITARY_SUPPLY_USE");
                Wildcard.from(GameResources.init().resource().armySupply().fileTitles()).forEach(s -> {
                    jsonUiTemplate.slider("MILITARY_SUPPLY_USE." + s, 0, 100);
                });

                jsonUiTemplate.header("RESOURCE");
                Wildcard.from(GameResources.getResources()).forEach(s -> {
                    jsonUiTemplate.slider("RESOURCE." + s, 0, 100);
                });

                jsonUiTemplate.header("RESOURCE_GROOMING");
                Wildcard.from(GameResources.getResources()).forEach(s -> {
                    jsonUiTemplate.slider("RESOURCE_GROOMING." + s, 0, 100);
                });

                JsonUITemplates.stats(jsonUiTemplate);
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
                jsonUiTemplate.sliderD("SPREAD", 0, 100, 3);
                jsonUiTemplate.slider("INFECTION_DAYS", 0, 100);
                Wildcard.from(GameResources.getClimates()).forEach(s -> {
                    jsonUiTemplate.sliderD("OCCURRENCE_CLIMATE." + s, 0, 100);
                });
            }).build().folder(availableHeight);
    }

    private static MultiTab<SimpleTab> rooms(int availableHeight) {
        return JsonUi.builder(PATHS.INIT().getFolder("room"))
            .templates( "WORKSHOP_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "WELL_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "UNIVERSITY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.sliderD("LEARNING_SPEED", 0, 100);
                jsonUiTemplate.separator();

                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "TOMB_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.dropDown("MONUMENT", GameResources.getMonuments());
                jsonUiTemplate.separator();

                JsonUITemplates.standingWithDefault(jsonUiTemplate);
                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "TEMPLE_", jsonUiTemplate -> {
                JsonUITemplates.roomHead(jsonUiTemplate);
                jsonUiTemplate.dropDown("SACRIFICE_TYPE", GameResources.getRaces());
                jsonUiTemplate.sliderD("SACRIFICE_TIME", 0, 100);
                jsonUiTemplate.dropDown("RELIGION", GameResources.getReligions());
                jsonUiTemplate.dropDown("FLOOR", GameResources.getFloors());
                jsonUiTemplate.dropDown("FLOOR_PATH", GameResources.getFloors());
                jsonUiTemplate.separator();

                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "TAVERN_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "STAGE_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "SPEAKER_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "SHRINE_", jsonUiTemplate -> {
                JsonUITemplates.roomHead(jsonUiTemplate);
                jsonUiTemplate.dropDown("RELIGION", GameResources.getReligions());
                jsonUiTemplate.multiDropDown("FLOOR", GameResources.getFloors());
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "RESTHOME_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "REFINER_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "POOL_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconS(jsonUiTemplate);
                jsonUiTemplate.color("WATER_COLOR");
                jsonUiTemplate.sliderD("WATER_DEPTH", 0, 100);
                jsonUiTemplate.checkbox("CLEARS_GRASS");
                jsonUiTemplate.separator();

                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "PLEASURE_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.dropDown("FLOOR2", GameResources.getFloors());
                jsonUiTemplate.color("COLOR_PIXEL_BAS");
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "PHYSICIAN_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "PASTURE_", jsonUiTemplate -> {
                JsonUITemplates.roomHead(jsonUiTemplate);
                jsonUiTemplate.dropDown("FLOOR", GameResources.getFloors());
                jsonUiTemplate.dropDown("FENCE", GameResources.getFences());
                jsonUiTemplate.dropDown("ANIMAL", GameResources.getAnimals());
                jsonUiTemplate.separator();

                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "ORCHARD_", jsonUiTemplate -> {
                JsonUITemplates.roomHead(jsonUiTemplate);
                jsonUiTemplate.dropDown("FLOOR", GameResources.getFloors());
                jsonUiTemplate.checkbox("INDOORS");
                jsonUiTemplate.dropDown("EXTRA_RESOURCE", GameResources.getResources());
                jsonUiTemplate.slider("EXTRA_AMOUNT", 0, 256);
                jsonUiTemplate.slider("DAYS_TILL_GROWTH", 0, 256);
                jsonUiTemplate.sliderD("RIPE_AT_PART_OF_YEAR", 0, 100);
                jsonUiTemplate.separator();

                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "NURSERY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.dropDown("RACE", GameResources.getRaces());
                jsonUiTemplate.slider("INCUBATION_DAYS", 0, 256);
                jsonUiTemplate.separator();

                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "MONUMENT_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.checkbox("SOLID");
                jsonUiTemplate.separator();

                JsonUITemplates.standingWithDefault(jsonUiTemplate);
                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "MINE_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.dropDown("MINABLE", GameResources.getMinables());
                jsonUiTemplate.text("SPRITE");
                jsonUiTemplate.slider("YEILD_WORKER_DAILY", 0, 1000);
                jsonUiTemplate.slider("DEGRADE_RATE", 0, 100);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.standingWithDefault(jsonUiTemplate);
                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "MARKET_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "LIBRARY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                JsonUITemplates.admin(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates("LAVATORY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "LABORATORY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                JsonUITemplates.admin(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "HUNTER_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.slider("MAX_EMPLOYED", 1, 10000);
                jsonUiTemplate.separator();

                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "GRAVEYARD_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.standingWithDefault(jsonUiTemplate);
                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "GATEHOUSE_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
            })
            .templates( "FISHERY_", jsonUiTemplate -> {
                JsonUITemplates.roomHead(jsonUiTemplate);
                jsonUiTemplate.slider("DEGRADE_RATE", 0, 100);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "FARM_", jsonUiTemplate -> {
                JsonUITemplates.roomHead(jsonUiTemplate);
                jsonUiTemplate.dropDown("GROWABLE", GameResources.getGrowables());
                jsonUiTemplate.checkbox("INDOORS");
                jsonUiTemplate.separator();

                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "EATERY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "CANTEEN_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.industryServiceRoom(jsonUiTemplate);
            })
            .templates( "BATH_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.color("WATER_COLOR");
                jsonUiTemplate.sliderD("WATER_OPACITY", 0, 100);
                jsonUiTemplate.separator();

                JsonUITemplates.industryServiceRoom(jsonUiTemplate);
            })
            .templates( "BARRACKS_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.sliderD("RACE_PREFERENCE", 0, 100);
                jsonUiTemplate.slider("FULL_TRAINING_IN_DAYS", 0, 1000);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "BARBER_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.sliderD("WORK_TIME_IN_DAYS", 0, 100);
                jsonUiTemplate.separator();

                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "ARTILLERY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.text("SOUND_WORK");
                jsonUiTemplate.separator();

                JsonUITemplates.projectile(jsonUiTemplate);
            })
            .templates( "ARENAG_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "ARENA_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "ARCHERY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.slider("TRAINING.FULL_TRAINING_IN_DAYS", 0, 1000);
                jsonUiTemplate.separator();

                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "ADMIN_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                JsonUITemplates.admin(jsonUiTemplate);
                jsonUiTemplate.separator();

                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .build().folder(availableHeight);
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
