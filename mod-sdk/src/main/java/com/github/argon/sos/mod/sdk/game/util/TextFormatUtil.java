package com.github.argon.sos.mod.sdk.game.util;

import lombok.experimental.UtilityClass;
import snake2d.util.color.ColorImp;
import util.colors.GCOLOR;
import util.gui.misc.GText;

/**
 * Utility class for transforming and formatting game texts
 */
@UtilityClass
public class TextFormatUtil {

    /**
     * Will format a text with a % sign and a - sign if negative
     *
     * @param text to add the formatted text to
     * @param value percentage value
     * @return text displayed as positive or negative percent
     */
    public static GText percentage(GText text, double value) {
        return percentage(text, value, 0);
    }

    /**
     * Will format a text with a % sign and a - sign if negative
     * @param text to add the formatted text to
     * @param value percentage value
     * @return text displayed as positive or negative percent
     */
    public static GText percentage(GText text, double value, int decimals) {
        if (!Double.isFinite(value)) {
            text.add('-').add('-').add('-');
            text.color(GCOLOR.T().INACTIVE);
            return text;
        }

        value*= 100;

        if (value < 0) {
            text.color(GCOLOR.T().IBAD);
            text.add('-');
            value = -value;
        }else if (value > 0) {
            text.color(ColorImp.TMP.interpolate(GCOLOR.T().IBAD, GCOLOR.T().IGOOD, value > 1 ? 1 : value));
        }else {
            text.color(GCOLOR.T().INACTIVE);
        }
        text.add(value, decimals, true);
        text.add('%');

        return text;
    }
}
