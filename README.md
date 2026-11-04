# Simulador de Ecosistema

TP de Interfaz Gráfica, 2da instancia evaluativa.

Es la simulación por turnos de la primera instancia, ahora como aplicación de
escritorio con interfaz Swing y arquitectura MVC. Hay plantas, conejos y lobos:
configurás cómo arranca todo desde una ventana, avanzás turno por turno y cada
3 turnos podés cambiar el clima o agregar entidades.

## Integrantes y rol

- Octavio Fakiani: paquete `vista`, paquete `controlador`, paquete `principal` (main) y diseño en Figma
- Tomás Maldonado: paquete `modelo` (separación en paquetes, modelo sin consola, eventos y reporte como datos)
- Milagros Venezia: paquete `dao` (guardado y retoma en archivo de texto) y su conexión con el controlador

## Links

- Figma: - Figma: https://www.figma.com/design/yvr9pMYwmPPuTdrnEuuj57 
- Video explicativo: https://drive.google.com/file/d/1V_TCscuG4SCfmadPzIjKLwLXMTqt15Xu/view?usp=drive_link

## Cómo ejecutarlo

Abrir la carpeta con NetBeans (File > Open Project). Si al abrirlo pide generar
los archivos de build, decirle que sí. Después Run Project (F6), la clase main
es `principal.Main`.

Si el diseñador (pestaña Design) dice que no encuentra alguna clase, hacer
Clean and Build una vez y volver a abrirlo.

Versiones: JDK 11 o más nuevo. No usa ninguna librería aparte del JDK.

Sin NetBeans:

    javac -encoding UTF-8 -d build/classes $(find src -name '*.java')
    java -cp build/classes principal.Main

## Estructura del proyecto

    src/
      principal/
        Main.java               crea la ventana y el controlador, los conecta y muestra la ventana
      vista/                    solo muestra y recoge datos (armada con el diseñador de NetBeans)
        VentanaPrincipal        JFrame con la barra de navegación y un CardLayout con las 3 pantallas
        PanelConfiguracion      pantalla 1: formulario de configuración inicial
        PanelSimulacion         pantalla 2: contadores, tabla de entidades, eventos e intervención
        PanelReporte            pantalla 3: reporte final
      controlador/
        Controlador.java        recibe las acciones, valida, coordina el modelo y le entrega datos a la vista
        VistaSimulador.java     interfaz con lo que el controlador le pide a la vista (sin Swing)
      dao/
        EcosistemaDAO.java      única clase que lee y escribe simulacion.txt (sin Swing)
      modelo/                   reglas de la simulación (entidades, clima, ecosistema), sin Swing ni consola
        Entidad, Planta, PlantaVenenosa, Animal, Conejo, Lobo, Clima, Ecosistema,
        Reproducible, Mortal, Peligroso
    documentacion/              prompts, conversaciones y diseño
      figma/DISENO.md           persona, pantallas, estados y decisiones de diseño

Cada pantalla de la vista son dos archivos: el `.java` y el `.form` del
diseñador de NetBeans. Todo lo que está entre `GEN-BEGIN` y `GEN-END` lo genera
el diseñador; el resto del código solo carga datos, habilita o deshabilita
componentes, cambia de pantalla y muestra mensajes.

## Cómo se comunican las capas

Al apretar "Avanzar turno":

1. La vista llama a `controlador.avanzarTurno()`.
2. El controlador le pide al modelo que procese el turno (`procesarTurno()`).
3. El modelo devuelve la lista de eventos del turno.
4. El controlador arma los datos (contadores, filas de la tabla, eventos, estado
   de la intervención) y se los entrega a la vista.
5. Si terminó la simulación (turnos completos o colapso), habilita "Ver reporte final".

La vista no importa nada del modelo y el controlador no usa nada de Swing: el
controlador conoce a la vista solo por la interfaz `VistaSimulador`.

## Cómo se juega

Primero se completa la configuración: plantas (5 a 30), conejos (2 a 15), lobos
(1 a 5), el clima y cuántos turnos (10 a 50). El controlador valida los datos y,
si algo está mal, escribe el error debajo del formulario.

