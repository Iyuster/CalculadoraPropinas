package com.mycompany.calculadorapropinas;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import javax.swing.border.EmptyBorder;

public class Calculadora {
    // Variables principales de la aplicación
    private JFrame frame;  // Ventana principal
    private JTextField precioItemField;  // Campo para ingresar el precio del item
    private JTextField porcentajePropinaField;  // Campo para ingresar el porcentaje de propina
    private JLabel totalMesaLabel;  // Etiqueta para mostrar el total de la mesa
    private JLabel propinaTotalLabel;  // Etiqueta para mostrar la propina calculada
    private JLabel totalConPropinaLabel;  // Etiqueta para mostrar el total con propina
    private double[] totalesMesas;  // Array que almacena los totales de las mesas
    private DecimalFormat df;  // Formateador de números para mostrar solo dos decimales
    private int mesaActual;  // Índice de la mesa actualmente seleccionada
    private static final String ARCHIVO_TOTALES = "totales.txt";  // Nombre del archivo que almacena los totales

    // Variables para el huevo de pascua
    private int contadorClicks = 0;
    private long ultimoClickTiempo = 0;

    // Constructor: Inicializa las variables, carga los datos y configura la ventana
    public Calculadora() {
        totalesMesas = new double[4];  // Inicialización de 4 mesas
        df = new DecimalFormat("#.##");  // Formato para mostrar 2 decimales
        mesaActual = -1;  // No hay mesa seleccionada inicialmente
        cargarTotalesDesdeArchivo();  // Cargar datos de archivo si existen
        inicializarVentana();  // Configurar la interfaz gráfica
    }

    // Muestra la ventana
    public void mostrar() {
        frame.setVisible(true);
    }

