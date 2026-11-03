package controlador;

import java.util.List;

/**
 * Lo que el controlador necesita que sepa hacer la vista. Es una interfaz para
 * que el controlador no dependa de ninguna clase de Swing: la ventana la
 * implementa y el controlador solo le manda datos ya listos para mostrar.
 */
public interface VistaSimulador {

    // ---- navegación entre pantallas ----
    void mostrarConfiguracion();

    void mostrarSimulacion();

    void mostrarReporte();

    // ---- mensajes ----
    // error de validación del formulario de configuración (queda escrito en la pantalla)
    void mostrarErrorConfiguracion(String mensaje);

    void limpiarErrorConfiguracion();

    // error general (ventana de aviso)
    void mostrarError(String mensaje);

    // confirmación o estado (queda escrito en la barra de estado de la simulación)
    void mostrarMensaje(String mensaje);

    // ---- pantalla de simulación ----
    // estado vacío: todavía no hay una simulación en curso
    void mostrarSinSimulacion();

    void mostrarContadores(int turno, int turnosTotales, String clima, int plantas,
            int conejos, int lobos);

    // cada fila: tipo, nombre, energía, edad, detalle
    void mostrarEntidades(Object[][] filas);

    void mostrarEventos(int turno, List<String> eventos);

    void actualizarIntervencion(boolean disponible, String estado, int lobosDisponibles);

    void habilitarAvance(boolean habilitado);

    void habilitarVerReporte(boolean habilitado);

    // ---- reporte final ----
    void mostrarTextoReporte(String causaFin, String texto);

    void mostrarReporteVacio();
}
