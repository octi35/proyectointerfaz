# Simulador de Ecosistema

TP de Interfaz Gráfica, 1ra instancia evaluativa.

Es una simulación por turnos que corre en la consola. Hay plantas, conejos y
lobos, vos configurás cómo arranca todo y cada 3 turnos podés meter mano.

## Integrantes

- Octavio Fakiani: Entidad, Planta, PlantaVenenosa y las interfaces
- Tomás Maldonado: Animal, Conejo y Lobo
- Milagros Venzia: Ecosistema, Main y el reporte final

## Cómo ejecutarlo

Abrir la carpeta con NetBeans (File > Open Project). Si al abrirlo pide generar
los archivos de build, decirle que sí. Después Run Project (F6), la clase main
es `ecosistema.Main`.

No usa ninguna librería aparte del JDK. Hace falta JDK 11 o más nuevo.

Si lo querés correr sin NetBeans:

    javac -encoding UTF-8 -d build/classes src/ecosistema/*.java
    java -cp build/classes ecosistema.Main

## Cómo se juega

Primero pide la configuración: plantas (5 a 30), conejos (2 a 15), lobos (1 a 5),
el clima y cuántos turnos (10 a 50). Si ponés cualquier cosa te lo vuelve a pedir.

Después se avanza con Enter. Cada 3 turnos aparece el menú para cambiar el clima,
agregar una entidad o seguir de largo. La partida termina cuando se cumplen los
turnos o cuando se extingue alguna de las tres especies, y ahí sale el reporte
final.

## Archivos

    src/ecosistema/
      Main.java             configuración, loop de turnos y menú de intervención
      Ecosistema.java       las listas de entidades y lo que pasa en cada turno
      Clima.java            enum con los 4 climas y sus efectos
      Entidad.java          clase abstracta base
      Planta.java
      PlantaVenenosa.java   punto bonus
      Animal.java           clase abstracta del medio
      Conejo.java
      Lobo.java
      Reproducible.java     interface, con método default
      Mortal.java           interface, con método default
      Peligroso.java        interface, punto bonus

## Qué pasa en cada turno

1. Las plantas que tienen energía suficiente se reproducen.
2. Los conejos comen una planta. Si no encuentran pierden 15 de energía.
3. Los lobos intentan cazar un conejo. La probabilidad sube con la energía del
   lobo, no es fija.
4. Todos envejecen y gastan energía. Los animales gastan un poco más según lo
   que pesan, y el clima les suma o les resta energía.
5. El que se quedó sin energía muere.
6. Se muestra el estado del ecosistema.

## Dónde está cada cosa de POO

- Clase abstracta con métodos abstractos: `Entidad` (`actuar` y `mostrarEstado`).
- Capa intermedia de herencia: `Animal`, con `moverse()` y `envejecer()` que
  comparten el conejo y el lobo.
- `Reproducible` la implementan Planta y Conejo. En `Ecosistema` se juntan en un
  solo `ArrayList<Reproducible>` para el reporte final.
- `Mortal` la implementan Conejo y Lobo (por Animal), con el default
  `verificarMuerte()`.
- Encapsulamiento: todo private, y los setters validan (la energía no baja de 0
  ni pasa de 150, el tamaño de la planta va de 1 a 5).
- Sobrecarga: `agregarEntidad(tipo)` y `agregarEntidad(tipo, energia)`.
- La planta venenosa va en el mismo `ArrayList<Planta>` que las normales, por eso
  el conejo no la puede distinguir hasta que se la come.

## Bonus que hicimos

- Planta venenosa: en vez de alimentar le saca 30 de energía al conejo.
- Historial de población turno a turno, y en el reporte final dice en qué turno
  cada especie llegó a su máximo y a su mínimo.
- Interface `Peligroso`, los peligrosos salen listados por nivel en el reporte.

## Desafíos

- Al principio la planta se moría apenas se la comían y el ecosistema colapsaba
  en 3 o 4 turnos, no se llegaba a jugar nada. Ahora la planta queda en energía
  mínima pero vuelve a juntar energía cada turno según el clima. En invierno no
  junta nada, así que ahí sí se pueden extinguir.
- Los conejos no se morían nunca de hambre. La energía no puede ser negativa
  (se lleva a 0) y después el bonus del clima los levantaba de vuelta. Se
  arregló aplicando el clima solo si al animal todavía le queda algo.
- Las plantas llegaban a más de 250 y la consola quedaba ilegible. Se puso un
  tope de 40 plantas y los conejos solo tienen cría si hay más plantas que
  conejos.
- Los lobos juntaban energía sin límite y no fallaban una caza, por eso la
  energía tiene un techo de 150.
- Las plantas venenosas no se reproducen, para que no se llene todo de veneno.

## Documentación

Las capturas y los links de cada uno están en la carpeta `documentacion/`.
