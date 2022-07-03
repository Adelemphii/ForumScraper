package tech.adelemphii.forumscraper.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class UserInput {

    public static int getInteger(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);

        try {
            return scanner.nextInt();
        } catch (InputMismatchException ignored) {
            System.out.println("That is not a valid number. Try again.");
            return getInteger(prompt);
        }
    }

    public static String getString(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);

        try {
            String string = scanner.next();
            if(string.isBlank() || string.isEmpty()) {
                return getString(prompt);
            }
            return string;
        } catch (InputMismatchException ignored) {
            System.out.println("That is not a valid string. Try again.");
            return getString(prompt);
        }
    }

    public static boolean getBoolean(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);

        try {
            return scanner.nextBoolean();
        } catch (InputMismatchException ignored) {
            System.out.println("That is not a valid boolean. Try again.");
            return getBoolean(prompt);
        }
    }

    public static List<String> getStringList(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(prompt + " (Example: this dam is dirty,very dope,pogchamp): ");

        String rawString = scanner.next();
        String[] split = rawString.split(",");

        return Arrays.asList(split);
    }
}
