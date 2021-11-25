import java.util.*;

enum Color {
    // Contains the Card Colors
    RED, GREEN, BLUE, YELLOW;

    public String toString() {
        switch (this) {
        case BLUE:
            return "B";
        case GREEN:
            return "G";
        case RED:
            return "R";
        case YELLOW:
            return "Y";
        default:
            return "ERROR";

        }
    }
}

enum Power {
    // Contains Special Card Powers
    REVERSE, SKIP, DRAW2;

    public String toString() {
        switch (this) {
        case DRAW2:
            return "Draw2";
        case REVERSE:
            return "Rev";
        case SKIP:
            return "Skip";
        default:
            return "ERROR";
        }
    }

}

interface Card {
    // Intervace for Card
    String cardVal();
}

class PlayingCard implements Card {

    // Base Class for Cards
    // [color] is shared by both Normal and Special Card
    public Color color;

    public PlayingCard(Color color) {
        this.color = color;
    }

    // Gets card Value
    public String cardVal() {
        return this.color.toString();
    }

    // Compares the color
    @Override
    public boolean equals(Object o) {
        if (o.getClass() == this.getClass()) {
            PlayingCard p = (PlayingCard) o;
            return p.color == this.color;
        }
        return false;
    }

}

class NormalCard extends PlayingCard {
    // [value] stores the number on the noraml card
    public int value;

    public NormalCard(Color color, int value) {
        super(color);
        this.value = value;
    }

    // Returns Card value
    public String cardVal() {
        return super.color.toString() + "_" + value;
    }

    // Compares color and number
    @Override
    public boolean equals(Object o) {
        if (o.getClass() == this.getClass()) {
            NormalCard p = (NormalCard) o;
            return p.value == this.value && p.color == this.color;
        }
        return false;
    }
}

class SpecialCard extends PlayingCard {

    // [power] stores the power on special Card
    public Power power;

    public SpecialCard(Color color, Power power) {
        super(color);
        this.power = power;
    }

    // Returns Card value
    public String cardVal() {
        return this.color.toString() + "_" + power.toString();
    }

    // Compares color and power
    @Override
    public boolean equals(Object o) {
        if (o.getClass() == this.getClass()) {
            SpecialCard p = (SpecialCard) o;
            return p.color == this.color && p.power == this.power;
        }
        return false;
    }
}

class Player extends Thread {
    // stores the top card of the discarded pile
    private PlayingCard card;
    // toggles on drawing a card from the deck
    private boolean alreadyDrawn;
    // stores the top card of the draw pile
    private PlayingCard nextCard;
    // stores the result of the
    private Map<String, PlayingCard> resultOFChance;

    // Sets parameters for the playing
    public void setParam(PlayingCard card, PlayingCard nextCard, boolean alreadyDrawn) {
        this.card = card;
        this.nextCard = nextCard;
        this.alreadyDrawn = alreadyDrawn;

    }

    // Gets result of Player's turn
    public Map<String, PlayingCard> getResult() {
        return this.resultOFChance;
    }

    // Clears the result Map for next turn
    public void clearResults() {
        resultOFChance.clear();
    }

    // Player name.
    private String name;
    // Player hand.
    private List<PlayingCard> hand = new ArrayList<PlayingCard>();

    // Constructor which sets player name to [name].
    public Player(String name) {
        this.name = name;
    }

    // Adds [card] to player hands.
    public void addCard(PlayingCard card) {
        this.hand.add(card);
    }

    // Getter for Player name.
    public String getPlayerName() {
        return this.name;
    }

    // Prints Player hand.
    private void printHand() {
        for (PlayingCard card : this.hand) {
            System.out.print(card.cardVal() + " ");
        }
        System.out.println("");
    }

    public int getCardsRemaining() {
        return this.hand.size();
    }

