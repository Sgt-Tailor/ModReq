package modreq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


public class modreq extends JavaPlugin  {
	public static modreq plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public ModReqCommandExecutor myExecutor;
	
	public File configFile;
	public File requestFile;
	public FileConfiguration req;
	public FileConfiguration config;
	
	

	
	@Override
	public void onEnable() {
		config = new YamlConfiguration();
		configFile = new File(getDataFolder().getAbsolutePath()+ "/config.yml");
		if(!configFile.exists()){
			firstrun();
		}
		loadYaml();
		myExecutor = new ModReqCommandExecutor(this);
		
		getCommand("modreq").setExecutor(myExecutor);
		getCommand("check").setExecutor(myExecutor);
		getCommand("tp-id").setExecutor(myExecutor);
		getCommand("claim").setExecutor(myExecutor);
		getCommand("re-open").setExecutor(myExecutor);
		getCommand("status").setExecutor(myExecutor);
		getCommand("done").setExecutor(myExecutor);
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled.");
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled ");
	}
	
	private void firstrun() {
		PluginDescriptionFile pdfFile = this.getDescription();
		if(!configFile.exists()){
			
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				
			}
			config.set("maximum-open-tickets", 5);
			this.logger.info("[" + pdfFile.getName()+ "]" + " config.yml successfully created");
		}
		
		saveYaml();
		
		
	}
	
	public void saveYaml() {
		try {
			config.save(configFile);
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public void loadYaml(){
		try {
			config.load(configFile);
		} catch (FileNotFoundException e) {
	
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}