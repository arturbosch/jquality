package com.gitlab.artismarti.smartsmells.smells.godclass

/**
 * @author artur
 */
class TiedClassCohesion {

	static double calc(Map<String, Set<String>> methodFieldAccesses) {
		double tcc = 1.0d
		double methodPairs = determineMethodPairs(methodFieldAccesses)
		double totalMethodPairs = calculateTotalMethodPairs(methodFieldAccesses)
		if (totalMethodPairs > 0.0d && methodPairs > 1.0d) {
			tcc = methodPairs / totalMethodPairs
		}
		return tcc;
	}

	private static double determineMethodPairs(Map<String, Set<String>> methodFieldAccesses) {
		def methods = methodFieldAccesses.keySet().toList();
		def methodCount = methods.size();
		def pairs = 0.0;
		if (methodCount > 1) {
			for (int i = 0; i < methodCount; i++) {
				for (int j = i + 1; j < methodCount; j++) {
					String firstMethodName = methods.get(i);
					String secondMethodName = methods.get(j);
					Set<String> accessesOfFirstMethod = methodFieldAccesses.get(firstMethodName);
					Set<String> accessesOfSecondMethod = methodFieldAccesses.get(secondMethodName);
					Set<String> combinedAccesses = new HashSet<String>();
					combinedAccesses.addAll(accessesOfFirstMethod);
					combinedAccesses.addAll(accessesOfSecondMethod);
					if (combinedAccesses.size() < (accessesOfFirstMethod.size() + accessesOfSecondMethod.size())) {
						pairs++;
					}
				}
			}
		}
		return pairs;
	}

	private static double calculateTotalMethodPairs(Map<String, Set<String>> methodFieldAccesses) {
		int n = methodFieldAccesses.size();
		return n * (n - 1) / 2.0;
	}
}
