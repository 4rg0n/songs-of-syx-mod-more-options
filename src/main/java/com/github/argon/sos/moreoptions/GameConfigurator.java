package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import game.GAME;
import game.events.EVENTS;
import init.sound.SOUND;
import init.sound.SoundAmbience;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import settlement.main.SETT;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameConfigurator {

    @Getter(lazy = true)
    private final static GameConfigurator instance = new GameConfigurator();

    private final static Logger log = Loggers.getLogger(GameConfigurator.class);

    public void applyConfig(MoreOptionsConfig config) {
        applyEventsConfig(config.getEvents());
        applySoundsConfig(config.getSounds());
        applyParticlesConfig(config.getWeather());
    }

    private void applyEventsConfig(MoreOptionsConfig.Events eventsConfig) {
        log.trace("Configure events: %s", eventsConfig);
        EVENTS events = GAME.events();

        // settlement events
        enableEvent(events.accident, eventsConfig.isAccident());
        enableEvent(events.advice, eventsConfig.isAdvice());
        enableEvent(events.disease, eventsConfig.isDisease());
        enableEvent(events.farm, eventsConfig.isFarm());
        enableEvent(events.fish, eventsConfig.isFish());
        enableEvent(events.killer, eventsConfig.isKiller());
        enableEvent(events.orchard, eventsConfig.isOrchard());
        enableEvent(events.pasture, eventsConfig.isPasture());
        enableEvent(events.raceWars, eventsConfig.isRaceWars());
        enableEvent(events.riot, eventsConfig.isRiot());
        enableEvent(events.slaver, eventsConfig.isSlaver());
        enableEvent(events.temperature, eventsConfig.isTemperature());
        enableEvent(events.uprising, eventsConfig.isUprising());

        // world events
        enableEvent(events.world.factionExpand, eventsConfig.isWorldFactionExpand());
        enableEvent(events.world.factionBreak, eventsConfig.isWorldFactionBreak());
        enableEvent(events.world.popup, eventsConfig.isWorldPopup());
        enableEvent(events.world.war, eventsConfig.isWorldWar());
        enableEvent(events.world.warPlayer, eventsConfig.isWorldWarPlayer());
        enableEvent(events.world.raider, eventsConfig.isWorldRaider());
        enableEvent(events.world.rebellion, eventsConfig.isWorldRebellion());
        enableEvent(events.world.plague, eventsConfig.isWorldPlague());
    }

    private void applySoundsConfig(MoreOptionsConfig.Sounds soundsConfig) {
        log.trace("Configure sounds: %s", soundsConfig);
        SoundAmbience soundAmbience = SOUND.ambience();

        soundAmbience.nature.setLimiter(soundsConfig.getNature());
        soundAmbience.night.setLimiter(soundsConfig.getNight());
        soundAmbience.rain.setLimiter(soundsConfig.getRain());
        soundAmbience.wind.setLimiter(soundsConfig.getWind());
        soundAmbience.thunder.setLimiter(soundsConfig.getThunder());
        soundAmbience.water.setLimiter(soundsConfig.getWater());
        soundAmbience.windhowl.setLimiter(soundsConfig.getWindHowl());
        soundAmbience.windTrees.setLimiter(soundsConfig.getWindTrees());
    }

    private void applyParticlesConfig(MoreOptionsConfig.Weather weatherConfig) {
        log.trace("Configure weather: %s", weatherConfig);

        SETT.WEATHER().rain.setLimiter(weatherConfig.getRain());
        SETT.WEATHER().snow.setLimiter(weatherConfig.getSnow());
        SETT.WEATHER().ice.setLimiter(weatherConfig.getIce());
        SETT.WEATHER().clouds.setLimiter(weatherConfig.getClouds());
        SETT.WEATHER().thunder.setLimiter(weatherConfig.getThunder());
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
