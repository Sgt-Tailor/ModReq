package commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import korik.SubCommandExecutor;
import modreq.modreq;

public class ModsCommand extends SubCommandExecutor{
    private modreq plugin;
    public ModsCommand(modreq instance) {
	plugin = instance;
    }
    
    @command
    public void Null(CommandSender sender, String[] args) {
	if(sender.hasPermission("modreq.mods")) {
		
		sender.sendMessage(ChatColor.GOLD+plugin.Messages.getString("list-of-mods", "-------List-of-Online-Mods-------"));
		
		Player[] op = Bukkit.getOnlinePlayers();
		String online = "";
		for(int i=0; i<op.length;i++) {
			if(op[i].hasPermission("modreq.check")) {
				if(sender instanceof Player) {
					if(((Player) sender).canSee(op[i])){
						if(i==0) {
							online = op[i].getDisplayName();
						}
						else {
							online = online + " " + op[i].getDisplayName();
						}
					}
					
				}
				else {
					if(i==0) {
						online = op[i].getDisplayName();
					}
					else {
						online = online + " " + op[i].getDisplayName();
					}
				}
					
			
			}
		}
		if(online.equals("")) {
			sender.sendMessage(ChatColor.GRAY + plugin.Messages.getString("no-mods", "There are no mods online"));
			return;
		}
		sender.sendMessage(online);
		return;
	}
	else {
		sender.sendMessage(ChatColor.RED + plugin.Messages.getString("no-permission", "You don't have permissions to do this)"));
		return;
	}
    }
}


