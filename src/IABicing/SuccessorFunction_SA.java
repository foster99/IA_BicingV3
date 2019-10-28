package IABicing;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import model.Board;
import model.Furgoneta;
import model.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SuccessorFunction_SA implements SuccessorFunction {

    @Override
    public List getSuccessors(Object state) {
        ArrayList<Successor> retVal = new ArrayList<>();
        BicingState b_state = (BicingState) state;

        Furgoneta[] OldFurgos = b_state.getFurgos();
        Random rand = new Random();

        double exceed = BicingState.getExceed_Bicis().size();
        double dem = BicingState.getDemand_Bicis().size();

        double ratio_dem = dem/(dem + exceed);

        boolean generated = false;
        if (rand.nextDouble() <= ratio_dem) {
            do {
                int f = rand.nextInt(OldFurgos.length);
                int dest = rand.nextInt(BicingState.getDemand_Bicis().size());
                // fullD1
                if (!OldFurgos[f].hasD1()) {

                    if (Board.distance(OldFurgos[f].origin, BicingState.getDemand_Bicis().get(dest).first) / 1000 >= BicingState.getDemand_Bicis().get(dest).second)
                        continue;

                    Furgoneta[] NewFurgos1 = new Furgoneta[OldFurgos.length];
                    for (int i = 0; i < OldFurgos.length; i++) NewFurgos1[i] = OldFurgos[i].clone();
                    String result1 = Operator.fullD1(NewFurgos1[f], dest);
                    BicingState succ1 = new BicingState(NewFurgos1);
                    retVal.add(new Successor(result1, succ1));
                    generated = true;
                }
                // fullD2
                else if (!OldFurgos[f].hasD2()) {

                    if (OldFurgos[f].d1 == BicingState.getDemand_Bicis().get(dest).first || Board.distance(OldFurgos[f].d1, BicingState.getDemand_Bicis().get(dest).first) / 1000 >= BicingState.getDemand_Bicis().get(dest).second)
                        continue;

                    Furgoneta[] NewFurgos2 = new Furgoneta[OldFurgos.length];
                    for (int i = 0; i < OldFurgos.length; i++) NewFurgos2[i] = OldFurgos[i].clone();
                    String result2 = Operator.fullD2(NewFurgos2[f], dest);
                    if (result2 != null) {
                        BicingState succ2 = new BicingState(NewFurgos2);
                        retVal.add(new Successor(result2, succ2));
                        generated = true;
                    }
                }
                // removeD1
                if (OldFurgos[f].hasD1()) {
                    Furgoneta[] NewFurgos3 = new Furgoneta[OldFurgos.length];
                    for (int i = 0; i < OldFurgos.length; i++) NewFurgos3[i] = OldFurgos[i].clone();
                    String result3 = Operator.removeD1(NewFurgos3[f]);
                    BicingState succ3 = new BicingState(NewFurgos3);
                    retVal.add(new Successor(result3, succ3));
                    generated = true;
                }
                // removeD2
                if (OldFurgos[f].hasD2()) {
                    Furgoneta[] NewFurgos4 = new Furgoneta[OldFurgos.length];
                    for (int i = 0; i < OldFurgos.length; i++) NewFurgos4[i] = OldFurgos[i].clone();
                    String result = Operator.removeD2(NewFurgos4[f]);
                    BicingState succ4 = new BicingState(NewFurgos4);
                    retVal.add(new Successor(result, succ4));
                    generated = true;
                }
            } while (!generated);
        }
        else {
            ArrayList<Integer> free = new ArrayList<>();
            boolean[] used = new boolean[BicingState.getStations().size()];
            for (Furgoneta F : OldFurgos) used[F.origin] = true;

            for (int i = 0; i < BicingState.getExceed_Bicis().size(); i++)
                if (!used[BicingState.getExceed_Bicis().get(i).first]) free.add(i);

            do {
                int f = rand.nextInt(OldFurgos.length);
                int origin = free.get(rand.nextInt(free.size()));

                Furgoneta[] NewFurgos5 = new Furgoneta[OldFurgos.length];
                for (int i = 0; i < OldFurgos.length; i++) NewFurgos5[i] = OldFurgos[i].clone();
                String result5 = Operator.changeOrigin(NewFurgos5[f], origin);
                BicingState succ5 = new BicingState(NewFurgos5);
                retVal.add(new Successor(result5, succ5));
                generated = true;

            } while (!generated);
        }

        return retVal;
    }
}