# ICamel &mdash; Jupyter kernel for Apache Camel

This kernel enables you to run [Apache Camel](https://camel.apache.org/) routes in Jupyter notebooks.

## Prerequisites

- [Jupyter](https://jupyter.org/install)
- Java >= 11

## Supported route languages

Currently only [JavaScript](https://camel.apache.org/camel-k/latest/languages/javascript.html) is supported.

## Installing

Build this project:

    mvn clean install

Then create a directory `camel` under the Jupyter kernels directory:

    mkdir `jupyter --data-dir`/kernels/camel

and copy `target/icamel-0.1-SNAPSHOT.jar` into the directory:

    cp target/icamel-0.1-SNAPSHOT.jar `jupyter --data-dir`/kernels/camel/

Finally, create a file `kernel.json` with the following content under the `camel` kernel directory. Note `<your-camel-kernel-dir>` needs to be substituted with the actual path (e.g. `/home/username/.local/share/jupyter/kernels/camel`):

```json
{
  "argv": [
    "java",
    "-jar",
    "<your-camel-kernel-dir>/icamel-0.1-SNAPSHOT.jar",
    "{connection_file}"
  ],
  "display_name": "Camel",
  "language": "camel",
  "interrupt_mode": "message",
  "env": {
  }
}
```
