# Diseño de la interfaz (Figma)

**Link al archivo de Figma:** PENDIENTE (pegar acá el link cuando esté el archivo)

Este documento describe las pantallas, los estados y las decisiones de diseño
del Simulador de Ecosistema. Es la guía para armar el archivo de Figma y
coincide con la interfaz que está programada en el paquete `vista`.

## Quién usa la aplicación y para qué

**Lucía, 20 años, estudiante de Biología.** Cursa Ecología y quiere entender
cómo cambia una cadena alimentaria (plantas, conejos, lobos) según el clima y
según lo que hace una persona. No sabe programar y no quiere leer reglas largas:
quiere cargar unos números, apretar un botón y ver qué pasa en cada turno.

**Escenario.** Lucía abre la aplicación antes de una clase, configura 10 plantas,
6 conejos y 2 lobos con clima soleado y 20 turnos, y avanza turno por turno
mirando quién come a quién. En el turno 3 cambia el clima a invierno para ver si
los conejos sobreviven. Al final mira el reporte para sacar conclusiones.

## Pantallas

Todas son de 1280 x 720, con la misma barra de navegación arriba
(`1. Configuración`, `2. Simulación`, `3. Reporte final`).

| # | Pantalla | Estados que hay que dibujar |
|---|----------|-----------------------------|
| 1 | Configuración | normal (valores por defecto) · error de formato (letras) · error de campo vacío · error de rango · confirmación "Hay una simulación en curso" |
| 2 | Simulación | sin datos (no hay simulación) · turno sin intervención · turno de intervención disponible · intervención ya usada (confirmación en la barra de estado) · error "ya hubo 5 lobos" · simulación terminada |
| 3 | Reporte final | sin datos (todavía no terminó) · reporte completo |

### Distribución de la pantalla de simulación

| Zona | Contenido |
|------|-----------|
| Norte | Contadores: turno, clima, plantas, conejos, lobos |
| Centro | Tabla de entidades vivas (tipo, nombre, energía, edad, detalle) |
| Este | Lista de eventos del turno |
| Sur | Panel de intervención, botones `Avanzar turno` / `Ver reporte final` / `Nueva simulación` y barra de estado |

## Estilo

- Tipografía: Segoe UI. Títulos 26 negrita, contadores 17 negrita, texto 15, tabla y eventos 13.
- Color de error: rojo `#CC0000`. El resto usa los grises neutros del tema Nimbus.
- Botón principal de cada pantalla: más grande y en negrita (`Iniciar simulación`, `Avanzar turno`, `Nueva simulación`).

## Decisiones de diseño y por qué

1. **El estado siempre a la vista (visibilidad del estado del sistema).** Los
   contadores quedan fijos arriba, la tabla es lo más grande de la pantalla y los
   eventos están al costado. Así Lucía no tiene que buscar nada después de cada
   turno: ve cuántos hay de cada especie, quién está vivo y qué pasó, sin cambiar
   de pantalla.

2. **Se bloquea lo que no se puede hacer y se explica por qué (prevención de
   errores y feedback).** Los controles de intervención están deshabilitados salvo
   cada 3 turnos, y arriba de ellos una frase dice si se puede intervenir o en
   cuántos turnos vuelve a poderse. Lo mismo pasa con `Avanzar turno` cuando la
   simulación terminó. En vez de dejar que se equivoque y mostrarle un error,
   el diseño no le deja el camino abierto.

3. **Los errores se muestran en el lugar y con el dato exacto (reconocer y
   recuperarse de errores).** En la configuración, el mensaje aparece en rojo
   justo arriba del botón e indica el campo y el rango ("Cantidad de lobos: tiene
   que estar entre 1 y 5"). Las acciones que hacen perder la simulación en curso
   piden confirmación antes de ejecutarse.

4. **Mismo esquema en las tres pantallas (consistencia).** Barra de navegación
   numerada en el mismo lugar, título grande centrado, botón principal en negrita
   y la misma tipografía. Las tres pantallas se sienten parte de lo mismo y la
   persona siempre sabe en qué paso del recorrido está.

## Si algo cambió al programarla

Completar acá después de armar el Figma, si algún elemento del diseño se
resolvió distinto en la aplicación y por qué.

## Prototipo navegable (bonus)

Conectar en Figma: Configuración → Simulación → Reporte final, y los botones de
la barra de navegación entre las tres.
