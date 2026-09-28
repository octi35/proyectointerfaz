package ecosistema;

/**
 * Lobo: caza conejos. No se reproduce, solo puede aparecer por intervención
 * del jugador.
 */
public class Lobo extends Animal implements Peligroso {

    /** Energia que gana el lobo por cada caza exitosa. */
    private static final int ENERGIA_PRESA = 30;

    private int exitosCaza;

    public Lobo(String nombre, double energia) {
        super(nombre, energia, 12, 35);
        this.exitosCaza = 0;
    }

    @Override
    public void actuar(Ecosistema eco) {
        comer(eco);
    }

    @Override
    public void comer(Ecosistema eco) {
        Conejo presa = eco.buscarConejoVivo();
        if (presa == null) {
            eco.registrarEvento("Lobo '" + getNombre() + "' no encontró presas");
            return;
        }
        moverse();
        if (Math.random() < calcularProbabilidadCaza(presa, eco.getClimaActual())) {
            presa.morir();
            setEnergia(getEnergia() + ENERGIA_PRESA);
            exitosCaza++;
            eco.registrarEvento("Lobo '" + getNombre() + "' cazó a Conejo '" + presa.getNombre()
                    + "' (+" + ENERGIA_PRESA + " energia) [cacerías: " + exitosCaza + "]");
        } else {
            eco.registrarEvento("Lobo '" + getNombre() + "' falló la caza");
        }
    }

    /**
     * Cuanta mas energia tiene el lobo mas chances tiene de cazar. Tambien
     * influye la diferencia de velocidad con la presa y el clima.
     */
    public double calcularProbabilidadCaza(Conejo presa, Clima clima) {
        double probabilidad = 0.10
                + getEnergia() / 400.0
                + (getVelocidad() - presa.getVelocidad()) / 100.0
                + clima.getBonusCaza();
        if (probabilidad < 0.05) {
            return 0.05;
        }
        if (probabilidad > 0.75) {
            return 0.75;
        }
        return probabilidad;
    }

    @Override
    public void mostrarEstado() {
        System.out.println("  Lobo '" + getNombre() + "' | energia: " + (int) getEnergia()
                + " | cacerías: " + exitosCaza);
    }

    /** Un lobo es mas peligroso cuanto mas cazó y mas energia le queda. */
    @Override
    public int getNivelPeligro() {
        return exitosCaza * 10 + (int) (getEnergia() / 10);
    }

    @Override
    public String getTipo() {
        return "Lobo";
    }

    public int getExitosCaza() {
        return exitosCaza;
    }

    public void setExitosCaza(int exitosCaza) {
        if (exitosCaza >= 0) {
            this.exitosCaza = exitosCaza;
        }
    }
}
