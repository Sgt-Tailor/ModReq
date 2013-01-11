package modreq;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class VersionChecker implements Runnable {
	
	private modreq plugin;
	public VersionChecker(modreq instance) {
		plugin = instance;
	}
	public String getVersion() throws Exception {
        URL website = new URL("http://www.wampiedriessen.eu/sven/modreqversion.txt");
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;
        
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
	public String getDownloadLink() {
		return null;
	}

	@Override
	public void run() {
		
		try {
			Bukkit.broadcastMessage(getVersion());
			plugin.latestVersion = getVersion();
		} catch (Exception e) {
			Logger.getLogger("Minecraft").info("Could not get latest version of ModReq  D:");
			e.printStackTrace();
		}
		
	}

}
