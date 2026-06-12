package net.runelite.client.plugins.microbot.shrekfighter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ShrekFighterPanel extends JDialog {

    private final DefaultComboBoxModel<String> fullModel;
    private final JComboBox<String> npcDropdown;
    private final JButton addButton;
    private final JPanel npcListPanel;
    private boolean updatingModel;

    public ShrekFighterPanel(Window owner) {
        super(owner, "Shrek Fighter - Select NPCs", ModalityType.MODELESS);
        setSize(380, 500);
        setLocationRelativeTo(owner);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(8, 8, 8, 8));
        content.setBackground(ColorScheme.DARK_GRAY_COLOR);
        setContentPane(content);

        // Dropdown + Add button row
        JPanel selectorRow = new JPanel(new GridBagLayout());
        selectorRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
        selectorRow.setBorder(new EmptyBorder(0, 0, 6, 0));

        fullModel = new DefaultComboBoxModel<>();
        npcDropdown = new JComboBox<>(fullModel);
        npcDropdown.setEditable(true);
        npcDropdown.setPreferredSize(new Dimension(0, 28));
        npcDropdown.setFont(FontManager.getRunescapeFont());
        npcDropdown.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        npcDropdown.setForeground(Color.WHITE);
        npcDropdown.getEditor().getEditorComponent().setFont(FontManager.getRunescapeFont());
        npcDropdown.getEditor().getEditorComponent().setBackground(ColorScheme.DARKER_GRAY_COLOR);
        npcDropdown.getEditor().getEditorComponent().setForeground(Color.WHITE);

        addButton = new JButton("Add");
        addButton.setFont(FontManager.getRunescapeBoldFont());
        addButton.setPreferredSize(new Dimension(60, 28));
        addButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(this::onAddClicked);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 6);
        selectorRow.add(npcDropdown, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        selectorRow.add(addButton, gbc);

        content.add(selectorRow, BorderLayout.NORTH);

        // NPC list scroll pane
        npcListPanel = new JPanel();
        npcListPanel.setLayout(new BoxLayout(npcListPanel, BoxLayout.Y_AXIS));
        npcListPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JScrollPane scrollPane = new JScrollPane(npcListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        content.add(scrollPane, BorderLayout.CENTER);

        // Filter wiring
        javax.swing.text.JTextComponent editor =
                (javax.swing.text.JTextComponent) npcDropdown.getEditor().getEditorComponent();
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!updatingModel) filterNpcNames();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!updatingModel) filterNpcNames();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!updatingModel) filterNpcNames();
            }
        });

        refreshNpcList();
        loadNpcNames();
    }

    private void filterNpcNames() {
        String text = ((javax.swing.text.JTextComponent) npcDropdown.getEditor().getEditorComponent()).getText();
        if (text == null) text = "";

        updatingModel = true;
        try {
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) npcDropdown.getModel();
            model.removeAllElements();

            if (text.isEmpty()) {
                for (int i = 0; i < fullModel.getSize(); i++) {
                    model.addElement(fullModel.getElementAt(i));
                }
            } else {
                String lower = text.toLowerCase();
                for (int i = 0; i < fullModel.getSize(); i++) {
                    String name = fullModel.getElementAt(i);
                    if (name != null && name.toLowerCase().contains(lower)) {
                        model.addElement(name);
                    }
                }
            }

            javax.swing.text.JTextComponent editor =
                    (javax.swing.text.JTextComponent) npcDropdown.getEditor().getEditorComponent();
            editor.setText(text);

            if (model.getSize() > 0) {
                npcDropdown.showPopup();
            }
        } finally {
            updatingModel = false;
        }
    }

    private void loadNpcNames() {
        java.util.concurrent.CompletableFuture.supplyAsync(() -> {
            List<String> names = new ArrayList<>();
            try (Reader reader = new InputStreamReader(
                    net.runelite.client.plugins.microbot.util.npc.Rs2NpcStats.class.getResourceAsStream("/npc/monsters_complete.json"),
                    StandardCharsets.UTF_8)) {
                Gson gson = new Gson();
                JsonObject root = gson.fromJson(reader, JsonObject.class);
                LinkedHashSet<String> unique = new LinkedHashSet<>();
                for (Map.Entry<String, com.google.gson.JsonElement> entry : root.entrySet()) {
                    JsonObject npc = entry.getValue().getAsJsonObject();
                    if (npc.has("name")) {
                        String name = npc.get("name").getAsString();
                        if (name != null && !name.isEmpty()) {
                            unique.add(name);
                        }
                    }
                }
                names.addAll(unique);
                names.sort(String.CASE_INSENSITIVE_ORDER);
            } catch (Exception e) {
                log.error("Failed to load NPC names", e);
            }
            return names;
        }).thenAccept(names -> {
            SwingUtilities.invokeLater(() -> {
                updatingModel = true;
                try {
                    fullModel.removeAllElements();
                    DefaultComboBoxModel<String> comboModel =
                            (DefaultComboBoxModel<String>) npcDropdown.getModel();
                    comboModel.removeAllElements();
                    for (String name : names) {
                        fullModel.addElement(name);
                        comboModel.addElement(name);
                    }
                } finally {
                    updatingModel = false;
                }
            });
        });
    }

    private void onAddClicked(ActionEvent e) {
        javax.swing.text.JTextComponent editor =
                (javax.swing.text.JTextComponent) npcDropdown.getEditor().getEditorComponent();
        String text = editor.getText();
        if (text == null) return;
        text = text.trim();
        if (text.isEmpty()) return;

        final String targetName = lookupNpcName(text);
        if (targetName == null) return;

        if (getCurrentNpcList().stream().anyMatch(n -> n.equalsIgnoreCase(targetName))) {
            editor.setText("");
            return;
        }

        ShrekFighterPlugin.addNpcToList(targetName);
        editor.setText("");
        refreshNpcList();
    }

    private List<String> getCurrentNpcList() {
        String raw = ShrekFighterPlugin.getNpcAttackList();
        if (raw == null || raw.isEmpty()) return new ArrayList<>();
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private void refreshNpcList() {
        npcListPanel.removeAll();
        List<String> npcs = getCurrentNpcList();
        LinkedHashSet<String> unique = new LinkedHashSet<>();
        for (String npc : npcs) {
            if (!npc.isEmpty()) {
                unique.add(npc);
            }
        }

        if (unique.isEmpty()) {
            JLabel emptyLabel = new JLabel("No NPCs targeted yet. Use the search above or right-click NPCs in-game.");
            emptyLabel.setFont(FontManager.getRunescapeSmallFont());
            emptyLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            npcListPanel.add(emptyLabel);
        } else {
            JLabel header = new JLabel("Currently targeted NPCs:");
            header.setFont(FontManager.getRunescapeBoldFont());
            header.setForeground(Color.ORANGE);
            header.setBorder(new EmptyBorder(4, 0, 4, 0));
            npcListPanel.add(header);
            for (String npcName : unique) {
                npcListPanel.add(createNpcRow(npcName));
            }
        }

        npcListPanel.revalidate();
        npcListPanel.repaint();
    }

    private JPanel createNpcRow(String npcName) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(ColorScheme.DARK_GRAY_COLOR);
        row.setBorder(new EmptyBorder(2, 2, 2, 2));

        JLabel nameLabel = new JLabel(npcName);
        nameLabel.setFont(FontManager.getRunescapeFont());
        nameLabel.setForeground(Color.WHITE);
        row.add(nameLabel, BorderLayout.CENTER);

        JButton removeButton = new JButton("X");
        removeButton.setFont(FontManager.getRunescapeSmallFont());
        removeButton.setPreferredSize(new Dimension(28, 20));
        removeButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        removeButton.setForeground(new Color(255, 80, 80));
        removeButton.setFocusPainted(false);
        removeButton.setBorder(BorderFactory.createEmptyBorder());
        removeButton.addActionListener(e -> {
            ShrekFighterPlugin.removeNpcFromList(npcName);
            refreshNpcList();
        });
        row.add(removeButton, BorderLayout.EAST);

        return row;
    }

    private String lookupNpcName(String input) {
        for (int i = 0; i < fullModel.getSize(); i++) {
            String name = fullModel.getElementAt(i);
            if (name != null && name.equalsIgnoreCase(input)) {
                return name;
            }
        }
        return null;
    }
}
