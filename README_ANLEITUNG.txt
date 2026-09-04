MR BEAUTY – CUBE DIAGNOSE APP

ZWECK
Diese kleine App verändert die Kassen-App nicht.
Sie misst direkt auf dem miniPOS Cube:

- echte physische Bildschirmauflösung in Pixeln
- App-Fensterbreite/-höhe
- Android density
- Android densityDpi
- scaledDensity
- xdpi / ydpi
- verwendete Android-Dichteklasse (mdpi/hdpi/xhdpi/xxhdpi/xxxhdpi)
- Launcher-Icon-Grösse in Pixeln
- Launcher-Icon-Dichte
- WebView window.innerWidth / innerHeight
- WebView devicePixelRatio
- screen.width / screen.height
- visualViewport-Werte

DAMIT KÖNNEN WIR DANACH EXAKT BESTIMMEN:
1. welche Icon-Ressource der Cube verwendet,
2. welche Pixelgrösse für das Startbildschirm-Logo sinnvoll ist,
3. wie 74×42 CSS-Pixel im Kassen-WebView tatsächlich auf Geräte-Pixel abgebildet werden.

GITHUB
Empfohlener Repo-Name:
cube-diagnose

Commit:
MR Beauty Cube Diagnose 1.0.0

Upload:
Den INHALT dieses ZIPs direkt ins Root des neuen GitHub-Repos hochladen.

Danach:
Actions -> Build Cube Diagnose APK -> Run workflow

Nach erfolgreichem Build:
Actions -> Build -> Artifacts -> Cube-Diagnose-APK herunterladen.

Auf dem Cube APK installieren, App "Cube Diagnose" öffnen,
den kompletten Ergebnisbildschirm fotografieren und in ChatGPT hochladen.

WICHTIG
Diese Diagnose-App verwendet KEIN Firebase, KEIN Worldline und greift
nicht auf die Kassen-Daten zu.
