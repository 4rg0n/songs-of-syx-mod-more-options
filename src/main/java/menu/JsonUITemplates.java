package menu;

import com.github.argon.sos.moreoptions.game.GameResources;
import com.github.argon.sos.moreoptions.game.json.GameJsonService;
import com.github.argon.sos.moreoptions.json.element.JsonArray;
import com.github.argon.sos.moreoptions.util.Lists;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import menu.json.JsonUiTemplate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUITemplates {

    private final static GameJsonService jsonService = GameJsonService.getInstance();

    public static void roomHead(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.color("MINI_COLOR");
        items(jsonUiTemplate);
    }

    public static void roomHeadIconSFloors(JsonUiTemplate jsonUiTemplate) {
        roomHeadIconS(jsonUiTemplate);
        jsonUiTemplate.selectS("FLOOR", GameResources.getFloors());
    }

    public static void roomHeadIconSFloor(JsonUiTemplate jsonUiTemplate) {
        roomHeadIconS(jsonUiTemplate);
        jsonUiTemplate.dropDown("FLOOR", GameResources.getFloors());
    }

    public static void roomHeadIconS(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.text("ICON");
        roomHead(jsonUiTemplate);
    }

    public static void roomHeadIconO(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.iconO("ICON");
        roomHead(jsonUiTemplate);
    }

    public static void standing(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("STANDING");
        standing("STANDING.", jsonUiTemplate);
        defaultStanding(jsonUiTemplate);
    }

    private static void standing(String prefix, JsonUiTemplate jsonUiTemplate) {
        GameResources.getHumanClasses().forEach(s -> {
            jsonUiTemplate.sliderD(prefix + s, 0, 100000);
        });
        jsonUiTemplate.slider(prefix + "MULTIPLIER", 0, 100000);
        jsonUiTemplate.sliderD(prefix + "EXPONENT", 0, 100, 3);
        jsonUiTemplate.checkbox(prefix + "INVERTED");
        jsonUiTemplate.checkbox(prefix + "DISMISS");
        jsonUiTemplate.slider(prefix + "PRIO", 0, 100000);
    }

    public static void defaultStanding(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("DEFAULT_STANDING");
        jsonUiTemplate.slider("DEFAULT_STANDING.MULTIPLIER", 0, 100000);
        jsonUiTemplate.sliderD("DEFAULT_STANDING.EXPONENT", 0, 100, 3);
        jsonUiTemplate.checkbox("DEFAULT_STANDING.INVERTED");
        jsonUiTemplate.checkbox("DEFAULT_STANDING.DISMISS");
    }

    public static void serviceStanding(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("SERVICE.STANDING");
        standing("SERVICE.STANDING.", jsonUiTemplate);
    }

    public static void service(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("SERVICE");
        jsonUiTemplate.sliderD("SERVICE.DEFAULT_ACCESS", 0, 10000);
        jsonUiTemplate.sliderD("SERVICE.DEFAULT_VALUE", 0, 10000);
        jsonUiTemplate.slider("SERVICE.RADIUS", 0, 50000);
        jsonUiTemplate.text("SERVICE.SOUND");
        jsonUiTemplate.dropDown("SERVICE.NEED", GameResources.getNeeds());
        serviceStanding(jsonUiTemplate);
    }

    public static void work(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("WORK");
        jsonUiTemplate.sliderD("WORK.SHIFT_OFFSET", 0, 100);
        jsonUiTemplate.text("WORK.SOUND");
        jsonUiTemplate.checkbox("WORK.USES_TOOL");
        jsonUiTemplate.slider("WORK.LARGE_WORKFORCE", 0, 10000);
        jsonUiTemplate.sliderD("WORK.NIGHT_SHIFT", 0, 99);
        jsonUiTemplate.sliderD("WORK.HEALTH_FACTOR", 0, 100);
        jsonUiTemplate.sliderD("WORK.FULFILLMENT", 0, 100);
        jsonUiTemplate.sliderD("WORK.ACCIDENTS_PER_YEAR", 0, 100, 4);

        employment(jsonUiTemplate);
    }

    private static void employment(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("EMPLOYMENT");
        jsonUiTemplate.sliderD("EMPLOYTMENT.SHIFT_OFFSET", 0, 100, 3);
        jsonUiTemplate.text("EMPLOYTMENT.SOUND");
        jsonUiTemplate.checkbox("EMPLOYTMENT.USES_TOOL");
        jsonUiTemplate.sliderD("EMPLOYTMENT.FULFILLMENT", 0, 100);
        jsonUiTemplate.sliderD("EMPLOYTMENT.EDUCATION_FACTOR", 0, 100);
    }

    public static void environmentEmit(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("ENVIRONMENT");
        GameResources.getEnvironments().forEach(s -> {
            jsonUiTemplate.sliderD("ENVIRONMENT_EMI." + s + ".VALUE", 0, 100);
            jsonUiTemplate.sliderD("ENVIRONMENT_EMI." + s + ".RADIUS", 0, 100);
        });
    }

    public static void room(JsonUiTemplate jsonUiTemplate) {
        work(jsonUiTemplate);
        environmentEmit(jsonUiTemplate);
    }

    public static void industryServiceRoom(JsonUiTemplate jsonUiTemplate) {
        industry(jsonUiTemplate);
        service(jsonUiTemplate);
        room(jsonUiTemplate);
    }

    public static void industryRoom(JsonUiTemplate jsonUiTemplate) {
        industry(jsonUiTemplate);
        room(jsonUiTemplate);
    }

    public static void serviceRoom(JsonUiTemplate jsonUiTemplate) {
        service(jsonUiTemplate);
        standing(jsonUiTemplate);
        room(jsonUiTemplate);
    }

    public static void admin(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.sliderD("VALUE_DEGRADE_PER_YEAR", 0, 1000, 3);
        jsonUiTemplate.slider("VALUE_PER_WORKER", 0, 100000);
        jsonUiTemplate.slider("VALUE_WORK_SPEED", 0, 1000);
    }

    public static void industry(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("INDUSTRY.IN");
        GameResources.getResources().forEach(s -> {
            jsonUiTemplate.sliderD("INDUSTRY.IN." + s, 0, 1000);
        });

        jsonUiTemplate.header("INDUSTRY.OUT");
        GameResources.getResources().forEach(s -> {
            jsonUiTemplate.sliderD("INDUSTRY.OUT." + s, 0, 1000);
        });
    }

    public static void projectile(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("PROJECTILE");
        jsonUiTemplate.color("PROJECTILE.COLOR");
        jsonUiTemplate.text("PROJECTILE.SPRITE_FILE");
        jsonUiTemplate.text("PROJECTILE.SOUND_RELEASE");
        jsonUiTemplate.text("PROJECTILE.SOUND_HIT");
        jsonUiTemplate.separator();

        jsonUiTemplate.sliderD("PROJECTILE.FROM.TILE_SPEED", 1, 250);
        jsonUiTemplate.slider("PROJECTILE.FROM.MASS", 1, 250);
        jsonUiTemplate.sliderD("PROJECTILE.FROM.TILE_SPEED", 1, 100000);
        jsonUiTemplate.sliderD("PROJECTILE.FROM.ACCURACY", 1, 100);
        jsonUiTemplate.slider("PROJECTILE.FROM.TILE_RADIUS_DAMAGE", 1, 10000);
        jsonUiTemplate.slider("PROJECTILE.FROM.RELOAD_SECONDS", 1, 10000);
        jsonUiTemplate.slider("PROJECTILE.FROM.MAX_ARCH_ANGLE_DEGREES", 0, 75);
        jsonUiTemplate.separator();

        jsonUiTemplate.sliderD("PROJECTILE.TO.TILE_SPEED", 1, 250);
        jsonUiTemplate.slider("PROJECTILE.TO.MASS", 1, 250);
        jsonUiTemplate.sliderD("PROJECTILE.TO.TILE_SPEED", 1, 100000);
        jsonUiTemplate.sliderD("PROJECTILE.TO.ACCURACY", 1, 100);
        jsonUiTemplate.slider("PROJECTILE.TO.TILE_RADIUS_DAMAGE", 1, 10000);
        jsonUiTemplate.slider("PROJECTILE.TO.RELOAD_SECONDS", 1, 10000);
        jsonUiTemplate.slider("PROJECTILE.TO.MAX_ARCH_ANGLE_DEGREES", 0, 75);
        jsonUiTemplate.separator();

        GameResources.getDamages().forEach(s -> {
            jsonUiTemplate.sliderD("PROJECTILE.FROM.DAMAGE." + s, 1, 100);
        });
    }

    public static void stats(JsonUiTemplate jsonUiTemplate) {
        jsonUiTemplate.header("STATS");
        GameResources.getStats().forEach(s -> {
            standing("STATS." + s + ".", jsonUiTemplate);
        });
    }

    public static void upgrades(JsonUiTemplate jsonUiTemplate, int amount) {
        Integer resourcesAmount = jsonService.get(PATHS.TEXT().getFolder("room").get(jsonUiTemplate.getName()))
            .flatMap(gameJsonResource -> jsonService.get(PATHS.INIT().getFolder("room").get(jsonUiTemplate.getName()))
            .flatMap(gameJsonResource1 -> gameJsonResource1.getJson().extract("RESOURCES", JsonArray.class).map(JsonArray::size)))
            .orElse(0);

        upgrades(jsonUiTemplate, amount, resourcesAmount);
    }

    private static void upgrades(JsonUiTemplate jsonUiTemplate, int amount, int resourceAmount) {
        jsonUiTemplate.header("UPGRADES");
        for (int i = 0; i < amount; i++) {
            jsonUiTemplate.sliderD("UPGRADES[" + i + "].BOOST", 0, 100);
            for (int j = 0; j < resourceAmount; j++) {
                jsonUiTemplate.slider("UPGRADES[" + i + "].RESOURCE_MASK[" + j + "]", Lists.of(0, 1, 2, 3, 5))
                    .description("TODO");
            }
        }
    }

    public static void items(JsonUiTemplate jsonUiTemplate) {
        jsonService.get(PATHS.TEXT().getFolder("room").get(jsonUiTemplate.getName())).ifPresent(gameJsonResource -> {
            Integer statsAmount = gameJsonResource.getJson().extract("STATS", JsonArray.class)
                .map(JsonArray::size)
                .orElse(0);

            Integer itemsAmount = gameJsonResource.getJson().extract("ITEMS", JsonArray.class)
                .map(JsonArray::size)
                .orElse(0);

            Integer resourcesAmount = jsonService.get(PATHS.INIT().getFolder("room").get(jsonUiTemplate.getName()))
                .flatMap(gameJsonResource1 -> gameJsonResource1.getJson().extract("RESOURCES", JsonArray.class)
                    .map(JsonArray::size)).orElse(0);

            items(jsonUiTemplate, itemsAmount, resourcesAmount, statsAmount);
        });
    }

    private static void items(JsonUiTemplate jsonUiTemplate, int itemsAmount, int resourcesAmount, int statsAmount) {
        jsonUiTemplate.header("ITEMS");
        jsonUiTemplate.selectS("RESOURCES", GameResources.getResources(), true);

        for (int i = 0; i < itemsAmount; i++) {
            for (int j = 0; j < resourcesAmount; j++) {
                jsonUiTemplate.slider("ITEMS[" + i + "].COSTS[" + j + "]", 0, 1000)
                    .description("TODO");
            }

            for (int j = 0; j < statsAmount; j++) {
                jsonUiTemplate.sliderD("ITEMS[" + i + "].STATS[" + j + "]", 0, 1000)
                    .description("TODO");
            }
        }
    }
}
