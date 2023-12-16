package com.amvera.cli.command.user;

import com.amvera.cli.dto.billing.BalanceGetResponse;
import com.amvera.cli.exception.CustomException;
import com.amvera.cli.service.BalanceService;
import com.amvera.cli.utils.ShellHelper;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;

@Command
public class BalanceCommand {
    private final BalanceService balanceService;
    private final ShellHelper helper;

    public BalanceCommand(BalanceService balanceService, ShellHelper helper) {
        this.balanceService = balanceService;
        this.helper = helper;
    }

    @Command(command = "balance", description = "Current balance")
    @CommandAvailability(provider = "userLoggedOutProvider")
    public String balance() {
        BalanceGetResponse balance = balanceService.getBalance();
        helper.println("Your current balance:");
        throw new CustomException();
//        return balance.balance().toString() + " " + balance.currency();
    }

}
