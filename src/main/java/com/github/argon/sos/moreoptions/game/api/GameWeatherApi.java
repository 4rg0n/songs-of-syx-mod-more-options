package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import game.time.TIME;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    private Map<String, WeatherThing> weatherThings;

    public Map<String, WeatherThing> getWeatherThings() {
        if (weatherThings == null) {
            weatherThings = new HashMap<>();
            SWEATHER gameWeather = SETT.WEATHER();

            weatherThings.put("weather.thunder", gameWeather.thunder);
            weatherThings.put("weather.ice", gameWeather.ice);
            weatherThings.put("weather.rain", gameWeather.rain);
            weatherThings.put("weather.clouds", gameWeather.clouds);
            weatherThings.put("weather.snow", gameWeather.snow);
            weatherThings.put("weather.moisture", gameWeather.moisture);
            weatherThings.put("weather.wind", gameWeather.wind);
            weatherThings.put("weather.growth", gameWeather.growth);
            weatherThings.put("weather.growthRipe", gameWeather.growthRipe);
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

    public void lockDayCycle(int cycle, boolean lock) {
        if (cycle > 6 || cycle < 0) {
            log.warn("Invalid day cycle %s. Must be bigger than 0 and lesser than 6", cycle);
            return;
        }

        TIME.light().sun().setCycleLock(cycle);
        TIME.light().sun().setLocked(lock);

        TIME.light().moon().setCycleLock(cycle);
        TIME.light().moon().setLocked(lock);
    }

    public void unlockDayCycle() {
        TIME.light().sun().setCycleLock(0);
        TIME.light().sun().setLocked(false);

        TIME.light().moon().setCycleLock(0);
        TIME.light().moon().setLocked(false);
    }

    public void setAmountLimit(WeatherThing weatherThing, int percentage) {
        double currentValue = weatherThing.getD();
        double limitPerc = MathUtil.toPercentage(percentage);
        double newValue = currentValue * limitPerc;

        log.trace("Applying amount limit %s%% to %s = %s", percentage, weatherThing.getClass().getSimpleName(), newValue);

        weatherThing.setAmountLimit(limitPerc);
    }
}
