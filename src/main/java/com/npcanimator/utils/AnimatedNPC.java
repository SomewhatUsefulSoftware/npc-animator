package com.npcanimator.utils;

import com.google.gson.JsonObject;
import net.runelite.api.NPC;

import java.util.ArrayList;

public class AnimatedNPC {

    private NPC npc;
    private final String npcName;
    private final int npcID;

    private boolean greetingEnabled;
    private int greetingID;

    private int idCount = 1;

    private final ArrayList<NPC> npcList = new ArrayList<>();


    // Create an AnimatedNPC object from the UI
    public AnimatedNPC (final NPC npc) {
        this.npcID = npc.getId();
        this.npc = npc;
        this.npcName = npc.getName();
        this.npcList.add(npc);
    }

    // Create an AnimatedNPC object from a JSON Object
    public AnimatedNPC (JsonObject jsonObject){
        this.npcID = jsonObject.get("npcID").getAsInt();
        this.npcName = jsonObject.get("npcName").getAsString();
        this.greetingEnabled = jsonObject.get("greetingEnabled").getAsBoolean();
        this.greetingID = jsonObject.get("greetingID").getAsInt();
    }

    public void increaseIDCount (NPC npc) {
        this.idCount += 1;
        this.npcList.add(npc);
    }
    public void decreaseIDCount(NPC npc) {
        this.idCount -= 1;
        this.npcList.remove(npc);
    }

    public int getIDCount() {
        return this.idCount;
    }

    public void setNPCObject(NPC npc) {
        this.npc = npc;
        this.npcList.clear();
        this.npcList.add(npc);
    }

    public JsonObject toJSON() {
        /*
            {
                "npcName":"Eliza","npcID":11852,
                "greetingEnabled":true,"greetingID":711
            }
         */

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("npcName", npcName);
        jsonObject.addProperty("npcID", npcID);
        jsonObject.addProperty("greetingEnabled", greetingEnabled);
        jsonObject.addProperty("greetingID", greetingID);

        return jsonObject;
    }

    public void setGreetingEnabled(boolean greetingEnabled) { this.greetingEnabled = greetingEnabled; }
    public void setGreeting(int greetingID) {
        this.greetingID = greetingID;
    }

    public void runNPCInteractionAnimation(NPC selectedNPC) {
        // Only run the greeting animation if its enabled and an animation has been set
        if (greetingID > 0 && greetingEnabled) {
            selectedNPC.setAnimation(greetingID);
            selectedNPC.setAnimationFrame(0);
        }
    }

    public int getId() {
        return this.npcID;
    }

    public String getName() {
        return this.npcName;
    }

    public void setAnimation(int animationID) {
        for (NPC selectedNPC : npcList) {
            selectedNPC.setAnimation(animationID);
            selectedNPC.setAnimationFrame(0);
        }
    }

    public void stopAnimation() {
        for (NPC selectedNPC: npcList) {
            selectedNPC.setAnimation(-1);
            selectedNPC.setAnimationFrame(0);
        }
    }

    public boolean isGreetingEnabled() {
        return greetingEnabled;
    }

    public int getGreetingID() {
        return greetingID;
    }
}
