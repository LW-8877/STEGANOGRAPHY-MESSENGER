# Encrypted Steganography Messenger (Desktop GUI v5)

This project lets you **encrypt a message with a password** and **hide it inside a PNG image**.
You can also open an encoded PNG, enter the password, and recover the message.

## Contents
- `src/` Java source files:
  - `AESUtil.java` – password → AES key (SHA-256), encrypt/decrypt Base64
  - `SteganographyUtil.java` – LSB hide/extract text in PNG
  - `MessengerGUI.java` – Swing desktop app (main class)
  - `ChatMain.java` – CLI fallback (send/read)
- `MANIFEST.MF` – defines `MessengerGUI` as the JAR main class
- `build.bat` / `build.sh` – compile + package scripts
- `launch4j-config.xml` – template to create a Windows `.exe`
- `input.png` – sample carrier image

## Build (Windows)
```
build.bat
```
Result: `messenger.jar`

## Build (Linux/Mac)
```
./build.sh
```

## Run
```
java -jar messenger.jar
```

## CLI Usage (optional)
```
javac -d out src/*.java
java -cp out ChatMain send input.png encoded.png "Hello" mypass
java -cp out ChatMain read encoded.png mypass
```

## Create Windows EXE (Launch4j)
1. Install Launch4j.
2. Open `launch4j-config.xml` in Launch4j GUI.
3. Set absolute paths for:
   - Output file (Messenger.exe)
   - Jar (messenger.jar)
   - Optional icon (.ico)
4. Build wrapper. Done!

## jpackage Alternative (JDK 17+)
```
jpackage --input . --name Messenger --main-jar messenger.jar --type exe
```

## Notes
- Lossless images only (PNG/BMP). JPEG not supported.
- Capacity ≈ (pixels * 3) / 8 characters.
- Encrypted payload is Base64; may be longer than original text.

Educational use only.
