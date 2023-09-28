package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
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
        applySettlementEventsConfig(config.getEventsSettlement());
        applyWorldEventsConfig(config.getEventsWorld());
        applyEventsChanceConfig(config.getEventsChance());
        applySoundsAmbienceConfig(config.getSoundsAmbience());
        applySoundsSettlementConfig(config.getSoundsSettlement());
        applySoundsRoomConfig(config.getSoundsRoom());
        applyWeatherConfig(config.getWeather());
        applyBoostersConfig(config.getBoosters());
    }

    private void applyBoostersConfig(Map<String, Integer> boostersConfig) {
        log.trace("Apply boosters config: %s", boostersConfig);
        boostersConfig.forEach((key, boost) -> {
            Map<String, BOOSTABLE> boostables = gameApis.boosterApi().getBoosters();

            if (boostables.containsKey(key)) {
                BOOSTABLE boostable = boostables.get(key);
                double currentValue = boostable.defAdd;
                double newValue = currentValue * MathUtil.toPercentage(boost);

                log.trace("Applying %s%% to %s = %s", boost, key, newValue);
                boostable.setDefAdd(newValue);

            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", boostables);
            }
        });
    }

    private void applyEventsChanceConfig(Map<String, Integer> eventsChanceConfig) {
        log.trace("Apply events chance config: %s", eventsChanceConfig);
        eventsChanceConfig.forEach((key, chance) -> {
            Map<String, EVENTS.EventResource> eventsChance = gameApis.eventsApi().getEventsChance();

            if (eventsChance.containsKey(key)) {
                EVENTS.EventResource event = eventsChance.get(key);
                log.trace("Setting %s chance to %s%%", key, chance);
                gameApis.eventsApi().setChance(event, chance);
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
                double limitPerc = MathUtil.toPercentage(limit);

                log.trace("Applying %s%% volume for %s", limit, key);
                ambienceSounds.get(key).setGainLimiter(limitPerc);
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
                double limitPerc = MathUtil.toPercentage(limit);

                log.trace("Applying %s%% volume for %s", limit, key);
                settlementSounds.get(key).setGainLimiter(limitPerc);
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
                double limitPerc = MathUtil.toPercentage(limit);

                log.trace("Applying %s%% volume for %s", limit, key);
                roomSounds.get(key).setGainLimiter(limitPerc);
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

                double currentValue = weatherThing.getD();
                double newValue = currentValue * MathUtil.toPercentage(value);

                log.trace("Applying %s%% to %s = %s", value, key, newValue);

                weatherThing.setD(newValue);
            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", weatherConfig);
            }
        });
    }
}
