package view;

import model.game.GameInitialConfigurations;
import presenter.WumpusPresenter;
import presenter.WumpusPresenterImpl;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class WumpusViewImpl extends JPanel implements WumpusView {
    private static final boolean CHEAT_MODE = false;

    WumpusPresenter wumpusPresenter;
    public static final int PANEL_WIDTH = 721;
    public static final int PANEL_HEIGHT = 717;
    private Mode currentMode;
    private long animationStartTime;

    final int[][] cavesCoordinates = {{334, 20}, {609, 220}, {499, 540}, {169, 540}, {62, 220},
            {169, 255}, {232, 168}, {334, 136}, {435, 168}, {499, 255}, {499, 361},
            {435, 447}, {334, 480}, {232, 447}, {169, 361}, {254, 336}, {285, 238},
            {387, 238}, {418, 336}, {334, 393}};

    final int caveSize = 45;
    final int playerSize = 16;

    final int invalidCave = -1;

    Graphics2D g;

    private boolean gameStarting = true;
    private int[] intendedCavesToShoot = new int[]{};
    private double animationColorFraction;
    private int[] actualPlayerCavesShot = new int[]{};
    private int[] enemyPlayerCavesShot = new int[]{};
    private final int animationDuration = 1000;

    public WumpusViewImpl() {

        wumpusPresenter = new WumpusPresenterImpl();
        wumpusPresenter.startNewGame();

        Mode.setView(this);
        this.currentMode = new MoveMode();

        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.white);
        setForeground(Color.lightGray);
        setFont(new Font("SansSerif", Font.PLAIN, 18));
        setFocusable(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mouseClickXAxis = e.getX();
                int mouseClickYAxis = e.getY();
                handleMouseClick(mouseClickXAxis, mouseClickYAxis, isLeftMouseButton(e), isRightMouseButton(e));
            }

        });
    }

    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (!gameStarting) {
            gameStarting = wumpusPresenter.isGameOver();
        }
        try {
            drawMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

        calculateAnimationIncrement();
    }

    @Override
    public void render() {
        repaint();
    }

    private void handleMouseClick(int mouseClickXAxis, int mouseClickYAxis, boolean leftClick, boolean rightClick) {
        if (wumpusPresenter.isGameOver()) {
            wumpusPresenter.startNewGame();
        } else {
            continueGame(mouseClickXAxis, mouseClickYAxis, leftClick, rightClick);
        }
        render();
    }

    private void continueGame(int mouseClickXAxis, int mouseClickYAxis, boolean leftClick, boolean rightClick) {
        int selectedCave = getSelectedCaveBasedOnMouseClickLocation(mouseClickXAxis, mouseClickYAxis);
        if (leftClick && selectedCave != invalidCave) {
            currentMode.handleLeftClick(selectedCave);
        } else if (rightClick) {
            currentMode.handleRightClick();
        }
    }

    private int getSelectedCaveBasedOnMouseClickLocation(int mouseClickXAxis, int mouseClickYAxis) {
        int selectedCave = invalidCave;
        for (int caveNumber = 0; caveNumber < cavesCoordinates.length; caveNumber++) {
            int[] cave = cavesCoordinates[caveNumber];
            int xAxisOfCaveLinkedToCurrentCave = cave[0];
            int yAxisOfCaveLinkedToCurrentCave = cave[1];
            if (isMouseClickWithinCorrectCave(mouseClickXAxis, mouseClickYAxis, xAxisOfCaveLinkedToCurrentCave, yAxisOfCaveLinkedToCurrentCave)) {
                selectedCave = caveNumber;
                break;
            }
        }
        return selectedCave;
    }

    private boolean isMouseClickWithinCorrectCave(int mouseClickXAxis, int mouseClickYAxis, int caveXAxis, int caveYAxis) {
        return (mouseClickXAxis > caveXAxis && mouseClickXAxis < caveXAxis + getCaveSize())
                && (mouseClickYAxis > caveYAxis && mouseClickYAxis < caveYAxis + getCaveSize());
    }

    void drawPlayer() throws IOException {
        int x = cavesCoordinates[wumpusPresenter.getPlayerCaveIndex()][0] + (getCaveSize() - getPlayerSize()) / 2;
        int y = cavesCoordinates[wumpusPresenter.getPlayerCaveIndex()][1] + (getCaveSize() - getPlayerSize()) - 2;

        Path2D player = new Path2D.Double();
        player.moveTo(x, y);
        player.lineTo(x + getPlayerSize(), y);
        player.lineTo(x + getPlayerSize() / 2, y - getPlayerSize());
        player.closePath();

        g.setColor(Color.white);
        g.fill(player);
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.black);
        g.draw(player);

        if (CHEAT_MODE) {
            drawHazards();
        }
    }

    private void drawHazards() throws IOException {
        drawHazardWithOffset("wumpus.png", wumpusPresenter.getWumpusCaveIndex(), +1, -1);

        for (int batCave : wumpusPresenter.getBatCaves()) {
            drawHazardWithOffset("bat.jpg", batCave, +1, +1);
        }

        for (int pitCave : wumpusPresenter.getPitsCaves()) {
            drawHazardWithOffset("pit.jpg", pitCave, -1, +1);
        }
        if (wumpusPresenter.isEnemyPlayerDead() || wumpusPresenter.getEnemyRemainingArrows() == 0) {
            drawHazardWithOffset("enemy-dead.png", wumpusPresenter.getEnemyPlayerCave(), -1, -1);
        } else {
            drawHazardWithOffset("enemy.png", wumpusPresenter.getEnemyPlayerCave(), -1, -1);
        }
    }

    private void drawHazardWithOffset(String imageName, int cave, int xOffsetSign, int yOffsetSign) throws IOException {
        Image wumpusImage = createImageObject(imageName, caveSize, caveSize);
        int[] wumpusCaveCoordinates = cavesCoordinates[cave];
        g.drawImage(wumpusImage, wumpusCaveCoordinates[0] + caveSize / 2 * xOffsetSign, wumpusCaveCoordinates[1] + caveSize / 2 * yOffsetSign, null);
    }

    void drawStartScreen() throws IOException {
        drawBackgroundMap();
        g.setColor(new Color(0xDDFFFFFF, true));
        g.fillRect(0, 0, getWidth(), getHeight() - 60);

        g.setColor(Color.darkGray);
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        g.drawString("Hunt the Wumpus", 160, 240);

        g.setFont(getFont());
        g.drawString("left-click to move, right-click to shoot", 210, 310);
        g.drawString("be aware that hazards may be in the same cave", 175, 345);
        g.drawString("click to start", 310, 380);

        if (wumpusPresenter.isGameWon()) {
            drawEndGameImage("won.png");
        }

        if (wumpusPresenter.isGameLost()) {
            drawEndGameImage("lost.png");
        }
    }

    private void drawEndGameImage(String imageName) throws IOException {
        int imageLength = 300;
        Image winImage = createImageObject(imageName, imageLength, imageLength);
        g.drawImage(winImage, PANEL_WIDTH - imageLength - 20, PANEL_HEIGHT - imageLength - 20, null);
    }

    void drawCaves() throws IOException {
        drawBackgroundMap();
        painLinkedCaves();
        paintPlayerCurrentCave();
        paintSelectedCavesToShoot();
        animateActuallyShotCaves();
        drawGameModeIcon();
        drawCaveBorders();
    }

    private void drawBackgroundMap() {
        drawLinksBetweenCaves();
        drawAndPaintAllCaves();
    }

    private void drawCaveBorders() {
        g.setColor(Color.darkGray);
        for (int[] r : cavesCoordinates)
            g.drawOval(r[0], r[1], getCaveSize(), getCaveSize());
    }

    private void drawLinksBetweenCaves() {
        g.setColor(Color.darkGray);
        g.setStroke(new BasicStroke(2));

        for (int i = 0; i < GameInitialConfigurations.CAVE_LINKS.length; i++) {
            for (int link : GameInitialConfigurations.CAVE_LINKS[i]) {
                int x1 = cavesCoordinates[i][0] + getCaveSize() / 2;
                int y1 = cavesCoordinates[i][1] + getCaveSize() / 2;
                int x2 = cavesCoordinates[link][0] + getCaveSize() / 2;
                int y2 = cavesCoordinates[link][1] + getCaveSize() / 2;
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }

    private void drawAndPaintAllCaves() {
        g.setColor(currentMode.getAllCavesColor());
        for (int[] r : cavesCoordinates)
            g.fillOval(r[0], r[1], getCaveSize(), getCaveSize());
    }

    private void painLinkedCaves() {
        g.setColor(currentMode.getLinkedCavesColor());
        for (int link : GameInitialConfigurations.CAVE_LINKS[wumpusPresenter.getPlayerCaveIndex()])
            g.fillOval(cavesCoordinates[link][0], cavesCoordinates[link][1], getCaveSize(), getCaveSize());
    }

    private void paintPlayerCurrentCave() {
        g.setColor(Color.gray);
        int[] currentCaveCoordinates = cavesCoordinates[wumpusPresenter.getPlayerCaveIndex()];
        g.fillOval(currentCaveCoordinates[0], currentCaveCoordinates[1], getCaveSize(), getCaveSize());
    }

    private void paintSelectedCavesToShoot() {
        for (int i = 0; i < intendedCavesToShoot.length; i++) {
            int cave = intendedCavesToShoot[i];
            g.setColor(Color.yellow);
            int[] caveCoordinates = cavesCoordinates[cave];
            g.fillOval(caveCoordinates[0], caveCoordinates[1], getCaveSize(), getCaveSize());
            g.setColor(Color.black);
            g.drawString(String.valueOf(i + 1), caveCoordinates[0], caveCoordinates[1] - 25);
        }
    }

    private void animateActuallyShotCaves() {
        animateTheseCaves(actualPlayerCavesShot, new Color(0, 0, 0, 1 - (float) animationColorFraction));

        animateTheseCaves(enemyPlayerCavesShot, new Color(0, 1, 1, 1 - (float) animationColorFraction));
    }

    private void animateTheseCaves(int[] enemyPlayerCavesShot, Color color) {
        g.setColor(color);
        for (int cave : enemyPlayerCavesShot) {
            int[] caveCoordinates = cavesCoordinates[cave];
            g.fillOval(caveCoordinates[0], caveCoordinates[1], caveSize, caveSize);
        }
    }

    private void drawGameModeIcon() throws IOException {
        Image newImage = createImageObject(currentMode.getIconName(), PANEL_WIDTH / 5 - 20, PANEL_HEIGHT / 5);
        g.drawImage(newImage, 20, 50, null);
    }

    private Image createImageObject(String imageName, int imageWidth, int imageHeight) throws IOException {
        final BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir") + "\\src\\main\\java\\view\\resources\\" + imageName));
        return image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
    }

    void drawMessage() {
        if (!gameStarting) {
            g.drawString("Arrows  " + wumpusPresenter.getNumberOfArrows(), 610, 30);
            g.drawString("Mode: " + currentMode.toString(), 30, 30);
        }

        if (wumpusPresenter.getWarnings() != null) {
            g.setColor(Color.black);

            List<String> messages = wumpusPresenter.getWarnings();
            // collapse identical messages
            messages = messages.stream().distinct().collect(toList());

            // concat at most three
            String msg = messages.stream().limit(3).collect(joining(" & "));
            g.drawString(msg, 20, getHeight() - 60);

            // if there's more, print underneath
            if (messages.size() > 3) {
                g.drawString("& " + messages.get(3), 20, getHeight() - 37);
            }

            drawShotCavesMessage(actualPlayerCavesShot, "You shot cave(s): ", 40);

            if (CHEAT_MODE) {
                drawShotCavesMessage(enemyPlayerCavesShot, "Enemy shot cave(s): ", 20);
            }
        }
    }

    private void drawShotCavesMessage(int[] shotCavesArray, String message, int bottomOffset) {
        if (shotCavesArray.length > 0) {
            String shotCavesMessage = Arrays.stream(shotCavesArray)
                    .mapToObj(String::valueOf)
                    .collect(joining(", ", message, ""));
            g.drawString(shotCavesMessage, PANEL_WIDTH / 2, getHeight() - bottomOffset);
        }
    }

    private void calculateAnimationIncrement() {
        long currentTime = System.nanoTime() / 1000000;
        long runningTime = currentTime - animationStartTime;
        final double animationDurationFraction = runningTime * 1.0 / animationDuration;

        if (animationDurationFraction >= 1) {
            actualPlayerCavesShot = new int[]{};
            enemyPlayerCavesShot = new int[]{};
            return;
        }

        render();
        animationColorFraction = Math.min(0.8, animationDurationFraction);
    }

    private void drawMap() throws IOException {
        if (gameStarting) {
            this.currentMode = new MoveMode();
            drawStartScreen();
            gameStarting = false;
        } else {
            drawCaves();
            drawPlayer();
        }
        drawMessage();
    }

    public int getCaveSize() {
        return caveSize;
    }

    public int getPlayerSize() {
        return playerSize;
    }

    public void move(int cave) {
        final Map<String, int[]> playerCavesMap = wumpusPresenter.move(cave);
        actualPlayerCavesShot = playerCavesMap.get("player");
        enemyPlayerCavesShot = playerCavesMap.get("enemy player");
        animationStartTime = (System.nanoTime() / 1000000);
    }

    public void setMode(Mode newMode) {
        this.currentMode = newMode;
    }

    public void shoot(int... caves) {
        if (caves.length == 0) {
            return;
        }
        final Map<String, int[]> playerCavesMap = wumpusPresenter.shoot(caves);
        actualPlayerCavesShot = playerCavesMap.get("player");
        enemyPlayerCavesShot = playerCavesMap.get("enemy player");
        animationStartTime = (System.nanoTime() / 1000000);
    }

    public void setCavesToShoot(int... caves) {
        this.intendedCavesToShoot = caves;
    }
}
