package com.amvera.cli.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExitAspect {

    @Pointcut("@annotation(org.springframework.shell.standard.ShellMethod) && execution(* com.amvera.cli.command..* (..))")
    public void shellRunExitPointCut() {
//        System.out.println("POINTCUT");
        // Pointcut
    }

    @After("shellRunExitPointCut()")
    public void shellRunMode() {
        System.out.println("AFTER");
        System.exit(0);
    }

}
