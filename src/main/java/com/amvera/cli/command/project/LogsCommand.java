package com.amvera.cli.command.project;

import com.amvera.cli.dto.project.LogGetResponse;
import com.amvera.cli.dto.project.ProjectGetResponse;
import com.amvera.cli.service.LogsService;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.ShellHelper;
import org.jline.reader.MaskingCallback;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Command(group = "Project commands")
public class LogsCommand {
    private final ProjectService projectService;
    private final LogsService logsService;
    private final Terminal terminal;
    private final ShellHelper helper;

    public LogsCommand(
            ProjectService projectService,
            LogsService logsService,
            Terminal terminal,
            ShellHelper helper
    ) {
        this.projectService = projectService;
        this.logsService = logsService;
        this.terminal = terminal;
        this.helper = helper;
    }

    @Command(command = "logs", description = "Buildings or running logs of project")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void logs(
            @Option(longNames = "project", shortNames = 'p', arity = OptionArity.EXACTLY_ONE, description = "Project id, name or slug", required = true) String project,
            @Option(longNames = "limit", shortNames = 'l', arity = OptionArity.EXACTLY_ONE, defaultValue = "50", description = "Log lines limit (max 1000)") Integer limit,
            @Option(longNames = "type", shortNames = 't', arity = OptionArity.EXACTLY_ONE, defaultValue = "50", description = "Type of logs (build or run)", required = true) String type
    ) {
        if (limit > 1000) limit = 1000;
        if (limit < 1) limit = 50;

        ProjectGetResponse projectResponse = projectService.findBy(project);

        List<String> availableTypes = new ArrayList<>(List.of("run", "build"));

        if (type == null || !availableTypes.contains(type.trim().toLowerCase())) {
            return;
        }

        helper.println("Loading logs can take up to 3 minutes.");
        helper.println("To interrupt process: Ctrl + C");
        helper.println("To interrupt process: input \"q\" and press \"Enter\"");
        helper.println("Also you can open new terminal window.");

        ExitThread exitThread = new ExitThread();
        exitThread.start();

        List<String> logsList = new ArrayList<>(logsService.logs(projectResponse, type, limit).stream().map(LogGetResponse::content).toList());
        Collections.reverse(logsList);
        String logs = String.join("\n", logsList);


        //todo: kill process when exception from request!!
        helper.println("\n" + logs);

        System.exit(0);
    }

    class ExitThread extends Thread {
        public void run() {
            String p = new AttributedString("Exit: ", AttributedStyle.DEFAULT.bold()).toAnsi();
            String s = null;
            while (true) {
                try {
                    s = new LineReaderImpl(terminal).readLine(p, null, new TestMask(), null);
                } catch (UserInterruptException | IOException e) {
                    System.exit(0);
                }
                if (s.trim().equalsIgnoreCase("q")) {
                    this.interrupt();
                    System.exit(0);
                }
            }

        }

    }

    class TestMask implements MaskingCallback {
        @Override
        public String display(String line) {

            if (line == null || line.isBlank()) {
                return "<input \"q\" to kill process>";
            }

            return line;
        }

        @Override
        public String history(String line) {
            return null;
        }
    }


}
