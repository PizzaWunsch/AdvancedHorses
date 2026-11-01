# AdvancedHorses

[🇩🇪 Deutsch](README.md) · 🇬🇧 English

A Fabric mod for **Minecraft 26.3** that turns horses into real companions. You tame them by caring for their needs instead of by luck. Every horse has a breed, a temperament and its own stats, which are inherited and grow with riding through a level system. Horses get hungry and tired, can get hurt, come running when you whistle and move in five shiftable gaits with much more realistic animations, hoofbeats and dust.

---

## Contents

- [Features at a glance](#features-at-a-glance)
- [Installation](#installation)
- [Settings via game rules](#settings-via-game-rules)
- [Taming](#taming)
- [Naming](#naming)
- [Viewing stats](#viewing-stats)
- [Breeds and temperaments](#breeds-and-temperaments)
- [Gaits, controls and energy](#gaits-controls-and-energy)
- [Potion of Stamina](#potion-of-stamina)
- [Stats, inheritance and levels](#stats-inheritance-and-levels)
- [Bond](#bond)
- [Age](#age)
- [Saddlebags](#saddlebags)
- [Hunger, resting and exhaustion](#hunger-resting-and-exhaustion)
- [Feeding trough](#feeding-trough)
- [Horseshoes and terrain](#horseshoes-and-terrain)
- [Injuries](#injuries)
- [Whistling](#whistling)
- [Horse book](#horse-book)
- [Pedigree](#pedigree)
- [Horse races](#horse-races)
- [Animations, sounds and particles](#animations-sounds-and-particles)
- [Key bindings](#key-bindings)
- [Creative tab](#creative-tab)
- [Recipes](#recipes)
- [Affected animals](#affected-animals)
- [Multiplayer](#multiplayer)
- [Building from source](#building-from-source)
- [Project structure](#project-structure)
- [Saved data](#saved-data)
- [Known limitations](#known-limitations)

---

## Features at a glance

| Area | What the mod does |
|---|---|
| Taming | Three needs per wild horse, with progress floating above the horse (similar to ARK) |
| Naming | A naming window opens right after taming |
| Breeds and temperaments | Warmblood, Thoroughbred, Draft horse, Pony and Mustang, plus six temperaments |
| Gaits | Walk, trot, canter, gallop and racing gallop, shifted with **W** and **S** |
| Energy | Gallop and racing gallop use up energy, and a winded horse shifts down |
| Potion of Stamina | A brewable potion that stops a horse's energy from dropping for a while |
| Stats | Speed, jump height, health, stamina, strength, bond, saturation and age |
| Inheritance and levels | Stats, breed and temperament are inherited. Running and jumping earns experience, and every level improves one stat |
| Bond | Grows through feeding, brushing and riding, and gives more experience and a longer whistle range |
| Saddlebags | Up to 6 slots, unlocked by strength |
| Hunger | Horses eat, graze, use the feeding trough and collapse without food |
| Horseshoes and terrain | Horseshoes go into their own slot in the horse inventory. Paths make horses faster, sand and mud slower. |
| Horseshoe enchantments | Unbreaking, Mending and three custom enchantments for speed, jump strength and stamina |
| Injuries | Falls and overexertion can make a horse lame |
| Whistling | Use the craftable horse whistle and your horse comes running |
| Horse book | A craftable book listing all your horses with stats and last known location |
| Pedigree | Parents and grandparents with their stats |
| Races | Race tracks with timing and a leaderboard |
| Animations | A footfall pattern per gait, hoofbeats in rhythm, dust, tail and mane physics, lying down, limping and more |
| Creative tab | All horse items, including saddle and horse armor, in a tab of their own |
| Settings | Everything can be adjusted per world with game rules |

---

## Installation

**Requirements**

| Component | Version |
|---|---|
| Minecraft | 26.3 |
| Fabric Loader | 0.20.1 or newer |
| Fabric API | 0.165.0+26.3 |
| Java | 25 |

1. Install Fabric Loader for Minecraft 26.3.
2. Put the matching **Fabric API** into the `mods` folder.
3. Put `AdvancedHorses-<version>.jar` into the `mods` folder as well.
4. Start the game.

The mod must be installed on **both the client and the server**.

---

## Settings via game rules

Instead of a config file, the mod uses **game rules**. They apply per world, are saved with the world and can be changed in game:

- with `/gamerule`,
- in the game rule menu when you create a world,
- by admins directly on a server, without a restart.

| Game rule | Default | Meaning |
|---|---|---|
| `advancedhorses:hunger` | `true` | Tamed horses get hungry |
| `advancedhorses:hunger_rate` | `100` | Hunger rate in percent (0–1000) |
| `advancedhorses:taming_effort` | `100` | Taming effort in percent (10–1000) |
| `advancedhorses:xp_rate` | `100` | Experience gain in percent (0–1000) |
| `advancedhorses:stamina` | `true` | Gallop and racing gallop use up energy |
| `advancedhorses:injuries` | `true` | Horses can become lame |
| `advancedhorses:aging` | `false` | Age affects speed and experience gain |
| `advancedhorses:terrain_speed` | `true` | Ground and horseshoes affect speed |
| `advancedhorses:resting` | `true` | Horses lie down on their own |

Example:

```
/gamerule advancedhorses:hunger_rate 50
/gamerule advancedhorses:aging true
```

---

## Taming

Every wild adult horse has **three needs**: **Hunger** always, plus two random others.

| Need | How to fulfill it | Required (base value) |
|---|---|---|
| **Hunger** | Feed wheat, sugar, apples, carrots, golden carrots, hay bales or golden apples | 12 food points |
| **Favorite treat** | One specific food this horse loves | 4 |
| **Affection** | Sneak and right-click with an empty hand | 6 times |
| **Grooming** | Right-click with a **brush** (uses durability) | 8 times |
| **Trust** | Mount it and let it throw you off | 4 attempts |
| **Company** | Stay within 6 blocks while sneaking | 40 seconds |

Food points while taming: sugar 1, wheat 2, apple/carrot 3, golden carrot 5, golden apple 8, hay bale 9, enchanted golden apple 12.

**How much effort a horse needs** depends on:

- the game rule `taming_effort`,
- the **breed**: Mustangs are harder to tame, ponies easier,
- the **temperament**: nervous horses are harder, calm ones easier.

Bred foals only need half as much.

**Rules**

- Once all three needs are fulfilled, the horse is tamed by the player who helped last.
- Hitting the horse costs 25 % of its progress.
- After 2 minutes without contact, needs that are not yet fulfilled slowly drop again.
- Progress is saved with the world.

**Display:** When you look at a wild horse (up to 16 blocks away), a panel floats above it. It shows the total progress, every need and a hint for the next open need.

---

## Naming

A window opens right after taming:

- Type a name and confirm with **Enter** or **Done**.
- **Later** closes the window. A regular name tag still works afterwards.

Names can be up to 32 characters long. Only the owner can rename the horse this way.

---

## Viewing stats

1. **In the horse inventory** (sneak and right-click a tamed horse): stats appear on the right, the pedigree on the left.
2. **Sneak and look at a tamed horse**: its stats float above it together with its name.

| Display | Meaning |
|---|---|
| Breed · Temperament | e.g. "Thoroughbred · Eager" |
| Age · Horseshoes | e.g. "7 years · Iron horseshoes" |
| **Level** | Current level and experience towards the next one, e.g. `Level 12 · 540/1840 XP` (tamed only) |
| **Speed** | Top speed in m/s |
| **Jump height** | Approximate jump height in blocks |
| **Health** | Current and maximum health |
| **Stamina** | 0–100 innate, plus level bonus. Determines energy and hunger use |
| **Strength** | 0–100 innate, plus level bonus. Determines the unlocked saddlebag slots (e.g. `4/6`) |
| **Bond** | 0–100 % |
| **Saturation** | 0–100 % (tamed only) |
| Status | Wild, resting, out of breath, lame, exhausted or the highest possible gait |

The **light blue part** of a bar shows what levels and horseshoe enchantments have added. For stamina and strength the bonus is also shown in brackets, e.g. `74 (+12)`.

---

## Breeds and temperaments

Every horse gets a **breed** and a **temperament** when it first appears. Donkeys and mules have no breed.

### Breeds

| Breed | Frequency | Speed | Stamina | Strength | Hunger | Taming |
|---|---|---|---|---|---|---|
| Warmblood | 35 % | normal | normal | normal | normal | normal |
| Thoroughbred | 15 % | **+12 %** | −15 | −10 | +15 % | +15 % |
| Draft horse | 20 % | −15 % | +10 | **+25** | +20 % | −10 % |
| Pony | 15 % | −12 % | **+20** | +5 | **−30 %** | −15 % |
| Mustang | 15 % | +4 % | +12 | normal | −10 % | **+35 %** |

### Temperaments

| Temperament | Effect |
|---|---|
| Calm | Taming −20 % |
| Nervous | Taming +25 %, experience +10 %, slightly higher energy use, gets hurt more easily |
| Greedy | 25 % more hunger, but food is 30 % more filling |
| Lazy | Experience −25 %, 15 % less hunger |
| Eager | Experience +25 %, slightly higher energy use |
| Brave | Taming +10 %, half as many injuries |

---

## Gaits, controls and energy

| Gait | Speed | Energy | Hunger |
|---|---|---|---|
| Walk | 30 % | recovers quickly | very low |
| Trot | 55 % | recovers slowly | low |
| Canter | 75 % | almost no use | medium |
| Gallop | 100 % | uses energy | high |
| Racing gallop | 122 % | uses a lot of energy | very high |

**Controls:** Tap **W** to shift up one gait and **S** to shift down one. When standing, **S** walks backwards. The horse **keeps its gait** on its own, so you only steer.

**Energy:**

- With average stamina, a horse lasts about one minute in **gallop** or 15 seconds in **racing gallop**.
- When the energy runs out, the horse is **out of breath**. It drops to canter until it has 30 % energy again.
- Stamina and bond make it last longer.
- Riding a racing gallop until complete exhaustion can cause an injury.

While riding, a display to the left of the hotbar shows:

- the current gait,
- locked gaits (red),
- saturation (green),
- energy (blue, gold while a Potion of Stamina is active).

---

## Potion of Stamina

The **Potion of Stamina** works **only on horses** (including donkeys and mules). While it is active, **energy does not drop**: gallop and racing gallop go on without a break, and the horse cannot get out of breath. Energy still recovers as usual.

| Potion | Duration |
|---|---|
| Potion of Stamina | 3:00 |
| Potion of Stamina (extended) | 8:00 |

**How a horse gets the potion:**

- **Right-click** a tamed horse with the potion. It drinks it and you get the empty bottle back.
- Throw a **splash** or **lingering potion**. This also works while riding and on wild horses.

Drinking the potion yourself does nothing. While it is active on the horse, the energy bar glows gold.

**Brewing** (in a brewing stand, like vanilla potions):

| Ingredient | Result |
|---|---|
| Awkward Potion + **hay bale** | Potion of Stamina |
| Potion of Stamina + **redstone** | extended to 8:00 |
| + **gunpowder** | splash potion |
| Splash potion + **dragon's breath** | lingering potion |

---

## Stats, inheritance and levels

### Inheritance

| What | How it is inherited |
|---|---|
| Speed, jump strength, health | As in vanilla |
| Stamina, strength | Close to the average of both parents, with some randomness |
| Breed | Usually from one parent. Different breeds have a 10 % chance of a Warmblood foal, and a donkey parent always gives one. |
| Temperament | 40 % from the mother, 40 % from the father, 20 % random |

### Levels and experience

Tamed horses earn **experience (XP)** while ridden and level up to **level 50**.

| Action | Experience |
|---|---|
| Walk | 0.1 XP per block |
| Trot | 0.4 XP per block |
| Canter | 0.7 XP per block |
| Gallop | 1 XP per block |
| Racing gallop | 1.3 XP per block |
| Jump | 4 XP per block of jump height |

A jump only counts from about 0.6 blocks of height. Walking up steps or falling off cliffs gives nothing.

**Experience needed:** level 1 needs 400 XP, and every further level needs 120 XP more. Level 50 takes 167,000 XP in total, which is about 167 km of galloping without any bonuses.

How fast a horse earns experience depends on:

- the game rule `xp_rate`,
- its **temperament** (Eager +25 %, Nervous +10 %, Lazy −25 %),
- its **bond** (up to +50 %),
- its **load**: each filled saddlebag slot adds about +8 %, up to +50 %,
- with aging enabled, its **age**.

### Upgrades per level

Every level improves **one** stat. The order repeats every five levels, so each stat gets exactly ten upgrades by level 50.

| Level | Upgrade | Per upgrade | At level 50 |
|---|---|---|---|
| 1, 6, 11, … 46 | **Speed** | +1.5 % | +15 % |
| 2, 7, 12, … 47 | **Jump strength** | +2.5 % | +25 % |
| 3, 8, 13, … 48 | **Stamina** | +2.5 | +25 |
| 4, 9, 14, … 49 | **Strength** | +3 | +30 |
| 5, 10, 15, … 50 | **Health** | +1 health point | +10 (5 hearts) |

On a level-up a sound plays, the horse tosses its head and the action bar shows which stat improved. A health upgrade fills the new health point right away.

### Why horses still differ

Upgrades are added **on top of the innate stats** instead of raising them to a fixed maximum:

- Speed and jump strength rise by a **percentage**. A fast horse stays faster than a slow one, even at level 50.
- Stamina, strength and health get a **flat bonus** that may go above 100. The gap between two horses stays the same.
- Breed and temperament keep working as before.

Two level 50 horses therefore differ just as much as they did at level 0. Levels are **not** inherited: foals start at level 0, so good genes still matter most.

Horses from older versions of the mod have their previous training progress converted into a matching level once.

---

## Bond

Bond (0–100 %) shows how much a horse trusts you. It grows through:

| Action | Gain |
|---|---|
| Feeding | +1 % |
| Brushing with a brush | +1.5 %, at most every 20 seconds |
| Riding | +1 % per 80 blocks |

A high bond gives you:

- up to **+50 % experience** while riding,
- up to **30 % faster energy recovery**,
- a larger **whistle range**: 48 blocks, up to 128 blocks at full bond.

---

## Age

Every horse has an age. **One Minecraft day equals one year.** Wild horses are 3–12 years old when they appear, foals start at 0.

When the game rule `advancedhorses:aging` is enabled (off by default):

| Age | Effect |
|---|---|
| under 4 years | Experience +30 % |
| over 14 years | Experience −30 % |
| 18 years and older | Speed −8 % |
| 24 years and older | Speed −15 % |

---

## Saddlebags

Horses (not donkeys or mules) have **6 inventory slots**:

- One slot unlocks per **16 strength**.
- Locked slots show a padlock.
- Items in locked slots can still be taken out.
- The items are saved and drop when the horse dies.

Donkeys and mules keep their chest with 15 slots.

---

## Hunger, resting and exhaustion

### Hunger

- **Saturation goes from 0 to 100.** A resting horse goes from full to hungry in about two Minecraft days.
- While riding it drops faster, depending on the gait.
- Breed, temperament, stamina and the game rule `hunger_rate` affect how fast it drops.

| Food | Saturation |
|---|---|
| Sugar | +3 |
| Red mushroom | +5 |
| Wheat | +6 |
| Apple, carrot | +8 |
| Golden carrot | +15 |
| Golden apple | +25 |
| Hay bale | +35 |
| Enchanted golden apple | +40 |
| Feeding trough (per level) | +25 |

Hungry horses **graze** on their own and walk to a filled **feeding trough**.

| Saturation | Effect |
|---|---|
| below 25 | No gallop or racing gallop, warning while riding |
| below 20 | Lies down much more often |
| below 10 | Walk and trot only |
| 0 | **Exhausted**: lies on its side and can only be ridden again after you feed it |

### Resting

- Horses lie down from time to time, more often at night and when injured.
- They get up when you mount them, when they get hurt or when you whistle.
- After getting up they stretch.

---

## Feeding trough

The **feeding trough** feeds your horses automatically.

- **Filling:** Right-click with a **hay bale** (+2 levels) or **3 wheat** (+1 level). It holds up to 4 levels, and you can see the hay inside.
- **Eating:** Tamed horses below 60 % saturation within 10 blocks walk over and eat one level (+25 saturation). Then they wait 10 seconds before eating again.
- Leashed horses do not go to the trough.

---

## Horseshoes and terrain

**Shoeing a horse:**

- Open the horse inventory (sneak and right-click a tamed horse).
- Below the saddle and armor slots there is a **horseshoe slot** with a horseshoe icon. Put the horseshoes in there by dragging them or with shift-click.
- Horseshoes have **durability** (iron 3,000, diamond 9,000). Every block you ride costs one point (less with Unbreaking), and the durability bar is shown on the item. When they are worn out, they disappear.
- You can take them out at any time. When the horse dies, the horseshoes drop.
- Foals cannot be shod.

| Horseshoes | Durability | On stone | Everywhere | Injury risk |
|---|---|---|---|---|
| Unshod | – | −10 % speed | – | normal |
| Iron | about 3,000 blocks | normal | – | −20 % |
| Diamond | about 9,000 blocks | **+5 %** | **+2 %** | **−50 %** |

**Terrain** (with the game rule `terrain_speed`):

| Ground | Speed |
|---|---|
| Dirt path | **+10 %** |
| Grass, dirt and similar | normal |
| Gravel | −5 % |
| Snow, powder snow | −15 to −20 % |
| Sand, red sand | −20 % |
| Mud | −35 % |
| Soul sand, soul soil | −40 % |
| Stone, deepslate, netherrack etc. | depends on horseshoes |

An unshod horse galloping on stone can get hurt over time.

### Enchantments

Horseshoes can be enchanted at an **enchanting table** and with books on an **anvil**. The effects apply while the horseshoes are in the horseshoe slot.

| Enchantment | Max level | Effect |
|---|---|---|
| **Unbreaking** | III | Horseshoes wear out more slowly, just like tools |
| **Mending** | I | Experience orbs you pick up **while riding** repair the horseshoes. Held in your hand it works as usual. |
| **Fleetfoot** | III | +3 % speed per level (max +9 %) |
| **Spring Hoof** | III | +5 % jump strength per level (max +15 %) |
| **Endurance** | III | +5 stamina per level (max +15) |

Fleetfoot, Spring Hoof and Endurance are also sold as books by librarians. All three can be combined. Like levels, they are added on top of the innate stats, so a level 50 horse with Fleetfoot III is +24 % faster than without.

---

## Injuries

With the game rule `injuries` (on by default), a horse can become **lame**. Causes:

- a fall of more than 6 blocks,
- riding a racing gallop until complete exhaustion,
- galloping unshod on hard ground.

A lame horse:

- can go no faster than **trot**,
- visibly limps,
- heals on its own after about 5 minutes, four times faster while lying down.

You can **heal it instantly** with a **golden carrot** or a **golden apple**. A brave temperament and horseshoes lower the risk.

---

## Whistling

The **horse whistle** is an item you craft (see [Recipes](#recipes)). Right-click with it and your nearest tamed horse within range comes to you:

- The range is 48 blocks, up to 128 at full bond.
- Lying horses get up, leashed horses are unleashed.
- Exhausted horses cannot come.
- After each use the whistle has a 3 second cooldown, shown on the item.

---

## Horse book

The **horse book** is an item you craft (see [Recipes](#recipes)). Right-click with it to open a list of all your tamed horses with:

- name, level and breed,
- speed, stamina, strength and bond,
- saturation,
- last known location and dimension.

The list is saved per world. A horse is removed from it when it dies.

---

## Pedigree

Bred horses remember their **parents** (with breed, speed, stamina and strength) and their **four grandparents**. The pedigree is shown to the left of the horse inventory. Wild horses are listed there as "Wild-caught".

---

## Horse races

Admins (game master permission level) set up race tracks:

| Command | Effect |
|---|---|
| `/horserace create <name>` | Creates a track with the start at your position |
| `/horserace finish <name>` | Sets the finish to your position |
| `/horserace remove <name>` | Deletes a track |
| `/horserace list` | Lists all tracks (any player) |
| `/horserace top <name>` | Shows the leaderboard (any player) |

**How a race works:**

1. Ride a horse into the start area (4 block radius).
2. As soon as you leave it, the timer starts. It is shown in the action bar.
3. In the finish area, the timer stops and your time is added to the **top 10**.

Dismounting cancels the race. It also expires automatically after 20 minutes.

---

## Animations, sounds and particles

### Movement

- **Footfall pattern per gait:**
  - Walk: four-beat
  - Trot: diagonal
  - Canter: three-beat
  - Gallop: four-beat
  - Racing gallop: stretched gallop
- **Own stride length per gait**, with smooth transitions.
- **Simulated knee bend**, body bounce and rocking, head nodding in rhythm.
- **Racing gallop:** outstretched neck, pinned ears, flowing tail.
- **Other movements:**
  - jumping with tucked legs
  - paddling while swimming
  - leaning into turns
- **Limping:** The injured leg takes shorter steps and the head bobs when it lands.

### Tail and mane

- The **tail** swings with inertia. It swings outward in turns, lifts when accelerating and keeps swaying afterwards.
- The **mane** blows in the wind depending on speed.

### Hoofbeats and dust

- **Hoofbeats** play exactly in rhythm with the footfall pattern and match the ground: wood sounds different, and the gallop has its own sound.
- From canter on, every hoof kicks up **dust**, and more of it on sand, dirt, gravel and snow.
- In shallow water it **splashes**.

### Rest and behavior

- breathing, looking around, twitching ears
- chewing while grazing
- lying down, sleeping, and lying on the side with heavy breathing when exhausted
- hungry horses let their head, ears and tail hang

### Single animations

| Animation | When |
|---|---|
| Head shake with snort | Randomly, when refusing food or when shifting down from a gallop |
| Pawing | Randomly, especially wild horses near you |
| Bucking | When a wild horse throws you off |
| Nuzzling | After petting, brushing and taming |
| Shaking off | After leaving water |
| Stretching | After getting up |
| Hoof stamping | Randomly, more often when saddled and waiting |
| Head toss | Impatiently while waiting, when shifting into racing gallop and when you whistle |

---

## Key bindings

| Key | Action | Can be changed under |
|---|---|---|
| **W** / **S** (while riding) | Shift gait up / down | Controls → Movement |

The mod adds no key bindings of its own. The horse book and the horse whistle are items.

---

## Creative tab

In creative mode there is a separate **"Horses"** tab with everything related to horses:

- horse book, horse whistle, feeding trough, iron and diamond horseshoes
- saddle and all horse armor (leather, copper, iron, gold, diamond, netherite)
- lead, name tag, brush, chest for donkeys and mules
- food: hay bale, wheat, sugar, apple, carrot, golden carrot, golden apple
- Potions of Stamina (normal and extended, each as potion, splash potion and lingering potion)
- spawn eggs for horse, donkey, mule, skeleton horse and zombie horse

The vanilla items also stay in their usual tabs.

---

## Recipes

**Horse book** (shapeless)
```
Book + Leather + Wheat
```

**Horse whistle** (shapeless)
```
Iron nugget + Iron nugget + String
```

**Feeding trough**
```
S   S        S = any wooden slab
S   H   S    H = hay bale
```

**Iron horseshoe**
```
I   .   I    I = iron ingot
N   .   N    N = iron nugget
N   I   N
```

**Diamond horseshoe**
```
D   .   D    D = diamond
D   E   D    E = iron horseshoe
```

**Potion of Stamina** (brewing stand)
```
Awkward Potion + Hay bale            → Potion of Stamina
Potion of Stamina + Redstone         → extended
```

---

## Affected animals

| Animal | Taming | Breed, stats, hunger, gaits, horseshoes | Saddlebags | Animations |
|---|---|---|---|---|
| Horse | ✔ | ✔ | ✔ (6 slots) | ✔ |
| Donkey | ✔ | ✔ (no breed) | Vanilla chest | ✔ |
| Mule | ✔ | ✔ (no breed) | Vanilla chest | ✔ |
| Skeleton and zombie horse | Vanilla | – | – | Gait animations |
| Llama, camel | Vanilla | – | – | Vanilla |

---

## Multiplayer

- The mod must be installed on the **server and all clients**.
- All state (taming, stats, gait, energy, resting, injuries, pedigree) comes from the server.
- Each client calculates hoofbeats, dust and animations itself, so everyone sees and hears the same.
- Game rules apply to all players in the world.

---

## Building from source

You need **Java 25** and **Gradle 9.8.0** or newer. The version is set in `gradle/wrapper/gradle-wrapper.properties` and your IDE downloads it automatically.

```bash
gradle build        # build the mod, the .jar ends up in build/libs/
gradle runClient    # start a test client
gradle runServer    # start a test server
```

---

## Project structure

```
src/main/java/dev/pizzawunsch/advancedhorses/
├── AdvancedHorses.java              Entry point: registration, events, commands
├── access/                          Interface to the extra horse data
├── anim/                            Footfall patterns, single animations, hoofbeats, tail physics
├── block/                           Feeding trough
├── book/                            Horse book (registry and storage)
├── horse/                           Gaits, stats, breeds, temperaments, horseshoes, hunger,
│                                    energy, bond, levels, horseshoe enchantments, age,
│                                    injuries, pedigree, whistling
├── inventory/                       Saddlebag slots with locking, horseshoe slot
├── item/                            Horse book, horse whistle
├── network/                         Packets (gait, horse book, naming)
├── race/                            Race tracks, timing, /horserace
├── registry/                        Items, blocks, effects and potions, creative tab, game rules
├── taming/                          Needs, taming, interactions
├── mixin/                           Hooks into the horse, horse inventory, bucking behavior and mending
│   └── client/                      Hooks into the model, renderer and inventory screen
└── client/
    ├── AdvancedHorsesClient.java    Client entry point
    ├── anim/                        Calculation of all model animations
    ├── gait/                        Gait shifting and riding display
    ├── hud/                         Taming, stats and pedigree panels
    └── screen/                      Naming window and horse book

src/main/resources/
├── assets/advancedhorses/           Languages, models, textures, blockstates, effect icon
└── data/                            Recipes, brewing recipes, enchantments, loot table, tags
```

---

## Saved data

**On the horse** (NBT):

| Key | Content |
|---|---|
| `AdvancedHorsesHUNGER`, `…ENERGY`, `…BOND` | Saturation, energy, bond |
| `AdvancedHorsesSTAMINA`, `…STRENGTH` | Inherited stamina and strength |
| `AdvancedHorsesLEVEL`, `…XP` | Level and experience towards the next level |
| `AdvancedHorsesSHOE_SPEED`, `…SHOE_JUMP`, `…SHOE_STAMINA` | Enchantment levels of the equipped horseshoes |
| `AdvancedHorsesBREED`, `…TRAIT`, `…SHOES`, `…FLAGS`, `…AGE` | Breed, temperament, horseshoes, states, age |
| `AdvancedHorsesShoe` | The equipped horseshoes (item including durability) |
| `AdvancedHorsesBirth`, `…LameTicks`, `…ShoeWear` | Time of birth, healing time, wear of the current block |
| `AdvancedHorsesPedigree` | Pedigree |
| `AdvancedHorsesRest`, `…RestTicks` | Resting state |
| `AdvancedHorsesItems` | Saddlebags |
| `AdvancedHorsesNeeds`, `…Points0–2`, `…LastProgress` | Taming progress |

**In the world folder:**

| File | Content |
|---|---|
| `advancedhorses_horses.json` | Horse book of all players |
| `advancedhorses_races.json` | Race tracks and leaderboards |

Existing worlds keep working. Horses without this data get a random breed, temperament, age and stats the first time they are loaded.

---

## Known limitations

- The horse model has no knee joints. Bent legs are suggested by shortening them.
- Tail and mane are rigid parts in the vanilla model. They swing and blow as a whole, but are not split into segments.
- The horse book only knows the location of horses that were loaded most recently.
- Whistling only works for horses in loaded chunks.
- Other mods that heavily change the horse model or riding behavior may conflict with this mod.

---

## License

All rights reserved. See [`LICENSE.txt`](LICENSE.txt).
