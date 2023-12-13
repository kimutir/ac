package com.amvera.cli.utils;

import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class SelectorCreator extends AbstractShellComponent {

    private List<SelectorItem<String>> tariffs = Arrays.asList(
            SelectorItem.of("Пробный", ProjectTariff.TRY.title()),
            SelectorItem.of("Начальный", ProjectTariff.BEGINNER.title()),
            SelectorItem.of("Начальный Плюс", ProjectTariff.BEGINNER_PLUS.title()),
            SelectorItem.of("Стандартный", ProjectTariff.STANDARD.title()),
            SelectorItem.of("Ультра", ProjectTariff.ULTRA.title())
    );

    private List<SelectorItem<String>> envs = Arrays.asList(
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
        return ProjectTariff.value(single(tariffs, "Select tariff"));
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
