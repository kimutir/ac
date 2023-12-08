package com.amvera.cli.style;

import org.springframework.shell.style.StyleSettings;
import org.springframework.shell.style.ThemeSettings;

public class CustomThemeSettings extends ThemeSettings {

    @Override
    public StyleSettings styles() {
        return new CustomStyleSettings();
    }
}
