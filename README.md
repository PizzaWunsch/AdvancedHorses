# AdvancedHorses

🇩🇪 Deutsch · [🇬🇧 English](README.en.md)

Eine Fabric-Mod für **Minecraft 26.3**, die Pferde zu echten Begleitern macht. Du zähmst sie über ihre Bedürfnisse statt durch Zufall. Jedes Pferd hat eine Rasse, ein Wesen und eigene Werte, die vererbt werden und durch ein Level-System mit dem Reiten wachsen. Pferde werden hungrig, werden müde, können sich verletzen, kommen auf einen Pfiff angelaufen und bewegen sich in fünf schaltbaren Gangarten mit deutlich realistischeren Animationen, Hufschlägen und Staub.

---

## Inhalt

- [Features im Überblick](#features-im-überblick)
- [Installation](#installation)
- [Einstellungen per Gamerule](#einstellungen-per-gamerule)
- [Zähmen](#zähmen)
- [Namen vergeben](#namen-vergeben)
- [Werte ansehen](#werte-ansehen)
- [Rassen und Wesen](#rassen-und-wesen)
- [Gangarten, Steuerung und Energie](#gangarten-steuerung-und-energie)
- [Trank der Ausdauer](#trank-der-ausdauer)
- [Werte, Vererbung und Level](#werte-vererbung-und-level)
- [Bindung](#bindung)
- [Alter](#alter)
- [Satteltaschen](#satteltaschen)
- [Hunger, Ruhen und Erschöpfung](#hunger-ruhen-und-erschöpfung)
- [Futtertrog](#futtertrog)
- [Hufeisen und Untergrund](#hufeisen-und-untergrund)
- [Verletzungen](#verletzungen)
- [Pfeifen](#pfeifen)
- [Pferdebuch](#pferdebuch)
- [Stammbaum](#stammbaum)
- [Pferderennen](#pferderennen)
- [Animationen, Sounds und Partikel](#animationen-sounds-und-partikel)
- [Tasten](#tasten)
- [Kreativ-Tab](#kreativ-tab)
- [Rezepte](#rezepte)
- [Welche Tiere betroffen sind](#welche-tiere-betroffen-sind)
- [Mehrspieler](#mehrspieler)
- [Aus dem Quellcode bauen](#aus-dem-quellcode-bauen)
- [Projektstruktur](#projektstruktur)
- [Gespeicherte Daten](#gespeicherte-daten)
- [Bekannte Einschränkungen](#bekannte-einschränkungen)

---

## Features im Überblick

| Bereich | Was die Mod macht |
|---|---|
| Zähmen | Drei Bedürfnisse pro wildem Pferd, Fortschritt schwebt über dem Pferd (ähnlich wie in ARK) |
| Namen | Nach der Zähmung öffnet sich ein Fenster zum Benennen |
| Rassen und Wesen | Warmblut, Vollblut, Kaltblut, Pony, Mustang, dazu sechs Charaktere |
| Gangarten | Schritt, Trab, Kanter, Galopp, Renngalopp, geschaltet mit **W** und **S** |
| Energie | Galopp und Renngalopp verbrauchen Energie, ein erschöpftes Pferd schaltet zurück |
| Trank der Ausdauer | Braubarer Trank, mit dem die Energie eines Pferdes eine Zeit lang nicht sinkt |
| Werte | Tempo, Sprunghöhe, Gesundheit, Ausdauer, Stärke, Bindung, Sättigung, Alter |
| Vererbung und Level | Werte, Rasse und Wesen werden vererbt. Laufen und Springen bringt Erfahrung, jedes Level verbessert einen Wert |
| Bindung | Wächst durch Füttern, Bürsten und Reiten, bringt mehr Erfahrung und eine größere Rufweite |
| Satteltaschen | Bis zu 6 Plätze, freigeschaltet über die Stärke |
| Hunger | Pferde fressen, grasen, gehen an den Futtertrog und brechen ohne Futter zusammen |
| Hufeisen und Untergrund | Hufeisen kommen in einen eigenen Slot im Pferdeinventar. Wege machen schneller, Sand und Schlamm langsamer. |
| Hufeisen-Verzauberungen | Haltbarkeit, Reparatur und drei eigene Verzauberungen für Tempo, Sprungkraft und Ausdauer |
| Verletzungen | Stürze und Überanstrengung lassen Pferde lahmen |
| Pfeifen | Mit der craftbaren Pferdepfeife kommt dein Pferd angelaufen |
| Pferdebuch | Ein craftbares Buch, das alle deine Pferde mit Werten und letztem Standort zeigt |
| Stammbaum | Eltern und Großeltern mit ihren Werten |
| Rennen | Strecken mit Zeitmessung und Bestenliste |
| Animationen | Eigene Fußfolge pro Gangart, Hufschläge im Takt, Staub, Schweif- und Mähnenphysik, Liegen, Lahmen und mehr |
| Kreativ-Tab | Alle Pferde-Items, auch Sattel und Pferderüstungen, in einem eigenen Tab |
| Einstellungen | Alles über Gamerules pro Welt einstellbar |

---

## Installation

**Voraussetzungen**

| Komponente | Version |
|---|---|
| Minecraft | 26.3 |
| Fabric Loader | 0.20.1 oder neuer |
| Fabric API | 0.165.0+26.3 |
| Java | 25 |

1. Fabric Loader für Minecraft 26.3 installieren.
2. Die passende **Fabric API** in den `mods`-Ordner legen.
3. `AdvancedHorses-<version>.jar` ebenfalls in den `mods`-Ordner legen.
4. Spiel starten.

Die Mod muss auf **Client und Server** installiert sein.

---

## Einstellungen per Gamerule

Statt einer Konfigurationsdatei nutzt die Mod **Gamerules**. Sie gelten pro Welt, werden mit der Welt gespeichert und lassen sich im Spiel ändern:

- mit `/gamerule`,
- im Gamerule-Menü beim Erstellen einer Welt,
- auf Servern direkt durch Admins, ohne Neustart.

| Gamerule | Standard | Bedeutung |
|---|---|---|
| `advancedhorses:hunger` | `true` | Gezähmte Pferde werden hungrig |
| `advancedhorses:hunger_rate` | `100` | Hungerverbrauch in Prozent (0–1000) |
| `advancedhorses:taming_effort` | `100` | Zähmungsaufwand in Prozent (10–1000) |
| `advancedhorses:xp_rate` | `100` | Erfahrungsgewinn in Prozent (0–1000) |
| `advancedhorses:stamina` | `true` | Energie verbrauchen bei Galopp und Renngalopp |
| `advancedhorses:injuries` | `true` | Pferde können lahmen |
| `advancedhorses:aging` | `false` | Alter wirkt sich auf Tempo und Erfahrungsgewinn aus |
| `advancedhorses:terrain_speed` | `true` | Untergrund und Hufeisen beeinflussen das Tempo |
| `advancedhorses:resting` | `true` | Pferde legen sich von selbst hin |

Beispiel:

```
/gamerule advancedhorses:hunger_rate 50
/gamerule advancedhorses:aging true
```

---

## Zähmen

Jedes wilde, erwachsene Pferd hat **drei Bedürfnisse**: **Hunger** immer, dazu zwei zufällige weitere.

| Bedürfnis | So erfüllst du es | Benötigt (Grundwert) |
|---|---|---|
| **Hunger** | Weizen, Zucker, Äpfel, Karotten, goldene Karotten, Heuballen oder goldene Äpfel füttern | 12 Futterpunkte |
| **Lieblingsleckerli** | Ein bestimmtes Futter, das das Pferd besonders mag | 4 Stück |
| **Zuneigung** | Schleichen und mit leerer Hand rechtsklicken | 6 Mal |
| **Fellpflege** | Mit einem **Pinsel** rechtsklicken (kostet Haltbarkeit) | 8 Mal |
| **Vertrauen** | Aufsteigen und dich abwerfen lassen | 4 Versuche |
| **Gesellschaft** | Schleichend im Umkreis von 6 Blöcken bleiben | 40 Sekunden |

Futterpunkte beim Zähmen: Zucker 1, Weizen 2, Apfel/Karotte 3, goldene Karotte 5, goldener Apfel 8, Heuballen 9, verzauberter goldener Apfel 12.

**Wie viel Aufwand ein Pferd braucht**, hängt ab von:

- der Gamerule `taming_effort`,
- der **Rasse**: Mustangs sind schwerer zu zähmen, Ponys leichter,
- dem **Wesen**: nervöse Pferde sind schwieriger, gelassene leichter.

Gezüchtete Fohlen brauchen nur halb so viel.

**Regeln**

- Sind alle drei Bedürfnisse erfüllt, ist das Pferd gezähmt, und zwar für den Spieler, der zuletzt geholfen hat.
- Schlägst du das Pferd, verliert es 25 % Fortschritt.
- Nach 2 Minuten ohne Kontakt sinkt der Fortschritt noch nicht erfüllter Bedürfnisse langsam.
- Der Fortschritt wird mit der Welt gespeichert.

**Anzeige:** Schaust du ein wildes Pferd an (bis 16 Blöcke), schwebt über ihm ein Panel mit Gesamtfortschritt, allen Bedürfnissen und einem Tipp für das nächste offene Bedürfnis.

---

## Namen vergeben

Direkt nach der Zähmung öffnet sich ein Fenster:

- Name eingeben und mit **Enter** oder **Fertig** bestätigen.
- **Später** schließt das Fenster. Danach geht auch ein normales Namensschild.

Erlaubt sind bis zu 32 Zeichen. Nur der Besitzer kann das Pferd so umbenennen.

---

## Werte ansehen

1. **Im Pferdeinventar** (Schleichen + Rechtsklick auf ein gezähmtes Pferd): Rechts erscheinen die Werte, links der Stammbaum.
2. **Schleichen und ein gezähmtes Pferd anschauen**: Die Werte schweben mit dem Namen über dem Pferd.

| Anzeige | Bedeutung |
|---|---|
| Rasse · Wesen | z. B. „Vollblut · Ehrgeizig“ |
| Alter · Hufeisen | z. B. „7 Jahre · Eisenhufeisen“ |
| **Level** | Aktuelles Level und Erfahrung bis zum nächsten, z. B. `Level 12 · 540/1840 EP` (nur gezähmt) |
| **Tempo** | Höchstgeschwindigkeit in m/s |
| **Sprunghöhe** | Ungefähre Sprunghöhe in Blöcken |
| **Gesundheit** | Aktuelle und maximale Lebenspunkte |
| **Ausdauer** | 0–100 angeboren, plus Level-Bonus. Bestimmt Energie- und Hungerverbrauch |
| **Stärke** | 0–100 angeboren, plus Level-Bonus. Bestimmt die freien Satteltaschen-Plätze (z. B. `4/6`) |
| **Bindung** | 0–100 % |
| **Sättigung** | 0–100 % (nur gezähmt) |
| Zustand | Wild, ruht, außer Atem, lahmt, erschöpft oder die höchste mögliche Gangart |

Der **hellblaue Teil** eines Balkens zeigt, was Level und Hufeisen-Verzauberungen hinzugefügt haben. Bei Ausdauer und Stärke steht der Bonus zusätzlich in Klammern, z. B. `74 (+12)`.

---

## Rassen und Wesen

Jedes Pferd bekommt beim ersten Auftauchen eine **Rasse** und ein **Wesen**. Esel und Maultiere haben keine Rasse.

### Rassen

| Rasse | Häufigkeit | Tempo | Ausdauer | Stärke | Hunger | Zähmung |
|---|---|---|---|---|---|---|
| Warmblut | 35 % | normal | normal | normal | normal | normal |
| Vollblut | 15 % | **+12 %** | −15 | −10 | +15 % | +15 % |
| Kaltblut | 20 % | −15 % | +10 | **+25** | +20 % | −10 % |
| Pony | 15 % | −12 % | **+20** | +5 | **−30 %** | −15 % |
| Mustang | 15 % | +4 % | +12 | normal | −10 % | **+35 %** |

### Wesen

| Wesen | Wirkung |
|---|---|
| Gelassen | Zähmung −20 % |
| Nervös | Zähmung +25 %, Erfahrung +10 %, etwas mehr Energieverbrauch, verletzt sich leichter |
| Verfressen | 25 % mehr Hunger, aber Futter sättigt 30 % besser |
| Faul | Erfahrung −25 %, 15 % weniger Hunger |
| Ehrgeizig | Erfahrung +25 %, etwas mehr Energieverbrauch |
| Mutig | Zähmung +10 %, halb so viele Verletzungen |

---

## Gangarten, Steuerung und Energie

| Gangart | Tempo | Energie | Hunger |
|---|---|---|---|
| Schritt | 30 % | erholt sich schnell | sehr gering |
| Trab | 55 % | erholt sich langsam | gering |
| Kanter | 75 % | kaum Verbrauch | mittel |
| Galopp | 100 % | Verbrauch | hoch |
| Renngalopp | 122 % | starker Verbrauch | sehr hoch |

**Steuerung:** **W** antippen schaltet eine Gangart hoch, **S** antippen eine runter. Im Stand geht das Pferd mit **S** rückwärts. Das Pferd **hält die Gangart**, du lenkst nur noch.

**Energie:**

- Mit durchschnittlicher Ausdauer hält ein Pferd etwa eine Minute **Galopp** oder 15 Sekunden **Renngalopp**.
- Ist die Energie leer, ist das Pferd **außer Atem**. Es schaltet auf Kanter zurück, bis wieder 30 % Energie da sind.
- Ausdauer und Bindung verlängern das.
- Renngalopp bis zur völligen Erschöpfung kann zu einer Verletzung führen.

Beim Reiten zeigt eine Anzeige links neben der Hotbar:

- die Gangart,
- die gesperrten Stufen (rot),
- die Sättigung (grün),
- die Energie (blau, golden während ein Trank der Ausdauer wirkt).

---

## Trank der Ausdauer

Der **Trank der Ausdauer** wirkt **nur bei Pferden** (auch Esel und Maultiere). Solange er wirkt, **sinkt die Energie nicht**: Galopp und Renngalopp gehen ohne Pause, und das Pferd kann nicht außer Atem geraten. Die Energie erholt sich trotzdem wie gewohnt.

| Trank | Dauer |
|---|---|
| Trank der Ausdauer | 3:00 |
| Trank der Ausdauer (verlängert) | 8:00 |

**So bekommt ein Pferd den Trank:**

- **Rechtsklick** mit dem Trank auf ein gezähmtes Pferd. Es trinkt ihn, du bekommst die leere Flasche zurück.
- **Wurftrank** oder **Verweiltrank** werfen. Das wirkt auch, während du reitest, und auf wilde Pferde.

Trinkst du den Trank selbst, passiert nichts. Während er beim Pferd wirkt, leuchtet die Energieanzeige golden.

**Brauen** (wie Vanilla-Tränke im Braustand):

| Zutat | Ergebnis |
|---|---|
| Seltsamer Trank + **Heuballen** | Trank der Ausdauer |
| Trank der Ausdauer + **Redstone** | verlängert auf 8:00 |
| + **Schwarzpulver** | Wurftrank |
| Wurftrank + **Drachenatem** | Verweiltrank |

---

## Werte, Vererbung und Level

### Vererbung

| Was | Wie es vererbt wird |
|---|---|
| Tempo, Sprungkraft, Gesundheit | Wie in Vanilla |
| Ausdauer, Stärke | Nahe dem Durchschnitt der Eltern, mit etwas Zufall |
| Rasse | Meist von einem Elternteil. Bei verschiedenen Rassen entsteht mit 10 % ein Warmblut, mit Esel immer. |
| Wesen | 40 % von der Mutter, 40 % vom Vater, 20 % zufällig |

### Level und Erfahrung

Gezähmte Pferde sammeln beim Reiten **Erfahrung (EP)** und steigen bis **Level 50** auf.

| Aktion | Erfahrung |
|---|---|
| Schritt | 0,1 EP pro Block |
| Trab | 0,4 EP pro Block |
| Kanter | 0,7 EP pro Block |
| Galopp | 1 EP pro Block |
| Renngalopp | 1,3 EP pro Block |
| Sprung | 4 EP pro Block Sprunghöhe |

Ein Sprung zählt erst ab etwa 0,6 Blöcken Höhe. Stufen hochlaufen oder von Klippen fallen bringt nichts.

**Benötigte Erfahrung:** Level 1 braucht 400 EP, jedes weitere Level 120 EP mehr. Level 50 erreichst du nach insgesamt 167.000 EP, ohne Boni also nach etwa 167 km Galopp.

Wie schnell ein Pferd Erfahrung sammelt, hängt ab von:

- der Gamerule `xp_rate`,
- dem **Wesen** (Ehrgeizig +25 %, Nervös +10 %, Faul −25 %),
- der **Bindung** (bis +50 %),
- dem **Gepäck**: jeder belegte Satteltaschen-Platz bringt etwa +8 %, bis zu +50 %,
- mit aktivem Altern auch dem **Alter**.

### Upgrades pro Level

Jedes Level verbessert **einen** Wert. Die Reihenfolge wiederholt sich alle fünf Level, so bekommt jeder Wert bis Level 50 genau zehn Upgrades.

| Level | Upgrade | Pro Upgrade | Bei Level 50 |
|---|---|---|---|
| 1, 6, 11, … 46 | **Tempo** | +1,5 % | +15 % |
| 2, 7, 12, … 47 | **Sprungkraft** | +2,5 % | +25 % |
| 3, 8, 13, … 48 | **Ausdauer** | +2,5 | +25 |
| 4, 9, 14, … 49 | **Stärke** | +3 | +30 |
| 5, 10, 15, … 50 | **Gesundheit** | +1 Lebenspunkt | +10 (5 Herzen) |

Beim Aufstieg erklingt ein Signal, das Pferd wirft den Kopf hoch und die Aktionsleiste zeigt, welcher Wert besser geworden ist. Bei einem Gesundheits-Upgrade wird der neue Lebenspunkt gleich aufgefüllt.

### Warum Pferde trotzdem verschieden bleiben

Die Upgrades kommen **auf die angeborenen Werte obendrauf**, statt sie auf einen festen Höchstwert anzuheben:

- Tempo und Sprungkraft steigen **prozentual**. Ein schnelles Pferd bleibt auch auf Level 50 schneller als ein langsames.
- Ausdauer, Stärke und Gesundheit bekommen einen **festen Bonus**, der über 100 hinausgehen darf. Der Abstand zwischen zwei Pferden bleibt also gleich.
- Rasse und Wesen wirken weiter wie gewohnt.

Zwei Pferde auf Level 50 unterscheiden sich damit genauso stark wie auf Level 0. Level werden **nicht** vererbt: Fohlen starten bei Level 0, gute Gene bleiben entscheidend.

Pferde aus älteren Versionen der Mod bekommen ihren bisherigen Trainingsstand einmalig in ein passendes Level umgerechnet.

---

## Bindung

Die Bindung (0–100 %) zeigt, wie sehr dir ein Pferd vertraut. Sie wächst durch:

| Aktion | Zuwachs |
|---|---|
| Füttern | +1 % |
| Bürsten mit dem Pinsel | +1,5 %, höchstens alle 20 Sekunden |
| Reiten | +1 % pro 80 Blöcke |

Eine hohe Bindung bringt dir:

- bis zu **+50 % Erfahrung** beim Reiten,
- bis zu **30 % schnellere Energieerholung**,
- eine größere **Rufweite** beim Pfeifen: 48 Blöcke, bis 128 Blöcke bei voller Bindung.

---

## Alter

Jedes Pferd hat ein Alter. **Ein Minecraft-Tag entspricht einem Jahr.** Wilde Pferde sind beim Auftauchen 3–12 Jahre alt, Fohlen 0.

Ist die Gamerule `advancedhorses:aging` aktiv (standardmäßig aus):

| Alter | Wirkung |
|---|---|
| unter 4 Jahre | Erfahrung +30 % |
| über 14 Jahre | Erfahrung −30 % |
| ab 18 Jahre | Tempo −8 % |
| ab 24 Jahre | Tempo −15 % |

---

## Satteltaschen

Pferde (nicht Esel und Maultiere) haben **6 Inventarplätze**:

- Pro **16 Stärke** ist ein Platz frei.
- Gesperrte Plätze zeigen ein Schloss.
- Items in gesperrten Plätzen lassen sich weiterhin herausnehmen.
- Die Items werden gespeichert und fallen beim Tod des Pferdes heraus.

Esel und Maultiere behalten ihre Truhe mit 15 Plätzen.

---

## Hunger, Ruhen und Erschöpfung

### Hunger

- **Sättigung von 0–100.** Ein ruhendes Pferd wird in etwa zwei Minecraft-Tagen von satt zu hungrig.
- Beim Reiten sinkt sie je nach Gangart schneller.
- Rasse, Wesen, Ausdauer und die Gamerule `hunger_rate` beeinflussen den Verbrauch.

| Futter | Sättigung |
|---|---|
| Zucker | +3 |
| Roter Pilz | +5 |
| Weizen | +6 |
| Apfel, Karotte | +8 |
| Goldene Karotte | +15 |
| Goldener Apfel | +25 |
| Heuballen | +35 |
| Verzauberter goldener Apfel | +40 |
| Futtertrog (pro Stufe) | +25 |

Hungrige Pferde **grasen** von selbst und laufen zu einem gefüllten **Futtertrog**.

| Sättigung | Folge |
|---|---|
| unter 25 | Kein Galopp oder Renngalopp, Warnung beim Reiten |
| unter 20 | Legt sich deutlich öfter hin |
| unter 10 | Nur noch Schritt und Trab |
| 0 | **Erschöpft**: liegt auf der Seite und lässt sich erst nach dem Füttern wieder reiten |

### Ruhen

- Pferde legen sich gelegentlich hin, nachts häufiger, verletzt öfter.
- Sie stehen auf, wenn du aufsteigst, sie verletzt werden oder du pfeifst.
- Nach dem Aufstehen strecken sie sich.

---

## Futtertrog

Der **Futtertrog** füttert deine Pferde automatisch.

- **Befüllen:** Rechtsklick mit einem **Heuballen** (+2 Stufen) oder **3 Weizen** (+1 Stufe). Bis zu 4 Stufen, die Füllung ist im Trog zu sehen.
- **Fressen:** Gezähmte Pferde unter 60 % Sättigung im Umkreis von 10 Blöcken laufen hin und fressen eine Stufe (+25 Sättigung). Danach machen sie 10 Sekunden Pause.
- Angeleinte Pferde gehen nicht zum Trog.

---

## Hufeisen und Untergrund

**Beschlagen:**

- Öffne das Pferdeinventar (Schleichen + Rechtsklick auf ein gezähmtes Pferd).
- Unter dem Sattel- und Rüstungsplatz gibt es einen **Hufeisen-Slot** mit einem Hufeisen-Symbol. Lege die Hufeisen dort hinein, per Drag-and-drop oder mit Shift-Klick.
- Hufeisen haben eine **Haltbarkeit** (Eisen 3.000, Diamant 9.000). Jeder gerittene Block kostet einen Punkt (mit Haltbarkeit weniger), der Haltbarkeitsbalken ist am Item zu sehen. Sind sie abgelaufen, verschwinden sie.
- Herausnehmen geht jederzeit. Stirbt das Pferd, fallen die Hufeisen heraus.
- Fohlen können nicht beschlagen werden.

| Hufeisen | Haltbarkeit | Auf Stein | Überall | Verletzungsrisiko |
|---|---|---|---|---|
| Unbeschlagen | – | −10 % Tempo | – | normal |
| Eisen | ca. 3.000 Blöcke | normal | – | −20 % |
| Diamant | ca. 9.000 Blöcke | **+5 %** | **+2 %** | **−50 %** |

**Untergrund** (mit Gamerule `terrain_speed`):

| Untergrund | Tempo |
|---|---|
| Trampelpfad | **+10 %** |
| Gras, Erde und Ähnliches | normal |
| Kies | −5 % |
| Schnee, Pulverschnee | −15 bis −20 % |
| Sand, roter Sand | −20 % |
| Schlamm | −35 % |
| Seelensand, Seelenerde | −40 % |
| Stein, Tiefenschiefer, Netherrack usw. | je nach Hufeisen |

Unbeschlagen im Galopp auf Stein kann sich ein Pferd mit der Zeit verletzen.

### Verzauberungen

Hufeisen lassen sich am **Zaubertisch** und am **Amboss** mit Büchern verzaubern. Die Wirkung gilt, solange die Hufeisen im Hufeisen-Slot liegen.

| Verzauberung | Max. Stufe | Wirkung |
|---|---|---|
| **Haltbarkeit** | III | Hufeisen nutzen sich langsamer ab, wie bei Werkzeugen |
| **Reparatur** | I | Erfahrungskugeln, die du **beim Reiten** aufsammelst, reparieren die Hufeisen. In der Hand funktioniert es wie gewohnt. |
| **Leichtfüßigkeit** | III | +3 % Tempo pro Stufe (max. +9 %) |
| **Federhuf** | III | +5 % Sprungkraft pro Stufe (max. +15 %) |
| **Durchhaltevermögen** | III | +5 Ausdauer pro Stufe (max. +15) |

Leichtfüßigkeit, Federhuf und Durchhaltevermögen gibt es auch als Bücher beim Bibliothekar. Alle drei lassen sich kombinieren. Wie die Level kommen sie auf die angeborenen Werte obendrauf, ein Pferd auf Level 50 mit Leichtfüßigkeit III ist also +24 % schneller als ohne.

---

## Verletzungen

Mit der Gamerule `injuries` (standardmäßig an) kann ein Pferd **lahmen**. Ursachen:

- ein Sturz aus mehr als 6 Blöcken,
- Renngalopp bis zur völligen Erschöpfung,
- unbeschlagen im Galopp auf hartem Untergrund.

Ein lahmendes Pferd:

- schafft höchstens **Trab**,
- humpelt sichtbar,
- heilt nach etwa 5 Minuten von selbst, im Liegen viermal so schnell.

**Sofort heilen** kannst du es mit einer **goldenen Karotte** oder einem **goldenen Apfel**. Mut und Hufeisen senken das Risiko.

---

## Pfeifen

Die **Pferdepfeife** ist ein Item, das du craftest (siehe [Rezepte](#rezepte)). Rechtsklick damit, und dein nächstes gezähmtes Pferd in Rufweite kommt zu dir:

- Die Rufweite liegt bei 48 Blöcken, mit voller Bindung bei bis zu 128.
- Liegende Pferde stehen auf, angeleinte werden losgemacht.
- Erschöpfte Pferde können nicht kommen.
- Nach jedem Pfiff braucht die Pfeife 3 Sekunden Abklingzeit, sichtbar am Item.

---

## Pferdebuch

Das **Pferdebuch** ist ein Item, das du craftest (siehe [Rezepte](#rezepte)). Rechtsklick damit öffnet eine Liste aller deiner gezähmten Pferde mit:

- Name, Level und Rasse,
- Tempo, Ausdauer, Stärke und Bindung,
- Sättigung,
- letztem bekannten Standort mit Dimension.

Die Liste wird pro Welt gespeichert. Ein Pferd verschwindet daraus, wenn es stirbt.

---

## Stammbaum

Gezüchtete Pferde merken sich ihre **Eltern** (mit Rasse, Tempo, Ausdauer und Stärke) und ihre **vier Großeltern**. Der Stammbaum steht links neben dem Pferdeinventar. Wilde Pferde heißen dort „Wildfang“.

---

## Pferderennen

Admins (Rechtestufe Spielleiter) legen Strecken an:

| Befehl | Wirkung |
|---|---|
| `/horserace create <name>` | Legt eine Strecke an, der Start ist deine Position |
| `/horserace finish <name>` | Setzt das Ziel auf deine Position |
| `/horserace remove <name>` | Löscht eine Strecke |
| `/horserace list` | Zeigt alle Strecken (für alle Spieler) |
| `/horserace top <name>` | Zeigt die Bestenliste (für alle Spieler) |

**Ablauf eines Rennens:**

1. Reite auf einem Pferd in den Startbereich (4 Blöcke Radius).
2. Sobald du ihn verlässt, läuft die Zeit. Sie steht in der Aktionsleiste.
3. Im Zielbereich wird die Zeit gestoppt und in die **Top 10** eingetragen.

Absteigen bricht das Rennen ab. Nach 20 Minuten verfällt es automatisch.

---

## Animationen, Sounds und Partikel

### Fortbewegung

- **Fußfolge pro Gangart:**
  - Schritt: Viertakt
  - Trab: diagonal
  - Kanter: Dreitakt
  - Galopp: Viertakt
  - Renngalopp: gestreckter Galopp
- **Eigene Schrittlänge pro Gangart**, mit weichen Übergängen.
- **Angedeutetes Knie**, Körperfedern und Schaukeln, Kopfnicken im Takt.
- **Renngalopp:** gestreckter Hals, angelegte Ohren, wehender Schweif.
- **Weitere Bewegungen:**
  - Springen mit angezogenen Beinen
  - Paddeln beim Schwimmen
  - Kurvenlage
- **Lahmen:** Das verletzte Bein macht kürzere Schritte, der Kopf nickt beim Auftreten.

### Schweif und Mähne

- Der **Schweif** schwingt mit Trägheit. Er schleudert in Kurven nach außen, hebt sich beim Beschleunigen und pendelt nach.
- Die **Mähne** weht abhängig vom Tempo im Wind.

### Hufschläge und Staub

- **Hufschläge** erklingen genau im Takt der Fußfolge, passend zum Untergrund: Holz klingt anders, im Galopp kommt der Galopp-Sound.
- Ab Kanter wirbelt jeder Huf **Staub** vom Boden auf. Auf Sand, Erde, Kies und Schnee ist es mehr.
- Im flachen Wasser **spritzt** es.

### Ruhe und Verhalten

- Atmen, Umschauen, zuckende Ohren
- Kauen beim Grasen
- Hinlegen, Schlafen, Liegen auf der Seite bei Erschöpfung mit schwerem Atem
- Hungrige Pferde lassen Kopf, Ohren und Schweif hängen

### Einzelne Animationen

| Animation | Wann |
|---|---|
| Kopfschütteln mit Schnauben | Zufällig, bei abgelehntem Futter oder beim Herunterschalten aus dem Galopp |
| Scharren | Zufällig, besonders bei wilden Pferden in deiner Nähe |
| Bocken | Wenn ein wildes Pferd dich abwirft |
| Kuscheln | Nach Streicheln, Bürsten und Zähmen |
| Schütteln | Nach dem Verlassen des Wassers |
| Strecken | Nach dem Aufstehen |
| Hufstampfen | Zufällig, häufiger bei gesattelten, wartenden Pferden |
| Kopf hochwerfen | Ungeduldig beim Warten, beim Wechsel in den Renngalopp und wenn du pfeifst |

---

## Tasten

| Taste | Funktion | Änderbar unter |
|---|---|---|
| **W** / **S** (beim Reiten) | Gangart hoch / runter | Steuerung → Bewegung |

Die Mod bringt keine eigenen Tasten mit. Pferdebuch und Pferdepfeife sind Items.

---

## Kreativ-Tab

Im Kreativmodus gibt es einen eigenen Tab **„Pferde“** mit allem, was zu Pferden gehört:

- Pferdebuch, Pferdepfeife, Futtertrog, Eisen- und Diamanthufeisen
- Sattel und alle Pferderüstungen (Leder, Kupfer, Eisen, Gold, Diamant, Netherit)
- Leine, Namensschild, Pinsel, Truhe für Esel und Maultiere
- Futter: Heuballen, Weizen, Zucker, Apfel, Karotte, goldene Karotte, goldener Apfel
- Tränke der Ausdauer (normal und verlängert, jeweils als Trank, Wurftrank und Verweiltrank)
- Spawn-Eier für Pferd, Esel, Maultier, Skelett- und Zombiepferd

Die Vanilla-Items bleiben zusätzlich in ihren normalen Tabs.

---

## Rezepte

**Pferdebuch** (formlos)
```
Buch + Leder + Weizen
```

**Pferdepfeife** (formlos)
```
Eisennugget + Eisennugget + Faden
```

**Futtertrog**
```
S   S        S = beliebige Holzstufe
S   H   S    H = Heuballen
```

**Eisenhufeisen**
```
I   .   I    I = Eisenbarren
N   .   N    N = Eisennugget
N   I   N
```

**Diamanthufeisen**
```
D   .   D    D = Diamant
D   E   D    E = Eisenhufeisen
```

**Trank der Ausdauer** (Braustand)
```
Seltsamer Trank + Heuballen          → Trank der Ausdauer
Trank der Ausdauer + Redstone        → verlängert
```

---

## Welche Tiere betroffen sind

| Tier | Zähmung | Rasse, Werte, Hunger, Gangarten, Hufeisen | Satteltaschen | Animationen |
|---|---|---|---|---|
| Pferd | ✔ | ✔ | ✔ (6 Plätze) | ✔ |
| Esel | ✔ | ✔ (ohne Rasse) | Vanilla-Truhe | ✔ |
| Maultier | ✔ | ✔ (ohne Rasse) | Vanilla-Truhe | ✔ |
| Skelett- und Zombiepferd | Vanilla | – | – | Gangarten-Animationen |
| Lama, Kamel | Vanilla | – | – | Vanilla |

---

## Mehrspieler

- Die Mod muss auf **Server und allen Clients** installiert sein.
- Alle Zustände (Zähmung, Werte, Gangart, Energie, Ruhen, Verletzungen, Stammbaum) kommen vom Server.
- Hufschläge, Staub und Animationen berechnet jeder Client selbst, alle sehen und hören dasselbe.
- Gamerules gelten für alle Spieler der Welt.

---

## Aus dem Quellcode bauen

Voraussetzung sind **Java 25** und **Gradle 9.8.0** oder neuer. Die Version ist in `gradle/wrapper/gradle-wrapper.properties` eingetragen, deine IDE lädt sie automatisch.

```bash
gradle build        # Mod bauen, die .jar liegt danach in build/libs/
gradle runClient    # Testclient starten
gradle runServer    # Testserver starten
```

---

## Projektstruktur

```
src/main/java/dev/pizzawunsch/advancedhorses/
├── AdvancedHorses.java              Einstiegspunkt: Registrierung, Events, Befehle
├── access/                          Schnittstelle zu den Zusatzdaten der Pferde
├── anim/                            Fußfolgen, Einzelanimationen, Hufschläge, Schweifphysik
├── block/                           Futtertrog
├── book/                            Pferdebuch (Register und Speicherung)
├── horse/                           Gangarten, Werte, Rassen, Wesen, Hufeisen, Hunger,
│                                    Energie, Bindung, Level, Hufeisen-Verzauberungen, Alter,
│                                    Verletzungen, Stammbaum, Pfeifen
├── inventory/                       Satteltaschen-Plätze mit Sperre, Hufeisen-Slot
├── item/                            Pferdebuch, Pferdepfeife
├── network/                         Pakete (Gangart, Pferdebuch, Benennen)
├── race/                            Rennstrecken, Zeitmessung, /horserace
├── registry/                        Items, Blöcke, Effekte und Tränke, Kreativ-Tab, Gamerules
├── taming/                          Bedürfnisse, Zähmung, Interaktionen
├── mixin/                           Eingriffe in Pferd, Pferdeinventar, Abwurf-Verhalten und Reparatur
│   └── client/                      Eingriffe in Modell, Renderer und Inventarbildschirm
└── client/
    ├── AdvancedHorsesClient.java    Client-Einstiegspunkt
    ├── anim/                        Berechnung aller Modellanimationen
    ├── gait/                        Gangschaltung und Reit-Anzeige
    ├── hud/                         Zähmungs-, Werte- und Stammbaum-Panel
    └── screen/                      Namensfenster und Pferdebuch

src/main/resources/
├── assets/advancedhorses/           Sprachen, Modelle, Texturen, Blockstates, Effekt-Symbol
└── data/                            Rezepte, Brau-Rezepte, Verzauberungen, Loot-Table, Tags
```

---

## Gespeicherte Daten

**Im Pferd** (NBT):

| Schlüssel | Inhalt |
|---|---|
| `AdvancedHorsesHUNGER`, `…ENERGY`, `…BOND` | Sättigung, Energie, Bindung |
| `AdvancedHorsesSTAMINA`, `…STRENGTH` | Angeborene Ausdauer und Stärke |
| `AdvancedHorsesLEVEL`, `…XP` | Level und Erfahrung bis zum nächsten Level |
| `AdvancedHorsesSHOE_SPEED`, `…SHOE_JUMP`, `…SHOE_STAMINA` | Verzauberungsstufen der eingelegten Hufeisen |
| `AdvancedHorsesBREED`, `…TRAIT`, `…SHOES`, `…FLAGS`, `…AGE` | Rasse, Wesen, Hufeisen, Zustände, Alter |
| `AdvancedHorsesShoe` | Die eingelegten Hufeisen (Item inklusive Haltbarkeit) |
| `AdvancedHorsesBirth`, `…LameTicks`, `…ShoeWear` | Geburtszeit, Heilungsdauer, angefangener Block der Abnutzung |
| `AdvancedHorsesPedigree` | Stammbaum |
| `AdvancedHorsesRest`, `…RestTicks` | Ruhezustand |
| `AdvancedHorsesItems` | Satteltaschen |
| `AdvancedHorsesNeeds`, `…Points0–2`, `…LastProgress` | Zähmungsfortschritt |

**Im Weltordner:**

| Datei | Inhalt |
|---|---|
| `advancedhorses_horses.json` | Pferdebuch aller Spieler |
| `advancedhorses_races.json` | Rennstrecken und Bestenlisten |

Bestehende Welten funktionieren weiter. Pferde ohne diese Daten bekommen beim ersten Laden Rasse, Wesen, Alter und Werte zugewürfelt.

---

## Bekannte Einschränkungen

- Das Pferdemodell hat keine Kniegelenke. Gebeugte Beine werden durch Verkürzen angedeutet.
- Schweif und Mähne sind im Vanilla-Modell starre Teile. Sie schwingen und wehen als Ganzes, sind aber nicht in Glieder unterteilt.
- Das Pferdebuch kennt Standorte nur von Pferden, die zuletzt geladen waren.
- Rufen funktioniert nur für Pferde in geladenen Chunks.
- Andere Mods, die das Pferdemodell oder das Reitverhalten stark verändern, können sich mit dieser Mod beißen.

---

## Lizenz

Alle Rechte vorbehalten. Siehe [`LICENSE.txt`](LICENSE.txt).
