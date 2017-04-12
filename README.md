# SmartSmells

[![build status](https://gitlab.com/arturbosch/SmartSmells/badges/master/build.svg)](https://gitlab.com/arturbosch/SmartSmells/commits/master)
[ ![Download](https://api.bintray.com/packages/arturbosch/code-analysis/SmartSmells/images/download.svg) ](https://bintray.com/arturbosch/code-analysis/SmartSmells/_latestVersion)

### Info

SmartSmells is a code smell detector for java powered by javaparser.
Current found smells are:

- GodClass
- FeatureEnvy
- DataClass
- LongMethod
- LongParameterList
- ComplexMethod
- LargeClass
- CommentSmell/Javadoc
- Cycle
- Deadcode
- MessageChain
- MiddleMan
- StateChecking
- ShotgunSurgery
- NestedBlockDepth
- ComplexCondition

### Installation

- cd {path}/SmartSmells
- gradle build to verify compilation and that all tests run
- gradle fatJar to build an executable jar

### Usage

#### Using the commandline jar

```
Usage: SmartSmells [options]
  Options:
    --config, -c
      Point to your SmartSmells configuration file. Prefer this over -f if 
      only specified detectors are needed. Take a look at the 
      default-config.yml file within SmartSmells git repository for an 
      example. 
    --filters, -f
      Regex expressions, separated by a comma to specify path filters eg. 
      '.*/test/.*' 
    --fullStack, -fs
      Use all available detectors with default thresholds.
    --groovy-config, -gc
      Point to your SmartSmells groovy-based configuration file. If using 
      groovy config, no other parameters are necessary as everything can be 
      configured through the groovy dsl. Take a look at the 
      default-config.groovy file for an example.
    --help, -h
      Shows this help message.
    --input, -i
      Specify a path where your project is located for the analysis.
    --output, -o
      Point to a path where the xml output file with the detection result 
      should be saved.
```

1. with groovy configuration

`java -jar SmartSmells.jar --groovy-config /path/to/groovy/config`

Take a look at the default-config.groovy file in this repository.

2. with yaml configuration and arguments

`java -jar SmartSmells.jar --input /path/to/project [--output /path/to/output/xml] --config /path/to/yaml/config [--filters .*/test/.*]`

Take a look at the default-config.yaml file in this repository.

Loading detectors from configuration file cannot be used together with --fullStack mode. Config comes first.

3. without configurations

`java -jar SmartSmells.jar --fullstack --input /path/to/project [--output /path/to/output/xml] [--filters .*/test/.*]`


##### Filters
Filters can help you sort out paths which are not relevant for your project eg. test data. 

It is recommended to filter test cases in your analysis. Test code differs too much from production code.
For example feature envy is present in every test method as test cases should be isolated and 
understandable by itself with minimum cohesion. 
Tests have other kinds of code smells which are not supported by SmartSmells (yet).

#### As Gradle task

Add following lines to your `build.gradle` file to create a `gradle smartsmells` task which can be executed from the console.

```groovy
repositories {
    maven {
        url  "http://dl.bintray.com/arturbosch/code-analysis"
    }
}

configurations {
    smartsmells
}

task smartsmells(type: JavaExec) {
    main = "io.gitlab.arturbosch.smartsmells.Main"
    classpath = configurations.smartsmells
    def input = "$project.projectDir.absolutePath/src/main"
    def baseDir = "$project.projectDir/reports/"
    def output = "$baseDir/smartsmells.xml"
    def params = [ '-i', input, '-o', output, '-fs', '-f', ".*/test/.*"]
    args(params)
}

dependencies {
    smartsmells 'io.gitlab.arturbosch.smartsmells:SmartSmells:1.0.0.[Milestone]'
}
```

As you can see above the input and output parameters must be specified.

You can also use the groovy-config for the gradle task

```
task smartsmells(type: JavaExec) {
    main = "io.gitlab.arturbosch.smartsmells.Main"
    classpath = configurations.smartsmells
    def params = [ '-gc', 'path/to/groovy/config]
    args(params)
}
```

