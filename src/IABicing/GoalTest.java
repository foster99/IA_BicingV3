package IABicing;

public class GoalTest implements aima.search.framework.GoalTest {
    @Override
    public boolean isGoalState(Object state) {
        BicingState b_state = (BicingState) state;
        return false;
    }
}