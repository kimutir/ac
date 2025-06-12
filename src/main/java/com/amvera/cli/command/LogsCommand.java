package com.amvera.cli.command;

import com.amvera.cli.service.LogsService;
import org.jline.reader.MaskingCallback;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.io.IOException;

@Command(command = "logs", group = "Logs commands")
public class LogsCommand {
    private final LogsService logsService;
    private final Terminal terminal;

    public LogsCommand(
            LogsService logsService,
            Terminal terminal
    ) {
        this.logsService = logsService;
        this.terminal = terminal;
    }

    @Command(command = "run")
    public void run(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug,
            @Option(longNames = "limit", shortNames = 'l', arity = OptionArity.EXACTLY_ONE, description = "Project slug", defaultValue = "100") Integer limit,
            @Option(longNames = "query", shortNames = 'q', arity = OptionArity.EXACTLY_ONE, description = "Project slug", defaultValue = "*") String query,
            @Option(longNames = "last", defaultValue = "30") Long last
    ) {
        logsService.run(slug, limit, query, last);
    }

    @Command(command = "build")
    public void build(
            @Option(longNames = "slug", shortNames = 's', arity = OptionArity.EXACTLY_ONE, description = "Project slug") String slug,
            @Option(longNames = "limit", shortNames = 'l', arity = OptionArity.EXACTLY_ONE, description = "Project slug", defaultValue = "100") Integer limit,
            @Option(longNames = "query", shortNames = 'q', arity = OptionArity.EXACTLY_ONE, description = "Project slug", defaultValue = "*") String query,
            @Option(longNames = "last", defaultValue = "30") Long last
    ) {
        logsService.build(slug, limit, query, last);
    }

    // test
    class ExitThread extends Thread {
        public void run() {
            try {
                String p = new AttributedString("Exit: ", AttributedStyle.DEFAULT.bold()).toAnsi();
                String s = null;
                while (true) {
                    try {
                        s = new LineReaderImpl(terminal).readLine(p, null, new KillProcessMask(), null);
                    } catch (UserInterruptException | IOException e) {
                        System.exit(0);
                    }
                    if (s.trim().equalsIgnoreCase("q")) {
                        this.interrupt();
                        System.exit(0);
                    }
                }
            } catch (Exception e) {
                // throws EndOfFileException
                System.exit(0);
            }


        }

    }

    static class KillProcessMask implements MaskingCallback {
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
