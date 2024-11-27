package com.mycompany.calculadorapropinas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;

public class Calculadora {
    private JFrame frame;
    private JTextField precioItemField;
    private JTextField porcentajePropinaField;
    private JLabel totalMesaLabel;
    private JLabel propinaTotalLabel;
    private JLabel totalConPropinaLabel;
    private JButton agregarItemButton;
    private JButton finalizarButton;
    private JButton resetButton;
    private JButton volverButton;

    private double[] totalesMesas;  // Array para los totales de cada mesa
    private DecimalFormat df;

    private int contadorClicks;
    private long ultimoClickTiempo;
    private int mesaActual;  // Para saber qué mesa está activa

    private static final String ARCHIVO_TOTALES = "totales.txt";

    public Calculadora() {
        totalesMesas = new double[4];  // Inicialización de los totales para 4 mesas
        df = new DecimalFormat("#.##");
        contadorClicks = 0;
        ultimoClickTiempo = 0;
        mesaActual = -1; // Ninguna mesa seleccionada inicialmente
        cargarTotalesDesdeArchivo();  // Cargamos los totales desde el archivo
        inicializarVentana();
    }

    public void mostrar() {
        frame.setVisible(true);
    }

    // Cargar los totales desde el archivo
    private void cargarTotalesDesdeArchivo() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_TOTALES))) {
            String linea;
            int index = 0;
            while ((linea = br.readLine()) != null && index < totalesMesas.length) {
                totalesMesas[index] = Double.parseDouble(linea);
                index++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de totales no encontrado. Se inicializarán los totales a 0.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Guardar los totales en el archivo
    private void guardarTotalesEnArchivo() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_TOTALES))) {
            for (double total : totalesMesas) {
                pw.println(total);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inicializar la ventana y configuraciones iniciales
    private void inicializarVentana() {
        frame = new JFrame("Calculadora de Propinas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Obtener el tamaño de la pantalla y ajustar el tamaño de la ventana
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int anchoPantalla = (int) pantalla.getWidth();
        int altoPantalla = (int) pantalla.getHeight();
        frame.setSize(anchoPantalla / 2, altoPantalla / 2); // Tamaño adaptado al 50% de la pantalla
        frame.setLocationRelativeTo(null);  // Centra la ventana en la pantalla
        frame.setLayout(new CardLayout());  // Usamos un CardLayout para cambiar entre pantallas

        // Crear el panel para la selección de mesa
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(2, 2, 20, 20));  // 2 filas y 2 columnas con espacio entre botones

        for (int i = 0; i < 4; i++) {
            JButton mesaButton = new JButton("Mesa " + (i + 1));
            int mesaIndex = i;  // Guardamos el índice de la mesa
            mesaButton.addActionListener(e -> abrirCalculadoraParaMesa(mesaIndex));
            menuPanel.add(mesaButton);
        }

        // Crear el panel de la calculadora de propinas
        JPanel calculadoraPanel = new JPanel();
        calculadoraPanel.setLayout(new GridLayout(9, 2, 10, 10));

        calculadoraPanel.add(new JLabel("Precio del Item:"));
        precioItemField = new JTextField();
        calculadoraPanel.add(precioItemField);

        agregarItemButton = new JButton("Agregar Item");
        agregarItemButton.addActionListener(new AgregarItemListener());
        calculadoraPanel.add(agregarItemButton);
        calculadoraPanel.add(new JLabel()); // Espacio en blanco

        calculadoraPanel.add(new JLabel("Total Mesa:"));
        totalMesaLabel = new JLabel("0.00 €");
        calculadoraPanel.add(totalMesaLabel);

        calculadoraPanel.add(new JLabel("% Propina:"));
        porcentajePropinaField = new JTextField("10");
        calculadoraPanel.add(porcentajePropinaField);

        finalizarButton = new JButton("Finalizar y Calcular");
        finalizarButton.addActionListener(new FinalizarListener());
        calculadoraPanel.add(finalizarButton);

        resetButton = new JButton("Reiniciar Mesa");
        resetButton.addActionListener(new ResetListener());
        calculadoraPanel.add(resetButton);

        calculadoraPanel.add(new JLabel("Propina:"));
        propinaTotalLabel = new JLabel();
        calculadoraPanel.add(propinaTotalLabel);

        calculadoraPanel.add(new JLabel("Total con Propina:"));
        totalConPropinaLabel = new JLabel();
        calculadoraPanel.add(totalConPropinaLabel);

        volverButton = new JButton("Volver al Menú Inicial");
        volverButton.addActionListener(new VolverAlMenuListener());
        calculadoraPanel.add(volverButton);

        // Añadir los paneles al CardLayout
        frame.add(menuPanel, "Menu");
        frame.add(calculadoraPanel, "Calculadora");

        frame.setVisible(true);
    }

    private void abrirCalculadoraParaMesa(int mesaIndex) {
        mesaActual = mesaIndex;  // Establecemos la mesa actual
        JPanel calculadoraPanel = (JPanel) frame.getContentPane().getComponent(1);
        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), "Calculadora");
        actualizarTotalMesa(mesaIndex);  // Actualizar el total de la mesa seleccionada
    }

    private class AgregarItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String precioText = precioItemField.getText();
            if (verificarHuevoDePascua(mesaActual, precioText)) {
                return;
            }
            if (!precioText.isEmpty()) {
                try {
                    double precioItem = Double.parseDouble(precioText);
                    if (precioItem < 0) {
                        JOptionPane.showMessageDialog(frame, "El precio no puede ser negativo.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    totalesMesas[mesaActual] += precioItem;  // Actualizamos el total de la mesa seleccionada
                    actualizarTotalMesa(mesaActual);
                    guardarTotalesEnArchivo();  // Guardar los datos tras cada cambio
                    precioItemField.setText("");  // Limpiar el campo de entrada
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Por favor, ingrese un precio válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class FinalizarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double totalMesa = totalesMesas[mesaActual];
                double porcentajePropina = Double.parseDouble(porcentajePropinaField.getText());
                double propina = totalMesa * (porcentajePropina / 100);
                double totalConPropina = totalMesa + propina;

                propinaTotalLabel.setText(df.format(propina) + " €");
                totalConPropinaLabel.setText(df.format(totalConPropina) + " €");

                totalesMesas[mesaActual] = 0;  // Reiniciar la mesa tras finalizar
                actualizarTotalMesa(mesaActual);
                guardarTotalesEnArchivo();  // Guardar el reinicio
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Por favor, ingrese un porcentaje de propina válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ResetListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            totalesMesas[mesaActual] = 0;  // Reiniciar el total de la mesa seleccionada
            actualizarTotalMesa(mesaActual);
            guardarTotalesEnArchivo();  // Guardar el reinicio
        }
    }

    private class VolverAlMenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
            cl.show(frame.getContentPane(), "Menu");
        }
    }

    private void actualizarTotalMesa(int mesaIndex) {
        totalMesaLabel.setText(df.format(totalesMesas[mesaIndex]) + " €");
    }

    private boolean verificarHuevoDePascua(int mesaIndex, String precioText) {
        long tiempoActual = System.currentTimeMillis();
        if (mesaIndex == 3) {  // Mesa 4
            if (tiempoActual - ultimoClickTiempo < 2000) {
                contadorClicks++;
                if (contadorClicks == 3) {
                    SwingUtilities.invokeLater(() -> {
                        new Juego(); // Abre el juego en una nueva ventana
                    });
                    contadorClicks = 0;
                    return true;
                }
            } else {
                contadorClicks = 1;
            }
            ultimoClickTiempo = tiempoActual;
        } else {
            contadorClicks = 0;
        }

        return false;
    }
}
