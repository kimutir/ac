package com.amvera.cli.utils;

import org.jline.reader.MaskingCallback;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AmveraInput {

    private final Terminal terminal;

    public AmveraInput(Terminal terminal) {
        this.terminal = terminal;
    }

    public String defaultInput(String prompt) {
        String p = new AttributedString(prompt, AttributedStyle.DEFAULT.bold()).toAnsi();
        try {
            return new LineReaderImpl(terminal).readLine(p, null, new CommonMask(), null);
        } catch (IOException e) {
            System.out.println("CAUGHT IN TRY CATCH");
            throw new RuntimeException(e);
        }
    }

    public String secretInput(String prompt) {
        String p = new AttributedString(prompt, AttributedStyle.DEFAULT.bold()).toAnsi();
        try {
            return new LineReaderImpl(terminal).readLine(p, null, new SecretMask(), null);
        } catch (IOException e) {
            System.out.println("CAUGHT IN TRY CATCH");
            throw new RuntimeException(e);
        }
    }

    public String confirmInput(String prompt, String confirm) {
        String p = new AttributedString(prompt, AttributedStyle.DEFAULT.bold()).toAnsi();
        try {
            return new LineReaderImpl(terminal).readLine(p, null, new CommonMask(confirm), null);
        } catch (IOException e) {
            System.out.println("CAUGHT IN TRY CATCH");
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

    class SecretMask implements MaskingCallback {
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
