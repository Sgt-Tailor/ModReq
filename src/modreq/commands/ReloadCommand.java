package modreq.commands;

import modreq.ModReq;
import modreq.korik.SubCommandExecutor;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommandExecutor{
    private ModReq plugin;
    
    public ReloadCommand(ModReq instance) {
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
