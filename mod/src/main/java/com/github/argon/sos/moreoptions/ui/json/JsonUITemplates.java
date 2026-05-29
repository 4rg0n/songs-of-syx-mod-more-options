package com.github.argon.sos.moreoptions.ui.json;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.game.asset.GameAssets;
import com.github.argon.sos.mod.sdk.json.GameJsonService;
import com.github.argon.sos.mod.sdk.json.element.JsonArray;
import com.github.argon.sos.moreoptions.ui.json.factory.JsonUiTemplate;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Some more often occurring fragments of configuration
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUITemplates {

    private final static GameJsonService jsonService = ModSdkModule.gameJsonService();

    public static void roomGeneral(JsonUiTemplate jsonUiTemplate) {
        roomIcon(jsonUiTemplate);
        jsonUiTemplate.color("MINI_COLOR");
        // todo "MINI_COLOR_PATTERN"
        resources(jsonUiTemplate);
        roomAreaCosts(jsonUiTemplate);
        floor(jsonUiTemplate);
        // todo REQUIRES
        climate("BONUS.", jsonUiTemplate);
        items(jsonUiTemplate);
    }

    public static void roomIcon(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.text("ICON");
        jsonUiTemplate.header("ICON");
        jsonUiTemplate.text("ICON.BG");
        jsonUiTemplate.text("ICON.FG");
        jsonUiTemplate.slider("ICON.SHADOW", -100, 100);
        jsonUiTemplate.slider("ICON.OFFX", -100, 100);
        jsonUiTemplate.slider("ICON.OFFY", -100, 100);
    }

    public static void roomAreaCosts(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.inputDouble("AREA_COSTS[0]", 0,100);
        jsonUiTemplate.inputDouble("AREA_COSTS[1]", 0,100);
        jsonUiTemplate.inputDouble("AREA_COSTS[2]", 0,100);
        jsonUiTemplate.inputDouble("AREA_COSTS[3]", 0,100);
    }

    public static void roomHead(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.color("MINI_COLOR");
        items(jsonUiTemplate);
    }

    public static void roomHeadIconSFloors(JsonUiTemplate jsonUiTemplate) {
        roomHeadIconS(jsonUiTemplate);
        JsonUITemplates.floors(jsonUiTemplate);
    }

    public static void roomHeadIconSFloor(JsonUiTemplate jsonUiTemplate) {
        roomHeadIconS(jsonUiTemplate);
        JsonUITemplates.floor(jsonUiTemplate);
    }

    public static void roomHeadIconS(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.text("ICON");
        roomHead(jsonUiTemplate);
    }

    public static void boosters(String prefix, JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header(prefix);
        GameAssets.getBoosters().forEach(s -> {
            jsonUiTemplate.slider(prefix + "." + s + ">ADD", -10000, 10000);
            jsonUiTemplate.sliderPerc(prefix + "." + s + ">MUL", 0, 1000000);
            jsonUiTemplate.space(10);
        });
    }

    public static void standing(JsonUiTemplate jsonUiTemplate) {
        standing("STANDING", jsonUiTemplate);
    }

    public static void standingWithDefault(JsonUiTemplate jsonUiTemplate) {
        standing(jsonUiTemplate);
        defaultStanding(jsonUiTemplate);
    }

    private static void standing(String prefix, JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header(prefix);
        jsonUiTemplate.slidersPerc(prefix, GameAssets.getHumanClasses(), 0, 100000);
        jsonUiTemplate.space(10);
        jsonUiTemplate.slider(prefix + ".MULTIPLIER", 0, 100000);
        jsonUiTemplate.sliderPerc(prefix + ".EXPONENT", 0, 100, 3);
        jsonUiTemplate.checkbox(prefix + ".INVERTED");
        jsonUiTemplate.checkbox(prefix + ".DISMISS");
        jsonUiTemplate.slider(prefix + ".PRIO", 0, 100000);
    }

    public static void defaultStanding(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("DEFAULT_STANDING");
        jsonUiTemplate.slider("DEFAULT_STANDING.MULTIPLIER", 0, 100000);
        jsonUiTemplate.sliderPerc("DEFAULT_STANDING.EXPONENT", 0, 100, 3);
        jsonUiTemplate.checkbox("DEFAULT_STANDING.INVERTED");
        jsonUiTemplate.checkbox("DEFAULT_STANDING.DISMISS");
    }

    public static void serviceStanding(JsonUiTemplate jsonUiTemplate) {
        standing("SERVICE.STANDING", jsonUiTemplate);
    }

    public static void sheet(String key, JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header(key);
        jsonUiTemplate.color(key + ".COLOR");
        jsonUiTemplate.slider(key + ".FPS", 0, 100000);
        jsonUiTemplate.sliderPerc(key + ".FPS_INTERVAL", 0, 100);
        jsonUiTemplate.slider(key + ".SHADOW_LENGTH", 0, 100);
        jsonUiTemplate.slider(key + ".SHADOW_HEIGHT", 0, 100);
        jsonUiTemplate.checkbox(key + ".TINT");
        jsonUiTemplate.checkbox(key + ".CIRCULAR");
        jsonUiTemplate.checkbox(key + ".ROTATES");
        jsonUiTemplate.checkbox(key + ".OVERWRITE");


        // TODO
        //  * This would be a very long list... too much noise. Needs a better solution
        //  * FRAMES are basically sprite files from e.g. sprite/game/
        //  * There's a semantic with the SPRITE name e.g. STORAGE_1X1 and the folder
        //  * FRAMES can come from sprite/game/, sprite/resource/ sprite/world/
        jsonUiTemplate.slider(key + ".FRAMES[0].FENCE", 0, 8);
    }

    public static void service(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("SERVICE");
        jsonUiTemplate.sliderPerc("SERVICE.DEFAULT_ACCESS", 0, 10000);
        jsonUiTemplate.sliderPerc("SERVICE.DEFAULT_VALUE", 0, 10000);
        jsonUiTemplate.slider("SERVICE.RADIUS", 0, 50000);
        jsonUiTemplate.text("SERVICE.SOUND");
        jsonUiTemplate.dropDown("SERVICE.NEED", GameAssets.getNeeds());
        jsonUiTemplate.space(10);
        boosters("SERVICE.BOOST", jsonUiTemplate);
        serviceStanding(jsonUiTemplate);
    }

    public static void work(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("WORK");
        jsonUiTemplate.sliderPerc("WORK.SHIFT_OFFSET", 0, 100, 3);
        jsonUiTemplate.text("WORK.SOUND");
        jsonUiTemplate.checkbox("WORK.USES_TOOL");
        jsonUiTemplate.slider("WORK.LARGE_WORKFORCE", 0, 10000);
        jsonUiTemplate.sliderPerc("WORK.NIGHT_SHIFT", 0, 99);
        jsonUiTemplate.sliderPerc("WORK.HEALTH_FACTOR", 0, 100);
        jsonUiTemplate.sliderPerc("WORK.FULFILLMENT", 0, 100);
        jsonUiTemplate.sliderPerc("WORK.ACCIDENTS_PER_YEAR", 0, 100, 4);

        employment(jsonUiTemplate);
    }

    private static void employment(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("EMPLOYMENT");
        jsonUiTemplate.sliderPerc("EMPLOYTMENT.SHIFT_OFFSET", 0, 100, 3);
        jsonUiTemplate.text("EMPLOYTMENT.SOUND");
        jsonUiTemplate.checkbox("EMPLOYTMENT.USES_TOOL");
        jsonUiTemplate.sliderPerc("EMPLOYTMENT.FULFILLMENT", 0, 100);
        jsonUiTemplate.sliderPerc("EMPLOYTMENT.EDUCATION_FACTOR", 0, 100);
    }

    public static void environmentEmit(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("ENVIRONMENT");
        GameAssets.getEnvironments().forEach(s -> {
            jsonUiTemplate.sliderPerc("ENVIRONMENT_EMI." + s + ".VALUE", 0, 100);
            jsonUiTemplate.sliderPerc("ENVIRONMENT_EMI." + s + ".RADIUS", 0, 100);
        });
    }

    public static void room(JsonUiTemplate jsonUiTemplate) {
        work(jsonUiTemplate);
        jsonUiTemplate.space();
        environmentEmit(jsonUiTemplate);
        jsonUiTemplate.space();
        // fixme these are not boosters
        //boosters("BONUS", jsonUiTemplate);
    }

    public static void industryServiceRoom(JsonUiTemplate jsonUiTemplate) {
        industry(jsonUiTemplate);
        jsonUiTemplate.space();
        service(jsonUiTemplate);
        jsonUiTemplate.space();
        room(jsonUiTemplate);
    }

    public static void industryRoom(JsonUiTemplate jsonUiTemplate) {
        industry(jsonUiTemplate);
        jsonUiTemplate.space();
        room(jsonUiTemplate);
    }

    public static void serviceRoom(JsonUiTemplate jsonUiTemplate) {
        service(jsonUiTemplate);
        jsonUiTemplate.space();
        standingWithDefault(jsonUiTemplate);
        jsonUiTemplate.space();
        room(jsonUiTemplate);
    }

    public static void admin(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.sliderPerc("VALUE_DEGRADE_PER_YEAR", 0, 1000, 3);
        jsonUiTemplate.slider("VALUE_PER_WORKER", 0, 100000);
        jsonUiTemplate.slider("VALUE_WORK_SPEED", 0, 1000);
    }

    public static void industry(JsonUiTemplate jsonUiTemplate) {
        industryIn(jsonUiTemplate);
        industryOut(jsonUiTemplate);
    }

    public static void industryIn(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("INDUSTRY.IN");
        jsonUiTemplate.slidersPerc("INDUSTRY.IN", GameAssets.getResources(), 0, 1000);
    }

    public static void industryOut(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("INDUSTRY.OUT");
        jsonUiTemplate.slidersPerc("INDUSTRY.OUT", GameAssets.getResources(), 0, 1000);
    }

    public static void projectile(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("PROJECTILE");
        jsonUiTemplate.color("PROJECTILE.COLOR");
        jsonUiTemplate.text("PROJECTILE.SPRITE_FILE");
        jsonUiTemplate.text("PROJECTILE.SOUND_RELEASE");
        jsonUiTemplate.text("PROJECTILE.SOUND_HIT");
        jsonUiTemplate.space(10);

        jsonUiTemplate.header("PROJECTILE.FROM");
        jsonUiTemplate.slider("PROJECTILE.FROM.MASS", 1, 250);
        jsonUiTemplate.sliderPerc("PROJECTILE.FROM.TILE_SPEED", 1, 300);
        jsonUiTemplate.sliderPerc("PROJECTILE.FROM.ACCURACY", 1, 100);
        jsonUiTemplate.slider("PROJECTILE.FROM.TILE_RADIUS_DAMAGE", 1, 10000);
        jsonUiTemplate.slider("PROJECTILE.FROM.RELOAD_SECONDS", 1, 10000);
        jsonUiTemplate.slider("PROJECTILE.FROM.MAX_ARCH_ANGLE_DEGREES", 0, 75);
        jsonUiTemplate.space(10);

        jsonUiTemplate.header("PROJECTILE.TO");
        jsonUiTemplate.sliderPerc("PROJECTILE.TO.TILE_SPEED", 1, 250);
        jsonUiTemplate.slider("PROJECTILE.TO.MASS", 1, 250);
        jsonUiTemplate.sliderPerc("PROJECTILE.TO.ACCURACY", 1, 100);
        jsonUiTemplate.slider("PROJECTILE.TO.TILE_RADIUS_DAMAGE", 1, 10000);
        jsonUiTemplate.slider("PROJECTILE.TO.RELOAD_SECONDS", 1, 10000);
        jsonUiTemplate.slider("PROJECTILE.TO.MAX_ARCH_ANGLE_DEGREES", 0, 75);
        jsonUiTemplate.space(10);

        jsonUiTemplate.header("PROJECTILE.FROM.DAMAGE");
        damage("PROJECTILE.FROM.", jsonUiTemplate);
    }

    public static void stats(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("STATS");
        GameAssets.getStats().forEach(s -> {
            standing("STATS." + s, jsonUiTemplate);
            jsonUiTemplate.space(10);
        });
    }

    public static void upgrades(JsonUiTemplate jsonUiTemplate, int amount) {
        Integer resourcesAmount = jsonService.get(PATHS.TEXT().getFolder("room").get(jsonUiTemplate.getName()))
            .flatMap(gameJsonResource -> jsonService.get(PATHS.INIT().getFolder("room").get(jsonUiTemplate.getName()))
            .flatMap(gameJsonResource1 -> gameJsonResource1.extract("RESOURCES", JsonArray.class).map(JsonArray::size)))
            .orElse(0);

        upgrades(jsonUiTemplate, amount, resourcesAmount);
    }

    private static void upgrades(JsonUiTemplate jsonUiTemplate, int amount, int resourceAmount) {
        jsonUiTemplate.header("UPGRADES");
        for (int i = 0; i < amount; i++) {
            jsonUiTemplate.sliderPerc("UPGRADES[" + i + "].BOOST", 0, 100);
            for (int j = 0; j < resourceAmount; j++) {
                jsonUiTemplate.slider("UPGRADES[" + i + "].RESOURCE_MASK[" + j + "]", List.of(0, 1, 2, 3, 5));
            }
            jsonUiTemplate.space(10);
        }
    }

    public static void items(JsonUiTemplate jsonUiTemplate) {
        jsonService.get(PATHS.TEXT().getFolder("room").get(jsonUiTemplate.getName())).ifPresent(gameJsonResource -> {
            Integer statsAmount = gameJsonResource.extract("STATS", JsonArray.class)
                .map(JsonArray::size)
                .orElse(0);

            Integer itemsAmount = gameJsonResource.extract("ITEMS", JsonArray.class)
                .map(JsonArray::size)
                .orElse(0);

            Integer resourcesAmount = jsonService.get(PATHS.INIT().getFolder("room").get(jsonUiTemplate.getName()))
                .flatMap(gameJsonResource1 -> gameJsonResource1.extract("RESOURCES", JsonArray.class)
                    .map(JsonArray::size)).orElse(0);

            items(jsonUiTemplate, itemsAmount, resourcesAmount, statsAmount);
        });
    }

    private static void items(JsonUiTemplate jsonUiTemplate, int itemsAmount, int resourcesAmount, int statsAmount) {
        resources(jsonUiTemplate);
        jsonUiTemplate.header("ITEMS");
        for (int i = 0; i < itemsAmount; i++) {
            for (int j = 0; j < resourcesAmount; j++) {
                jsonUiTemplate.slider("ITEMS[" + i + "].COSTS[" + j + "]", 0, 10000);
            }
            jsonUiTemplate.space(10);
            for (int j = 0; j < statsAmount; j++) {
                jsonUiTemplate.sliderPerc("ITEMS[" + i + "].STATS[" + j + "]", 0, 10000, 3);
            }
            jsonUiTemplate.space(10);
        }
    }

    public static void climate(String prefix, JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header(prefix + "CLIMATE");
        jsonUiTemplate.slidersPerc(prefix + "CLIMATE", GameAssets.getClimates(), 0, 100);
    }

    public static void terrain(String prefix, JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header(prefix + "TERRAIN");
        jsonUiTemplate.slidersPerc(prefix+ "TERRAIN", GameAssets.getTerrains(), 0, 10000000);
    }
    
    public static void floor(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.dropDown("FLOOR", GameAssets.getFloors());
    }

    public static void floors(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.dropDownList("FLOOR", GameAssets.getFloors());
    }

    public static void resources(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.dropDownList("RESOURCES", GameAssets.getResources());
    }

    public static void training(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.slider("TRAINING.FULL_TRAINING_IN_DAYS", 0, 1000);
        boosters("BOOST", jsonUiTemplate);
    }

    public static void damage(String prefix, JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.slidersPerc(prefix + "DAMAGE", GameAssets.getDamages(), 0, 10000);

    }
}
