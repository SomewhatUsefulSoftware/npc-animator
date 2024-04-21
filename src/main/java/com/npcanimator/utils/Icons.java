package com.npcanimator.utils;

import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class Icons {
    public static final BufferedImage NAV_BUTTON = ImageUtil.loadImageResource(Icons.class, "/cheer_icon.png");
    final static BufferedImage collapsedImage = ImageUtil.loadImageResource(Icons.class, "/collapsed_icon.png");
    final static BufferedImage expandedImage = ImageUtil.loadImageResource(Icons.class, "/expanded_icon.png");

    public static final ImageIcon COLLAPSED_ICON = new ImageIcon(collapsedImage);
    public static final ImageIcon EXPANDED_ICON = new ImageIcon(expandedImage);
}