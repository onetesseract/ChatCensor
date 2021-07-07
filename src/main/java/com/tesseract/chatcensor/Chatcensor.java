package com.tesseract.chatcensor;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Map;

@Plugin(
        id = "chatcensor",
        name = "Chatcensor",
        version = "1.0-SNAPSHOT",
        authors = {"onetesseract"}
)
public class Chatcensor {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final File configFile;
    public static String censor;
    public static ArrayList<String> banned;
    public static boolean caseSensitive;

    @Inject
    public Chatcensor(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.logger = logger;
        this.server = server;
        this.dataDirectory = dataDirectory;
        if(!dataDirectory.toFile().exists()) {
            dataDirectory.toFile().mkdir();
        }
        File config = new File(dataDirectory.toFile(), "config.yml");
        this.configFile = config;
        if (!config.exists()) {
            createDefaultConfig();
        }
        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(config);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Yaml yaml = new Yaml();
        Map<String, Object> yml = yaml.load(fileIn);
        if (yml == null) {
            createDefaultConfig();
            try {
                fileIn = new FileInputStream(config);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            yml = yaml.load(fileIn);
        }
        this.censor = (String) yml.get("censor");
        this.banned = (ArrayList<String>) yml.get("banned");
        this.caseSensitive = (boolean) yml.get("caseSensitive");


        logger.info("Banned words: " + banned.toString());
        logger.info("Censor: " + censor);
    }

    private void createDefaultConfig() {
        try {
            // config.createNewFile();
            FileWriter writer = new FileWriter(configFile);
            InputStream defaultFile = this.getClass().getClassLoader().getResourceAsStream("config.yml");
            FileOutputStream fileOut = new FileOutputStream(configFile);
            Files.copy(defaultFile, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            fileOut.close();
            defaultFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getEventManager().register(this, new ChatListener());
    }
}
