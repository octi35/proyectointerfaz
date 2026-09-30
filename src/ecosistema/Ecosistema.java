package ecosistema;

import java.util.ArrayList;
import java.util.Random;

public class Ecosistema {

    private static final int MAX_LOBOS = 5;
    private static final int MAX_PLANTAS = 40;

    private ArrayList<Planta> plantas;
    private ArrayList<Conejo> conejos;
    private ArrayList<Lobo> lobos;
    private Clima climaActual;
    private int turnoActual;
    private int turnosTotales;
    private Random random;

    // datos que se van guardando para el reporte final
    private ArrayList<Integer> historialPlantas;
    private ArrayList<Integer> historialConejos;
    private ArrayList<Integer> historialLobos;
    private ArrayList<Integer> eventosPorTurno;
    private int eventosTurno;
    private int nacimientosPlantas;
    private int nacimientosConejos;
    private int lobosAgregados;

    // para ponerle nombre a cada entidad nueva
    private int contadorPlantas;
    private int contadorVenenosas;
    private int contadorConejos;
    private int contadorLobos;
    private String[] nombresConejos = {"Blas", "Luna", "Rex", "Tito", "Coco", "Nina"};
    private String[] nombresLobos = {"Fang", "Sombra", "Zeus", "Nieve", "Rayo"};

    public Ecosistema(int cantPlantas, int cantConejos, int cantLobos, Clima climaInicial,
            int turnosTotales) {
        this.plantas = new ArrayList<>();
        this.conejos = new ArrayList<>();
        this.lobos = new ArrayList<>();
        this.climaActual = climaInicial;
        this.turnoActual = 0;
        this.turnosTotales = turnosTotales;
        this.random = new Random();
        this.historialPlantas = new ArrayList<>();
        this.historialConejos = new ArrayList<>();
        this.historialLobos = new ArrayList<>();
        this.eventosPorTurno = new ArrayList<>();

        for (int i = 0; i < cantPlantas; i++) {
            double energia = 25 + random.nextInt(31);
            int tamanio = 1 + random.nextInt(5);
            // una de cada seis sale venenosa
            if (random.nextInt(6) == 0) {
                plantas.add(new PlantaVenenosa(nombreVenenosa(), energia, tamanio));
            } else {
                plantas.add(new Planta(nombrePlanta(), energia, tamanio));
            }
        }
        for (int i = 0; i < cantConejos; i++) {
            conejos.add(new Conejo(nombreConejo(), 30 + random.nextInt(31)));
        }
        for (int i = 0; i < cantLobos; i++) {
            lobos.add(new Lobo(nombreLobo(), 40 + random.nextInt(31)));
        }
        guardarHistorial();
    }

    public void procesarTurno() {
        turnoActual++;
        eventosTurno = 0;

        System.out.println();
        System.out.println("=== TURNO " + turnoActual + " | Clima: " + climaActual.getNombre() + " ===");
        System.out.println("Plantas: " + contarPlantas() + "  Conejos: " + contarConejos()
                + "  Lobos: " + contarLobos());
        System.out.println("-- Eventos --");

        // 1) las plantas se reproducen (uso una copia porque la lista crece mientras la recorro)
        for (Planta p : new ArrayList<>(plantas)) {
            if (p.isViva()) {
                p.actuar(this);
            }
        }
        // 2) los conejos comen y despues intentan reproducirse
        for (Conejo c : new ArrayList<>(conejos)) {
            if (c.estaVivo()) {
                c.actuar(this);
            }
        }
        // 3) los lobos cazan
        for (Lobo l : lobos) {
            if (l.estaVivo()) {
                l.actuar(this);
            }
        }
        // 4) todos envejecen y gastan energia, y el clima les suma o resta
        for (Planta p : plantas) {
            if (p.isViva()) {
                p.envejecer();
                p.crecer(climaActual);
            }
        }
        for (Conejo c : conejos) {
            if (c.estaVivo()) {
                c.envejecer();
                // si ya estaba en 0 no lo revive el clima
                if (c.getEnergia() > 0) {
                    c.setEnergia(c.getEnergia() + climaActual.getEnergiaConejos());
                }
            }
        }
        for (Lobo l : lobos) {
            if (l.estaVivo()) {
                l.envejecer();
                if (l.getEnergia() > 0) {
                    l.setEnergia(l.getEnergia() + climaActual.getEnergiaLobos());
                }
            }
        }
        // 5) mueren los que se quedaron sin energia
        for (Planta p : plantas) {
            if (p.isViva() && p.getEnergia() <= 0) {
                p.setViva(false);
                registrarEvento("Planta '" + p.getNombre() + "' se secó");
            }
        }
        int vivosAntes = contarConejos() + contarLobos();
        for (Conejo c : conejos) {
            c.verificarMuerte();
        }
        for (Lobo l : lobos) {
            l.verificarMuerte();
        }
        // verificarMuerte ya imprime, aca solo las cuento como eventos
        eventosTurno += vivosAntes - contarConejos() - contarLobos();

        if (eventosTurno == 0) {
            System.out.println("  (sin novedades)");
        }
        // 6) estado al final del turno
        mostrarEstado();
        guardarHistorial();
        eventosPorTurno.add(eventosTurno);
    }

