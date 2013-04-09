package modreq;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

public class VersionChecker implements Runnable {

    private ModReq plugin;
    private String currentVersion;
    private Logger logger = Logger.getLogger("Minecraft");

    public VersionChecker(ModReq instance) {
	plugin = instance;
	currentVersion = plugin.getCurrentVersion();
	logger.info("current version " + currentVersion);
    }

    public String getVersion() throws Exception {
	URL website = new URL(
		"http://website.shadowblox.com/plugins/modreqversion.txt");
	URLConnection connection = website.openConnection();
	BufferedReader in = new BufferedReader(new InputStreamReader(
		connection.getInputStream()));

	StringBuilder response = new StringBuilder();
	String inputLine;

	while ((inputLine = in.readLine()) != null) {
	    response.append(inputLine);
	}
	in.close();

	return response.toString();
    }

    public String getDownloadLink() throws Exception {
	URL website = new URL(
		"http://website.shadowblox.com/plugins/downloadlink.txt");
	URLConnection connection = website.openConnection();
	BufferedReader in = new BufferedReader(new InputStreamReader(
		connection.getInputStream()));

	StringBuilder response = new StringBuilder();
	String inputLine;

	while ((inputLine = in.readLine()) != null) {
	    response.append(inputLine);
	}
	in.close();

	return response.toString();
    }

    @Override
    public void run() {

	try {
	    String latestVersion = getVersion();
	    String downloadLink = getDownloadLink();

	    plugin.latestVersion = latestVersion;
	    plugin.DownloadLink = downloadLink;

	    // logger.info("[ModReq] Latest version is " + latestVersion +
	    // ", comparing to current version");

	    if (!currentVersion.equals(latestVersion)) {
		logger.info("[ModReq] The current and the lastest version do not match, a newer version must be available! (or you are running an Alfa/Beta build)");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.info("Failed");
	}

    }

}
