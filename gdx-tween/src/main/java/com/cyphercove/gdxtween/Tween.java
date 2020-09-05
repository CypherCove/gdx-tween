package com.cyphercove.gdxtween;

import com.badlogic.gdx.utils.Pool;
import org.jetbrains.annotations.Nullable;

public interface Tween extends  Pool.Poolable {
    /**
     * Called on all running Tweens when starting a TargetingTween to check whether the new one should interrupt this tween.
     *
     * @param sourceTween  The tween that is being started.
     * @param currentWorldSpeed If not null and the source target is currently being manipulated by this tween and is being
     *                     interpolated by a TargetingTween of the same type, then the current speed should be filled
     *                     into the array. Otherwise, the array should not be modified.
     * @return True if this tween should be interrupted (cancelled by the TweenRunner).
     */
    boolean checkInterruption(TargetingTween<?, ?> sourceTween, @Nullable float[] currentWorldSpeed);
}
