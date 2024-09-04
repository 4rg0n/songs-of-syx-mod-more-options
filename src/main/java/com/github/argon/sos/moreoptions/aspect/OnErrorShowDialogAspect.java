package com.github.argon.sos.moreoptions.aspect;

import com.github.argon.sos.moreoptions.aspect.annotation.OnErrorShowDialog;
import com.github.argon.sos.moreoptions.game.ui.Window;
import com.github.argon.sos.moreoptions.ui.ErrorDialog;
import com.github.argon.sos.moreoptions.ui.UiFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class OnErrorShowDialogAspect {
    @Pointcut("@annotation(showError)")
    public void callAt(OnErrorShowDialog showError) {
    }

    @Around("callAt(showError)")
    public Object around(ProceedingJoinPoint pjp, OnErrorShowDialog showError) throws Throwable {
        try {
            return pjp.proceed();
        } catch (Exception e) {
            Window<ErrorDialog> errorDialog = UiFactory.getInstance().buildErrorDialog(e);
            errorDialog.show();
            throw e;
        }
    }
}
