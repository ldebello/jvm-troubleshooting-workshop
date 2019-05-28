package ar.com.javacuriosities.labs.jit;

import ar.com.javacuriosities.utils.Utils;

public class Main {

    public static void main(String[] args) {
        Utils.start(args);

        Logic logic = new Logic();

        while (true) {
            Days day = Days.values()[(int) (Math.random() * 6)];
            logic.deadCode();
            logic.inlining(14, 2);
            logic.bigMethod(day);
            logic.autoboxingElimination();
            logic.escapeAnalysis();
            logic.lockElision();
            logic.loopConditionals();
        }
    }
}
