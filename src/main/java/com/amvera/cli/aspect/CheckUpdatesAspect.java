package com.amvera.cli.aspect;

import com.amvera.cli.utils.ConfigUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CheckUpdatesAspect {

    @Autowired
    private ConfigUtils configUtils;

    @Before("execution(public * com.amvera.cli.command..*(..))")
    public void beforeCommand() {
        configUtils.update(configUtils.shouldUpdate());
    }

}