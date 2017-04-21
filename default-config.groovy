//noinspection GroovyAssignabilityCheck
config {

	input '/home/artur/Repos/quide-master/Implementierung/QuideService/src/main/java'
	output '/home/artur/test/stuff.xml'

	filters {
		filter '.*/test/.*'
	}

	metrics(['wmc', 'atfd', 'tcc']) // not yet supported, need to be configured through MetricFacadeBuilder
	jars(['/home/artur/Repos/SmartSmells/src/test/resources/detector.jar']) // additional detectors, see detector-template project

	detectors {
		detector('comment') { // loose comments or comments over private members -> naming should be meaningful -> no doc needed
			let('active', 'false')
		}
		detector('complexcondition') { // complexity based on number of '&&' and '||' expressions
			let('active', 'true')
			let('threshold', '3')
		}
		detector('complexmethod') { // complexity based on McCabe
			let('active', 'true')
			let('threshold', '10')
		}
		detector('cycle') { // direct cycles -> A uses B, B uses A
			let('active', 'true')
		}
		detector('dataclass') { // classes with only attributes, getter, setters, hashcode, equals, compareTo methods
			let('active', 'true')
		}
		detector('deadcode') { // private unused members
			let('active', 'true')
		}
		detector('featureenvy') {
			// uses the feature envy factor method by Kwankamol Nongpong -> http://ieeexplore.ieee.org/document/7051460/
			let('active', 'true')
			let('ignoreStatic', 'true') // static methods are often just plain utility methods which make extended use in parameters
			let('threshold', '0.52') // the feature envy factor threshold, 0.52 is based on experience
			let('base', '0.5')
			let('weight', '0.5')
		}
		detector('godclass') {
			let('active', 'true')
			let('wmc', '47') // weighted method count -> McCabe for all methods
			let('atfd', '5') // access to foreign data -> usage of other classes
			let('tcc', '0.33') // tied class cohesion -> are own attributes used in methods?
		}
		detector('javadoc') { // are public members documented? parameter, throw and return tags too?
			let('active', 'false')
			let('onlyInterfaces', 'true')
		}
		detector('largeclass') {
			let('active', 'true')
			let('threshold', '150') // logical length without comments, newlines, package and import statements
		}
		detector('longmethod') {
			let('active', 'true')
			let('threshold', '20') // length of a method without headers
		}
		detector('longparameterlist') {
			let('active', 'true')
			let('threshold', '5') // length of parameters in methods/constructors
		}
		detector('middleman') {
			let('active', 'true')
			let('threshold', 'half') //of methods are delegates [third|half|all]
		}
		detector('messagechain') {
			let('active', 'true')
			let('threshold', '3') // at which chain length?
		}
		detector('nestedblockdepth') {
			let('active', 'true')
			let('threshold', '3') // depth of statement nesting
		}
		detector('shotgunsurgery') {
			let('active', 'true')
			let('cc', '5') // class calls
			let('cm', '10') // method calls
		}
		detector('statechecking') { // conditional blocks for extreme type or constant checking
			let('active', 'true')
		}
		detector('metrics') {
			let('active', 'false')
			let('skipCC_CM', 'true')
		}
	}

}