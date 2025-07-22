#!/usr/bin/env bash
set -e
mkdir -p out
echo "Compiling..."
javac -d out src/*.java
echo "Packaging..."
jar cfm messenger.jar MANIFEST.MF -C out .
echo "Run with: java -jar messenger.jar"
