package menu;

import com.github.argon.sos.moreoptions.game.data.GameResources;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.ButtonMenu;
import com.github.argon.sos.moreoptions.game.ui.Switcher;
import com.github.argon.sos.moreoptions.game.ui.Tabulator;
import com.github.argon.sos.mod.sdk.util.Maps;
import init.C;
import init.paths.PATHS;
import lombok.Getter;
import menu.json.JsonUITemplates;
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
            .templates(animals())
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
        return JsonUi.builder(PATHS.INIT().getFolder("animal"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.slider("HEIGHT", 0, 10);
                jsonUiTemplate.slider("SPEED", 0, 10);
                jsonUiTemplate.slider("MASS", 0, 1000);
                jsonUiTemplate.sliderD("DANGER", 0, 100);
                jsonUiTemplate.color("COLOR");
                jsonUiTemplate.text("ICON");
                jsonUiTemplate.dropDown("SPRITE", GameResources.sprite().animal().fileTitles());
                jsonUiTemplate.sliderD("LIVES_IN_CAVES", 0, 100);
                jsonUiTemplate.checkbox("CARAVAN");
                jsonUiTemplate.checkbox("PACK");
                jsonUiTemplate.checkbox("GRAZES");
                JsonUITemplates.resources(jsonUiTemplate);
                // todo
                //  slider list for RESOURCE_AMOUNT
                //  list of string inputs for SOUND

                JsonUITemplates.climate(jsonUiTemplate);
                JsonUITemplates.terrain(jsonUiTemplate);
                JsonUITemplates.damage("", jsonUiTemplate);
            }).build();
    }

    private JsonUi fences() {
        return JsonUi.builder(PATHS.SETT().init.getFolder("fence"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.dropDown("RESOURCE", GameResources.getResources());
                jsonUiTemplate.slider("RESOURCE_AMOUNT", 0, 1000);
                jsonUiTemplate.color("MINIMAP_COLOR");
                jsonUiTemplate.text("BUILD_SOUND");
                JsonUITemplates.standing(jsonUiTemplate);
            }).build();
    }

    private JsonUi environments() {
        return JsonUi.builder(PATHS.SETT().init.getFolder("environment"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.sliderD("DECLINE_VALUE", 0, 100);
                JsonUITemplates.standing(jsonUiTemplate);
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
                jsonUiTemplate.slidersD("ROAD.ENVIRONMENT", GameResources.getEnvironments(), 0, 100);
                jsonUiTemplate.header("ROAD.PREFERENCE");
                jsonUiTemplate.slidersD("ROAD.PREFERENCE", GameResources.getEnvironments(), 0, 100);
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
                jsonUiTemplate.slidersD("PREFERENCE", GameResources.getRaces(), 0, 100);
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

                JsonUITemplates.stats(jsonUiTemplate);
                JsonUITemplates.boosters("BOOST", jsonUiTemplate);
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
                jsonUiTemplate.slider("SQUALOR_POPULATION_DELTA", 0, 10000);
            })
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.checkbox("EPIDEMIC");
                jsonUiTemplate.checkbox("REGULAR");
                jsonUiTemplate.sliderD("FATALITY_RATE", 0, 100);
                jsonUiTemplate.sliderD("SPREAD", 0, 100, 3);
                jsonUiTemplate.slider("INFECTION_DAYS", 0, 100);
                jsonUiTemplate.header("OCCURRENCE_CLIMATE");
                jsonUiTemplate.slidersD("OCCURRENCE_CLIMATE", GameResources.getClimates(), 0, 100);
            }).build();
    }

    private static JsonUi rooms() {
        return JsonUi.builder(PATHS.INIT().getFolder("room"))
            .templates( "WORKSHOP_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "WELL_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "UNIVERSITY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.sliderD("LEARNING_SPEED", 0, 100);
                jsonUiTemplate.space();

                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "TOMB_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.dropDown("MONUMENT", GameResources.getMonuments());
                jsonUiTemplate.space();

                JsonUITemplates.standingWithDefault(jsonUiTemplate);
                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "TEMPLE_", jsonUiTemplate -> {
                JsonUITemplates.roomHead(jsonUiTemplate);
                jsonUiTemplate.dropDown("SACRIFICE_TYPE", GameResources.getRaces());
                jsonUiTemplate.sliderD("SACRIFICE_TIME", 0, 100);
                jsonUiTemplate.dropDown("RELIGION", GameResources.getReligions());
                JsonUITemplates.floor(jsonUiTemplate);
                JsonUITemplates.floor(jsonUiTemplate);
                jsonUiTemplate.dropDown("FLOOR_PATH", GameResources.getFloors());
                jsonUiTemplate.space();

                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "TAVERN_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "STAGE_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "SPEAKER_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "SHRINE_", jsonUiTemplate -> {
                JsonUITemplates.roomHead(jsonUiTemplate);
                jsonUiTemplate.dropDown("RELIGION", GameResources.getReligions());
                JsonUITemplates.floors(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "RESTHOME_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "REFINER_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "POOL_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconS(jsonUiTemplate);
                jsonUiTemplate.color("WATER_COLOR");
                jsonUiTemplate.sliderD("WATER_DEPTH", 0, 100);
                jsonUiTemplate.checkbox("CLEARS_GRASS");
                jsonUiTemplate.space();

                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "PLEASURE_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.dropDown("FLOOR2", GameResources.getFloors());
                jsonUiTemplate.color("COLOR_PIXEL_BAS");
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "PHYSICIAN_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "PASTURE_", jsonUiTemplate -> {
                JsonUITemplates.roomHead(jsonUiTemplate);
                JsonUITemplates.floor(jsonUiTemplate);
                jsonUiTemplate.dropDown("FENCE", GameResources.getFences());
                jsonUiTemplate.dropDown("ANIMAL", GameResources.getAnimals());
                jsonUiTemplate.space();

                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "ORCHARD_", jsonUiTemplate -> {
                JsonUITemplates.roomHead(jsonUiTemplate);
                JsonUITemplates.floor(jsonUiTemplate);
                jsonUiTemplate.checkbox("INDOORS");
                jsonUiTemplate.dropDown("EXTRA_RESOURCE", GameResources.getResources());
                jsonUiTemplate.slider("EXTRA_AMOUNT", 0, 256);
                jsonUiTemplate.slider("DAYS_TILL_GROWTH", 0, 256);
                jsonUiTemplate.sliderD("RIPE_AT_PART_OF_YEAR", 0, 100);
                jsonUiTemplate.space();

                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "NURSERY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.dropDown("RACE", GameResources.getRaces());
                jsonUiTemplate.slider("INCUBATION_DAYS", 0, 256);
                jsonUiTemplate.space();

                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "MONUMENT_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.checkbox("SOLID");
                jsonUiTemplate.space();

                JsonUITemplates.standingWithDefault(jsonUiTemplate);
                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "MINE_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.dropDown("MINABLE", GameResources.getMinables());
                jsonUiTemplate.text("SPRITE");
                jsonUiTemplate.slider("YEILD_WORKER_DAILY", 0, 1000);
                jsonUiTemplate.slider("DEGRADE_RATE", 0, 100);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.standingWithDefault(jsonUiTemplate);
                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "MARKET_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "LIBRARY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                JsonUITemplates.admin(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates("LAVATORY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "LABORATORY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                JsonUITemplates.admin(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "HUNTER_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.slider("MAX_EMPLOYED", 1, 10000);
                jsonUiTemplate.space();

                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "GRAVEYARD_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.standingWithDefault(jsonUiTemplate);
                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "GATEHOUSE_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
            })
            .templates( "FISHERY_", jsonUiTemplate -> {
                JsonUITemplates.roomHead(jsonUiTemplate);
                jsonUiTemplate.slider("DEGRADE_RATE", 0, 100);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "FARM_", jsonUiTemplate -> {
                JsonUITemplates.roomHead(jsonUiTemplate);
                jsonUiTemplate.dropDown("GROWABLE", GameResources.getGrowables());
                jsonUiTemplate.checkbox("INDOORS");
                jsonUiTemplate.space();

                JsonUITemplates.industryRoom(jsonUiTemplate);
            })
            .templates( "EATERY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "CANTEEN_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.industryServiceRoom(jsonUiTemplate);
            })
            .templates( "BATH_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.color("WATER_COLOR");
                jsonUiTemplate.sliderD("WATER_OPACITY", 0, 100);
                jsonUiTemplate.space();

                JsonUITemplates.industryServiceRoom(jsonUiTemplate);
            })
            .templates( "BARRACKS_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.sliderD("RACE_PREFERENCE", 0, 100);
                JsonUITemplates.training(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 3);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "BARBER_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloors(jsonUiTemplate);
                jsonUiTemplate.sliderD("WORK_TIME_IN_DAYS", 0, 100);
                jsonUiTemplate.space();

                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "ARTILLERY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.text("SOUND_WORK");
                jsonUiTemplate.space();

                JsonUITemplates.projectile(jsonUiTemplate);
            })
            .templates( "ARENAG_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "ARENA_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate, 2);
                JsonUITemplates.serviceRoom(jsonUiTemplate);
            })
            .templates( "ARCHERY_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                JsonUITemplates.training(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.room(jsonUiTemplate);
            })
            .templates( "ADMIN_", jsonUiTemplate -> {
                JsonUITemplates.roomHeadIconSFloor(jsonUiTemplate);
                JsonUITemplates.admin(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.industryRoom(jsonUiTemplate);
            }).build();
    }

    private static JsonUi settlement() {
        return JsonUi.builder(PATHS.CONFIG())
            .template("Sett", jsonUiTemplate -> {
                jsonUiTemplate.sliderD("HAPPINESS_EXPONENT", 0, 100);
                jsonUiTemplate.slider("TOURIST_PER_YEAR_MAX", 0, 4096, 32);
                jsonUiTemplate.slider("TOURIST_CRETIDS", 0, 10000);
                jsonUiTemplate.slider("DIMENSION", 64, 2048, 64);
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
