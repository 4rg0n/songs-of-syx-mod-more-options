package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.ConfigUtil;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricCollector;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.metric.MetricScheduler;
import game.events.EVENTS;
import init.sound.SoundAmbience;
import init.sound.SoundSettlement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import settlement.weather.WeatherThing;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * For manipulating game classes by given config {@link MoreOptionsConfig}
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MoreOptionsConfigurator {

    @Getter(lazy = true)
    private final static MoreOptionsConfigurator instance = new MoreOptionsConfigurator(
        GameApis.getInstance(),
        MetricCollector.getInstance(),
        MetricExporter.getInstance(),
        MetricScheduler.getInstance()
    );

    private final GameApis gameApis;

    private final MetricCollector metricCollector;

    private final MetricExporter metricExporter;

    private final MetricScheduler metricScheduler;

    private final static Logger log = Loggers.getLogger(MoreOptionsConfigurator.class);

    /**
     * Inject given configuration into the game
     *
     * @param config to apply
     */
    public void applyConfig(MoreOptionsConfig config) {
        log.debug("Apply More Options config to game");
        log.trace("Config: %s", config);

        try {
            applySettlementEventsConfig(config.getEvents().getSettlement());
            applyWorldEventsConfig(config.getEvents().getWorld());
            applyEventsChanceConfig(config.getEvents().getChance());
            applySoundsAmbienceConfig(config.getSounds().getAmbience());
            applySoundsSettlementConfig(config.getSounds().getSettlement());
            applySoundsRoomConfig(config.getSounds().getRoom());
            applyWeatherConfig(ConfigUtil.extract(config.getWeather()));
            applyBoostersConfig(config.getBoosters());
            applyMetrics(config.getMetrics());
        } catch (Exception e) {
            log.error("Could not apply config: %s", config, e);
        }
    }

    private void applyMetrics(MoreOptionsConfig.Metrics metrics) {
        // enabled changed?
        if (metricScheduler.isStarted() != metrics.isEnabled()) {
            if (metrics.isEnabled()) {
                metricScheduler.clear();
                metricScheduler
                    .schedule(metricCollector::buffer,
                        0, metrics.getCollectionRateSeconds().getValue(), TimeUnit.SECONDS)
                    .schedule(metricExporter::export,
                        0, metrics.getExportRateMinutes().getValue(), TimeUnit.MINUTES)
                    .start();
                metricScheduler.start();
            } else {
                metricScheduler.stop();
            }
        }

        // whitelist for stats changed?
        if (!metricCollector.getWhiteList().containsAll(metrics.getStats())) {
            metricCollector.getKeyList().clear();
            metricCollector.getKeyList().addAll(metrics.getStats());
        }
    }

    private void applyBoostersConfig(Map<String, MoreOptionsConfig.Range> rangeMap) {
        gameApis.boosterApi().setBoosters(rangeMap);
    }

    private void applyEventsChanceConfig(Map<String, MoreOptionsConfig.Range> eventsChanceConfig) {
        eventsChanceConfig.forEach((key, range) -> {
            Map<String, EVENTS.EventResource> eventsChance = gameApis.eventsApi().getEventsChance();

            if (eventsChance.containsKey(key)) {
                log.trace("Setting %s chance to %s%%", key, range.getValue());
                gameApis.eventsApi().setChance(key, range.getValue());
            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", eventsChance);
            }
        });
    }

    private void applySettlementEventsConfig(Map<String, Boolean> eventsConfig) {
        Map<String, EVENTS.EventResource> settlementEvents = gameApis.eventsApi().getSettlementEvents();

        eventsConfig.forEach((key, enabled) -> {
            if (settlementEvents.containsKey(key)) {
                EVENTS.EventResource event = settlementEvents.get(key);
                log.trace("Setting event %s enabled = %s", key, enabled);
                gameApis.eventsApi().enableEvent(event, enabled);

                if (!enabled) {
                    gameApis.eventsApi().reset(event);
                }

            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", settlementEvents);
            }
        });
    }

    private void applyWorldEventsConfig(Map<String, Boolean> eventsConfig) {
        Map<String, EVENTS.EventResource> worldEvents = gameApis.eventsApi().getWorldEvents();

        eventsConfig.forEach((key, enabled) -> {
            if (worldEvents.containsKey(key)) {
                EVENTS.EventResource event = worldEvents.get(key);
                log.trace("Setting event %s enabled = %s", event.getClass().getSimpleName(), enabled);
                gameApis.eventsApi().enableEvent(event, enabled);

                if (!enabled) {
                    gameApis.eventsApi().reset(event);
                }
            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", worldEvents);
            }
        });
    }

    private void applySoundsAmbienceConfig(Map<String, MoreOptionsConfig.Range> soundsConfig) {
        Map<String, SoundAmbience.Ambience> ambienceSounds = gameApis.soundsApi().getAmbienceSounds();

        soundsConfig.forEach((key, range) -> {
            if (ambienceSounds.containsKey(key)) {
                SoundAmbience.Ambience ambienceSound = ambienceSounds.get(key);
                gameApis.soundsApi().setSoundGainLimiter(ambienceSound, range.getValue());
            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", ambienceSounds);
            }
        });
    }

    private void applySoundsSettlementConfig(Map<String, MoreOptionsConfig.Range> soundsConfig) {
        Map<String, SoundSettlement.Sound> settlementSounds = gameApis.soundsApi().getSettlementSounds();

        soundsConfig.forEach((key, range) -> {
            if (settlementSounds.containsKey(key)) {
                SoundSettlement.Sound settlementSound = settlementSounds.get(key);
                gameApis.soundsApi().setSoundsGainLimiter(settlementSound, range.getValue());
            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", settlementSounds);
            }
        });
    }

    private void applySoundsRoomConfig(Map<String, MoreOptionsConfig.Range> soundsConfig) {
        Map<String, SoundSettlement.Sound> roomSounds = gameApis.soundsApi().getRoomSounds();

        soundsConfig.forEach((key, range) -> {
            if (roomSounds.containsKey(key)) {
                SoundSettlement.Sound roomeSound = roomSounds.get(key);
                gameApis.soundsApi().setSoundsGainLimiter(roomeSound, range.getValue());
            } else {
                log.warn("Could not find entry %s in game api result.", key);
                log.trace("API Result: %s", roomSounds);
            }
        });
    }

    private void applyWeatherConfig(Map<String, Integer> weatherConfig) {
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
