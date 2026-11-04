package controlador;


import dao.EcosistemaDAO;
import java.util.ArrayList;
import java.util.List;
import modelo.Clima;
import modelo.Conejo;
import modelo.Ecosistema;
import modelo.Entidad;
import modelo.Lobo;
import modelo.Planta;
import modelo.PlantaVenenosa;

/**
 * Recibe las acciones de la vista, valida lo que ingresó la persona, le pide
 * cosas al modelo y le devuelve a la vista los datos ya listos para mostrar.
 * No tiene nada de Swing.
 */
public class Controlador {

    // rangos de la configuración inicial (los mismos de la primera instancia)
    private static final int MIN_PLANTAS = 5;
    private static final int MAX_PLANTAS = 30;
    private static final int MIN_CONEJOS = 2;
    private static final int MAX_CONEJOS = 15;
    private static final int MIN_LOBOS = 1;
    private static final int MAX_LOBOS = 5;
    private static final int MIN_TURNOS = 10;
    private static final int MAX_TURNOS = 50;
    private static final int MIN_ENERGIA = 10;
    private static final int MAX_ENERGIA = 100;

    private Ecosistema eco;
    private VistaSimulador vista;
    // el unico que habla con el archivo es el DAO, y solo lo usa el controlador
    private EcosistemaDAO dao;
    // en cada turno de intervención se puede hacer una sola cosa (igual que en la consola)
    private boolean intervencionUsada;

    public void setVista(VistaSimulador vista) {
        this.vista = vista;
    }

      public void setDao(EcosistemaDAO dao) {
        this.dao = dao;
    }

    // al abrir la aplicación: si hay una simulación guardada la retoma,
    // si no, queda la configuración para empezar una nueva
    public void iniciar() {
        reiniciarPantallas();
        if (!dao.existeArchivo()) {
            return;
        }
        Ecosistema guardado = dao.cargar();   // → DAO: leé el archivo
        if (guardado == null) {               // ← el archivo tenía datos inválidos
            vista.mostrarError("La simulación guardada tiene datos inválidos y no se pudo "
                    + "retomar. Configurá una nueva.");
            return;
        }
        eco = guardado;
        actualizarVista();
        vista.mostrarEventos(eco.getTurnoActual(), new ArrayList<String>());
        vista.mostrarSimulacion();
        vista.mostrarMensaje("Se retomó la simulación guardada en el turno "
                + eco.getTurnoActual() + " de " + eco.getTurnosTotales() + ".");
    }

    // deja la aplicación en su estado inicial: se ve la configuración y no hay simulación
    private void reiniciarPantallas() {
        eco = null;
        intervencionUsada = false;
        vista.mostrarSinSimulacion();
        vista.mostrarReporteVacio();
        vista.limpiarErrorConfiguracion();
        vista.mostrarConfiguracion();
    }
    // ---------------------------------------------------------------
    // Configuración
    // ---------------------------------------------------------------

    public void configurarSimulacion(String textoPlantas, String textoConejos,
            String textoLobos, String nombreClima, String textoTurnos) {
        int plantas;
        int conejos;
        int lobos;
        int turnos;
        Clima clima;
        try {
            plantas = leerEntero(textoPlantas, "Cantidad de plantas", MIN_PLANTAS, MAX_PLANTAS);
            conejos = leerEntero(textoConejos, "Cantidad de conejos", MIN_CONEJOS, MAX_CONEJOS);
            lobos = leerEntero(textoLobos, "Cantidad de lobos", MIN_LOBOS, MAX_LOBOS);
            turnos = leerEntero(textoTurnos, "Cantidad de turnos", MIN_TURNOS, MAX_TURNOS);
            clima = buscarClima(nombreClima);
        } catch (IllegalArgumentException e) {
            vista.mostrarErrorConfiguracion(e.getMessage());
            return;
        }

        eco = new Ecosistema(plantas, conejos, lobos, clima, turnos);
        intervencionUsada = false;
        guardarCambios();

        vista.limpiarErrorConfiguracion();
        vista.mostrarReporteVacio();
        actualizarVista();
        vista.mostrarEventos(0, new ArrayList<String>());
        vista.mostrarSimulacion();
        vista.mostrarMensaje("Simulación creada: " + plantas + " plantas, " + conejos
                + " conejos, " + lobos + " lobos, clima " + clima.getNombre() + ", " + turnos
                + " turnos. Presioná \"Avanzar turno\" para empezar.");
    }

    // pide confirmación en la vista solo si hay una simulación sin terminar
    public boolean hayPartidaEnCurso() {
        return eco != null && !eco.simulacionTerminada();
    }

