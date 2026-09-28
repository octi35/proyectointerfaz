package ecosistema;

import java.util.Scanner;

/**
 * Punto de entrada del simulador. Se encarga de la configuración inicial, del
 * bucle de turnos y del menú de intervención.
 */
public class Main {

    private static final Scanner teclado = new Scanner(System.in);

    /** Cada cuántos turnos se le ofrece al jugador intervenir. */
    private static final int TURNOS_ENTRE_INTERVENCIONES = 3;

    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println("  SIMULADOR DE ECOSISTEMA POR TURNOS");
        System.out.println("=======================================");

        Ecosistema eco = configurar();

        while (!eco.simulacionTerminada()) {
            eco.procesarTurno();

            if (eco.simulacionTerminada()) {
                break;
            }
            if (eco.getTurnoActual() % TURNOS_ENTRE_INTERVENCIONES == 0) {
                intervenir(eco);
            } else {
                System.out.println();
                System.out.print(">>> Presione Enter para continuar... ");
                teclado.nextLine();
            }
        }

        eco.generarReporteFinal();

        if (confirmar("¿Quiere ver el detalle de las entidades que quedaron vivas? (s/n): ")) {
            eco.mostrarDetalle();
        }
        System.out.println();
        System.out.println("Fin de la simulación.");
    }

    /** Pide los datos iniciales hasta que el jugador confirme la configuración. */
    private static Ecosistema configurar() {
        while (true) {
            System.out.println();
            System.out.println("-- Configuración inicial --");
            int plantas = leerEntero("Cantidad de plantas (5 a 30): ", 5, 30);
            int conejos = leerEntero("Cantidad de conejos (2 a 15): ", 2, 15);
            int lobos = leerEntero("Cantidad de lobos (1 a 5): ", 1, 5);
            Clima clima = leerClima("Clima inicial:");
            int turnos = leerEntero("Cantidad de turnos (10 a 50): ", 10, 50);

            System.out.println();
            System.out.println("Resumen: " + plantas + " plantas, " + conejos + " conejos, "
                    + lobos + " lobos, clima " + clima.getNombre() + ", " + turnos + " turnos.");

            if (confirmar("¿Confirma la configuración? (s/n): ")) {
                System.out.println("Ecosistema creado. Arranca la simulación.");
                return new Ecosistema(plantas, conejos, lobos, clima, turnos);
            }
            System.out.println("Se vuelve a cargar la configuración.");
        }
    }

    /** Menú que aparece cada tres turnos. */
    private static void intervenir(Ecosistema eco) {
        System.out.println();
        System.out.println("=== INTERVENCIÓN (cada " + TURNOS_ENTRE_INTERVENCIONES + " turnos) ===");
        System.out.println("1. Cambiar clima (actual: " + eco.getClimaActual().getNombre() + ")");
        System.out.println("2. Agregar entidad");
        System.out.println("3. Solo avanzar");
        int opcion = leerEntero("Opción: ", 1, 3);

        if (opcion == 1) {
            cambiarClima(eco);
        } else if (opcion == 2) {
            agregarEntidad(eco);
        } else {
            System.out.println("El turno avanza sin cambios.");
        }
    }

    private static void cambiarClima(Ecosistema eco) {
        Clima nuevo = leerClima("Nuevo clima:");
        if (nuevo == eco.getClimaActual()) {
            System.out.println("Ese ya es el clima actual, no hay nada que cambiar.");
            return;
        }
        if (confirmar("¿Cambiar el clima a " + nuevo.getNombre() + "? (s/n): ")) {
            eco.cambiarClima(nuevo);
            System.out.println("El clima ahora es " + nuevo.getNombre() + ".");
        } else {
            System.out.println("Se mantiene el clima " + eco.getClimaActual().getNombre() + ".");
        }
    }

    private static void agregarEntidad(Ecosistema eco) {
        System.out.print("¿Qué entidad agregar? (planta/venenosa/conejo/lobo): ");
        String tipo = teclado.nextLine().trim().toLowerCase();

        if (!tipo.equals("planta") && !tipo.equals("venenosa")
                && !tipo.equals("conejo") && !tipo.equals("lobo")) {
            System.out.println("Ese tipo de entidad no existe.");
            return;
        }
        if (tipo.equals("lobo") && !eco.puedeAgregarLobo()) {
            System.out.println("Ya se agregaron los 5 lobos permitidos para toda la simulación.");
            return;
        }
        if (!confirmar("¿Confirma agregar un " + tipo + "? (s/n): ")) {
            System.out.println("Operación cancelada.");
            return;
        }

        String nombre = eco.agregarEntidad(tipo);
        if (nombre == null) {
            System.out.println("No se pudo agregar la entidad.");
            return;
        }
        System.out.println("Se agregó '" + nombre + "' al ecosistema.");
        if (tipo.equals("lobo")) {
            System.out.println("Quedan " + eco.getLobosDisponibles() + " lobos disponibles.");
        }
    }

    private static Clima leerClima(String titulo) {
        System.out.println(titulo);
        System.out.println("  1. Soleado");
        System.out.println("  2. Lluvioso");
        System.out.println("  3. Sequía");
        System.out.println("  4. Invierno");
        return Clima.porNumero(leerEntero("Opción (1 a 4): ", 1, 4));
    }

    /** Lee un entero y no sigue hasta que entre en el rango pedido. */
    private static int leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje);
            String linea = teclado.nextLine().trim();
            try {
                int valor = Integer.parseInt(linea);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                System.out.println("El valor tiene que estar entre " + minimo + " y " + maximo + ".");
            } catch (NumberFormatException e) {
                System.out.println("Eso no es un número entero.");
            }
        }
    }

    private static boolean confirmar(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String respuesta = teclado.nextLine().trim().toLowerCase();
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                return true;
            }
            if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            }
            System.out.println("Responda con s o n.");
        }
    }
}
