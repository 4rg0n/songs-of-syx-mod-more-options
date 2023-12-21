package com.github.argon.sos.moreoptions.game.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import snake2d.util.color.ColorImp;
import util.colors.GCOLOR;
import util.gui.misc.GText;
import util.info.GFORMAT;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextFormatUtil {

    public static GText perc(GText text, double f) {
        return perc(text, f, 0);
    }


    public static GText perc(GText text, double f, int decimals) {
        if (!Double.isFinite(f)) {
            text.add('-').add('-').add('-');
            text.color(GCOLOR.T().INACTIVE);
            return text;
        }

        f*= 100;

        if (f < 0) {
            text.color(GCOLOR.T().IBAD);
            text.add('-');
            f = -f;
        }else if (f > 0) {
            text.color(ColorImp.TMP.interpolate(GCOLOR.T().IBAD, GCOLOR.T().IGOOD, f > 1 ? 1 : f));
        }else {
            text.color(GCOLOR.T().INACTIVE);
        }
        text.add(f, decimals, true);
        text.add('%');

        return text;
    }

    public static GText iBig(GText text, long i) {
        return GFORMAT.iBig(text, i);
    }
}
