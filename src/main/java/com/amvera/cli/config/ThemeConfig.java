package com.amvera.cli.config;

//import com.amvera.cli.custom.HelpTest;
import com.amvera.cli.style.CustomThemeSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.style.*;

@Configuration
public class ThemeConfig {
    @Bean
    public Theme theme() {
        return new Theme() {
            @Override
            public String getName() {
                return "amvera-theme";
            }

            @Override
            public ThemeSettings getSettings() {
                return new CustomThemeSettings();
            }
        };
    }

}
