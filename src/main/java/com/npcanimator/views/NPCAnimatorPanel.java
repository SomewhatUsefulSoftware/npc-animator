package com.npcanimator.views;

import com.npcanimator.NPCAnimatorPlugin;
import com.npcanimator.utils.AnimatedNPC;
import com.npcanimator.utils.SimpleEmote;
import com.npcanimator.utils.StandardEmotes;
import net.runelite.api.Player;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import static com.npcanimator.utils.Icons.COLLAPSED_ICON;
import static com.npcanimator.utils.Icons.EXPANDED_ICON;

public class NPCAnimatorPanel extends PluginPanel
{
    private final NPCAnimatorPlugin plugin;

    private final JPanel savedNPCAnimationsContainer = new JPanel();
    private final JPanel spawnedNPCAnimationsContainer = new JPanel();

    private final HashMap<Integer, NPCAnimationRow> savedAnimatedNPCRowMap = new HashMap<>();
    private final HashMap<Integer, NPCAnimationRow> spawnedAnimatedNPCRowMap = new HashMap<>();

    private final JLabel pluginNameLabel = new JLabel();
    private final JLabel pluginIcon = new JLabel();

    private Player myPlayer;

    // UI Elements
    private final JPanel tabHeaderPanel = new JPanel();
    private final JButton savedTabButton = new JButton();
    private final JButton spawnedTabButton = new JButton();
    private final JButton animationsTabButton = new JButton();
    private final JPanel playerAnimationPanel = new JPanel();
    private final JPanel playerAnimationDetailsPanel = new JPanel();
    private final SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(1, 0, 100000, 1);
    private final JSpinner playerAnimationJSpinner = new JSpinner(spinnerNumberModel);

    private String selectedTab = "Animations";
    private final JPanel savedEmotesListContainer = new JPanel();
    private final JPanel defaultEmotesListContainer = new JPanel();

    private final JPanel savedAnimationsTabHeaderPanel = new JPanel();
    private final JButton defaultAnimationTabButton = new JButton();
    private final JButton savedAnimationTabButton = new JButton();
    private String selectedEmoteAnimationTab = "Saved";

    private final SpinnerNumberModel savedSpinnerNumberModel = new SpinnerNumberModel(1, 0, 100000, 1);
    private final SpinnerNumberModel spawnedSpinnerNumberModel = new SpinnerNumberModel(1, 0, 100000, 1);
    private final JSpinner savedTestingAnimationJSpinner = new JSpinner(savedSpinnerNumberModel);
    private final JSpinner spawnedTestingAnimationJSpinner = new JSpinner(spawnedSpinnerNumberModel);

    private boolean savedNPCTestingCollapsed = false;
    private boolean spawnedNPCTestingCollapsed = false;
    private boolean playerAnimationTestingCollapsed = false;
    private JPanel spawnedTestingPanel;
    private JPanel savedTestingPanel;


    void runSelectedAnimation() {
        try {
            int animationID = Integer.parseInt(playerAnimationJSpinner.getValue().toString());
            this.myPlayer.setAnimation(animationID);
            this.myPlayer.setAnimationFrame(0);
        }
        catch (Exception e) {
            // Failed to run animation
        }
    }

    void stopSelectedAnimation() {
        this.myPlayer.setAnimation(-1);
        this.myPlayer.setAnimationFrame(0);
    }

    void playAnimationOnAllSaved() {
        int animationID;

        try {
            animationID = Integer.parseInt(savedTestingAnimationJSpinner.getValue().toString());
            this.plugin.runAnimationForAllSavedNPCs(animationID);
        }
        catch (Exception e) {
            return;
        }
    }
    void stopAnimationOnAllSaved() {
        try {
            this.plugin.runAnimationForAllSavedNPCs(-1);
        }
        catch (Exception e) {
            return;
        }
    }

    void playAnimationOnAllSpawned() {
        int animationID;

        try {
            animationID = Integer.parseInt(spawnedTestingAnimationJSpinner.getValue().toString());
            this.plugin.runAnimationForAllSpawnedNPCs(animationID);
        }
        catch (Exception e) {
            return;
        }
    }
    void stopAnimationOnAllSpawned() {
        try {
            this.plugin.runAnimationForAllSpawnedNPCs(-1);
        }
        catch (Exception e) {
            return;
        }
    }

