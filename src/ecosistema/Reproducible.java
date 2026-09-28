package ecosistema;

/**
 * La implementan las entidades capaces de dejar descendencia.
 *
 * Gracias a esta interface el ecosistema puede meter plantas y conejos en una
 * misma lista y recorrerlos con el mismo codigo.
 */
public interface Reproducible {

    /** Crea la cria y la suma al ecosistema. */
    void reproducirse(Ecosistema eco);

    /** Indica si la entidad esta en condiciones de reproducirse. */
    boolean puedeReproducirse();

    /** Chequea la condicion y, si se cumple, reproduce a la entidad. */
    default void intentarReproduccion(Ecosistema eco) {
        if (puedeReproducirse()) {
            reproducirse(eco);
        }
    }
}
