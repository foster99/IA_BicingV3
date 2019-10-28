package model;

import java.util.Comparator;

public class Pair {
    public final int first;
    public int second;

    public Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public class SortByFirst implements Comparator<Pair> {
        @Override
        public int compare(Pair o1, Pair o2) {
            return o1.first - o2.first;
        }
    }

    public static class SortBySecond implements Comparator<Pair> {
        @Override
        public int compare(Pair o1, Pair o2) { // de mayor a menor
            return o2.second - o1.second;
        }
    }

    public Pair clone() {
        return new Pair(first, second);
    }
}
