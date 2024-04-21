package com.npcanimator.views;

import com.npcanimator.NPCAnimatorPlugin;
import com.npcanimator.utils.AnimatedNPC;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

import static com.npcanimator.utils.Icons.COLLAPSED_ICON;
import static com.npcanimator.utils.Icons.EXPANDED_ICON;

public class NPCAnimationRow extends JPanel {

    private final NPCAnimatorPlugin plugin;
    private final AnimatedNPC animatedNPC;

    /*
        headerContainer
        detailsContainer
        greetingsContainer
     */

    private final JPanel headerContainer = new JPanel();
        private final JButton collapseButton = new JButton();
        private final String npcName;
        private final JLabel npcNameLabel = new JLabel();

    private final JPanel detailsContainer = new JPanel();
        private final SpinnerNumberModel testingSpinnerNumberModel = new SpinnerNumberModel(1, 0, 10000, 1);
        private final JSpinner animationJSpinner = new JSpinner(testingSpinnerNumberModel);
        private final JLabel animationLabel = new JLabel();
        private final JButton playAnimationButton = new JButton();
        private final JButton stopAnimationButton = new JButton();

    private final JPanel greetingsContainer = new JPanel();
        private final JLabel greetingLabel = new JLabel();
        private final JCheckBox greetingCheckbox = new JCheckBox();
        private final SpinnerNumberModel greetingSpinnerNumberModel = new SpinnerNumberModel(1, 0, 10000, 1);
        private final JSpinner greetingJSpinner = new JSpinner(greetingSpinnerNumberModel);
        private final JButton playGreetingAnimationButton = new JButton();
        private final JButton stopGreetingAnimationButton = new JButton();

    private final JPanel footerContainer = new JPanel();
        private final JButton saveAnimationButton = new JButton();

    private static final String DELETE_NPC_WARNING_TEXT = "Delete %s from your saved NPCs?";
    private static final String SAVE_NPC_WARNING_TEXT = "Save %s's animation?";

    private static final Color expandedBGColor = new Color(48, 48, 48);

    private int idCount = 1;
    private boolean npcIsVisible;

    public NPCAnimationRow(final NPCAnimatorPlugin plugin, AnimatedNPC animatedNPC, boolean isSaved) {
        this.plugin = plugin;
        this.animatedNPC = animatedNPC;

        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARKER_GRAY_COLOR.darker()),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        buildHeaderContainer();
        buildDetailsContainer();
        buildGreetingsContainer();

        this.npcName = animatedNPC.getName();
        this.npcNameLabel.setText(npcName);
        this.animationJSpinner.setValue(0);
        this.greetingCheckbox.setSelected(animatedNPC.isGreetingEnabled());
        this.greetingJSpinner.setValue(animatedNPC.getGreetingID());

