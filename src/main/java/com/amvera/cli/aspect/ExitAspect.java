package com.amvera.cli.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExitAspect {

    @Pointcut("@annotation(org.springframework.shell.standard.ShellMethod)")
    public void shellRunExitPointCut() {
    }

    @After("shellRunExitPointCut()")
    public void shellRunMode() {
        System.out.println("Command completed. This message is for test purpose only.");
        System.exit(0);
    }

}
