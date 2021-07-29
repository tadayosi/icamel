#!/bin/bash

KERNEL_PATH=$(jupyter --data-dir)/kernels/camel

cat <<EOF > $KERNEL_PATH/kernel.json
{
  "argv": [
    "java",
    "-Dquarkus.log.console.enable=true",
    "-Dquarkus.log.console.color=true",
    "-Dquarkus.log.level=INFO",
    "-Dquarkus.log.category.\"io.quarkus\".level=WARN",
    "-Dquarkus.log.category.\"org.apache.camel\".level=WARN",
    "-Dquarkus.log.category.\"com.github.cameltooling.lsp\".level=WARN",
    "-Dquarkus.log.category.\"io.github.tadayosi.icamel\".level=DEBUG",
    "-jar",
    "$KERNEL_PATH/icamel-0.4-SNAPSHOT-runner.jar",
    "{connection_file}"
  ],
  "display_name": "Camel",
  "language": "camel",
  "interrupt_mode": "message",
  "env": {
  }
}
EOF

