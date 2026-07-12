package com.github.argon.sos.mod.sdk.ui.layout;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import init.sprite.UI.UI;
import util.gui.misc.GText;


/**
 * For aligning ui elements.
 */
public class Layout {

    /**
     * Creates a new {@link Layout}.
     */
    public Layout() {
    }

    private final static I18nTranslator i18n = ModSdkModule.i18n().get(Layout.class);

    /**
     * Ui element shown when there's not enough space to fit something.
     */
    public final static GText NO_SPACE = new GText(UI.FONT().S, i18n.t("Layout.text.no.space")).warnify();

    /**
     * Creates a new {@link VerticalLayout} with given max height.
     *
     * @param maxHeight in pixels
     * @return created vertical layout
     */
    public static VerticalLayout vertical(int maxHeight) {
        return new VerticalLayout(maxHeight);
    }
}
