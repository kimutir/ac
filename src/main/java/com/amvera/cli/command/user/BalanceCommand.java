package com.amvera.cli.command.user;

import com.amvera.cli.dto.billing.BalanceResponse;
import com.amvera.cli.service.BalanceService;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;

@Command(group = "User commands")
public class BalanceCommand {
    private final BalanceService balanceService;
    private final ShellHelper helper;

    public BalanceCommand(BalanceService balanceService, ShellHelper helper) {
        this.balanceService = balanceService;
        this.helper = helper;
    }

    @Command(command = "balance", description = "Current balance")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public void balance() {
        BalanceResponse balance = balanceService.getBalance();
        helper.println("Your current balance:");
        helper.println(balance.balance().toString() + " " + balance.currency());
    }

}
