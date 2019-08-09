import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * The BigTwoTable class implements the CardGameTable interface. It builds a GUI for the Big Two card game
 * and handle all user actions.
 *
 * @author Anmol Gupta
 */
public class BigTwoTable implements CardGameTable {

    private BigTwoClient game; // a card game associates with this table.
    private boolean[] selected; // a boolean array indicating which cards are being selected
    private int activePlayer; // an integer specifying the index of the active player.
    private JFrame frame; // the main window of the application
    private JPanel bigTwoPanel; // a panel for showing the cards of each player and the cards played on the table
    private JButton playButton; // a “Play” button for the active player to play the selected cards.
    private JButton passButton; // a “Pass” button for the active player to pass his/her turn to the next player.
    private JTextArea msgArea; // a text area for showing the current game status as well as end of game messages.
    private JTextArea chatArea; // a text area showing the users chat messages
    private JTextField chatTypeArea; // a text input for users to input chat messages
    private Image[][] cardImages; // a 2D array storing the images for the faces of the cards.
    private Image cardBackImage; // an image for the backs of the cards.
    private Image[] avatars; // an array storing the images for the avatars
    private boolean clickEnabled; // a boolean specifying whether selecting cards is allowed or not.

    /**
     * A constructor for the BigTwoTable class that loads the images into arrays
     * and initializes the GUI by drawing it on the frame.
     *
     * @param game A reference to the client object that is associated with this table.
     */
    public BigTwoTable(BigTwoClient game) {

        this.game = game;
        this.selected = new boolean[13];
        loadImages();
        initializeGUI();
        this.disable();
    }

    /**
     * This method sets the index of the currently active player.
     *
     * @param activePlayer an integer value specifying the index of the active player.
     */
    @Override
    public void setActivePlayer(int activePlayer) {
        this.activePlayer = activePlayer;
        this.enableOrDisableTableBasedOnActivePlayer();
    }

    /**
     * This method returns an array of indices of the cards selected by the user.
     *
     * @return an array of indices of the cards selected.
     */
    @Override
    public int[] getSelected() {
        ArrayList<Integer> tempArrayList = new ArrayList<>();
        for (int i = 0; i < this.selected.length; i++) {
            if (this.selected[i]) {
                tempArrayList.add(i);
            }
        }
        return tempArrayList.stream().mapToInt(i -> i).toArray();
    }

    /**
     * This method resets the list of selected cards by clearing the concerned array.
     */
    @Override
    public void resetSelected() {
        this.selected = new boolean[13];
    }

    /**
     * This method repaints the GUI by redrawing all the graphics elements of the main frame.
     */
    @Override
    public void repaint() {
        frame.repaint();
    }

    /**
     * This method prints the specified string to the message area of the GUI.
     *
     * @param msg the string to be printed to the message area of the card game
     */
    @Override
    public void printMsg(String msg) {
        this.msgArea.append(msg);
    }

    /**
     * This method prints the specified chat message to the chat area of the GUI.
     *
     * @param msg the string to be printed to the chat area of the card game
     */
    public void printChatMessage(String msg) {
        this.chatArea.append(msg + "\n");
    }

    /**
     * This method clears the message area of the GUI.
     */
    @Override
    public void clearMsgArea() {
        this.msgArea.setText("");
    }

    /**
     * This method resets the GUI.
     * It resets the selected cards array, clears the message area and enables user interaction with the GUI.
     */
    @Override
    public void reset() {
        this.resetSelected();
        this.clearMsgArea();
    }

    /**
     * This method enables user interactions with the GUI.
     * It makes the play and pass button clickable and the cards selectable.
     */
    @Override
    public void enable() {
        toggleEnableDisable(true);
    }

    /**
     * This method disables user interactions with the GUI.
     * It makes the play and pass button non-clickable and the cards unselectable.
     */
    @Override
    public void disable() {
        toggleEnableDisable(false);
    }

    private class PlayerPanel extends JPanel implements MouseListener {

        private int playerIndex; // the index of the player to whom this instance belongs.

        /**
         * A constructor of the PlayerPanel class that sets the background color to green
         * and attaches a mouseListener to its instance.
         *
         * @param playerIndex The index of the player to whom this instance belongs.
         */
        public PlayerPanel(int playerIndex) {
            setBackground(new Color(0, 50, 0));
            this.playerIndex = playerIndex;
            this.addMouseListener(this);
        }

        /**
         * This method writes the player number, draws the avatar and the cards held by the player
         * to whom this instance belongs.
         *
         * @param graphics The graphics object of the PlayerPanel instance.
         */
        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);


            graphics.setColor(this.playerIndex == game.getCurrentIdx() ? Color.YELLOW : Color.WHITE);

            if (this.playerIndex == game.getPlayerID()) {
                graphics.drawString(game.getPlayerList().get(this.playerIndex).getName(), 20, 15);
                graphics.drawString("You", 35, 30);
            } else {
                graphics.drawString(game.getPlayerList().get(this.playerIndex).getName(), 20, 30);
            }

