![Jarviz](../jarviz-logo.png)

# Jarviz Library

This Java library scans the Java [bytecode](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html) of binary artifacts using a custom classloader and generates the dependency coupling data as a [JSON Lines (.jsonl)](http://jsonlines.org) file. Currently only JAR and WAR artifact formats are supported. To find the dependency couplings, Jarviz analyzes the [opcodes](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html) using [ASM](https://asm.ow2.io) bytecode analysis framework.


### Sample Coupling Data

```json
{
  "appSetName": "FooPortfolio",
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

## JVM Internals

Java method level couplings happen when a method of a particular class (call site) invokes a target method of another class (receiver). In order to view these couplings, analysing bytecode is a better way compared to analysing the source code. This approach is called the static code analysis of `bytecode` (compiled Java code that a JVM can interpret). The compiler will modify, re-arrange and optimize the source code at compilation. It will also generate metadata in the `bytecode`. This metadata can be further leveraged to identify more information about the dependency couplings. Therefore the dependency couplings identified by static code analysis of `bytecode` is almost identical to the dependency couplings seen by the JVM at runtime.

The Java virtual machine supports following invoke commands (`opcodes`) which can ultimately result in generating couplings:

- `invokestatic` - Invokes a class (static) methods.
- `invokespecial` - Invokes instance initialization methods, private methods and methods of a superclass of the current class.
- `invokevirtual` - Invokes public and protected non-static methods via dynamic dispatch.
- `invokeinterface` - This is similar to `invokevirtual` except for the method dispatch being based on an interface type.
- `invokedynamic` - Invokes dynamic methods (lambdas) to facilitates the implementation of dynamic languages.

Analysing `invokedynamic` can be tricky as the method handle can change dynamically at runtime. Java provides a performance optimization for these `invokedynamic` invocations by providing a bootstrap method handle initially, and executes that method handle if the call site does not change. We are using that strategy to evaluate usages from `invokedynamic`, but it should be noted that static code analysis may not be accurate if the code is extensively using `invokedynamic` calls to change behavior at runtime.

### Java Class Naming

Java source specification and JVM specification use different formats to define fully qualified Java class names. Java source specification uses the format of `com.foo.bar.MyClass` and JVM specification uses the format of `com/foo/bar/MyClass`. Jarviz removes this confusion by always using the Java source specification format ( i.e. `com.foo.bar.MyClass`) while internally converting between two formats when necessary.

### Jarviz Class Loader

Jarviz creates a custom class loader using the [bootstrap class loader](https://docs.oracle.com/javase/8/docs/technotes/tools/findingclasses.html) as the parent, to safely load and unload the classes from the artifact files. The bootstrap classloader is mainly responsible for loading JDK internal classes, typically `rt.jar` and other core libraries located in `$JAVA_HOME/jre/lib` directory. Given that the classes from the artifact have not been already loaded in the bootstrap classloader, we can guarantee that there are no collisions. A new classloader is created for each artifact before the analysis and destroyed along with the loaded classes after the analysis.

## Usage

```shell
$ java -cp <Jarviz JAR> "com.vrbo.jarviz.AnalyzeCommand" -c <Config File> -a <Artifact File> -f <Filter FIle> -o <Output File>
```

### Parameters

```
 -c, --config  <arg>       Path to the configurations file (JSON)
 -a, --artifacts  <arg>    Path to the file containing list of artifacts (JSON)
 -f, --filter  <arg>       Path to the coupling filter configuration file (JSON)
 -o, --output  <arg>       Path to the newline-delimited JSON output file (.jsonl)
 -h, --help                Display help
 -v, --version             Display version
```

**Examples**

To analyze Java artifacts:
```shell
$ java -cp "jarviz-shaded.jar" "com.vrbo.jarviz.AnalyzeCommand" -c config.json -a artifacts.json -f filter.json
```
To get help:
```shell
$ java -cp "jarviz-shaded.jar" "com.vrbo.jarviz.AnalyzeCommand" -h
```
To get version:
```shell
$ java -cp "jarviz-shaded.jar" "com.vrbo.jarviz.AnalyzeCommand" -v
```

### Exit Status

| Status   | Description               |
| :------: | ------------------------- |
|   0      | Successful                |
|   1      | CLI initialization failed |
|   2      | Analyser failed           |


## File Formats

Jarviz uses JSON file format for input and output files.

### Configuration File

This is an input file for Jarviz containing configurations needed to run the command.


#### Sample
```json
{
  "artifactDirectory": "/tmp/jarviz/artifacts",
  "mavenTimeOutSeconds": 300,
  "continueOnMavenError": false
}
```

See the full sample file: [config.json](../jarviz-cli/samples/config.json)

#### Fields

- `artifactDirectory` - Where local copies of the artifacts are stored. This directory will also be used to save the downloaded artifacts from a remote Maven repository.
- `mavenTimeOutSeconds` - Set the time out for the Maven process to prevent it from hanging indefinitely. Default is 5 minutes (300).
- `continueOnMavenError` - Tells the analyzer whether to continue or stop running if it encounters and error when downloading artifacts.

#### Java References

| Link                                                                                                  |
| ----------------------------------------------------------------------------------------------------- |
| [`com.vrbo.jarviz.config.JarvizConfig`](src/main/java/com/vrbo/jarviz/config/JarvizConfig.java)       |
| [`com.vrbo.jarviz.config.LocalRepoConfig`](src/main/java/com/vrbo/jarviz/config/LocalRepoConfig.java) |
| [`com.vrbo.jarviz.config.NexusConfig`](src/main/java/com/vrbo/jarviz/config/NexusConfig.java)         |


### Artifacts File

The artifacts file is an input file that specifies the application set holding the collection of artifacts to be analyzed. The file name should be passed to the tool using `-a` parameter. The concepts of "application set" and "application" are very loosely defined. An artifact is a direct representation of a Java binary (a JAR or a WAR file). It is up to the user to define a proper hierarchical structure for the application set, applications and artifacts. The dependency analysis does not depend on the user-defined hierarchy, only the graphical output format will be affected.

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

See the full sample file: [artifacts.json](../jarviz-cli/samples/artifacts.json)

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

#### Java References

| Link                                                                                              |
| ------------------------------------------------------------------------------------------------- |
| [`com.vrbo.jarviz.model.ApplicationSet`](src/main/java/com/vrbo/jarviz/model/ApplicationSet.java) |
| [`com.vrbo.jarviz.model.Application`](src/main/java/com/vrbo/jarviz/model/Application.java)       |
| [`com.vrbo.jarviz.model.Artifact`](src/main/java/com/vrbo/jarviz/model/Artifact.java)             |


### Filter File

The filter file is an input file that specifies the RegEx patterns to filter the couplings found at analysis step, before generating the output data. The file name should be passed to the tool using `-f` parameter.

#### Sample

This is including all the couplings to any method in packages with name prefixed with `"com.foo"`. This will also exclude the internal references within those packages (i.e. circular dependency couplings within `"com.foo"`).

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

See the full sample file: [filter.json](../jarviz-cli/samples/filter.json)

#### Fields

- `sourcePackage` - Optional RegEx pattern to match with the package name (of the source class) in the coupling.
    - e.g. `^(com\\.xyz\\.foo|com\\.xyz\\.bar).*$` - To match any source package name starting with `com.xyz.foo` or `com.xyz.bar` (including sub-packages).
- `sourceClass` - Optional RegEx pattern to match the name of the source class in the coupling. This matches against the simple class name (i.e. not the fully-qualified class name).
    - e.g. `^(ABC|Xyz|Hello)$` - To match any source class name to `ABC`, `Xyz` or `Hello`.
- `sourceMethod` - Optional RegEx pattern to match with the method name (of the source class) in the coupling.
    - e.g. `^(get|set|is)Token$` - To match a method name to `getToken`, `setToken` or `isToken`.
- `targetPackage` - Optional RegEx pattern to match with the package name (of the target class) in the coupling.
    - e.g. `^(com\\.xyz\\.foo|com\\.xyz\\.bar)$` - To exactly match any target package to `com.xyz.foo` or `com.xyz.bar` (excluding sub-packages).
- `targetClass` - Optional RegEx pattern to match the name of the target class in the coupling. This matches against the simple class name (i.e. not the fully-qualified class name).
    - e.g. `^MyClass[1-4]$` - To match any target class name to `MyClass1`, `MyClass2`, `MyClass3` or `MyClass4`.
- `targetMethod` - Optional RegEx pattern to match with the method name (of the target class) in the coupling.
    - e.g. `^myMethod$` - To exactly match a method name to `myMethod`.

#### Java References

| Link |
| ---- |
| [`com.vrbo.jarviz.model.CouplingFilter`](src/main/java/com/vrbo/jarviz/model/CouplingFilter.java) |



### Coupling Data Output File

The output of `analyze` is a JSON Lines ([`.jsonl`](http://jsonlines.org/)) file containing the dependency coupling data. If Jarviz finds a lot of dependencies, this file can be large and the `.jsonl` format makes it easy to stream-process the output.

#### Sample

```json
{"applicationName": "MyApp", "artifactFileName": "foo-product-1.2.1.jar", "artifactId": "foo-product", "artifactGroup": "foo.bar", "artifactVersion": "1.2.1", "sourceClass": "foo.bar.MyClass", "sourceMethod": "doFirstTask", "targetClass": "foo.bar.YourClass", "targetMethod": "getProductId"}
{"applicationName": "MyApp", "artifactFileName": "foo-product-1.2.1.jar", "artifactId": "foo-product", "artifactGroup": "foo.bar", "artifactVersion": "1.2.1", "sourceClass": "foo.bar.MyClass", "sourceMethod": "doSecondTask", "targetClass": "foo.bar.YourClass", "targetMethod": "getProductName"}
...
```

See the full sample file: [sample_jarviz_result.jsonl](../jarviz-cli/samples/sample_jarviz_result.jsonl)

#### Fields

- `appSetName` - Optional name for the application set.
- `applicationName` - The human readable name of the application.
- `artifactFileName` - The file name of the artifact (e.g. `"foo-product-1.2.1.jar"`).
- `artifactId` - The id of the artifact (e.g. `"foo-product"`).
- `artifactGroup` - Group id of the artifact (e.g. `"com.foo.bar"`).
- `artifactVersion` - The version of the artifact (e.g. `"1.2.0"` or `"1.2.1-SNAPSHOT"`).
- `sourceClass` - The fully qualified name of the source class in the coupling.
- `sourceMethod` - The method name (of the source class) in the coupling.
- `targetClass` - The fully qualified name of the target class in the coupling.
- `targetMethod` - The method name (of the target class) in the coupling.

#### Java References

| Link                                                                                              |
| ------------------------------------------------------------------------------------------------- |
| [`com.vrbo.jarviz.model.CouplingRecord`](src/main/java/com/vrbo/jarviz/model/CouplingRecord.java) |


## Development

### Initial Setup

Clone the Jarviz project locally from the GitHub repo, and verify it builds properly.

```shell
$ mvn clean install
```

### IDE Setup
Jarviz makes use of the [Immutables](https://immutables.github.io/) library to auto-generate immutable model classes. If you encounter `ClassNotFound` errors in your IDE try following the appropriate steps outlined in the [Immutables documentation](http://immutables.github.io/apt.html). The generated source code for the models will be in `target/generated-sources` and test code will be in `target/generated-test-sources`. Re-importing the Maven modules into the IDE will be helpful in order to see the generated source in the workspace.

### How to Contribute
If you are enthusiastic about contributing to Jarviz development, please send a pull request.

## Legal

This project is available under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).

Copyright 2020 Expedia, Inc.