    void playAllGreetings() {
        this.plugin.runGreetingsForAllSavedNPCs();
    }

    void saveSelectedAnimation() {
        int animationID;

        try {
            animationID = Integer.parseInt(playerAnimationJSpinner.getValue().toString());
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(
                this.playerAnimationPanel,
                "Animation ID must be a number",
                "Invalid Animation ID",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        final String result = JOptionPane.showInputDialog(this.playerAnimationPanel, "Name this animation");
        if (Objects.equals(result, "")) {
            JOptionPane.showMessageDialog(
                this.playerAnimationPanel,
                "Name cannot be empty!",
                "Invalid Animation Name",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (Objects.equals(result, null)) {
            return;
        }

        SimpleEmote emote = new SimpleEmote(result, animationID);

        if (!this.plugin.addEmoteToSaved(emote)) {
            JOptionPane.showMessageDialog(
                this.playerAnimationPanel,
                "Animation ID has already been saved.",
                "Invalid Animation ID",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    void changeTab(String tabName) {
        this.selectedTab = tabName;
        this.pluginNameLabel.setText("NPC Animator - " + tabName);

        if (Objects.equals(tabName, "Animations")) {
            this.playerAnimationPanel.setVisible(true);
            this.savedAnimationsTabHeaderPanel.setVisible(true);

            this.savedNPCAnimationsContainer.setVisible(false);
            this.spawnedNPCAnimationsContainer.setVisible(false);

            this.animationsTabButton.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR.darker());
            this.savedTabButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
            this.spawnedTabButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());

            if (Objects.equals(this.selectedEmoteAnimationTab, "Saved")) {
                this.savedAnimationTabButton.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR.darker());
                this.defaultAnimationTabButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
                this.savedEmotesListContainer.setVisible(true);
                this.defaultEmotesListContainer.setVisible(false);
            }
            else if (Objects.equals(this.selectedEmoteAnimationTab, "Default")) {
                this.defaultAnimationTabButton.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR.darker());
                this.savedAnimationTabButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
                this.savedEmotesListContainer.setVisible(false);
                this.defaultEmotesListContainer.setVisible(true);
            }
        }
        else if (Objects.equals(tabName, "Saved")) {
            this.playerAnimationPanel.setVisible(false);
            this.savedAnimationsTabHeaderPanel.setVisible(false);
            this.savedEmotesListContainer.setVisible(false);
            this.defaultEmotesListContainer.setVisible(false);
            this.savedNPCAnimationsContainer.setVisible(true);
            this.spawnedNPCAnimationsContainer.setVisible(false);

            this.savedTabButton.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR.darker());
            this.animationsTabButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
            this.spawnedTabButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        }
        else if (Objects.equals(tabName, "Spawned")) {
            this.playerAnimationPanel.setVisible(false);
            this.savedAnimationsTabHeaderPanel.setVisible(false);
            this.savedEmotesListContainer.setVisible(false);
            this.defaultEmotesListContainer.setVisible(false);
            this.savedNPCAnimationsContainer.setVisible(false);
            this.spawnedNPCAnimationsContainer.setVisible(true);

            this.spawnedTabButton.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR.darker());
            this.animationsTabButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
            this.savedTabButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        }
    }

    void changePlayerAnimationsTab(String tabName) {
        this.selectedEmoteAnimationTab = tabName;

        if (Objects.equals(tabName, "Default")) {
            this.defaultEmotesListContainer.setVisible(true);
            this.savedEmotesListContainer.setVisible(false);

            this.savedAnimationTabButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
            this.defaultAnimationTabButton.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR.darker());
        }
        else if (Objects.equals(tabName, "Saved")) {
            this.defaultEmotesListContainer.setVisible(false);
            this.savedEmotesListContainer.setVisible(true);

            this.savedAnimationTabButton.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR.darker());
            this.defaultAnimationTabButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        }
    }

    public void setPlayerForAnimatorPanel(Player myPlayer) {
        this.myPlayer = myPlayer;
    }


    public NPCAnimatorPanel(final NPCAnimatorPlugin plugin) {
        this.plugin = plugin;

        setBorder(new EmptyBorder(6, 6, 6, 6));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new BorderLayout());

        final JPanel headerLayoutPanel = new JPanel();
        headerLayoutPanel.setLayout(new BoxLayout(headerLayoutPanel, BoxLayout.Y_AXIS));
        headerLayoutPanel.setBorder(new EmptyBorder(5, 5, 10, 5));
        buildTabHeaderPanel();
        headerLayoutPanel.add(this.tabHeaderPanel);
        this.getScrollPane().setColumnHeaderView(headerLayoutPanel);

        final JPanel layoutPanel = new JPanel();
        layoutPanel.setLayout(new BoxLayout(layoutPanel, BoxLayout.Y_AXIS));
        add(layoutPanel, BorderLayout.NORTH);

        buildPlayerAnimationPanel();
        buildSavedAnimationTabHeaderPanel();
        buildSavedNPCAnimationsPanel();
        buildSpawnedNPCAnimationsPanel();

        layoutPanel.add(this.playerAnimationPanel);
        layoutPanel.add(this.savedAnimationsTabHeaderPanel);
        layoutPanel.add(this.defaultEmotesListContainer);
        layoutPanel.add(this.savedEmotesListContainer);
        layoutPanel.add(savedNPCAnimationsContainer);
        layoutPanel.add(spawnedNPCAnimationsContainer);
    }

    JPanel buildAnimationTestingPanel(String extraHelpLabel, boolean isSaved) {
        JPanel animationTestingPanel = new JPanel();
        animationTestingPanel.setLayout(new BorderLayout(0, 0));
        animationTestingPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 5, 0, ColorScheme.DARKER_GRAY_COLOR),
            BorderFactory.createEmptyBorder(0, 10, 10, 10)
        ));

        JPanel testingHeaderPanel = new JPanel();
        BoxLayout testingLayout = new BoxLayout(testingHeaderPanel, BoxLayout.X_AXIS);
        testingHeaderPanel.setLayout(testingLayout);
        testingHeaderPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel testingDetailsPanel = new JPanel();
        GridLayout testingDetailsPanelLayout = new GridLayout();
        if (isSaved) {
            testingDetailsPanelLayout.setRows(4);
        }
        else {
            testingDetailsPanelLayout.setRows(3);
        }
        testingDetailsPanelLayout.setColumns(1);
        testingDetailsPanelLayout.setVgap(5);
        testingDetailsPanel.setLayout(testingDetailsPanelLayout);

        JLabel testingHelpLabel = new JLabel();
        testingHelpLabel.setText("NPC Testing");
        testingHelpLabel.setFont(FontManager.getRunescapeSmallFont());
        testingHelpLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        testingHelpLabel.setMinimumSize(new Dimension(1, testingHelpLabel.getPreferredSize().height));

        JButton collapseButton = new JButton();
        SwingUtil.removeButtonDecorations(collapseButton);
        collapseButton.setIcon(EXPANDED_ICON);
        collapseButton.setSelectedIcon(COLLAPSED_ICON);
        SwingUtil.addModalTooltip(collapseButton, "Expand", "Collapse");
        collapseButton.setBorder(new EmptyBorder(0, 20, 0, 0));
        collapseButton.setContentAreaFilled(false);
        collapseButton.setUI(new BasicButtonUI());

        testingHeaderPanel.add(testingHelpLabel);
        testingHeaderPanel.add(Box.createHorizontalGlue());
        testingHeaderPanel.add(collapseButton);


        JLabel testingExtraHelpLabel = new JLabel();
        testingExtraHelpLabel.setText(extraHelpLabel);
        testingExtraHelpLabel.setFont(FontManager.getRunescapeSmallFont());
        testingExtraHelpLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        testingExtraHelpLabel.setMinimumSize(new Dimension(1, testingExtraHelpLabel.getPreferredSize().height));

        JLabel testingAnimationIDLabel = new JLabel();
        testingAnimationIDLabel.setText("Animation ID");
        testingAnimationIDLabel.setFont(FontManager.getRunescapeSmallFont());
        testingAnimationIDLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        testingAnimationIDLabel.setMinimumSize(new Dimension(1, testingAnimationIDLabel.getPreferredSize().height));

        JButton testingPlayAnimationButton = new JButton();
        SwingUtil.removeButtonDecorations(testingPlayAnimationButton);
        testingPlayAnimationButton.setText("Play");
        testingPlayAnimationButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        testingPlayAnimationButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        testingPlayAnimationButton.setUI(new BasicButtonUI());

        JButton testingStopAnimationButton = new JButton();
        SwingUtil.removeButtonDecorations(testingStopAnimationButton);
        testingStopAnimationButton.setText("Stop");
        testingStopAnimationButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        testingStopAnimationButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        testingStopAnimationButton.setUI(new BasicButtonUI());

        if (isSaved) {
            testingPlayAnimationButton.addActionListener(evt -> playAnimationOnAllSaved());
            testingStopAnimationButton.addActionListener(evt -> stopAnimationOnAllSaved());
        }
        else {
            testingPlayAnimationButton.addActionListener(evt -> playAnimationOnAllSpawned());
            testingStopAnimationButton.addActionListener(evt -> stopAnimationOnAllSpawned());
        }


        JButton testingPlayGreetingsAnimationButton = new JButton();
        SwingUtil.removeButtonDecorations(testingPlayGreetingsAnimationButton);
        testingPlayGreetingsAnimationButton.setText("Play Greetings");
        testingPlayGreetingsAnimationButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        testingPlayGreetingsAnimationButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        testingPlayGreetingsAnimationButton.setUI(new BasicButtonUI());
        testingPlayGreetingsAnimationButton.addActionListener(evt -> playAllGreetings());

        JPanel animationTestingBoxLayoutPanel = new JPanel();
        animationTestingBoxLayoutPanel.setLayout(new BoxLayout(animationTestingBoxLayoutPanel, BoxLayout.X_AXIS));
        animationTestingBoxLayoutPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

        savedTestingAnimationJSpinner.setFont(FontManager.getRunescapeSmallFont());
        spawnedTestingAnimationJSpinner.setFont(FontManager.getRunescapeSmallFont());

        GridLayout collapsedTestingPanelLayout = new GridLayout();
        collapsedTestingPanelLayout.setRows(1);
        collapsedTestingPanelLayout.setColumns(1);
        collapsedTestingPanelLayout.setVgap(5);

        if (isSaved) {
            collapseButton.addActionListener(evt -> {
                if (savedNPCTestingCollapsed) {
                    testingDetailsPanel.setVisible(true);
                    savedNPCTestingCollapsed = false;
                    collapseButton.setSelected(false);
                }
                else {
                    testingDetailsPanel.setVisible(false);
                    savedNPCTestingCollapsed = true;
                    collapseButton.setSelected(true);
                }
            });
            animationTestingBoxLayoutPanel.add(savedTestingAnimationJSpinner);
        }
        else {
            collapseButton.addActionListener(evt -> {
                if (spawnedNPCTestingCollapsed) {
                    testingDetailsPanel.setVisible(true);
                    spawnedNPCTestingCollapsed = false;
                    collapseButton.setSelected(false);
                }
                else {
                    testingDetailsPanel.setVisible(false);
                    spawnedNPCTestingCollapsed = true;
                    collapseButton.setSelected(true);
                }
            });
            animationTestingBoxLayoutPanel.add(spawnedTestingAnimationJSpinner);
        }

        animationTestingBoxLayoutPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        animationTestingBoxLayoutPanel.add(testingPlayAnimationButton);
        animationTestingBoxLayoutPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        animationTestingBoxLayoutPanel.add(testingStopAnimationButton);

        testingDetailsPanel.add(testingExtraHelpLabel);
        testingDetailsPanel.add(testingAnimationIDLabel);
        testingDetailsPanel.add(animationTestingBoxLayoutPanel);
        if (isSaved) {
            testingDetailsPanel.add(testingPlayGreetingsAnimationButton);
            savedNPCTestingCollapsed = true;
        }
        else {
            spawnedNPCTestingCollapsed = true;
        }
        testingDetailsPanel.setVisible(false);
        collapseButton.setSelected(true);

        animationTestingPanel.add(testingHeaderPanel, BorderLayout.NORTH);
        animationTestingPanel.add(testingDetailsPanel, BorderLayout.CENTER);

        return animationTestingPanel;
    }

    void buildSavedNPCAnimationsPanel() {
        savedNPCAnimationsContainer.setLayout(new BoxLayout(savedNPCAnimationsContainer, BoxLayout.Y_AXIS));
        savedNPCAnimationsContainer.setBorder(new EmptyBorder(0, 0, 0, 0));
        savedNPCAnimationsContainer.setVisible(false);

        this.savedTestingPanel = buildAnimationTestingPanel("Play animation on all visible saved NPCs", true);
        savedNPCAnimationsContainer.add(this.savedTestingPanel);
    }

    void buildSpawnedNPCAnimationsPanel() {
        spawnedNPCAnimationsContainer.setLayout(new BoxLayout(spawnedNPCAnimationsContainer, BoxLayout.Y_AXIS));
        spawnedNPCAnimationsContainer.setBorder(new EmptyBorder(0, 0, 0, 0));
        spawnedNPCAnimationsContainer.setVisible(false);

        this.spawnedTestingPanel = buildAnimationTestingPanel("Play animation on all visible NPCs", false);
        spawnedNPCAnimationsContainer.add(this.spawnedTestingPanel);
    }

    private void buildPlayerAnimationPanel() {
        this.playerAnimationPanel.setLayout(new BoxLayout(this.playerAnimationPanel, BoxLayout.Y_AXIS));
        this.playerAnimationPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.playerAnimationPanel.setVisible(true);

        buildDetailsContainer();
        this.playerAnimationPanel.add(this.playerAnimationDetailsPanel);
    }

    void buildDetailsContainer() {
        this.playerAnimationDetailsPanel.setLayout(new BorderLayout(0, 0));
        this.playerAnimationDetailsPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        buildDetailsComponents();

        JPanel testingHeaderPanel = new JPanel();
        BoxLayout testingLayout = new BoxLayout(testingHeaderPanel, BoxLayout.X_AXIS);
        testingHeaderPanel.setLayout(testingLayout);
        testingHeaderPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel testingDetailsPanel = new JPanel();
        GridLayout testingDetailsPanelLayout = new GridLayout();
        testingDetailsPanelLayout.setRows(4);
        testingDetailsPanelLayout.setColumns(1);
        testingDetailsPanelLayout.setVgap(5);
        testingDetailsPanel.setLayout(testingDetailsPanelLayout);

        JLabel testingHelpLabel = new JLabel();
        testingHelpLabel.setText("Player Animations");
        testingHelpLabel.setFont(FontManager.getRunescapeSmallFont());
        testingHelpLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        testingHelpLabel.setMinimumSize(new Dimension(1, testingHelpLabel.getPreferredSize().height));

        JButton collapseButton = new JButton();
        SwingUtil.removeButtonDecorations(collapseButton);
        collapseButton.setIcon(EXPANDED_ICON);
        collapseButton.setSelectedIcon(COLLAPSED_ICON);
        SwingUtil.addModalTooltip(collapseButton, "Expand", "Collapse");
        collapseButton.setBorder(new EmptyBorder(0, 20, 0, 0));
        collapseButton.setContentAreaFilled(false);
        collapseButton.setUI(new BasicButtonUI());

        testingHeaderPanel.add(testingHelpLabel);
        testingHeaderPanel.add(Box.createHorizontalGlue());
        testingHeaderPanel.add(collapseButton);

        JLabel testingExtraHelpLabel = new JLabel();
        testingExtraHelpLabel.setText("Test and save animations");
        testingExtraHelpLabel.setFont(FontManager.getRunescapeSmallFont());
        testingExtraHelpLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        testingExtraHelpLabel.setMinimumSize(new Dimension(1, testingExtraHelpLabel.getPreferredSize().height));

        JLabel testingAnimationIDLabel = new JLabel();
        testingAnimationIDLabel.setText("Animation ID");
        testingAnimationIDLabel.setFont(FontManager.getRunescapeSmallFont());
        testingAnimationIDLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        testingAnimationIDLabel.setMinimumSize(new Dimension(1, testingAnimationIDLabel.getPreferredSize().height));

        JButton testingPlayAnimationButton = new JButton();
        SwingUtil.removeButtonDecorations(testingPlayAnimationButton);
        testingPlayAnimationButton.setText("Play");
        testingPlayAnimationButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        testingPlayAnimationButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        testingPlayAnimationButton.setUI(new BasicButtonUI());

        JButton testingStopAnimationButton = new JButton();
        SwingUtil.removeButtonDecorations(testingStopAnimationButton);
        testingStopAnimationButton.setText("Stop");
        testingStopAnimationButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        testingStopAnimationButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        testingStopAnimationButton.setUI(new BasicButtonUI());


        testingPlayAnimationButton.addActionListener(evt -> runSelectedAnimation());
        testingStopAnimationButton.addActionListener(evt -> stopSelectedAnimation());


        JButton testingSaveAnimationButton = new JButton();
        SwingUtil.removeButtonDecorations(testingSaveAnimationButton);
        testingSaveAnimationButton.setText("Save Animation");
        testingSaveAnimationButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        testingSaveAnimationButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        testingSaveAnimationButton.setUI(new BasicButtonUI());
        testingSaveAnimationButton.addActionListener(evt -> saveSelectedAnimation());

        GridLayout collapsedTestingPanelLayout = new GridLayout();
        collapsedTestingPanelLayout.setRows(1);
        collapsedTestingPanelLayout.setColumns(1);
        collapsedTestingPanelLayout.setVgap(5);

        collapseButton.addActionListener(evt -> {
            if (playerAnimationTestingCollapsed) {
                testingDetailsPanel.setVisible(true);
                playerAnimationTestingCollapsed = false;
                collapseButton.setSelected(false);
            }
            else {
                testingDetailsPanel.setVisible(false);
                playerAnimationTestingCollapsed = true;
                collapseButton.setSelected(true);
            }
        });

        JPanel animationTestingBoxLayoutPanel = new JPanel();
        animationTestingBoxLayoutPanel.setLayout(new BoxLayout(animationTestingBoxLayoutPanel, BoxLayout.X_AXIS));
        animationTestingBoxLayoutPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

        this.playerAnimationJSpinner.setFont(FontManager.getRunescapeSmallFont());

        animationTestingBoxLayoutPanel.add(this.playerAnimationJSpinner);
        animationTestingBoxLayoutPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        animationTestingBoxLayoutPanel.add(testingPlayAnimationButton);
        animationTestingBoxLayoutPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        animationTestingBoxLayoutPanel.add(testingStopAnimationButton);

        testingDetailsPanel.add(testingExtraHelpLabel);
        testingDetailsPanel.add(testingAnimationIDLabel);
        testingDetailsPanel.add(animationTestingBoxLayoutPanel);
        testingDetailsPanel.add(testingSaveAnimationButton);
        testingDetailsPanel.setVisible(false);
        playerAnimationTestingCollapsed = true;
        collapseButton.setSelected(true);

        this.playerAnimationDetailsPanel.add(testingHeaderPanel, BorderLayout.NORTH);
        this.playerAnimationDetailsPanel.add(testingDetailsPanel, BorderLayout.CENTER);
    }

    void buildDetailsComponents() {
        this.defaultEmotesListContainer.setLayout(new BoxLayout(this.defaultEmotesListContainer, BoxLayout.Y_AXIS));
        this.savedEmotesListContainer.setLayout(new BoxLayout(this.savedEmotesListContainer, BoxLayout.Y_AXIS));

        StandardEmotes standardEmotes = new StandardEmotes();
        ArrayList<SimpleEmote> sortedEmoteList = standardEmotes.getSortedEmoteArrayList();

        sortedEmoteList.forEach(simpleEmote -> {
            EmoteAnimationRow emoteRow = new EmoteAnimationRow(plugin, simpleEmote, true);
            this.defaultEmotesListContainer.add(emoteRow);
        });

        this.defaultEmotesListContainer.setVisible(false);
    }

    private void buildTabHeaderPanel() {
        this.tabHeaderPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.tabHeaderPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        GridLayout layout = new GridLayout();
        layout.setRows(1);
        layout.setColumns(3);
        this.tabHeaderPanel.setLayout(layout);
        this.tabHeaderPanel.setVisible(true);

        Font rsSmallFont = FontManager.getRunescapeSmallFont();
        Font tabButtonFont = new Font(rsSmallFont.getFontName(), rsSmallFont.getStyle(), 14);

        SwingUtil.removeButtonDecorations(this.animationsTabButton);
        this.animationsTabButton.setText("Animations");
        this.animationsTabButton.setFont(tabButtonFont);
        this.animationsTabButton.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR.darker());
        this.animationsTabButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.animationsTabButton.setPreferredSize(new Dimension(this.animationsTabButton.getPreferredSize().width, 30));
        this.animationsTabButton.setMargin(new Insets(0, 0, 0, 0));
        this.animationsTabButton.setUI(new BasicButtonUI());
        this.animationsTabButton.addActionListener(evt -> changeTab("Animations"));
        this.tabHeaderPanel.add(this.animationsTabButton);

        SwingUtil.removeButtonDecorations(this.savedTabButton);
        this.savedTabButton.setText("Saved");
        this.savedTabButton.setFont(tabButtonFont);
        this.savedTabButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        this.savedTabButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.savedTabButton.setUI(new BasicButtonUI());
        this.savedTabButton.addActionListener(evt -> changeTab("Saved"));
        this.tabHeaderPanel.add(this.savedTabButton);

        SwingUtil.removeButtonDecorations(this.spawnedTabButton);
        this.spawnedTabButton.setText("Spawned");
        this.spawnedTabButton.setFont(tabButtonFont);
        this.spawnedTabButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        this.spawnedTabButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.spawnedTabButton.setUI(new BasicButtonUI());
        this.spawnedTabButton.addActionListener(evt -> changeTab("Spawned"));
        this.tabHeaderPanel.add(this.spawnedTabButton);
    }

    private void buildSavedAnimationTabHeaderPanel() {
        this.savedAnimationsTabHeaderPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(5, 0, 0, 0, ColorScheme.DARKER_GRAY_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        GridLayout layout = new GridLayout();
        layout.setRows(1);
        layout.setColumns(2);
        this.savedAnimationsTabHeaderPanel.setLayout(layout);
        this.savedAnimationsTabHeaderPanel.setVisible(true);

        Font rsSmallFont = FontManager.getRunescapeSmallFont();
        Font tabButtonFont = new Font(rsSmallFont.getFontName(), rsSmallFont.getStyle(), 14);

        SwingUtil.removeButtonDecorations(this.savedAnimationTabButton);
        this.savedAnimationTabButton.setText("Saved");
        this.savedAnimationTabButton.setFont(tabButtonFont);
        this.savedAnimationTabButton.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR.darker());
        this.savedAnimationTabButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.savedAnimationTabButton.setUI(new BasicButtonUI());
        this.savedAnimationTabButton.addActionListener(evt -> changePlayerAnimationsTab("Saved"));
        this.savedAnimationsTabHeaderPanel.add(this.savedAnimationTabButton);

        SwingUtil.removeButtonDecorations(this.defaultAnimationTabButton);
        this.defaultAnimationTabButton.setText("Default");
        this.defaultAnimationTabButton.setFont(tabButtonFont);
        this.defaultAnimationTabButton.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        this.defaultAnimationTabButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        this.defaultAnimationTabButton.setPreferredSize(new Dimension(this.defaultAnimationTabButton.getPreferredSize().width, 30));
        this.defaultAnimationTabButton.setMargin(new Insets(0, 0, 0, 0));
        this.defaultAnimationTabButton.setUI(new BasicButtonUI());
        this.defaultAnimationTabButton.addActionListener(evt -> changePlayerAnimationsTab("Default"));
        this.savedAnimationsTabHeaderPanel.add(this.defaultAnimationTabButton);
    }

    public void loadHeaderIcon(BufferedImage img) {
        ImageIcon imageIcon = new ImageIcon(img.getScaledInstance(24, 32, Image.SCALE_SMOOTH));
        pluginIcon.setIcon(imageIcon);
    }

    public void addFromSaveFile(HashMap<Integer, AnimatedNPC> saveList) {
        saveList.entrySet().stream()
        .sorted(Comparator.comparing(k -> k.getValue().getName()))
        .forEach(k -> {
            NPCAnimationRow npcRow = new NPCAnimationRow(plugin, k.getValue(), true);
            this.savedNPCAnimationsContainer.add(npcRow);
            this.savedAnimatedNPCRowMap.put(k.getValue().getId(), npcRow);
        });

        this.savedNPCAnimationsContainer.revalidate();
    }

    public void addSpawnedNPC(final AnimatedNPC animatedNPC) {
        NPCAnimationRow npcRow = new NPCAnimationRow(plugin, animatedNPC, false);
        this.spawnedNPCAnimationsContainer.add(npcRow);
        this.spawnedNPCAnimationsContainer.revalidate();
        this.spawnedAnimatedNPCRowMap.put(animatedNPC.getId(), npcRow);
    }

    public void updateSpawnedNPC(final AnimatedNPC animatedNPC, boolean isIncrease) {
        NPCAnimationRow spawnedNPCRow = this.spawnedAnimatedNPCRowMap.get(animatedNPC.getId());

        if (isIncrease) {
            spawnedNPCRow.increaseIDCount();
        }
        else {
            spawnedNPCRow.decreaseIDCount();
        }
    }

    public void removeSpawnedNPC (int npcID) {
        NPCAnimationRow selectedRow = this.spawnedAnimatedNPCRowMap.get(npcID);
        this.spawnedNPCAnimationsContainer.remove(selectedRow);
        this.spawnedNPCAnimationsContainer.revalidate();
    }

    public void removeAllSpawnedNPCs() {
        this.spawnedNPCAnimationsContainer.removeAll();
        this.spawnedNPCAnimationsContainer.add(this.spawnedTestingPanel);
        this.spawnedNPCAnimationsContainer.revalidate();
    }

    public void addSavedNPC(final AnimatedNPC animatedNPC) {
        NPCAnimationRow npcRow = new NPCAnimationRow(plugin, animatedNPC, true);
        this.savedNPCAnimationsContainer.add(npcRow);
        this.savedNPCAnimationsContainer.revalidate();
        this.savedAnimatedNPCRowMap.put(animatedNPC.getId(), npcRow);
    }

    public void setSavedNPCVisible(final int npcID, boolean isVisible) {
        NPCAnimationRow savedNPCRow = this.savedAnimatedNPCRowMap.get(npcID);
        savedNPCRow.setSavedNPCVisible(isVisible);
    }

    public void resetAllSavedNPCRows() {
        for (NPCAnimationRow row : this.savedAnimatedNPCRowMap.values()) {
            row.setSavedNPCVisible(false);
        }
        this.savedNPCAnimationsContainer.revalidate();
    }

    public void updateSavedNPC(final AnimatedNPC animatedNPC) {
        NPCAnimationRow savedNPCRow = this.savedAnimatedNPCRowMap.get(animatedNPC.getId());
        savedNPCRow.updateGreetingsComponents(animatedNPC);
        savedNPCRow.revalidate();
        this.savedNPCAnimationsContainer.revalidate();
    }

    public void removeSavedNPC(final NPCAnimationRow row) {
        this.savedNPCAnimationsContainer.remove(row);
        this.savedNPCAnimationsContainer.revalidate();
    }

    public void addEmotesFromSaveFile(HashMap<Integer, SimpleEmote> saveList) {
        saveList.entrySet().stream()
        .sorted(Comparator.comparing(k -> k.getValue().getName()))
        .forEach(k -> {
            EmoteAnimationRow emoteRow = new EmoteAnimationRow(plugin, k.getValue(), false);
            this.savedEmotesListContainer.add(emoteRow);
        });

        this.savedEmotesListContainer.revalidate();
    }

    public void addSavedEmote(final SimpleEmote emote) {
        EmoteAnimationRow emoteRow = new EmoteAnimationRow(plugin, emote, false);
        this.savedEmotesListContainer.add(emoteRow);
        this.savedEmotesListContainer.revalidate();
    }

    public void removeSavedEmote(final EmoteAnimationRow row) {
        this.savedEmotesListContainer.remove(row);
        this.savedEmotesListContainer.revalidate();
    }
}