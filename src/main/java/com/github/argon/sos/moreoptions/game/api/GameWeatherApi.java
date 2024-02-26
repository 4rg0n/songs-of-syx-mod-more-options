package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import settlement.main.SETT;
import settlement.weather.SWEATHER;
import settlement.weather.WeatherThing;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameWeatherApi {

    private final Logger log = Loggers.getLogger(GameWeatherApi.class);

    @Getter(lazy = true)
    private final static GameWeatherApi instance = new GameWeatherApi();

    public final static String KEY_PREFIX = "weather";

    @Nullable
    private Map<String, WeatherThing> weatherThings;

    public Map<String, WeatherThing> getWeatherThings() {
        if (weatherThings == null) {
            weatherThings = new HashMap<>();
            SWEATHER gameWeather = SETT.WEATHER();

            weatherThings.put(KEY_PREFIX + ".thunder", gameWeather.thunder);
            weatherThings.put(KEY_PREFIX + ".ice", gameWeather.ice);
            weatherThings.put(KEY_PREFIX + ".rain", gameWeather.rain);
            weatherThings.put(KEY_PREFIX + ".clouds", gameWeather.clouds);
            weatherThings.put(KEY_PREFIX + ".snow", gameWeather.snow);
            weatherThings.put(KEY_PREFIX + ".moisture", gameWeather.moisture);
            weatherThings.put(KEY_PREFIX + ".wind", gameWeather.wind);
            weatherThings.put(KEY_PREFIX + ".growth", gameWeather.growth);
            weatherThings.put(KEY_PREFIX + ".growthRipe", gameWeather.growthRipe);
        }

        return weatherThings;
    }

    public void clearCached() {
        weatherThings = null;
    }

    /**
     * 0 = night?
     * 1 = predawn
     * 2 = dawn
     * 3 = dusk
     * 4 = afterDusk
     * 5 = day?
     * 6 = night?
     */

    //TODO::experimental, need to do MoreOptionsScript thingy first
//    public void lockDayCycle(int cycle, boolean lock) {
//        if (cycle > 6 || cycle < 0) {
//            log.warn("Invalid day cycle %s. Must be bigger than 0 and lesser than 6", cycle);
//            return;
//        }
//
//        TIME.light().sun().setCycleLock(cycle);
//        TIME.light().sun().setLocked(lock);
//
//        TIME.light().moon().setCycleLock(cycle);
//        TIME.light().moon().setLocked(lock);
//    }
//
//    public void unlockDayCycle() {
//        TIME.light().sun().setCycleLock(0);
//        TIME.light().sun().setLocked(false);
//
//        TIME.light().moon().setCycleLock(0);
//        TIME.light().moon().setLocked(false);
//    }

    public void setAmountLimit(WeatherThing weatherThing, int percentage) {
        double currentValue = weatherThing.getD();
        double limitPerc = MathUtil.toPercentage(percentage);
        double newValue = currentValue * limitPerc;

        log.trace("Applying amount limit %s%% to %s = %s", percentage, weatherThing.getClass().getSimpleName(), newValue);

        weatherThing.setAmountLimit(limitPerc);
    }
}
