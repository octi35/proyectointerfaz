package ecosistema;

/**
 * Capa intermedia entre Entidad y los animales concretos.
 *
 * Agrega lo que comparten conejos y lobos: se mueven, comen y pueden morir.
 */
public abstract class Animal extends Entidad implements Mortal {

    private int velocidad;
    private double peso;

    public Animal(String nombre, double energia, int velocidad, double peso) {
        super(nombre, energia);
        setVelocidad(velocidad);
        setPeso(peso);
    }

    /** Cada animal busca su comida de una forma distinta. */
    public abstract void comer(Ecosistema eco);

    /** Desplazamiento comun a todos los animales. */
    public void moverse() {
        System.out.println("  " + getTipo() + " '" + getNombre() + "' se desplazó buscando comida");
    }

    /** Ademas de la energia base, un animal gasta segun lo que pesa. */
    @Override
    public void envejecer() {
        super.envejecer();
        setEnergia(getEnergia() - peso / 10);
    }

    @Override
    public boolean estaVivo() {
        return isViva();
    }

    @Override
    public void morir() {
        setViva(false);
        setEnergia(0);
    }

    public int getVelocidad() {
        return velocidad;
    }

    public final void setVelocidad(int velocidad) {
        if (velocidad > 0) {
            this.velocidad = velocidad;
        } else {
            this.velocidad = 1;
        }
    }

    public double getPeso() {
        return peso;
    }

    public final void setPeso(double peso) {
        if (peso > 0) {
            this.peso = peso;
        } else {
            this.peso = 1;
        }
    }
}
