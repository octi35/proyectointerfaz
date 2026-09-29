package ecosistema;

public interface Mortal {

    boolean estaVivo();

    void morir();

    // estos tres ya los tiene Entidad, se piden para poder usarlos en el default
    double getEnergia();

    String getNombre();

    String getTipo();

    default void verificarMuerte() {
        if (estaVivo() && getEnergia() <= 0) {
            morir();
            System.out.println("  " + getTipo() + " '" + getNombre() + "' murió de inanición");
        }
    }
}
