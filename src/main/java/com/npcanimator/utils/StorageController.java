package com.npcanimator.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

// This class handles the saving and loading of data in JSON format
public class StorageController {
    static String packageName = "npcanimator";

    private static final File NPCANIMATOR_DIR = new File(RUNELITE_DIR, packageName);
    private static final File NPCANIMATOR_SAVE_DATA_DIR = new File(NPCANIMATOR_DIR, "data");


    public HashMap<Integer, AnimatedNPC> readNPCAnimationFile(String profileKey) {
        // Attempt to read saved npc animations file for profileKey
        try {
            String filePath = getPluginSaveFilePath(profileKey, "npc");
            assert filePath != null;
            FileReader reader = new FileReader(filePath);
            JsonArray fileContents = new JsonParser().parse(reader).getAsJsonArray();
            HashMap<Integer, AnimatedNPC> savedAnimatedNPCMap = new HashMap<>();

            for (JsonElement animatedNPCJSON : fileContents) {
                AnimatedNPC animatedNPC = new AnimatedNPC(animatedNPCJSON.getAsJsonObject());
                savedAnimatedNPCMap.put(animatedNPC.getId(), animatedNPC);
            }

            return savedAnimatedNPCMap;
        }
        catch (FileNotFoundException e) {
            // Failed to read npc animations file
        }
        return null;
    }

    public HashMap<Integer, SimpleEmote> readSavedEmotesFile(String profileKey) {
        // Attempt to read saved emotes file for profileKey
        try {
            String filePath = getPluginSaveFilePath(profileKey, "emote");
            assert filePath != null;
            FileReader reader = new FileReader(filePath);
            JsonArray fileContents = new JsonParser().parse(reader).getAsJsonArray();
            HashMap<Integer, SimpleEmote> savedEmoteMap = new HashMap<>();

            for (JsonElement emoteJSON : fileContents) {
                SimpleEmote emote = new SimpleEmote(emoteJSON.getAsJsonObject());
                savedEmoteMap.put(emote.getId(), emote);
            }

            return savedEmoteMap;
        }
        catch (FileNotFoundException e) {
            // Failed to read emotes file
        }
        return null;
    }

    public void writeFile(String profileKey, String contents, String type) {
        String filePath = "";
        try {
            filePath = getPluginSaveFilePath(profileKey, type);
            assert filePath != null;
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(contents);
            writer.close();
        }
        catch (IOException e) {
            // Unable to write JSON file at path: " + filePath + "\n" + e.getMessage())
        }

    }

    private String getDataFilePath(String fileName) {
        File directory = new File(NPCANIMATOR_SAVE_DATA_DIR + File.separator);
        directory.mkdirs();
        return directory + File.separator + fileName;
    }

    private String getPluginSaveFilePath(String profileKey, String type) {
        if (Objects.equals(type, "npc")) {
            String fileName = packageName + "-npc-" + profileKey + ".json";
            return getDataFilePath(fileName);
        }
        else if (Objects.equals(type, "emote")) {
            String fileName = packageName + "-emote-" + profileKey + ".json";
            return getDataFilePath(fileName);
        }
        return null;
    }
}