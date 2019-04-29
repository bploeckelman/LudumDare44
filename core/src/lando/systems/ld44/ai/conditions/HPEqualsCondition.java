package lando.systems.ld44.ai.conditions;

import lando.systems.ld44.entities.Boss;

public class HPEqualsCondition implements Condition {

    Boss boss;
    int hp;

    public HPEqualsCondition(Boss boss, int hp) {
        this.boss = boss;
        this.hp = hp;
    }

    @Override
    public boolean isTrue() {
        return boss.hitpoints == hp && boss.actionCompleted;
    }
}
