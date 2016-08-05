package io.gitlab.arturbosch.smartsmells.java;

/**
 * @author artur
 */
@SuppressWarnings("ALL")
public class FeatureEnvyDummy {

    private String s = "Hello World";
    private boolean b;
    private int i = 5;
    private HasLogic myLogic = new HasLogic();
    private HasFeatures hasFeatures = new HasFeatures();

    public void envyMethod() {
        HasLogic otherLogic = hasFeatures.getHasLogic();
        otherLogic.doStuff(s);
        otherLogic.doStuff(s);
        hasFeatures.reallyYouCallMe();
        hasFeatures.soNiceStuff();
        int me = i * i;
        myLogic.doStuff(" " + me);
    }

    public static void staticMethod() {
        Staticly staticly = new Staticly();
        staticly.envy();
        staticly.envy();
        staticly.envy();
        staticly.envy();
        staticly.envy();
    }

    private static class Staticly {
        public void envy() {}
    }

    private class HasFeatures {
        HasLogic hasLogic = new HasLogic();

        HasLogic getHasLogic() {
            return hasLogic;
        }

        void soNiceStuff() {
            System.out.println();
        }

        void reallyYouCallMe() {
            System.out.println();
        }

    }

    private class HasLogic {

        void doStuff(String me) {
            System.out.println(me);
        }
    }
}
