package IABicing;

import IA.Bicing.Estaciones;
import IA.Bicing.Estacion;
import model.Board;
import model.Furgoneta;
import model.Pair;


import java.util.*;

public class BicingState {

    // Atributos estaticos del mapa del problema
    private static Estaciones Stations;
    private static ArrayList<Pair> demand_bicis;
    private static ArrayList<Pair> exceed_bicis;
    private static TreeMap<Double,Integer> sorted_exceed;
    private static int max_furgo;

    // Representacion del estado
    private Furgoneta[] Furgos;

    BicingState(Furgoneta[] NewFurgos) {
        Furgos = NewFurgos;
    }
    public BicingState(int nest, int nbic, int demanda, int seed, int num_furgo, int init_sol) {

        Stations = new Estaciones(nest, nbic, demanda, seed);

        demand_bicis = new ArrayList<>();   // first -> id || second ->  bicis que faltan hasta la demanda
        exceed_bicis = new ArrayList<>();
        sorted_exceed = new TreeMap<>();  // key -> disponible || value ->  origin index

        Pair P;
        for (int i = 0; i < Stations.size(); ++i) {
            // Separar estaciones con falta y exceso de bicis
            Estacion S = Stations.get(i);
            int dem = S.getDemanda();
            int next = S.getNumBicicletasNext();
            int noused = S.getNumBicicletasNoUsadas();

            if (dem > next) {
                P = new Pair(i, dem-next);
                demand_bicis.add(P);
            }
            else {
                int value = Math.min(Math.min(next - dem, noused), 30);
                if (value > 0) exceed_bicis.add(new Pair(i, value));
            }

        }
        max_furgo = Math.min(num_furgo, exceed_bicis.size());
        Furgos = new Furgoneta[max_furgo];
        exceed_bicis.sort(new Pair.SortBySecond()); // ordenar las estaciones por la cantidad de bicis que tienen
    }

    // SOLUCIONES INICIALES
    public void initialSolution0() {
        //USAR LAS ESTACIONES CON MAYOR NUMERO DE BICIS SOBRANTES
        for (int i = 0; i < max_furgo; i++) {
            Furgos[i]= new Furgoneta((short) exceed_bicis.get(i).first, (short) -1, (short) -1, (short) 0,(short) 0);
        }
    }
    public void initialSolution1() {

        // USA, DE CADA ZONA DEL MAPA, LA ESTACION CON MAS BICICLETAS DISPONIBLES
        boolean[] used = new boolean[exceed_bicis.size()];
        boolean[] zones = new boolean[max_furgo];

        int diff = 10000/max_furgo;
        int f = 0, it = 0;

        for (; it < exceed_bicis.size(); it++) {
            int pos = Stations.get(exceed_bicis.get(it).first).getCoordY() / diff;
            if (pos >= max_furgo) --pos;
            if (!zones[pos]) {
                Furgos[f++] = new Furgoneta((short) exceed_bicis.get(it).first, (short)-1, (short) -1, (short) 0, (short) 0);
                zones[pos] = true;
                used[it] = true;
            }
        }

        // si no hay una estacion en cada zona, rellena con las siguientes que mas furgos tengan, sin importar la zona
        it = 0;
        while (f < max_furgo) {
            for (; it < exceed_bicis.size(); it++) if (!used[it]) break;
            Furgos[f++] = new Furgoneta((short) exceed_bicis.get(it).first, (short) -1, (short) -1, (short)0, (short) 0);
            used[it] = true;
        }

    }
    public void initialSolution2() {
        // ASIGNACION RANDOM
        Random rand = new Random();
        int origin;
        boolean[] used = new boolean[Stations.size()];
        for (int i = 0; i < max_furgo; i++) {
            do origin = rand.nextInt((short) exceed_bicis.size()); while(used[exceed_bicis.get(origin).first]);
            Furgos[i] = new Furgoneta((short) exceed_bicis.get(origin).first, (short) -1, (short) -1, (short) 0, (short) 0);
            used[exceed_bicis.get(origin).first] = true;
        }
    }

    // EVALUACION DEL ESTADO
    double computeBenefits() {

        double Benefits = 0;
        int[] demandas = new int[Stations.size()];

        // Dinero gastado en transporte de bicis
        for (Furgoneta furgo : Furgos) {

            if (!furgo.hasOrigin() || !furgo.hasD1()) continue;

            Benefits -= furgo.costeRecorrido();

            demandas[furgo.d1] += furgo.qtt1;

            if (furgo.hasD2())
                demandas[furgo.d2] += furgo.qtt2;
        }

        for (Pair est : demand_bicis)
            Benefits += Math.min(demandas[est.first], est.second);

        return Benefits;
    }
    double getTotalDistance() {
        double distancia_total = 0;
        for (Furgoneta res : this.Furgos) {
            distancia_total += res.totalDistance();
        }
        return distancia_total;
    }
    double getOriginDistance() {
        double distancia_total = 0;
        for (Furgoneta ori1 : this.Furgos)
            for (Furgoneta ori2 : Furgos)
                distancia_total += Board.distance(ori1.origin, ori2.origin);

        return distancia_total;
    }
    public double getActive() {

        int active = 0;
        for (Furgoneta furgo : Furgos) {
            if (furgo.isActive()) active++;
        }
        return active;
    }
    public double getDoubleDest() {

        int active = 0;
        for (Furgoneta furgo : Furgos) {
            if (furgo.usa2D()) active++;
        }
        return active;
    }

    public String AsignacionBicisToString() {

        // Hay que programar el printing de las asignaciones de las bicicletas.
        // es decir, poner cuantas furgos hay, cuantas bicis cada furgo, donde dejan las bicis, los km recorridos
        // y tambien el coste (puede ser desglosado o no) y los beneficios.

        // en otras palabras, imprimir la solucion para que veamos que cojones ha encontrado



        boolean linux_tabs = true;
        long time = System.currentTimeMillis() - Board.tick;

        StringBuilder ret = new StringBuilder();
        ret.append("\n\nID\tOriX\tOriY\tQttT\tDes1X\tDes1Y\tQtt1\tDes2X\tDes2Y\tQtt2\n");

        int id = 1;
        for (Furgoneta f : Furgos) {
            String FurgoInfo = f.toString(linux_tabs);
            if (FurgoInfo == null) continue;
            ret.append(id).append("\t").append(FurgoInfo).append('\n');
            ++id;
        }


        ret.append("\n- Beneficios:\t").append(this.computeBenefits()).append(" euros.\n");
        ret.append("- Distancia:\t").append(this.getTotalDistance()).append(" km.\n");
        ret.append("- Tiempo: \t").append(time).append(" ms.\n");

        return ret.toString();
    }

    public String AsignacionBicisToStringTest() {

        long time = System.currentTimeMillis() - Board.tick;

        StringBuilder ret = new StringBuilder();

        //ret.append(this.computeBenefits()).append("\t").append(this.getTotalDistance()).append("\t").append((int)this.getActive()).append("\t").append(time);
        ret.append(time);
        return ret.toString();
    }

    // GETTERS
    Furgoneta[] getFurgos() { return Furgos; }
    static ArrayList<Pair> getDemand_Bicis() { return demand_bicis;}
    public static TreeMap<Double,Integer> getSorted_exceed() { return sorted_exceed;}
    public static ArrayList<Pair> getExceed_Bicis() { return exceed_bicis;}
    public static Estaciones getStations() { return Stations; }
    public static int getMax_furgo() {
        return max_furgo;
    }
}
