# Simulador de Ecosistema

Trabajo práctico de Interfaz Gráfica (1° instancia evaluativa). Es una
simulación por turnos que corre en la terminal: hay plantas, conejos y lobos
que interactúan entre sí, y el jugador configura el escenario inicial y puede
intervenir cada 3 turnos.

## Cómo ejecutarlo

### Desde NetBeans

1. Abrir NetBeans (probado con NetBeans 17 y 21) y elegir *File > Open Project*.
2. Seleccionar la carpeta del repositorio. Aparece como proyecto Java
   `SimuladorEcosistema`.
3. Si NetBeans avisa que faltan archivos de build, aceptar que los genere:
   `nbproject/build-impl.xml` se arma solo la primera vez que se abre.
4. Ejecutar con *Run Project* (F6). La clase principal es `ecosistema.Main`.

No usa librerías externas, solo el JDK. Hace falta JDK 11 o superior.

### Desde la consola

```
javac -encoding UTF-8 -d build/classes src/ecosistema/*.java
java -cp build/classes ecosistema.Main
```

## Cómo se juega

Al arrancar pide la configuración inicial (con validación de rangos):

| Dato | Rango |
|------|-------|
| Plantas | 5 a 30 |
| Conejos | 2 a 15 |
| Lobos | 1 a 5 |
| Clima | Soleado, Lluvioso, Sequía o Invierno |
| Turnos | 10 a 50 |

Después se avanza turno a turno con Enter. Cada 3 turnos aparece el menú de
intervención para cambiar el clima, agregar una entidad o simplemente seguir.
La partida termina cuando se cumplen los turnos o cuando se extingue alguna de
las tres poblaciones, y ahí se imprime el reporte final.

## Estructura del proyecto

```
src/ecosistema/
  Main.java             configuración inicial, loop de turnos y menú de intervención
  Ecosistema.java       contiene las listas de entidades y procesa cada turno
  Clima.java            enum con los cuatro climas y sus efectos
  Entidad.java          clase abstracta base (nombre, energía, edad, viva)
  Planta.java           extiende Entidad, implementa Reproducible
  PlantaVenenosa.java   extiende Planta, implementa Peligroso (punto bonus)
  Animal.java           clase abstracta intermedia, implementa Mortal
  Conejo.java           extiende Animal, implementa Reproducible
  Lobo.java             extiende Animal, implementa Peligroso
  Reproducible.java     interface con método default intentarReproduccion()
  Mortal.java           interface con método default verificarMuerte()
  Peligroso.java        interface con getNivelPeligro() (punto bonus)
```

## Cómo funciona cada turno

1. Las plantas con energía suficiente se reproducen (el clima define la chance).
2. Los conejos buscan una planta y la comen. Si no encuentran pierden 15 de energía.
3. Los lobos intentan cazar un conejo. La probabilidad sube con la energía del lobo.
4. Todas las entidades envejecen y gastan energía. Los animales gastan un poco más
   según lo que pesan, y las plantas rebrotan lo que les da el clima.
5. Las entidades que quedaron sin energía mueren.
6. Se muestra el estado del ecosistema.

## Decisiones que tomamos

- **Las plantas no mueren al ser comidas.** `serComida()` las deja en energía
  mínima como pide la consigna, pero si el clima les permite rebrotar antes del
  final del turno sobreviven. Con las plantas muriendo de una, el ecosistema
  colapsaba en 3 o 4 turnos siempre y no se llegaba a jugar nada.
- **Tope de 30 plantas en el terreno.** Sin un límite las plantas llegaban a 250
  y la pantalla quedaba ilegible.
- **Los conejos no tienen cría todos los turnos.** Piden más de 60 de energía,
  otro conejo vivo, que haya más plantas que conejos, y además hay un 40% de
  chance. Sin eso se duplicaban turno a turno.
- **El clima no revive animales.** La energía se lleva a 0 cuando baja de cero
  (como pide la consigna), así que el bonus de energía del clima se aplica solo
  si al animal todavía le queda algo. Si no, un conejo hambriento nunca moría.
- **Techo de 150 de energía**, para que los lobos no se vuelvan invencibles.

## Requisitos de POO

| Requisito | Dónde está |
|-----------|-----------|
| Clase abstracta con métodos abstractos | `Entidad` (`actuar`, `mostrarEstado`) |
| Capa intermedia de herencia | `Animal`, con `moverse()` y `envejecer()` compartidos |
| Interface implementada por dos clases | `Reproducible` en `Planta` y `Conejo` |
| Método default aprovechado | `intentarReproduccion()` y `verificarMuerte()` |
| Polimorfismo | `ArrayList<Reproducible>` y `ArrayList<Peligroso>` en `Ecosistema`; las plantas venenosas viajan en el mismo `ArrayList<Planta>` |
| Encapsulamiento | todos los atributos `private`, con setters que validan |
| Sobrecarga | `agregarEntidad(tipo)` / `agregarEntidad(tipo, energia)` y `verificarMuerte()` / `verificarMuerte(eco)` |

## Puntos bonus incluidos

- Planta venenosa: el conejo pierde 30 de energía y no puede distinguirla antes
  de comerla.
- Historial de población turno a turno, con el máximo y el mínimo de cada
  especie en el reporte final.
- Interface `Peligroso`, con los elementos peligrosos listados por nivel.

## Integrantes

| Integrante | Rol |
|------------|-----|
| | |
| | |
| | |

## Documentación

Las capturas y los links de las consultas de cada integrante están en la
carpeta `documentacion/`.
