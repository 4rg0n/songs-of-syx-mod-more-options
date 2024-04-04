package com.github.argon.sos.moreoptions.game.ui.json;

import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.json.element.JsonDouble;

public class JsonUiFactory {
    public static Slider slider(JsonDouble jsonDouble) {
        return slider(jsonDouble, -1, 1);
    }

    public static Slider slider(JsonDouble jsonDouble, double min, double max) {
        Double value = jsonDouble.getValue();
        return Slider.builder()
            .min((int) (min * 100))
            .max((int) (max * 100))
            .value((int) (value * 100))
            .input(true)
            .build();
    }
}
