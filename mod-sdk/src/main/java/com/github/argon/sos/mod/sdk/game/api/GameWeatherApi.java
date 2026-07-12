package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.MathUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import settlement.main.SETT;
import settlement.weather.SWEATHER;
import settlement.weather.WeatherThing;

import java.util.HashMap;
import java.util.Map;

/**
 * For interacting with the game weather effects.
 */
@RequiredArgsConstructor
public class GameWeatherApi implements Resettable {

    private final Logger log = Loggers.getLogger(GameWeatherApi.class);

    /**
     * Used as prefix for weather keys to identify them as weather.
     */
    public final static String KEY_PREFIX = "weather";

    @Nullable
    private Map<String, WeatherThing> weatherThings;

    /**
     * Returns a map with the weather key and the corresponding {@link WeatherThing}s.
     *
     * @return map of all weather things
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        weatherThings = null;
    }

    /**
     * Set how many effects are displayed for a given {@link WeatherThing}.
     *
     * @param weatherThing to set the amount for
     * @param percentage to display
     */
    public void setAmountLimit(WeatherThing weatherThing, int percentage) {
        double currentValue = weatherThing.getD();
        double limitPerc = MathUtil.toPercentage(percentage);
        double newValue = currentValue * limitPerc;

        log.trace("Applying amount limit %s%% to %s = %s", percentage, weatherThing.getClass().getSimpleName(), newValue);

        weatherThing.setAmountLimit(limitPerc);
    }
}
