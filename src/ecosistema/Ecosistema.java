package ecosistema;

import java.util.ArrayList;
import java.util.Random;

/**
 * Contiene a todas las entidades y lleva adelante la simulación turno a turno.
 *
 * Las entidades muertas quedan en las listas para poder armar el reporte final,
 * por eso en todos lados se cuenta filtrando por las que siguen vivas.
 */
public class Ecosistema {

    /** Tope de lobos que el jugador puede agregar en toda la partida. */
    private static final int MAX_LOBOS_AGREGADOS = 5;

    /** Cuantas plantas entran en el terreno. Pasado ese numero no se reproducen. */
    private static final int MAX_PLANTAS = 30;

    private static final String[] NOMBRES_CONEJOS = {"Blas", "Luna", "Rex", "Tito", "Coco",
        "Nina", "Pipo", "Mora", "Tomi", "Frida"};
    private static final String[] NOMBRES_LOBOS = {"Fang", "Sombra", "Zeus", "Nieve", "Rayo",
        "Bruno", "Kira", "Atila"};

    private ArrayList<Planta> plantas;
    private ArrayList<Conejo> conejos;
    private ArrayList<Lobo> lobos;
    private Clima climaActual;
    private int turnoActual;

    private final int turnosTotales;
    private final Random random;

    // Datos que se juntan durante la partida para el reporte final
    private ArrayList<int[]> historial;
    private ArrayList<Integer> eventosPorTurno;
    private int eventosTurno;
    private int nacimientosPlantas;
    private int nacimientosConejos;
    private int muertesPlantas;
    private int muertesConejos;
    private int muertesLobos;
    private int lobosAgregados;
    private int contadorPlantas;
    private int contadorVenenosas;
    private int contadorConejos;
    private int contadorLobos;

    public Ecosistema(int cantPlantas, int cantConejos, int cantLobos, Clima climaInicial,
            int turnosTotales) {
        this.plantas = new ArrayList<>();
        this.conejos = new ArrayList<>();
        this.lobos = new ArrayList<>();
        this.climaActual = climaInicial;
        this.turnoActual = 0;
        this.turnosTotales = turnosTotales;
        this.random = new Random();
        this.historial = new ArrayList<>();
        this.eventosPorTurno = new ArrayList<>();

        crearPoblacionInicial(cantPlantas, cantConejos, cantLobos);
        historial.add(new int[]{contarPlantas(), contarConejos(), contarLobos()});
    }

    /** Arma la población de arranque con energia aleatoria. */
    private void crearPoblacionInicial(int cantPlantas, int cantConejos, int cantLobos) {
        for (int i = 0; i < cantPlantas; i++) {
            double energia = 25 + random.nextInt(31);
            int tamanio = 1 + random.nextInt(5);
            // una de cada siete plantas sale venenosa
            if (random.nextInt(7) == 0) {
                plantas.add(new PlantaVenenosa(nuevoNombreVenenosa(), energia, tamanio));
            } else {
                plantas.add(new Planta(nuevoNombrePlanta(), energia, tamanio));
            }
        }
        for (int i = 0; i < cantConejos; i++) {
            conejos.add(new Conejo(nuevoNombreConejo(), 30 + random.nextInt(31)));
        }
        for (int i = 0; i < cantLobos; i++) {
            lobos.add(new Lobo(nuevoNombreLobo(), 40 + random.nextInt(31)));
        }
    }

