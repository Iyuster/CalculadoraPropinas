package com.mycompany.calculadorapropinas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Juego extends JPanel {
    private enum GameState {
        MENU, PLAYING, PAUSED, GAME_OVER, WIN
    }

    private static final int BASE_WIDTH = 1280;
    private static final int BASE_HEIGHT = 720;
    private float scaleX, scaleY;
    private Character character;
    private ArrayList<Platform> platforms;
    private Meta meta;
    private boolean gameOver;
    private int score;
    private boolean win;
    private int level = 1;
    private GameState currentState;
    private JButton playButton, exitButton, resumeButton, mainMenuButton;

    public Juego() {
        JFrame frame = new JFrame("Plataformer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.setResizable(false);
        metodoDeJuego();
        createButtons();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Timer timer = new Timer(16, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentState == GameState.PLAYING) {
                    character.update();
                }
                repaint();
            }
        });
        timer.start();
    }

    private void createButtons() {
        playButton = new JButton("Jugar");
        exitButton = new JButton("Salir");
        resumeButton = new JButton("Reanudar");
        mainMenuButton = new JButton("Menú Principal");

        playButton.addActionListener(e -> {
            currentState = GameState.PLAYING;
            removeAll();
            revalidate();
            repaint();
        });

        exitButton.addActionListener(e -> System.exit(0));

        resumeButton.addActionListener(e -> {
            currentState = GameState.PLAYING;
            removeAll();
            revalidate();
            repaint();
        });

        mainMenuButton.addActionListener(e -> {
            currentState = GameState.MENU;
            removeAll();
            add(playButton);
            add(exitButton);
            revalidate();
            repaint();
        });
    }

    public void metodoDeJuego() {
        setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
        setBackground(Color.BLUE);
        character = new Character();
        platforms = new ArrayList<>();
        currentState = GameState.MENU;
        score = 0;
        loadLevel(level);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyRelease(e);
            }
        });
        setFocusable(true);
    }

    private void updateScaleFactors() {
        scaleX = (float) getWidth() / BASE_WIDTH;
        scaleY = (float) getHeight() / BASE_HEIGHT;
    }

    private void handleKeyPress(KeyEvent e) {
        switch (currentState) {
            case PLAYING:
                character.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    currentState = GameState.PAUSED;
                    drawPauseScreen(getGraphics());
                }
                break;
            case PAUSED:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    currentState = GameState.PLAYING;
                    removeAll();
                    revalidate();
                    repaint();
                }
                break;
            case GAME_OVER:
            case WIN:
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    resetGame();
                }
                break;
        }
    }

    private void handleKeyRelease(KeyEvent e) {
        if (currentState == GameState.PLAYING) {
            character.keyReleased(e);
        }
    }

    private void resetGame() {
        level = 1;
        score = 0;
        character = new Character();
        loadLevel(level);
        currentState = GameState.PLAYING;
    }

    private void loadLevel(int level) {
        platforms.clear();
        switch (level) {
            case 1:
                platforms.add(new Platform(0, BASE_HEIGHT - 20, BASE_WIDTH, 20));
                platforms.add(new Platform(100, 300, 100, 20));
                platforms.add(new Platform(250, 200, 100, 20));
                platforms.add(new Platform(400, 300, 100, 20));
                platforms.add(new Platform(550, 200, 100, 20));
                platforms.add(new Platform(700, 300, 100, 20));
                platforms.add(new Platform(950, 400, 100, 20));
                platforms.add(new Platform(1150, 300, 100, 20));
                meta = new Meta(1150, 280);
                break;
            case 2:
                platforms.add(new Platform(100, 350, 100, 20));
                platforms.add(new Platform(300, 250, 100, 20));
                platforms.add(new Platform(500, 350, 100, 20));
                platforms.add(new Platform(700, 250, 100, 20));
                platforms.add(new Platform(900, 350, 100, 20));
                platforms.add(new Platform(1100, 250, 100, 20));
                meta = new Meta(1100, 230);
                break;
            default:
                win = true;
                break;
        }
        character.x = 100;
        character.y = 200;
    }

    private void nextLevel() {
        level++;
        loadLevel(level);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateScaleFactors();

        switch (currentState) {
            case MENU:
                drawMenu(g);
                break;
            case PLAYING:
                drawGame(g);
                break;
            case PAUSED:
                drawGame(g);
                drawPauseScreen(g);
                break;
            case GAME_OVER:
                drawGame(g);
                drawGameOver(g);
                break;
            case WIN:
                drawGame(g);
                drawWinScreen(g);
                break;
        }
    }

    private void drawMenu(Graphics g) {
        removeAll();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        add(playButton, gbc);
        add(exitButton, gbc);

        revalidate();
    }

    private void drawGame(Graphics g) {
        character.draw(g);
        for (Platform platform : platforms) {
            platform.draw(g);
        }
        meta.draw(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, Math.round(16 * scaleY)));
        g.drawString("Score: " + score, Math.round(10 * scaleX), Math.round(20 * scaleY));
        g.drawString("Level: " + level, Math.round(10 * scaleX), Math.round(40 * scaleY));
    }

    private void drawPauseScreen(Graphics g) {
        removeAll();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        add(resumeButton, gbc);
        add(mainMenuButton, gbc);

        revalidate();
    }

    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 128));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, Math.round(48 * scaleY)));
        g.drawString("Game Over!", Math.round(BASE_WIDTH/4f * scaleX), Math.round(BASE_HEIGHT/2f * scaleY));
        g.setFont(new Font("Arial", Font.PLAIN, Math.round(24 * scaleY)));
        g.drawString("Press ENTER to Restart", Math.round(BASE_WIDTH/3f * scaleX), Math.round(BASE_HEIGHT/1.5f * scaleY));
    }

    private void drawWinScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 128));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, Math.round(48 * scaleY)));
        g.drawString("You Win!", Math.round(BASE_WIDTH/4f * scaleX), Math.round(BASE_HEIGHT/2f * scaleY));
        g.setFont(new Font("Arial", Font.PLAIN, Math.round(24 * scaleY)));
        g.drawString("Press ENTER to Play Again", Math.round(BASE_WIDTH/3f * scaleX), Math.round(BASE_HEIGHT/1.5f * scaleY));
    }

    class Character {
        private float x, y;
        private float width = 40, height = 40;
        private float xSpeed = 0, ySpeed = 0;
        private boolean isOnFloor = false;
        private boolean hasAirJump = true;
        private final float GRAVITY = 0.5f;
        private final float JUMP_FORCE = -10;
        private final float MOVE_SPEED = 5;
        private boolean jumpKeyWasReleased = true;
        private boolean isJumping = false;
        private boolean jumpRequested = false;
        private boolean isDashing = false;
        private final float DASH_SPEED = 15;
        private final int DASH_DURATION = 10;
        private int dashTimer = 0;
        private final int DASH_COOLDOWN = 50;
        private int dashCooldownTimer = 0;

        public Character() {
            x = 100;
            y = 200;
        }

        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    xSpeed = -MOVE_SPEED;
                    break;
                case KeyEvent.VK_D:
                    xSpeed = MOVE_SPEED;
                    break;
                case KeyEvent.VK_SPACE:
                    if (jumpKeyWasReleased) {
                        jumpRequested = true;
                        jumpKeyWasReleased = false;
                    }
                    break;
                case KeyEvent.VK_SHIFT:
                    dash();
                    break;
            }
        }

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {
                xSpeed = 0;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                jumpKeyWasReleased = true;
                isJumping = false;
            }
        }

        private void jump() {
            if (!isJumping) {
                if (isOnFloor) {
                    ySpeed = JUMP_FORCE;
                    isOnFloor = false;
                    isJumping = true;
                } else if (hasAirJump) {
                    ySpeed = JUMP_FORCE;
                    hasAirJump = false;
                    isJumping = true;
                }
            }
        }

        private void dash() {
            if (!isDashing && dashCooldownTimer == 0) {
                isDashing = true;
                dashTimer = DASH_DURATION;
                ySpeed = 0;
            }
        }

        public boolean intersects(Platform platform) {
            return x < platform.x + platform.width && x + width > platform.x &&
                   y < platform.y + platform.height && y + height > platform.y;
        }

        public boolean intersects(Meta meta) {
            return x < meta.x + meta.width && x + width > meta.x &&
                   y < meta.y + meta.height && y + height > meta.y;
        }

        private void handleCollision(Platform platform) {
            float overlapX = Math.min(x + width - platform.x, platform.x + platform.width - x);
            float overlapY = Math.min(y + height - platform.y, platform.y + platform.height - y);

            if (overlapX < overlapY) {
                if (x < platform.x) {
                    x = platform.x - width;
                } else {
                    x = platform.x + platform.width;
                }
                if (isDashing) {
                    isDashing = false;
                    dashTimer = 0;
                    dashCooldownTimer = DASH_COOLDOWN;
                }
                xSpeed = 0;
            } else {
                if (y < platform.y) {
                    y = platform.y - height;
                    ySpeed = 0;
                    isOnFloor = true;
                    hasAirJump = true;
                } else {
                    y = platform.y + platform.height;
                    ySpeed = 0;
                }
            }
        }

        public void update() {
            if (jumpRequested) {
                jump();
                jumpRequested = false;
            }

            if (isDashing) {
                x += xSpeed > 0 ? DASH_SPEED : (xSpeed < 0 ? -DASH_SPEED : 0);
                dashTimer--;
                if (dashTimer <= 0) {
                    isDashing = false;
                    dashCooldownTimer = DASH_COOLDOWN;
                }
            } else {
                x += xSpeed;
                y += ySpeed;
                ySpeed += GRAVITY;
            }

            if (dashCooldownTimer > 0) {
                dashCooldownTimer--;
            }

            isOnFloor = false;
            for (Platform platform : platforms) {
                if (intersects(platform)) {
                    handleCollision(platform);
                }
            }

            if (y > BASE_HEIGHT) {
                currentState = GameState.GAME_OVER;
            }

            if (intersects(meta)) {
                score += 100;
                nextLevel();
            }

            x = Math.max(0, Math.min(x, BASE_WIDTH - width));
        }

        public void draw(Graphics g) {
            g.setColor(isDashing ? Color.CYAN : Color.GREEN);
            g.fillRect(Math.round(x * scaleX), Math.round(y * scaleY), Math.round(width * scaleX), Math.round(height * scaleY));
            if (dashCooldownTimer > 0) {
                g.setColor(Color.RED);
                g.fillRect(Math.round(x * scaleX), Math.round((y - 10) * scaleY), Math.round((width * dashCooldownTimer / DASH_COOLDOWN) * scaleX), Math.round(5 * scaleY));
            }
        }
    }

    class Platform {
        float x, y, width, height;

        public Platform(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void draw(Graphics g) {
            g.setColor(Color.GRAY);
            g.fillRect(Math.round(x * scaleX), Math.round(y * scaleY),
                       Math.round(width * scaleX), Math.round(height * scaleY));
        }
    }

    class Meta {
        float x, y;
        int width = 20;
        int height = 20;

        public Meta(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void draw(Graphics g) {
            g.setColor(Color.YELLOW);
            g.fillRect(Math.round(x * scaleX), Math.round(y * scaleY),
                       Math.round(width * scaleX), Math.round(height * scaleY));
        }
    }
}


