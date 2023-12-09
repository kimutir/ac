package com.amvera.cli.aspect;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionsAspect {

    @Autowired
    private Terminal terminal;

    @Pointcut("@annotation(org.springframework.shell.standard.ShellMethod) ")
    public void ifShellMethod() {
    }

    // todo: add exception handler
    @AfterThrowing(pointcut = "ifShellMethod()")
    public void ex() {
        terminal.writer().println("Some error. This message is for test purpose only.");
        terminal.writer().flush();
        System.exit(0);
    }

}
