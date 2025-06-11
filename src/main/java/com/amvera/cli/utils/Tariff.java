package com.amvera.cli.utils;

public enum Tariff {
    TRIAL("Пробный", 4),
    BEGINNER("Начальный", 1),
    BEGINNER_PLUS("Начальный Плюс", 5),
    STANDARD("Стандартный", 2),
    ULTRA("Ультра", 3),
    ULTRA_CPU("Ультра CPU", 6),
    UNKNOWN("Неизвестный", 0);

    private final String title;
    private final int id;

    Tariff(String title, int id) {
        this.title = title;
        this.id = id;
    }

    public String title() {
        return this.title;
    }
    public int id() {return this.id;}

    public static Tariff value(String title) {
        return switch (title) {
            case "Пробный" -> TRIAL;
            case "Начальный" -> BEGINNER;
            case "Начальный Плюс" -> BEGINNER_PLUS;
            case "Стандартный" -> STANDARD;
            case "Ультра" -> ULTRA;
            case "Ультра CPU" -> ULTRA_CPU;
            default -> UNKNOWN;
        };
    }

    public static Tariff value(int num) {
        return switch (num) {
            case 4 -> TRIAL;
            case 1 -> BEGINNER;
            case 5 -> BEGINNER_PLUS;
            case 2 -> STANDARD;
            case 3 -> ULTRA;
            case 6 -> ULTRA_CPU;
            default -> UNKNOWN;
        };
    }

    public static Tariff valueOfName(String name) {
        if (name.startsWith("Пробный")) return TRIAL;
        if (name.startsWith("Начальный Плюс")) return BEGINNER_PLUS;
        if (name.startsWith("Начальный")) return BEGINNER;
        if (name.startsWith("Стандартный")) return STANDARD;
        if (name.startsWith("Ультра")) return ULTRA;
        if (name.startsWith("Ультра CPU")) return ULTRA_CPU;

        return UNKNOWN;
    }
}