    /** Ejecuta un turno completo respetando el orden de acciones. */
    public void procesarTurno() {
        turnoActual++;
        eventosTurno = 0;

        int plantasAntes = contarPlantas();
        int conejosAntes = contarConejos();
        int lobosAntes = contarLobos();
        int nacieronPlantas = nacimientosPlantas;
        int nacieronConejos = nacimientosConejos;

        System.out.println();
        System.out.println("=== TURNO " + turnoActual + " | Clima: " + climaActual.getNombre() + " ===");
        System.out.println("Plantas: " + plantasAntes + "  Conejos: " + conejosAntes
                + "  Lobos: " + lobosAntes);
        System.out.println("-- Eventos --");

        // 1) las plantas con energia suficiente se reproducen
        for (Planta p : new ArrayList<>(plantas)) {
            if (p.isViva()) {
                p.actuar(this);
            }
        }
        // 2) los conejos buscan comida y despues intentan reproducirse
        for (Conejo c : new ArrayList<>(conejos)) {
            if (c.estaVivo()) {
                c.actuar(this);
            }
        }
        // 3) los lobos salen a cazar
        for (Lobo l : new ArrayList<>(lobos)) {
            if (l.estaVivo()) {
                l.actuar(this);
            }
        }
        // 4) todas envejecen, gastan energia base y el clima les pasa factura
        for (Planta p : plantas) {
            if (p.isViva()) {
                p.envejecer();
                p.rebrotar(climaActual);
            }
        }
        for (Conejo c : conejos) {
            if (c.estaVivo()) {
                c.envejecer();
                // el clima no revive a un animal que ya se quedó en cero
                if (c.getEnergia() > 0) {
                    c.setEnergia(c.getEnergia() + climaActual.getAjusteConejo());
                }
            }
        }
        for (Lobo l : lobos) {
            if (l.estaVivo()) {
                l.envejecer();
                if (l.getEnergia() > 0) {
                    l.setEnergia(l.getEnergia() + climaActual.getAjusteLobo());
                }
            }
        }
        // 5) las entidades que se quedaron sin energia mueren
        for (Planta p : plantas) {
            if (p.isViva() && p.getEnergia() <= 0) {
                p.setViva(false);
                registrarEvento(p.getTipo() + " '" + p.getNombre() + "' se secó");
            }
        }
        for (Conejo c : conejos) {
            c.verificarMuerte(this);
        }
        for (Lobo l : lobos) {
            l.verificarMuerte(this);
        }

        if (eventosTurno == 0) {
            System.out.println("  (turno sin novedades)");
        }

        // 6) estado del ecosistema al cerrar el turno
        mostrarEstado();

        muertesPlantas += plantasAntes + (nacimientosPlantas - nacieronPlantas) - contarPlantas();
        muertesConejos += conejosAntes + (nacimientosConejos - nacieronConejos) - contarConejos();
        muertesLobos += lobosAntes - contarLobos();

        historial.add(new int[]{contarPlantas(), contarConejos(), contarLobos()});
        eventosPorTurno.add(eventosTurno);
    }

    /** Imprime el conteo de cada población y el clima actual. */
    public void mostrarEstado() {
        System.out.println("Estado: Plantas: " + contarPlantas() + "  Conejos: " + contarConejos()
                + "  Lobos: " + contarLobos() + "  | Clima: " + climaActual.getNombre());
    }

    /** Muestra el detalle de cada entidad viva. */
    public void mostrarDetalle() {
        for (Planta p : plantas) {
            if (p.isViva()) {
                p.mostrarEstado();
            }
        }
        for (Conejo c : conejos) {
            if (c.estaVivo()) {
                c.mostrarEstado();
            }
        }
        for (Lobo l : lobos) {
            if (l.estaVivo()) {
                l.mostrarEstado();
            }
        }
    }

    /** Anota un evento del turno y lo muestra por pantalla. */
    public void registrarEvento(String texto) {
        eventosTurno++;
        System.out.println("  " + texto);
    }

    /** Agrega una entidad nueva con energia aleatoria. Devuelve el nombre o null. */
    public String agregarEntidad(String tipo) {
        return agregarEntidad(tipo, 30 + random.nextInt(31));
    }

