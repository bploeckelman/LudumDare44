package lando.systems.ld44.utils;

public class CallbackTimer {
    private float time = 0;

    private CallbackListener callback;

    public CallbackTimer(CallbackListener callback) {
        this.callback = callback;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public void update(float dt) {
        if (time > 0) {
            time -= dt;
            if (time <= 0) {
                time = 0;
                callback.callback();
            }
        }
    }
}
