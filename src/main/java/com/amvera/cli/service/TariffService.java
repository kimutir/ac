package com.amvera.cli.service;

import com.amvera.cli.client.TariffClient;
import com.amvera.cli.dto.billing.Tariff;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.select.TariffSelectItem;
import com.amvera.cli.utils.table.AmveraTable;
import com.amvera.cli.utils.table.TariffTableModel;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TariffService {

    private final AmveraTable table;
    private final ShellHelper helper;
    private final AmveraSelector selector;
    private final TariffClient tariffClient;

    public TariffService(
            AmveraTable table,
            ShellHelper helper,
            AmveraSelector selector,
            TariffClient tariffClient
    ) {
        this.table = table;
        this.helper = helper;
        this.selector = selector;
        this.tariffClient = tariffClient;
    }

    public Tariff select() {
        List<SelectorItem<TariffSelectItem>> tariffs = tariffClient.getAll().tariffs().stream().map(TariffResponse::toSelectItem).toList();

        if (tariffs.isEmpty()) throw new RuntimeException("No tariffs found");

        return selector.singleSelector(tariffs, "Select tariff: ", true).getTariff();
    }

    public void renderTable() {
        List<TariffTableModel> tariffs = tariffClient.getAll().tariffs().stream().map(TariffTableModel::new).toList();
        helper.println(table.tariffs(tariffs));
    }

}
