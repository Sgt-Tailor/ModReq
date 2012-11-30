package modreq;

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
		
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
				   public void run() {
					   TicketHandler th = new TicketHandler();
					   int opentickets = th.getOpenTicketsAmount();
					   if(opentickets > 0) {
						p.sendMessage(ChatColor.GOLD+"[ModReq]" + ChatColor.GREEN + "There are "+ Integer.toString(opentickets) + " open Tickets waiting for you!");
							
					   }
						  
					
					 
				   }
			}, 60L);
			
		}
	}
}
