package ecosistema;

/**
 * Planta que en lugar de alimentar intoxica al conejo que la come.
 *
 * Se guarda en el mismo ArrayList<Planta> que las plantas normales, asi que el
 * conejo no puede distinguirla hasta que ya se la comió.
 */
public class PlantaVenenosa extends Planta implements Peligroso {

    /** Energia que le saca al conejo que la come. */
    private static final int DANIO = 30;

    public PlantaVenenosa(String nombre, double energia, int tamanio) {
        super(nombre, energia, tamanio);
    }

    /** Devuelve un valor negativo: el conejo pierde energia en vez de ganar. */
    @Override
    public int serComida() {
        super.serComida();
        return -DANIO;
    }

    @Override
    protected Planta crearHija(Ecosistema eco, double energia) {
        return new PlantaVenenosa(eco.nuevoNombreVenenosa(), energia, 1 + (int) (Math.random() * 5));
    }

    @Override
    public int getNivelPeligro() {
        return DANIO;
    }

    @Override
    public String getTipo() {
        return "Planta venenosa";
    }
}
