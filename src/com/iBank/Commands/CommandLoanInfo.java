package com.iBank.Commands;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

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
 *  /bank loaninfo <PLAYER> - loan info of others
 *  /bank loaninfo Display your loan info
 * @author steffengy
 *
 */
@CommandInfo(
		arguments = { "Player" }, 
		permission = "iBank.access",
		root = "bank", 
		sub = "loaninfo"
)
public class CommandLoanInfo implements Command {
	public void handle(CommandSender sender, String[] arguments) {
		handle(sender, arguments, false);
	}
	public void handle(CommandSender sender, String[] arguments, boolean check) {
		if(!check && !iBank.canExecuteCommand(((Player)sender))) {
			MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorNotRegion.toString());
			return;
		}
		if(arguments.length == 0) {
			if(!(sender instanceof Player)) {
				MessageManager.send(sender, Configuration.StringEntry.ErrorNoPlayer.toString());
				return;
			}
			showLoanInfo(((Player)sender).getName(), sender, 0);
		}else if(arguments.length == 1 || arguments.length == 2) {
			char tmp;
			if((tmp = arguments[0].charAt(0)) >= '0' && tmp <= '9') {
				showLoanInfo(((Player)sender).getName(), sender, Integer.parseInt(arguments[0]));
				return;
			}
			boolean allowed = (!(sender instanceof Player)) || iBank.hasPermission(sender, "iBank.loaninfo"); 
			if(allowed){
				int site = arguments.length > 1 ? Integer.parseInt(arguments[1]) +1 : 0;
				showLoanInfo(arguments[0], sender, site);
			}else{
				MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorNoAccess.getValue());
			}
		}else{
			MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorWrongArguments.getValue());
		}
	}
	/**
	 * Shows the loan info to destination
	 * @param user The to-info user
	 * @param destination The destination user
	 * @site which dataset shall be showed?
	 */
	public void showLoanInfo(String user, CommandSender destination,int site) {
		List<Loan> allLoans = null;
		if(user.equalsIgnoreCase("all!")) { 
			allLoans = Bank.getLoans();
		}else
		allLoans = Bank.getLoansByAccount(user);
		int sites = (int)Math.ceil(((double)allLoans.size() / 10));
		site = site > sites ? sites : site;
		
		MessageManager.send(destination, "&y&" + Configuration.StringEntry.GeneralInfo.toString().replace("$type$", "Player").replace("$name$", user));
		int i = 0;
		for(Loan loan : allLoans) {
			if(i < (site * 10)) { i++; continue; }
			if(i > (site * 10)) break;
			String[] lang = new String[] { Configuration.StringEntry.GeneralUntil.toString(), Configuration.StringEntry.GeneralPer.toString(), Configuration.StringEntry.GeneralMin.toString() };
			String date = new SimpleDateFormat("dd.MMM.yy HH:mm:ss").format(new Date(System.currentTimeMillis()+(loan.getLeftTime() * 1000)));
			String minutes = String.valueOf(loan.getInterval());
			MessageManager.send(destination, (i+1)+"."+" id:("+loan.getId()+") "+iBank.format(loan.getAmount())+" "+lang[0]+" "+date+" "+String.valueOf(loan.getInterest()) + "% " + lang[1] + " " + minutes+ " " +lang[2], "");
			i++;
		}
	}
	public String getHelp() {
		return Configuration.StringEntry.LoanInfoDescription.getValue();
	}
}
