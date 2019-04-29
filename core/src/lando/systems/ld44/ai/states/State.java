package lando.systems.ld44.ai.states;


public interface State {

    void update(float dt);
    void onEnter();
    void onExit();

}