    /** Misma operación pero fijando la energia inicial. */
    public String agregarEntidad(String tipo, double energia) {
        String elegido = tipo == null ? "" : tipo.trim().toLowerCase();
        int tamanio = 1 + random.nextInt(5);

        if (elegido.equals("planta")) {
            Planta nueva = new Planta(nuevoNombrePlanta(), energia, tamanio);
            plantas.add(nueva);
            return nueva.getNombre();
        }
        if (elegido.equals("venenosa")) {
            PlantaVenenosa nueva = new PlantaVenenosa(nuevoNombreVenenosa(), energia, tamanio);
            plantas.add(nueva);
            return nueva.getNombre();
        }
        if (elegido.equals("conejo")) {
            Conejo nuevo = new Conejo(nuevoNombreConejo(), energia);
            conejos.add(nuevo);
            return nuevo.getNombre();
        }
        if (elegido.equals("lobo")) {
            if (!puedeAgregarLobo()) {
                return null;
            }
            Lobo nuevo = new Lobo(nuevoNombreLobo(), energia);
            lobos.add(nuevo);
            lobosAgregados++;
            return nuevo.getNombre();
        }
        return null;
    }

    public void cambiarClima(Clima nuevo) {
        if (nuevo != null) {
            this.climaActual = nuevo;
        }
    }

    /** Suma una planta nacida por reproducción. */
    public void agregarPlanta(Planta hija) {
        plantas.add(hija);
        nacimientosPlantas++;
    }

    /** Suma un conejo nacido por reproducción. */
    public void agregarConejo(Conejo cria) {
        conejos.add(cria);
        nacimientosConejos++;
    }

    /**
     * Devuelve una planta viva al azar que todavia tenga energia, o null si no
     * queda ninguna. Una planta ya pastoreada este turno no sirve de comida.
     */
    public Planta buscarPlantaViva() {
        ArrayList<Planta> vivas = new ArrayList<>();
        for (Planta p : plantas) {
            if (p.isViva() && p.getEnergia() > 0) {
                vivas.add(p);
            }
        }
        if (vivas.isEmpty()) {
            return null;
        }
        return vivas.get(random.nextInt(vivas.size()));
    }

    /** Devuelve un conejo vivo al azar, o null si no queda ninguno. */
    public Conejo buscarConejoVivo() {
        ArrayList<Conejo> vivos = new ArrayList<>();
        for (Conejo c : conejos) {
            if (c.estaVivo()) {
                vivos.add(c);
            }
        }
        if (vivos.isEmpty()) {
            return null;
        }
        return vivos.get(random.nextInt(vivos.size()));
    }

    /**
     * Junta plantas y conejos en una sola lista. Como las dos clases implementan
     * Reproducible se pueden recorrer con el mismo código.
     */
    public ArrayList<Reproducible> getReproducibles() {
        ArrayList<Reproducible> lista = new ArrayList<>();
        for (Planta p : plantas) {
            if (p.isViva()) {
                lista.add(p);
            }
        }
        for (Conejo c : conejos) {
            if (c.estaVivo()) {
                lista.add(c);
            }
        }
        return lista;
    }

    /** Entidades vivas que hoy están en condiciones de reproducirse. */
    public int contarListasParaReproducirse() {
        int total = 0;
        for (Reproducible r : getReproducibles()) {
            if (r.puedeReproducirse()) {
                total++;
            }
        }
        return total;
    }

    /** Lobos y plantas venenosas vivos, ordenados de mayor a menor peligro. */
    public ArrayList<Peligroso> getPeligrosos() {
        ArrayList<Peligroso> lista = new ArrayList<>();
        for (Lobo l : lobos) {
            if (l.estaVivo()) {
                lista.add(l);
            }
        }
        for (Planta p : plantas) {
            if (p.isViva() && p instanceof Peligroso) {
                lista.add((Peligroso) p);
            }
        }
        for (int i = 0; i < lista.size(); i++) {
            for (int j = i + 1; j < lista.size(); j++) {
                if (lista.get(j).getNivelPeligro() > lista.get(i).getNivelPeligro()) {
                    Peligroso aux = lista.get(i);
                    lista.set(i, lista.get(j));
                    lista.set(j, aux);
                }
            }
        }
        return lista;
    }

