package com.amvera.cli.aspect;

import org.aspectj.lang.annotation.*;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExitAspect {

    @Autowired
    private Terminal terminal;

    @Pointcut("@annotation(org.springframework.shell.standard.ShellMethod) ")
    public void ifShellMethod() {
    }

    @AfterReturning(pointcut = "ifShellMethod()", returning = "output")
    public void shellRunMode(String output) {
        terminal.writer().println(output);
        terminal.writer().println("Command completed. This message is for test purpose only.");
        terminal.writer().flush();
        System.exit(0);
    }

}
