package lando.systems.ld44.ai.conditions;

import lando.systems.ld44.entities.Boss;

public class TookDamageCondition implements Condition {
    Boss boss;

    public TookDamageCondition(Boss boss) {
        this.boss = boss;
    }

    @Override
    public boolean isTrue() {
        return boss.tookDamage;
    }
}
