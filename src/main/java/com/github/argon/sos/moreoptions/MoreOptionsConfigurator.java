package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.ConfigUtil;
import com.github.argon.sos.moreoptions.config.domain.*;
import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricCollector;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.metric.MetricScheduler;
import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.MathUtil;
import game.events.EVENTS;
import init.sound.SoundAmbience;
import init.sound.SoundSettlement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import settlement.weather.WeatherThing;
import world.battle.AC_Resolver;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * For manipulating game classes by given config {@link MoreOptionsV3Config}
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MoreOptionsConfigurator implements Phases {

    @Getter(lazy = true)
    private final static MoreOptionsConfigurator instance = new MoreOptionsConfigurator(
        GameApis.getInstance(),
        MetricCollector.getInstance(),
        MetricExporter.getInstance(),
        MetricScheduler.getInstance()
    );

    private final static Logger log = Loggers.getLogger(MoreOptionsConfigurator.class);

    private final GameApis gameApis;

    private final MetricCollector metricCollector;

    private final MetricExporter metricExporter;

    private final MetricScheduler metricScheduler;

    @Setter
    private Level envLogLevel;
    private Set<String> lastMetricStats = new HashSet<>();
    private Action<MoreOptionsV3Config> afterApplyAction = o -> {};

    public void onAfterApplyAction(Action<MoreOptionsV3Config> afterApplyAction) {
        this.afterApplyAction = afterApplyAction;
    }

    /**
     * Inject given configuration into the game
     *
     * @param config to apply
     */
    public boolean applyConfig(@Nullable MoreOptionsV3Config config) {
        if (config == null) {
            return false;
        }

        log.debug("Apply More Options config to game");
        log.trace("Config: %s", config);

        Loggers.setLevels((envLogLevel == null) ? config.getLogLevel() : envLogLevel);
        List<Boolean> results = Lists.of(
            applyEventsBattleTribute(config.getEvents()),
            applyWorldEventsConfig(config.getEvents().getWorld()),
            applyEventsChanceConfig(config.getEvents().getChance()),
            applySoundsAmbienceConfig(config.getSounds().getAmbience()),
            applySoundsSettlementConfig(config.getSounds().getSettlement()),
            applySoundsRoomConfig(config.getSounds().getRoom()),
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

    private boolean applyEventsBattleTribute(EventsConfig config) {
        AC_Resolver.setEnemyLootMulti(MathUtil.toPercentage(config.getEnemyBattleLoot().getValue()));
        AC_Resolver.setPlayerLootMulti(MathUtil.toPercentage(config.getPlayerBattleLoot().getValue()));
        return true;
    }

    private boolean applyRacesConfig(RacesConfig races) {
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

    private boolean applyMetricsConfig(MetricsConfig metricsConfig) {
        try {
            Set<String> metricStats = metricsConfig.getStats();

            // use new file when exported stats change
            if (!metricStats.containsAll(lastMetricStats)) {
                log.debug("New metric export stat list with %s stats", metricStats.size());
                log.trace("Stats: %s", metricStats);
                metricExporter.newExportFile();
                lastMetricStats = new TreeSet<>(metricStats);
            }

            // reschedule
            if (metricScheduler.isStarted() && metricsConfig.isEnabled()) {
                log.debug("Reschedule metric collection scheduler");
                metricScheduler.stop();
                metricScheduler.clear();
                metricScheduler
                    .schedule(() -> metricCollector.buffer(metricStats),
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
                    .schedule(() -> metricCollector.buffer(metricStats),
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

    private boolean applyBoostersConfig(BoostersConfig boostersConfig) {
        try {
            gameApis.booster().setBoosters(boostersConfig);
        } catch (Exception e) {
            log.error("Could not apply boosters config to game", e);
            return false;
        }

        return true;
    }

    private boolean applyEventsChanceConfig(Map<String, Range> eventsChanceConfig) {
        try {
            eventsChanceConfig.forEach((key, range) -> {
                Map<String, EVENTS.EventResource> eventsChance = gameApis.events().getEventsChance();

                if (eventsChance.containsKey(key)) {
                    log.trace("Setting %s chance to %s%%", key, range.getValue());
                    gameApis.events().setChance(key, range.getValue());
                } else {
                    log.warn("Could not find entry %s in game api result.", key);
                    log.trace("API Result: %s", eventsChance);
                }
            });
        } catch (Exception e) {
            log.error("Could not apply event chances game", e);
            return false;
        }

        return true;
    }

    private boolean applySettlementEventsConfig(Map<String, Boolean> eventsConfig) {
        try {
            Map<String, EVENTS.EventResource> settlementEvents = gameApis.events().getSettlementEvents();

            eventsConfig.forEach((key, enabled) -> {
                if (settlementEvents.containsKey(key)) {
                    EVENTS.EventResource event = settlementEvents.get(key);
                    log.trace("Setting event %s enabled = %s", key, enabled);
                    gameApis.events().enableEvent(event, enabled);

                    if (!enabled) {
                        gameApis.events().reset(event);
                    }

                } else {
                    log.warn("Could not find entry %s in game api result.", key);
                    log.trace("API Result: %s", settlementEvents);
                }
            });
        } catch (Exception e) {
            log.error("Could not apply settlement events config to game", e);
            return false;
        }

        return true;
    }

    private boolean applyWorldEventsConfig(Map<String, Boolean> eventsConfig) {
        try {
            Map<String, EVENTS.EventResource> worldEvents = gameApis.events().getWorldEvents();

            eventsConfig.forEach((key, enabled) -> {
                if (worldEvents.containsKey(key)) {
                    EVENTS.EventResource event = worldEvents.get(key);
                    log.trace("Setting event %s enabled = %s", event.getClass().getSimpleName(), enabled);
                    gameApis.events().enableEvent(event, enabled);

                    if (!enabled) {
                        gameApis.events().reset(event);
                    }
                } else {
                    log.warn("Could not find entry %s in game api result.", key);
                    log.trace("API Result: %s", worldEvents);
                }
            });
        } catch (Exception e) {
            log.error("Could not apply world events config to game", e);
            return false;
        }

        return true;
    }

    private boolean applySoundsAmbienceConfig(Map<String, Range> soundsConfig) {
        try {
            Map<String, SoundAmbience.Ambience> ambienceSounds = gameApis.sounds().getAmbienceSounds();

            soundsConfig.forEach((key, range) -> {
                if (ambienceSounds.containsKey(key)) {
                    SoundAmbience.Ambience ambienceSound = ambienceSounds.get(key);
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

    private boolean applySoundsSettlementConfig(Map<String, Range> soundsConfig) {
        try {
            Map<String, SoundSettlement.Sound> settlementSounds = gameApis.sounds().getSettlementSounds();

            soundsConfig.forEach((key, range) -> {
                if (settlementSounds.containsKey(key)) {
                    SoundSettlement.Sound settlementSound = settlementSounds.get(key);
                    gameApis.sounds().setSoundsGainLimiter(settlementSound, range.getValue());
                } else {
                    log.warn("Could not find entry %s in game api result.", key);
                    log.trace("API Result: %s", settlementSounds);
                }
            });
        } catch (Exception e) {
            log.error("Could not apply sounds settlement config to game", e);
            return false;
        }

        return true;
    }

    private boolean applySoundsRoomConfig(Map<String, Range> soundsConfig) {
        try {
            Map<String, SoundSettlement.Sound> roomSounds = gameApis.sounds().getRoomSounds();

            soundsConfig.forEach((key, range) -> {
                if (roomSounds.containsKey(key)) {
                    SoundSettlement.Sound roomeSound = roomSounds.get(key);
                    gameApis.sounds().setSoundsGainLimiter(roomeSound, range.getValue());
                } else {
                    log.warn("Could not find entry %s in game api result.", key);
                    log.trace("API Result: %s", roomSounds);
                }
            });
        } catch (Exception e) {
            log.error("Could not apply sounds room config to game", e);
            return false;
        }

        return true;
    }

    private boolean applyWeatherConfig(WeatherConfig weatherConfig) {
        try {
            Map<String, Integer> weatherEffects = ConfigUtil.extractValues(weatherConfig.getEffects());
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