    // Player plays his chance.
    // [card] is the top card of discarded pile.
    // as 2 playes cannot play simultaneously, [play] must be synchronised
    synchronized private void play() {

        // Print in hand cards
        printHand();

        // First Check if any card is available to play
        Map<String, PlayingCard> result = new HashMap<String, PlayingCard>();
        if (alreadyDrawn) {
            result.put("drawn", nextCard);
        }
        if (!check(card) && !alreadyDrawn) {
            System.out.println("No possible chance, drawing card from stack.");

            this.hand.add(nextCard);
            printHand();
            alreadyDrawn = true;
            // Add the drawn card in hand
            result.put("drawn", nextCard);
            if (!checkValidity(card, nextCard)) {
                alreadyDrawn = false;
                // if drawn card is not valid
                System.out.println("No possible plays. Passing chance to next player.");
                this.resultOFChance = result;
                return;
            }
        }
        System.out.println("What do you want to play?");
        String input = UNO.scanner.nextLine();
        try {
            PlayingCard userChance = parseUserChance(input);
            if (checkValidity(card, userChance)) {

                boolean validCard = false;
                for (int i = 0; i < this.hand.size(); i++) {
                    validCard = checkEquality(userChance, this.hand.get(i));
                    if (validCard) {
                        break;
                    }
                }
                if (validCard) {
                    for (int i = 0; i < this.hand.size(); i++) {
                        if (this.hand.get(i).equals(userChance)) {
                            this.hand.remove(i);
                            break;
                        }
                    }
                    alreadyDrawn = false;
                    result.put("played", userChance);
                    this.resultOFChance = result;
                    return;
                } else {
                    System.out.println("Please enter card from your hand.");
                    play();
                }
            } else {
                System.out.println("Please play valid chance.");
                play();
            }
        } catch (Exception e) {
            System.out.println("Please enter just like the card as printed.");
            play();
        }
    }

    // Checks for good card in the present hand
    private boolean check(PlayingCard topOfStack) {
        boolean available = false;
        for (int i = 0; i < this.hand.size(); i++) {
            available = checkValidity(topOfStack, this.hand.get(i));
            if (available) {
                break;
            }
        }
        return available;
    }

    // Checks whether user played valid card.
    // Depending on [topOfStack] card and [playerChance] card.
    // Returns [false] if [topOfStack] card and [playerChance] card if:
    // 1) if both are normal cards with unequal value and different color,
    // 2) if both are special cards with unequal power and different color.
    // Else returns [true].
    private boolean checkValidity(PlayingCard topOfStack, PlayingCard playerChance) {
        int colorComparison = topOfStack.color.compareTo(playerChance.color);
        // System.out.println("Color comparison : " + colorComparison);
        if (colorComparison != 0) {
            if (topOfStack.getClass() == NormalCard.class && playerChance.getClass() == NormalCard.class) {
                NormalCard tc = (NormalCard) topOfStack;
                NormalCard pc = (NormalCard) playerChance;
                // System.out.println(tc.color.toString() + " " + tc.value + " " + pc.value);
                if (tc.value == pc.value) {
                    // System.out.println("Color not equal but value equal");
                    return true;
                }

            } else if (topOfStack.getClass() == SpecialCard.class && playerChance.getClass() == SpecialCard.class) {
                SpecialCard tc = (SpecialCard) topOfStack;
                SpecialCard pc = (SpecialCard) playerChance;
                // System.out.println(tc.color.toString() + " " + tc.power.toString() + " " +
                // pc.power.toString());
                if (tc.power.equals(pc.power)) {
                    // System.out.println("Color not equal but value power: " + powerComparison);
                    return true;
                }
            }

            return false;

        }
        return true;
    }

