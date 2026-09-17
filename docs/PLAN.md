# Barro — Plan maestro

Documento de diseño y alcance. El `CLAUDE.md` cubre el _cómo_ técnico del repo;
este cubre el _qué_ y el _por qué_ del contenido.

---

## 1. Concepto

**Barro** es un set de bloques decorativos de arquitectura y artesanía mexicana
para Minecraft Java.

El hueco que llena: los mods de decoración establecidos son estética europea o
nórdica — roble, mimbre, granja inglesa, piedra de castillo. No existe un set
coherente de materiales latinoamericanos: barro cocido, talavera, adobe, cantera,
petate, equipal, herrería de patio.

No es un mod de "temática mexicana" con sombreros y chiles. Es una **paleta de
materiales de construcción** que se integra con el vocabulario visual de vanilla,
pensada para que alguien construya una casa de patio, un mercado o una hacienda
sin que se vea pegado con cinta.

## 2. Alcance de la v1

**Dentro:**

- Bloques decorativos: cubos, losas, escaleras, muros
- Bloques con modelo propio y rotación (muebles, ollas, macetas)
- Recetas, loot tables, tags de vanilla
- Una creative tab propia
- Localización en inglés y español

**Fuera (explícitamente):**

- Block entities, inventarios, GUIs
- Entidades, mobs, monturas (incluye "sentarse en la silla")
- Generación de mundo, dimensiones, biomas
- Mecánicas nuevas de cualquier tipo
- Mixins

Si una idea requiere algo de la lista de fuera, va al backlog, no a la v1.
El objetivo de la v1 es dominar el pipeline de contenido, no aprender todo a la vez.

## 3. Principios de diseño

**La paleta sale de vanilla.** Los colores se sacan con cuentagotas de las texturas
del juego — terracota, terracota vidriada, cobre en sus estados de oxidación, barro,
maderas. No se inventan colores. Un bloque que no comparte la paleta se detecta al
instante aunque el jugador no sepa explicar por qué.

De vanilla se toman **solo los colores**, nunca el acomodo de píxeles. Los motivos se
diseñan a mano y la variación del material (esmalte, grano, tierra) es ruido propio
generado con semilla. Las texturas se generan con `java tools/textures/Tex.java`
desde los specs de `tools/textures/`; para cambiar una textura se edita su spec y se
regenera, no el PNG.

**Cada pieza es autónoma.** Ninguna pieza depende de otra para ser útil. Esto permite
cortar el alcance en cualquier momento y seguir teniendo algo publicable.

**Se integra, no compite.** Los bloques deben verse bien _junto a_ los de vanilla,
no reemplazarlos. Losas y escaleras de cada material base, porque eso es lo que
la gente realmente usa al construir.

**Fidelidad sobre estilización.** Los nombres y las formas se apegan al objeto real
(un equipal es un equipal, no una "silla mexicana"). Si un objeto no se puede
representar dignamente en 16x16, no entra.

## 4. Convenciones

**Namespace:** `barro`

**Identificadores de recursos en español, sin acentos ni ñ.**
`barro:azulejo_talavera`, `barro:equipal`, `barro:olla_de_barro`.

Razón: los objetos son culturalmente específicos y no tienen traducción honesta al
inglés. "Equipal" no es "leather chair". Traducirlos perdería justo lo que hace
distinto al mod. Restricción técnica: el namespace y el path solo admiten
`[a-z0-9/._-]`, así que nada de acentos ni `ñ` en los ids — esos van en el archivo
de idioma.

**Localización:** `en_us.json` y `es_mx.json`. El inglés usa el término original con
una aclaración breve cuando hace falta (`Equipal Chair`), no una traducción inventada.

**Código y comentarios en inglés.** Solo los ids de contenido y los archivos de
idioma son en español.

## 5. Fases de contenido

El orden es deliberado: cada fase introduce exactamente una técnica nueva de
modelado. No saltar fases.

### Fase 1 — Cubos simples

_Técnica: blockstate mínimo, puro trabajo de textura._

- `azulejo_talavera` (con variantes de patrón; cada patrón es un bloque separado, no una propiedad del blockstate)
- `adobe`
- `barro_cocido`
- `cantera`

**Primero un solo patrón de talavera, de punta a punta.** Tiene que cumplir toda la
lista de "terminado" (sección 6) y estar verificado en Fabric y en NeoForge antes de
texturizar otro patrón o material. Hasta entonces no se sabe cuánto cuesta una pieza.
(Hecho con `flor`.)

