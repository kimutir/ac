package com.amvera.cli.style;

import org.springframework.shell.style.StyleSettings;
import org.springframework.shell.style.ThemeSettings;

/**
 * Class for customization default theme.
 */
public class AmveraThemeSettings extends ThemeSettings {

    /**
     * Apply custom styles settings.
     * @return StyleSettings
     */
    @Override
    public StyleSettings styles() {
        return new AmveraStyleSettings();
    }
}
