package IABicing;

import aima.search.framework.HeuristicFunction;
import model.Furgoneta;
import model.Pair;

public class HeuristicFunction0 implements HeuristicFunction {
    @Override
    public double getHeuristicValue(Object state) {
        BicingState b_state = (BicingState) state;

        double Benefits = 0;

        int[] demandas = new int[BicingState.getStations().size()];

        for (Furgoneta furgo : b_state.getFurgos()) {
            if (!furgo.hasOrigin() || !furgo.hasD1()) continue;

            demandas[furgo.d1] += furgo.qtt1;
            if (furgo.hasD2()) demandas[furgo.d2] += furgo.qtt2;
        }

        for (Pair est : BicingState.getDemand_Bicis())
            Benefits += Math.min(demandas[est.first], est.second);

        return -1.0 * Benefits;
    }
}