        add(this.headerContainer, BorderLayout.NORTH);
        add(this.detailsContainer, BorderLayout.CENTER);
        add(this.greetingsContainer, BorderLayout.SOUTH);
        collapse();
        if (isSaved) {
            addDeleteMenu();
        }
    }

    public void increaseIDCount () {
        this.idCount += 1;
        this.npcNameLabel.setText(npcName + "(" + this.idCount + ")");
    }
    public void decreaseIDCount () {
        this.idCount -= 1;
        if (this.idCount == 1) {
            this.npcNameLabel.setText(npcName);
        }
        else {
            this.npcNameLabel.setText(npcName + "(" + this.idCount + ")");
        }
    }

    void addDeleteMenu() {
        final JMenuItem delete = new JMenuItem("Delete NPC");
        delete.addActionListener(e -> {
            final int result = JOptionPane.showOptionDialog(
                this, String.format(DELETE_NPC_WARNING_TEXT, this.animatedNPC.getName()),
                "Delete NPC", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                null, new String[]{"Yes", "No"},
                "No"
            );

            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            deleteNPCAnimation();
        });

        // Create popup menu with a delete NPC button
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        popupMenu.add(delete);
        this.setComponentPopupMenu(popupMenu);
    }

    boolean isCollapsed() {
        return collapseButton.isSelected();
    }

    void collapse() {
        if (!isCollapsed()) {
            collapseButton.setSelected(true);
            detailsContainer.setVisible(false);
            greetingsContainer.setVisible(false);
            this.setBackground(ColorScheme.DARK_GRAY_COLOR);
            this.headerContainer.setBackground(ColorScheme.DARK_GRAY_COLOR);
        }
    }

    void expand() {
        if (isCollapsed()) {
            collapseButton.setSelected(false);
            detailsContainer.setVisible(true);
            greetingsContainer.setVisible(true);
            this.setBackground(expandedBGColor);
            this.headerContainer.setBackground(expandedBGColor);
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
        this.headerContainer.setBorder(new EmptyBorder(0, 0, 0, 0));

        buildHeaderComponents();

        this.headerContainer.add(this.npcNameLabel);
        this.headerContainer.add(Box.createHorizontalGlue());
        this.headerContainer.add(this.collapseButton);
    }

    void buildHeaderComponents() {
        SwingUtil.removeButtonDecorations(this.collapseButton);
        this.collapseButton.setIcon(EXPANDED_ICON);
        this.collapseButton.setSelectedIcon(COLLAPSED_ICON);
        SwingUtil.addModalTooltip(this.collapseButton, "Expand", "Collapse");
        this.collapseButton.setBorder(new EmptyBorder(0, 20, 0, 0));
        this.collapseButton.setContentAreaFilled(false);
        this.collapseButton.setUI(new BasicButtonUI());
        this.collapseButton.addActionListener(evt -> toggleCollapse());

        this.npcNameLabel.setText("NPC Name");
        this.npcNameLabel.setFont(FontManager.getRunescapeSmallFont());
        this.npcNameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.npcNameLabel.setMinimumSize(new Dimension(1, this.npcNameLabel.getPreferredSize().height));
    }

    public void setSavedNPCVisible(boolean isVisible) {
        if (isVisible != npcIsVisible) {
            npcIsVisible = isVisible;
            if (isVisible) {
                this.npcNameLabel.setForeground(ColorScheme.GRAND_EXCHANGE_LIMIT);
            }
            else {
                this.npcNameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            }
        }
    }

    void buildDetailsContainer() {
        GridLayout layout = new GridLayout();
        layout.setRows(2);
        layout.setColumns(1);
        layout.setVgap(0);
        this.detailsContainer.setLayout(layout);
        this.detailsContainer.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.detailsContainer.setBackground(expandedBGColor);

        JPanel animationTestingBoxLayoutPanel = new JPanel();
        animationTestingBoxLayoutPanel.setLayout(new BoxLayout(animationTestingBoxLayoutPanel, BoxLayout.X_AXIS));
        animationTestingBoxLayoutPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
        animationTestingBoxLayoutPanel.setBackground(expandedBGColor);

        buildDetailsComponents();

        this.detailsContainer.add(this.animationLabel);
        animationTestingBoxLayoutPanel.add(this.animationJSpinner);
        animationTestingBoxLayoutPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        animationTestingBoxLayoutPanel.add(this.playAnimationButton);
        animationTestingBoxLayoutPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        animationTestingBoxLayoutPanel.add(this.stopAnimationButton);
        this.detailsContainer.add(animationTestingBoxLayoutPanel);
    }

    void buildDetailsComponents() {
        this.animationLabel.setText("Animation Testing");
        this.animationLabel.setFont(FontManager.getRunescapeSmallFont());
        this.animationLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.animationLabel.setMinimumSize(new Dimension(1, this.animationLabel.getPreferredSize().height));

        SwingUtil.removeButtonDecorations(this.playAnimationButton);
        this.playAnimationButton.setText("Play");
        this.playAnimationButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        this.playAnimationButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.playAnimationButton.setUI(new BasicButtonUI());
        this.playAnimationButton.addActionListener(evt -> runSelectedAnimation());

        SwingUtil.removeButtonDecorations(this.stopAnimationButton);
        this.stopAnimationButton.setText("Stop");
        this.stopAnimationButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        this.stopAnimationButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.stopAnimationButton.setUI(new BasicButtonUI());
        this.stopAnimationButton.addActionListener(evt -> stopSelectedAnimation());

        this.animationJSpinner.setFont(FontManager.getRunescapeSmallFont());
    }

    void runSelectedAnimation() {
        try {
            int animationID = Integer.parseInt(animationJSpinner.getValue().toString());
            this.animatedNPC.setAnimation(animationID);
        }
        catch (Exception e) {
            // Failed to run selected animation
        }
    }

    void runGreetingAnimation() {
        try {
            int animationID = Integer.parseInt(greetingJSpinner.getValue().toString());
            this.animatedNPC.setAnimation(animationID);
        }
        catch (Exception e) {
            // Failed to run greeting animation
        }
    }

    void stopSelectedAnimation() {
        try {
            this.animatedNPC.stopAnimation();
        }
        catch (Exception e) {
            // Failed to stop the NPC animation
        }
    }

    void buildGreetingsContainer() {
        buildGreetingsComponents();
        buildFooterContainer();

        GridLayout layout = new GridLayout();
        layout.setRows(4);
        layout.setColumns(1);
        layout.setVgap(5);
        this.greetingsContainer.setLayout(layout);
        this.greetingsContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 0, 5, 0),
                BorderFactory.createMatteBorder(1, 0, 0, 0, ColorScheme.MEDIUM_GRAY_COLOR)
        ));
        this.greetingsContainer.setBackground(expandedBGColor);

        JPanel animationTestingBoxLayoutPanel = new JPanel();
        animationTestingBoxLayoutPanel.setLayout(new BoxLayout(animationTestingBoxLayoutPanel, BoxLayout.X_AXIS));
        animationTestingBoxLayoutPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
        animationTestingBoxLayoutPanel.setBackground(expandedBGColor);

        this.greetingsContainer.add(this.greetingLabel);
        this.greetingsContainer.add(this.greetingCheckbox);
        animationTestingBoxLayoutPanel.add(this.greetingJSpinner);
        animationTestingBoxLayoutPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        animationTestingBoxLayoutPanel.add(this.playGreetingAnimationButton);
        animationTestingBoxLayoutPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        animationTestingBoxLayoutPanel.add(this.stopGreetingAnimationButton);
        this.greetingsContainer.add(animationTestingBoxLayoutPanel);
        this.greetingsContainer.add(this.saveAnimationButton);
    }

    void updateGreetingsComponents(AnimatedNPC animatedNPC) {
        this.greetingCheckbox.setSelected(animatedNPC.isGreetingEnabled());
        this.greetingJSpinner.setValue(animatedNPC.getGreetingID());
    }

    void buildGreetingsComponents() {
        this.greetingLabel.setText("Greeting Animation");
        this.greetingLabel.setFont(FontManager.getRunescapeSmallFont());
        this.greetingLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.greetingLabel.setMinimumSize(new Dimension(1, this.greetingLabel.getPreferredSize().height));
        this.greetingLabel.setBorder(new EmptyBorder(5,0,0,0));

        this.greetingCheckbox.setText("Enabled");
        this.greetingCheckbox.setFont(FontManager.getRunescapeSmallFont());
        this.greetingCheckbox.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.greetingCheckbox.setMinimumSize(new Dimension(1, this.greetingCheckbox.getPreferredSize().height));
        this.greetingCheckbox.setMargin(new Insets(0, 0, 0, 0));

        this.greetingJSpinner.setFont(FontManager.getRunescapeSmallFont());

        SwingUtil.removeButtonDecorations(this.playGreetingAnimationButton);
        this.playGreetingAnimationButton.setText("Play");
        this.playGreetingAnimationButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        this.playGreetingAnimationButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.playGreetingAnimationButton.setUI(new BasicButtonUI());
        this.playGreetingAnimationButton.addActionListener(evt -> runGreetingAnimation());

        SwingUtil.removeButtonDecorations(this.stopGreetingAnimationButton);
        this.stopGreetingAnimationButton.setText("Stop");
        this.stopGreetingAnimationButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        this.stopGreetingAnimationButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.stopGreetingAnimationButton.setUI(new BasicButtonUI());
        this.stopGreetingAnimationButton.addActionListener(evt -> stopSelectedAnimation());
    }

    void buildFooterContainer() {
        BoxLayout layout = new BoxLayout(this.footerContainer, BoxLayout.X_AXIS);
        this.footerContainer.setLayout(layout);
        this.footerContainer.setBorder(new EmptyBorder(15, 0, 0, 0));

        buildFooterComponents();

        this.footerContainer.add(Box.createHorizontalGlue());
        this.footerContainer.add(this.saveAnimationButton);
    }

    void buildFooterComponents() {
        SwingUtil.removeButtonDecorations(this.saveAnimationButton);
        this.saveAnimationButton.setText("Save");
        this.saveAnimationButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        this.saveAnimationButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.saveAnimationButton.setUI(new BasicButtonUI());
        this.saveAnimationButton.addActionListener(evt -> saveNPCAnimation());
    }

    void saveNPCAnimation() {
        boolean greetingEnabled = this.greetingCheckbox.isSelected();
        int greetingAnimationID;

        try {
            greetingAnimationID = Integer.parseInt(greetingJSpinner.getValue().toString());
        }
        catch (Exception e) {
            greetingAnimationID = -1;
        }

        final int result = JOptionPane.showOptionDialog(
            this, String.format(SAVE_NPC_WARNING_TEXT, this.animatedNPC.getName()),
            "Save Animation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
            null, new String[]{"Yes", "No"},
            "No"
        );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            this.animatedNPC.setGreetingEnabled(greetingEnabled);
            this.animatedNPC.setGreeting(greetingAnimationID);
            this.plugin.addNPCToSaved(this.animatedNPC);

            JOptionPane.showMessageDialog(
                this,
                "NPC Animation Saved",
                "Saved",
                JOptionPane.WARNING_MESSAGE
            );

            collapse();
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Failed to save NPC Animation",
                "Save Failed",
                JOptionPane.ERROR_MESSAGE
            );
        }

    }

    void deleteNPCAnimation() {
        this.plugin.deleteNPCFromSaved(this.animatedNPC, this);
    }
}
