package principal;

import controlador.Controlador;
import vista.VentanaPrincipal;

/**
 * Punto de entrada de la aplicación: crea la ventana y el controlador, los
 * conecta y muestra la ventana. No tiene lógica de la simulación.
 */
public class Main {

    public static void main(String[] args) {
        // look and feel Nimbus (si no está, se queda con el de por defecto)
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName())
                    .log(java.util.logging.Level.WARNING, "No se pudo aplicar Nimbus", ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VentanaPrincipal ventana = new VentanaPrincipal();
                Controlador controlador = new Controlador();
                controlador.setVista(ventana);
                ventana.setControlador(controlador);
                controlador.iniciar();
                ventana.setVisible(true);
            }
        });
    }
}
