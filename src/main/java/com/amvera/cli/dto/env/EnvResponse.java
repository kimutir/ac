package com.amvera.cli.dto.env;

import com.amvera.cli.utils.select.EnvSelectItem;
import org.springframework.shell.component.support.SelectorItem;

import java.util.Objects;

public record EnvResponse(
        Long id,
        String name,
        String value,
        Boolean isSecret
) {

    public SelectorItem<EnvSelectItem> toSelectorItem() {
        EnvSelectItem selectItem = new EnvSelectItem(this);
        return SelectorItem.of(selectItem.getName(), selectItem);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnvResponse envDTO = (EnvResponse) o;
        return Objects.equals(name, envDTO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}