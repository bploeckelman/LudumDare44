package lando.systems.ld44.ai;


import lando.systems.ld44.ai.conditions.Condition;
import lando.systems.ld44.ai.states.State;


public class Transition {
    State from;
    Condition condition;
    State to;

    public Transition(State from, Condition condition, State to){
        this.from = from;
        this.condition = condition;
        this.to = to;
    }
}