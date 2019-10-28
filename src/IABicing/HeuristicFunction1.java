package IABicing;

import aima.search.framework.HeuristicFunction;
import model.Board;

public class HeuristicFunction1 implements HeuristicFunction {
    @Override
    public double getHeuristicValue(Object state) {
        BicingState b_state = (BicingState) state;

        // Calculo de dinerico
        double Benefits = b_state.computeBenefits();

        // System.out.println("Heuristic Value = " + Benefits);
        return -1.0 * Benefits;
    }
}// 750 37500 150 0 1234 0 0