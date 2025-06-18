package com.amvera.cli.command;

import com.amvera.cli.service.DomainService;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

@Command(command = "domain", group = "Domain commands")
public class DomainCommand {

    private final DomainService domainService;

    public DomainCommand(DomainService domainService) {
        this.domainService = domainService;
    }

    @Command(command = "", description = "Returns project domain list")
    @CommandAvailability(provider = "userLoggedInProvider")
    public void get(
            @Option(longNames = "slug", shortNames = 's', arity = CommandRegistration.OptionArity.EXACTLY_ONE, description = "Project slug") String slug
    ) {
        domainService.renderTable(slug);
    }

//    @Command(command = "add", description = "Returns project domain list")
//    @CommandAvailability(provider = "userLoggedOutProvider")
//    public void add(
//            @Option(longNames = "slug", shortNames = 's', arity = CommandRegistration.OptionArity.EXACTLY_ONE, description = "Project slug") String slug
//    ) {
////        domainService.get
//    }
//
//    @Command(command = "delete", description = "Environment variables for specified project")
//    @CommandAvailability(provider = "userLoggedOutProvider")
//    public void delete(
//            @Option(longNames = "slug", shortNames = 's', arity = CommandRegistration.OptionArity.EXACTLY_ONE, description = "Project slug") String slug,
//            @Option(longNames = "id", shortNames = 'i', arity = CommandRegistration.OptionArity.EXACTLY_ONE, description = "Domain id") Long id
//    ) {
//        domainService.delete(slug, id);
//    }
//
//    @Command(command = "update", description = "Environment variables for specified project")
//    @CommandAvailability(provider = "userLoggedOutProvider")
//    public void update(
//            @Option(longNames = "slug", shortNames = 's', arity = CommandRegistration.OptionArity.EXACTLY_ONE, description = "Project slug") String slug
//    ) {
////        envService.create(slug);
//    }

}
