package com.github.argon.sos.moreoptions.game.api;

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
}
