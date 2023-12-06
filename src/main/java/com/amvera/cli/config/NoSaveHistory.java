package com.amvera.cli.config;

import org.jline.reader.impl.history.DefaultHistory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Stops creating logs txt file.
 */
@Component
public class NoSaveHistory extends DefaultHistory {
    @Override
    public void save() throws IOException {

    }
}
