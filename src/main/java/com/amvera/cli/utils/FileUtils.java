package com.amvera.cli.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    private static final String HOME = System.getenv("HOME") != null ? System.getenv("HOME") : System.getenv("PROGRAMFILES");

    public static final Path AMVERA_DIR = Paths.get(HOME, "Amvera");
    public static final Path CONFIG_PATH  = AMVERA_DIR.resolve("config.json");
    public static final Path TOKEN_PATH  = AMVERA_DIR.resolve("token.json");

}
