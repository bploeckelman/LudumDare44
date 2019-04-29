package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld44.ai.StateMachine;
import lando.systems.ld44.ai.Transition;
import lando.systems.ld44.ai.conditions.*;
import lando.systems.ld44.ai.states.*;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.utils.Config;

public class Boss {
    public int hitpoints;
    protected StateMachine stateMachine;
    public Vector2 position;
    public Rectangle bounds;
    public GameScreen gameScreen;
    public TextureRegion texture;
    public boolean tookDamage;
    public boolean actionCompleted;
    public float direction;
    public Color tint;

    public Boss(GameScreen gameScreen){
        this.gameScreen = gameScreen;
        this.texture = gameScreen.assets.handFlat;
        this.position = new Vector2(600, 400);
        this.hitpoints = 3;
        tookDamage = false;
        direction = -1;
        bounds = new Rectangle();
        tint = new Color(1f, 1f, 1f, 1f);
        initializeStateMachine();
    }

    private void initializeStateMachine(){
        State idleState = new BossIdleState(this);
        State spawnCoinsState = new SpawnCoinsState(this);
        State stabState = new BossStabState(this);
        State poundState = new BossPoundState(this);
        State injuredState = new BossInjuredState(this);
        State bossDeadState = new BossDeadState(this);

        Condition needsCoins = new NeedsCoinsCondition(gameScreen);
        Condition hasEnoughCoins = new HasEnoughCoinsCondition(gameScreen);
        Condition has3HP = new HPEqualsCondition(this, 3);
        Condition has2HP = new HPEqualsCondition(this, 2);
        Condition has1HP = new HPEqualsCondition(this, 1);
        Condition dead = new HPEqualsCondition(this, 0);
        Condition tookDamage = new TookDamageCondition(this);
        Condition actionCompleted = new ActionCompletedCondition(this);


        Array<Transition> transitions = new Array<Transition>();

        transitions.add(new Transition(idleState, needsCoins, spawnCoinsState));
        transitions.add(new Transition(spawnCoinsState, hasEnoughCoins, idleState));
        transitions.add(new Transition(idleState, has3HP, stabState));
        transitions.add(new Transition(stabState, has3HP, idleState));
        transitions.add(new Transition(idleState, has2HP, poundState));
        transitions.add(new Transition(poundState, has2HP, idleState));
        transitions.add(new Transition(poundState, has1HP, stabState));
        transitions.add(new Transition(stabState, has1HP, poundState));
        transitions.add(new Transition(idleState, has1HP, stabState));
        transitions.add(new Transition(poundState, tookDamage, injuredState));
        transitions.add(new Transition(stabState, tookDamage, injuredState));
        transitions.add(new Transition(injuredState, dead, bossDeadState));
        transitions.add(new Transition(injuredState, actionCompleted, idleState));

        stateMachine = new StateMachine(idleState, transitions);
    }


    public void update(float dt){
        stateMachine.update(dt);

        for (GameEntity entity : gameScreen.gameEntities){
            if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                if (projectile.bounds.overlaps(bounds)) {
                    hitpoints--;
                    tookDamage = true;
                    projectile.markHit();
                    break;
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.setColor(tint);
        if (direction < 0) {
            // Magic Numbers, must be the last day of the jam
            batch.draw(texture, position.x, position.y, 130, 100);
        } else {
            // Magic Numbers, must be the last day of the jam
            batch.draw(texture, position.x + 130, position.y, -130, 100);
        }
        batch.setColor(Color.WHITE);
        if (Config.debug) {
            gameScreen.assets.ninePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    public void patrol(float dt){
        position.x += 100 * dt * direction * (4-hitpoints);
        if (position.x < 36) direction = 1f;
        if (position.x > 640) direction = -1f;
    }
}
