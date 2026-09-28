package ecosistema;

/**
 * Conejo: come plantas y, si junta energia suficiente, tiene crias.
 */
public class Conejo extends Animal implements Reproducible {

    private static final int ENERGIA_SIN_COMIDA = 15;
    private static final int ENERGIA_PARA_CRIAR = 60;
    private static final int ENERGIA_CRIA = 25;
    private static final int COSTO_CRIA = 40;
    private static final double CHANCE_CRIA = 0.40;
    private static final int UMBRAL_PELIGRO = 20;

    public Conejo(String nombre, double energia) {
        super(nombre, energia, 6 + (int) (Math.random() * 5), 2.5);
    }

    /** Primero busca comida y despues, si le alcanza la energia, se reproduce. */
    @Override
    public void actuar(Ecosistema eco) {
        comer(eco);
        // hace falta otro conejo vivo y que haya comida de sobra en el ecosistema;
        // aun asi no hay cria todos los turnos
        if (estaVivo() && eco.contarConejos() > 1
                && eco.contarConejos() < eco.contarPlantas()
                && Math.random() < CHANCE_CRIA) {
            intentarReproduccion(eco);
        }
    }

    @Override
    public void comer(Ecosistema eco) {
        Planta planta = eco.buscarPlantaViva();
        if (planta == null) {
            moverse();
            setEnergia(getEnergia() - ENERGIA_SIN_COMIDA);
            eco.registrarEvento("Conejo '" + getNombre() + "' no encontró comida (-"
                    + ENERGIA_SIN_COMIDA + " energia)" + avisoPeligro());
            return;
        }
        // serComida() puede devolver un valor negativo si la planta era venenosa
        int valor = planta.serComida();
        setEnergia(getEnergia() + valor);
        if (valor >= 0) {
            eco.registrarEvento("Conejo '" + getNombre() + "' comió '" + planta.getNombre()
                    + "' (+" + valor + " energia)" + avisoPeligro());
        } else {
            eco.registrarEvento("Conejo '" + getNombre() + "' comió '" + planta.getNombre()
                    + "' y se intoxicó (" + valor + " energia)" + avisoPeligro());
        }
    }

    @Override
    public boolean puedeReproducirse() {
        return estaVivo() && getEnergia() > ENERGIA_PARA_CRIAR;
    }

    @Override
    public void reproducirse(Ecosistema eco) {
        setEnergia(getEnergia() - COSTO_CRIA);
        Conejo cria = new Conejo(eco.nuevoNombreConejo(), ENERGIA_CRIA);
        eco.agregarConejo(cria);
        eco.registrarEvento("Conejo '" + getNombre() + "' tuvo una cría -> nuevo conejo '"
                + cria.getNombre() + "'");
    }

    @Override
    public void mostrarEstado() {
        System.out.println("  Conejo '" + getNombre() + "' | energia: " + (int) getEnergia()
                + (getEnergia() < UMBRAL_PELIGRO ? " | EN PELIGRO" : ""));
    }

    /** Texto que se agrega al evento cuando al conejo le queda poca energia. */
    private String avisoPeligro() {
        if (estaVivo() && getEnergia() < UMBRAL_PELIGRO) {
            return " [PELIGRO: energia=" + (int) getEnergia() + "]";
        }
        return "";
    }

    @Override
    public String getTipo() {
        return "Conejo";
    }
}
