package com.amvera.cli.utils;

import com.amvera.cli.exception.TokenException;
import com.amvera.cli.exception.UnauthorizedException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TokenUtils {

    private static final String FILE_NAME = "./token.txt";

    public static void saveToke(String token) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME)) {
            fileOutputStream.write(token.getBytes());
//            System.out.println("FILE CREATED");
        } catch (IOException e) {
            System.out.println("Ошибка записи токена");
        }
    }

    public static String readToken() {
        String token;
        try (FileInputStream fileInputStream = new FileInputStream(FILE_NAME)) {
            token = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
            return token;
        } catch (IOException e) {
            System.out.println("token.txt not found");
            throw new UnauthorizedException("You need to login first.");
//            token = "";
        } catch (Exception e) {
            System.out.println("something bad: " + e.getMessage());
            throw new TokenException(e.getMessage());
        }

//        return token;
    }

    public static void deleteToken() {}

}
