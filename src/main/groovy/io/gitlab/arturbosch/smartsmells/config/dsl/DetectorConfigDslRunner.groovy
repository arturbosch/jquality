package io.gitlab.arturbosch.smartsmells.config.dsl
/**
 * @author Artur Bosch
 */
class DetectorConfigDslRunner {

	static DetectorConfigDsl execute(String dsl) {
		Script dslScript = new GroovyShell().parse(dsl)
		def configDsl = new DetectorConfigDsl()

		dslScript.metaClass = createEMC(dslScript.class, { ExpandoMetaClass emc ->
			emc.config { Closure closure ->
				closure.delegate = configDsl
				closure.resolveStrategy = DELEGATE_FIRST
				closure()
			}
		})

		dslScript.run()
		return configDsl
	}

	static DetectorConfigDsl execute(File dsl) {
		return execute(dsl.text)
	}

	static ExpandoMetaClass createEMC(Class clazz, Closure cl) {
		ExpandoMetaClass emc = new ExpandoMetaClass(clazz, false)
		cl(emc)
		emc.initialize()
		return emc
	}

}
