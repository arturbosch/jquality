package io.gitlab.arturbosch.jpal.dummies;

import io.gitlab.arturbosch.jpal.dummies.resolving.LongChainResolving;
import io.gitlab.arturbosch.jpal.dummies.resolving.SolveTypeDummy;
import io.gitlab.arturbosch.jpal.dummies.resolving.SubSolveTypeDummy;

/**
 * @author Artur Bosch
 */
@SuppressWarnings("ALL")
public class ResolvingDummy {

	private int a = 5;
	private int b = 5;

	private int x = 5;

	private InnerResolvingDummy inner = new InnerResolvingDummy();
	private SolveTypeDummy solveDummy = new SolveTypeDummy();

	// variable resolving
	public int m(int d) {
		int c = 5;
		if (a == c) {
			b += c;
		}
		return b + d;
	}

	// same symbol name in different variables
	public void m2() {
		int x = 0;
		while (true) {
			this.x = x + 1;
			int xnew = x + 2;
		}
	}

	// resolving 'this' or one level calls/accesses
	public String m3() {
		String method = solveDummy.method(x);
		m2();
		int x = this.x;
		return inner.s + method + inner.call();
	}

	// resolving method chaining + object creation
	public void m4() {
		new ChainResolving().inner.call();
		String result = new ChainResolving().chain().method(1);
		SolveTypeDummy.instance().method(SolveTypeDummy.instanceField);
	}

	// how about very long chains?
	public void m5() {
		new LongChainResolving().firstChain.secondChain.thirdChain
				.fourthChain.fifthChain.selfChain.selfChain.getAnInt();
	}

	// Simple Inheritence
	public void m6() {
		new SubSolveTypeDummy().stuff();
		new SubSolveTypeDummy().method(new SubSolveTypeDummy().MAGIC_NUMBER);
		new SubSolveTypeDummy().instance().instance().method(5);
	}

	// builder pattern
	public void m7() {
		new ResolvingBuilder().withId(1).withName("abc").build();
	}

	class InnerResolvingDummy {
		String s;

		String call() {
			return "";
		}
	}

	class ChainResolving {
		private InnerResolvingDummy inner = new InnerResolvingDummy();

		SolveTypeDummy chain() {
			return new SolveTypeDummy();
		}
	}

	class ResolvingBuilder {
		ResolvingBuilder withId(int i){
			return this;
		}
		ResolvingBuilder withName(String s){
			return this;
		}
		ResolvingDummy build() {
			return new ResolvingDummy();
		}
	}
}
