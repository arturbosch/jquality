# jquality

[![build status](https://gitlab.com/arturbosch/SmartSmells/badges/master/build.svg)](https://gitlab.com/arturbosch/SmartSmells/commits/master)
[![CodeFactor](https://www.codefactor.io/repository/github/arturbosch/jquality/badge/master)](https://www.codefactor.io/repository/github/arturbosch/jquality/overview/master)
[ ![Download](https://api.bintray.com/packages/arturbosch/code-analysis/SmartSmells/images/download.svg) ](https://bintray.com/arturbosch/code-analysis/SmartSmells/_latestVersion)

### Info

jquality is a code smell detector for java powered by javaparser.
Current found smells are:

- BrainMethod
- ClassDataShouldBePrivate
- CommentSmell/Javadoc
- ComplexCondition
- ComplexMethod
- Cycle
- DataClass
- Deadcode
- FeatureEnvy
- GodClass
- LargeClass
- LongMethod
- LongParameterList
- MessageChain
- MiddleMan
- NestedBlockDepth
- RefusedParentBequest
- ShotgunSurgery
- StateChecking
- TraditionBreaker

### Installation

- cd [repos]/jquality
- gradle build to verify compilation and that all tests run
- gradle shadowJar to build an executable jar

### Usage

#### Using the commandline jar

```
Usage: jquality [options]
  Options:
    --config, -c
      Point to your configuration file. Supported formats are YAML and GROOVY. 
      Take a look at default-config.[yml|groovy]
    --filters, -f
      Regex expressions, separated by a comma to specify path filters eg. 
      '.*/test/.*' 
    --fullStack, --fullstack, -fs
      Use all available detectors with default thresholds.
    --help, -h
      Shows this help message.
      Default: false
    --input, -i
      Specify a path where your project is located for the analysis.
    --metrics, -m
      Additionally runs the metric facade, printing the means for configured 
      metrics. 
      Default: false
    --output, -o
      Point to a path where the xml output file with the detection result 
      should be saved.
```

1. with groovy configuration

`java -jar jquality-[version]-all.jar --groovy-config /path/to/groovy/config`

Take a look at the default-config.groovy file in this repository.

2. with yaml configuration and arguments

`java -jar jquality-[version]-all.jar --input /path/to/project [--output /path/to/output/xml] --config /path/to/yaml/config [--filters .*/test/.*]`

Take a look at the default-config.yaml file in this repository.

Loading detectors from configuration file cannot be used together with --fullStack mode. Config comes first.

3. without configurations

`java -jar jquality-[version]-all.jar --fullstack --input /path/to/project [--output /path/to/output/xml] [--filters .*/test/.*]`


##### Filters
Filters can help you sort out paths which are not relevant for your project eg. test data. 

It is recommended to filter test cases in your analysis. Test code differs too much from production code.
For example feature envy is present in every test method as test cases should be isolated and 
understandable by itself with minimum cohesion. 
Tests have other kinds of code smells which are not supported by jquality.

#### As Gradle task

Add following lines to your `build.gradle` file to create a `gradle jquality` task which can be executed from the console.

```groovy
repositories {
    maven {
        url  "http://dl.bintray.com/arturbosch/code-analysis"
    }
}

configurations {
    jquality
}

task jquality(type: JavaExec) {
    main = "io.gitlab.arturbosch.smartsmells.Main"
    classpath = configurations.jquality
    def input = "$project.projectDir.absolutePath/src/main"
    def baseDir = "$project.projectDir/reports/"
    def output = "$baseDir/report.xml"
    def params = [ '-i', input, '-o', output, '-fs', '-f', ".*/test/.*"]
    args(params)
}

dependencies {
    jquality 'io.gitlab.arturbosch.smartsmells:SmartSmells:1.0.0.[Milestone]'
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

