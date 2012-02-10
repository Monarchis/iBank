package com.iBank.Commands;

import java.math.BigDecimal;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.iBank.iBank;
import com.iBank.system.Bank;
import com.iBank.system.Command;
import com.iBank.system.CommandInfo;
import com.iBank.system.Configuration;
import com.iBank.system.Loan;
import com.iBank.system.MessageManager;

/**
 *  /bank payback (ID) [AMOUNT]
 * @author steffengy
 *
 */
@CommandInfo(
		arguments = { "(ID)", "Amount" }, 
		help = "", 
		permission = "iBank.loan",
		root = "bank", 
		sub = "payback"
)
public class CommandPayBack implements Command {
	public void handle(CommandSender sender, String[] arguments) {
		handle(sender, arguments, false);
	}
	public void handle(CommandSender sender, String[] arguments, boolean check) {
		if(!(sender instanceof Player)) {
			MessageManager.send(sender, Configuration.StringEntry.ErrorNoPlayer.toString());
			return;
		}
		if(!check && !iBank.canExecuteCommand(((Player)sender))) {
			MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorNotRegion.toString());
			return;
		}
		if(arguments.length == 2) {
			//arguments[0] -> int
			int arg = 0;
			try{
				arg = Integer.parseInt(arguments[0]);
			}catch(Exception e) {
				MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorWrongArguments.toString()+" [ID]");
				return;
			}
			//arguments[1] -> BigDecimal
			BigDecimal todp = new BigDecimal("0.00");
			try{
				todp = new BigDecimal(arguments[1]);
			}catch(Exception e) {
				MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorWrongArguments.toString()+" [AMOUNT]");
				return;
			}
			if(!iBank.economy.has(((Player)sender).getName(), todp.doubleValue())) {
				MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorNotEnough.toString());
				return;
			}
			//try to get this loan
			Loan loan = Bank.getLoanById(arg);
			if(loan == null) {
				//notfound
				MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorNotExist.toString().replace("$name", String.valueOf(arg)));
			}else{
				//loan.getAmount() has to be bigger or equal than given (0 or -1)
				if(!(loan.getAmount().compareTo(todp)<=0)) {
					//throw error
					MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorWrongArguments.toString() + "AMOUNT>LOAN");
					return;
				}
				loan.setAmount(loan.getAmount().subtract(todp));
			    iBank.economy.withdrawPlayer(((Player)sender).getName(), todp.doubleValue());
				//<= to prevent MAGIC exceptions
				if(loan.getAmount().compareTo(new BigDecimal("0.00"))<=0) {
					loan.remove();
				}
				MessageManager.send(sender, "&g&"+Configuration.StringEntry.SuccessPayback.getValue().replace("$amount$", iBank.format(todp)));
			}
		}else if(arguments.length == 1){
			//loop through all, calculate stuff, etc.
			//arguments[0] -> BigDecimal
			BigDecimal todp = new BigDecimal("0.00");
			try{
				todp = new BigDecimal(arguments[0]);
			}catch(Exception e) {
				MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorWrongArguments.toString()+" [AMOUNT]");
				return;
			}
			if(!iBank.economy.has(((Player)sender).getName(), todp.doubleValue())) {
				MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorNotEnough.toString());
				return;
			}
			BigDecimal paiedback = new BigDecimal("0.00");
			for(Loan loan : Bank.getLoansByAccount(((Player)sender).getName())) {
				//todp > loan => remove loan, todp -= loan
				if(todp.compareTo(loan.getAmount()) >= 0) {
					iBank.economy.withdrawPlayer(((Player)sender).getName(), loan.getAmount().doubleValue());
					paiedback = paiedback.add(loan.getAmount());
					todp.subtract(loan.getAmount());
					loan.remove();
				}
				//todp < loan => subtract as much as possible, loan -= todp
				if(todp.compareTo(loan.getAmount()) < 0) {
					loan.setAmount(loan.getAmount().subtract(todp));
					paiedback = paiedback.add(todp);
					iBank.economy.withdrawPlayer(((Player)sender).getName(), todp.doubleValue());
					//break because no money left
					break;
				}
			}
			MessageManager.send(sender, "&g&"+Configuration.StringEntry.SuccessPayback.getValue().replace("$amount$", iBank.format(paiedback)));
		}else{
			MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorWrongArguments.toString());
		}
	}
	public String getHelp() {
		return Configuration.StringEntry.PayBackDescription.getValue();
	}
}
