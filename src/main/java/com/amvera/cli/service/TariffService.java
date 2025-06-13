package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.config.Endpoints;
import com.amvera.cli.dto.billing.Tariff;
import com.amvera.cli.dto.billing.TariffListResponse;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.select.TariffSelectItem;
import com.amvera.cli.utils.table.AmveraTable;
import com.amvera.cli.utils.table.TariffTableModel;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class TariffService {

    private final AmveraTable table;
    private final ShellHelper helper;
    private final AmveraSelector selector;
    private final Endpoints endpoints;
    private final AmveraHttpClient client;

    public TariffService(
            AmveraTable table,
            ShellHelper helper,
            AmveraSelector selector,
            Endpoints endpoints,
            AmveraHttpClient client
    ) {
        this.table = table;
        this.helper = helper;
        this.selector = selector;
        this.endpoints = endpoints;
        this.client = client;
    }

    public Tariff select() {
        List<SelectorItem<TariffSelectItem>> tariffs = client.get(
                URI.create(endpoints.tariff()),
                TariffListResponse.class,
                "Error when getting tariff list"
        ).tariffs().stream().map(TariffResponse::toSelectItem).toList();

        if (tariffs.isEmpty()) throw new RuntimeException("No tariffs found");

        return selector.singleSelector(tariffs, "Select tariff: ", true).getTariff();
    }

    public void renderTable() {
        List<TariffTableModel> tariffs = client.get(
                URI.create(endpoints.tariff()),
                TariffListResponse.class,
                "Error when getting tariff list"
        ).tariffs().stream().map(TariffTableModel::new).toList();

        helper.println(table.tariffs(tariffs));
    }

}
