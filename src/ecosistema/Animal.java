package ecosistema;

public abstract class Animal extends Entidad implements Mortal {

    private int velocidad;
    private double peso;

    public Animal(String nombre, double energia, int velocidad, double peso) {
        super(nombre, energia);
        setVelocidad(velocidad);
        setPeso(peso);
    }

    public abstract void comer(Ecosistema eco);

        // la velocidad dice cuantos metros avanza en un turno
    public void moverse() {
        System.out.println("  " + getTipo() + " '" + getNombre() + "' se desplazó "
                + velocidad + " metros");
    }

    // ademas de la energia base, gasta un poco mas segun su peso
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

    public void setVelocidad(int velocidad) {
        if (velocidad > 0) {
            this.velocidad = velocidad;
        } else {
            this.velocidad = 1;
        }
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        if (peso > 0) {
            this.peso = peso;
        } else {
            this.peso = 1;
        }
    }
}