    public void mostrarEstado() {
        System.out.println("Estado: Plantas: " + contarPlantas() + "  Conejos: " + contarConejos()
                + "  Lobos: " + contarLobos() + "  | Clima: " + climaActual.getNombre());
    }

    // muestra cada entidad viva usando su propio mostrarEstado()
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

    public void registrarEvento(String texto) {
        eventosTurno++;
        System.out.println("  " + texto);
    }

    // agrega una entidad con energia al azar, devuelve el nombre (o null si no se pudo)
    public String agregarEntidad(String tipo) {
        return agregarEntidad(tipo, 30 + random.nextInt(31));
    }

    // igual que el anterior pero eligiendo la energia inicial
    public String agregarEntidad(String tipo, double energia) {
        int tamanio = 1 + random.nextInt(5);
        if (tipo.equals("planta")) {
            Planta nueva = new Planta(nombrePlanta(), energia, tamanio);
            plantas.add(nueva);
            return nueva.getNombre();
        }
        if (tipo.equals("venenosa")) {
            PlantaVenenosa nueva = new PlantaVenenosa(nombreVenenosa(), energia, tamanio);
            plantas.add(nueva);
            return nueva.getNombre();
        }
        if (tipo.equals("conejo")) {
            Conejo nuevo = new Conejo(nombreConejo(), energia);
            conejos.add(nuevo);
            return nuevo.getNombre();
        }
        if (tipo.equals("lobo") && puedeAgregarLobo()) {
            Lobo nuevo = new Lobo(nombreLobo(), energia);
            lobos.add(nuevo);
            lobosAgregados++;
            return nuevo.getNombre();
        }
        return null;
    }

    public void cambiarClima(Clima nuevo) {
        this.climaActual = nuevo;
    }

    // las dos siguientes se usan cuando nace una entidad
    public void agregarPlanta(Planta hija) {
        plantas.add(hija);
        nacimientosPlantas++;
    }

    public void agregarConejo(Conejo cria) {
        conejos.add(cria);
        nacimientosConejos++;
    }

    // una planta viva al azar (si ya la comieron este turno queda en 0 y no sirve)
    public Planta buscarPlanta() {
        ArrayList<Planta> disponibles = new ArrayList<>();
        for (Planta p : plantas) {
            if (p.isViva() && p.getEnergia() > 0) {
                disponibles.add(p);
            }
        }
        if (disponibles.isEmpty()) {
            return null;
        }
        return disponibles.get(random.nextInt(disponibles.size()));
    }

