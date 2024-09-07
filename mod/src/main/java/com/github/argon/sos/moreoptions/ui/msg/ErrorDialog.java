package com.github.argon.sos.moreoptions.ui.msg;

import com.github.argon.sos.mod.sdk.game.ui.Button;
import com.github.argon.sos.mod.sdk.game.ui.HorizontalLine;
import com.github.argon.sos.mod.sdk.i18n.I18n;
import init.sprite.UI.UI;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;

public class ErrorDialog extends GuiSection {

    private final static I18n i18n = I18n.get(ErrorDialog.class);

    @Getter
    private Button copyButton;
    @Getter
    private Button reportButton;
    @Getter
    private Button closeButton;
    @Getter
    private final Throwable exception;
    @Getter
    @Nullable
    private final String errorMessage;

    public ErrorDialog(Throwable exception, @Nullable String errorMessage) {
        this.exception = exception;
        this.errorMessage = errorMessage;

        GText text1 = new GText(UI.FONT().M, i18n.t("ErrorDialog.text.line1"));
        GText text2 = new GText(UI.FONT().M, i18n.t("ErrorDialog.text.line2"));
        GText exceptionMessage = new GText(UI.FONT().M, exception.getMessage()).errorify();

        addDownC(0, text1);
        addDownC(5, text2);

        if (errorMessage != null) {
            addDownC(10, new GText(UI.FONT().M, errorMessage).warnify());
        }

        addDownC(10, exceptionMessage);
        GuiSection buttons = buttons();

        addDownC(10, new HorizontalLine(buttons.body().width(), 14, 1));
        addDownC(10, buttons);
    }
    private GuiSection buttons() {
        GuiSection section = new GuiSection();

        this.copyButton = new Button(i18n.t("ErrorDialog.button.copy.name"), COLOR.WHITE15,
            i18n.t("ErrorDialog.button.copy.desc"));
        section.addRight(0, copyButton);

        this.reportButton = new Button(i18n.t("ErrorDialog.button.report.name"), COLOR.WHITE15,
            i18n.t("ErrorDialog.button.report.desc"));
        section.addRight(20, reportButton);

        this.closeButton = new Button(i18n.t("ErrorDialog.button.close.name"), COLOR.WHITE15,
            i18n.t("ErrorDialog.button.close.desc"));
        section.addRight(20, closeButton);

        return section;
    }
}
