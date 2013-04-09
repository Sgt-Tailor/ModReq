package modreq.commands;

import modreq.ModReq;
import modreq.korik.SubCommandExecutor;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ModhelpCommand extends SubCommandExecutor{
    private ModReq plugin;
    public ModhelpCommand(ModReq instance) {
	plugin = instance;
    }
    
    @command
    public void Null(CommandSender sender, String[] args) {
	
		sender.sendMessage(ChatColor.YELLOW + "----ModReq-Help-Message----");
		if(sender.hasPermission("modreq.request")) {sender.sendMessage(ChatColor.GOLD + "/modreq <message> " + ChatColor.WHITE + plugin.Messages.getString("info-modreq"));
		}if(sender.hasPermission("modreq.check")) {sender.sendMessage(ChatColor.GOLD + "/check <page> " + ChatColor.WHITE + plugin.Messages.getString("info-check"));
		}if(sender.hasPermission("modreq.check")) {sender.sendMessage(ChatColor.GOLD + "/check claimed <page> " + ChatColor.WHITE + plugin.Messages.getString("info-check"));
		}if(sender.hasPermission("modreq.check")) {sender.sendMessage(ChatColor.GOLD + "/check closed <page> " + ChatColor.WHITE + plugin.Messages.getString("info-check"));
		}if(sender.hasPermission("modreq.check")) {sender.sendMessage(ChatColor.GOLD + "/check id <id> " + ChatColor.WHITE + plugin.Messages.getString("info-ticket"));
		}if(sender.hasPermission("modreq.check")) {sender.sendMessage(ChatColor.GOLD + "/ticket " + ChatColor.WHITE + plugin.Messages.getString("info-ticket"));
		}if(sender.hasPermission("modreq.tp-id")) {sender.sendMessage(ChatColor.GOLD + "/tp-id <number> " + ChatColor.WHITE + plugin.Messages.getString("info-tp-id"));
		}if(sender.hasPermission("modreq.claim")) {sender.sendMessage(ChatColor.GOLD + "/claim <number> " + ChatColor.WHITE + plugin.Messages.getString("info-claim"));
		}if(sender.hasPermission("modreq.check")) {sender.sendMessage(ChatColor.GOLD + "/comment <id> <comment> " + ChatColor.WHITE + plugin.Messages.getString("info-comment"));
		}if(sender.hasPermission("modreq.reopen")) {sender.sendMessage(ChatColor.GOLD + "/re-open <number> (message) " + ChatColor.WHITE + plugin.Messages.getString("info-re-open"));
		}if(sender.hasPermission("modreq.status")) {sender.sendMessage(ChatColor.GOLD + "/status (number) " + ChatColor.WHITE + plugin.Messages.getString("info-status"));
		}if(sender.hasPermission("modreq.close")) {sender.sendMessage(ChatColor.GOLD + "/done <number> (message) " + ChatColor.WHITE + plugin.Messages.getString("info-done"));
		}if(sender.hasPermission("modreq.close")) {sender.sendMessage(ChatColor.GOLD + "/close <number> (message) " + ChatColor.WHITE + plugin.Messages.getString("info-done"));
		}if(sender.hasPermission("modreq.mods")) {sender.sendMessage(ChatColor.GOLD + "/mods " + ChatColor.WHITE + plugin.Messages.getString("info-mods"));
		}sender.sendMessage(ChatColor.GOLD + "/modhelp " + ChatColor.WHITE + plugin.Messages.getString("info-modhelp"));
		return; 
	
    }

}
