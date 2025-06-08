package com.amvera.cli.utils;

import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.component.support.SelectorList;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class AmveraSelector extends AbstractShellComponent {

    private final List<SelectorItem<String>> tariffs = Arrays.asList(
            SelectorItem.of("Пробный", Tariff.TRY.title()),
            SelectorItem.of("Начальный", Tariff.BEGINNER.title()),
            SelectorItem.of("Начальный Плюс", Tariff.BEGINNER_PLUS.title()),
            SelectorItem.of("Стандартный", Tariff.STANDARD.title()),
            SelectorItem.of("Ультра", Tariff.ULTRA.title()),
            SelectorItem.of("Ультра CPU", Tariff.ULTRA_CPU.title())
    );

    private final List<SelectorItem<String>> serviceTypes = Arrays.asList(
            SelectorItem.of("Project", ServiceType.PROJECT.name()),
            SelectorItem.of("Managed PostgreSQL", ServiceType.POSTGRESQL.name()),
            SelectorItem.of("Preconfigured service", ServiceType.PRECONFIGURED.name())
    );

    public int selectTariff() {
        return Tariff.value(singleSelector(tariffs, "Select tariff: ", true));
    }

    public int selectServiceType() {
        return ServiceType.valueOf(singleSelector(serviceTypes, "Select service type: ", true)).getId();
    }

    /**
     * Ordered single item selector
     *
     * @param items  items to select
     * @param prompt title
     * @return selected item
     */
    public String singleSelector(List<SelectorItem<String>> items, String prompt, boolean showResult) {
        try {
            SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(
                    getTerminal(),
                    items,
                    prompt,
                    null
            );

            component.setResourceLoader(getResourceLoader());
            component.setTemplateExecutor(getTemplateExecutor());
            component.setMaxItems(items.size());
            component.setPrintResults(showResult);

            SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                    .run(SingleItemSelector.SingleItemSelectorContext.empty());

            return context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
        } catch (Error e) {
            System.exit(0);
            return null;
        }
    }

    public Boolean yesOrNoSingleSelector(String title) {
        try {
            SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(
                    getTerminal(),
                    List.of(
                            SelectorItem.of("Yes", "Yes"),
                            SelectorItem.of("No", "No")
                    ),
                    title,
                    null
            );
            component.setResourceLoader(getResourceLoader());
            component.setTemplateExecutor(getTemplateExecutor());
            component.setMaxItems(2);

            return component
                    .run(SingleItemSelector.SingleItemSelectorContext.empty())
                    .getResultItem()
                    .flatMap(si -> Optional.of(si.getItem().equalsIgnoreCase("Yes")))
                    .get();
        } catch (Error e) {
            System.exit(0);
            return null;
        }
    }

}
