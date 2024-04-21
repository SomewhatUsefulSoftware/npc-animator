package com.npcanimator;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Provides;
import com.npcanimator.utils.AnimatedNPC;
import com.npcanimator.utils.Icons;
import com.npcanimator.utils.SimpleEmote;
import com.npcanimator.utils.StorageController;
import com.npcanimator.views.EmoteAnimationRow;
import com.npcanimator.views.NPCAnimationRow;
import com.npcanimator.views.NPCAnimatorPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;

import javax.inject.Inject;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@PluginDescriptor(
        name = "NPC Animator",
        description = "Set and run custom animations on NPCs and your own character",
        tags = {"animations", "npcs"}
)

public class NPCAnimatorPlugin extends Plugin {
    private String profileKey;

    HashMap<Integer, AnimatedNPC> savedAnimatedNPCMap = new HashMap<>();
    HashMap<Integer, AnimatedNPC> spawnedAnimatedNPCMap = new HashMap<>();
    HashMap<Integer, SimpleEmote> playerSavedEmotesMap = new HashMap<>();
    Player myPlayer;

    // UI
    private NPCAnimatorPanel panel;
    private NavigationButton navButton;
    private boolean populateSpawnedNPCs;

    @Inject
    private Client client;

    @Inject
    private Gson gson;

    @Inject
    private NPCAnimatorConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private ItemManager itemManager;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private StorageController storageController;


