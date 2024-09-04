package com.github.argon.sos.moreoptions.aspect;

import com.github.argon.sos.moreoptions.aspect.annotation.ShowError;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class PresentErrorAspect {
    @Pointcut("@annotation(showError)")
    public void callAt(ShowError showError) {
    }

    @Around("callAt(showError)")
    public Object around(ProceedingJoinPoint pjp, ShowError showError) throws Throwable {
        return pjp.proceed();
    }
}