    // la confirmación la pide la vista; acá se borra lo guardado y se vuelve a empezar
    public void nuevaSimulacion() {
        if (!dao.borrar()) {
            vista.mostrarError("No se pudo borrar la simulación guardada.");
        }
        reiniciarPantallas();
    }

    // ---------------------------------------------------------------
    // Simulación
    // ---------------------------------------------------------------

    public void avanzarTurno() {
        if (eco == null) {
            vista.mostrarError("No hay una simulación en curso. Configurá una primero.");
            return;
        }
        if (eco.simulacionTerminada()) {
            vista.mostrarError("La simulación ya terminó. Mirá el reporte final.");
            actualizarVista();
            return;
        }

        // se copia la lista porque el modelo la reutiliza en el turno siguiente
        List<String> eventos = new ArrayList<>(eco.procesarTurno());
        intervencionUsada = false;
        guardarCambios();

        actualizarVista();
        vista.mostrarEventos(eco.getTurnoActual(), eventos);

        if (eco.simulacionTerminada()) {
            vista.mostrarMensaje("Fin de la simulación: " + eco.getCausaFin()
                    + ". Ya podés ver el reporte final.");
        } else {
            vista.mostrarMensaje("Turno " + eco.getTurnoActual() + " procesado.");
        }
    }

    // ---------------------------------------------------------------
    // Intervención del jugador
    // ---------------------------------------------------------------

    public void cambiarClima(String nombreClima) {
        if (!puedeIntervenirAhora()) {
            return;
        }
        Clima nuevo;
        try {
            nuevo = buscarClima(nombreClima);
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
            return;
        }
        if (nuevo == eco.getClimaActual()) {
            vista.mostrarError("El clima ya es " + nuevo.getNombre() + ". Elegí otro.");
            return;
        }
        eco.cambiarClima(nuevo);
        intervencionUsada = true;
        guardarCambios();
        actualizarVista();
        vista.mostrarMensaje("El clima ahora es " + nuevo.getNombre() + ".");
    }

    // textoEnergia puede venir vacío: en ese caso la energía inicial es al azar
    public void agregarEntidad(String tipoElegido, String textoEnergia) {
        if (!puedeIntervenirAhora()) {
            return;
        }
        String tipo = claveDeTipo(tipoElegido);
        if (tipo == null) {
            vista.mostrarError("Elegí qué entidad querés agregar.");
            return;
        }

        boolean conEnergia = textoEnergia != null && !textoEnergia.trim().isEmpty();
        int energia = 0;
        if (conEnergia) {
            try {
                energia = leerEntero(textoEnergia, "Energía inicial", MIN_ENERGIA, MAX_ENERGIA);
            } catch (IllegalArgumentException e) {
                vista.mostrarError(e.getMessage());
                return;
            }
        }

        // regla de la primera instancia: máximo 5 lobos en toda la simulación
        if (tipo.equals("lobo") && !eco.puedeAgregarLobo()) {
            vista.mostrarError("Ya hubo 5 lobos en la simulación, que es el máximo permitido.");
            return;
        }

        // se usan las dos versiones de agregarEntidad (sobrecarga del modelo)
        String nombre;
        if (conEnergia) {
            nombre = eco.agregarEntidad(tipo, energia);
        } else {
            nombre = eco.agregarEntidad(tipo);
        }
        if (nombre == null) {
            vista.mostrarError("No se pudo agregar la entidad.");
            return;
        }
        intervencionUsada = true;
        guardarCambios();
        actualizarVista();
        vista.mostrarMensaje("Se agregó '" + nombre + "' al ecosistema.");
        
    }

    // ---------------------------------------------------------------
    // Navegación
    // ---------------------------------------------------------------

    public void irAConfiguracion() {
        vista.mostrarConfiguracion();
    }

    public void irASimulacion() {
        if (eco == null) {
            vista.mostrarSinSimulacion();
        }
        vista.mostrarSimulacion();
    }

    public void irAReporte() {
        if (eco == null || !eco.simulacionTerminada()) {
            vista.mostrarReporteVacio();
        } else {
            vista.mostrarTextoReporte(eco.getCausaFin(), eco.generarReporteFinal());
        }
        vista.mostrarReporte();
    }

    // ---------------------------------------------------------------
    // Auxiliares
    // ---------------------------------------------------------------
    // se llama después de cada cambio: guarda la simulación completa en el archivo.
    // Si el DAO devuelve false, la vista avisa
    private void guardarCambios() {
        boolean salioBien = dao.guardar(eco);   // → DAO / ← true o false
        if (!salioBien) {
            vista.mostrarError("No se pudo guardar la simulación en el archivo simulacion.txt.");
        }
    }
    