    // un conejo vivo al azar
    public Conejo buscarConejo() {
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

    // plantas y conejos juntos en una misma lista, porque los dos son Reproducible
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

    public int cuantosPuedenReproducirse() {
        int total = 0;
        for (Reproducible r : getReproducibles()) {
            if (r.puedeReproducirse()) {
                total++;
            }
        }
        return total;
    }

    // lobos y plantas venenosas vivos, de mayor a menor peligro
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
        // orden de burbuja
        for (int i = 0; i < lista.size() - 1; i++) {
            for (int j = 0; j < lista.size() - 1 - i; j++) {
                if (lista.get(j).getNivelPeligro() < lista.get(j + 1).getNivelPeligro()) {
                    Peligroso aux = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, aux);
                }
            }
        }
        return lista;
    }

    // true si alguna poblacion llego a 0
    public boolean ecosistemaColapsado() {
        return contarPlantas() == 0 || contarConejos() == 0 || contarLobos() == 0;
    }

    public boolean simulacionTerminada() {
        return turnoActual >= turnosTotales || ecosistemaColapsado();
    }

    public String getCausaFin() {
        if (contarPlantas() == 0) {
            return "colapso, se extinguieron las plantas";
        }
        if (contarConejos() == 0) {
            return "colapso, se extinguieron los conejos";
        }
        if (contarLobos() == 0) {
            return "colapso, se extinguieron los lobos";
        }
        return "se completaron los " + turnosTotales + " turnos";
    }

    public void generarReporteFinal() {
        System.out.println();
        System.out.println("========== REPORTE FINAL ==========");
        System.out.println("Turnos jugados: " + turnoActual + " de " + turnosTotales);
        System.out.println("Causa de fin: " + getCausaFin());

        System.out.println();
        System.out.println("-- Población final --");
        System.out.println("Plantas: " + contarPlantas() + "  Conejos: " + contarConejos()
                + "  Lobos: " + contarLobos());
        System.out.println("Listas para reproducirse: " + cuantosPuedenReproducirse());

        // las listas guardan tambien a los muertos, asi que muertos = total - vivos
        System.out.println();
        System.out.println("-- Nacimientos y muertes --");
        System.out.println("Plantas: " + nacimientosPlantas + " nacimientos, "
                + (plantas.size() - contarPlantas()) + " muertes");
        System.out.println("Conejos: " + nacimientosConejos + " nacimientos, "
                + (conejos.size() - contarConejos()) + " muertes");
        System.out.println("Lobos: 0 nacimientos, " + (lobos.size() - contarLobos())
                + " muertes (el jugador agregó " + lobosAgregados + ")");

        System.out.println();
        System.out.println("-- Turno de mayor actividad --");
        int mejorTurno = 0;
        for (int i = 1; i < eventosPorTurno.size(); i++) {
            if (eventosPorTurno.get(i) > eventosPorTurno.get(mejorTurno)) {
                mejorTurno = i;
            }
        }
        if (eventosPorTurno.isEmpty()) {
            System.out.println("No se jugó ningún turno.");
        } else {
            System.out.println("Turno " + (mejorTurno + 1) + " con " + eventosPorTurno.get(mejorTurno)
                    + " eventos.");
        }

        System.out.println();
        System.out.println("-- Entidad más longeva de cada tipo --");
        mostrarMasLongeva("Planta", new ArrayList<Entidad>(plantas));
        mostrarMasLongeva("Conejo", new ArrayList<Entidad>(conejos));
        mostrarMasLongeva("Lobo", new ArrayList<Entidad>(lobos));

        System.out.println();
        System.out.println("-- Lobo con más cacerías --");
        Lobo mejorLobo = null;
        for (Lobo l : lobos) {
            if (mejorLobo == null || l.getExitosCaza() > mejorLobo.getExitosCaza()) {
                mejorLobo = l;
            }
        }
        if (mejorLobo == null) {
            System.out.println("No hubo lobos.");
        } else {
            System.out.println("'" + mejorLobo.getNombre() + "' con " + mejorLobo.getExitosCaza()
                    + " cacerías.");
        }

        System.out.println();
        System.out.println("-- Máximo y mínimo de cada población --");
        mostrarMaxMin("Plantas", historialPlantas);
        mostrarMaxMin("Conejos", historialConejos);
        mostrarMaxMin("Lobos", historialLobos);

        System.out.println();
        System.out.println("-- Peligrosos que quedaron (de mayor a menor) --");
        ArrayList<Peligroso> peligrosos = getPeligrosos();
        if (peligrosos.isEmpty()) {
            System.out.println("No quedó ninguno.");
        }
        for (Peligroso p : peligrosos) {
            Entidad e = (Entidad) p;
            System.out.println("  " + e.getTipo() + " '" + e.getNombre() + "' | nivel: "
                    + p.getNivelPeligro());
        }
        System.out.println("===================================");
    }

    private void mostrarMasLongeva(String titulo, ArrayList<Entidad> lista) {
        Entidad mayor = null;
        for (Entidad e : lista) {
            if (mayor == null || e.getEdad() > mayor.getEdad()) {
                mayor = e;
            }
        }
        if (mayor == null) {
            System.out.println(titulo + ": no hubo.");
        } else {
            String estado = "murió";
            if (mayor.isViva()) {
                estado = "sigue viva";
            }
            System.out.println(titulo + ": '" + mayor.getNombre() + "' con " + mayor.getEdad()
                    + " turnos (" + estado + ")");
        }
    }

    // el historial arranca en el turno 0 (antes de empezar)
    private void mostrarMaxMin(String titulo, ArrayList<Integer> historial) {
        int max = historial.get(0);
        int min = historial.get(0);
        int turnoMax = 0;
        int turnoMin = 0;
        for (int i = 1; i < historial.size(); i++) {
            if (historial.get(i) > max) {
                max = historial.get(i);
                turnoMax = i;
            }
            if (historial.get(i) < min) {
                min = historial.get(i);
                turnoMin = i;
            }
        }
        System.out.println(titulo + ": máximo " + max + " (turno " + turnoMax + "), mínimo "
                + min + " (turno " + turnoMin + ")");
    }

    private void guardarHistorial() {
        historialPlantas.add(contarPlantas());
        historialConejos.add(contarConejos());
        historialLobos.add(contarLobos());
    }

    public String nombrePlanta() {
        contadorPlantas++;
        return "Helecho-" + contadorPlantas;
    }

    public String nombreVenenosa() {
        contadorVenenosas++;
        return "Cicuta-" + contadorVenenosas;
    }

    public String nombreConejo() {
        contadorConejos++;
        return nombresConejos[random.nextInt(nombresConejos.length)] + "-" + contadorConejos;
    }

    public String nombreLobo() {
        contadorLobos++;
        return nombresLobos[random.nextInt(nombresLobos.length)] + "-" + contadorLobos;
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

    public boolean hayLugarParaPlantas() {
        return contarPlantas() < MAX_PLANTAS;
    }

        // no puede haber mas de 5 lobos en toda la simulacion (contando los iniciales
    // y los agregados). La lista guarda tambien a los muertos, por eso sirve size()
    public boolean puedeAgregarLobo() {
        return lobos.size() < MAX_LOBOS;
    
    }

    public ArrayList<Planta> getPlantas() {
        return plantas;
    }

    public ArrayList<Conejo> getConejos() {
        return conejos;
    }

    public ArrayList<Lobo> getLobos() {
        return lobos;
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
