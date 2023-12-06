//package com.amvera.cli.config;
//
////import com.amvera.cli.custom.standart.commands.HelpCustom;
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.boot.info.BuildProperties;
//import org.springframework.boot.info.GitProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.shell.boot.SpringShellProperties;
//import org.springframework.shell.style.TemplateExecutor;
//
//@Configuration
//public class ConfigureStandardCommands {
//    @Bean
//    public HelpCustom help(
//            SpringShellProperties properties,
//            ObjectProvider<TemplateExecutor> templateExecutor
//    ) {
//        HelpCustom help = new HelpCustom(templateExecutor.getIfAvailable());
//        if (properties.getCommand().getHelp().getGroupingMode() == SpringShellProperties.HelpCommand.GroupingMode.FLAT) {
//            help.setShowGroups(false);
//        }
//        help.setCommandTemplate(properties.getCommand().getHelp().getCommandTemplate());
//        help.setCommandsTemplate(properties.getCommand().getHelp().getCommandsTemplate());
//        return help;
//    }
//
//
//    @Bean
//    public VersionCustom version(
//            SpringShellProperties properties,
//            ObjectProvider<BuildProperties> buildProperties,
//            ObjectProvider<GitProperties> gitProperties,
//            ObjectProvider<TemplateExecutor> templateExecutor) {
//        VersionCustom version = new VersionCustom(templateExecutor.getIfAvailable());
//        version.setBuildProperties(buildProperties.getIfAvailable());
//        version.setGitProperties(gitProperties.getIfAvailable());
//        SpringShellProperties.VersionCommand versionProperties = properties.getCommand().getVersion();
//        version.setTemplate(versionProperties.getTemplate());
//        version.setShowBuildArtifact(versionProperties.isShowBuildArtifact());
//        version.setShowBuildGroup(versionProperties.isShowBuildGroup());
//        version.setShowBuildName(versionProperties.isShowBuildName());
//        version.setShowBuildTime(versionProperties.isShowBuildTime());
//        version.setShowBuildVersion(versionProperties.isShowBuildVersion());
//        version.setShowGitBranch(versionProperties.isShowGitBranch());
//        version.setShowGitCommitId(versionProperties.isShowGitCommitId());
//        version.setShowGitShortCommitId(versionProperties.isShowGitShortCommitId());
//        version.setShowGitCommitTime(versionProperties.isShowGitCommitTime());
//        return version;
//    }
//
//
//
//}
