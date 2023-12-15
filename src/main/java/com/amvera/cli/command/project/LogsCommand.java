package com.amvera.cli.command.project;

import com.amvera.cli.dto.project.LogGetResponse;
import com.amvera.cli.service.ProjectService;
import com.amvera.cli.utils.ShellHelper;
import org.jline.reader.MaskingCallback;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.stream.Collectors;

@ShellComponent
public class LogsCommand {

    private final ProjectService projectService;
    private final Terminal terminal;
    private final ShellHelper helper;

    public LogsCommand(ProjectService projectService, Terminal terminal, ShellHelper helper) {
        this.projectService = projectService;
        this.terminal = terminal;
        this.helper = helper;
    }

    @ShellMethod(
            key = "logs",
            value = "Buildings or running logs of project"
    )
    public void logs(
            @ShellOption(value = {"-p", "--project"}, arity = 1, help = "Project id, name or slug") String project,
            @ShellOption(value = {"-l", "--limit"}, arity = 1, defaultValue = "50", help = "Log lines limit (max 1000)") Integer limit,
            @ShellOption(value = {"-t", "--type"}, arity = 1, help = "Type of logs (build or run)") String type
    ) {
        if (limit > 1000) limit = 1000;
        if (limit < 1) limit = 50;

        if (type == null || type.equalsIgnoreCase("build") || type.equalsIgnoreCase("run")) {
            return;
        }

        helper.println("Loading logs can take up to 3 minutes.");
        helper.println("To interrupt process: Ctrl + C");
        helper.println("To interrupt process: input \"q\" and press \"Enter\"");
        helper.println("Also you can open new terminal window.");

        ExitThread exitThread = new ExitThread();
        exitThread.start();

        String logs = projectService.logs(project, type, limit).stream().map(LogGetResponse::content).collect(Collectors.joining("\n"));

        //todo: kill process when exception from request!!
        helper.println(logs);

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
