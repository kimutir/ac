package com.amvera.cli.utils;

import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
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
            SelectorItem.of("Ультра", Tariff.ULTRA.title())
    );

    private final List<SelectorItem<String>> envs = Arrays.asList(
            SelectorItem.of("Python", Environment.PYTHON.name()),
            SelectorItem.of("Java/Kotlin", Environment.JVM.name()),
            SelectorItem.of("Node", Environment.NODE.name()),
            SelectorItem.of("Docker", Environment.DOCKER.name()),
            SelectorItem.of("DB", Environment.DB.name())
    );

    public Environment selectEnvironment() {
        return Environment.valueOf(single(envs, "Environment"));
    }

    public int selectTariff() {
        return Tariff.value(single(tariffs, "Select tariff"));
    }

    public String single(List<SelectorItem<String>> items, String name) {
        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                items, name, null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());

        return context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
    }



}
