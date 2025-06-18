package com.amvera.cli.dto.project;

public enum ServiceType {
    PROJECT(1, "compute"),
    POSTGRESQL(2, "cnpg"),
    PRECONFIGURED(4, "marketplace");

    private final String title;
    private final int id;

    ServiceType(int id, String title) {
        this.title = title;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public static ServiceType valueOf(int id) {
        return switch (id) {
            case 1 -> PROJECT;
            case 2 -> POSTGRESQL;
            case 4 -> PRECONFIGURED;
            default -> null;
        };
    }

    public static ServiceType valueOfString(String name) {
        if (name == null) {
            return null;
        }

        return switch (name) {
            case "compute" -> PROJECT;
            case "cnpg" -> POSTGRESQL;
            case "marketplace" -> PRECONFIGURED;
            default -> null;
        };
    }
}
