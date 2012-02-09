package com.iBank.Commands;

import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.iBank.iBank;
import com.iBank.system.Bank;
import com.iBank.system.Command;
import com.iBank.system.CommandInfo;
import com.iBank.system.Configuration;
import com.iBank.system.MessageManager;
import com.iBank.Listeners.iBankListener;

@CommandInfo(
		arguments = { "Name" }, 
		help = "", 
		permission = "iBank.regions", 
		root = "bank", 
		sub = "addregion"
)

public class CommandAddRegion implements Command {

	/**
	 * Adds an region
	 *  /bank addregion REGIONNAME
	 *  Can't be run from console
	 */
	@Override
	public void handle(CommandSender sender, String[] arguments) {
		if(!(sender instanceof Player)) {
			MessageManager.send(sender, Configuration.StringEntry.ErrorNoPlayer.toString());
			return;
		}
		
		if(arguments.length==1) {
			Entry<Location, Location> raw = ((iBankListener)iBank.Listener).LastMarkedPoint.get(sender.getName());
			if(raw == null || raw.getKey()==null || raw.getValue()==null){
				MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorRegionSelect.toString());
				return;
			}
			if(!Bank.hasRegion(arguments[0])) {
				Bank.createRegion(arguments[0], raw.getKey(), raw.getValue());
				MessageManager.send(sender, "&g&"+Configuration.StringEntry.SuccessAddRegion.toString().replace("$name$", arguments[0]));
			}else{
				MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorAlreadyExists.toString().replace("$name$", "Region "+arguments[0]+" "));
				return;
			}
		}else{
			MessageManager.send(sender, "&r&"+Configuration.StringEntry.ErrorWrongArguments.toString());
		}
	}

}