**Patrones de talavera:** `flor`, `estrella`, `rombos`, `hojas`, `cruz`, `sol`,
`medallon`, `cenefa`, más los lisos `liso_azul`, `liso_amarillo` y `liso_verde` para
enmarcar los patrones.

- **Paleta:** solo los seis colores de la talavera poblana (azul, amarillo, naranja,
  verde, malva y negro) sobre el mismo esmalte crema. Cada color sale de la terracota
  vidriada de vanilla correspondiente.
- **Simetría:** todos los patrones son simétricos, así que no necesitan `facing`. Un
  patrón asimétrico espera a la Fase 5.
- **Recetas:** `flor` se craftea (8 terracota vidriada blanca + tinte azul → 8). Todos
  los patrones, incluida `flor`, salen del cortapiedras a partir de cualquier azulejo
  del tag `barro:azulejos_talavera`. Un patrón nuevo solo necesita entrar al tag y
  tener su receta de cortapiedras.

**Barro cocido:** `barro_cocido` es el material liso, base de las losas, escaleras y
muros de la Fase 2. `petatillo` es el piso de ladrillitos en tejido de canasta. Las
dos tienen las propiedades de la terracota de vanilla. Recetas: 2x2 terracota → 4
`barro_cocido` y 2x2 `barro_cocido` → 4 `petatillo`, cada una también en el
cortapiedras.

**Adobe:** bloques grandes de tierra con paja, en dos hiladas cuatrapeadas. Más claro
y cálido que los ladrillos de lodo de vanilla, con sus propiedades y su sonido.
Receta: lodo y trigo en diagonal (2x2) → 2, porque el adobe es barro con paja.

**Cantera:** cantera rosa, un sillar labrado por bloque con junta fina. Grano de
toba con los rosas del granito, y las propiedades y el sonido de la toba (la cantera
es toba volcánica). Receta: 8 toba + tinte rosa → 8.

### Fase 2 — Variantes derivadas

_Técnica: modelos heredados. Muy poco esfuerzo, mucho valor de construcción._

- Losa, escalera y muro de cada material de la Fase 1

Hecho para los materiales base: `barro_cocido`, `adobe` y `cantera`, 9 bloques en
total.

- **Ids:** `losa_de_*`, `escaleras_de_*` y `pared_de_*`, con los mismos términos que
  usa vanilla en es_mx ("Losa de…", "Escaleras de…", "Pared de…").
- **Fuera:** la talavera, porque el patrón queda cortado en medio bloque (vanilla
  tampoco tiene losas de terracota vidriada), y el petatillo, que es acabado de piso.
- **Recetas:** las de vanilla (3 → 6 losas, 6 → 4 escaleras, 6 → 6 paredes), más el
  cortapiedras desde el bloque base.

### Fase 3 — Modelos planos y colgantes

_Técnica: modelos no cúbicos, rotación por blockstate._

- `petate` (tapete)
- `papel_picado` (colgante)

- **Petate:** tapete de palma tejida, con el `CarpetBlock` de vanilla (alto de 1 px,
  se rompe a mano, arde). Receta: 2x2 de caña de azúcar → 4.
- **Papel picado:** tira colgante con dos modelos planos propios y las propiedades
  `facing` y `attached`. Contra la cara de un bloque firme queda pegado a esa pared;
  en cualquier otro lado cuelga al centro de su bloque, mirando a quien lo coloca, de
  modo que una fila se lee como una sola tira. Sin colisión y se rompe de un golpe.
  No exige soporte: si quitas la pared, el papel se queda pegado en el aire.
  Receta: 3 hilos, 1 papel y los tintes de los dos colores de los banderines → 3.
- **Caída (`sag`):** cada segmento cuenta hasta 3 vecinos iguales de cada lado y baja
  según el menor de los dos, 1.5 px por nivel. Los extremos quedan arriba y el centro
  abajo, así que la tira cuelga en curva y la curva crece con el tendido. Esto usa la
  técnica de la Fase 6 (blockstates que reaccionan a vecinos), adelantada a petición.
- **Transparencia:** en 26.1 no se registra en código. El juego elige la capa de
  recorte según el alfa de la textura, y el modelo solo puede forzar translucidez con
  `force_translucent`. Por eso el papel picado no necesita nada del lado del cliente.

### Fase 4 — Primer modelo propio

_Técnica: Blockbench de cero, hitbox propio._

- `maceta_de_barro`
- `olla_de_barro`
- `comal`
- `molcajete`

Los cuatro son objetos de piso con modelo y hitbox propios, sin rotación (son
redondos o simétricos), hechos con `SmallDecorBlock`.

