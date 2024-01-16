# Lovely jOOQ

This project contains opinionated helpers and extensions of [jOOQ](https://github.com/jOOQ/jOOQ) used at Lovely Systems.

Built on **jOOQ 3.18.6**.

## Prerequisites

- Java 17 installed and selected by default

## Generating HTML docs

In case you would like to release a new version, it's also important to update the generated docs via Dokka. To do so, you can run:

```shell
./gradlew dokkaHtml
```

The generated docs will be available under [docs/html/index.html](docs/html/index.html).
