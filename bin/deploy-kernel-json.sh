#!/bin/bash

KERNEL_PATH=$(jupyter --data-dir)/kernels/camel

cat <<EOF > $KERNEL_PATH/kernel.json
{
  "argv": [
    "java",
    "-Dorg.slf4j.simpleLogger.logFile=System.out",
    "-Dorg.slf4j.simpleLogger.showDateTime=true",
    "-Dorg.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss.SSS",
    "-Dorg.slf4j.simpleLogger.defaultLogLevel=INFO",
    "-Dorg.slf4j.simpleLogger.log.io.github.tadayosi.icamel=DEBUG",
    "-Dorg.slf4j.simpleLogger.log.org.apache.camel=WARN",
    "-Dorg.slf4j.simpleLogger.log.com.github.cameltooling.lsp=WARN",
    "-jar",
    "$KERNEL_PATH/icamel-0.4-SNAPSHOT.jar",
    "{connection_file}"
  ],
  "display_name": "Camel",
  "language": "camel",
  "interrupt_mode": "message",
  "env": {
  }
}
EOF

