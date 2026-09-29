package ecosistema;

public class PlantaVenenosa extends Planta implements Peligroso {

    public PlantaVenenosa(String nombre, double energia, int tamanio) {
        super(nombre, energia, tamanio);
    }

    // en vez de dar energia, se la saca al conejo
    @Override
    public int serComida() {
        super.serComida();
        return -30;
    }

    // las venenosas no se reproducen
    @Override
    public boolean puedeReproducirse() {
        return false;
    }

    @Override
    public int getNivelPeligro() {
        return 30;
    }

    @Override
    public String getTipo() {
        return "Planta venenosa";
    }
}
