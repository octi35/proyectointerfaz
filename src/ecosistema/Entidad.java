package ecosistema;

public abstract class Entidad {

    private static final double ENERGIA_BASE = 2;
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

    public abstract void actuar(Ecosistema eco);

    public abstract void mostrarEstado();

    // suma un turno de edad y gasta la energia base
    public void envejecer() {
        edad++;
        setEnergia(energia - ENERGIA_BASE);
    }

    // cada clase hija lo cambia para mostrar su tipo
    public String getTipo() {
        return "Entidad";
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            this.nombre = "Sin nombre";
        } else {
            this.nombre = nombre.trim();
        }
    }

    public double getEnergia() {
        return energia;
    }

    // la energia no puede ser negativa (se lleva a 0) ni pasar de 150
    public void setEnergia(double energia) {
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
