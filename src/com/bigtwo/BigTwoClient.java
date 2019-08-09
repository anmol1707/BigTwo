package com.bigtwo;

import com.bigtwo.Hands.*;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * This class models the logic of the BigTwo game by connecting the client to the server,
 * distributing the cards among players, check whether the game is complete
 * and making the game progress by taking and processing the input from the user.
 *
 * @author Anmol Gupta
 *
 */
public class BigTwoClient implements CardGame, NetworkGame {

    private int numOfPlayers; // an integer specifying the number of players.
    private Deck deck; // a deck of cards.
    private ArrayList<CardGamePlayer> playerList; // a list of players.
    private ArrayList<Hand> handsOnTable; // a list of hands played on the table.
    private int playerID; // an integer specifying the playerID (i.e., index) of the local player.
    private String playerName; // a string specifying the name of the local player.
    private String serverIP; // a string specifying the IP address of the game server.
    private int serverPort; // an integer specifying the TCP port of the game server.
    private Socket sock; // a socket connection to the game server.
    private ObjectOutputStream oos; // an ObjectOutputStream for sending messages to the server.
    private int currentIdx; // an integer specifying the index of the player for the current turn.
    private BigTwoTable table; // a Big Two table which builds the GUI for the game and handles all user actions.

    /**
     *
     * This method is the constructor to the com.bigtwo.BigTwoClient class. It creates and adds 4 players to the
     * playerList associated with this game. It takes the name as an input from the user, sets the server IP
     * address and port and initiates the connection.
     *
     */
    public BigTwoClient() {
        this.numOfPlayers = 4;
        ArrayList<CardGamePlayer> players = new ArrayList<>();
        for (int i = 0; i < getNumOfPlayers(); i++) {
            CardGamePlayer cardGamePlayer = new CardGamePlayer();
            players.add(cardGamePlayer);
        }
        this.handsOnTable = new ArrayList<>();
        this.playerList = players;
        this.table = new BigTwoTable(this);

        String name = null;
        while (name == null || name.length() == 0) {
            name = JOptionPane.showInputDialog("Enter Your Name: ");
        }

        this.setPlayerName(name);
        this.setServerIP("127.0.0.1");
        this.setServerPort(2396);
        this.makeConnection();
    }

    /**
     *
     * This method returns the number of players in the game
     *
     * @return an integer denoting the number of players
     */
    @Override
    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    /**
     * This methods retrieves and returns the value of deck of cards
     *
     * @return The value of the deck of cards
     */
    @Override
    public Deck getDeck() {
        return deck;
    }

    /**
     * This method retrieves the list of players associated with this game.
     *
     * @return The value of the list of players
     */
    @Override
    public ArrayList<CardGamePlayer> getPlayerList() {
        return playerList;
    }

    /**
     * This method retrieves the list of hands played on the table
     *
     * @return The value of the list of hands played
     */
    @Override
    public ArrayList<Hand> getHandsOnTable() {
        return handsOnTable;
    }

    /**
     * This method retrieves the index of the current player in the playerList.
     *
     * @return The value of the index of the current player
     */
    @Override
    public int getCurrentIdx() {
        return currentIdx;
    }

    /**
     * This method is used for starting the game with a given shuffled deck of cards.
     *
     * @param deck The initial value of the deck of cards used to start the game
     */
    @Override
    public void start(Deck deck) {
        clearCards();
        this.deck = deck;
        distributeCards();
        this.currentIdx = getPlayerWithThreeOfDiamonds();
        this.table.setActivePlayer(this.getCurrentIdx());
    }

