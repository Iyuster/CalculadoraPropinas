package com.mycompany.calculadorapropinas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Random;

public class Juego extends JPanel {
    private BarcoPirata barcoPirata;
    private ArrayList<Disparo> disparos;
    private ArrayList<DisparoEnemigo> disparosEnemigos;
    private ArrayList<BarcoEnemigo> barcosEnemigos;
    private int puntos;
    private boolean juegoTerminado;
    private boolean juegoTerminado2;
    private double velocidad = 1;
    private Image pirateShipImage;
    private Image enemyShipImage;
    private Image projectileImage;
    private Image BackgroundImage;
    private Image LoseImage;
    private boolean enPantallaTitulo = true;
    private Timer enemyTimer;
    private Clip backgroundMusic;
    
    private JButton startButton;
    private JButton exitButton;
    private JButton restartButton;
    private JButton menuButton;

    public Juego() {
        enPantallaTitulo = true;
        
        ImageIcon pirateIcon = new ImageIcon("images/pirate_ship.png");
        ImageIcon enemyIcon = new ImageIcon("images/enemy_ship.png");
        ImageIcon projectileIcon = new ImageIcon("images/cannonball.png");
        BackgroundImage = new ImageIcon("images/background.gif").getImage();
        LoseImage = new ImageIcon("images/lost.jpg").getImage();

        pirateShipImage = pirateIcon.getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);
        enemyShipImage = enemyIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        projectileImage = projectileIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);

        barcoPirata = new BarcoPirata();
        disparos = new ArrayList<>();
        barcosEnemigos = new ArrayList<>();
        puntos = 0;
        juegoTerminado = false;
        juegoTerminado2 = false;

        setLayout(null);

        startButton = new JButton("Iniciar Juego");
        exitButton = new JButton("Salir");
        restartButton = new JButton("Reiniciar");
        menuButton = new JButton("Volver al Menú");

        startButton.addActionListener(e -> iniciarJuego());
        exitButton.addActionListener(e -> System.exit(0));
        restartButton.addActionListener(e -> reiniciarJuego());
        menuButton.addActionListener(e -> volverAPantallaTitulo());

        startButton.setBounds(300, 300, 200, 50);
        exitButton.setBounds(300, 370, 200, 50);
        restartButton.setBounds(300, 300, 200, 50);
        menuButton.setBounds(300, 370, 200, 50);

        add(startButton);
        add(exitButton);
        add(restartButton);
        add(menuButton);
        
        Timer gameTimer = new Timer(10, e -> repaint());
            gameTimer.start();

        mostrarBotonesMenuPrincipal();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!enPantallaTitulo && !juegoTerminado && !juegoTerminado2) {
                    barcoPirata.keyPressed(e);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!enPantallaTitulo && !juegoTerminado && !juegoTerminado2) {
                    barcoPirata.keyReleased(e);
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    private void mostrarBotonesMenuPrincipal() {
        startButton.setVisible(true);
        exitButton.setVisible(true);
        restartButton.setVisible(false);
        menuButton.setVisible(false);
    }

    private void mostrarBotonesFinJuego() {
        startButton.setVisible(false);
        exitButton.setVisible(false);
        restartButton.setVisible(true);
        menuButton.setVisible(true);
    }

    private void ocultarTodosBotones() {
        startButton.setVisible(false);
        exitButton.setVisible(false);
        restartButton.setVisible(false);
        menuButton.setVisible(false);
    }

    private void iniciarJuego() {
        enPantallaTitulo = false;
        barcoPirata = new BarcoPirata();
        disparos = new ArrayList<>();
        barcosEnemigos = new ArrayList<>();
        disparosEnemigos = new ArrayList<>();
        puntos = 0;
        juegoTerminado = false;
        juegoTerminado2 = false;
        velocidad = 1;

        ocultarTodosBotones();

        if (enemyTimer != null) {
            enemyTimer.stop();
        }
        enemyTimer = new Timer(1500, e -> {
            if (!enPantallaTitulo && !juegoTerminado && !juegoTerminado2) {
                int numBoats = (int) (Math.random() * 3) + 1;
                for (int i = 0; i < numBoats; i++) {
                    int x = (int) (Math.random() * (getWidth() - 40));
                    barcosEnemigos.add(new BarcoEnemigo(x, 0));
                }
            }
        });
        enemyTimer.start();

        playBackgroundMusic();
        requestFocusInWindow();
    }

    private void volverAPantallaTitulo() {
        enPantallaTitulo = true;
        barcoPirata = null;
        disparos = new ArrayList<>();
        barcosEnemigos = new ArrayList<>();
        puntos = 0;
        juegoTerminado = false;
        juegoTerminado2 = false;
        if (enemyTimer != null) {
            enemyTimer.stop();
        }
        mostrarBotonesMenuPrincipal();
        repaint();
        stopBackgroundMusic();
    }

    // Método para reiniciar el juego
    public void reiniciarJuego() {
        iniciarJuego(); // Volver a iniciar el juego
        repaint(); // Redibujar la pantalla
    }
    
    
    private void playBackgroundMusic() {
        try {
            InputStream audioSrc = getClass().getResourceAsStream("/Soundtrack.wav");
            if (audioSrc == null) {
                System.out.println("Could not find audio file");
                return;
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(bufferedIn);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInput);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
}
    
private void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(BackgroundImage, 0, 0, getWidth(), getHeight(), this);

        if (enPantallaTitulo) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("Pirate Wars", getWidth() / 2 - 120, getHeight() / 2 - 50);
        }  else if (juegoTerminado) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawImage(LoseImage, 0, 0, getWidth(), getHeight(), this);
    
            // Centrar el texto
            String mensajeGanado = "¡Has ganado!";
            FontMetrics fm = g.getFontMetrics();
            int mensajeWidth = fm.stringWidth(mensajeGanado);
            int mensajeX = (getWidth() - mensajeWidth) / 2; // Centrado horizontalmente
            int mensajeY = getHeight() / 2 - 50; // Ajusta este valor para mover el mensaje hacia arriba

            g.drawString(mensajeGanado, mensajeX, mensajeY);
    
            mostrarBotonesFinJuego();
            stopBackgroundMusic();
        } else if (juegoTerminado2) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawImage(LoseImage, 0, 0, getWidth(), getHeight(), this);
            
            // Centrar el texto
            String mensajePerdido = "¡Has perdido!";
            FontMetrics fm = g.getFontMetrics();
            int mensajeWidth = fm.stringWidth(mensajePerdido);
            int mensajeX = (getWidth() - mensajeWidth) / 2; // Centrado horizontalmente
            int mensajeY = getHeight() / 2 - 50; // Ajusta este valor para mover el mensaje hacia arriba

            g.drawString(mensajePerdido, mensajeX, mensajeY);
    
            mostrarBotonesFinJuego();
            stopBackgroundMusic();
        } else {
            if (barcoPirata != null) {
                barcoPirata.dibujar(g);
                barcoPirata.dibujarHitbox(g);
            }
            for (Disparo disparo : disparos) {
                disparo.mover();
                disparo.dibujar(g);
            }
            ArrayList<Disparo> disparosRemover = new ArrayList<>();
            ArrayList<BarcoEnemigo> barcosEnemigosRemover = new ArrayList<>();
            for (BarcoEnemigo enemigo : barcosEnemigos) {
                enemigo.mover();
                enemigo.actualizarDireccion(getWidth());
                enemigo.dibujar(g);
                for (Disparo disparo : disparos) {
                    if (disparo.intersecta(enemigo)) {
                        disparosRemover.add(disparo);
                        barcosEnemigosRemover.add(enemigo);
                        puntos += 10;
                    }
                }
                if (barcoPirata.intersecta2(enemigo)) {
                    juegoTerminado2 = true;
                }
                if (enemigo.getY() > getHeight()) {
                    barcosEnemigosRemover.add(enemigo);
                    puntos -= 5;
                }
            }
            disparos.removeAll(disparosRemover);
            barcosEnemigos.removeAll(barcosEnemigosRemover);

            ArrayList<DisparoEnemigo> disparosEnemigosRemover = new ArrayList<>();
            for (BarcoEnemigo enemigo : barcosEnemigos) {
                if (enemigo.puedeDisparar()) {
                    disparosEnemigos.add(enemigo.disparar());
                }
            }
            for (DisparoEnemigo disparoEnemigo : disparosEnemigos) {
                disparoEnemigo.mover();
                disparoEnemigo.dibujar(g);
                if (disparoEnemigo.fueraDePantalla(getHeight())) {
                    disparosEnemigosRemover.add(disparoEnemigo);
                } else if (barcoPirata.intersecta(disparoEnemigo)) {
                    juegoTerminado2 = true;
                    disparosEnemigosRemover.add(disparoEnemigo);
                }
            }
            disparosEnemigos.removeAll(disparosEnemigosRemover);

            g.setColor(Color.BLUE);
            g.drawString("Puntos: " + puntos, 10, 20);

            if (puntos >= 500) {
                juegoTerminado = true;
            }
            if (puntos < 0) {
                juegoTerminado2 = true;
            }
        }
    }
    
    // Clase para el barco pirata
    class BarcoPirata {
        private int x, y;
        private boolean izquierda, derecha, arriba, abajo;
        private long ultimoDisparo;
        private final long TIEMPO_RECARGA = 300;
        private double rotationAngle = 0;
        private final int velocidad = 2;

        public BarcoPirata() {
            x = 175;
            y = 600;
            izquierda = derecha = arriba = abajo = false;
            ultimoDisparo = 0;
        }

        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A: izquierda = true; break;
                case KeyEvent.VK_D: derecha = true; break;
                case KeyEvent.VK_W: arriba = true; break;
                case KeyEvent.VK_S: abajo = true; break;
                case KeyEvent.VK_SPACE:
                    long tiempoActual = System.currentTimeMillis();
                    if (tiempoActual - ultimoDisparo >= TIEMPO_RECARGA) {
                        disparos.add(new Disparo(x + pirateShipImage.getWidth(null) / 2, y));
                        ultimoDisparo = tiempoActual;
                    }
                    break;
            }
        }

        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A: izquierda = false; break;
                case KeyEvent.VK_D: derecha = false; break;
                case KeyEvent.VK_W: arriba = false; break;
                case KeyEvent.VK_S: abajo = false; break;
            }
        }

        public void dibujar(Graphics g) {
            mover();
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.translate(x + pirateShipImage.getWidth(null) / 2, y + pirateShipImage.getHeight(null) / 2);
            g2d.rotate(rotationAngle);
            g2d.drawImage(pirateShipImage, -pirateShipImage.getWidth(null) / 2, -pirateShipImage.getHeight(null) / 2, null);
            g2d.dispose();
        }

        private void mover() {
            int dx = 0, dy = 0;
            if (izquierda) dx -= velocidad;
            if (derecha) dx += velocidad;
            if (arriba) dy -= velocidad;
            if (abajo) dy += velocidad;

            if (dx != 0 || dy != 0) {
                rotationAngle = Math.atan2(dy, dx);
                x += dx;
                y += dy;
                x = Math.max(0, Math.min(x, getWidth() - pirateShipImage.getWidth(null)));
                y = Math.max(0, Math.min(y, getHeight() - pirateShipImage.getHeight(null)));
            }
        }

        public boolean intersecta2(BarcoEnemigo enemigo) {
            // Crear una transformación rotacional basada en el ángulo de rotación del barco
            AffineTransform at = AffineTransform.getRotateInstance(rotationAngle, x + pirateShipImage.getWidth(null) / 2, y + pirateShipImage.getHeight(null) / 2);
    
            // Crear una forma rotada a partir de la imagen del barco
            Shape rotatedShip = at.createTransformedShape(new Rectangle(x, y, pirateShipImage.getWidth(null), pirateShipImage.getHeight(null)));
    
            // Definir el área del enemigo
            Rectangle areaEnemigo = new Rectangle(enemigo.getX(), enemigo.getY(), 30, 30); // Ajusta el tamaño según sea necesario
    
            // Comprobar si hay intersección
            return rotatedShip.intersects(areaEnemigo);
        }

        public boolean intersecta(DisparoEnemigo disparo) {
            // Crear una transformación rotacional basada en el ángulo de rotación del barco
            AffineTransform at = AffineTransform.getRotateInstance(rotationAngle, x + pirateShipImage.getWidth(null) / 2, y + pirateShipImage.getHeight(null) / 2);
    
            // Crear una forma rotada a partir de la imagen del barco
            Shape rotatedShip = at.createTransformedShape(new Rectangle(x, y, pirateShipImage.getWidth(null), pirateShipImage.getHeight(null)));
    
            // Comprobar si hay intersección con el disparo enemigo
            return rotatedShip.intersects(disparo.getBounds());
        }

         // Add this method for debugging
        public void dibujarHitbox(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.RED);
            AffineTransform at = AffineTransform.getRotateInstance(rotationAngle, x + pirateShipImage.getWidth(null) / 2, y + pirateShipImage.getHeight(null) / 2);
            //g2d.draw(rotatedShip);
            g2d.dispose();
        }
    }

        // Clase para los barcos enemigos
    class BarcoEnemigo {
            private int x, y; // Posiciones del enemigo
            private int direccion; // Dirección de movimiento
            private static final Random random = new Random(); // Generador de números aleatorios
            private long ultimoDisparo;
            private final long TIEMPO_RECARGA = 2000;

            public BarcoEnemigo(int x, int y) {
                this.x = x; // Posición inicial en X
                this.y = y; // Posición inicial en Y
                this.direccion = random.nextBoolean() ? 1 : -1; // Direccion aleatoria
                this.ultimoDisparo = System.currentTimeMillis();
            }

            public void mover() {
                y += 1; // Mover hacia abajo
                x += direccion; // Mover en dirección horizontal
            }

            public void actualizarDireccion(int anchoPantalla) {
                // Cambiar dirección si el barco enemigo alcanza los bordes
                if (x <= 0) {
                    x = 0; // Ajustar posición
                    direccion = 1; // Cambiar dirección
                } else if (x >= anchoPantalla - 80) {
                    x = anchoPantalla - 80; // Ajustar posición
                    direccion = -1; // Cambiar dirección
                }
            }

            public int getX() {
                return x; // Retornar posición X
            }

            public int getY() {
            return y; // Retornar posición Y
            }
    
             public void dibujar(Graphics g) {
                // Dibujar barco enemigo
                if (!enPantallaTitulo) {
                    g.drawImage(enemyShipImage, x, y, null);
                }       
        }
            public boolean puedeDisparar() {
                long tiempoActual = System.currentTimeMillis();
                if (tiempoActual - ultimoDisparo >= TIEMPO_RECARGA) {
                    ultimoDisparo = tiempoActual;
                    return true;
        }
        return false;
    }

    public DisparoEnemigo disparar() {
        return new DisparoEnemigo(x + enemyShipImage.getWidth(null) / 2, y + enemyShipImage.getHeight(null));
    }
    }
    class DisparoEnemigo {
        private int x, y;

        public DisparoEnemigo(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void mover() {
            y += 3; // Enemy shots move downwards faster than the ship
        }

        public void dibujar(Graphics g) {
            g.setColor(Color.RED); // Different color for enemy shots
            g.fillOval(x, y, 8, 8); // Smaller size for enemy shots
        }

        public int getX() { return x; }
        public int getY() { return y; }

        public boolean fueraDePantalla(int alturaPantalla) {
            return y > alturaPantalla;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, 12, 12);
        }
    }

    // Clase para los disparos
    class Disparo {
        private int x, y; // Posiciones del disparo

        public Disparo(int x, int y) {
            this.x = x; // Posición inicial en X
            this.y = y; // Posición inicial en Y
        }

        public void mover() {
            y -= 3; // Mover hacia arriba
        }

        public void dibujar(Graphics g) {
            g.drawImage(projectileImage, x, y, null); // Dibujar proyectil
        }

        public boolean intersecta(BarcoEnemigo enemigo) {
            int shrinkFactor = 5;
            Rectangle r1 = new Rectangle(x, y, projectileImage.getWidth(null), projectileImage.getHeight(null));
            Rectangle r2 = new Rectangle(
                enemigo.getX() + shrinkFactor,
                enemigo.getY() + shrinkFactor,
                enemyShipImage.getWidth(null) - 2 * shrinkFactor,
                enemyShipImage.getHeight(null) - 2 * shrinkFactor
            ); // Área del enemigo ajustada
            return r1.intersects(r2); // Retornar si hay intersección
        }
    }    
}
