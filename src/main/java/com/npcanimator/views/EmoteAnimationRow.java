package com.npcanimator.views;

import com.npcanimator.NPCAnimatorPlugin;
import com.npcanimator.utils.SimpleEmote;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import static com.npcanimator.utils.Icons.COLLAPSED_ICON;
import static com.npcanimator.utils.Icons.EXPANDED_ICON;

public class EmoteAnimationRow extends JPanel {

    private final NPCAnimatorPlugin plugin;
    private final int id;
    private final SimpleEmote emote;

    /*
        headerContainer
        detailsContainer
        greetingsContainer
     */

    private final JPanel headerContainer = new JPanel();
    private final JButton collapseButton = new JButton();
    private final JLabel rowNameLabel = new JLabel();

    private final JPanel detailsContainer = new JPanel();
    private final JButton playAnimationButton = new JButton();

    private final JPanel footerContainer = new JPanel();

    private static final String DELETE_ANIMATION_WARNING_TEXT = "Delete \"%s\" from your saved animations?";
    private static final String SAVE_ANIMATION_WARNING_TEXT = "Add \"%s\" to your saved animations?";


    public EmoteAnimationRow(final NPCAnimatorPlugin plugin, SimpleEmote emote, boolean isStandardEmote) {
        this.plugin = plugin;
        this.emote = emote;
        this.id = emote.getId();

        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARKER_GRAY_COLOR.darker()),
                BorderFactory.createEmptyBorder(5, 0, 5, 10)
        ));
        buildHeaderContainer();

        this.rowNameLabel.setText(emote.getName() + ": " + this.id);

        add(this.headerContainer, BorderLayout.NORTH);
        add(this.detailsContainer, BorderLayout.CENTER);
        collapse();
        if (!isStandardEmote) {
            addDeleteMenu();
        }
        else {
            addQuickSaveMenu();
        }
    }

    void addDeleteMenu() {
        final JMenuItem copy = new JMenuItem("Copy ID");
        copy.addActionListener(e -> {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(Integer.toString(this.id));
            clip.setContents(tText, null);
        });

        final JMenuItem delete = getDeleteJMenuItem();

        // Create popup menu with a copy and delete button
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        popupMenu.add(copy);
        popupMenu.add(delete);
        this.setComponentPopupMenu(popupMenu);
    }

    private JMenuItem getDeleteJMenuItem() {
        final JMenuItem delete = new JMenuItem("Delete Animation");
        delete.addActionListener(e -> {
            final int result = JOptionPane.showOptionDialog(
                this, String.format(DELETE_ANIMATION_WARNING_TEXT, this.emote.getName()),
                "Delete Animation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                null, new String[]{"Yes", "No"},
                "No"
            );

            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            deleteEmoteRow();
        });
        return delete;
    }

    void addQuickSaveMenu() {
        final JMenuItem copy = new JMenuItem("Copy ID");
        copy.addActionListener(e -> {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(Integer.toString(this.id));
            clip.setContents(tText, null);
        });

        final JMenuItem save = getSaveJMenuItem();

        // Create popup menu with a copy and save button
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        popupMenu.add(copy);
        popupMenu.add(save);
        this.setComponentPopupMenu(popupMenu);
    }

    private JMenuItem getSaveJMenuItem() {
        final JMenuItem save = new JMenuItem("Save Animation");
        save.addActionListener(e -> {
            final int result = JOptionPane.showOptionDialog(
                this,
                String.format(SAVE_ANIMATION_WARNING_TEXT, this.emote.getName()),
                "Delete Animation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                null, new String[]{"Yes", "No"},
                "No"
            );

            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            this.plugin.addEmoteToSaved(this.emote);
        });
        return save;
    }

    boolean isCollapsed() {
        return collapseButton.isSelected();
    }

    void collapse() {
        if (!isCollapsed()) {
            collapseButton.setSelected(true);
            detailsContainer.setVisible(false);
            footerContainer.setVisible(false);
        }
    }

    void expand() {
        if (isCollapsed()) {
            collapseButton.setSelected(false);
            detailsContainer.setVisible(true);
            footerContainer.setVisible(true);
        }
    }

    void toggleCollapse() {
        if (isCollapsed()) {
            expand();
        }
        else {
            collapse();
        }
    }

    void buildHeaderContainer() {
        BoxLayout layout = new BoxLayout(this.headerContainer, BoxLayout.X_AXIS);

        this.headerContainer.setLayout(layout);
        this.headerContainer.setBorder(new EmptyBorder(5, 0, 0, 0));

        buildHeaderComponents();

        this.headerContainer.add(Box.createRigidArea(new Dimension(10, 0)));
        this.headerContainer.add(this.rowNameLabel);

        this.headerContainer.add(Box.createHorizontalGlue());
        this.headerContainer.add(this.playAnimationButton);
    }

    void buildHeaderComponents() {
        SwingUtil.removeButtonDecorations(this.collapseButton);
        this.collapseButton.setIcon(EXPANDED_ICON);
        this.collapseButton.setSelectedIcon(COLLAPSED_ICON);
        SwingUtil.addModalTooltip(this.collapseButton, "Expand", "Collapse");
        this.collapseButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        this.collapseButton.setUI(new BasicButtonUI());
        this.collapseButton.addActionListener(evt -> toggleCollapse());

        this.rowNameLabel.setText("Animation");
        this.rowNameLabel.setFont(FontManager.getRunescapeSmallFont());
        this.rowNameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.rowNameLabel.setMinimumSize(new Dimension(1, this.rowNameLabel.getPreferredSize().height));

        SwingUtil.removeButtonDecorations(this.playAnimationButton);
        this.playAnimationButton.setText("Play");
        this.playAnimationButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        this.playAnimationButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.playAnimationButton.setUI(new BasicButtonUI());
        this.playAnimationButton.addActionListener(evt -> runSelectedAnimation());
    }

    void runSelectedAnimation() {
        try {
            this.plugin.runAnimationFromList(this.id);
        }
        catch (Exception e) {
            // Failed to run selected animation
        }
    }

    void deleteEmoteRow() {
        this.plugin.deleteEmoteFromSaved(this.emote, this);
    }
}
