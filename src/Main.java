
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Main {
    
    // 4x3 format
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 900;
    public static final Color BGCOL = new Color(245, 245, 250);
    
    // DATABASE
    public static final String DB_FILE = "lingi_db.dat";
    public static List<Deck> decks = new ArrayList<>();
    public static Deck selectedDeck = null;  // current deck

    public static JPanel deckListPanel;
    public static JLabel statusLabel;
    
    public static void main(String[] args) {
        loadDatabase();
        SwingUtilities.invokeLater(() -> createWindow());
    }

    public static void createDeck(JFrame parentWindow) {
        
        String deckName = JOptionPane.showInputDialog(
            parentWindow,
            "Please enter deck name:",
            "New Deck",
            JOptionPane.PLAIN_MESSAGE
        );

        if (deckName != null && !deckName.trim().isEmpty()) {
            Deck newDeck = new Deck(deckName.trim());
            decks.add(newDeck);
            saveDatabase();
            refreshDeckList(parentWindow);
            System.out.println("Deck created: " + newDeck.name + " | Total decks: " + decks.size());
        }
    }

    public static void deleteDeck(JFrame parentWindow, Deck deckToDelete) {
        int confirm = JOptionPane.showConfirmDialog(
            parentWindow,
            "Delete deck \"" + deckToDelete.name + "\"?",
            "Delete Deck",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            decks.remove(deckToDelete);
            if (selectedDeck == deckToDelete) {
                selectedDeck = null;
            }
            saveDatabase();
            refreshDeckList(parentWindow);
        }
    }

    public static void refreshDeckList(JFrame window) {
        deckListPanel.removeAll();

        for (Deck deck : decks) {
            // Строка: [Кнопка колоды] [+] [X]
            JPanel rowPanel = new JPanel(new BorderLayout(5, 0));
            rowPanel.setMaximumSize(new Dimension(320, 42));
            rowPanel.setBackground(new Color(235, 235, 242));
            rowPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

            // 1. Кнопка выбора колоды
            JButton selectBtn = new JButton(deck.name + " (" + deck.cards.size() + ")");
            selectBtn.setHorizontalAlignment(SwingConstants.LEFT);
            selectBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
            if (deck == selectedDeck) {
                selectBtn.setBackground(new Color(210, 225, 255));
                selectBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            }
            selectBtn.addActionListener(e -> {
                selectedDeck = deck;
                statusLabel.setText("Selected: " + deck.name);
                refreshDeckList(window);
            });

            // Контейнер под кнопки [+] и [X]
            JPanel rightButtons = new JPanel(new BorderLayout(4, 0));
            rightButtons.setBackground(new Color(235, 235, 242));

            // 2. Плюсик (заглушка редактора)
            JButton addCardBtn = new JButton("+");
            addCardBtn.setToolTipText("Add card");
            addCardBtn.addActionListener(e -> {
                selectedDeck = deck;
                refreshDeckList(window);
                JOptionPane.showMessageDialog(window, "Editor stub for: " + deck.name);
            });

            // 3. Крестик (удаление колоды)
            JButton deleteBtn = new JButton("X");
            deleteBtn.setForeground(Color.RED);
            deleteBtn.setToolTipText("Delete deck");
            deleteBtn.addActionListener(e -> deleteDeck(window, deck));

            rightButtons.add(addCardBtn, BorderLayout.WEST);
            rightButtons.add(deleteBtn, BorderLayout.EAST);

            rowPanel.add(selectBtn, BorderLayout.CENTER);
            rowPanel.add(rightButtons, BorderLayout.EAST);

            deckListPanel.add(rowPanel);
        }

        if (selectedDeck == null) {
            statusLabel.setText(decks.isEmpty() ? "No decks yet. Create one!" : "Select a deck on the right");
        }

        deckListPanel.revalidate();
        deckListPanel.repaint();
    }

    public static void saveDatabase() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DB_FILE))) {
            oos.writeObject(decks);
        } catch (Exception e) {
            System.err.println("Error saving database: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadDatabase() {
        File file = new File(DB_FILE);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            decks = (List<Deck>) ois.readObject();
            System.out.println("Loaded decks: " + decks.size());
        } catch (Exception e) {
            System.err.println("Error loading database: " + e.getMessage());
        }
    }
    
    public static void createWindow() {

        // Creating a window
        JFrame window = new JFrame("Your Dictionary");

        window.setBackground(Color.MAGENTA);
        window.setSize(WIDTH, HEIGHT);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);

        // Creating a top panel
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Decks");
        JMenuItem addDeckItem = new JMenuItem("New Deck");
        addDeckItem.addActionListener(e -> createDeck(window));
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(addDeckItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        window.setJMenuBar(menuBar);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BGCOL);
        mainPanel.setLayout(new BorderLayout());

        // Lingi
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BGCOL);

        JPanel lingiBoxWrapper = new JPanel();
        lingiBoxWrapper.setLayout(new BoxLayout(lingiBoxWrapper, BoxLayout.Y_AXIS));
        lingiBoxWrapper.setBackground(BGCOL);

        JLabel lingiArtBox = new JLabel("Lingi 32x32 (256x256)", SwingConstants.CENTER);
        lingiArtBox.setPreferredSize(new Dimension(256, 256));
        lingiArtBox.setMinimumSize(new Dimension(256, 256));
        lingiArtBox.setMaximumSize(new Dimension(256, 256));
        lingiArtBox.setOpaque(true);
        lingiArtBox.setBackground(new Color(225, 225, 235));
        lingiArtBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        lingiArtBox.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        statusLabel = new JLabel("Lingi is waiting...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        lingiBoxWrapper.add(lingiArtBox);
        lingiBoxWrapper.add(Box.createRigidArea(new Dimension(0, 12)));
        lingiBoxWrapper.add(statusLabel);
        centerPanel.add(lingiBoxWrapper);

        // --- СПРАВА: Панель со списком колод ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(350, HEIGHT));
        rightPanel.setBackground(new Color(235, 235, 242));
        rightPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));

        JLabel rightHeader = new JLabel("Decks", SwingConstants.CENTER);
        rightHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        rightHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        rightPanel.add(rightHeader, BorderLayout.NORTH);

        deckListPanel = new JPanel();
        deckListPanel.setLayout(new BoxLayout(deckListPanel, BoxLayout.Y_AXIS));
        deckListPanel.setBackground(new Color(235, 235, 242));
        deckListPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JScrollPane scrollPane = new JScrollPane(deckListPanel);
        scrollPane.setBorder(null);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // Отрисовываем загруженные из БД колоды при запуске
        refreshDeckList(window);


        window.add(mainPanel);
        window.setVisible(true);
    }
}
