package ar.com.javacuriosities.labs.jit;

public class Main {

    public static void main(String[] args) {
        Logic logic = new Logic();

        while (true) {
            logic.deadCode();
            logic.inlining(14, 2);
            logic.autoboxingElimination();
            logic.escapeAnalysis();
            logic.lockElision();
            logic.loopConditionals();
        }
    }
}
