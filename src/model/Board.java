package model;

import java.util.*;

import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import IABicing.*;

import static java.lang.Math.abs;
// 0 500 25000 100 0 -1 0 1 1 10000 10 5 0.001
public class Board {

    public static long tick;

    public static void main(String[] args) {

        int nest, nbic, f, dem, seed, init_sol, algorithm, heuristic, testing;

        if (args.length > 0) {
            testing= Integer.parseInt(args[0]);
            nest = Integer.parseInt(args[1]);
            nbic = Integer.parseInt(args[2]);
            f = Integer.parseInt(args[3]);
            dem = Integer.parseInt(args[4]);

            Random random = new Random();
            seed = Integer.parseInt(args[5]);
            if (seed == -1) seed = random.nextInt();

            init_sol = Integer.parseInt(args[6]);

            BicingState initial_state = new BicingState(nest, nbic, dem, seed, f, init_sol);

            if (init_sol == 0)
                initial_state.initialSolution0();
            else if (init_sol == 1)
                initial_state.initialSolution1();
            else if (init_sol == 2)
                initial_state.initialSolution2();

            heuristic = Integer.parseInt(args[7]);

            algorithm = Integer.parseInt(args[8]);
            if (algorithm == 0)
                Bicing_HillClimbing(initial_state,testing, heuristic);
            else if (algorithm == 1)
                Bicing_SimulatedAnnealing(initial_state, args,testing, heuristic);
        }

        else {
            Scanner in = new Scanner(System.in);

            System.out.println("Salida en modo:\n" +
                    "\t- Introduce (0) para obtener la informacion COMPLETA (Recomendada).\n" +
                    "\t- Introduce (1) para obtener la informacion SIMPLIFICADA (solo para testing).");
            testing=in.nextInt();

            System.out.println("Introduce el numero de Estaciones:");
            nest = in.nextInt();

            System.out.println("Introduce el numero de Bicicletas (este debe ser igual o superior a " + (nest * 50) + "):");
            nbic = in.nextInt();

            System.out.println("Introduce el numero de furgonetas (relacion 1:5 = " + nest/5 + "):");
            f = in.nextInt();

            System.out.println("Tipo de demanda:\n" +
                    "\t- Introduce (0) para demanda EQUILIBRADA.\n" +
                    "\t- Introduce (1) para demanda HORA PUNTA.");
            dem = in.nextInt();

            System.out.println("Introduce una seed para la generacion del mapa (si deseas una aleatoria, introduce (-1):");
            seed = in.nextInt();
            if (seed == -1) {
                Random random = new Random();
                seed = random.nextInt();
            }

            System.out.println("Tipo de solucion inicial:\n" +
                    "\t- Introduce (0) para utilizar la solucion que selecciona las estaciones con mas bicis.\n" +
                    "\t- Introduce (1) para utilizar la solucion que selecciona las estaciones con sistema de puntos.\n" +
                    "\t- Introduce (2) para utilizar la solucion que selecciona estaciones RANDOM.\n");
            init_sol = in.nextInt();

            BicingState initial_state = new BicingState(nest, nbic, dem, seed, f, init_sol);


            if (init_sol == 0)
                initial_state.initialSolution0();
            else if (init_sol == 1)
                initial_state.initialSolution1();
            else if (init_sol == 2)
                initial_state.initialSolution2();

            System.out.println("Criterio del Heuristico:\n" +
                    "\t- Introduce (0) para utilizar TRANSPORTE GRATIS.\n" +
                    "\t- Introduce (1) para utilizar BENEFICIO.");
            heuristic = in.nextInt();

            System.out.println("Algoritmo de busqueda local:\n" +
                    "\t- Introduce (0) para utilizar HILL CLIMBING.\n" +
                    "\t- Introduce (1) para utilizar SIMULATED ANNEALING.");
            algorithm = in.nextInt();
            if (algorithm == 0)
                Bicing_HillClimbing(initial_state, testing, heuristic);
            else if (algorithm == 1)
                Bicing_SimulatedAnnealing(initial_state, null, testing, heuristic);
        }

    }

    private static void Bicing_HillClimbing(BicingState state, int testing, int heuristic) {
        try {

            Problem problem;
            if (heuristic == 0) problem = new Problem(state, new SuccessorFunction_HC(), new GoalTest(), new HeuristicFunction0());
            else problem = new Problem(state, new SuccessorFunction_HC(), new GoalTest(), new HeuristicFunction1());

            tick = System.currentTimeMillis();

            Search search = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem, search);


            if(testing == 1){
                System.out.println("\n" + ((BicingState) search.getGoalState()).AsignacionBicisToStringTest());
            }
            else {
                printActions(agent.getActions());
                printInstrumentation(agent.getInstrumentation());
                System.out.print(search.getGoalState().toString());
                System.out.println("\n" + ((BicingState) search.getGoalState()).AsignacionBicisToString());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void Bicing_SimulatedAnnealing(BicingState state, String[] args, int testing, int heuristic) {
        try {
            Problem problem;
            if (heuristic == 0) problem = new Problem(state, new SuccessorFunction_SA(), new GoalTest(), new HeuristicFunction0());
            else problem = new Problem(state, new SuccessorFunction_SA(), new GoalTest(), new HeuristicFunction1());

            // Default simulated annealing
            int steps = 10000;
            int stiter = 5;
            int k = 5;
            double lamb = 0.001;

            if ((args != null) && (Integer.parseInt(args[9]) == 1)) { // Lectura de argumentos
                steps = Integer.parseInt(args[10]);
                stiter = Integer.parseInt(args[11]);
                k = Integer.parseInt(args[12]);
                lamb = Double.parseDouble(args[13]);
            }
            else { // Lectura de consola
                System.out.println("Parametros de Simulated Annealing:\n" +
                        "\t- Introduce (0) para utilizar los definidos por defecto.\n" +
                        "\t- Introduce (1) para introducirlos manualmente.");

                Scanner in = new Scanner(System.in).useLocale(Locale.US);
                int mode = in.nextInt();
                if (mode == 1) {
                    System.out.println("Steps:");
                    steps = in.nextInt();

                    System.out.println("Stiter:");
                    stiter = in.nextInt();

                    System.out.println("K:");
                    k = in.nextInt();

                    System.out.println("Lamb:");
                    lamb = in.nextDouble();
                }
            }

            tick = System.currentTimeMillis();

            Search search = new SimulatedAnnealingSearch(steps, stiter, k, lamb);
            SearchAgent agent = new SearchAgent(problem, search);

            if(testing == 1){
                System.out.println("\n" + ((BicingState) search.getGoalState()).AsignacionBicisToStringTest());
            }
            else{
                System.out.print(search.getGoalState().toString());
                System.out.println("\n" + ((BicingState) search.getGoalState()).AsignacionBicisToString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printActions(List actions) {
        for (Object o : actions) {
            String action = (String) o;
            System.out.println(action);
        }
    }
    private static void printInstrumentation(Properties properties) {
        for (Object o : properties.keySet()) {
            String key = (String) o;
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }

    }

    private static int distance(int x1, int y1, int x2, int y2) {
        return abs(x1 - x2) + abs(y1 - y2);
    }
    public static int distance(int e1, int e2) {
        return distance(
                BicingState.getStations().get(e1).getCoordX(),
                BicingState.getStations().get(e1).getCoordY(),
                BicingState.getStations().get(e2).getCoordX(),
                BicingState.getStations().get(e2).getCoordY());
    }
}