    /**
     *
     * This method creates a com.bigtwo.CardGameMessage object of type MOVE and passes in the indices of the cards being
     * played in this move. It then sends this message to the server using the sendMessage function.
     *
     * @param playerID the playerID of the player who makes the move
     * @param cardIdx the indices of the card used to make the move
     */
    @Override
    public void makeMove(int playerID, int[] cardIdx) {
        CardGameMessage cardGameMessage = new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx);
        sendMessage(cardGameMessage);
    }

    /**
     *
     * This method receives the playerId and the card indices from the parseMessage function that listens
     * to the messages broadcasted by the server. It implements the logic to play a hand based on the cards
     * played in this move.
     *
     * @param playerID the playerID of the player who makes the move
     * @param cardIdx the indices of the card used to make the move
     */
    @Override
    public void checkMove(int playerID, int[] cardIdx) {
        this.table.disable();
        Hand lastHand = null;

        if (this.getHandsOnTable().size() != 0) {
            lastHand = this.getHandsOnTable().get(this.getHandsOnTable().size() - 1);
        }

        CardGamePlayer currentPlayer = playerList.get(playerID);
        CardList cardsToPlay = currentPlayer.play(cardIdx);

        if (cardsToPlay == null) {
            if (lastHand == null || lastHand.getPlayer().equals(currentPlayer)) {
                this.table.printMsg("{Pass} <== Not a legal move!!!\n");
            } else {
                this.table.printMsg("{Pass}\n");
                this.currentIdx = (this.currentIdx + 1) % this.playerList.size();
                this.table.printMsg(this.getPlayerList().get(this.getCurrentIdx()).getName() + "'s turn:\n");
            }
            continueGame();
            return;
        }
        cardsToPlay.sort();
        BigTwoCard threeOfDiamonds = new BigTwoCard(0, 0);
        if (this.handsOnTable.size() == 0 && !cardsToPlay.getCard(0).equals(threeOfDiamonds)) {
            this.table.printMsg(cardsToPlay.toString() + " <== Not a legal move!!!\n");
            continueGame();
            return;
        }
        Hand newHand = composeHand(currentPlayer, cardsToPlay);

        if (newHand == null) {
            this.table.printMsg(cardsToPlay.toString() + " <== Not a legal move!!!\n");
            continueGame();
            return;
        } else if (lastHand != null && !newHand.beats(lastHand)) {
            this.table.printMsg("{" + newHand.getType() + "} " + newHand.toString() + " <== Not a legal move!!!\n");
            continueGame();
            return;
        }

        lastHand = newHand;
        this.handsOnTable.add(lastHand);
        currentPlayer.removeCards(cardsToPlay);
        this.currentIdx = (this.currentIdx + 1) % this.playerList.size();
        this.table.printMsg("{" + newHand.getType() + "} " + newHand.toString() + "\n");
        if (endOfGame()) {
            this.table.disable();
            this.table.resetSelected();
            this.table.repaint();
            String message = "Game Ends\n";
            for (int j = 0; j < this.playerList.size(); j++) {
                if (this.getPlayerList().get(j).getNumOfCards() != 0) {
                    message = message.concat(this.getPlayerList().get(j).getName() + " has " + this.playerList.get(j).getNumOfCards() + " cards in hand.\n");
                } else {
                    message = message.concat(this.getPlayerList().get(j).getName() + " wins the game.\n");
                }
            }
            JOptionPane.showMessageDialog(null, message);
            CardGameMessage readyMessage = new CardGameMessage(CardGameMessage.READY, -1, null);
            sendMessage(readyMessage);
        } else {
            this.table.printMsg(this.getPlayerList().get(this.getCurrentIdx()).getName() + "'s turn:\n");
            continueGame();
        }
    }

    /**
     * This method checks whether the game has ended or not.
     *
     * @return a boolean value specifying whether the game has ended or not
     */
    @Override
    public boolean endOfGame() {
        for (int i = 0; i < this.playerList.size(); i++) {
            if (this.playerList.get(i).getNumOfCards() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * This method returns the id of the player associated with this client.
     *
     * @return The id of the player.
     */
    @Override
    public int getPlayerID() {
        return playerID;
    }

    /**
     *
     * This method sets the id of the player associated with this client.
     *
     * @param playerID The id of the player.
     */
    @Override
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /**
     *
     * This method returns the name of the player associated with this client.
     *
     * @return The name of the player.
     */
    @Override
    public String getPlayerName() {
        return playerName;
    }

    /**
     *
     * This method sets the name of the player associated with this client.
     *
     * @param playerName The name of the player.
     */
    @Override
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     *
     * This method returns the IP address of the server.
     *
     * @return The IP address of the server.
     */
    @Override
    public String getServerIP() {
        return serverIP;
    }

    /**
     *
     * This method sets the IP address of the server to this client.
     *
     * @param serverIP The IP address of the server.
     */
    @Override
    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    /**
     *
     * This method returns the server port.
     *
     * @return The port number of the server.
     */
    @Override
    public int getServerPort() {
        return serverPort;
    }

    /**
     *
     * This methods set the server port of the server to this client.
     *
     * @param serverPort The port number of the server.
     */
    @Override
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     *
     * This method connects the socket to the server and sends com.bigtwo.CardGameMessage objects of type JOIN and Ready
     * to indicate that the client is wanting to join the game and is ready to play.
     *
     */
    @Override
    public void makeConnection() {
        try {
            this.sock = new Socket(getServerIP(), getServerPort());
            this.oos = new ObjectOutputStream(this.sock.getOutputStream());

            ServerHandler threadJob = new ServerHandler();
            Thread myThread = new Thread(threadJob);
            myThread.start();

            CardGameMessage joinMessage = new CardGameMessage(CardGameMessage.JOIN, -1, getPlayerName());
            sendMessage(joinMessage);
            CardGameMessage readyMessage = new CardGameMessage(CardGameMessage.READY, -1, null);
            sendMessage(readyMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * This method implements the logic that the game has to execute based on the different com.bigtwo.GameMessage object
     * received from the sever.
     *
     * @param message The com.bigtwo.GameMessage object received from the thread listening to the server.
     */
    @Override
    public synchronized void parseMessage(GameMessage message) {

        switch (message.getType()) {
            case CardGameMessage.PLAYER_LIST:
                setPlayerID(message.getPlayerID());
                String[] playerNames = (String[]) message.getData();
                for (int i = 0; i < playerNames.length; i++) {
                    if (playerNames[i] != null) {
                        this.getPlayerList().get(i).setName(playerNames[i]);
                    }
                }
                break;
            case CardGameMessage.JOIN:
                this.getPlayerList().get(message.getPlayerID()).setName((String) message.getData());
                break;
            case CardGameMessage.FULL:
                this.table.printMsg("The table is full and no more players can join!");
                try {
                    this.sock.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CardGameMessage.QUIT:
                this.getPlayerList().get(message.getPlayerID()).setName("");
                if (!endOfGame()) {
                    this.table.disable();
                    CardGameMessage readyGameMessage = new CardGameMessage(CardGameMessage.READY, -1, null);
                    sendMessage(readyGameMessage);
                }
                break;
            case CardGameMessage.READY:
                this.table.printMsg(this.getPlayerList().get(message.getPlayerID()).getName() + " is ready to play.\n");
                break;
            case CardGameMessage.START:
                start((BigTwoDeck) message.getData());
                break;
            case CardGameMessage.MOVE:
                checkMove(message.getPlayerID(), (int[]) message.getData());
                break;
            case CardGameMessage.MSG:
                this.table.printChatMessage((String) message.getData());
                break;
            default:
                System.out.println("Message received of type: " + message.getType() + " , ignored!");
                break;
        }
        this.table.repaint();
    }

    /**
     *
     * This method writes the given com.bigtwo.GameMessage object to the ObjectOutputStream which is connected to
     * the server.
     *
     * @param message The com.bigtwo.GameMessage object to be sent to the server.
     */
    @Override
    public void sendMessage(GameMessage message) {
        try {
            this.oos.writeObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ServerHandler implements Runnable {

        /**
         *
         * This function starts a new thread that is responsible for listening to the messages broadcasted by
         * the server and passing the message to the parseMessage function for further processing.
         *
         */
        @Override
        public void run() {
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(sock.getInputStream());
                while (isConnected()) {
                    CardGameMessage messageFromServer = (CardGameMessage) ois.readObject();
                    if (messageFromServer != null) {
                        parseMessage(messageFromServer);
                    }
                }
                ois.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * This method checks whether the socket is connected to the server or not.
     *
     * @return A boolean value specifying if the socket is connected to the server.
     */
    public boolean isConnected() {
        return (sock != null && !sock.isClosed());
    }

    /**
     * This method starts a Big Two card game by creating a com.bigtwo.BigTwoClient object.
     *
     * @param args Any arguments provided to the main function
     */
    public static void main(String[] args) {
        new BigTwoClient();
    }

    /**
     * This method returns a valid hand from the specified list of cards of the player.
     * Returns null is no valid hand can be composed from the specified cards.
     *
     * @param player The specific player for which we need to return the hand
     * @param cards  The list of cards selected by the player
     * @return A valid hand based on the selected cards
     */
    public static Hand composeHand(CardGamePlayer player, CardList cards) {
        if (cards.size() == 1) {
            return new Single(player, cards);
        } else if (cards.size() == 2 && (new Pair(player, cards)).isValid()) {
            return new Pair(player, cards);
        } else if (cards.size() == 3 && (new Triple(player, cards)).isValid()) {
            return new Triple(player, cards);
        } else if (cards.size() == 5) {
            if ((new StraightFlush(player, cards).isValid())) {
                return new StraightFlush(player, cards);
            } else if ((new Quad(player, cards)).isValid()) {
                return new Quad(player, cards);
            } else if ((new FullHouse(player, cards)).isValid()) {
                return new FullHouse(player, cards);
            } else if ((new Flush(player, cards)).isValid()) {
                return new Flush(player, cards);
            } else if ((new Straight(player, cards)).isValid()) {
                return new Straight(player, cards);
            }
            return null;
        }
        return null;
    }

    private int getPlayerWithThreeOfDiamonds() {
        BigTwoCard threeOfDiamonds = new BigTwoCard(0, 0);
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getCardsInHand().contains(threeOfDiamonds)) {
                return i;
            }
        }
        return -1;
    }

    private void distributeCards() {
        for (int i = 0; i < 13; i++) {
            for (CardGamePlayer player : this.playerList) {
                player.addCard(deck.removeCard(0));
            }
        }
    }

    private void clearCards() {
        for (CardGamePlayer player : this.playerList) {
            player.removeAllCards();
        }
        this.handsOnTable.clear();
    }

    private void continueGame() {
        this.table.resetSelected();
        this.table.setActivePlayer(this.getCurrentIdx());
        this.table.repaint();
    }
}
