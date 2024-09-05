package com.github.argon.sos.moreoptions.aspect;

import com.github.argon.sos.moreoptions.aspect.annotation.OnErrorShowDialog;
import com.github.argon.sos.moreoptions.ui.msg.Message;
import com.github.argon.sos.moreoptions.util.ExceptionUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class OnErrorShowDialogAspect {

    private Throwable lastException;

    @Pointcut("@annotation(showError)")
    public void callAt(OnErrorShowDialog showError) {}

    @Around("callAt(showError)")
    public Object around(ProceedingJoinPoint pjp, OnErrorShowDialog showError) throws Throwable {
        try {
            return pjp.proceed();
        } catch (Exception e) {
            // prevent chaining: first shown error dialog in exception chain wins
            if (!ExceptionUtil.contains(e, lastException)) {
                Message.errorDialog(e);
            }

            lastException = e;
            return e;
        }
    }
}
