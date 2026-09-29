package ecosistema;

public class Conejo extends Animal implements Reproducible {

    public Conejo(String nombre, double energia) {
        super(nombre, energia, 8, 2.5);
    }

    // primero come y despues intenta tener una cria
    @Override
    public void actuar(Ecosistema eco) {
        comer(eco);
        // tiene que haber otro conejo vivo y mas plantas que conejos
        if (estaVivo() && eco.contarConejos() > 1 && eco.contarConejos() < eco.contarPlantas()
                && Math.random() < 0.4) {
            intentarReproduccion(eco);
        }
    }

    @Override
    public void comer(Ecosistema eco) {
        Planta planta = eco.buscarPlanta();
        if (planta == null) {
            moverse();
            setEnergia(getEnergia() - 15);
            eco.registrarEvento("Conejo '" + getNombre() + "' no encontró comida (-15 energia)"
                    + avisoPeligro());
            return;
        }
        // si la planta es venenosa, serComida() devuelve un numero negativo
        int valor = planta.serComida();
        setEnergia(getEnergia() + valor);
        if (valor > 0) {
            eco.registrarEvento("Conejo '" + getNombre() + "' comió '" + planta.getNombre()
                    + "' (+" + valor + " energia)" + avisoPeligro());
        } else {
            eco.registrarEvento("Conejo '" + getNombre() + "' comió '" + planta.getNombre()
                    + "' y se intoxicó (" + valor + " energia)" + avisoPeligro());
        }
    }

    @Override
    public boolean puedeReproducirse() {
        return estaVivo() && getEnergia() > 60;
    }

    // le pasa 30 de energia a la cria
    @Override
    public void reproducirse(Ecosistema eco) {
        setEnergia(getEnergia() - 30);
        Conejo cria = new Conejo(eco.nombreConejo(), 30);
        eco.agregarConejo(cria);
        eco.registrarEvento("Conejo '" + getNombre() + "' tuvo una cría -> nuevo conejo '"
                + cria.getNombre() + "'");
    }

    @Override
    public void mostrarEstado() {
        String peligro = "";
        if (getEnergia() < 20) {
            peligro = " | EN PELIGRO";
        }
        System.out.println("  Conejo '" + getNombre() + "' | energia: " + (int) getEnergia() + peligro);
    }

    // texto que se agrega cuando le queda poca energia
    private String avisoPeligro() {
        if (estaVivo() && getEnergia() < 20) {
            return " [PELIGRO: energia=" + (int) getEnergia() + "]";
        }
        return "";
    }

    @Override
    public String getTipo() {
        return "Conejo";
    }
}
