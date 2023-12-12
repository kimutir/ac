package com.amvera.cli.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TokenUtils {

    private static final String FILE_NAME = "./token.txt";

    public static void saveResponseToken(String token) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME)) {
            fileOutputStream.write(token.getBytes());
            System.out.println("FILE CREATED");
        } catch (IOException e) {
            System.out.println("Ошибка записи токена");
        }
    }

    public static String readResponseToken() {

        try (FileInputStream fileInputStream = new FileInputStream(FILE_NAME)) {
            String token = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("FILE READ");
            return token;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
