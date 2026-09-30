package ecosistema;

import java.util.Scanner;

public class Main {

    private static final Scanner teclado = new Scanner(System.in);

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
            // cada 3 turnos se puede intervenir
            if (eco.getTurnoActual() % 3 == 0) {
                intervenir(eco);
            } else {
                System.out.println();
                System.out.print(">>> Presione Enter para continuar... ");
                teclado.nextLine();
            }
        }

        eco.generarReporteFinal();

        if (confirmar("¿Quiere ver el detalle de lo que quedó vivo? (s/n): ")) {
            eco.mostrarDetalle();
        }
        System.out.println("Fin de la simulación.");
    }

    // pide los datos hasta que el jugador confirme
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
                return new Ecosistema(plantas, conejos, lobos, clima, turnos);
            }
            System.out.println("Se vuelve a pedir la configuración.");
        }
    }

    private static void intervenir(Ecosistema eco) {
        System.out.println();
        System.out.println("=== INTERVENCIÓN (cada 3 turnos) ===");
        System.out.println("1. Cambiar clima (actual: " + eco.getClimaActual().getNombre() + ")");
        System.out.println("2. Agregar entidad");
        System.out.println("3. Solo avanzar");
        int opcion = leerEntero("Opción: ", 1, 3);

        if (opcion == 1) {
            cambiarClima(eco);
        } else if (opcion == 2) {
            agregarEntidad(eco);
        }
    }

    private static void cambiarClima(Ecosistema eco) {
        Clima nuevo = leerClima("Nuevo clima:");
        if (confirmar("¿Cambiar el clima a " + nuevo.getNombre() + "? (s/n): ")) {
            eco.cambiarClima(nuevo);
            System.out.println("El clima ahora es " + nuevo.getNombre() + ".");
        } else {
            System.out.println("Se queda el mismo clima.");
        }
    }

    private static void agregarEntidad(Ecosistema eco) {
        System.out.print("¿Qué entidad agregar? (planta/venenosa/conejo/lobo): ");
        String tipo = teclado.nextLine().trim().toLowerCase();

        if (!tipo.equals("planta") && !tipo.equals("venenosa") && !tipo.equals("conejo")
                && !tipo.equals("lobo")) {
            System.out.println("Ese tipo no existe.");
            return;
        }
        if (tipo.equals("lobo") && !eco.puedeAgregarLobo()) {
            System.out.println("Ya hubo 5 lobos en la simulación, que es el máximo permitido.");
            return;
        }
        if (confirmar("¿Confirma agregar un/a " + tipo + "? (s/n): ")) {
            String nombre;
            // se usan las dos versiones de agregarEntidad (sobrecarga)
            if (confirmar("¿Quiere elegir la energía inicial? (s/n): ")) {
                int energia = leerEntero("Energía inicial (10 a 100): ", 10, 100);
                nombre = eco.agregarEntidad(tipo, energia);
            } else {
                nombre = eco.agregarEntidad(tipo);
            }
            System.out.println("Se agregó '" + nombre + "' al ecosistema.");
        } else {
            System.out.println("No se agregó nada.");
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

    // no sigue hasta que el numero este dentro del rango
    private static int leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje);
            try {
                int valor = Integer.parseInt(teclado.nextLine().trim());
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                System.out.println("Tiene que estar entre " + minimo + " y " + maximo + ".");
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero.");
            }
        }
    }

    private static boolean confirmar(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String respuesta = teclado.nextLine().trim().toLowerCase();
            if (respuesta.equals("s")) {
                return true;
            }
            if (respuesta.equals("n")) {
                return false;
            }
            System.out.println("Responda s o n.");
        }
    }
}
