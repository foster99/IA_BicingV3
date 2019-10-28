package IABicing;

import IA.Bicing.Estacion;
import model.Board;
import model.Furgoneta;
import model.Pair;

@SuppressWarnings("ALL")
class Operator {
    static String fullD1(Furgoneta F, int dest) {
        // Operador que a una furgoneta con origen definido, le asigna un D1 con todas las bicicletas posibles.
        Pair destino = BicingState.getDemand_Bicis().get(dest); // Indice sobre Stations de la Estacion destino y su demanda

        Estacion est = BicingState.getStations().get(F.origin);
        int sobra_de_origen = Math.min(est.getNumBicicletasNext() - est.getDemanda(), est.getNumBicicletasNoUsadas());

        F.d1 = (short) destino.first;
        F.qtt1 = (short) Math.min(destino.second,  Math.min(sobra_de_origen, 30));

        return "[fullD1]: Destino 1 asignado y lleno en furgoneta.";
    }
    static String fullD2(Furgoneta F, int dest) {
        // Operador que a una furgoneta con origen definido, le asigna un D1 con todas las bicicletas posibles.
        if (F.full()) return null;

        Pair destino = BicingState.getDemand_Bicis().get(dest); // Indice sobre Stations de la Estacion destino y su demanda
        Estacion est = BicingState.getStations().get(F.origin);
        int sobras = Math.min(est.getNumBicicletasNext() - est.getDemanda(), est.getNumBicicletasNoUsadas()) - F.qtt1;

        F.d2 = (short) destino.first;
        F.qtt2 = (short) Math.min(destino.second, sobras);

        return "[fullD2]: Destino 2 asignado y lleno (lo maximo posible) en furgoneta.";
    }
    static String removeD1(Furgoneta F) {
        if (--F.qtt1 <= 0) {
            F.off();
            return "[removeD1]: Off en furgoneta.";
        }
        return "[removeD1]: --Qtt1 en furgoneta.";
    }
    static String removeD2(Furgoneta F) {
        if (--F.qtt2 <= 0) {
            F.d2 = -1;
            F.qtt2 = 0;
            return "[removeD2]: D2 eliminado.";
        }
        return "[removeD2]: --Qtt2 en furgoneta.";
    }

    static String changeOrigin(Furgoneta F, int index) {

        Pair origin = BicingState.getExceed_Bicis().get(index);

        F.origin = (short) origin.first;
        if (F.hasD2() && F.hasD1() && Board.distance(F.origin, F.d1) > Board.distance(F.origin, F.d2)) F.swap();

        if (F.hasD1()) {
            Estacion dem1 = BicingState.getStations().get(F.d1);
            int demanda1 = dem1.getNumBicicletasNext() - dem1.getDemanda();
            F.qtt1 = (short) Math.min(demanda1, (Math.min(30, origin.second)));

            if (F.hasD2()) {
                Estacion dem2 = BicingState.getStations().get(F.d2);
                int demanda2 = dem2.getNumBicicletasNext() - dem2.getDemanda();
                F.qtt2 = (short) Math.min(demanda1, (Math.min(30, origin.second) - F.qtt1));
                if (F.qtt2 < 0) {
                    F.qtt2 = (short) 0;
                    F.d2 = (short) -1;
                }
            }
        }

        return "[ChangeOrigin]: Origen cambiado.";
    }
}
