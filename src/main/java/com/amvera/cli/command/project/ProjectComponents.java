package com.amvera.cli.command.project;

import com.amvera.cli.utils.ProjectTariff;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ProjectComponents {
    public static final Map<String, String> tariff = new LinkedHashMap<>();

    static {
        tariff.put("1.Пробный", ProjectTariff.TRY.title());
        tariff.put("2.Начальный", ProjectTariff.BEGINNER.title());
        tariff.put("3.Начальный Плюс", ProjectTariff.BEGINNER_PLUS.title());
        tariff.put("4.Стандартный", ProjectTariff.STANDARD.title());
        tariff.put("5.Ультра", ProjectTariff.ULTRA.title());
    }

    public static final Map<String, String> environment = new HashMap<>(
            Map.of(
                    "Python", "python",
                    "Java/Kotlin", "jvm",
                    "Node", "node",
                    "Docker", "docker",
                    "DB", "db"
            )
    );

    public static final Map<String, String> instrumentJVM = new HashMap<>(
            Map.of(
                    "Maven", "maven",
                    "Gradle", "gradle"
            )
    );

    public static final Map<String, String> instrumentPython = new HashMap<>(
            Map.of(
                    "PIP", "pip"
            )
    );

    public static final Map<String, String> instrumentNode = new HashMap<>(
            Map.of(
                    "NPM", "npm",
                    "Browser", "browser"
            )
    );

    public static final Map<String, String> instrumentDocker = new HashMap<>(
            Map.of(
                    "Docker", "docker"
            )
    );

    public static final Map<String, String> instrumentDB = new LinkedHashMap<>(
            Map.of(
                    "PostgreSQL", "postgresql",
                    "pgAdmin", "pgadmin",
                    "MySQL", "mysql",
                    "MongoDB", "mongodb",
                    "Mongo-Express", "mongo-express",
                    "phpMyAdmin","phpmyadmin",
                    "Redis", "redis",
                    "Redis-insight", "redis-insight"
            )
    );



}
