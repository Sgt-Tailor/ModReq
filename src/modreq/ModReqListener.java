package modreq;

import managers.TicketHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ModReqListener implements Listener{
	private modreq plugin;
	public ModReqListener(modreq instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		if(p.hasPermission("modreq.check")) {
		
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {//check for open tickets
					
				   public void run() {
					   TicketHandler th = new TicketHandler();
					   int opentickets = th.getOpenTicketsAmount();
					   if(opentickets > 0) {
						p.sendMessage(ChatColor.GOLD+"[ModReq]" + ChatColor.GREEN + Integer.toString(opentickets) +" "+ plugin.Messages.getString("notification", "open tickets are waiting for you"));
							
					   }
						  
					
					 
				   }
			}, 60L);		
		}
		if(p.hasPermission("modreq.update")) {
			if(plugin.getConfig().getBoolean("check-updates",true)) {
				String currentVersion = plugin.getDescription().getVersion();
				if(plugin.latestVersion != null && currentVersion != null) {
					if(!plugin.latestVersion.equals(currentVersion)) {
						p.sendMessage(ChatColor.GOLD+"[ModReq]" + ChatColor.DARK_PURPLE + "A newer version of ModReq is available. If you wish to download this file to the modreq folder do /updatemodreq");
					}
				}
			}
		}
	}
}
