package com.github.argon.sos.moreoptions.init;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricScheduler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Initializer implements InitPhases {

    @Getter(lazy = true)
    private final static Initializer instance = new Initializer(
        GameApis.getInstance(),
        ConfigStore.getInstance(),
        MetricScheduler.getInstance()
    );

    private final GameApis gameApis;
    private final ConfigStore configStore;
    private final MetricScheduler metricScheduler;

    private final static Logger log = Loggers.getLogger(Initializer.class);

    @Override
    public void initGamePresent() {
        gameApis.initGamePresent();
    }

    @Override
    public void initGameRunning() {
        gameApis.initGameRunning();
    }

    @Override
    public void initBeforeGameCreated() {
        gameApis.initBeforeGameCreated();
        configStore.initBeforeGameCreated();
    }

    @Override
    public void initCreateInstance() {
        gameApis.initCreateInstance();
        configStore.initCreateInstance();
    }

    @Override
    public void initGameSaveLoaded(MoreOptionsConfig config) {
        gameApis.initGameSaveLoaded(config);
    }

    public void crash(Throwable throwable) {
        // stop all running tasks (just in case)
        metricScheduler.stop();
    }
}
