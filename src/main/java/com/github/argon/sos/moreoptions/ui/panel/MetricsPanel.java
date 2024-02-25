package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Toggler;
import com.github.argon.sos.moreoptions.game.ui.Valuable;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.element.LabelBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.LabeledBuilder;
import com.github.argon.sos.moreoptions.util.Lists;
import snake2d.util.gui.GuiSection;

import java.util.List;

/**
 * Contains control elements for enabling and disabling game events.
 */
public class MetricsPanel extends GuiSection implements Valuable<Void> {

    private static final Logger log = Loggers.getLogger(MetricsPanel.class);


    public MetricsPanel(
        MoreOptionsConfig.Metrics metricsConfig
    ) {
        Toggler<Boolean> toggler = new Toggler<>(Lists.of(
            Toggler.Info.<Boolean>builder()
                .key(true)
                .title("Started")
                .description("")
                .build(),
            Toggler.Info.<Boolean>builder()
                .key(false)
                .title("Stopped")
                .description("")
                .build()
        ));

        BuildResult<List<GuiSection>, Toggler<Boolean>> buildResult = LabeledBuilder.<Toggler<Boolean>>builder().translate(
            LabeledBuilder.Definition.<Toggler<Boolean>>builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    .key("TODO")
                    .title("Metric Export")
                    .description("Exports")
                    .build())
                .element(toggler)
                .build()
        ).build();

        


    }

    public MoreOptionsConfig.Metrics getConfig() {
        return null;
    }

    public void applyConfig(MoreOptionsConfig.Metrics metricsConfig) {

    }


    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public void setValue(Void value) {

    }
}
