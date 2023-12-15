package com.amvera.cli.command.user;

import com.amvera.cli.dto.billing.BalanceGetResponse;
import com.amvera.cli.service.BalanceService;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class BalanceCommand {
    private final BalanceService balanceService;
    private final ShellHelper helper;

    public BalanceCommand(BalanceService balanceService, ShellHelper helper) {
        this.balanceService = balanceService;
        this.helper = helper;
    }

    @ShellMethod(
            key = "balance",
            value = "Current balance"
    )
    public String balance() {
        BalanceGetResponse balance = balanceService.getBalance();
        helper.println("Your current balance:");
        return balance.balance().toString() + " " + balance.currency();
    }

}
