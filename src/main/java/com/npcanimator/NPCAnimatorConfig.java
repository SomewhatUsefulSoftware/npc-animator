package com.npcanimator;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(NPCAnimatorConfig.CONFIG_GROUP)
public interface NPCAnimatorConfig extends Config
{
    String CONFIG_GROUP = "NPCAnimator";

    @ConfigItem(
        keyName = "populateSpawnedNPCs",
        name = "Populate Spawned NPCs List",
        description = "Recommended to disable this option when you are not adding animations to spawned NPCs."
    )
    default boolean populateSpawnedNPCs() {
        return true;
    }

}
