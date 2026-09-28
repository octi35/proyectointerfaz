package ecosistema;

/**
 * Clase base de todas las entidades que viven en el ecosistema.
 *
 * Guarda lo que tienen en comun (nombre, energia, edad y si sigue viva) y deja
 * abiertos los dos comportamientos que cada tipo resuelve a su manera.
 */
public abstract class Entidad {

    /** Energia que cuesta estar vivo un turno. */
    private static final double ENERGIA_BASE = 2;

    /** Techo de energia: ninguna entidad puede acumular mas que esto. */
    private static final double ENERGIA_MAXIMA = 150;

    private String nombre;
    private double energia;
    private int edad;
    private boolean viva;

    public Entidad(String nombre, double energia) {
        setNombre(nombre);
        setEnergia(energia);
        this.edad = 0;
        this.viva = true;
    }

    /** Lo que hace la entidad en su turno. */
    public abstract void actuar(Ecosistema eco);

    /** Muestra por pantalla como esta la entidad. */
    public abstract void mostrarEstado();

    /** Suma un turno de vida y descuenta la energia que cuesta existir. */
    public void envejecer() {
        edad++;
        setEnergia(energia - ENERGIA_BASE);
    }

    /** Etiqueta del tipo, se usa en los mensajes de pantalla. */
    public String getTipo() {
        return "Entidad";
    }

    public String getNombre() {
        return nombre;
    }

    public final void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            this.nombre = "Sin nombre";
        } else {
            this.nombre = nombre.trim();
        }
    }

    public double getEnergia() {
        return energia;
    }

    /** La energia nunca queda negativa ni pasa el techo permitido. */
    public final void setEnergia(double energia) {
        if (energia < 0) {
            this.energia = 0;
        } else if (energia > ENERGIA_MAXIMA) {
            this.energia = ENERGIA_MAXIMA;
        } else {
            this.energia = energia;
        }
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        if (edad >= 0) {
            this.edad = edad;
        }
    }

    public boolean isViva() {
        return viva;
    }

    public void setViva(boolean viva) {
        this.viva = viva;
    }
}
