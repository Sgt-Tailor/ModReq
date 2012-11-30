package modreq;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class modreq extends JavaPlugin  {
	public static modreq plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public ModReqCommandExecutor myExecutor;
	public File configFile;
	public FileConfiguration config;
	
	

	
	@Override
	public void onEnable() {
		config = new YamlConfiguration();
		configFile = new File(getDataFolder().getAbsolutePath()+ "/config.yml");
		if(!configFile.exists()){
			firstrun();
		}
		myExecutor = new ModReqCommandExecutor(this);
		if(!this.getConfig().getString("version").equalsIgnoreCase(this.getDescription().getVersion())){
			logger.info("[ModReq] You plugin version does not match the config version. Please visit the bukkitdev page for more information");
		}
		getCommand("modreq").setExecutor(myExecutor);
		getCommand("check").setExecutor(myExecutor);
		getCommand("tp-id").setExecutor(myExecutor);
		getCommand("claim").setExecutor(myExecutor);
		getCommand("re-open").setExecutor(myExecutor);
		getCommand("status").setExecutor(myExecutor);
		getCommand("done").setExecutor(myExecutor);
		getCommand("mods").setExecutor(myExecutor);
		getCommand("modhelp").setExecutor(myExecutor);
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new ModReqListener(this), this);
		PluginDescriptionFile pdfFile = this.getDescription();
		checkSQL();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled.");
	
		startNotify();
	}
	
	@SuppressWarnings("unused")
	private void checkSQL() {
		TicketHandler th = new TicketHandler();
		
	}

	private void startNotify() {
		if(this.getConfig().getBoolean("notify-on-time")){
			logger.info("[ModReq] Notifying on time enabled");
			long time = this.getConfig().getLong("time-period");
			time = time * 1200;
			Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
				
				   public void run() {
					   TicketHandler th = new TicketHandler();
					   int opentickets = th.getOpenTicketsAmount();
					   if(opentickets > 0) {
						   Player[] online = Bukkit.getOnlinePlayers();
						   for(int i=0; i<online.length;i++) {
							   if(online[i].hasPermission("modreq.check")) {
								   online[i].sendMessage(ChatColor.GOLD+"[ModReq]" + ChatColor.GREEN + "There are "+ Integer.toString(opentickets) + " open Tickets waiting for you!");
							   }
						   }
					   }
						  
					
					 
				   }
			}, 60L, time);
		}
		
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled ");
	}
	
	private void firstrun() {//create the config.yml
		this.saveDefaultConfig();
	}
	
	public void saveYaml() {
		
	}
}