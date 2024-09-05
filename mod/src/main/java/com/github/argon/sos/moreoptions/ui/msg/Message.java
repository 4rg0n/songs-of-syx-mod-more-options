package com.github.argon.sos.moreoptions.ui.msg;

import com.github.argon.sos.moreoptions.game.ui.Window;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.ui.UiFactory;
import com.github.argon.sos.moreoptions.util.ExceptionUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Message {
    protected static final I18n i18n = I18n.get(Message.class);

    public static void errorDialog(Throwable throwable) {
        errorDialog(throwable, null);
    }

    public static void errorDialog(Throwable throwable, @Nullable String translationKey, Object... args) {
        String translatedMessage = i18n.tn(translationKey, args);
        Window<ErrorDialog> errorDialog = UiFactory.getInstance().buildErrorDialog(throwable, translatedMessage);
        errorDialog.show();
    }

    public static void notify(String translationKey, Object... args) {
        Notificator.getInstance().notify(i18n.t(translationKey, args));
    }

    public static void notifySuccess(String translationKey, Object... args) {
        Notificator.getInstance().notifySuccess(i18n.t(translationKey, args));
    }

    public static void notifyError(String translationKey, Object... args) {
        Throwable ex = ExceptionUtil.extractThrowable(args);

        if (ex != null) {
            args = Arrays.copyOf(args, args.length - 1);
            Notificator.getInstance().notifyError(i18n.t(translationKey, args), ex);
        } else {
            Notificator.getInstance().notifyError(i18n.t(translationKey, args));
        }
    }
}
