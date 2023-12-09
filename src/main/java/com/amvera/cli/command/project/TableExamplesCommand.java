package com.amvera.cli.command.project;

import com.amvera.cli.command.auth.LoginCommand;
import com.amvera.cli.exception.CustomException;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import java.io.IOException;

@ShellComponent
public class TableExamplesCommand {
    public String[] CONTINENTS = {"Europe", "North America", "South America", "Africa", "Asia", "Austraila and Oceania"};
    public String[] COUNTRIES1 = {"Germany", "Америка", "Brasil", "Nigeria", "China", "Australia"};
    public String[] COUNTRIES2 = {"France", "Canada", "Argentina", "Egypt", "India", "New Zeeland"};
    private final ShellHelper shellHelper;
    private final LoginCommand loginCommand;

    public TableExamplesCommand(ShellHelper shellHelper, LoginCommand loginCommand) {
        this.shellHelper = shellHelper;
        this.loginCommand = loginCommand;
    }

    @ShellMethod("Display sample tables")
    public void table() throws IOException, InterruptedException {
        Object[][] sampleData = new String[][] {
                CONTINENTS,
                COUNTRIES1,
                COUNTRIES2
        };
        TableModel model = new ArrayTableModel(sampleData);
        TableBuilder tableBuilder = new TableBuilder(model);
        shellHelper.printInfo("oldschool border style");
        tableBuilder.addFullBorder(BorderStyle.oldschool);
        shellHelper.print(tableBuilder.build().render(180));
    }
}
