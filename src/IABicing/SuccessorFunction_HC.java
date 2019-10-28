package IABicing;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SuccessorFunction_HC implements SuccessorFunction {

    public List getSuccessors(Object state) {
        ArrayList<Successor> retVal = new ArrayList<>();
        BicingState b_state = (BicingState) state;

        Furgoneta[] OldFurgos = b_state.getFurgos();

        for (int f = 0; f < OldFurgos.length; f++) {

            for (int dest = 0; dest < BicingState.getDemand_Bicis().size(); dest++) {

                // fullD1
                if (!OldFurgos[f].hasD1()) {

                    if (Board.distance(OldFurgos[f].origin, BicingState.getDemand_Bicis().get(dest).first)/1000 >= BicingState.getDemand_Bicis().get(dest).second) continue;

                    Furgoneta[] NewFurgos1 = new Furgoneta[OldFurgos.length];
                    for (int i = 0; i < OldFurgos.length; i++) NewFurgos1[i] = OldFurgos[i].clone();
                    String result1 = Operator.fullD1(NewFurgos1[f], dest);
                    BicingState succ1 = new BicingState(NewFurgos1);
                    retVal.add(new Successor(result1, succ1));
                }

                // fullD2
                else if (!OldFurgos[f].hasD2()) {

                    if (OldFurgos[f].d1 == BicingState.getDemand_Bicis().get(dest).first || Board.distance(OldFurgos[f].d1, BicingState.getDemand_Bicis().get(dest).first)/1000 >= BicingState.getDemand_Bicis().get(dest).second) continue;

                    Furgoneta[] NewFurgos2 = new Furgoneta[OldFurgos.length];
                    for (int i = 0; i < OldFurgos.length; i++) NewFurgos2[i] = OldFurgos[i].clone();
                    String result2 = Operator.fullD2(NewFurgos2[f], dest);
                    if (result2 == null) continue;
                    BicingState succ2 = new BicingState(NewFurgos2);
                    retVal.add(new Successor(result2, succ2));
                }
            }

            // removeD1
            if (OldFurgos[f].hasD1()) {
                Furgoneta[] NewFurgos3 = new Furgoneta[OldFurgos.length];
                for (int i = 0; i < OldFurgos.length; i++) NewFurgos3[i] = OldFurgos[i].clone();
                String result3 = Operator.removeD1(NewFurgos3[f]);
                BicingState succ3 = new BicingState(NewFurgos3);
                retVal.add(new Successor(result3, succ3));
            }
            // removeD2
            if (OldFurgos[f].hasD2()) {
                Furgoneta[] NewFurgos4 = new Furgoneta[OldFurgos.length];
                for (int i = 0; i < OldFurgos.length; i++) NewFurgos4[i] = OldFurgos[i].clone();
                String result = Operator.removeD2(NewFurgos4[f]);
                BicingState succ4 = new BicingState(NewFurgos4);
                retVal.add(new Successor(result, succ4));
            }
            /*
            // changeOrigin
            boolean[] used = new boolean[BicingState.getStations().size()];
            for (Furgoneta F : OldFurgos) used[F.origin] = true;

            for (int origin = 0; origin < BicingState.getExceed_Bicis().size(); origin++) {

                if (used[BicingState.getExceed_Bicis().get(origin).first]) continue; // origenes asignados a otras furgonetas

                Furgoneta[] NewFurgos5 = new Furgoneta[OldFurgos.length];
                for (int i = 0; i < OldFurgos.length; i++) NewFurgos5[i] = OldFurgos[i].clone();
                String result5 = Operator.changeOrigin(NewFurgos5[f], origin);
                BicingState succ5 = new BicingState(NewFurgos5);
                retVal.add(new Successor(result5, succ5));
            }*/
        }

        return retVal;
    }


}