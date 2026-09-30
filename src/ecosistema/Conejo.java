package ecosistema;

public class Conejo extends Animal implements Reproducible {

    // valores de las reglas del conejo, asi no quedan numeros sueltos en el codigo
    private static final int PERDIDA_SIN_COMIDA = 15;
    private static final int ENERGIA_PARA_REPRODUCIRSE = 60;
    private static final int ENERGIA_CRIA = 30;
    private static final int LIMITE_PELIGRO = 20;

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
            setEnergia(getEnergia() - PERDIDA_SIN_COMIDA);
            eco.registrarEvento("Conejo '" + getNombre() + "' no encontró comida (-"
                    + PERDIDA_SIN_COMIDA + " energia)" + avisoPeligro());
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
        return estaVivo() && getEnergia() > ENERGIA_PARA_REPRODUCIRSE;
    }

    // le pasa parte de su energia a la cria
    @Override
    public void reproducirse(Ecosistema eco) {
        setEnergia(getEnergia() - ENERGIA_CRIA);
        Conejo cria = new Conejo(eco.nombreConejo(), ENERGIA_CRIA);
        eco.agregarConejo(cria);
        eco.registrarEvento("Conejo '" + getNombre() + "' tuvo una cría -> nuevo conejo '"
                + cria.getNombre() + "'");
    }

    @Override
    public void mostrarEstado() {
        String peligro = "";
        if (estaEnPeligro()) {
            peligro = " | EN PELIGRO";
        }
        System.out.println("  Conejo '" + getNombre() + "' | energia: " + (int) getEnergia() + peligro);
    }

    // true si le queda menos de 20 de energia
    public boolean estaEnPeligro() {
        return getEnergia() < LIMITE_PELIGRO;
    }

    // texto que se agrega cuando le queda poca energia
    private String avisoPeligro() {
        if (estaVivo() && estaEnPeligro()) {
            return " [PELIGRO: energia=" + (int) getEnergia() + "]";
        }
        return "";
    }

    @Override
    public String getTipo() {
        return "Conejo";
    }
}
