package io.gitlab.arturbosch.jpal.dummies.resolving;

/**
 * @author Artur Bosch
 */
@SuppressWarnings("ALL")
public class LongChainResolving {

	public FirstChain firstChain = new FirstChain();

	public class FirstChain {
		public SecondChain secondChain = new SecondChain();
	}

	public class SecondChain {
		public ThirdChain thirdChain = new ThirdChain();
	}

	public class ThirdChain {
		public FourthChain fourthChain = new FourthChain();
	}

	public class FourthChain {
		public FifthChain fifthChain = new FifthChain();

		public class FifthChain {
			public int anInt;
			public FifthChain selfChain = new FifthChain();

			public int getAnInt() {
				return anInt;
			}
		}

	}

}