    // Carga los totales desde un archivo para persistencia de datos
    private void cargarTotalesDesdeArchivo() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_TOTALES))) {
            String linea;
            int index = 0;
            while ((linea = br.readLine()) != null && index < totalesMesas.length) {
                totalesMesas[index] = Double.parseDouble(linea);  // Convertir la línea a double y asignarla
                index++;
            }
        } catch (IOException e) {
            System.out.println("Archivo de totales no encontrado. Inicializando a 0.");
        }
    }

    // Guarda los totales en un archivo para persistencia de datos
    private void guardarTotalesEnArchivo() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_TOTALES))) {
            for (double total : totalesMesas) {
                pw.println(total);  // Guardar cada total en una nueva línea
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Configura la ventana principal y los componentes visuales
    private void inicializarVentana() {
        frame = new JFrame("Calculadora de Propinas");  // Título de la ventana
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Cierra el programa al cerrar la ventana
        frame.setSize(800, 600);  // Dimensiones iniciales
        frame.setLocationRelativeTo(null);  // Centra la ventana en la pantalla
        frame.setLayout(new CardLayout());  // Permite cambiar entre pantallas (menú y calculadora)

        // Crear el panel para la selección de mesa
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(2, 2, 20, 20));  // 2 filas, 2 columnas con espacio entre botones

        // Añadir margen externo al panel de botones
        menuPanel.setBorder(new EmptyBorder(20, 20, 20, 20));  // Márgenes externos de 20 píxeles alrededor del panel

        for (int i = 0; i < 4; i++) {
            int mesaIndex = i;  
            JButton mesaButton = new JButton("Mesa " + (i + 1));  

            mesaButton.setBackground(new Color(100, 149, 237));  
            mesaButton.setForeground(Color.WHITE);  
            mesaButton.setFont(new Font("Arial", Font.BOLD, 16));  
            mesaButton.setMargin(new Insets(20, 20, 20, 20));  

            mesaButton.addActionListener(e -> abrirCalculadoraParaMesa(mesaIndex));  
            menuPanel.add(mesaButton);  
        }

        // Panel para la calculadora de propinas
        JPanel calculadoraPanel = new JPanel(new GridBagLayout());  // Usamos GridBagLayout para mayor control
        GridBagConstraints gbc = new GridBagConstraints();  // Configuración de los componentes
        gbc.insets = new Insets(10, 10, 10, 10);  // Márgenes entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Componente ocupa todo el ancho

        // Añade los componentes con su configuración específica
        gbc.gridx = 0; gbc.gridy = 0;
        calculadoraPanel.add(new JLabel("Precio del Item:"), gbc);
        precioItemField = new JTextField();
        gbc.gridx = 1;
        calculadoraPanel.add(precioItemField, gbc);

        JButton agregarItemButton = new JButton("Agregar Item");
        agregarItemButton.addActionListener(e -> agregarItem());  // Acción con Lambda
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        calculadoraPanel.add(agregarItemButton, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        calculadoraPanel.add(new JLabel("Total Mesa:"), gbc);
        totalMesaLabel = new JLabel("0.00 €");
        gbc.gridx = 1;
        calculadoraPanel.add(totalMesaLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        calculadoraPanel.add(new JLabel("% Propina:"), gbc);
        porcentajePropinaField = new JTextField("10");  // Valor predeterminado
        gbc.gridx = 1;
        calculadoraPanel.add(porcentajePropinaField, gbc);

        JButton finalizarButton = new JButton("Finalizar y Calcular");
        finalizarButton.addActionListener(e -> finalizarCalculo());  // Acción con Lambda
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        calculadoraPanel.add(finalizarButton, gbc);

        JButton resetButton = new JButton("Reiniciar Mesa");
        resetButton.addActionListener(e -> reiniciarMesa());  // Acción con Lambda
        gbc.gridy = 5;
        calculadoraPanel.add(resetButton, gbc);

        gbc.gridy = 6; gbc.gridx = 0;
        calculadoraPanel.add(new JLabel("Propina:"), gbc);
        propinaTotalLabel = new JLabel();
        gbc.gridx = 1;
        calculadoraPanel.add(propinaTotalLabel, gbc);

        gbc.gridy = 7; gbc.gridx = 0;
        calculadoraPanel.add(new JLabel("Total con Propina:"), gbc);
        totalConPropinaLabel = new JLabel();
        gbc.gridx = 1;
        calculadoraPanel.add(totalConPropinaLabel, gbc);

        JButton volverButton = new JButton("Volver al Menú Inicial");
        volverButton.addActionListener(e -> volverAlMenu());  // Acción con Lambda
        gbc.gridy = 8; gbc.gridx = 0;
        gbc.gridwidth = 2;
        calculadoraPanel.add(volverButton, gbc);

        // Añadir ambos paneles al CardLayout
        frame.add(menuPanel, "Menu");
        frame.add(calculadoraPanel, "Calculadora");
        frame.setVisible(true);  // Mostrar la ventana
    }

    // Cambia a la calculadora y carga la mesa seleccionada
    private void abrirCalculadoraParaMesa(int mesaIndex) {
        mesaActual = mesaIndex;  // Guarda la mesa seleccionada
        ((CardLayout) frame.getContentPane().getLayout()).show(frame.getContentPane(), "Calculadora");
        actualizarTotalMesa(mesaIndex);  // Actualiza el total mostrado
    }

    // Agrega un item al total de la mesa
    private void agregarItem() {
        try {
            double precioItem = Double.parseDouble(precioItemField.getText());

            // Verificación para el huevo de pascua: si el precio es 1741
            if (precioItem == 1741) {
                // Activar el huevo de pascua y abrir el juego
                SwingUtilities.invokeLater(() -> {
                    JFrame frame = new JFrame("PirateWars");
                    Juego juego = new Juego();
                    frame.add(juego);
                    frame.setSize(800, 700);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setVisible(true);
                });
            } else {
                // Si el precio no es 1741, agregarlo al total de la mesa
                totalesMesas[mesaActual] += precioItem;
                actualizarTotalMesa(mesaActual);  // Actualiza el total de la mesa
            }

            precioItemField.setText("");  // Limpiar campo de texto
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Precio inválido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Finaliza el cálculo de la propina
    private void finalizarCalculo() {
        try {
            double porcentaje = Double.parseDouble(porcentajePropinaField.getText());  // Leer porcentaje
            double propina = (totalesMesas[mesaActual] * porcentaje) / 100;
            double totalConPropina = totalesMesas[mesaActual] + propina;

            propinaTotalLabel.setText(df.format(propina) + " €");
            totalConPropinaLabel.setText(df.format(totalConPropina) + " €");
            totalesMesas[mesaActual] = 0;  // Reinicia el total de la mesa
            actualizarTotalMesa(mesaActual);
            guardarTotalesEnArchivo();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Porcentaje inválido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Reinicia la mesa a 0
    private void reiniciarMesa() {
        totalesMesas[mesaActual] = 0;
        actualizarTotalMesa(mesaActual);
        guardarTotalesEnArchivo();
    }

    // Regresa al menú principal
    private void volverAlMenu() {
        ((CardLayout) frame.getContentPane().getLayout()).show(frame.getContentPane(), "Menu");
    }

    // Actualiza el total mostrado para la mesa actual
    private void actualizarTotalMesa(int mesaIndex) {
        totalMesaLabel.setText(df.format(totalesMesas[mesaIndex]) + " €");
    }
}

