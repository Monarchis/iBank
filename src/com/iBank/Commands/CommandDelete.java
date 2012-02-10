package com.iBank.Commands;

import org.bukkit.command.CommandSender;

import com.iBank.system.Bank;
import com.iBank.system.Command;
import com.iBank.system.CommandInfo;
import com.iBank.system.Configuration;
import com.iBank.system.MessageManager;

/**
 *  /bank delete <NAME>
 * @author steffengy
 *
 */
@CommandInfo(
		arguments = { "Name" }, 
		help = "", 
		permission = "iBank.manage",
		root = "bank", 
		sub = "delete"
)
public class CommandDelete implements Command {
	public void handle(CommandSender sender, String[] arguments) { 
		if(arguments.length == 1) {
			if(Bank.hasAccount(arguments[0])) {
				Bank.removeAccount(arguments[0]);
				MessageManager.send(sender, "&g&"+Configuration.StringEntry.SuccessDelAccount.toString().replace("$name$", arguments[0]));
			}else{
				MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorNotExist.toString().replace("$name$", arguments[0]));
			}
		}else{
			MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorWrongArguments.toString());
		}
	}
	public String getHelp() {
		return Configuration.StringEntry.DeleteDescription.getValue();
	}
}
