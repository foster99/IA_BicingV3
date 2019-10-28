package IABicing;

import aima.search.framework.HeuristicFunction;

class HeuristicFunction_SA implements HeuristicFunction {
    @Override
    public double getHeuristicValue(Object state) {
        BicingState b_state = (BicingState) state;

        // Calculo de dinerico
        double Benefits = b_state.computeBenefits();

        System.out.println("Heuristic Value = " + Benefits);
        return -1.0 * Benefits;
    }
}