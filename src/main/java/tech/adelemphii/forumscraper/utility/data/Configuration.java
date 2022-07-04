package tech.adelemphii.forumscraper.utility.data;

import tech.adelemphii.forumscraper.utility.UserInput;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

public class Configuration {

    private String discordBotToken;
    private boolean headless;

    private String commandPrefix = "!";

    private static Configuration instance;

    public static Configuration getInstance() {
        if(instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public void loadConfiguration(File configurationFile) {
        if (!configurationFile.exists()) {
            try {
                configurationFile.getParentFile().mkdirs();
                configurationFile.createNewFile();

                Map<String, ?> config = createConfig();
                writeConfig(configurationFile, config);
                readConfig(configurationFile);
            } catch (IOException e) {
                System.out.println("Error creating configuration file.");
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            readConfig(configurationFile);
        }
    }

    private void readConfig(File configurationFile) {
        // read the config from the file
        try {
            Scanner scanner = new Scanner(configurationFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitLine = line.split(": ");
                if(splitLine.length == 2) {
                    String key = splitLine[0];
                    String value = splitLine[1];
                    if(key.equalsIgnoreCase("discordBotToken")) {
                        discordBotToken = value;
                    } else if(key.equals("headless")) {
                        headless = Boolean.parseBoolean(value);
                    } else if(key.equalsIgnoreCase("commandPrefix")) {
                        commandPrefix = value;
                    }
                }
            }
            if(commandPrefix == null || commandPrefix.isEmpty() || commandPrefix.isBlank()) {
                commandPrefix = "!";
            }
        } catch (IOException e) {
            System.out.println("Error reading configuration file.");
            System.exit(1);
            return;
        }
        System.out.println(this);
    }

    private void writeConfig(File configurationFile, Map<String, ?> config) {
        // write the config to the file
        try {
            configurationFile.createNewFile();
            // write config using a buffered writer
            BufferedWriter writer = new BufferedWriter(new FileWriter(configurationFile));
            for (Map.Entry<String, ?> entry : config.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
            writer.close();
            System.out.println("Config file saved at: " + configurationFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error writing configuration file.");
            System.exit(1);
        }
    }

    private Map<String, ?> createConfig() {
        Console console = System.console();
        String token = "";

        // if the console is null, it's being run from inside an IDE & masking inside an IDE is wonky
        if(console != null) {
            token = String.valueOf(System.console().readPassword("Discord bot token: "));
        } else {
            token = UserInput.getString("Discord bot token: ");
        }
        boolean headless = UserInput.getBoolean("Headless mode? (true/false): ");
        String commandPrefix = UserInput.getString("Command Prefix? (Default is '!'): ");
        //List<String> activityMessages = UserInput.getStringList("Custom Activity Messages");

        return Map.of("discordBotToken", token, "headless", headless, "commandPrefix", commandPrefix);
    }

    public String getDiscordBotToken() {
        return discordBotToken;
    }

    public void setDiscordBotToken(String discordBotToken) {
        this.discordBotToken = discordBotToken;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "discordBotToken='" + discordBotToken + '\'' +
                ", headless=" + headless +
                ", commandPrefix='" + commandPrefix + '\'' +
                '}';
    }
}
