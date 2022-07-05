package tech.adelemphii.forumscraper.utility.data;

import com.google.gson.Gson;
import tech.adelemphii.forumscraper.ForumScraper;
import tech.adelemphii.forumscraper.objects.Server;

import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class ServerStorageUtility {

    private static final Map<Long, Server> servers = new HashMap<>();
    private static File serversFile;

    public static Server getServer(Long id) {
        return servers.get(id);
    }

    public static Map<Long, Server> getServers() {
        return servers;
    }

    public static void addServer(Server server) {
        servers.put(server.getServerID(), server);
    }

    public static void removeServer(Long id) {
        servers.remove(id);
    }

    private static void loadFile() {
        try {
            serversFile = new File(ForumScraper.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            serversFile = new File(serversFile.getAbsolutePath() + "/files/servers.json");
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void saveServers() {
        if(serversFile == null) {
            loadFile();
        }
        Gson gson = new Gson();

        try {
            serversFile.getParentFile().mkdir();
            serversFile.createNewFile();
            Writer writer = new FileWriter(serversFile, false);
            gson.toJson(servers.values(), writer);
            writer.flush();
            writer.close();
            System.out.println("Server information saved.");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadServers() {
        if(serversFile == null) {
            loadFile();
        }
        Gson gson = new Gson();
        try {
            if(serversFile.exists()) {
                Reader reader = new FileReader(serversFile);
                Server[] serverArray = gson.fromJson(reader, Server[].class);

                if(serverArray == null) {
                    return;
                }

                for (Server server : serverArray) {
                    servers.put(server.getServerID(), server);
                }
                System.out.println("Server information has been loaded.");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
