# Schiffe Versenken (Java Konsole)

Dieses Projekt enthaelt ein voll spielbares **Schiffe-Versenken-Spiel** fuer die Konsole mit sauberer Trennung zwischen:

- **Game Engine** (Regeln, Spiellogik, Trefferauswertung)
- **Console UI** (Benutzereingabe und Anzeige)

Dadurch kannst du spaeter leicht eine GUI (z. B. JavaFX oder Swing) auf die bestehende Logik aufsetzen.

## Features

- 2-Spieler-Modus im Wechsel
- Schiff-Setup pro Spieler: **manuell** oder **zufaellig**
- Robuste Eingabepruefung (Koordinaten, Richtung, Bereich)
- Treffer/Miss/Versenkt-Logik
- Sieg-Erkennung, wenn alle gegnerischen Schiffsfelder getroffen wurden
- Board-Rendering:
  - `S` = eigenes Schiff
  - `X` = Treffer
  - `o` = Fehlschuss
  - `.` = unbekannt
- Schiffe duerfen sich nicht ueberlappen oder direkt beruehren (inkl. diagonal)

## Projektstruktur

- `Main.java` – Konsolenprogramm (Startpunkt)
- `GAME.java` – Spielzustand, aktive Runde, Gewinnerlogik
- `GRID.java` – Spielbrett, Platzierung, Schuesse, Rendering
- `SHIP.java` – Schiff mit Trefferstatus
- `Coordinate.java` – Koordinatenmodell + Parsing (`A1` oder `1,1`)
- `Orientation.java` – Ausrichtung (`H`/`V`)
- `ShotResult.java`, `ShotReport.java` – Schuss-Ergebnisobjekte
- `ShipType.java` – Flottendefinition (Name, Laenge, Anzahl)

## Starten

### Kompilieren

```bash
javac *.java
```

### Ausfuehren

```bash
java Main
```

## GUI-Erweiterung (spaeter)

Die GUI kann direkt auf `GAME`, `GRID`, `SHIP`, `ShotReport` und `Coordinate` zugreifen, ohne Konsolenlogik uebernehmen zu muessen.

Empfohlen fuer GUI:

- Eingaben in der GUI in `Coordinate`/`Orientation` umwandeln
- Spielfeld ueber `render...` Methoden oder direkte Zellabfragen anzeigen
- Schuesse via `GAME.playTurn(...)` ausfuehren
- Turn-Switch und Endscreen ueber `GAME`-Status steuern
