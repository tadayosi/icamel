# ICamel &mdash; Jupyter kernel for Apache Camel

This kernel enables you to run [Apache Camel](https://camel.apache.org/) routes in Jupyter notebooks.

## Prerequisites

- [Jupyter](https://jupyter.org/install)
- Java >= 11

## Supported Camel route languages

All [route languages](https://camel.apache.org/camel-k/latest/languages/languages.html) supported by [Camel K](https://camel.apache.org/camel-k/latest/index.html) except YAML are supported by ICamel. See the following links to learn how to write a Camel route with each language.

- [Groovy](https://camel.apache.org/camel-k/latest/languages/groovy.html)
- [Kotlin](https://camel.apache.org/camel-k/latest/languages/kotlin.html)
- [JavaScript](https://camel.apache.org/camel-k/latest/languages/javascript.html)
- [Java](https://camel.apache.org/camel-k/latest/languages/java.html)
- [XML](https://camel.apache.org/camel-k/latest/languages/xml.html)

The opinionated default language for ICamel is JavaScript. To use other languages than JavaScript or XML, prepend a comment line `// language=...` at the beginning of each cell.

- Groovy
  
  ```groovy
  // language=groovy
  from('timer:tick')
      .process { it.in.body = 'Hello Camel K!' }
      .to('log:info')
  ```
  
- Kotlin
  
  ```kotlin
  // language=kts
  from("timer:tick")
      .process { e -> e.getIn().body = "Hello Camel K!" }
      .to("log:info")
  ```
  
- java
  
  ```java
  // language=java
  import org.apache.camel.builder.RouteBuilder;
  
  public class Sample extends RouteBuilder {
      @Override
      public void configure() throws Exception {
          from("timer:tick")
              .setBody()
                .constant("Hello Camel K!")
              .to("log:info");
      }
  }
  ```

## Installing

Download the latest `icamel-0.x.jar` from https://github.com/tadayosi/icamel/packages.

Then create a directory `camel` under the Jupyter kernels directory:

    mkdir `jupyter --data-dir`/kernels/camel

and copy `target/icamel-0.x.jar` into the directory:

    cp target/icamel-0.x.jar `jupyter --data-dir`/kernels/camel/

Finally, create a file `kernel.json` with the following content under the `camel` kernel directory. Note `<your-camel-kernel-dir>` needs to be substituted with the actual path (e.g. `/home/username/.local/share/jupyter/kernels/camel`):

```json
{
  "argv": [
    "java",
    "-jar",
    "<your-camel-kernel-dir>/icamel-0.x.jar",
    "{connection_file}"
  ],
  "display_name": "Camel",
  "language": "camel",
  "interrupt_mode": "message",
  "env": {
  }
}
```

## Build from source

Run the following command:

    mvn clean install