Después se avanza con "Avanzar turno". Cada 3 turnos se habilita el panel de
intervención: se puede cambiar el clima o agregar una entidad (una sola cosa por
intervención). El máximo es de 5 lobos en toda la simulación, contando los
iniciales. La partida termina cuando se cumplen los turnos o cuando se extingue
alguna de las tres especies, y ahí se habilita el reporte final.

"Nueva simulación" pide confirmación si hay una en curso.
## Guardado y retoma

La simulación se guarda sola en `simulacion.txt`, en la carpeta del proyecto
(ruta relativa), después de configurarla, después de cada turno y después de
cada intervención. Al abrir la aplicación, si el archivo existe, se retoma en
el mismo turno. "Nueva simulación" borra el archivo.

Formato: una línea por dato, campos separados por `;`

    SIMULACION;turnoActual;turnosTotales;CLIMA;nacPlantas;nacConejos;lobosAgregados
    HISTORIAL;plantas;conejos;lobos
    EVENTOS;cantidad
    PLANTA;nombre;energia;edad;viva;tamanio      (VENENOSA igual)
    CONEJO;nombre;energia;edad;viva;velocidad
    LOBO;nombre;energia;edad;viva;cacerias

Se guardan también las entidades muertas porque el reporte final las necesita.
Si el archivo no existe o tiene datos inválidos, la aplicación no se rompe:
avisa y deja empezar una simulación nueva.

## Qué pasa en cada turno

1. Las plantas que tienen energía suficiente se reproducen.
2. Los conejos comen una planta. Si no encuentran pierden 15 de energía.
3. Los lobos intentan cazar un conejo. La probabilidad sube con la energía del
   lobo, no es fija.
4. Todos envejecen y gastan energía. Los animales gastan un poco más según lo
   que pesan, y el clima les suma o les resta energía.
5. El que se quedó sin energía muere.
6. Se muestra el estado del ecosistema y los eventos del turno.

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

- Planta venenosa: en vez de alimentar le saca 30 de energía al conejo. En la
  tabla aparece como "Planta venenosa" y en los eventos se registra cuando un
  conejo la come.
- Historial de población turno a turno, y en el reporte final dice en qué turno
  cada especie llegó a su máximo y a su mínimo.
- Interface `Peligroso`, los peligrosos salen listados por nivel en el reporte.

## Desafíos

- La consola y la ventana no pueden compartir código de entrada y salida, así
  que el modelo ya no imprime nada: guarda los eventos del turno en una lista y
  el controlador se los entrega a la vista.
- La vista no puede preguntarle datos al modelo. Para que la tabla se arme sin
  que la vista conozca las entidades, el controlador convierte cada entidad viva
  en una fila de datos simples.
- El controlador no puede usar `JOptionPane`. Las confirmaciones las hace la
  vista; el controlador solo le avisa si hay una simulación en curso
  (`hayPartidaEnCurso()`) y la vista decide si pregunta.
- En la consola cada intervención era una sola acción. Se mantuvo igual: después
  de cambiar el clima o agregar una entidad, el panel se bloquea hasta el turno
  siguiente.
- Para retomar la simulación el modelo tenía que poder rearmarse desde el
  archivo. Se agregó un segundo constructor de `Ecosistema` que arranca vacío y
  métodos para cargar las entidades sin contarlas como nacimientos.
- El DAO no puede mostrar mensajes: devuelve true/false o null y el controlador
  decide qué mostrar en la vista.

## Uso de IA / herramientas externas

La vista, el controlador y el main de esta entrega se armaron con ayuda de
Claude (Anthropic). El detalle de los prompts y las conversaciones va en
`documentacion/octavio-fakiani/`.

Tomás y Milagros usaron Claude para que los guíe con el modelo y el
DAO, y para estudiar cómo funciona MVC. Sus preguntas están en
`documentacion/tomas-maldonado/` y `documentacion/milagros-venezia/`.

## Documentación

Las capturas y los links de cada uno están en la carpeta `documentacion/`.
