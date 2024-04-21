package com.npcanimator.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StandardEmotes {

    HashMap<Integer, String> emoteList = new HashMap<>();
    ArrayList<SimpleEmote> emoteArrayList = new ArrayList<>();


    public StandardEmotes() {
        emoteList.put(855, "Yes");
        emoteList.put(856, "No");
        emoteList.put(858, "Bow");
        emoteList.put(859, "Angry");
        emoteList.put(857, "Think");
        emoteList.put(863, "Wave");
        emoteList.put(2113, "Shrug");
        emoteList.put(862, "Cheer");
        emoteList.put(864, "Beckon");
        emoteList.put(861, "Laugh");
        emoteList.put(2109, "Jump for Joy");
        emoteList.put(2111, "Yawn");
        emoteList.put(866, "Dance");
        emoteList.put(2106, "Jig");
        emoteList.put(2107, "Spin");
        emoteList.put(2108, "Headbang");
        emoteList.put(860, "Cry");
        emoteList.put(1374, "Blow Kiss");
        emoteList.put(2105, "Panic");
        emoteList.put(2110, "Raspberry");
        emoteList.put(865, "Clap");
        emoteList.put(2112, "Salute");
        emoteList.put(4276, "Idea");
        emoteList.put(4278, "Stamp");
        emoteList.put(4280, "Flap");
        emoteList.put(4275, "Slap Head");
        emoteList.put(3543, "Zombie Dance");
        emoteList.put(874, "Sit up");
        emoteList.put(872, "Push up");
        emoteList.put(870, "Star jump");
        emoteList.put(868, "Jog");
        emoteList.put(8917, "Flex");
        convertToSortedHashMap();
    }

    // Sort emotes alphabetically
    void convertToSortedHashMap() {
        emoteList.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .forEach(k -> {
            SimpleEmote emote = new SimpleEmote(k.getValue(), k.getKey());
            emoteArrayList.add(emote);
        });
    }

    public ArrayList<SimpleEmote> getSortedEmoteArrayList() {
        return emoteArrayList;
    }
}
