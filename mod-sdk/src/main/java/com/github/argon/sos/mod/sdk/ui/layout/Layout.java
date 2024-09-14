package com.github.argon.sos.mod.sdk.ui.layout;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import init.sprite.UI.UI;
import util.gui.misc.GText;


/**
 * TODO: PoC
 */
public class Layout {
    private final static I18nTranslator i18n = ModSdkModule.i18n().get(Layout.class);

    public final static GText NO_SPACE = new GText(UI.FONT().S, i18n.t("Layout.text.no.space")).warnify();

    public static VerticalLayout vertical(int maxHeight) {
        return new VerticalLayout(maxHeight);
    }
}
