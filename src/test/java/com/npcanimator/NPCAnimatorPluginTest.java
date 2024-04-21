package com.npcanimator;
import com.npcanimator.NPCAnimatorPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class NPCAnimatorPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(NPCAnimatorPlugin.class);
        RuneLite.main(args);
    }
}