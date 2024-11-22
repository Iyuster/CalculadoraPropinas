package com.mycompany.calculadorapropinas;

/**
 *
 * @author Iago Garcia
 */

import javax.swing.SwingUtilities; //El uso de SwingUtilities ayuda a prevenir diferentes problemas,
                                   //que si tendriamos con otras formas de llamar a las clases  


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculadoraPropinas calculadora = new CalculadoraPropinas();
            calculadora.mostrar();
        });
    }
} 
   
