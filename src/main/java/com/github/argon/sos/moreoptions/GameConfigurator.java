package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.api.GameEventsApi;
import com.github.argon.sos.moreoptions.game.api.GameSoundsApi;
import com.github.argon.sos.moreoptions.game.api.GameWeatherApi;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import game.events.EVENTS;
import init.sound.SoundAmbience;
import init.sound.SoundSettlement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import settlement.weather.WeatherThing;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameConfigurator {

    @Getter(lazy = true)
    private final static GameConfigurator instance = new GameConfigurator(
        GameEventsApi.getInstance(),
        GameSoundsApi.getInstance(),
        GameWeatherApi.getInstance()
    );

    private final GameEventsApi gameEventsApi;

    private final GameSoundsApi gameSoundsApi;

    private final GameWeatherApi gameWeatherApi;

    private final static Logger log = Loggers.getLogger(GameConfigurator.class);

    public void applyConfig(MoreOptionsConfig config) {
        applySettlementEventsConfig(config.getEventsSettlement());
        applyWorldEventsConfig(config.getEventsWorld());
        applySoundsAmbienceConfig(config.getSoundsAmbience());
        applySoundsSettlementConfig(config.getSoundsSettlement());
        applyWeatherConfig(config.getWeather());
    }

    private void applySettlementEventsConfig(Map<String, Boolean> eventsConfig) {
        log.trace("Configure settlement events: %s", eventsConfig);
        Map<String, EVENTS.EventResource> settlementEvents = gameEventsApi.getSettlementEvents();

        eventsConfig.forEach((key, value) -> {
            enableEvent(settlementEvents.get(key), value);
        });
    }

    private void applyWorldEventsConfig(Map<String, Boolean> eventsConfig) {
        log.trace("Configure world events: %s", eventsConfig);
        Map<String, EVENTS.EventResource> worldEvents = gameEventsApi.getWorldEvents();

        eventsConfig.forEach((key, value) -> {
            enableEvent(worldEvents.get(key), value);
        });
    }

    private void applySoundsAmbienceConfig(Map<String, Integer> soundsAmbienceConfig) {
        log.trace("Configure ambience sounds: %s", soundsAmbienceConfig);
        Map<String, SoundAmbience.Ambience> ambienceSounds = gameSoundsApi.getAmbienceSounds();

        soundsAmbienceConfig.forEach((key, value) -> {
            ambienceSounds.get(key).setLimiter(value);
        });
    }

    private void applySoundsSettlementConfig(Map<String, Integer> soundsSettlementConfig) {
        log.trace("Configure settlement sounds: %s", soundsSettlementConfig);
        Map<String, SoundSettlement.Sound> settlementSounds = gameSoundsApi.getSettlementSounds();

        soundsSettlementConfig.forEach((key, value) -> {
            settlementSounds.get(key).setLimiter(value);
        });
    }

    private void applyWeatherConfig(Map<String, Integer> weatherConfig) {
        log.trace("Configure weather: %s", weatherConfig);

        Map<String, WeatherThing> weatherThings = gameWeatherApi.getWeatherThings();

        weatherConfig.forEach((key, value) -> {
            weatherThings.get(key).setLimiter(value);
        });
    }

    private void enableEvent(EVENTS.EventResource event, Boolean enabled) {
        ReflectionUtil.getDeclaredField("supress", EVENTS.EventResource.class).ifPresent(field -> {
            try {
                ReflectionUtil.setField(field, event, !enabled);
            } catch (Exception e) {
                log.warn("Could not set event %s to enabled = %s", event.getClass().getSimpleName(), enabled, e);
            }
        });
    }
}
