package ecosistema;

/**
 * Planta del ecosistema. Junta energia con el sol y sirve de comida a los conejos.
 */
public class Planta extends Entidad implements Reproducible {

    /** Energia minima para poder reproducirse. */
    private static final int ENERGIA_PARA_REPRODUCIRSE = 25;

    private int tamanio;

    public Planta(String nombre, double energia, int tamanio) {
        super(nombre, energia);
        setTamanio(tamanio);
    }

    /** El clima y el lugar disponible deciden si este turno hay chances. */
    @Override
    public void actuar(Ecosistema eco) {
        double factor = eco.getClimaActual().getFactorPlanta();
        if (factor > 0 && eco.hayLugarParaPlantas() && Math.random() < 0.35 * factor) {
            intentarReproduccion(eco);
        }
    }

    @Override
    public boolean puedeReproducirse() {
        return isViva() && getEnergia() >= ENERGIA_PARA_REPRODUCIRSE;
    }

    /** La planta parte su energia al medio y esa mitad se va con la hija. */
    @Override
    public void reproducirse(Ecosistema eco) {
        double energiaHija = getEnergia() / 2;
        setEnergia(getEnergia() - energiaHija);
        Planta hija = crearHija(eco, energiaHija);
        eco.agregarPlanta(hija);
        eco.registrarEvento(getTipo() + " '" + getNombre() + "' se reprodujo -> nueva planta '"
                + hija.getNombre() + "' (energia: " + (int) energiaHija + ")");
    }

    /**
     * Vuelve a juntar energia al final del turno. Es lo que le permite a una
     * planta pastoreada seguir viva, siempre que el clima la acompañe.
     */
    public void rebrotar(Clima clima) {
        setEnergia(getEnergia() + clima.getEnergiaPlanta());
    }

    /**
     * Arma la cria. Cada subclase devuelve una planta de su propio tipo.
     */
    protected Planta crearHija(Ecosistema eco, double energia) {
        return new Planta(eco.nuevoNombrePlanta(), energia, 1 + (int) (Math.random() * 5));
    }

    @Override
    public void mostrarEstado() {
        System.out.println("  " + getTipo() + " '" + getNombre() + "' | tamaño: " + tamanio
                + " | energia: " + (int) getEnergia());
    }

    /**
     * La planta se queda sin energia y devuelve lo que alimenta al que la comió.
     * Si el clima le permite rebrotar antes del final del turno sobrevive; si no,
     * se queda en cero y muere.
     */
    public int serComida() {
        setEnergia(0);
        return tamanio * 10;
    }

    @Override
    public String getTipo() {
        return "Planta";
    }

    public int getTamanio() {
        return tamanio;
    }

    /** El tamaño solo puede ir de 1 a 5, cualquier otro valor se acomoda. */
    public final void setTamanio(int tamanio) {
        if (tamanio < 1) {
            this.tamanio = 1;
        } else if (tamanio > 5) {
            this.tamanio = 5;
        } else {
            this.tamanio = tamanio;
        }
    }
}
