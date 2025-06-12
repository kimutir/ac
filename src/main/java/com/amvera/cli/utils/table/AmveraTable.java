package com.amvera.cli.utils.table;

import com.amvera.cli.dto.domain.DomainResponse;
import com.amvera.cli.dto.env.EnvResponse;
import com.amvera.cli.dto.project.ProjectResponse;
import com.amvera.cli.dto.project.cnpg.CnpgBackupResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.shell.table.*;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

import static org.springframework.shell.table.CellMatchers.at;

@Component
public class AmveraTable {

    private final ObjectMapper mapper;

    public AmveraTable(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Returns table of all user projects.
     *
     * @param projects list of projects
     * @return table as String
     */
    public String projects(List<ProjectResponse> projects) {
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("name", "NAME");
        headers.put("slug", "SLUG");
        headers.put("status", "STATUS");
        headers.put("requiredInstances", "REQ INST");
        headers.put("instances", "CUR INST");
        headers.put("serviceType", "TYPE");
        TableModel model = new BeanListTableModel<>(projects, headers);
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder
                .addInnerBorder(BorderStyle.oldschool)
                .addHeaderBorder(BorderStyle.fancy_light);

        for (int i = 0; i < projects.size() + 1; i++) {
            tableBuilder.on(at(i, 0)).addFormatter(new SpaceFormatter()).addAligner(SimpleHorizontalAligner.center);
            tableBuilder.on(at(i, 1)).addFormatter(new SpaceFormatter()).addAligner(SimpleHorizontalAligner.center);
            tableBuilder.on(at(i, 2)).addFormatter(new SpaceFormatter()).addAligner(SimpleHorizontalAligner.center);
            tableBuilder.on(at(i, 3)).addFormatter(new SpaceFormatter()).addAligner(SimpleHorizontalAligner.center);
            tableBuilder.on(at(i, 4)).addAligner(SimpleHorizontalAligner.center);
            tableBuilder.on(at(i, 5)).addAligner(SimpleHorizontalAligner.center);
            tableBuilder.on(at(i, 6)).addFormatter(new SpaceFormatter()).addAligner(SimpleHorizontalAligner.center);
        }

        return tableBuilder.build().render(200);
    }

    /**
     * Returns table of all project environment variables.
     *
     * @param envs list of environment variables
     * @return table as String
     */
    public String environments(List<EnvResponse> envs) {
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("name", "NAME");
        headers.put("value", "VALUE");
        headers.put("isSecret", "SECRET");
        TableModel model = new BeanListTableModel<>(envs, headers);
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder
                .addInnerBorder(BorderStyle.oldschool)
                .addHeaderBorder(BorderStyle.fancy_light);

        for (int i = 0; i < envs.size() + 1; i++) {
            for (int j = 0; j < headers.size(); j++) {
                tableBuilder.on(at(i, j)).addFormatter(new SpaceFormatter());
            }
        }

        return tableBuilder.build().render(180);
    }

    /**
     * Returns table of all project domains.
     *
     * @param domains list of domains
     * @return table as String
     */
    public String domains(List<DomainResponse> domains) {
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("domainName", "DOMAIN_NAME");
        headers.put("isDefault", "IS_DEFAULT");
        headers.put("activated", "IS_ACTIVE");
        headers.put("domainType", "DOMAIN_TYPE");
        headers.put("ingressType", "CONNECTION_TYPE");

        TableModel model = new BeanListTableModel<>(domains, headers);

        TableBuilder tableBuilder = new TableBuilder(model)
                .addInnerBorder(BorderStyle.oldschool)
                .addHeaderBorder(BorderStyle.fancy_light);

        for (int i = 0; i < domains.size() + 1; i++) {
            for (int j = 0; j < headers.size(); j++) {
                tableBuilder.on(at(i, j)).addFormatter(new SpaceFormatter()).addAligner(SimpleHorizontalAligner.center);
            }
        }

        return tableBuilder.build().render(180);
    }

    /**
     * Returns table of all tariffs.
     *
     * @param tariffs list of tariffs
     * @return table as String
     */
    public String tariffs(List<TariffTableModel> tariffs) {
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("name", "TARIFF_NAME");
        headers.put("cpu", "CPU_LIMIT");
        headers.put("ram", "RAM_LIMIT");
        headers.put("volume", "SSD_LIMIT");

        TableModel model = new BeanListTableModel<>(tariffs, headers);

        TableBuilder tableBuilder = new TableBuilder(model)
                .addInnerBorder(BorderStyle.oldschool)
                .addHeaderBorder(BorderStyle.fancy_light);

        for (int i = 0; i < tariffs.size() + 1; i++) {
            for (int j = 0; j < headers.size(); j++) {
                tableBuilder.on(at(i, j)).addFormatter(new SpaceFormatter()).addAligner(SimpleHorizontalAligner.center);
            }
        }

        return tableBuilder.build().render(180);
    }

    /**
     * Returns table of all cnpg backups.
     *
     * @param backups list of cnpg backups
     * @return table as String
     */
    public String cnpgBackups(List<CnpgBackupResponse> backups) {
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("backupId", "ID");
        headers.put("name", "NAME");
        headers.put("isScheduled", "IS_SCHEDULED");
        headers.put("status", "STATUS");
        headers.put("createdAt", "CREATED_AT");

        TableModel model = new BeanListTableModel<>(backups, headers);

        TableBuilder tableBuilder = new TableBuilder(model)
                .addInnerBorder(BorderStyle.oldschool)
                .addHeaderBorder(BorderStyle.fancy_light);

        for (int i = 0; i < backups.size() + 1; i++) {
            for (int j = 0; j < headers.size(); j++) {
                tableBuilder.on(at(i, j)).addFormatter(new SpaceFormatter()).addAligner(SimpleHorizontalAligner.center);
            }
        }

        return tableBuilder.build().render(180);
    }

    /**
     * Returns 2 columns table for single entity.
     *
     * @param obj target entity
     * @return table as String
     */
    public String singleEntityTable(Object obj) {
        SingleEntityTableModelBuilder tableModelBuilder = new SingleEntityTableModelBuilder(obj, mapper);
        TableModel model = tableModelBuilder.build();
        TableBuilder tableBuilder = new TableBuilder(model);
        return tableBuilder
                .addFullBorder(BorderStyle.fancy_light)
                .on(CellMatchers.column(0)).addFormatter(value -> new String[]{value + "  "})
                .on(CellMatchers.column(1)).addFormatter(value -> new String[]{value + "  "})
                .build()
                .render(180);
    }

    /**
     * Custom formatter for table cells to add some spaces.
     */
    private static class SpaceFormatter implements Formatter {
        @Override
        public String[] format(Object value) {
            String[] result = new String[1];
            result[0] = value + "  ";
            return result;
        }
    }

}
