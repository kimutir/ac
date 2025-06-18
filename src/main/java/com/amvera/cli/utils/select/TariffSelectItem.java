package com.amvera.cli.utils.select;

import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.dto.billing.Tariff;

public class TariffSelectItem {

    private final String name;
    private final Tariff tariff;

    public TariffSelectItem(TariffResponse tariff) {
        this.name = Tariff.value(tariff.id()).name();
        this.tariff = Tariff.value(tariff.id());
    }

    public Tariff getTariff() {
        return tariff;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
