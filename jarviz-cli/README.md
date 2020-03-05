![Jarviz](../jarviz-logo.png)

# Jarviz CLI Tool

Jarviz CLI is a command-line tool designed for \*nix systems to perform dependency analysis for Java applications. Internally it uses both the [Jarviz Java library](../jarviz-lib) and [Jarviz graph tool](../jarviz-graph) to provide a useful command-line interface to the user.

As a prerequisite, Jarviz CLI requires [java](https://openjdk.java.net), [maven](https://maven.apache.org), [node](https://nodejs.org) and [npm](https://www.npmjs.com/get-npm) to be installed.


## Quick Start

- Prerequisite: Verify that [java](https://openjdk.java.net), [maven](https://maven.apache.org), [node](https://nodejs.org) and [npm](https://www.npmjs.com/get-npm) are installed in the system.
  - Java and Maven are required to download Java libraries.
  - Node and NPM are required to download Node modules.
- Checkout the project from GitHub and change the directory to `jarviz-cli` module.
- Run `./jarviz graph -f samples/filter.json -a samples/artifacts.json`
- Open the generated HTML file in the browser.

## Usage

```shell
$ jarviz <command> [parameters]
```

### Commands

- `analyze` - Generates the coupling data as a `.jsonl` file by analyzing Java artifacts.
- `graph` - First generates the coupling data and then generates the HTML graph using that data.

#### Example `analyze` Command

```shell
$ jarviz analyze -a artifacts.json -f filter.json
```

#### Example `graph` Command

```shell
$ jarviz graph -a artifacts.json -f filter.json
```

### Parameters

```
 -a, --artifacts  <arg>     Path to the file containing list of artifacts (JSON)
 -f, --filter  <arg>        Path to the coupling filter configuration file (JSON)
 -h, --help                 To display help
 -v, --version              To display version
```

Version (`-v`) and help (`-h`) parameters are supported by both Jarviz CLI and individual commands.

#### Example

```shell
$ jarviz graph -h
```

```shell
$ jarviz -h
```

### Exit Status

| Status   | Description               |
| :------: | ------------------------- |
| 0        | Successful                |
| 1        | CLI initialization failed |
| 2        | Analyser failed           |

### Versioning

When the Jarviz CLI is run for the first time, it will create a directory at `~/.jarviz` to store the necessary configuration files and executable files. To get the latest versions of Jarviz executables (Java application and the Node.js application), simply check out the latest version of the Jarviz CLI tool and run.

## File Formats

Jarviz CLI uses JSON file format for input and output files.

### Configuration File

This is an input file containing configurations needed to run the tool. By default, Jarviz CLI tool will look for this file at `~/.jarviz/config.json`. A skeletal configuration file will be created when the tool is run for the first time.

#### Sample

```json
{
  "artifactDirectory": "/tmp/jarviz/artifacts"
}
```

#### Fields

- `artifactDirectory` - Where local copies of the artifacts are stored. This directory will also be used to save the downloaded artifacts from a remote Maven repository.


### Artifacts File

The artifacts file is an input file that specifies an application set holding the collection of artifacts to be analyzed. The file name should be passed to the tool using the `-a` parameter. The concepts of “application set” and “application” are loosely defined. An artifact is a direct representation of a Java binary (a JAR or a WAR file). It is up to the user to define a proper hierarchical structure for the application set, applications and artifacts. The dependency analysis does not depend on the user-defined hierarchy, only the graphical output format will be affected.

#### Sample

```json
{
  "appSetName": "FooPortfolio",
  "applications": [
    {
      "appName": "CustomerWidget",
      "artifacts": [
        {
          "artifactId": "user-profile-service",
          "groupId": "com.foo",
          "version": "1.43.2"
        },
        {
          "artifactId": "product-service",
          "groupId": "com.foo.bar",
          "version": "0.0.3",
          "classifier": "logic"
        },
        {
          "artifactId": "cutting-edge-service",
          "groupId": "com.foo.bar",
          "version": "1.0.1-20200114.191052-15",
          "baseVersion": "1.0.1-SNAPSHOT"
        }
      ]
    },
    {
      "appName": "FooProduct",
      "artifacts": [
        {
          "artifactId": "foo-product",
          "groupId": "com.foo",
          "packaging": "war",
          "version": "12.0.0-final"
        }
      ]
    }
  ]
}
```

#### Fields

- `appSetName` - Optional name for the application set.
- `applications` - The list of applications that belong to the application set.
  - `appName` - The human readable name of the application.
  - `artifacts` - The list of artifacts that belong to the application.
    - `artifactId` - The id of the artifact (e.g. `"foo-product"`).
    - `groupId` - Group id of the artifact (e.g. `"com.foo.bar"`).
    - `version` - The version of the artifact (e.g. `"1.2.0"`).
    - `baseVersion`- Optional base version of a snapshot artifact. Checking out a snapshot using Maven can result in a file with version containing timestamp, 
    such as `fooBar-1.0.1-20200708.191052-12.jar` (e.g. `"1.2.1-SNAPSHOT"` where version is set to `"1.0.1-20200708.191052-12"`).
    - `packaging` - Optional packaging type of the artifact. Supported values are `"jar"` (default) and `"war"`.
    - `classifier` - Optional classifier of the artifact (e.g. `"src"`).


### Filter File

The filter file is an input file that contains filters in RegEx format. These filters will control which couplings will be selected in the final result set. The file name should be passed using the `-f` parameter.

#### Sample

This includes couplings with target methods from classes where package name starts with `"com.foo"`. This will also exclude circular dependency couplings within the same package.

```json
{
  "include": {
    "targetPackage": "^(com\\.foo).*$"
  },
  "exclude": {
    "sourcePackage": "^(com\\.foo).*$"
  }
}
```

#### Fields

- `sourcePackage` - Optional RegEx pattern to match with the package name (of the source class) in the coupling.
  - e.g. `^(com\\.xyz\\.foo|com\\.xyz\\.bar).*$` - To match any source package name starting with `com.xyz.foo` or `com.xyz.bar` (including sub-packages).
- `sourceClass` - Optional RegEx pattern to match the name of the source class in the coupling. This matches against the simple class name (i.e. not the fully-qualified class name).
  - e.g. `^(ABC|Xyz|Hello)$` - To match any source class name to `ABC`, `Xyz` or `Hello`.
- `sourceMethod` - Optional RegEx pattern to match the method name of the source class in the coupling.
  - e.g. `^(get|set|is)Token$` - To match a method name to `getToken`, `setToken` or `isToken`.
- `targetPackage` - Optional RegEx pattern to match with the package name (of the target class) in the coupling.
  - e.g. `^(com\\.xyz\\.foo|com\\.xyz\\.bar)$` - To exactly match any target package to `com.xyz.foo` or `com.xyz.bar` (excluding sub-packages).
- `targetClass` - Optional RegEx pattern to match the name of the target class in the coupling. This matches against the simple class name (i.e. not the fully-qualified class name).
  - e.g. `^MyClass[1-4]$` - To match any target class name to `MyClass1`, `MyClass2`, `MyClass3` or `MyClass4`.
- `targetMethod` - Optional RegEx pattern to match with the method name (of the target class) in the coupling.
  - e.g. `^myMethod$` - To exactly match a method name to `myMethod`.

## Coupling Data Output File

The output of the `analyze` step is a [JSON Lines (.jsonl)](http://jsonlines.org/) file. If Jarviz finds a lot of dependencies, this file can be large and then this format makes it easy to stream-process the output.

#### Sample

### Sample Dependency Coupling Data

```json
{
  "applicationName": "MyApp",
  "artifactFileName": "foo-product-1.2.1.jar",
  "artifactId": "foo-product",
  "artifactGroup": "com.foo.bar",
  "artifactVersion": "1.2.1",
  "sourceClass": "foo.bar.MyClass",
  "sourceMethod": "doFirstTask",
  "targetClass": "foo.bar.YourClass",
  "targetMethod": "getProductId"
}↵
...
```

#### Fields

- `applicationName` - The human-readable name of the application.
- `artifactFileName` - The file name of the artifact (e.g. `"foo-product-1.2.1.jar"`).
- `artifactId` - The id of the artifact (e.g. `"foo-product"`).
- `artifactGroup` - Group id of the artifact (e.g. `"com.foo.bar"`).
- `artifactVersion` - The version of the artifact (e.g. `"1.2.0"` or `"1.2.1-SNAPSHOT"`).
- `sourceClass` - The fully-qualified name of the source class in the coupling.
- `sourceMethod` - The method name of the source class in the coupling.
- `targetClass` - The fully-qualified name of the target class in the coupling.
- `targetMethod` - The method name of the target class in the coupling.

#### Java Reference


## Dependency Graph Output File

The output of the `graph` command is an HTML file containing the dependency graph. The graph is generated using the coupling data from the `analyze` step.

#### Sample

<img src="../jarviz-graph/graph.png" width="600" />

## Legal

This project is available under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).

Copyright 2020 Expedia, Inc.