- **Texturas tipo atlas:** cada una reparte el 16x16 en regiones y las caras del
  modelo apuntan a la que les toca: el hueco de la vasija (anillos concéntricos)
  arriba a la izquierda, material liso a su derecha, y abajo la franja de los
  costados, sombreada de claro al centro a oscuro en los bordes para que el objeto se
  lea redondo. La olla lleva banda pintada en cobalto y crema de la talavera.
- **Siluetas:** la olla tiene panza, cuello y dos asas; el comal es un octágono bajo
  con el borde levantado; el molcajete es un tazón octagonal con tres patas y su
  tejolote dentro. El octágono se arma con dos cajas cruzadas.
- **Iconos:** los modelos heredan `minecraft:block/block` (de ahí salen las
  transformaciones de vista) y cada uno ajusta su `display.gui`: el comal se inclina
  más para que se vea la plancha, y los chicos se agrandan un poco.
- **Materiales:** la loza usa el sonido de la vasija decorada y se rompe a mano; un
  pistón la destroza, como en vanilla. El molcajete es piedra volcánica: sonido de
  basalto y dureza 1.5.
- **Recetas:** cortapiedras. Barro cocido → maceta, olla o comal; basalto →
  molcajete, que es piedra tallada.

### Fase 5 — Rotación direccional

_Técnica: propiedad `facing` en el blockstate._

- `equipal`
- `banca_de_madera`
- `mesa_de_madera`

### Fase 6 — Estados múltiples

_Técnica: blockstates que reaccionan a bloques vecinos._

- `repisa`
- Mesas que se conectan entre sí

## 6. Definición de "terminado"

Una pieza no está hecha hasta que cumple todo esto:

- [ ] Modelo en `assets/barro/models/block/`
- [ ] Blockstate en `assets/barro/blockstates/`
- [ ] Textura 16x16 con paleta derivada de vanilla
- [ ] Modelo de item (o heredado del bloque)
- [ ] Entrada en `en_us.json` y en `es_mx.json`
- [ ] Receta en `data/barro/recipe/`
- [ ] Loot table en `data/barro/loot_table/` (se dropea a sí misma)
- [ ] Tags: al menos `mineable/<herramienta>` y el tag de dureza correspondiente
- [ ] Aparece en la creative tab del mod
- [ ] **Verificada visualmente en Fabric y en NeoForge**, no solo compilada

El último punto es el que se olvida. Compilar no es evidencia de que se vea bien.

## 7. Roadmap de versiones

| Versión | Contenido                         | Criterio de salida                                 |
| ------- | --------------------------------- | -------------------------------------------------- |
| 0.1.0   | Fase 1 completa                   | Una pieza recorre el pipeline entero sin fricción  |
| 0.2.0   | Fase 2                            | El set base sirve para construir una casa completa |
| 0.3.0   | Fases 3 y 4                       | Primeros modelos propios en el juego               |
| 0.4.0   | Fases 5 y 6                       | Muebles colocables y orientables                   |
| 1.0.0   | Pulido, icono, página de Modrinth | Publicable                                         |

## 8. Pendientes técnicos

- [ ] `maven_group` sigue en `com.example` → cambiar a `io.github.kevdev_code`
- [ ] Falta `assets/barro/icon.png`
- [x] Rango de NeoForge bajado a `[26.1.2,)`: el mod no usa ninguna API del cargador.
      Architectury bajó al piso de la línea 20.x (`>=20.0.12`); se sube cuando se use
      algo que no exista ahí
- [ ] Definir si la creative tab agrupa todo o se separa por material
- [x] Licencia: código MIT (`LICENSE`), arte con todos los derechos reservados
      (`LICENSE-ASSETS`). Ambos archivos van dentro de los jars
- [x] `LICENSE-ASSETS` ya cubre los modelos con geometría propia (los que tienen
      `elements`); los que solo heredan un padre quedan bajo MIT
- [ ] Antes de publicar: descripción real del mod en `fabric.mod.json` y
      `neoforge.mods.toml` (sigue la de la plantilla), y quitar `"suggests":
      {"another-mod"}` de la plantilla

## 9. Backlog

Ideas descartadas de la v1, no olvidadas:

- Sillas donde el jugador se pueda sentar (requiere entidad de montura)
- Bloque de fermentación / curado con progreso (requiere block entity)
- Talavera teñible con los 16 colores
- Herrería de patio: rejas, faroles, barandales
- Puesto de mercado: huacales, toldos, básculas
