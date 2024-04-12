package menu;

import com.github.argon.sos.moreoptions.game.Wildcard;
import com.github.argon.sos.moreoptions.game.data.GameResources;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.ButtonMenu;
import com.github.argon.sos.moreoptions.game.ui.Switcher;
import com.github.argon.sos.moreoptions.game.ui.Tabulator;
import com.github.argon.sos.moreoptions.util.Maps;
import init.C;
import init.paths.PATHS;
import lombok.Getter;
import menu.json.JsonUIBlueprints;
import menu.json.JsonUi;
import menu.json.tab.AbstractTab;
import menu.json.tab.FilesTab;
import menu.json.tab.SimpleTab;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;

public class MoreOptionsEditor extends GuiSection {

    @Getter
    private final Tabulator<String, AbstractTab, Void> tabulator;
    public MoreOptionsEditor(@Nullable StringInputSprite searchInput) {
        int availableHeight = FullWindow.AVAILABLE_HEIGHT;

        FilesTab<SimpleTab> filesTab = JsonUi.builder(PATHS.INIT())
            .templates(settlement())
            .templates(environments())
            .templates(fences())
            .templates(floors())
            .templates(fortifications())
            .templates(structures())
            .templates(world())
            .templates(races())
            .templates(rooms())
            .templates(diseases())
            .build()
            .filesTab(availableHeight);

        tabulator = Tabulator.<String, AbstractTab, Void>builder()
            .tabs(Maps.of(
                "editor", filesTab
            ))
            .tabMenu(Switcher.<String>builder()
                .menu(ButtonMenu.<String>builder()
                    .search(searchInput)
                    .maxHeight(FullWindow.TOP_HEIGHT - 10)
                    .maxWidth(C.WIDTH() - 50)
                    .buttons(Maps.of(
                        "editor", new Button("Config Editor")
                    ))
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

    private JsonUi animals() {
        return JsonUi.builder(PATHS.SETT().init.getFolder("fence"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.dropDown("RESOURCE", GameResources.getResources());
                jsonUiTemplate.slider("RESOURCE_AMOUNT", 0, 1000);
                jsonUiTemplate.slider("HEIGHT", 0, 10);
                jsonUiTemplate.slider("SPEED", 0, 10);
                jsonUiTemplate.slider("MASS", 0, 10);
                jsonUiTemplate.sliderD("DANGER", 0, 100);
                jsonUiTemplate.color("COLOR");
                jsonUiTemplate.text("ICON");
                jsonUiTemplate.dropDown("SPRITE", GameResources.sprite().animal().fileTitles());
                jsonUiTemplate.sliderD("LIVES_IN_CAVES", 0, 100);
                jsonUiTemplate.checkbox("CARAVAN");

                

                JsonUIBlueprints.standing(jsonUiTemplate);
            }).build();
    }

    private JsonUi fences() {
        return JsonUi.builder(PATHS.SETT().init.getFolder("fence"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.dropDown("RESOURCE", GameResources.getResources());
                jsonUiTemplate.slider("RESOURCE_AMOUNT", 0, 1000);
                jsonUiTemplate.color("MINIMAP_COLOR");
                jsonUiTemplate.text("BUILD_SOUND");
                JsonUIBlueprints.standing(jsonUiTemplate);
            }).build();
    }

    private JsonUi environments() {
        return JsonUi.builder(PATHS.SETT().init.getFolder("environment"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.sliderD("DECLINE_VALUE", 0, 100);
                JsonUIBlueprints.standing(jsonUiTemplate);
            }).build();
    }

    private JsonUi floors() {
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
            }).build();
    }

    private JsonUi fortifications() {
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
            }).build();
    }

    private JsonUi structures() {
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
            }).build();
    }

    private static JsonUi races() {
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
                jsonUiTemplate.slidersD("PREFERRED.STRUCTURE",
                    GameResources.getStructures(), true, 0, 100);

                jsonUiTemplate.header("PREFERRED.ROAD");
                jsonUiTemplate.slidersD("PREFERRED.ROAD",
                    GameResources.getFloors(), true, 0, 100);

                jsonUiTemplate.header("PREFERRED.WORK");
                jsonUiTemplate.slidersD("PREFERRED.WORK",
                    GameResources.getRooms(), true, 0, 100);

                jsonUiTemplate.header("PREFERRED.OTHER_RACES");
                jsonUiTemplate.slidersD("PREFERRED.OTHER_RACES",
                    GameResources.getRaces(), true, 0, 100);

                jsonUiTemplate.header("PREFERRED.OTHER_RACES_REVERSE");
                jsonUiTemplate.slidersD("PREFERRED.OTHER_RACES_REVERSE",
                    GameResources.getRaces(), true, 0, 100);

                jsonUiTemplate.header("PREFERRED.TRAITS");
                jsonUiTemplate.slidersD("PREFERRED.TRAITS",
                    GameResources.init().race().trait().fileTitles(), true, 0, 100);

                jsonUiTemplate.header("MILITARY_EQUIPMENT_EFFICIENCY");
                jsonUiTemplate.sliders("MILITARY_EQUIPMENT_EFFICIENCY",
                    GameResources.init().stats().folder("equip").fileTitles(), true, 0, 100);

                jsonUiTemplate.header("MILITARY_SUPPLY_USE");
                jsonUiTemplate.sliders("MILITARY_SUPPLY_USE",
                    GameResources.init().resource().armySupply().fileTitles(), true, 0, 100);

                jsonUiTemplate.header("RESOURCE");
                jsonUiTemplate.sliders("RESOURCE",
                    GameResources.getResources(), true, 0, 100);

                jsonUiTemplate.header("RESOURCE_GROOMING");
                jsonUiTemplate.sliders("RESOURCE_GROOMING",
                    GameResources.getResources(), true, 0, 100);

                JsonUIBlueprints.stats(jsonUiTemplate);
            }).build();
    }

    private static JsonUi world() {
        return JsonUi.builder(PATHS.INIT_WORLD().getFolder("config"))
            .template("General", jsonUiTemplate -> {
                jsonUiTemplate.slider("POPULATION_MAX_CAPITOL", 101, 256000);
                jsonUiTemplate.slider("REGION_TRIBUTE_AMOUNT", 0, 100);
                jsonUiTemplate.slider("TRADE_COST_PER_TILE", 0, 1000);
                jsonUiTemplate.sliderD("FOREST_AMOUNT", 0, 100);
                jsonUiTemplate.slider("POPULATION_LOYALTY_PENALTY", 0, 10000);
            }).build();
    }

    private static JsonUi diseases() {
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
            }).build();
    }

    private static JsonUi rooms() {
        return JsonUi.builder(PATHS.INIT().getFolder("room"))
            .templates( "WORKSHOP_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 2);
                JsonUIBlueprints.industryRoom(jsonUiTemplate);
            })
            .templates( "WELL_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 3);
                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "UNIVERSITY_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.sliderD("LEARNING_SPEED", 0, 100);
                jsonUiTemplate.space();

                JsonUIBlueprints.room(jsonUiTemplate);
            })
            .templates( "TOMB_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.dropDown("MONUMENT", GameResources.getMonuments());
                jsonUiTemplate.space();

                JsonUIBlueprints.standingWithDefault(jsonUiTemplate);
                JsonUIBlueprints.room(jsonUiTemplate);
            })
            .templates( "TEMPLE_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHead(jsonUiTemplate);
                jsonUiTemplate.dropDown("SACRIFICE_TYPE", GameResources.getRaces());
                jsonUiTemplate.sliderD("SACRIFICE_TIME", 0, 100);
                jsonUiTemplate.dropDown("RELIGION", GameResources.getReligions());
                jsonUiTemplate.dropDown("FLOOR", GameResources.getFloors());
                jsonUiTemplate.dropDown("FLOOR_PATH", GameResources.getFloors());
                jsonUiTemplate.space();

                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "TAVERN_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 2);
                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "STAGE_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 2);
                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "SPEAKER_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "SHRINE_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHead(jsonUiTemplate);
                jsonUiTemplate.dropDown("RELIGION", GameResources.getReligions());
                jsonUiTemplate.multiDropDown("FLOOR", GameResources.getFloors());
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 3);
                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "RESTHOME_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.room(jsonUiTemplate);
            })
            .templates( "REFINER_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 3);
                JsonUIBlueprints.industryRoom(jsonUiTemplate);
            })
            .templates( "POOL_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconS(jsonUiTemplate);
                jsonUiTemplate.color("WATER_COLOR");
                jsonUiTemplate.sliderD("WATER_DEPTH", 0, 100);
                jsonUiTemplate.checkbox("CLEARS_GRASS");
                jsonUiTemplate.space();

                JsonUIBlueprints.room(jsonUiTemplate);
            })
            .templates( "PLEASURE_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.dropDown("FLOOR2", GameResources.getFloors());
                jsonUiTemplate.color("COLOR_PIXEL_BAS");
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 3);
                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "PHYSICIAN_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "PASTURE_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHead(jsonUiTemplate);
                jsonUiTemplate.dropDown("FLOOR", GameResources.getFloors());
                jsonUiTemplate.dropDown("FENCE", GameResources.getFences());
                jsonUiTemplate.dropDown("ANIMAL", GameResources.getAnimals());
                jsonUiTemplate.space();

                JsonUIBlueprints.industryRoom(jsonUiTemplate);
            })
            .templates( "ORCHARD_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHead(jsonUiTemplate);
                jsonUiTemplate.dropDown("FLOOR", GameResources.getFloors());
                jsonUiTemplate.checkbox("INDOORS");
                jsonUiTemplate.dropDown("EXTRA_RESOURCE", GameResources.getResources());
                jsonUiTemplate.slider("EXTRA_AMOUNT", 0, 256);
                jsonUiTemplate.slider("DAYS_TILL_GROWTH", 0, 256);
                jsonUiTemplate.sliderD("RIPE_AT_PART_OF_YEAR", 0, 100);
                jsonUiTemplate.space();

                JsonUIBlueprints.industryRoom(jsonUiTemplate);
            })
            .templates( "NURSERY_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.dropDown("RACE", GameResources.getRaces());
                jsonUiTemplate.slider("INCUBATION_DAYS", 0, 256);
                jsonUiTemplate.space();

                JsonUIBlueprints.industryRoom(jsonUiTemplate);
            })
            .templates( "MONUMENT_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.checkbox("SOLID");
                jsonUiTemplate.space();

                JsonUIBlueprints.standingWithDefault(jsonUiTemplate);
                JsonUIBlueprints.room(jsonUiTemplate);
            })
            .templates( "MINE_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.dropDown("MINABLE", GameResources.getMinables());
                jsonUiTemplate.text("SPRITE");
                jsonUiTemplate.slider("YEILD_WORKER_DAILY", 0, 1000);
                jsonUiTemplate.slider("DEGRADE_RATE", 0, 100);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 3);
                JsonUIBlueprints.standingWithDefault(jsonUiTemplate);
                JsonUIBlueprints.room(jsonUiTemplate);
            })
            .templates( "MARKET_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 2);
                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "LIBRARY_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloors(jsonUiTemplate);
                JsonUIBlueprints.admin(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 2);
                JsonUIBlueprints.industryRoom(jsonUiTemplate);
            })
            .templates("LAVATORY_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 2);
                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "LABORATORY_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloors(jsonUiTemplate);
                JsonUIBlueprints.admin(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 3);
                JsonUIBlueprints.industryRoom(jsonUiTemplate);
            })
            .templates( "HUNTER_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.slider("MAX_EMPLOYED", 1, 10000);
                jsonUiTemplate.space();

                JsonUIBlueprints.industryRoom(jsonUiTemplate);
            })
            .templates( "GRAVEYARD_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.standingWithDefault(jsonUiTemplate);
                JsonUIBlueprints.room(jsonUiTemplate);
            })
            .templates( "GATEHOUSE_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
            })
            .templates( "FISHERY_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHead(jsonUiTemplate);
                jsonUiTemplate.slider("DEGRADE_RATE", 0, 100);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 2);
                JsonUIBlueprints.industryRoom(jsonUiTemplate);
            })
            .templates( "FARM_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHead(jsonUiTemplate);
                jsonUiTemplate.dropDown("GROWABLE", GameResources.getGrowables());
                jsonUiTemplate.checkbox("INDOORS");
                jsonUiTemplate.space();

                JsonUIBlueprints.industryRoom(jsonUiTemplate);
            })
            .templates( "EATERY_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 2);
                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "CANTEEN_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.industryServiceRoom(jsonUiTemplate);
            })
            .templates( "BATH_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.color("WATER_COLOR");
                jsonUiTemplate.sliderD("WATER_OPACITY", 0, 100);
                jsonUiTemplate.space();

                JsonUIBlueprints.industryServiceRoom(jsonUiTemplate);
            })
            .templates( "BARRACKS_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.sliderD("RACE_PREFERENCE", 0, 100);
                jsonUiTemplate.slider("FULL_TRAINING_IN_DAYS", 0, 1000);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 3);
                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "BARBER_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.sliderD("WORK_TIME_IN_DAYS", 0, 100);
                jsonUiTemplate.space();

                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "ARTILLERY_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.text("SOUND_WORK");
                jsonUiTemplate.space();

                JsonUIBlueprints.projectile(jsonUiTemplate);
            })
            .templates( "ARENAG_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "ARENA_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.upgrades(jsonUiTemplate, 2);
                JsonUIBlueprints.serviceRoom(jsonUiTemplate);
            })
            .templates( "ARCHERY_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.slider("TRAINING.FULL_TRAINING_IN_DAYS", 0, 1000);
                jsonUiTemplate.space();

                JsonUIBlueprints.room(jsonUiTemplate);
            })
            .templates( "ADMIN_", jsonUiTemplate -> {
                JsonUIBlueprints.roomHeadIconSFloor(jsonUiTemplate);
                JsonUIBlueprints.admin(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUIBlueprints.industryRoom(jsonUiTemplate);
            }).build();
    }

    private static JsonUi settlement() {
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
                jsonUiTemplate.sliderD("ACCIDENT", 0, 100);
                jsonUiTemplate.sliderD("BRAWL", 0, 10);
            }).build();
    }
}
