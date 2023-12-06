package com.amvera.cli.utils;

import java.io.FileOutputStream;
import java.io.IOException;

public class SaveResponseToken {

    private static final String FILE_NAME = "token.txt";

    public static void saveResponseToken(String token) throws IOException {
        try(FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME)){
            fileOutputStream.write(token.getBytes());
        }
    }

}
