package com.amvera.cli.service;

import com.amvera.cli.client.AmveraHttpClient;
import com.amvera.cli.dto.billing.TariffListResponse;
import com.amvera.cli.dto.billing.TariffResponse;
import com.amvera.cli.utils.ShellHelper;
import com.amvera.cli.dto.billing.Tariff;
import com.amvera.cli.utils.select.AmveraSelector;
import com.amvera.cli.utils.select.TariffSelectItem;
import com.amvera.cli.utils.table.AmveraTable;
import com.amvera.cli.utils.table.TariffTableModel;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;

@Service
public class TariffService {

    private final AmveraHttpClient client;
    private final AmveraTable table;
    private final ShellHelper helper;
    private final AmveraSelector selector;

    public TariffService(AmveraHttpClient client, AmveraTable table, ShellHelper helper, AmveraSelector selector) {
        this.client = client;
        this.table = table;
        this.helper = helper;
        this.selector = selector;
    }

    public Tariff select() {
        List<SelectorItem<TariffSelectItem>> tariffs = getAllRequest().tariffs().stream().map(TariffResponse::toSelectItem).toList();
        return selector.singleSelector(tariffs, "Select tariff: ", true).getTariff();
    }

    public void renderTable() {
        List<TariffTableModel> tariffs = getAllRequest().tariffs().stream().map(TariffTableModel::new).toList();
        helper.println(table.tariffs(tariffs));
    }

    public TariffListResponse getAllRequest() {
        ResponseEntity<TariffListResponse> response = client.tariff()
                .get()
                .uri("?currency={currency}", Currency.getInstance("RUB"))
                .retrieve()
                .toEntity(TariffListResponse.class);

        // todo: check and throw exception
//        if (tariff == null) {
//            throw ClientExceptions.noContent("Tariff loading failed.");
//        }

        return response.getBody();
    }

    public TariffResponse getByTariffSlugRequest(String slug) {
        ResponseEntity<TariffResponse> response = client.tariff()
                .get()
                .uri("/slug/{slug}?currency={currency}", slug, Currency.getInstance("RUB"))
                .retrieve()
                .toEntity(TariffResponse.class);

        // todo: check and throw exception
//        if (tariff == null) {
//            throw ClientExceptions.noContent("Tariff loading failed.");
//        }
        System.out.println(response.getBody());
        return response.getBody();
    }

}