    @Provides
    NPCAnimatorConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(NPCAnimatorConfig.class);
    }


    @Override
    protected void startUp() throws Exception {
        profileKey = null;
        panel = new NPCAnimatorPanel(this);
        spriteManager.getSpriteAsync(SpriteID.EMOTE_CHEER, 0, panel::loadHeaderIcon);

        navButton = NavigationButton.builder()
                .tooltip("NPC Animator")
                .icon(Icons.NAV_BUTTON)
                .priority(5)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);

        String profileKey = configManager.getRSProfileKey();
        switchProfile(profileKey);
    }


    /*   SUBSCRIPTIONS   */

    @Subscribe
    public void onRuneScapeProfileChanged(RuneScapeProfileChanged e) {
        final String profileKey = configManager.getRSProfileKey();
        if (profileKey == null) {
            return;
        }

        if (profileKey.equals(this.profileKey)) {
            return;
        }

        switchProfile(profileKey);
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        String key = configChanged.getKey();
        if (key.equals("populateSpawnedNPCs")) {
            populateSpawnedNPCs = Boolean.parseBoolean(configChanged.getNewValue());
            if (!populateSpawnedNPCs) {
                spawnedAnimatedNPCMap.clear();
                SwingUtilities.invokeLater(() -> panel.removeAllSpawnedNPCs());
            }
        }
    }

    /**
        // Only needed for getting the animation IDs for the default emotes
        // @Subscribe
        public void onAnimationChanged(AnimationChanged animationChanged) {
            System.out.println("Animation Changed");
            Actor actor = animationChanged.getActor();
            System.out.println(actor.getName() + ": " + Integer.toString(actor.getAnimation()));
            if (animationChanged.getActor().equals(client.getLocalPlayer())) {
                System.out.println(animationChanged.getActor().getAnimation());
            }
        }
     **/

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        GameState gameState = gameStateChanged.getGameState();
        if (gameState == GameState.LOGGED_IN) {
            this.myPlayer = client.getLocalPlayer();
            panel.setPlayerForAnimatorPanel(this.myPlayer);
        }
        else if (gameState == GameState.LOGIN_SCREEN || gameState == GameState.HOPPING) {
            this.myPlayer = null;
            spawnedAnimatedNPCMap.clear();
            SwingUtilities.invokeLater(() -> panel.removeAllSpawnedNPCs());
            SwingUtilities.invokeLater(() -> panel.resetAllSavedNPCRows());
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        if (!populateSpawnedNPCs) {
            return;
        }
        NPC npc = npcSpawned.getNpc();
        int npcID = npc.getId();
        AnimatedNPC savedAnimatedNPC = null;

        if (Objects.equals(npc.getName(), "null")) {
            return;
        }

        if (!spawnedAnimatedNPCMap.containsKey(npcID)) {
            // create an AnimatedNPC object
            AnimatedNPC spawnedAnimatedNPC = new AnimatedNPC(npc);

            if (savedAnimatedNPCMap.containsKey(npcID)) {
                savedAnimatedNPC = savedAnimatedNPCMap.get(npcID);
                savedAnimatedNPC.setNPCObject(npc);
                // Set NPC visibility in saved list
                SwingUtilities.invokeLater(() -> panel.setSavedNPCVisible(npcID, true));
                spawnedAnimatedNPC = savedAnimatedNPC;
            }
            spawnedAnimatedNPCMap.put(npcID, spawnedAnimatedNPC);
            AnimatedNPC finalSpawnedAnimatedNPC = spawnedAnimatedNPC;
            SwingUtilities.invokeLater(() -> panel.addSpawnedNPC(finalSpawnedAnimatedNPC));
        }
        else {
            // update the AnimatedNPC object idCount
            AnimatedNPC spawnedAnimatedNPC = spawnedAnimatedNPCMap.get(npcID);
            spawnedAnimatedNPC.increaseIDCount(npc);
            spawnedAnimatedNPCMap.put(npcID, spawnedAnimatedNPC);
            SwingUtilities.invokeLater(() -> panel.updateSpawnedNPC(spawnedAnimatedNPC, true));

            if (savedAnimatedNPCMap.containsKey(npcID)) {
                savedAnimatedNPC = savedAnimatedNPCMap.get(npcID);
                savedAnimatedNPC.increaseIDCount(npc);
                savedAnimatedNPCMap.put(npcID, savedAnimatedNPC);
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        if (!populateSpawnedNPCs) {
            return;
        }
        NPC npc = npcDespawned.getNpc();
        int npcID = npc.getId();

        if (spawnedAnimatedNPCMap.containsKey(npcID)) {
            AnimatedNPC despawnedAnimatedNPC = spawnedAnimatedNPCMap.get(npcID);
            if (despawnedAnimatedNPC.getIDCount() <= 1) {
                spawnedAnimatedNPCMap.remove(npcID);
                SwingUtilities.invokeLater(() -> panel.removeSpawnedNPC(npcID));
            }
            else {
                despawnedAnimatedNPC.decreaseIDCount(npc);
                spawnedAnimatedNPCMap.put(npcID, despawnedAnimatedNPC);
                SwingUtilities.invokeLater(() -> panel.updateSpawnedNPC(despawnedAnimatedNPC, false));
            }
        }

        if (savedAnimatedNPCMap.containsKey(npcID)) {
            AnimatedNPC despawnedAnimatedNPC = savedAnimatedNPCMap.get(npcID);
            despawnedAnimatedNPC.decreaseIDCount(npc);
            savedAnimatedNPCMap.put(npcID, despawnedAnimatedNPC);
            if (despawnedAnimatedNPC.getIDCount() == 0) {
                // Set NPC visibility in saved list
                SwingUtilities.invokeLater(() -> panel.setSavedNPCVisible(npcID, false));
            }
        }
    }


    @Subscribe
    public void onInteractingChanged(InteractingChanged interactingChanged)
    {
        Actor sourceActor = interactingChanged.getSource();
        if (sourceActor.equals(client.getLocalPlayer())) {
            Actor targetActor = interactingChanged.getTarget();
            if (targetActor == null) {
                return;
            }

            if (targetActor instanceof NPC) {
                NPC targetNPC = (NPC) targetActor;
                int npcID = targetNPC.getId();

                // Run interaction animation if needed
                if (savedAnimatedNPCMap.containsKey(npcID)) {
                    AnimatedNPC selectedNPC = savedAnimatedNPCMap.get(npcID);
                    selectedNPC.runNPCInteractionAnimation(targetNPC);
                }
            }
        }
    }


    /*   Handling Profile Changes   */

    private void switchProfile(String profileKey) {
        populateSpawnedNPCs = config.populateSpawnedNPCs();

        executor.execute(() -> {
            this.profileKey = profileKey;
            if (this.profileKey != null) {
                HashMap<Integer, AnimatedNPC> savedList = storageController.readNPCAnimationFile(this.profileKey);
                HashMap<Integer, SimpleEmote> playerEmoteList = storageController.readSavedEmotesFile(this.profileKey);
                if (savedList != null) {
                    savedAnimatedNPCMap = savedList;
                    SwingUtilities.invokeLater(() -> panel.addFromSaveFile(savedList));
                }
                if (playerEmoteList != null) {
                    playerSavedEmotesMap = playerEmoteList;
                    SwingUtilities.invokeLater(() -> panel.addEmotesFromSaveFile(playerSavedEmotesMap));
                }
            }
        });
    }


    /*   Run Animation Code   */

    public void runAnimationFromList(int animationID) {
        try {
            this.myPlayer.setAnimation(animationID);
            this.myPlayer.setAnimationFrame(0);
        }
        catch (Exception e) {
            // System.out.println(e.getMessage());
        }
    }

    public void runGreetingsForAllSavedNPCs() {
        for (AnimatedNPC savedAnimatedNPC : savedAnimatedNPCMap.values()) {
            savedAnimatedNPC.setAnimation(savedAnimatedNPC.getGreetingID());
        }
    }

    public void runAnimationForAllSavedNPCs(int animationID) {
        for (AnimatedNPC savedAnimatedNPC : savedAnimatedNPCMap.values()) {
            savedAnimatedNPC.setAnimation(animationID);
        }
    }
    public void runAnimationForAllSpawnedNPCs(int animationID) {
        for (AnimatedNPC spawnedAnimatedNPC : spawnedAnimatedNPCMap.values()) {
            spawnedAnimatedNPC.setAnimation(animationID);
        }
    }


    /*   NPC Handling   */

    public void addNPCToSaved(AnimatedNPC animatedNPC) {
        int animatedNPCID = animatedNPC.getId();
        if (!savedAnimatedNPCMap.containsKey(animatedNPCID)) {
            // NPC not saved -> add it
            savedAnimatedNPCMap.put(animatedNPCID, animatedNPC);
            SwingUtilities.invokeLater(() -> panel.addSavedNPC(animatedNPC));
            saveAnimatedNPCsToFile();
        }
        else {
            // NPC already saved -> update it
            savedAnimatedNPCMap.put(animatedNPCID, animatedNPC);
            SwingUtilities.invokeLater(() -> panel.updateSavedNPC(animatedNPC));
            saveAnimatedNPCsToFile();
        }
    }

    public void deleteNPCFromSaved(AnimatedNPC animatedNPC, NPCAnimationRow row) {
        int animatedNPCID = animatedNPC.getId();
        if (!savedAnimatedNPCMap.containsKey(animatedNPCID)) {
            return;
        }
        savedAnimatedNPCMap.remove(animatedNPCID);
        saveAnimatedNPCsToFile();
        SwingUtilities.invokeLater(() -> panel.removeSavedNPC(row));
    }

    void saveAnimatedNPCsToFile() {
        if (Strings.isNullOrEmpty(this.profileKey)) {
            // No profile key -> cannot save
            return;
        }

        ArrayList<JsonObject> jsonObjects = new ArrayList<>();

        for (AnimatedNPC savedAnimatedNPC : savedAnimatedNPCMap.values()) {
            jsonObjects.add(savedAnimatedNPC.toJSON());
        }

        String jsonAll = gson.toJson(jsonObjects);
        storageController.writeFile(this.profileKey, jsonAll, "npc");
    }


    /*   Emote Handling   */

    void saveEmotesToFile() {
        if (Strings.isNullOrEmpty(this.profileKey)) {
            // No profile key -> cannot save
            return;
        }

        ArrayList<JsonObject> jsonObjects = new ArrayList<>();

        for (SimpleEmote savedEmote : playerSavedEmotesMap.values()) {
            jsonObjects.add(savedEmote.toJSON());
        }

        String jsonAll = gson.toJson(jsonObjects);
        storageController.writeFile(this.profileKey, jsonAll, "emote");
    }

    public boolean addEmoteToSaved(SimpleEmote emote) {
        if (!playerSavedEmotesMap.containsKey(emote.getId())) {
            playerSavedEmotesMap.put(emote.getId(), emote);
            SwingUtilities.invokeLater(() -> panel.addSavedEmote(emote));
            saveEmotesToFile();
            return true;
        }
        return false;
    }

    public void deleteEmoteFromSaved(SimpleEmote emote, EmoteAnimationRow row) {
        int id = emote.getId();
        if (!playerSavedEmotesMap.containsKey(id)) {
            return;
        }
        playerSavedEmotesMap.remove(id);
        saveEmotesToFile();
        SwingUtilities.invokeLater(() -> panel.removeSavedEmote(row));
    }
}
