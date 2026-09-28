package ecosistema;

/**
 * Climas posibles del ecosistema.
 *
 * Cada clima guarda como afecta a las tres poblaciones: cuanto favorece la
 * reproduccion de las plantas, cuanta energia gana o pierde cada animal por
 * turno y cuanto extra de probabilidad de caza tienen los lobos.
 */
public enum Clima {

    SOLEADO("Soleado", 1.5, 12, 5, 0, 0.0),
    LLUVIOSO("Lluvioso", 2.0, 15, 3, -5, 0.0),
    SEQUIA("Sequía", 0.5, 4, -5, 0, 0.0),
    INVIERNO("Invierno", 0.0, 3, -8, 0, 0.20);

    private final String nombre;
    private final double factorPlanta;
    private final int energiaPlanta;
    private final int ajusteConejo;
    private final int ajusteLobo;
    private final double bonusCaza;

    Clima(String nombre, double factorPlanta, int energiaPlanta, int ajusteConejo,
            int ajusteLobo, double bonusCaza) {
        this.nombre = nombre;
        this.factorPlanta = factorPlanta;
        this.energiaPlanta = energiaPlanta;
        this.ajusteConejo = ajusteConejo;
        this.ajusteLobo = ajusteLobo;
        this.bonusCaza = bonusCaza;
    }

    public String getNombre() {
        return nombre;
    }

    /** Multiplica la chance de que una planta se reproduzca. En 0 no se reproduce ninguna. */
    public double getFactorPlanta() {
        return factorPlanta;
    }

    /** Energia que junta una planta por turno (fotosintesis). */
    public int getEnergiaPlanta() {
        return energiaPlanta;
    }

    public int getAjusteConejo() {
        return ajusteConejo;
    }

    public int getAjusteLobo() {
        return ajusteLobo;
    }

    public double getBonusCaza() {
        return bonusCaza;
    }

    /** Traduce la opcion del menu al clima correspondiente. */
    public static Clima porNumero(int opcion) {
        switch (opcion) {
            case 1:
                return SOLEADO;
            case 2:
                return LLUVIOSO;
            case 3:
                return SEQUIA;
            default:
                return INVIERNO;
        }
    }
}
