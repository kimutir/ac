package com.amvera.cli.dto.billing;

import com.amvera.cli.utils.select.TariffSelectItem;
import org.springframework.shell.component.support.SelectorItem;

import java.util.List;

public record TariffResponse(
        int id,
        String  slug,
        String  name,
        String  serviceTypeName,
        String  description,
        List<ConditionResponse> conditions,
        Boolean active
) {

    public SelectorItem<TariffSelectItem> toSelectItem() {
        TariffSelectItem selectItem = new TariffSelectItem(this);
        return SelectorItem.of(selectItem.getName(), selectItem);
    }

}
