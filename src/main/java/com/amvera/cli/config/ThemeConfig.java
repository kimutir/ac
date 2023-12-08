package com.amvera.cli.config;

//import com.amvera.cli.custom.HelpTest;
import com.amvera.cli.style.CustomThemeSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.standard.commands.Help;
import org.springframework.shell.style.*;

@Configuration
public class ThemeConfig {
//    @Bean
//    public HelpTest help(Theme theme) {
//        ThemeRegistry registry = new ThemeRegistry();
//        registry.register(theme);
//        ThemeResolver resolver = new ThemeResolver(registry, "amvera-theme");
//        TemplateExecutor executor = new TemplateExecutor(resolver);
//        HelpTest help = new HelpTest(executor);
//        help.setCommandTemplate("classpath:template/help-command-custom.stg");
//        help.setCommandsTemplate("classpath:template/help-commands-custom.stg");
//        return help;
//    }

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