    /** True si alguna de las tres poblaciones llegó a cero. */
    public boolean ecosistemaColapsado() {
        return contarPlantas() == 0 || contarConejos() == 0 || contarLobos() == 0;
    }

    /** True cuando se cumplieron los turnos o el ecosistema colapsó. */
    public boolean simulacionTerminada() {
        return turnoActual >= turnosTotales || ecosistemaColapsado();
    }

    public String getCausaFin() {
        if (contarPlantas() == 0) {
            return "colapso del ecosistema, se extinguieron las plantas";
        }
        if (contarConejos() == 0) {
            return "colapso del ecosistema, se extinguieron los conejos";
        }
        if (contarLobos() == 0) {
            return "colapso del ecosistema, se extinguieron los lobos";
        }
        return "se completaron los " + turnosTotales + " turnos configurados";
    }

    /** Imprime el resumen completo de la partida. */
    public void generarReporteFinal() {
        System.out.println();
        System.out.println("========== REPORTE FINAL ==========");
        System.out.println("Turnos jugados: " + turnoActual + " de " + turnosTotales);
        System.out.println("Causa de fin: " + getCausaFin());

        System.out.println();
        System.out.println("-- Población final --");
        System.out.println("Plantas: " + contarPlantas() + "  Conejos: " + contarConejos()
                + "  Lobos: " + contarLobos());
        System.out.println("Listas para reproducirse: " + contarListasParaReproducirse());

        System.out.println();
        System.out.println("-- Nacimientos y muertes --");
        System.out.println("Plantas: " + nacimientosPlantas + " nacimientos / " + muertesPlantas + " muertes");
        System.out.println("Conejos: " + nacimientosConejos + " nacimientos / " + muertesConejos + " muertes");
        System.out.println("Lobos:   0 nacimientos / " + muertesLobos + " muertes ("
                + lobosAgregados + " agregados por el jugador)");

        System.out.println();
        System.out.println("-- Turno de mayor actividad --");
        mostrarTurnoMasActivo();

        System.out.println();
        System.out.println("-- Entidad más longeva de cada tipo --");
        mostrarMasLongeva("Planta", plantas);
        mostrarMasLongeva("Conejo", conejos);
        mostrarMasLongeva("Lobo", lobos);

        System.out.println();
        System.out.println("-- Lobo con más cacerías --");
        mostrarMejorCazador();

        System.out.println();
        System.out.println("-- Máximos y mínimos de cada población --");
        mostrarMaximoYMinimo("Plantas", 0);
        mostrarMaximoYMinimo("Conejos", 1);
        mostrarMaximoYMinimo("Lobos", 2);

        System.out.println();
        System.out.println("-- Elementos peligrosos que quedaron --");
        ArrayList<Peligroso> peligrosos = getPeligrosos();
        if (peligrosos.isEmpty()) {
            System.out.println("No quedó ninguno.");
        } else {
            for (Peligroso p : peligrosos) {
                Entidad e = (Entidad) p;
                System.out.println("  " + e.getTipo() + " '" + e.getNombre()
                        + "' | nivel de peligro: " + p.getNivelPeligro());
            }
        }
        System.out.println();
        System.out.println("===================================");
    }

    private void mostrarTurnoMasActivo() {
        if (eventosPorTurno.isEmpty()) {
            System.out.println("No se jugó ningún turno.");
            return;
        }
        int mejor = 0;
        for (int i = 1; i < eventosPorTurno.size(); i++) {
            if (eventosPorTurno.get(i) > eventosPorTurno.get(mejor)) {
                mejor = i;
            }
        }
        System.out.println("Turno " + (mejor + 1) + " con " + eventosPorTurno.get(mejor) + " eventos.");
    }

