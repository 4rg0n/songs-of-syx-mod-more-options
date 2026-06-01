package menu;

import com.github.argon.sos.mod.sdk.game.asset.GameAssets;
import com.github.argon.sos.mod.sdk.ui.Button;
import com.github.argon.sos.mod.sdk.ui.ButtonMenu;
import com.github.argon.sos.mod.sdk.ui.Switcher;
import com.github.argon.sos.mod.sdk.ui.Tabulator;
import com.github.argon.sos.moreoptions.ui.json.JsonUITemplates;
import com.github.argon.sos.moreoptions.ui.json.JsonUi;
import com.github.argon.sos.moreoptions.ui.json.tab.AbstractTab;
import com.github.argon.sos.moreoptions.ui.json.tab.FilesTab;
import com.github.argon.sos.moreoptions.ui.json.tab.SimpleTab;
import init.constant.C;
import init.paths.PATHS;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;

import java.util.List;
import java.util.Map;

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
            .tabs(Map.of(
                "editor", filesTab
            ))
            .tabMenu(Switcher.<String>builder()
                .menu(ButtonMenu.<String>builder()
                    .search(searchInput)
                    .maxHeight(FullWindow.TOP_HEIGHT - 10)
                    .maxWidth(C.WIDTH() - 50)
                    .buttons(Map.of(
                        "editor", new Button("Config Editor")
                    ))
                    .sameWidth(true)
                    .horizontal(true)
                    .margin(3)
                    .buttonColor(COLOR.WHITE25)
                    .build())
                .highlight(true)
                .activeKey("editor")
                .build())
            .center(true)
            .build();

        addDownC(0, tabulator);
    }

    private JsonUi animals() {
        return JsonUi.builder(PATHS.INIT().getFolder("animal"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.slider("HEIGHT", 0, 50);
                jsonUiTemplate.slider("SPEED", 1, 31);
                jsonUiTemplate.slider("MASS", 1, 500);
                jsonUiTemplate.sliderPerc("DANGER", 0, 100);
                jsonUiTemplate.color("COLOR");
                jsonUiTemplate.text("ICON");
                jsonUiTemplate.dropDown("SPRITE", GameAssets.sprite().animal().fileTitles());
                jsonUiTemplate.sliderPerc("LIVES_IN_CAVES", 0, 100);
                jsonUiTemplate.checkbox("CARAVAN");
                jsonUiTemplate.checkbox("PACK");
                jsonUiTemplate.checkbox("GRAZES");
                JsonUITemplates.resources(jsonUiTemplate);
                // todo
                //  slider list for RESOURCE_AMOUNT
                //  list of string inputs for ANIMAL_CALL_<ANIMAL>

                JsonUITemplates.climate("", jsonUiTemplate);
                JsonUITemplates.terrain("", jsonUiTemplate);
                JsonUITemplates.damage("", jsonUiTemplate);
            }).build();
    }

    private JsonUi fences() {
        return JsonUi.builder(PATHS.SETT().init.getFolder("fence"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.dropDown("RESOURCE", GameAssets.getResources());
                jsonUiTemplate.slider("RESOURCE_AMOUNT", 0, 1000);
                jsonUiTemplate.color("MINIMAP_COLOR");
                JsonUITemplates.standing(jsonUiTemplate);
            }).build();
    }

    private JsonUi environments() {
        return JsonUi.builder(PATHS.SETT().init.getFolder("environment"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.sliderPerc("DECLINE_VALUE", 0, 100);
                JsonUITemplates.standing(jsonUiTemplate);
            }).build();
    }

    private JsonUi floors() {
        return JsonUi.builder(PATHS.SETT().init.getFolder("floor"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.header("GAME_TEXTURE");
                jsonUiTemplate.dropDown("GAME_TEXTURE.FILE", GameAssets.sprite().textures().fileTitles());
                jsonUiTemplate.inputInteger("GAME_TEXTURE.ROW", 0, 7);

                jsonUiTemplate.header("ROAD");
                jsonUiTemplate.dropDown("ROAD.RESOURCE", GameAssets.getResources());
                jsonUiTemplate.slider("ROAD.RESOURCE_AMOUNT", 0, 1000);
                jsonUiTemplate.slider("ROAD.SPEED", 0, 4);
                jsonUiTemplate.sliderPerc("ROAD.DURABILITY", 0, 100);

                jsonUiTemplate.header("ROAD.ENVIRONMENT");
                jsonUiTemplate.slidersPerc("ROAD.ENVIRONMENT", GameAssets.getEnvironments(), 0, 100);
                jsonUiTemplate.header("ROAD.PREFERENCE");
                jsonUiTemplate.slidersPerc("ROAD.PREFERENCE", GameAssets.getEnvironments(), 0, 100);
            }).build();
    }

    private JsonUi fortifications() {
        return JsonUi.builder(PATHS.SETT().init.getFolder("fortification"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.dropDown("SPRITE", GameAssets.sprite().settlement().fortification().fileTitles());
                jsonUiTemplate.color("COLOR");
                jsonUiTemplate.color("MINIMAP_COLOR");
                jsonUiTemplate.dropDown("RESOURCE", GameAssets.getResources());
                jsonUiTemplate.slider("RESOURCE_AMOUNT", 1, 1000);
                jsonUiTemplate.slider("HEIGHT", 1, 20);
                jsonUiTemplate.sliderPerc("DURABILITY", 0, 100);
            }).build();
    }

    private JsonUi structures() {
        return JsonUi.builder(PATHS.SETT().init.getFolder("structure"))
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.dropDown("SPRITE", GameAssets.sprite().settlement().structure().fileTitles());
                jsonUiTemplate.color("COLOR");
                jsonUiTemplate.color("MINIMAP_COLOR");
                jsonUiTemplate.dropDown("RESOURCE", GameAssets.getResources());
                jsonUiTemplate.slider("RESOURCE_AMOUNT", 1, 1000);
                jsonUiTemplate.sliderPerc("BUILD_TIME", 0, 100);
                jsonUiTemplate.sliderPerc("DURABILITY", 0, 100);

                jsonUiTemplate.header("PREFERENCE");
                jsonUiTemplate.slidersPerc("PREFERENCE", GameAssets.getRaces(), 0, 100);
            }).build();
    }

    private static JsonUi races() {
        return JsonUi.builder(PATHS.RACE().init)
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.checkbox("PLAYABLE");

                jsonUiTemplate.dropDown("SPRITE_FILE", GameAssets.getRaces());
                jsonUiTemplate.dropDown("BIO_FILE", GameAssets.getRaceBioFiles());
                jsonUiTemplate.dropDown("BIO_FILE_SPECIFIC", GameAssets.getRaceBioSpecificFiles());
                jsonUiTemplate.dropDown("HOME", GameAssets.getRaceHomeFiles());
                jsonUiTemplate.dropDown("KING_FILE", GameAssets.getRaceKingFiles());
                jsonUiTemplate.dropDown("RAID_TEXT_FILE", GameAssets.getRaceKingFiles());
                jsonUiTemplate.dropDown("RAIDER_NAME_FILE", GameAssets.getRaceRaiderNameFiles());
                jsonUiTemplate.dropDown("WORLD_NAME_FILE", GameAssets.getWorldNameFiles());

                jsonUiTemplate.text("ICON_SMALL");
                jsonUiTemplate.text("ICON_BIG");

                jsonUiTemplate.header("PROPERTIES");
                jsonUiTemplate.slider("PROPERTIES.HEIGHT", 0, 200);
                jsonUiTemplate.slider("PROPERTIES.WIDTH", 5, 15);
                jsonUiTemplate.slider("PROPERTIES.ADULT_AT_DAY", 0, 10000);
                jsonUiTemplate.slider("PROPERTIES.BABY_DAYS", 0, 10000);
                jsonUiTemplate.slider("PROPERTIES.CHILD_DAYS", 0, 10000);
                jsonUiTemplate.slider("PROPERTIES.SLAVE_PRICE", 0, 10000);
                jsonUiTemplate.sliderPerc("PROPERTIES.SLAVE_PRICE_RECOVERY", 0, 1000);
                jsonUiTemplate.sliderPerc("PROPERTIES.RAID_MERCINARY", 0, 10000000);
                jsonUiTemplate.checkbox("PROPERTIES.CORPSE_DECAY");
                jsonUiTemplate.checkbox("PROPERTIES.SLEEPS");

                jsonUiTemplate.header("POPULATION");
                jsonUiTemplate.sliderPerc("POPULATION.GROWTH", 0, 100, 3);
                jsonUiTemplate.sliderPerc("POPULATION.IMMIGRATION_RATE", 0, 10000000);
                jsonUiTemplate.sliderPerc("POPULATION.MAX", 0, 100);
                JsonUITemplates.climate("POPULATION.", jsonUiTemplate);
                JsonUITemplates.terrain("POPULATION.", jsonUiTemplate);

                jsonUiTemplate.header("TOURISM");
                jsonUiTemplate.sliderPerc("TOURIST.CREDITS", 0, 10000000);
                jsonUiTemplate.sliderPerc("TOURIST.OCCURENCE", 0, 10000000);
                jsonUiTemplate.dropDown("TOURIST.TOURIST_TEXT_FILE", GameAssets.getTouristTextFiles());

                jsonUiTemplate.header("EQUIPMENT");
                jsonUiTemplate.multiDropDown("EQUIPMENT.EQUIPMENT_ENABLED", GameAssets.getAllEquipments());
                jsonUiTemplate.multiDropDown("EQUIPMENT.EQUIPMENT_NOT_ENABLED", GameAssets.getAllEquipments());

                jsonUiTemplate.header("MILITARY_SUPPLY_USE");
                jsonUiTemplate.multiDropDown("PREFERRED", GameAssets.getSupplies());

                jsonUiTemplate.header("ROOM_FLOOR_OVERRIDE");
                GameAssets.getRooms().forEach(room -> {
                    jsonUiTemplate.dropDown("ROOM_FLOOR_OVERRIDE." + room, GameAssets.getFloors());
                });

                jsonUiTemplate.header("PREFERRED");
                jsonUiTemplate.sliderPerc("PREFERRED.RESOURCE_RPICE_MUL", 0, 10000);
                jsonUiTemplate.sliderPerc("PREFERRED.RESOURCE_RPICE_CAP", 0, 100);
                jsonUiTemplate.multiDropDown("PREFERRED.FOOD", GameAssets.getEdibles());
                jsonUiTemplate.multiDropDown("PREFERRED.DRINK", GameAssets.getDrinkables());

                jsonUiTemplate.header("PREFERRED.STRUCTURE");
                jsonUiTemplate.slidersPerc("PREFERRED.STRUCTURE",
                    GameAssets.getStructures(), true, 0, 100);

                jsonUiTemplate.header("PREFERRED.ROAD");
                jsonUiTemplate.slidersPerc("PREFERRED.ROAD",
                    GameAssets.getFloors(), true, 0, 100);

                jsonUiTemplate.header("PREFERRED.POOL");
                jsonUiTemplate.slidersPerc("PREFERRED.POOL",
                    GameAssets.getPoolRooms(), true, 0, 100);

                jsonUiTemplate.header("PREFERRED.WORK");
                jsonUiTemplate.slidersPerc("PREFERRED.WORK",
                    GameAssets.getRooms(), true, -1000000, 1000000);

                jsonUiTemplate.header("PREFERRED.OTHER_RACES");
                jsonUiTemplate.slidersPerc("PREFERRED.OTHER_RACES",
                    GameAssets.getRaces(), true, 0, 100);

                jsonUiTemplate.header("PREFERRED.OTHER_RACES_REVERSE");
                jsonUiTemplate.slidersPerc("PREFERRED.OTHER_RACES_REVERSE",
                    GameAssets.getRaces(), true, 0, 100);

                jsonUiTemplate.header("PREFERRED.WORLD_BUILDING");
                jsonUiTemplate.slidersPerc("PREFERRED.WORLD_BUILDING", GameAssets.getWorldBuildings(),  0, 10000);

                jsonUiTemplate.header("PREFERRED.CRIME");
                jsonUiTemplate.sliders("PREFERRED.CRIME", GameAssets.getCrimes(), 0, 10000);

                jsonUiTemplate.header("PREFERRED.PUNISHMENT");
                jsonUiTemplate.sliders("PREFERRED.PUNISHMENT", GameAssets.getPunishments(), 0, 10000);

                jsonUiTemplate.header("RESOURCE");
                jsonUiTemplate.sliders("RESOURCE",
                    GameAssets.getResources(), true, 0, 100);

                jsonUiTemplate.header("RESOURCE_GROOMING");
                jsonUiTemplate.sliders("RESOURCE_GROOMING",
                    GameAssets.getResources(), true, 0, 100);

                JsonUITemplates.stats(jsonUiTemplate);
                JsonUITemplates.boosters("BOOST", jsonUiTemplate);
            }).build();
    }

    private static JsonUi world() {
        return JsonUi.builder(PATHS.INIT_WORLD().getFolder("config"))
            .template("General", jsonUiTemplate -> {
                jsonUiTemplate.slider("POPULATION_CAPACITY_MAX", 1, 100000);
                jsonUiTemplate.slider("POPULATION_PLAYER_REGION", 1, 10000);
                jsonUiTemplate.slider("TILE_DIMENSION", 128, 512, 16);
                jsonUiTemplate.sliderPerc("FOREST_AMOUNT", 0, 100);
                jsonUiTemplate.slider("REGION_SIZE", 0, 1000);
            }).build();
    }

    private static JsonUi diseases() {
        return JsonUi.builder(PATHS.INIT().getFolder("disease"))
            .template("_CONFIG", jsonUiTemplate -> {
                jsonUiTemplate.slider("REGULAR_SICKNESS_DAY_INVERVAL", 1, 10000000);
            })
            .templates(jsonUiTemplate -> {
                jsonUiTemplate.checkbox("EPIDEMIC");
                jsonUiTemplate.checkbox("REGULAR");
                jsonUiTemplate.sliderPerc("FATALITY_RATE", 0, 100);
                jsonUiTemplate.sliderPerc("SPREAD", 0, 100, 3);
                jsonUiTemplate.slider("INFECTION_DAYS", 0, 100);
                jsonUiTemplate.color("COLOR");
                jsonUiTemplate.header("OCCURRENCE_CLIMATE");
                jsonUiTemplate.slidersPerc("OCCURRENCE_CLIMATE", GameAssets.getClimates(), 0, 100, 3);
                jsonUiTemplate.header("OCCURRENCE_TERRAIN");
                jsonUiTemplate.slidersPerc("OCCURRENCE_TERRAIN", GameAssets.getTerrains(), 0, 100, 3);
            }).build();
    }

    private static JsonUi rooms() {
        return JsonUi.builder(PATHS.INIT().getFolder("room"))
            .template( "_ASYLUM", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("FLOOR2", GameAssets.getFloors());
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.industryIn(jsonUiTemplate);
            })
            .template( "_BENCH", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .template( "_BUILDER", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();
                JsonUITemplates.work(jsonUiTemplate);

            })
            .template( "_CANNIBAL", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();
                JsonUITemplates.work(jsonUiTemplate);
            })
            .template( "_COURT", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();
                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
            })
            .template( "_DUMP_CORPSE", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();
                JsonUITemplates.service(jsonUiTemplate);
            })
            .template( "_EMBASSY", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.consumption(jsonUiTemplate);
                JsonUITemplates.knowledgeValues(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.industryIn(jsonUiTemplate);
            })
            .template( "_EXECUTION", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("FLOOR2", GameAssets.getFloors());
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
            })
            .template( "_GUARD", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("EQUIPMENT_TO_USE", GameAssets.getAllEquipments());
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .template( "_HAULER", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
            })
            .template( "_HEARTH", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .template( "_HOME", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .template( "_HOME_CHAMBER", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("MONUMENT", GameAssets.getMonuments());

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
            })
            .template( "_HOSPITAL", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.industryIn(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
            })
            .template( "_IMPORT", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("FLOOR2", GameAssets.getFloors());

            })
            .template( "_INN", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("FLOOR2", GameAssets.getFloors());
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
            })
            .template( "_MILITARY_SUPPLY", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("FLOOR2", GameAssets.getFloors());
                jsonUiTemplate.dropDown("FENCE", GameAssets.getFences());
                jsonUiTemplate.dropDown("LIVESTOCK", GameAssets.getResources());
                JsonUITemplates.work(jsonUiTemplate);
            })
            .template( "_PRISON", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("FLOOR2", GameAssets.getFloors());
                JsonUITemplates.work(jsonUiTemplate);
            })
            .template( "_STATION", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("FLOOR2", GameAssets.getFloors());
                JsonUITemplates.work(jsonUiTemplate);
            })
            .template( "_STOCKADE", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
            })
            .template( "_STOCKPILE", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .template( "_TRANSPORT", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("FLOOR2", GameAssets.getFloors());
                JsonUITemplates.work(jsonUiTemplate);
            })
            .template( "_WATERCANAL", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
            })
            .template( "_WATERDRAIN", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
            })
            .template( "_WATERPUMP", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
                JsonUITemplates.environmentEmit(jsonUiTemplate);
            })
            .template( "_WOODCUTTER", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.storage(jsonUiTemplate);
                JsonUITemplates.industryOut(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
                JsonUITemplates.environmentEmit(jsonUiTemplate);
            })
            .templates( "ARCHERY_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.header("DIV_SPRITE");
                jsonUiTemplate.text("DIV_SPRITE.ICON");
                jsonUiTemplate.color("DIV_SPRITE.COLOR");

                JsonUITemplates.training(jsonUiTemplate);
                JsonUITemplates.employment(jsonUiTemplate);
                JsonUITemplates.environmentEmit(jsonUiTemplate);

            })
            .templates( "ADMIN_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.slider("POP_MIN", 0, 60000);
                jsonUiTemplate.inputDouble("INCREASE_POW", 0, 100);
                jsonUiTemplate.sliderPerc("BOOST_FROM", 0, 100);
                jsonUiTemplate.sliderPerc("BOOST_TO", 0, 100000);

                JsonUITemplates.boosters("BOOSTING", jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.industryIn(jsonUiTemplate);
            })
            .templates( "ARENAG_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
            })
            .templates( "ARTILLERY_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.projectile(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
            })
            .templates( "BARBER_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.inputDouble("WORK_TIME_IN_DAYS", 0, Double.MAX_VALUE);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
            })
            .templates( "BARRACKS_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.header("DIV_SPRITE");
                jsonUiTemplate.text("DIV_SPRITE.ICON");
                jsonUiTemplate.color("DIV_SPRITE.COLOR");

                JsonUITemplates.training(jsonUiTemplate);
                JsonUITemplates.employment(jsonUiTemplate);
                JsonUITemplates.environmentEmit(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "BATH_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.color("WATER_COLOR_COLOR");
                jsonUiTemplate.sliderPerc("WATER_OPACITY", 0, 100);

                JsonUITemplates.industryIn(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "CANTEEN_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.industryIn(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "EATERY_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "FARM_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.checkbox("INDOORS");
                jsonUiTemplate.dropDown("GROWABLE", GameAssets.getResources());

                JsonUITemplates.industryOut(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
            })
            .templates( "FIGHTPIT_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "FISHERY_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.industryOut(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "GATEHOUSE_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
            })
            .templates( "GRAVEYARD_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("PATHWAY", GameAssets.getFloors());

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.standing(jsonUiTemplate);
            })
            .templates( "HUNTER_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.industries(jsonUiTemplate);
            })
            .templates( "LABORATORY_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.experienceBonus(jsonUiTemplate);
                JsonUITemplates.knowledgeValues(jsonUiTemplate);
                JsonUITemplates.consumption(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "LAVATORY_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "LIBRARY_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.experienceBonus(jsonUiTemplate);
                JsonUITemplates.knowledgeValues(jsonUiTemplate);
                JsonUITemplates.consumption(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "MARKET_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "MINE_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("MINEABLE", GameAssets.getMinables());
                jsonUiTemplate.inputDouble("YEILD_WORKER_DAILY", 0, 1000);

                JsonUITemplates.storage(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
                JsonUITemplates.environmentEmit(jsonUiTemplate);
            })
            .templates( "MONUMENT_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.checkbox("SOLID");
                jsonUiTemplate.dropDown("TYPE", List.of("TORCH"));
                jsonUiTemplate.slider("MAX_VALUE", -10000, 10000);


                JsonUITemplates.standing(jsonUiTemplate);
                JsonUITemplates.standing("STANDING_UPGRADE.STANDING", jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
                JsonUITemplates.environmentEmit(jsonUiTemplate);
            })
            .templates( "NURSERY_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("RACE", GameAssets.getRaces());
                jsonUiTemplate.slider("INCUBATION_DAYS", 0, 127);

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.industryIn(jsonUiTemplate);
            })
            .templates( "ORCHARD_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.checkbox("INDOORS");
                jsonUiTemplate.dropDown("EXTRA_RESOURCE", GameAssets.getResources());
                jsonUiTemplate.slider("EXTRA_RESOURCE_AMOUNT", 0, 10000);
                jsonUiTemplate.slider("DAYS_TILL_GROWTH", 8, 1024);
                jsonUiTemplate.sliderPerc("RIPE_AT_PART_OF_YEAR", 0, 100);

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.industryOut(jsonUiTemplate);
            })
            .templates( "PASTURE_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("ANIMAL", GameAssets.getAnimals());
                jsonUiTemplate.dropDown("FENCE", GameAssets.getFences());

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.industryOut(jsonUiTemplate);
            })
            .templates( "PHYSICIAN_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
            })
            .templates( "PLEASURE_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.color("COLOR_PIXEL_BASE");
                jsonUiTemplate.dropDown("FLOOR2", GameAssets.getFloors());

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "POOL_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.color("WATER_COLOR");
                jsonUiTemplate.sliderPerc("WATER_DEPTH", 0, 100);
                jsonUiTemplate.checkbox("CLEARS_GRASS");

                jsonUiTemplate.header("BOTTOM_TEXTURE");
                jsonUiTemplate.text("BOTTOM_TEXTURE.FILE");
                jsonUiTemplate.inputInteger("BOTTOM_TEXTURE.ROW", 0, 100);

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "REFINER_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.storage(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
                JsonUITemplates.industries(jsonUiTemplate);
                JsonUITemplates.environmentEmit(jsonUiTemplate);
            })
            .templates( "RESTHOME_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.employment(jsonUiTemplate);
            })
            .templates( "SCHOOL_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.sliderPerc("LEARNING_SPEED", 0 , 100);

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.industryIn(jsonUiTemplate);
            })
            .templates( "SHRINE_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("RELIGION", GameAssets.getReligions());

                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.industryIn(jsonUiTemplate);
            })
            .templates( "SPEAKER_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "STAGE_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "TAVERN_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
                JsonUITemplates.environmentEmit(jsonUiTemplate);
            })
            .templates( "TEMPLE_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("RELIGION", GameAssets.getReligions());
                jsonUiTemplate.dropDown("SACRIFICE_TYPE", GameAssets.getSacrifices());
                jsonUiTemplate.dropDown("FLOOR", GameAssets.getFloors());
                jsonUiTemplate.dropDown("FLOOR_PATH", GameAssets.getFloors());

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.service(jsonUiTemplate);
            })
            .templates( "TOMB_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.dropDown("MONUMENT", GameAssets.getMonuments());

                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.standing(jsonUiTemplate);
            })
            .templates( "UNIVERSITY_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                jsonUiTemplate.sliderPerc("LEARNING_SPEED", 0, 100);

                JsonUITemplates.employment(jsonUiTemplate);
            })
            .templates( "WELL_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.service(jsonUiTemplate);
                JsonUITemplates.upgrades(jsonUiTemplate);
            })
            .templates( "WORKSHOP_", jsonUiTemplate -> {
                JsonUITemplates.roomGeneral(jsonUiTemplate);
                jsonUiTemplate.space();

                JsonUITemplates.storage(jsonUiTemplate);
                JsonUITemplates.work(jsonUiTemplate);
                JsonUITemplates.environmentEmit(jsonUiTemplate);
                JsonUITemplates.industries(jsonUiTemplate);
            })
            .build();
    }

    private static JsonUi settlement() {
        return JsonUi.builder(PATHS.CONFIG().init)
            .template("Sett", jsonUiTemplate -> {
                jsonUiTemplate.sliderPerc("HAPPINESS_EXPONENT", 0, 100);
                jsonUiTemplate.inputDouble("MAX_POP", 0, 1, 4);
                jsonUiTemplate.inputInteger("SECONDS_PER_HOUR", 0, 1000000);
                jsonUiTemplate.inputInteger("HOURS_PER_DAY", 0, 1000000);
                jsonUiTemplate.slider("TOURIST_PER_YEAR_MAX", 0, 4096, 32);
                jsonUiTemplate.slider("TOURIST_CRETIDS", 0, 10000);
                jsonUiTemplate.slider("POP_RAIDER_WORTH", 0, 10000);
                jsonUiTemplate.slider("DIMENSION", 64, 2048, 64);
            })
            .template("Battle", jsonUiTemplate -> {
                jsonUiTemplate.sliderPerc("DAMAGE_REDUCTION", 1, 1000000);
                jsonUiTemplate.sliderPerc("MORALE_HOLDOUT", 0, 10000);
                jsonUiTemplate.slider("TRAINING_DEGRADE", 0, 50);
                jsonUiTemplate.slider("MEN_PER_DIVISION", 1, 255);
                jsonUiTemplate.slider("DIVISIONS_PER_ARMY", 1, 126);
                jsonUiTemplate.slider("REGION_MAX_DIV", 0, 127);
            })
            .template("LandingParty", jsonUiTemplate -> {
                jsonUiTemplate.sliders("RESOURCES", GameAssets.getResources(), 0, 10000);
            })
            .template("LEAVE_CAUSE", jsonUiTemplate -> {
                jsonUiTemplate.sliderPerc("EMMIGRATED", 0, 1000);
                jsonUiTemplate.sliderPerc("STARVED", 0, 1000);
                jsonUiTemplate.sliderPerc("SLAYED", 0, 1000);
                jsonUiTemplate.sliderPerc("ANIMAL", 0, 1000);
                jsonUiTemplate.sliderPerc("AGE", 0, 1000);
                jsonUiTemplate.sliderPerc("HEAT", 0, 1000);
                jsonUiTemplate.sliderPerc("COLD", 0, 1000);
                jsonUiTemplate.sliderPerc("MURDER", 0, 1000);
                jsonUiTemplate.sliderPerc("EXECUTED", 0, 1000);
                jsonUiTemplate.sliderPerc("DESERTED", 0, 1000);
                jsonUiTemplate.sliderPerc("DROWNED", 0, 1000);
                jsonUiTemplate.sliderPerc("ARMY", 0, 1000);
                jsonUiTemplate.sliderPerc("OTHER", 0, 1000);
                jsonUiTemplate.sliderPerc("PUNISHED", 0, 1000);
                jsonUiTemplate.sliderPerc("EXILED", 0, 1000);
                jsonUiTemplate.sliderPerc("SACRIFICED", 0, 1000);
                jsonUiTemplate.sliderPerc("DISEASE", 0, 1000);
                jsonUiTemplate.sliderPerc("ACCIDENT", 0, 1000);
                jsonUiTemplate.sliderPerc("BRAWL", 0, 1000);
                jsonUiTemplate.sliderPerc("INSANE", 0, 1000);
                jsonUiTemplate.sliderPerc("SOLD", 0, 1000);
            }).build();
    }
}
