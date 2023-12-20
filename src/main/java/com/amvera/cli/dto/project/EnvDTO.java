package com.amvera.cli.dto.project;

import java.util.Objects;

public record EnvDTO(
        Integer id,
        String name,
        String value,
        Boolean isSecret
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnvDTO envDTO = (EnvDTO) o;
        return Objects.equals(name, envDTO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}