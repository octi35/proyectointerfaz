package modelo;

public interface Mortal {

    boolean estaVivo();

    void morir();

    // estos tres ya los tiene Entidad, se piden para poder usarlos en el default
    double getEnergia();

    String getNombre();

    String getTipo();

    // devuelve true si murió en este momento, asi el ecosistema registra el evento
    default boolean verificarMuerte() {
        if (estaVivo() && getEnergia() <= 0) {
            morir();
            return true;
        }
        return false;
    }
}