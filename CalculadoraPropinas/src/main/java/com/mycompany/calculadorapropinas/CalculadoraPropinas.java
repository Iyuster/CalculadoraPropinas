package com.mycompany.calculadorapropinas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class CalculadoraPropinas {
    private JFrame frame;
    private JComboBox<String> mesaSelector;
    private JTextField precioItemField;
    private JTextField porcentajePropinaField;
    private JLabel totalMesaLabel;
    private JLabel propinaTotalLabel;
    private JLabel totalConPropinaLabel;
    private JButton agregarItemButton;
    private JButton finalizarButton;
    private JButton resetButton;

    private double[] totalesMesas;
    private DecimalFormat df;

    private int contadorClicks;
    private long ultimoClickTiempo;

    public CalculadoraPropinas() {
        totalesMesas = new double[4];
        df = new DecimalFormat("#.##");
        contadorClicks = 0;
        ultimoClickTiempo = 0;
        inicializarComponentes();
    }
    
    public void mostrar() {
        frame.setVisible(true);
    }

    private void inicializarComponentes() {
        frame = new JFrame("Calculadora de Propinas");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(8, 2, 10, 10));

        frame.add(new JLabel("Seleccionar Mesa:"));
        mesaSelector = new JComboBox<>(new String[]{"Mesa 1", "Mesa 2", "Mesa 3", "Mesa 4"});
        frame.add(mesaSelector);

        frame.add(new JLabel("Precio del Item:"));
        precioItemField = new JTextField();
        frame.add(precioItemField);

        agregarItemButton = new JButton("Agregar Item");
        agregarItemButton.addActionListener(new AgregarItemListener());
        frame.add(agregarItemButton);
        frame.add(new JLabel()); // Espacio en blanco

        frame.add(new JLabel("Total Mesa:"));
        totalMesaLabel = new JLabel("0.00 €");
        frame.add(totalMesaLabel);

        frame.add(new JLabel("% Propina:"));
        porcentajePropinaField = new JTextField("10");
        frame.add(porcentajePropinaField);

        finalizarButton = new JButton("Finalizar y Calcular");
        finalizarButton.addActionListener(new FinalizarListener());
        frame.add(finalizarButton);

        resetButton = new JButton("Reiniciar Mesa");
        resetButton.addActionListener(new ResetListener());
        frame.add(resetButton);

        frame.add(new JLabel("Propina:"));
        propinaTotalLabel = new JLabel();
        frame.add(propinaTotalLabel);

        frame.add(new JLabel("Total con Propina:"));
        totalConPropinaLabel = new JLabel();
        frame.add(totalConPropinaLabel);
        
        frame.setVisible(true);
    }

    private class AgregarItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int mesaIndex = mesaSelector.getSelectedIndex();
            String precioText = precioItemField.getText();

            if (verificarHuevoDePascua(mesaIndex, precioText)) {
                return; // Si se activó el huevo de Pascua, no continuamos
            }

            if (!precioText.isEmpty()) {
                try {
                    double precioItem = Double.parseDouble(precioText);
                    totalesMesas[mesaIndex] += precioItem;
                    actualizarTotalMesa(mesaIndex);
                    precioItemField.setText("");
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
                int mesaIndex = mesaSelector.getSelectedIndex();
                double totalMesa = totalesMesas[mesaIndex];
                double porcentajePropina = Double.parseDouble(porcentajePropinaField.getText());
                double propina = totalMesa * (porcentajePropina / 100);
                double totalConPropina = totalMesa + propina;

                propinaTotalLabel.setText(df.format(propina) + " €");
                totalConPropinaLabel.setText(df.format(totalConPropina) + " €");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Por favor, ingrese un porcentaje de propina válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ResetListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int mesaIndex = mesaSelector.getSelectedIndex();
            totalesMesas[mesaIndex] = 0;
            actualizarTotalMesa(mesaIndex);
            propinaTotalLabel.setText("");
            totalConPropinaLabel.setText("");
        }
    }

    private void actualizarTotalMesa(int mesaIndex) {
        totalMesaLabel.setText(df.format(totalesMesas[mesaIndex]) + " €");
    }

    private boolean verificarHuevoDePascua(int mesaIndex, String precioText) {
        long tiempoActual = System.currentTimeMillis();
        if (mesaIndex == 3) { // Mesa 4
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