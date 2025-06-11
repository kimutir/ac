package com.amvera.cli.utils.select;

import com.amvera.cli.dto.project.EnvResponse;

public class EnvSelectItem {

    private final String name;
    private final EnvResponse env;

    public EnvSelectItem(EnvResponse env) {
        this.name = env.name();
        this.env = env;
    }

    public String getName() {
        return name;
    }

    public EnvResponse getEnv() {
        return env;
    }

    @Override
    public String toString() {
        return String.format("%s [ %s ]", name, env.isSecret() ? "secret" : "environment");
    }

}
