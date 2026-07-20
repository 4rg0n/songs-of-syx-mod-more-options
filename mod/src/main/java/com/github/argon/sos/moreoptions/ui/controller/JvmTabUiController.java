package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.mod.sdk.game.jvm.JvmArgs;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.moreoptions.ui.tab.jvm.JvmTab;

import java.util.List;
import java.util.function.Consumer;

public class JvmTabUiController extends AbstractUiUiController<JvmTab> {

    private final static Logger log = Loggers.getLogger(JvmTabUiController.class);

    public JvmTabUiController(JvmTab jvmTab) {
        super(jvmTab);

        jvmTab.getMemoryMinSlider().valueChangeAction(minMemoryChange());
        jvmTab.getMemoryMaxSlider().valueChangeAction(maxMemoryChange());
    }

    public Consumer<Integer> minMemoryChange() {
        return value -> {
            String jvmArgsString = getElement().getJvmArgsInputArea().getValue();
            try {
                JvmArgs jvmArgs = JvmArgs.fromString(jvmArgsString);
                List<JvmArgs.JvmArg> xmsArgs = jvmArgs.getByValueContains("-Xms");
                String valueString = "-Xms" + value + "m";

                if (xmsArgs.isEmpty()) {
                    jvmArgs.addArg(null, valueString);
                } else {
                    JvmArgs.JvmArg jvmArg = xmsArgs.getLast();
                    jvmArg.setValue(valueString);
                }

                getElement().getJvmArgsInputArea().setValue(jvmArgs.toString());
            } catch (Exception e) {
                log.error("Error while updating jvm args from min memory slider.", e);
            }
        };
    }

    public Consumer<Integer> maxMemoryChange() {
        return value -> {
            String jvmArgsString = getElement().getJvmArgsInputArea().getValue();
            try {
                JvmArgs jvmArgs = JvmArgs.fromString(jvmArgsString);
                List<JvmArgs.JvmArg> xmsArgs = jvmArgs.getByValueContains("-Xmx");
                String valueString = "-Xmx" + value + "m";

                if (xmsArgs.isEmpty()) {
                    jvmArgs.addArg(null, valueString);
                } else {
                    JvmArgs.JvmArg jvmArg = xmsArgs.getLast();
                    jvmArg.setValue(valueString);
                }

                getElement().getJvmArgsInputArea().setValue(jvmArgs.toString());
            } catch (Exception e) {
                log.error("Error while updating jvm args from max memory slider.", e);
            }
        };
    }
}
