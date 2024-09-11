package com.github.argon.sos.moreoptions.ui.msg;

import com.github.argon.sos.mod.sdk.ui.Notificator;
import com.github.argon.sos.mod.sdk.game.ui.Window;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.util.ExceptionUtil;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.ui.UiFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@RequiredArgsConstructor
public class Messages {
    private static final I18nTranslator i18n = ModModule.i18n().get(Messages.class);

    private final Notificator notificator;

    private final UiFactory uiFactory;

    public void errorDialog(Throwable throwable) {
        errorDialog(throwable, null);
    }

    public void errorDialog(Throwable throwable, @Nullable String translationKey, Object... args) {
        String translatedMessage = i18n.tn(translationKey, args);
        Window<ErrorDialog> errorDialog = uiFactory.buildErrorDialog(throwable, translatedMessage);
        errorDialog.show();
    }

    public void notify(String translationKey, Object... args) {
        notificator.notify(i18n.t(translationKey, args));
    }

    public void notifySuccess(String translationKey, Object... args) {
        notificator.notifySuccess(i18n.t(translationKey, args));
    }

    public void notifyError(String translationKey, Object... args) {
        Throwable ex = ExceptionUtil.extractThrowableLast(args);

        if (ex != null) {
            args = Arrays.copyOf(args, args.length - 1);
            notificator.notifyError(i18n.t(translationKey, args), ex);
        } else {
            notificator.notifyError(i18n.t(translationKey, args));
        }
    }
}