            graphics.drawImage(avatars[this.playerIndex], 10, 35, this);
            for (int j = 0; j < game.getPlayerList().get(this.playerIndex).getNumOfCards(); j++) {
                int xPos = 90 + j * cardImages[0][0].getWidth(this) / 2;
                int yPos = !selected[j] || this.playerIndex != game.getCurrentIdx() ? 10 : 0;
                if (this.playerIndex == game.getPlayerID() || game.endOfGame()) {
                    int cardSuit = game.getPlayerList().get(this.playerIndex).getCardsInHand().getCard(j).getSuit();
                    int cardRank = game.getPlayerList().get(this.playerIndex).getCardsInHand().getCard(j).getRank();
                    graphics.drawImage(cardImages[cardSuit][cardRank], xPos, yPos, this);
                } else {
                    graphics.drawImage(cardBackImage, xPos, yPos, this);
                }
            }
        }

        /**
         * This method handles the logic when the user clicks on a particular PlayerPanel instance.
         *
         * @param event The event generated when the mouseEntered action is trigger by the user.
         */
        @Override
        public void mouseClicked(MouseEvent event) {
            int width = cardImages[0][0].getWidth(this);
            int height = cardImages[0][0].getHeight(this);
            int num = game.getPlayerList().get(activePlayer).getNumOfCards();

            int minX = 90;
            int maxX = 90 + (width / 2) * num + width;
            int minY = 0;
            int maxY = 10 + height;
            if (clickEnabled && event.getX() >= minX && event.getX() <= maxX && event.getY() >= minY && event.getY() <= maxY) {
                int card = (int) Math.ceil((event.getX() - 90) / (width / 2));
                card = card / num > 0 ? num - 1 : card;
                if (selected[card]) {
                    if (event.getY() > (maxY - 10) && event.getX() < (90 + (width / 2) * card + width / 2) && !selected[card - 1]) {
                        if (card != 0) {
                            card = card - 1;
                        }
                        selected[card] = true;
                    } else if (event.getY() < (maxY - 10)) {
                        selected[card] = false;
                    }
                } else if (event.getY() > (minY + 10)) {
                    selected[card] = true;
                } else if (selected[card - 1] && event.getX() < (90 + (width / 2) * card + width / 2)) {
                    selected[card - 1] = false;
                }
                this.repaint();
            }
        }

        /**
         * A dummy override of the mouseEntered function of the MouseListener class.
         *
         * @param e The event generated when the mouseEntered action is trigger by the user.
         */
        @Override
        public void mouseEntered(MouseEvent e) {
        }

        /**
         * A dummy override of the mouseReleased function of the MouseListener class.
         *
         * @param e The event generated when the mouseReleased action is trigger by the user.
         */
        @Override
        public void mouseReleased(MouseEvent e) {
        }

        /**
         * A dummy override of the mouseExited function of the MouseListener class.
         *
         * @param e The event generated when the mouseExited action is trigger by the user.
         */
        @Override
        public void mouseExited(MouseEvent e) {
        }

        /**
         * A dummy override of the mousePressed function of the MouseListener class.
         *
         * @param e The event generated when the mousePressed action is trigger by the user.
         */
        @Override
        public void mousePressed(MouseEvent e) {
        }
    }

    private class BigTwoPanel extends JPanel {

        /**
         * This is the constructor for the BigTwoPanel class that gives the green background to the game panel and creates
         * 4 instances of PlayerPanel class and adds them to itself.
         */
        public BigTwoPanel() {
            setBackground(new Color(0, 50, 0));
            for (int i = 0; i < game.getNumOfPlayers(); i++) {
                PlayerPanel playerPanel = new PlayerPanel(i);
                playerPanel.setPreferredSize(new Dimension(600, 150));
                this.add(playerPanel);
            }
        }

        /**
         * This method draws its 4 child PlayerPanel objects on the table. It also draws the last hand played, if any.
         *
         * @param graphics The graphics object of the BigTwoPanel instance.
         */
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.setColor(Color.WHITE);

            for (int i = 0; i < this.getComponents().length; i++) {
                this.getComponent(i).repaint();
            }
            drawLastHandOnTable(graphics, this);
        }
    }

    private class PlayButtonListener implements ActionListener {

        /**
         * This function implements the logic to play the move when the user clicks the play button from the menu.
         *
         * @param event The event object generated when the user clicks the play button.
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            int[] selectedIndices = getSelected();
            if (selectedIndices.length == 0) {
                printMsg("[] <== Not a legal move!!!\n");
                return;
            }
            game.makeMove(activePlayer, selectedIndices);
        }
    }

    private class PassButtonListener implements ActionListener {

        /**
         * This function implements the logic to pass the move when the user clicks the pass button from the menu.
         *
         * @param e The event object generated when the user clicks the pass button.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            game.makeMove(activePlayer, null);
        }
    }

    private class ConnectMenuItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!game.isConnected()) {
                reset();
                game.makeConnection();
            }
        }
    }

    private class QuitMenuItemListener implements ActionListener {

        /**
         * This function implements the logic to quit the game when the user clicks the quit button from the menu.
         *
         * @param e The event object generated when the user clicks the quit button.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(1);
        }
    }

    private class SendMessageListener implements ActionListener {

        /**
         * This function implements the logic to send the chat message entered by the user to the server.
         *
         * @param e The event object generated when the sends the chat message.
         */
        public void actionPerformed(ActionEvent e) {
            CardGameMessage chatMessage = new CardGameMessage(CardGameMessage.MSG, -1, chatTypeArea.getText());
            chatTypeArea.setText("");
            game.sendMessage(chatMessage);
        }
    }

    private void loadImages() {
        Image[] avatars = new Image[4];
        Image[][] cardImages = new Image[4][13];

        String[] avatarsName = {"batman_72.png", "flash_72.png", "superman_72.png", "wonder_woman_72.png"};
        for (int i = 0; i < avatarsName.length; i++) {
            avatars[i] = new ImageIcon("assets/avatars/" + avatarsName[i]).getImage();
        }

        String[] cardNames = {"a", "2", "3", "4", "5", "6", "7", "8", "9", "10", "j", "q", "k"};
        String[] deckNames = {"Diamonds", "Clubs", "Hearts", "Spades"};
        for (int i = 0; i < deckNames.length; i++) {
            for (int j = 0; j < cardNames.length; j++) {
                cardImages[i][j] = new ImageIcon("assets/cards/" + cardNames[j] + deckNames[i] + ".jpeg").getImage();
            }
        }

        this.cardImages = cardImages;
        this.avatars = avatars;
        this.cardBackImage = new ImageIcon("assets/cards/cardBack.jpeg").getImage();
    }

    private void drawLastHandOnTable(Graphics graphics, BigTwoPanel bigTwoPanel) {
        if (game.getHandsOnTable().size() > 0) {
            Hand lastHand = game.getHandsOnTable().get(game.getHandsOnTable().size() - 1);
            graphics.drawString("Played by " + lastHand.getPlayer().getName(), 50, 650);
            for (int i = 0; i < lastHand.size(); i++) {
                int cardSuit = lastHand.getCard(i).getSuit();
                int cardRank = lastHand.getCard(i).getRank();
                graphics.drawImage(cardImages[cardSuit][cardRank], 50 + i * (cardImages[0][0].getWidth(bigTwoPanel) + 5), 660, bigTwoPanel);
            }
        }
    }

    private void initializeGUI() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 900);
        frame.setResizable(false);

        JPanel bigTwoPanel = new BigTwoPanel();
        bigTwoPanel.setPreferredSize(new Dimension(700, 900));
        frame.add(bigTwoPanel, BorderLayout.WEST);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");
        menuBar.add(menu);
        JMenuItem menuItem1 = new JMenuItem("Connect");
        menuItem1.addActionListener(new ConnectMenuItemListener());
        menu.add(menuItem1);

        JMenuItem menuItem2 = new JMenuItem("Quit");
        menuItem2.addActionListener(new QuitMenuItemListener());
        menu.add(menuItem2);
        frame.add(menuBar, BorderLayout.NORTH);

        JPanel messages = new JPanel();
        messages.setLayout(new BoxLayout(messages, BoxLayout.PAGE_AXIS));

        JTextArea msgArea = new JTextArea(20, 24);
        msgArea.setEnabled(false);
        DefaultCaret caret = (DefaultCaret) msgArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(msgArea);
        messages.add(scrollPane);

        JTextArea chatArea = new JTextArea(21, 24);
        chatArea.setEnabled(false);
        DefaultCaret caretChat = (DefaultCaret) chatArea.getCaret();
        caretChat.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPaneChat = new JScrollPane();
        scrollPaneChat.setViewportView(chatArea);
        messages.add(scrollPaneChat);

        JPanel chat = new JPanel();
        chat.setLayout(new FlowLayout());
        chat.add(new JLabel("Message:"));
        chatTypeArea = new JTextField();
        chatTypeArea.getDocument().putProperty("filterNewlines", Boolean.TRUE);
        chatTypeArea.addActionListener(new SendMessageListener());
        chatTypeArea.setPreferredSize(new Dimension(200, 24));
        chat.add(chatTypeArea);
        messages.add(chat);

        frame.add(messages, BorderLayout.EAST);

        JPanel buttons = new JPanel();
        JButton playButton = new JButton("Play");
        playButton.addActionListener(new PlayButtonListener());
        JButton passButton = new JButton("Pass");
        passButton.addActionListener(new PassButtonListener());
        buttons.add(playButton);
        buttons.add(passButton);
        frame.add(buttons, BorderLayout.SOUTH);

        frame.setVisible(true);

        this.chatArea = chatArea;
        this.msgArea = msgArea;
        this.playButton = playButton;
        this.passButton = passButton;
        this.frame = frame;
        this.bigTwoPanel = bigTwoPanel;
    }

    private void toggleEnableDisable(boolean valueToSet) {
        this.playButton.setEnabled(valueToSet);
        this.passButton.setEnabled(valueToSet);
        this.clickEnabled = valueToSet;
    }

    private void enableOrDisableTableBasedOnActivePlayer() {
        if (game.getPlayerID() == game.getCurrentIdx()) {
            this.enable();
        } else {
            this.disable();
        }
    }
}
