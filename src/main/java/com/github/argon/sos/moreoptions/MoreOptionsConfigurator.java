package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.ConfigUtil;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import game.events.EVENTS;
import init.boostable.BOOSTABLE;
import init.sound.SoundAmbience;
import init.sound.SoundSettlement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import settlement.weather.WeatherThing;

import java.util.Map;

/**
 * For manipulating game classes by given config {@link MoreOptionsConfig}
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MoreOptionsConfigurator {

    @Getter(lazy = true)
    private final static MoreOptionsConfigurator instance = new MoreOptionsConfigurator(
        GameApis.getInstance()
    );

    private final GameApis gameApis;

    private final static Logger log = Loggers.getLogger(MoreOptionsConfigurator.class);

    /**
     * Inject given configuration into the game
     *
     * @param config to apply
     */
    public void applyConfig(MoreOptionsConfig config) {
        log.debug("Apply More Options config to game");
        try {
            applyFactionWarAdd(config.getFactionWarAdd().getValue());
            applySettlementEventsConfig(config.getEventsSettlement());
            applyWorldEventsConfig(config.getEventsWorld());
            applyEventsChanceConfig(ConfigUtil.extract(config.getEventsChance()));
            applySoundsAmbienceConfig(ConfigUtil.extract(config.getSoundsAmbience()));
            applySoundsSettlementConfig(ConfigUtil.extract(config.getSoundsSettlement()));
            applySoundsRoomConfig(ConfigUtil.extract(config.getSoundsRoom()));
            applyWeatherConfig(ConfigUtil.extract(config.getWeather()));
            applyBoostersConfig(ConfigUtil.extract(config.getBoosters()));
        } catch (Exception e) {
            log.error("Could not apply config: %s", config, e);
        }
    }

    private void applyFactionWarAdd(Integer value) {
        gameApis.eventsApi().setFactionWarAddValue(value);
    }

    private void applyBoostersConfig(Map<String, Integer> boostersConfig) {
        log.trace("Apply boosters config: %s", boostersConfig);
        boostersConfig.forEach((key, boost) -> {
            Map<String, BOOSTABLE> boostables = gameApis.boosterApi().getAllBoosters();

            if (boostables.containsKey(key)) {
                BOOSTABLE boostable = boostables.get(key);
                gameApis.boosterApi().setBoosterValue(boostable, boost);
            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", boostables);
            }
        });
    }

    private void applyEventsChanceConfig(Map<String, Integer> eventsChanceConfig) {
        log.trace("Apply events chance config: %s", eventsChanceConfig);
        eventsChanceConfig.forEach((key, value) -> {
            Map<String, EVENTS.EventResource> eventsChance = gameApis.eventsApi().getEventsChance();

            if (eventsChance.containsKey(key)) {
                log.trace("Setting %s chance to %s%%", key, value);
                gameApis.eventsApi().setChance(key, value);
            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", eventsChance);
            }
        });
    }

    private void applySettlementEventsConfig(Map<String, Boolean> eventsConfig) {
        log.trace("Apply settlement events config: %s", eventsConfig);
        Map<String, EVENTS.EventResource> settlementEvents = gameApis.eventsApi().getSettlementEvents();

        eventsConfig.forEach((key, enabled) -> {
            if (settlementEvents.containsKey(key)) {
                EVENTS.EventResource event = settlementEvents.get(key);
                log.trace("Setting event %s enabled = %s", key, enabled);
                gameApis.eventsApi().enableEvent(event, enabled);

                if (enabled == false) {
                    gameApis.eventsApi().reset(event);
                }

            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", settlementEvents);
            }
        });
    }

    private void applyWorldEventsConfig(Map<String, Boolean> eventsConfig) {
        log.trace("Apply world events config: %s", eventsConfig);
        Map<String, EVENTS.EventResource> worldEvents = gameApis.eventsApi().getWorldEvents();

        eventsConfig.forEach((key, enabled) -> {
            if (worldEvents.containsKey(key)) {
                EVENTS.EventResource event = worldEvents.get(key);
                log.trace("Setting event %s enabled = %s", event.getClass().getSimpleName(), enabled);
                gameApis.eventsApi().enableEvent(event, enabled);

                if (enabled == false) {
                    gameApis.eventsApi().reset(event);
                }
            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", worldEvents);
            }
        });
    }

    private void applySoundsAmbienceConfig(Map<String, Integer> soundsAmbienceConfig) {
        log.trace("Apply ambience sounds config: %s", soundsAmbienceConfig);
        Map<String, SoundAmbience.Ambience> ambienceSounds = gameApis.soundsApi().getAmbienceSounds();

        soundsAmbienceConfig.forEach((key, limit) -> {
            if (ambienceSounds.containsKey(key)) {
                SoundAmbience.Ambience ambienceSound = ambienceSounds.get(key);
                gameApis.soundsApi().setSoundGainLimiter(ambienceSound, limit);
            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", ambienceSounds);
            }
        });
    }

    private void applySoundsSettlementConfig(Map<String, Integer> soundsSettlementConfig) {
        log.trace("Apply settlement sounds config: %s", soundsSettlementConfig);
        Map<String, SoundSettlement.Sound> settlementSounds = gameApis.soundsApi().getSettlementSounds();

        soundsSettlementConfig.forEach((key, limit) -> {
            if (settlementSounds.containsKey(key)) {
                SoundSettlement.Sound settlementSound = settlementSounds.get(key);
                gameApis.soundsApi().setSoundsGainLimiter(settlementSound, limit);
            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", settlementSounds);
            }
        });
    }

    private void applySoundsRoomConfig(Map<String, Integer> soundsRoomConfig) {
        log.trace("Apply room sounds config: %s", soundsRoomConfig);
        Map<String, SoundSettlement.Sound> roomSounds = gameApis.soundsApi().getRoomSounds();

        soundsRoomConfig.forEach((key, limit) -> {
            if (roomSounds.containsKey(key)) {
                SoundSettlement.Sound roomeSound = roomSounds.get(key);
                gameApis.soundsApi().setSoundsGainLimiter(roomeSound, limit);
            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", roomSounds);
            }
        });
    }

    private void applyWeatherConfig(Map<String, Integer> weatherConfig) {
        log.trace("Apply weather config: %s", weatherConfig);

        Map<String, WeatherThing> weatherThings = gameApis.weatherApi().getWeatherThings();

        weatherConfig.forEach((key, value) -> {
            if (weatherThings.containsKey(key)) {
                WeatherThing weatherThing = weatherThings.get(key);
                gameApis.weatherApi().setAmountLimit(weatherThing, value);
            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", weatherConfig);
            }
        });
    }
}
