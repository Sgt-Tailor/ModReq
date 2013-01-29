package commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import korik.SubCommandExecutor;
import modreq.modreq;

public class ReloadCommand extends SubCommandExecutor{
    private modreq plugin;
    
    public ReloadCommand(modreq instance) {
	plugin = instance;
    }
    
    @command
    public void Null(CommandSender sender, String[] args) {
	if(sender.hasPermission("modreq.reload")) {
	    plugin.reload();
	    sender.sendMessage(ChatColor.GREEN + "Modreq Reloaded");
	}
    }

}
