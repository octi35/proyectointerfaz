package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import modelo.Clima;
import modelo.Conejo;
import modelo.Ecosistema;
import modelo.Entidad;
import modelo.Lobo;
import modelo.Planta;
import modelo.PlantaVenenosa;

/**
 * Unica clase que toca el archivo simulacion.txt. Guarda la simulacion y la
 * vuelve a armar al abrir la aplicacion. No usa Swing ni muestra mensajes:
 * devuelve true/false o null y el controlador decide que hacer.
 *
 * Formato: una linea por dato, campos separados por ";"
 *   SIMULACION;turnoActual;turnosTotales;CLIMA;nacPlantas;nacConejos;lobosAgregados
 *   HISTORIAL;plantas;conejos;lobos        (una por turno, desde el turno 0)
 *   EVENTOS;cantidad                       (una por turno jugado)
 *   PLANTA;nombre;energia;edad;viva;tamanio
 *   VENENOSA;nombre;energia;edad;viva;tamanio
 *   CONEJO;nombre;energia;edad;viva;velocidad
 *   LOBO;nombre;energia;edad;viva;cacerias
 */
public class EcosistemaDAO {

    // ruta relativa: el archivo queda en la carpeta del proyecto
    private static final String ARCHIVO = "simulacion.txt";
    private static final String SEPARADOR = ";";

    public boolean existeArchivo() {
        return new File(ARCHIVO).exists();
    }

    // ---------------------------------------------------------------
    // GUARDAR: Ecosistema -> archivo
    // ---------------------------------------------------------------
    // devuelve true si pudo guardar y false si hubo un error
    public boolean guardar(Ecosistema eco) {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ARCHIVO))) {
            escribirLinea(escritor, "SIMULACION" + SEPARADOR + eco.getTurnoActual()
                    + SEPARADOR + eco.getTurnosTotales()
                    + SEPARADOR + eco.getClimaActual().name()
                    + SEPARADOR + eco.getNacimientosPlantas()
                    + SEPARADOR + eco.getNacimientosConejos()
                    + SEPARADOR + eco.getLobosAgregados());

            for (int i = 0; i < eco.getHistorialPlantas().size(); i++) {
                escribirLinea(escritor, "HISTORIAL" + SEPARADOR + eco.getHistorialPlantas().get(i)
                        + SEPARADOR + eco.getHistorialConejos().get(i)
                        + SEPARADOR + eco.getHistorialLobos().get(i));
            }
            for (int cantidad : eco.getEventosPorTurno()) {
                escribirLinea(escritor, "EVENTOS" + SEPARADOR + cantidad);
            }
            // se guardan tambien las entidades muertas: el reporte las necesita
            for (Planta p : eco.getPlantas()) {
                String tipo = "PLANTA";
                if (p instanceof PlantaVenenosa) {
                    tipo = "VENENOSA";
                }
                escribirLinea(escritor, tipo + SEPARADOR + datosComunes(p)
                        + SEPARADOR + p.getTamanio());
            }
            for (Conejo c : eco.getConejos()) {
                escribirLinea(escritor, "CONEJO" + SEPARADOR + datosComunes(c)
                        + SEPARADOR + c.getVelocidad());
            }
            for (Lobo l : eco.getLobos()) {
                escribirLinea(escritor, "LOBO" + SEPARADOR + datosComunes(l)
                        + SEPARADOR + l.getExitosCaza());
            }
            return true;    // le avisa al controlador que salio bien
        } catch (IOException e) {
            return false;   // le avisa al controlador que fallo (el decide que mostrar)
        }
    }

    // ---------------------------------------------------------------
    // CARGAR: archivo -> Ecosistema
    // ---------------------------------------------------------------
    // devuelve null si no hay archivo o si tiene datos invalidos
    public Ecosistema cargar() {
        if (!existeArchivo()) {
            return null;
        }
        Ecosistema eco = null;
        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(SEPARADOR);
                switch (partes[0]) {
                    case "SIMULACION":
                        eco = new Ecosistema(Clima.valueOf(partes[3]),
                                Integer.parseInt(partes[2]), Integer.parseInt(partes[1]));
                        eco.cargarEstadisticas(Integer.parseInt(partes[4]),
                                Integer.parseInt(partes[5]), Integer.parseInt(partes[6]));
                        break;
                    case "HISTORIAL":
                        eco.cargarHistorial(Integer.parseInt(partes[1]),
                                Integer.parseInt(partes[2]), Integer.parseInt(partes[3]));
                        break;
                    case "EVENTOS":
                        eco.cargarEventosPorTurno(Integer.parseInt(partes[1]));
                        break;
                    case "PLANTA":
                    case "VENENOSA":
                        eco.cargarPlanta(armarPlanta(partes));
                        break;
                    case "CONEJO":
                        eco.cargarConejo(armarConejo(partes));
                        break;
                    case "LOBO":
                        eco.cargarLobo(armarLobo(partes));
                        break;
                    default:
                        return null;   // una linea que no se reconoce: archivo invalido
                }
            }
        } catch (IOException | IllegalArgumentException | ArrayIndexOutOfBoundsException
                | NullPointerException e) {
            // no se pudo leer, un numero mal escrito, faltan campos
            // o hay datos antes de la linea SIMULACION
            return null;
        }
        // sin la linea SIMULACION o sin historial no se puede seguir la simulacion
        if (eco == null || eco.getHistorialPlantas().isEmpty()) {
            return null;
        }
        return eco;
    }

    // borra la simulacion guardada (para empezar una nueva)
    public boolean borrar() {
        File archivo = new File(ARCHIVO);
        return !archivo.exists() || archivo.delete();
    }

    // ---------------------------------------------------------------
    // Auxiliares (privados)
    // ---------------------------------------------------------------

    private void escribirLinea(BufferedWriter escritor, String linea) throws IOException {
        escritor.write(linea);
        escritor.newLine();
    }

    // nombre;energia;edad;viva (lo que tienen todas las entidades, por herencia de Entidad)
    private String datosComunes(Entidad e) {
        return e.getNombre() + SEPARADOR + e.getEnergia() + SEPARADOR + e.getEdad()
                + SEPARADOR + e.isViva();
    }

    private Planta armarPlanta(String[] partes) {
        String nombre = partes[1];
        double energia = Double.parseDouble(partes[2]);
        int tamanio = Integer.parseInt(partes[5]);
        Planta p;
        if (partes[0].equals("VENENOSA")) {
            p = new PlantaVenenosa(nombre, energia, tamanio);
        } else {
            p = new Planta(nombre, energia, tamanio);
        }
        p.setEdad(Integer.parseInt(partes[3]));
        p.setViva(Boolean.parseBoolean(partes[4]));
        return p;
    }

    private Conejo armarConejo(String[] partes) {
        Conejo c = new Conejo(partes[1], Double.parseDouble(partes[2]));
        c.setEdad(Integer.parseInt(partes[3]));
        c.setViva(Boolean.parseBoolean(partes[4]));
        c.setVelocidad(Integer.parseInt(partes[5]));
        return c;
    }

    private Lobo armarLobo(String[] partes) {
        Lobo l = new Lobo(partes[1], Double.parseDouble(partes[2]));
        l.setEdad(Integer.parseInt(partes[3]));
        l.setViva(Boolean.parseBoolean(partes[4]));
        l.setExitosCaza(Integer.parseInt(partes[5]));
        return l;
    }
}