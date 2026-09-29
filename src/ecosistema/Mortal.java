package ecosistema;

/**
 * La implementan las entidades que pueden morir por quedarse sin energia.
 */
public interface Mortal {

    boolean estaVivo();

    void morir();

    double getEnergia();

    String getNombre();

    String getTipo();

    /** Mata a la entidad si se quedo sin energia y muestra el evento. */
    default void verificarMuerte() {
        if (estaVivo() && getEnergia() <= 0) {
            morir();
            System.out.println("  " + getTipo() + " '" + getNombre() + "' murió de inanición");
        }
    }

    /** Misma logica, pero dejando anotado el evento en el turno. */
    default void verificarMuerte(Ecosistema eco) {
        if (estaVivo() && getEnergia() <= 0) {
            morir();
            eco.registrarEvento(getTipo() + " '" + getNombre() + "' murió de inanición");
        }
    }
}
