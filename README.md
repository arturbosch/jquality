# SmartSmells

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
- CommentSmell
- Cycle
- Deadcode
- MessageChain
- MiddleMan

### Installation

- cd {path}/SmartSmells
- gradle unitTest to verify all tests
- gradle fatJar to build an executable jar

### Usage

- After installing SmartSmells and copying the default-config.yaml to a location you are aware of, you can start SmartSmells with:

- java -jar SmartSmells.jar --input "/path/to/project" --config "path/to/default-config.yaml" --output "path/to/output.xml"

- Shortkeys are: '-i', '-c', '-o'

### As Gradle task

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
	def input = "$project.projectDir.absolutePath"
	def output = "$project.projectDir/smartsmells.xml"
	def params = [ '-i', input, '-o', output, '-f']
	args(params)
}

dependencies {
	smartsmells 'io.gitlab.arturbosch.smartsmells:SmartSmells:M7'
}
```