    // le manda a la vista todo lo que tiene que mostrar del estado actual
    private void actualizarVista() {
        vista.mostrarContadores(eco.getTurnoActual(), eco.getTurnosTotales(),
                eco.getClimaActual().getNombre(), eco.contarPlantas(), eco.contarConejos(),
                eco.contarLobos());
        vista.mostrarEntidades(armarFilas());

        boolean terminada = eco.simulacionTerminada();
        vista.habilitarAvance(!terminada);
        vista.habilitarVerReporte(terminada);

        boolean puedeIntervenir = eco.puedeIntervenir();
        String estado;
        if (terminada) {
            estado = "La simulación terminó, ya no se puede intervenir.";
        } else if (puedeIntervenir && intervencionUsada) {
            estado = "Ya intervinieron en este turno. Avancen para seguir.";
        } else if (puedeIntervenir) {
            estado = "Intervención disponible: cambiá el clima, agregá una entidad o solo avanzá.";
        } else {
            int faltan = 3 - (eco.getTurnoActual() % 3);
            estado = "Intervención no disponible. Próxima en " + faltan
                    + (faltan == 1 ? " turno." : " turnos.");
        }
        vista.actualizarIntervencion(puedeIntervenir && !intervencionUsada, estado,
                eco.getLobosDisponibles());
    }

    // una fila por entidad viva: tipo, nombre, energía, edad y detalle
    private Object[][] armarFilas() {
        ArrayList<Entidad> vivas = eco.getEntidadesVivas();
        Object[][] filas = new Object[vivas.size()][5];
        for (int i = 0; i < vivas.size(); i++) {
            Entidad e = vivas.get(i);
            filas[i][0] = e.getTipo();
            filas[i][1] = e.getNombre();
            filas[i][2] = (int) e.getEnergia();
            filas[i][3] = e.getEdad();
            filas[i][4] = detalleDe(e);
        }
        return filas;
    }

    private String detalleDe(Entidad e) {
        if (e instanceof PlantaVenenosa) {
            return "Tóxica";
        }
        if (e instanceof Planta) {
            return "Tamaño " + ((Planta) e).getTamanio();
        }
        if (e instanceof Conejo) {
            return ((Conejo) e).estaEnPeligro() ? "EN PELIGRO" : "Estable";
        }
        if (e instanceof Lobo) {
            return "Cacerías: " + ((Lobo) e).getExitosCaza();
        }
        return "";
    }

    // verifica que haya simulación, que no haya terminado y que sea turno de intervención
    private boolean puedeIntervenirAhora() {
        if (eco == null) {
            vista.mostrarError("No hay una simulación en curso.");
            return false;
        }
        if (eco.simulacionTerminada()) {
            vista.mostrarError("La simulación ya terminó, no se puede intervenir.");
            return false;
        }
        if (!eco.puedeIntervenir()) {
            vista.mostrarError("Solo se puede intervenir cada 3 turnos.");
            return false;
        }
        if (intervencionUsada) {
            vista.mostrarError("Ya intervinieron en este turno. Avancen para seguir.");
            return false;
        }
        return true;
    }

    // vacío, no numérico o fuera de rango: tira IllegalArgumentException con el mensaje para la persona
    private int leerEntero(String texto, String campo, int minimo, int maximo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + ": no puede estar vacío.");
        }
        int valor;
        try {
            valor = Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + ": ingresá un número entero, sin letras ni decimales.");
        }
        if (valor < minimo || valor > maximo) {
            throw new IllegalArgumentException(campo + ": tiene que estar entre " + minimo
                    + " y " + maximo + ".");
        }
        return valor;
    }

    private Clima buscarClima(String nombre) {
        if (nombre != null) {
            for (Clima c : Clima.values()) {
                if (c.getNombre().equals(nombre)) {
                    return c;
                }
            }
        }
        throw new IllegalArgumentException("Clima: elegí uno de la lista.");
    }

    // pasa del texto que se muestra al tipo que entiende el modelo
    private String claveDeTipo(String elegido) {
        if (elegido == null) {
            return null;
        }
        switch (elegido) {
            case "Planta":
                return "planta";
            case "Planta venenosa":
                return "venenosa";
            case "Conejo":
                return "conejo";
            case "Lobo":
                return "lobo";
            default:
                return null;
        }
    }
}
