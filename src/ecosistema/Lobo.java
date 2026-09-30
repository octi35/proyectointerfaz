package ecosistema;

public class Lobo extends Animal implements Peligroso {

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
        Conejo presa = eco.buscarConejo();
        if (presa == null) {
            eco.registrarEvento("Lobo '" + getNombre() + "' no encontró presas");
            return;
        }
        moverse();
        double probabilidad = probabilidadCaza(eco.getClimaActual());
        int porcentaje = (int) (probabilidad * 100);
        if (Math.random() < probabilidad) {
            presa.morir();
            setEnergia(getEnergia() + 30);
            exitosCaza++;
            eco.registrarEvento("Lobo '" + getNombre() + "' cazó a Conejo '" + presa.getNombre()
                    + "' (+30 energia) [cacerías: " + exitosCaza + ", chance: " + porcentaje + "%]");
        } else {
                        eco.registrarEvento("Lobo '" + getNombre() + "' falló la caza (chance: " + porcentaje + "%)");
        }
    }

    // cuanta mas energia tiene el lobo, mas chances de cazar (en invierno tiene +20%)
    public double probabilidadCaza(Clima clima) {
        double probabilidad = 0.1 + getEnergia() / 300 + clima.getBonusCaza();
        if (probabilidad > 0.9) {
            probabilidad = 0.9;
        }
        return probabilidad;
    }

    @Override
    public void mostrarEstado() {
        System.out.println("  Lobo '" + getNombre() + "' | energia: " + (int) getEnergia()
                + " | cacerías: " + exitosCaza);
    }

    // mas peligroso cuantas mas cacerias tiene y mas energia le queda
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
