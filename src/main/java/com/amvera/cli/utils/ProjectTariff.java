package com.amvera.cli.utils;

public enum ProjectTariff {
    TRY("Пробный"),
    BEGINNER("Начальный"),
    BEGINNER_PLUS("Начальный Плюс"),
    STANDARD("Стандартный"),
    ULTRA("Ультра");

    private final String title;

    ProjectTariff(String title) {
        this.title = title;
    }

    public String title() {
        return this.title;
    }

    public static int value(String title) {
        switch (title) {
            case "Пробный" -> {
                return 4;
            }
            case "Начальный" -> {
                return 1;
            }
            case "Начальный Плюс" -> {
                return 5;
            }
            case "Стандартный" -> {
                return 2;
            }
            case "Ультра" -> {
                return 3;
            }
            default -> {
                return 0;
            }
        }
    }

    public static String value(int num) {
        switch (num) {
            case 4 -> {
                return "Пробный";
            }
            case 1 -> {
                return "Начальный";
            }
            case 5 -> {
                return "Начальный Плюс";
            }
            case 2 -> {
                return "Стандартный";
            }
            case 3 -> {
                return "Ультра";
            }
            default -> {
                return null;
            }
        }
    }
}
