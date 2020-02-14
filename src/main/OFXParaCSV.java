/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import view.TelaConversora;

/**
 *
 * @author Christian
 */
public class OFXParaCSV {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(new WindowsLookAndFeel());

            // WebLookAndFeel.install();
            //UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
        } catch (Exception e) {
            System.out.println("Erro ao carregar um layout.");
            JOptionPane.showMessageDialog(null, "Erro ao carregar um layout.");
        }
        TelaConversora tela = new TelaConversora();
        tela.setLocationRelativeTo(null);
        tela.setVisible(true);

    }

}
