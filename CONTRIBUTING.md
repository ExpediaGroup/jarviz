# How To Contribute

We'd love to accept your patches and contributions to this project. There are just a few guidelines you need to follow which are described in detail below.

## Fork this repo

You should create a fork of this project in your account and work from there. You can create a fork by clicking the fork button in GitHub.

## One feature, one branch

Work for each new feature/issue should occur in its own branch. To create a new branch from the command line:

```shell
git checkout -b my-new-feature
```

where "my-new-feature" describes what you're working on.

## Verify your changes locally

### jarviz-lib

Use Maven to build the project and verify that projects compiles properly. This project was currently tested with Java 8 and Java 11.

```shell
$ mvn clean install
```

Before opening a pull request, ensure that your new code conforms to the code style as defined by the .editorconfig file in the project.

### jarviz-graph

Use NPM to build the project and verify that projects starts properly.

```shell
$ npm install
$ npm run build:example
```

Verify build functions correctly.

```shell
$ npm install
$ npm run build
```

### jarviz-cli

Run the Jarviz CLI and verify it successfully generates the dependency coupling graph.

```shell
$ ./jarviz graph -f samples/filter.json -a samples/artifacts.json
```

## Add tests for any bug fixes or new functionality

Please make sure that the new changes are covered by unit tests.

## Add documentation for new or updated functionality

Please add appropriate documentation in the README and source code. Also please ask the maintainers to update the wiki with any relevant information.

## Merging your contribution

Create a new pull request and your code will be reviewed by the maintainers. They will confirm at least the following:

-   Tests run successfully (unit, coverage, integration, code style, etc.).
-   Contribution policy has been followed.

A maintainer will need to sign off on your pull request before it can be merged.
