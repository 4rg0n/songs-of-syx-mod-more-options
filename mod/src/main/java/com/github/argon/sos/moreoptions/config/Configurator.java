package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.mod.sdk.data.domain.DataUtil;
import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
import com.github.argon.sos.mod.sdk.util.MathUtil;
import com.github.argon.sos.moreoptions.config.domain.*;
import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.api.GameApis;
import com.github.argon.sos.mod.sdk.metric.MetricCollector;
import com.github.argon.sos.mod.sdk.metric.MetricExporter;
import com.github.argon.sos.mod.sdk.metric.MetricScheduler;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.moreoptions.game.api.GameBoosterApi;
import game.audio.Ambiance;
import game.events.EVENTS;
import lombok.*;
import org.jetbrains.annotations.Nullable;
import settlement.weather.WeatherThing;
import world.battle.AC_Resolver;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * For manipulating game classes by given config {@link MoreOptionsV5Config}
 */
@RequiredArgsConstructor
public class Configurator implements Phases {
    private final static Logger log = Loggers.getLogger(Configurator.class);

    private final GameApis gameApis;
    private final GameBoosterApi gameBoosterApi;
    private final MetricCollector metricCollector;
    private final MetricExporter metricExporter;
    private final MetricScheduler metricScheduler;

    @Setter
    private Level envLogLevel;
    private Set<String> lastMetricStats = new HashSet<>();
    private Action<MoreOptionsV5Config> afterApplyAction = o -> {};

    public void onAfterApplyAction(Action<MoreOptionsV5Config> afterApplyAction) {
        this.afterApplyAction = afterApplyAction;
    }

    /**
     * Inject given configuration into the game
     *
     * @param config to apply
     */
    public boolean applyConfig(@Nullable MoreOptionsV5Config config) {
        if (config == null) {
            return false;
        }

        log.debug("Apply More Options config to game");
        log.trace("Config: %s", config);

        Loggers.setLevels((envLogLevel == null) ? config.getLogLevel() : envLogLevel);
        List<Boolean> results = Lists.of(
            applyEventsConfig(config.getEvents()),
            applySoundsConfig(config.getSounds()),
            applyWeatherConfig(config.getWeather()),
            applyBoostersConfig(config.getBoosters()),
            applyMetricsConfig(config.getMetrics()),
            applyRacesConfig(config.getRaces())
        );

        try {
            afterApplyAction.accept(config);
        } catch (Exception e) {
            log.error("Error executing after apply action", e);
            return false;
        }

        // when there's at least one failed apply result, return false
        return results.stream()
            .filter(success -> !success)
            .findFirst()
            .orElse(true);
    }

    public boolean applySoundsConfig(SoundsConfig sounds) {
        try {
            Map<String, Ambiance> ambienceSounds = gameApis.sounds().getAmbienceSounds();

            sounds.getAmbience().forEach((key, range) -> {
                if (ambienceSounds.containsKey(key)) {
                    Ambiance ambienceSound = ambienceSounds.get(key);
                    gameApis.sounds().setSoundGainLimiter(ambienceSound, range.getValue());
                } else {
                    log.warn("Could not find entry %s in game api result.", key);
                    log.trace("API Result: %s", ambienceSounds);
                }
            });
        } catch (Exception e) {
            log.error("Could not apply sound ambience config to game", e);
            return false;
        }

        return true;
    }

    public boolean applyRacesConfig(RacesConfig races) {
        try {
            races.getLikings().forEach(liking -> gameApis.race().setLiking(
                liking.getRace(),
                liking.getOtherRace(),
                MathUtil.toPercentage(liking.getRange().getValue())));
        } catch (Exception e) {
            log.error("Could not apply race config to game", e);
            return false;
        }

        return true;
    }

