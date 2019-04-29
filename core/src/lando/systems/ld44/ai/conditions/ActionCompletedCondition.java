package lando.systems.ld44.ai.conditions;

import lando.systems.ld44.entities.Boss;

public class ActionCompletedCondition implements Condition {

    Boss boss;

    public ActionCompletedCondition(Boss boss) {
        this.boss = boss;
    }

    @Override
    public boolean isTrue() {
        return boss.actionCompleted;
    }
}
