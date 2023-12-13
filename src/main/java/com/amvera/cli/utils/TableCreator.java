package com.amvera.cli.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.shell.table.*;
import org.springframework.stereotype.Component;

@Component
public class TableCreator {

    public String singleEntytiTable(Object obj, ObjectMapper mapper) {
        SingleEntityTableModelBuilder tableModelBuilder = new SingleEntityTableModelBuilder(obj, mapper);
        TableModel model = tableModelBuilder.build();
        TableBuilder tableBuilder = new TableBuilder(model);
        return tableBuilder
                .addInnerBorder(BorderStyle.oldschool)
                .addHeaderBorder(BorderStyle.fancy_light)
                .on(CellMatchers.column(0)).addFormatter(value -> new String[]{value + "  "})
                .on(CellMatchers.column(1)).addFormatter(value -> new String[]{value + "  "})
                .build()
                .render(180);
    }

}
