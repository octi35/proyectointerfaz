package ecosistema;

public class Planta extends Entidad implements Reproducible {

    private int tamanio;

    public Planta(String nombre, double energia, int tamanio) {
        super(nombre, energia);
        setTamanio(tamanio);
    }

    // el clima decide cuanta chance tiene de reproducirse
    @Override
    public void actuar(Ecosistema eco) {
        double chance = 0.3 * eco.getClimaActual().getFactorPlantas();
        if (eco.hayLugarParaPlantas() && Math.random() < chance) {
            intentarReproduccion(eco);
        }
    }

    // la planta junta energia segun el clima (asi puede volver a crecer despues de que se la comen)
    public void crecer(Clima clima) {
        setEnergia(getEnergia() + clima.getEnergiaPlantas());
    }

    @Override
    public boolean puedeReproducirse() {
        return isViva() && getEnergia() >= 25;
    }

    // la hija se lleva la mitad de la energia
    @Override
    public void reproducirse(Ecosistema eco) {
        double energiaHija = getEnergia() / 2;
        setEnergia(getEnergia() - energiaHija);
        Planta hija = new Planta(eco.nombrePlanta(), energiaHija, 1 + (int) (Math.random() * 5));
        eco.agregarPlanta(hija);
        eco.registrarEvento("Planta '" + getNombre() + "' se reprodujo -> nueva planta '"
                + hija.getNombre() + "' (energia: " + (int) energiaHija + ")");
    }

    @Override
    public void mostrarEstado() {
        System.out.println("  " + getTipo() + " '" + getNombre() + "' | tamaño: " + tamanio
                + " | energia: " + (int) getEnergia());
    }

    // queda con la energia al minimo y devuelve cuanto alimenta
    public int serComida() {
        setEnergia(0);
        return tamanio * 10;
    }

    @Override
    public String getTipo() {
        return "Planta";
    }

    public int getTamanio() {
        return tamanio;
    }

    // el tamaño va de 1 a 5
    public void setTamanio(int tamanio) {
        if (tamanio < 1) {
            this.tamanio = 1;
        } else if (tamanio > 5) {
            this.tamanio = 5;
        } else {
            this.tamanio = tamanio;
        }
    }
}