    public boolean applyMetricsConfig(MetricsConfig metricsConfig) {
        try {
            Map<String, Boolean> metricStats = metricsConfig.getStats();
            Set<String> enabledStats = metricStats.entrySet().stream()
                .filter(Map.Entry::getValue)// is enabled?
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

            // use new file when exported stats change
            if (!enabledStats.containsAll(lastMetricStats)) {
                log.debug("New metric export stat list with %s stats", enabledStats.size());
                log.trace("Stats: %s", enabledStats);
                metricExporter.newExportFile();
                lastMetricStats = new TreeSet<>(enabledStats);
            }

            // reschedule
            if (metricScheduler.isStarted() && metricsConfig.isEnabled()) {
                log.debug("Reschedule metric collection scheduler");
                metricScheduler.stop();
                metricScheduler.clear();
                metricScheduler
                    .schedule(() -> metricCollector.buffer(enabledStats),
                        metricsConfig.getCollectionRateSeconds().getValue(), metricsConfig.getCollectionRateSeconds().getValue(), TimeUnit.SECONDS)
                    .schedule(metricExporter::export,
                        metricsConfig.getExportRateMinutes().getValue(), metricsConfig.getExportRateMinutes().getValue(), TimeUnit.MINUTES)
                    .start();
                return true;
            }

            // stop
            if (metricScheduler.isStarted()) {
                log.debug("Stop metric scheduler");
                metricScheduler.stop();
                return true;
            }

            // start
            if (metricsConfig.isEnabled()) {
                log.debug("Start metric scheduler");
                metricScheduler
                    .schedule(() -> metricCollector.buffer(enabledStats),
                        metricsConfig.getCollectionRateSeconds().getValue(), metricsConfig.getCollectionRateSeconds().getValue(), TimeUnit.SECONDS)
                    .schedule(metricExporter::export,
                        metricsConfig.getExportRateMinutes().getValue(), metricsConfig.getExportRateMinutes().getValue(), TimeUnit.MINUTES)
                    .start();
            }
        } catch (Exception e) {
            log.error("Could not apply metrics config", e);
            return false;
        }

        return true;
    }

    public boolean applyBoostersConfig(BoostersConfig boostersConfig) {
        try {
            gameBoosterApi.setBoosters(boostersConfig);
        } catch (Exception e) {
            log.error("Could not apply boosters config to game", e);
            return false;
        }

        return true;
    }

    public boolean applyEventsConfig(EventsConfig eventsConfig) {
        try {
            Map<String, EVENTS.EventResource> gameEvents = gameApis.events().getEvents();

            eventsConfig.getEvents().forEach((key, enabled) -> {
                if (gameEvents.containsKey(key)) {
                    EVENTS.EventResource event = gameEvents.get(key);
                    log.trace("Setting event %s enabled = %s", event.getClass().getSimpleName(), enabled);
                    gameApis.events().enableEvent(event, enabled);

                    if (!enabled) {
                        gameApis.events().reset(event);
                    }
                } else {
                    log.warn("Could not find entry %s in game api result.", key);
                    log.trace("API Result: %s", gameEvents);
                }
            });
        } catch (Exception e) {
            log.error("Could not apply events config to game", e);
            return false;
        }

        AC_Resolver.setEnemyLootMulti(MathUtil.toPercentage(eventsConfig.getEnemyBattleLoot().getValue()));
        AC_Resolver.setPlayerLootMulti(MathUtil.toPercentage(eventsConfig.getPlayerBattleLoot().getValue()));

        return true;
    }

    public boolean applyWeatherConfig(WeatherConfig weatherConfig) {
        try {
            Map<String, Integer> weatherEffects = DataUtil.extractValues(weatherConfig.getEffects());
            Map<String, WeatherThing> weatherThings = gameApis.weather().getWeatherThings();

            weatherEffects.forEach((key, value) -> {
                if (weatherThings.containsKey(key)) {
                    WeatherThing weatherThing = weatherThings.get(key);
                    gameApis.weather().setAmountLimit(weatherThing, value);
                } else {
                    log.warn("Could not find entry %s in game api result.", key);
                    log.trace("API Result: %s", weatherEffects);
                }
            });
        } catch (Exception e) {
            log.error("Could not apply weather config to game", e);
            return false;
        }

        return true;
    }
}