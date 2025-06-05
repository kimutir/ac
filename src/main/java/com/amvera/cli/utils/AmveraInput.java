package com.amvera.cli.utils;

import org.jline.reader.MaskingCallback;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.stereotype.Component;

import java.io.IOException;
// todo: add validation
@Component
public class AmveraInput {

    private final Terminal terminal;
    private final ShellHelper helper;

    public AmveraInput(
            Terminal terminal,
            ShellHelper helper
    ) {
        this.terminal = terminal;
        this.helper = helper;
    }

    public String notBlankOrNullInput(String prompt) {
        String input = defaultInput(prompt);

        if (input == null || input.isBlank()) {
            helper.printError("Value can not be empty.");
            return notBlankOrNullInput(prompt);
        }

        return input;
    }

    public String notBlankOrNullInput(String prompt, String defaultValue) {
        String input = inputWithDefault(prompt, defaultValue);

        if (input == null || input.isBlank()) {
            helper.printError("Value can not be empty.");
            return notBlankOrNullInput(prompt, defaultValue);
        }

        return input;
    }



    public String defaultInput(String prompt) {
        String p = new AttributedString(prompt, AttributedStyle.DEFAULT.bold()).toAnsi();
        try {
            return new LineReaderImpl(terminal).readLine(p, null, new CommonMask(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String inputWithDefault(String prompt, String defaultValue) {
        String p = new AttributedString(prompt, AttributedStyle.DEFAULT.bold()).toAnsi();
        try {
            return new LineReaderImpl(terminal).readLine(p, null, new CommonMask(), defaultValue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String secretInput(String prompt) {
        String p = new AttributedString(prompt, AttributedStyle.DEFAULT.bold()).toAnsi();
        try {
            return new LineReaderImpl(terminal).readLine(p, null, new SecretMask(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String confirmInput(String prompt, String confirm) {
        String p = new AttributedString(prompt, AttributedStyle.DEFAULT.bold()).toAnsi();
        try {
            return new LineReaderImpl(terminal).readLine(p, null, new CommonMask(confirm), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class CommonMask implements MaskingCallback {
        private String mask;

        public CommonMask() {
        }

        public CommonMask(String mask) {
            this.mask = mask;
        }

        @Override
        public String display(String line) {
            if (line == null || line.isEmpty()) {
                return mask == null ? "<enter value>" : mask;
            }
            return line;
        }

        @Override
        public String history(String line) {
            return null;
        }
    }

    static class SecretMask implements MaskingCallback {
        @Override
        public String display(String line) {

            if (line == null || line.isEmpty()) {
                return "<enter value>";
            }

            return "*".repeat(line.length());
        }

        @Override
        public String history(String line) {
            return null;
        }
    }

}
