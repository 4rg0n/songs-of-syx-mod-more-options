package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
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

    private final static Logger log = Loggers.getLogger(GameWeatherApi.class);

    @Getter(lazy = true)
    private final static GameWeatherApi instance = new GameWeatherApi();

    public Map<String, WeatherThing> getWeatherThings() {
        Map<String, WeatherThing> weatherThings = new HashMap<>();
        SWEATHER gameWeather = SETT.WEATHER();

        weatherThings.put("thunder", gameWeather.thunder);
        weatherThings.put("ice", gameWeather.ice);
        weatherThings.put("rain", gameWeather.rain);
        weatherThings.put("clouds", gameWeather.clouds);
        weatherThings.put("snow", gameWeather.snow);
        weatherThings.put("moisture", gameWeather.moisture);
        weatherThings.put("wind", gameWeather.wind);
        weatherThings.put("growth", gameWeather.growth);
        weatherThings.put("growthRipe", gameWeather.growthRipe);

        return weatherThings;
    }
}
