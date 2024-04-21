package com.npcanimator.utils;

import com.google.gson.JsonObject;

public class SimpleEmote {

    String name;
    int id;

    public SimpleEmote(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public SimpleEmote (JsonObject jsonObject){
        this.name = jsonObject.get("name").getAsString();
        this.id = jsonObject.get("id").getAsInt();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public JsonObject toJSON() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("id", id);

        return jsonObject;
    }
}
