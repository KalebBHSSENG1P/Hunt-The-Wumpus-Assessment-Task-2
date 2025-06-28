import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class HuntTheWumpus extends JPanel implements KeyListener {

    // Constants for directions
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;

    // Colours
    private static final Color BROWN = new Color(193,154,107);
    private static final Color BLACK = Color.BLACK;
    private static final Color RED = new Color(138,7,7);

    // Game settings
    private static final int NUM_BATS = 3;
    private static final int NUM_PITS = 3;
    private static final int NUM_ARROWS = 1;
    private static final boolean MOBILE_WUMPUS = false;
    private static final int WUMPUS_MOVE_CHANCE = 50; // %

    // Cave layout: Map<Room, List of connected rooms [UP, DOWN, LEFT, RIGHT]>
    private static final Map<Integer, int[]> cave = new HashMap<>();

    static {
        cave.put(1, new int[]{0,8,2,5});
        cave.put(2, new int[]{0,10,3,1});
        cave.put(3, new int[]{0,12,4,2});
        cave.put(4, new int[]{0,14,5,3});
        cave.put(5, new int[]{0,6,1,4});
        cave.put(6, new int[]{5,0,7,15});
        cave.put(7, new int[]{0,17,8,6});
        cave.put(8, new int[]{1,0,9,7});
        cave.put(9, new int[]{0,18,10,8});
        cave.put(10,new int[]{2,0,11,9});
        cave.put(11,new int[]{0,19,12,10});
        cave.put(12,new int[]{3,0,13,11});
        cave.put(13,new int[]{0,20,14,12});
        cave.put(14,new int[]{4,0,15,13});
        cave.put(15,new int[]{0,16,6,14});
        cave.put(16,new int[]{15,0,17,20});
        cave.put(17,new int[]{7,0,18,16});
        cave.put(18,new int[]{9,0,19,17});
        cave.put(19,new int[]{11,0,20,18});
        cave.put(20,new int[]{13,0,16,19});
    }

    // Game state variables
    private int playerPos;
    private int wumpusPos;
    private int numArrows;
    private final List<Integer> batsList = new ArrayList<>();
    private final List<Integer> pitsList = new ArrayList<>();
    private final List<Integer> arrowsList = new ArrayList<>();
    private Random random = new Random();

    // Player sprites
    private Image playerUpImage;
    private Image playerDownImage;
    private Image playerLeftImage;
    private Image playerRightImage;

    {
        try {
        playerUpImage = new ImageIcon("images/player_up.png").getImage();
        playerDownImage = new ImageIcon("images/player_down.png").getImage();
        playerLeftImage = new ImageIcon("images/player_left.png").getImage();
        playerRightImage = new ImageIcon("images/player.png").getImage();
        } 
        catch (Exception e) {
            System.err.println("Failed to load player images.");
            }
    }

    // Window size
    private int width = 800;
    private int height = 600;

    // Game messages
    private String message = "";
    private int playerDirection;

    // Main constructor
    public HuntTheWumpus() {
        setPreferredSize(new Dimension(width, height));
        setBackground(BLACK);
        setFocusable(true);
        addKeyListener(this);

        resetGame();
    }

    // Reset game state
    private void resetGame() {
        batsList.clear();
        pitsList.clear();
        arrowsList.clear();
        numArrows = NUM_ARROWS;

        // Place player
        playerPos = random.nextInt(20) + 1;

        // Place wumpus
        placeWumpus();

        // Place bats
        for (int i=0; i<NUM_BATS; i++) placeBat();

        // Place pits
        for (int i=0; i<NUM_PITS; i++) placePit();

        // Place arrows
        for (int i=0; i<NUM_ARROWS; i++) placeArrow();

        message = "Welcome to Hunt the Wumpus! Use arrow keys to move. Shift + arrow to shoot.";
    }

    // Place wumpus in a random room not the player's
    private void placeWumpus() {
        wumpusPos = playerPos;
        while (wumpusPos == playerPos) {
            wumpusPos = random.nextInt(20) + 1;
        }
    }

    private void placeBat() {
        int pos;
        do {
            pos = random.nextInt(20) + 1;
        } while (pos == playerPos || batsList.contains(pos) || pos == wumpusPos || pitsList.contains(pos));
        batsList.add(pos);
    }

    private void placePit() {
        int pos;
        do {
            pos = random.nextInt(20) + 1;
        } while (pos == playerPos || batsList.contains(pos) || pos == wumpusPos || pitsList.contains(pos));
        pitsList.add(pos);
    }

    private void placeArrow() {
        int pos;
        do {
            pos = random.nextInt(20) + 1;
        } while (pos == playerPos || batsList.contains(pos) || pos == wumpusPos || pitsList.contains(pos));
        arrowsList.add(pos);
    }

    // Check hazards in the room player moved to
    private void checkRoom() {
        if (playerPos == wumpusPos) {
            gameOver("You were eaten by the WUMPUS!!!");
        }
        if (pitsList.contains(playerPos)) {
            gameOver("You fell into a bottomless pit!!");
        }
        if (batsList.contains(playerPos)) {
            message = "Bats pick you up and drop you elsewhere!";
            repaint();
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

            // Move the bats (remove current bat)
            batsList.remove((Integer)playerPos);
            int newBatPos;
            do {
                newBatPos = random.nextInt(20) + 1;
            } while (newBatPos == playerPos || batsList.contains(newBatPos) || newBatPos == wumpusPos || pitsList.contains(newBatPos));
            batsList.add(newBatPos);

            // Move the player
            int newPlayerPos;
            do {
                newPlayerPos = random.nextInt(20) + 1;
            } while (newPlayerPos == playerPos || batsList.contains(newPlayerPos) || newPlayerPos == wumpusPos || pitsList.contains(newPlayerPos));
            playerPos = newPlayerPos;
            message = "You are now in room " + playerPos;
        }

        if (arrowsList.contains(playerPos)) {
            message = "You have found an arrow!";
            repaint();
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            arrowsList.remove((Integer)playerPos);
            numArrows++;
        }
    }

    // Game over message and exit
    private void gameOver(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    // Move the wumpus randomly if allowed
    private void moveWumpus() {
        if (!MOBILE_WUMPUS) return;
        if (random.nextInt(100) > WUMPUS_MOVE_CHANCE) return;

        int[] exits = cave.get(wumpusPos);
        for (int newRoom : exits) {
            if (newRoom == 0 || newRoom == playerPos) continue;
            if (batsList.contains(newRoom)) continue;
            if (pitsList.contains(newRoom)) continue;
            wumpusPos = newRoom;
            break;
        }
    }

    // Shoot an arrow in given direction
    private void shootArrow(int direction) {
        if (numArrows == 0) {
            message = "No arrows left!";
            return;
        }
        numArrows--;

        int[] exits = cave.get(playerPos);
        int targetRoom = exits[direction];
        if (targetRoom == wumpusPos) {
            gameOver("Your aim was true and you killed the Wumpus!");
        } else {
            message = "Your arrow missed...";
            placeWumpus(); // Move wumpus
        }
        if (numArrows == 0) {
            gameOver("You ran out of arrows and died!");
        }
    }

    // Check neighbors for hazards (used for warning messages)
    private boolean checkNeighborRooms(int pos, List<Integer> list) {
        int[] exits = cave.get(pos);
        for (int room : exits) {
            if (room > 0 && list.contains(room)) return true;
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        g.setColor(BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw room circle (brown)
        int radius = Math.min(getWidth(), getHeight()) / 3;
        g.setColor(BROWN);
        g.fillOval(getWidth()/2 - radius, getHeight()/2 - radius, radius*2, radius*2);

        // Draw exits
        int exitWidth = 80;
        int exitLength = getWidth() / 2;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int[] exits = cave.get(playerPos);
        g.setColor(BROWN);
        if (exits[LEFT] > 0) {
            g.fillRect(centerX - radius - exitLength, centerY - exitWidth / 2, exitLength, exitWidth);
        }

        if (exits[RIGHT] > 0) {
            g.fillRect(centerX + radius, centerY - exitWidth / 2, exitLength, exitWidth);
        }

        if (exits[UP] > 0) {
            g.fillRect(centerX - exitWidth / 2, centerY - radius - exitLength, exitWidth, exitLength);
        }

        if (exits[DOWN] > 0) {
            g.fillRect(centerX - exitWidth / 2, centerY + radius, exitWidth, exitLength);
        }

        // Draw warnings for hazards nearby
        if (checkNeighborRooms(playerPos, Collections.singletonList(wumpusPos))) {
            g.setColor(RED);
            g.fillOval(getWidth()/2 - radius/2, getHeight()/2 - radius/2, radius, radius);
        }
        if (checkNeighborRooms(playerPos, batsList)) {
            g.setColor(Color.WHITE);
            g.drawString("You hear the squeaking of bats nearby", 10, 20);
        }
        if (checkNeighborRooms(playerPos, pitsList)) {
            g.setColor(Color.WHITE);
            g.drawString("You feel a draft from a nearby pit", 10, 40);
        }

        // Draw player pointing direction depending on key input
        Image imgToDraw = null;

        switch (playerDirection) {
            case UP:
                imgToDraw = playerUpImage;
                break;
            case DOWN:
                imgToDraw = playerDownImage;
                break;
            case LEFT:
                imgToDraw = playerLeftImage;
                break;
            case RIGHT:
                imgToDraw = playerRightImage;
                break;
        }

        if (imgToDraw != null) {
            // Draw image centered at centerX, centerY
            int imgWidth = imgToDraw.getWidth(null);
            int imgHeight = imgToDraw.getHeight(null);
            g.drawImage(imgToDraw, centerX - imgWidth/2, centerY - imgHeight/2, null);
        }

        // Draw HUD info
        g.setColor(Color.WHITE);
        g.drawString("Room: " + playerPos, 10, getHeight() - 60);
        g.drawString("Arrows: " + numArrows, 10, getHeight() - 40);
        g.drawString(message, 10, getHeight() - 20);
    }

    // Move player if possible
    private void movePlayer(int direction) {
        int[] exits = cave.get(playerPos);
        int nextRoom = exits[direction];
        if (nextRoom == 0) {
            message = "You can't move that way!";
        } else {
            playerPos = nextRoom;
            playerDirection = direction;
            message = "You moved to room " + playerPos;
            checkRoom();
            moveWumpus();
        }
        repaint();
    }

    // Handle key presses
    @Override
    public void keyPressed(KeyEvent e) {
        boolean shift = e.isShiftDown();
        int key = e.getKeyCode();

        if (!shift) {
            switch (key) {
                case KeyEvent.VK_UP: movePlayer(UP); break;
                case KeyEvent.VK_DOWN: movePlayer(DOWN); break;
                case KeyEvent.VK_LEFT: movePlayer(LEFT); break;
                case KeyEvent.VK_RIGHT: movePlayer(RIGHT); break;
            }
        } else {
            switch (key) {
                case KeyEvent.VK_UP: shootArrow(UP); break;
                case KeyEvent.VK_DOWN: shootArrow(DOWN); break;
                case KeyEvent.VK_LEFT: shootArrow(LEFT); break;
                case KeyEvent.VK_RIGHT: shootArrow(RIGHT); break;
            }
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    // Main method to start the game
    public static void main(String[] args) {
        JFrame frame = new JFrame("Hunt the Wumpus");
        HuntTheWumpus game = new HuntTheWumpus();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}