    /** Recorre cualquier lista de entidades y muestra la de mayor edad. */
    private void mostrarMasLongeva(String titulo, ArrayList<? extends Entidad> lista) {
        Entidad mayor = null;
        for (Entidad e : lista) {
            if (mayor == null || e.getEdad() > mayor.getEdad()) {
                mayor = e;
            }
        }
        if (mayor == null) {
            System.out.println(titulo + ": no hubo ninguna.");
        } else {
            System.out.println(titulo + ": '" + mayor.getNombre() + "' con " + mayor.getEdad()
                    + " turnos de vida" + (mayor.isViva() ? " (sigue viva)" : " (murió)"));
        }
    }

    private void mostrarMejorCazador() {
        Lobo mejor = null;
        for (Lobo l : lobos) {
            if (mejor == null || l.getExitosCaza() > mejor.getExitosCaza()) {
                mejor = l;
            }
        }
        if (mejor == null) {
            System.out.println("No hubo lobos en la simulación.");
        } else {
            System.out.println("'" + mejor.getNombre() + "' con " + mejor.getExitosCaza() + " cacerías.");
        }
    }

    private void mostrarMaximoYMinimo(String titulo, int indice) {
        int max = historial.get(0)[indice];
        int min = historial.get(0)[indice];
        int turnoMax = 0;
        int turnoMin = 0;
        for (int i = 1; i < historial.size(); i++) {
            int valor = historial.get(i)[indice];
            if (valor > max) {
                max = valor;
                turnoMax = i;
            }
            if (valor < min) {
                min = valor;
                turnoMin = i;
            }
        }
        System.out.println(titulo + ": máximo " + max + " (turno " + turnoMax + ")"
                + " | mínimo " + min + " (turno " + turnoMin + ")");
    }

    public String nuevoNombrePlanta() {
        contadorPlantas++;
        return "Helecho-" + contadorPlantas;
    }

    public String nuevoNombreVenenosa() {
        contadorVenenosas++;
        return "Cicuta-" + contadorVenenosas;
    }

    public String nuevoNombreConejo() {
        contadorConejos++;
        return armarNombre(NOMBRES_CONEJOS, contadorConejos);
    }

    public String nuevoNombreLobo() {
        contadorLobos++;
        return armarNombre(NOMBRES_LOBOS, contadorLobos);
    }

    /** Usa los nombres de la lista y, cuando se terminan, les agrega un número. */
    private String armarNombre(String[] nombres, int contador) {
        String base = nombres[(contador - 1) % nombres.length];
        int vuelta = (contador - 1) / nombres.length;
        if (vuelta == 0) {
            return base;
        }
        return base + "-" + (vuelta + 1);
    }

    public int contarPlantas() {
        int total = 0;
        for (Planta p : plantas) {
            if (p.isViva()) {
                total++;
            }
        }
        return total;
    }

    public int contarConejos() {
        int total = 0;
        for (Conejo c : conejos) {
            if (c.estaVivo()) {
                total++;
            }
        }
        return total;
    }

    public int contarLobos() {
        int total = 0;
        for (Lobo l : lobos) {
            if (l.estaVivo()) {
                total++;
            }
        }
        return total;
    }

    /** True mientras quede lugar en el terreno para plantas nuevas. */
    public boolean hayLugarParaPlantas() {
        return contarPlantas() < MAX_PLANTAS;
    }

    public boolean puedeAgregarLobo() {
        return lobosAgregados < MAX_LOBOS_AGREGADOS;
    }

    public int getLobosDisponibles() {
        return MAX_LOBOS_AGREGADOS - lobosAgregados;
    }

    public Clima getClimaActual() {
        return climaActual;
    }

    public int getTurnoActual() {
        return turnoActual;
    }

    public int getTurnosTotales() {
        return turnosTotales;
    }
}
