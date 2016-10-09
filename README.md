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