    // Check if equal
    private boolean checkEquality(PlayingCard topOfStack, PlayingCard playerChance) {
        int colorComparison = topOfStack.color.compareTo(playerChance.color);
        // System.out.println("Color comparison : " + colorComparison);
        if (colorComparison == 0) {
            if (topOfStack.getClass() == NormalCard.class && playerChance.getClass() == NormalCard.class) {
                NormalCard tc = (NormalCard) topOfStack;
                NormalCard pc = (NormalCard) playerChance;
                // System.out.println(tc.color.toString() + " " + tc.value + " " + pc.value);
                if (tc.value == pc.value) {
                    // System.out.println("Color equal value equal");
                    return true;
                }

            } else if (topOfStack.getClass() == SpecialCard.class && playerChance.getClass() == SpecialCard.class) {
                SpecialCard tc = (SpecialCard) topOfStack;
                SpecialCard pc = (SpecialCard) playerChance;
                // System.out.println(tc.color.toString() + " " + tc.power.toString() + " " +
                // pc.power.toString());
                int powerComparison = tc.power.compareTo(pc.power);
                if (powerComparison == 0) {
                    // System.out.println("Color equal value power: " + powerComparison);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    // Parses [userInput] to Playing card object
    private PlayingCard parseUserChance(String userInput) throws Exception {
        String[] list = userInput.split("_");
        if (list.length != 2) {
            throw new Exception();
        } else {
            Color color;
            if (list[0].charAt(0) == 'r' || list[0].charAt(0) == 'R') {
                color = Color.RED;
            } else if (list[0].charAt(0) == 'b' || list[0].charAt(0) == 'B') {
                color = Color.BLUE;
            } else if (list[0].charAt(0) == 'y' || list[0].charAt(0) == 'Y') {
                color = Color.YELLOW;
            } else if (list[0].charAt(0) == 'g' || list[0].charAt(0) == 'G') {
                color = Color.GREEN;
            } else {
                throw new Exception();
            }
            // System.out.println("User color : " +color.toString());
            if (list[1].length() > 1) {
                Power power;
                if (list[1].compareTo("Skip") == 0) {
                    power = Power.SKIP;
                } else if (list[1].compareToIgnoreCase("Rev") == 0) {
                    power = Power.REVERSE;
                } else if (list[1].compareTo("Draw2") == 0) {
                    power = Power.DRAW2;
                } else {
                    throw new Exception();
                }
                // System.out.println("User Power : " +power.toString());
                return new SpecialCard(color, power);
            } else {
                int i = Integer.parseInt(list[1]);
                // System.out.println("User value : " +i);
                return new NormalCard(color, i);
            }
        }
    }

    @Override
    public void run() {
        play();
    }

}

class Game {

    // Contains cards to draw
    private List<PlayingCard> drawPile = new ArrayList<PlayingCard>();
    // Contains discarded cards
    private List<PlayingCard> discardPile = new ArrayList<PlayingCard>();
    // Contains players
    private List<Player> players = new ArrayList<Player>();

    private int numberOfPlayers;

    // Initialises draw Deck which then distributes cards to players
    private void initialiseDeck() {
        for (Color color : Color.values()) {
            for (int i = 1; i < 10; i++) {
                drawPile.add(new NormalCard(color, i));
            }
            for (Power power : Power.values()) {
                drawPile.add(new SpecialCard(color, power));
            }
        }
        assert (drawPile.size() == 48);
    }

    // Shuffles draw deck
    private void shuffleDeck() {
        System.out.println("Shuffling Deck..");
        Collections.shuffle(drawPile);
    }

    // Adds Players who'll play
    private void addPlayers() {
        // Get number of Players as input
        System.out.println("Enter Number of players (between 2-5): ");
        String input = UNO.scanner.nextLine();
        try {
            int numberOfPlayers = Integer.parseInt(input);
            if (numberOfPlayers <= 5 && numberOfPlayers >= 2) {
                this.numberOfPlayers = numberOfPlayers;
                for (int i = 0; i < numberOfPlayers; i++) {
                    System.out.println("Enter name for Player " + (i + 1) + " : ");
                    input = UNO.scanner.nextLine();
                    this.players.add(new Player(input));
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("Please enter number from 2 to 5");
            addPlayers();
        }
    }

    // Distributes Cards to players
    private void distributeCards() {
        System.out.println("Distributing Cards.");
        for (int j = 0; j < 7; j++) {
            for (int i = 0; i < this.numberOfPlayers; i++) {
                this.players.get(i).addCard(this.drawPile.get(0));
                this.drawPile.remove(0);
            }
        }
    }

    // Print top card
    private void printTopCard() {
        assert (!this.discardPile.isEmpty());
        System.out.println("Top Card: " + this.discardPile.get(this.discardPile.size() - 1).cardVal());
    }

    private void fetchTopCard() {
        if (this.drawPile.get(0).getClass() == SpecialCard.class) {
            System.out.println("Got Special card !");
            shuffleDeck();
            fetchTopCard();
        } else {
            this.discardPile.add(this.drawPile.get(0));
            this.drawPile.remove(0);
        }
    }

    // Adds the [card] played by user in discard pile
    private void playCard(PlayingCard card) {
        this.discardPile.add(card);
        if (this.drawPile.isEmpty()) {
            System.out.println("Draw deck is empty.");
            this.drawPile = this.discardPile.subList(0, this.discardPile.size() - 1);
            shuffleDeck();
            this.discardPile = this.discardPile.subList(this.discardPile.size() - 1, this.discardPile.size());
        }
    }

    // Starts the game
    public void startGame() {
        // Add players
        addPlayers();
        // Initialise Deck
        initialiseDeck();
        // Shuffle Cards
        shuffleDeck();
        // Distribute Cards
        distributeCards();
        // Fetch Top card
        fetchTopCard();
        // [reverse] tells the order
        boolean reverse = false;
        // [initialIndex] start index for loops
        int initialIndex = 0;
        // [gameOver] breaks the main loop
        boolean gameOver = false;
        // Must be synchronised
        synchronized (this) {
            while (!gameOver) {
                // For reverse Order
                if (reverse) {
                    // [gotReverse] boolean to break the loop
                    boolean gotReverse = false;
                    for (int i = initialIndex; i >= 0; i--) {
                        // Main loop of game
                        printTopCard();
                        System.out.print("Player " + (i + 1) + " (" + this.players.get(i).getPlayerName() + ") : ");

                        this.players.get(i).setParam(this.discardPile.get(this.discardPile.size() - 1),
                                this.drawPile.get(0), false);

                        this.players.get(i).run();
                        Map<String, PlayingCard> playerChance = this.players.get(i).getResult();

                        if (playerChance.containsKey("drawn"))
                            this.drawPile.remove(0);

                        if (playerChance.containsKey("played")) {
                            if (playerChance.get("played").getClass() == NormalCard.class) {
                                playCard(playerChance.get("played"));
                            } else {
                                playCard(playerChance.get("played"));
                                SpecialCard sc = (SpecialCard) playerChance.get("played");
                                switch (sc.power) {
                                case DRAW2:
                                    this.players.get((i + 1) % (this.numberOfPlayers)).addCard(this.drawPile.get(0));
                                    this.drawPile.remove(0);
                                    this.players.get((i + 1) % (this.numberOfPlayers)).addCard(this.drawPile.get(0));
                                    this.drawPile.remove(0);
                                    System.out.println("Next Player Draws 2");
                                    break;
                                case REVERSE:
                                    if (i == 0) {
                                        initialIndex = this.numberOfPlayers - 1;
                                    } else {
                                        initialIndex = i + 1;
                                    }
                                    reverse = false;
                                    gotReverse = true;
                                    System.out.println("Reverse Order");
                                    break;
                                case SKIP:
                                    System.out.println("Skipping next player");
                                    i--;
                                    i = i + this.numberOfPlayers;
                                    i = i % (this.numberOfPlayers);
                                    break;

                                }

                            }
                        }

                        this.players.get(i).clearResults();
                        if (this.players.get(i).getCardsRemaining() == 0) {
                            System.out.println("End Of Game\nPlayer " + (i + 1) + " ( "
                                    + this.players.get(i).getPlayerName() + " ) wins !!!");
                            gameOver = true;
                            break;

                        }
                        if (gotReverse) {
                            break;
                        }

                    }
                    if (!gotReverse) {
                        initialIndex = this.numberOfPlayers - 1;
                    }
                }
                // For normal Order
                else {
                    // [gotReverse] boolean to break the loop
                    boolean gotReverse = false;
                    for (int i = initialIndex; i < this.players.size(); i++) {
                        // Main loop of game
                        printTopCard();
                        System.out.print("Player " + (i + 1) + " (" + this.players.get(i).getPlayerName() + ") : ");

                        this.players.get(i).setParam(this.discardPile.get(this.discardPile.size() - 1),
                                this.drawPile.get(0), false);

                        this.players.get(i).run();
                        Map<String, PlayingCard> playerChance = this.players.get(i).getResult();

                        if (playerChance.containsKey("drawn"))
                            this.drawPile.remove(0);

                        if (playerChance.containsKey("played")) {
                            if (playerChance.get("played").getClass() == NormalCard.class) {
                                playCard(playerChance.get("played"));
                            } else {
                                playCard(playerChance.get("played"));
                                SpecialCard sc = (SpecialCard) playerChance.get("played");
                                switch (sc.power) {
                                case DRAW2:
                                    this.players.get((i + 1) % (this.numberOfPlayers)).addCard(this.drawPile.get(0));
                                    this.drawPile.remove(0);
                                    this.players.get((i + 1) % (this.numberOfPlayers)).addCard(this.drawPile.get(0));
                                    this.drawPile.remove(0);
                                    System.out.println("Next Player Draws 2");
                                    break;
                                case REVERSE:
                                    gotReverse = true;
                                    if (i == this.numberOfPlayers - 1) {
                                        initialIndex = 0;
                                    } else {
                                        initialIndex = i - 1;
                                    }
                                    reverse = true;
                                    System.out.println("Reverse Order");
                                    break;
                                case SKIP:
                                    System.out.println("Skipping next player");
                                    i++;
                                    i = i % (this.numberOfPlayers);
                                    break;

                                }

                            }
                        }

                        this.players.get(i).clearResults();
                        if (this.players.get(i).getCardsRemaining() == 0) {
                            System.out.println("End Of Game\nPlayer " + (i + 1) + " ( "
                                    + this.players.get(i).getPlayerName() + " ) wins !!!");
                            gameOver = true;
                            break;
                        }
                        if (gotReverse) {
                            break;
                        }

                    }
                    if (!gotReverse) {
                        initialIndex = 0;
                    }

                }
            }

        }
    }

}

public class UNO {
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Game game = new Game();
        // Start the game
        game.startGame();
    }

}
