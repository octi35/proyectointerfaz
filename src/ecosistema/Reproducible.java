package ecosistema;

public interface Reproducible {

    void reproducirse(Ecosistema eco);

    boolean puedeReproducirse();

    default void intentarReproduccion(Ecosistema eco) {
        if (puedeReproducirse()) {
            reproducirse(eco);
        }
    }
}
