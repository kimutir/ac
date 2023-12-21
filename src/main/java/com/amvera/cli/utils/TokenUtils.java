package com.amvera.cli.utils;

import com.amvera.cli.exception.TokenNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TokenUtils {
    // HOME - Mac OS
    // USERPROFILE - Windows
    private static final String HOME = System.getenv("HOME") != null ? System.getenv("HOME") : System.getenv("USERPROFILE");
    private static final String PATH = HOME + File.separator + ".amvera";

    public static void saveToke(String token) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(PATH)) {
            fileOutputStream.write(token.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Unable to save token. Contact us to solve problem.");
        }
    }

    public static String readToken() {
        String token;
        try (FileInputStream fileInputStream = new FileInputStream(PATH)) {
            token = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
            return token;
        }
        catch (IOException e) {
            throw new TokenNotFoundException("Token was not found.");
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to read token. Contact us to solve problem.");
        }
    }

    public static String deleteToken() {
        File fileToDelete = new File(PATH);
        fileToDelete.delete();

        return "Logged out successfully!";
    }

}
