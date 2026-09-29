package ecosistema;

public enum Clima {

    // nombre, reproduccion de plantas, energia que junta la planta,
    // energia extra de conejos, energia extra de lobos, bonus de caza
    SOLEADO("Soleado", 1.5, 12, 5, 0, 0),
    LLUVIOSO("Lluvioso", 2, 15, 3, -5, 0),
    SEQUIA("Sequía", 0.5, 4, -5, 0, 0),
    INVIERNO("Invierno", 0, 0, -8, 0, 0.2);

    private final String nombre;
    private final double factorPlantas;
    private final int energiaPlantas;
    private final int energiaConejos;
    private final int energiaLobos;
    private final double bonusCaza;

    Clima(String nombre, double factorPlantas, int energiaPlantas, int energiaConejos,
            int energiaLobos, double bonusCaza) {
        this.nombre = nombre;
        this.factorPlantas = factorPlantas;
        this.energiaPlantas = energiaPlantas;
        this.energiaConejos = energiaConejos;
        this.energiaLobos = energiaLobos;
        this.bonusCaza = bonusCaza;
    }

    public String getNombre() {
        return nombre;
    }

    public double getFactorPlantas() {
        return factorPlantas;
    }

    public int getEnergiaPlantas() {
        return energiaPlantas;
    }

    public int getEnergiaConejos() {
        return energiaConejos;
    }

    public int getEnergiaLobos() {
        return energiaLobos;
    }

    public double getBonusCaza() {
        return bonusCaza;
    }

    // pasa de la opcion del menu al clima
    public static Clima porNumero(int opcion) {
        if (opcion == 1) {
            return SOLEADO;
        } else if (opcion == 2) {
            return LLUVIOSO;
        } else if (opcion == 3) {
            return SEQUIA;
        }
        return INVIERNO;
    }
}
