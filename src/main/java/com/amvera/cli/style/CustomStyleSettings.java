package com.amvera.cli.style;

import org.springframework.shell.style.StyleSettings;

public class CustomStyleSettings extends StyleSettings {

    @Override
    public String value() {
        return "default";
    }

    @Override
    public String listValue() {
        return "fg-rgb:#FFA500";
    }

    @Override
    public String itemSelector() {
        return "bold,fg-rgb:#FFA500";
    }

    @Override
    public String itemSelected() {
        return "fg-rgb:#FFA500";
    }
}
