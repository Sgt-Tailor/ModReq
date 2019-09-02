/*
 Modreq Minecraft/Bukkit server ticket system
 Copyright (C) 2013 Sven Wiltink

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package modreq;

import modreq.repository.TicketRepository;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ModReqListener implements Listener {

    private ModReq plugin;

    public ModReqListener(ModReq instance) {
        plugin = instance;
    }
    
    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        if (p.hasPermission("modreq.check")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    new Runnable() {
                @Override
                public void run() {
                    TicketRepository th = new TicketRepository();
                    int opentickets = th.getTicketCountByStatus(Status.OPEN);
                    if (opentickets > 0) {
                        p.sendMessage(ChatColor.GOLD + "[ModReq]" + ModReq.format(ModReq.getInstance().Messages.getString("staff.all.notification"), "", Integer.toString(opentickets),""));
                    }

                }
            }, 60L);
        }
    }
}
