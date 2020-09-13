/* ******************************************************************************
 * Copyright 2020 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.cyphercove.gdxtween.math;

import com.badlogic.gdx.utils.GdxRuntimeException;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/** A mutable int wrapper. */
public class ScalarInt implements Serializable {
    private static final long serialVersionUID = -3782199586747300873L;

    public final static ScalarInt Zero = new ScalarInt(0);

    /**
     * The value of the int.
     */
    public int x;

    public ScalarInt() {
    }

    public ScalarInt(int x) {
        this.x = x;
    }

    public ScalarInt(@NotNull ScalarInt scalar) {
        this.x = scalar.x;
    }

    @NotNull public ScalarInt cpy () {
        return new ScalarInt(x);
    }

    @NotNull public ScalarInt limit (int limit) {
        if (x > limit)
            x = limit;
        else if (x < -limit)
            x = -limit;
        return this;
    }

    @NotNull public ScalarInt setLength (int len) {
        x = (int)Math.signum(x) * len;
        return this;
    }

    @NotNull public ScalarInt clamp (int min, int max) {
        int abs = Math.abs(x);
        if (abs < min)
            x = (int)Math.signum(x) * min;
        else if (abs > max)
            x = (int)Math.signum(x) * max;
        return this;
    }

    @NotNull public ScalarInt set (ScalarInt v) {
        x = v.x;
        return this;
    }

    @NotNull public ScalarInt set (int x){
        this.x = x;
        return this;
    }

    @NotNull public ScalarInt sub (ScalarInt v) {
        x -= v.x;
        return this;
    }

    @NotNull public ScalarInt sub (int x) {
        this.x -= x;
        return this;
    }

    @NotNull public ScalarInt add (ScalarInt v) {
        x += v.x;
        return this;
    }

    @NotNull public ScalarInt add (int x) {
        this.x += x;
        return this;
    }

    public float dst (ScalarInt v) {
        return Math.abs(x - v.x);
    }

    /** Converts this {@code ScalarInt} to a string in the format {@code (x)}.
     * @return a string representation of this object. */
    @Override
    @NotNull public String toString () {
        return "(" + x + ")";
    }

    /** Sets this {@code ScalarInt} to the value represented by the specified string according to the format of {@link #toString()}.
     * @param v the string.
     * @return this vector for chaining */
    @NotNull public ScalarInt fromString (String v) {
        int s = v.indexOf(',', 1);
        if (s != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
            try {
                int x = Integer.parseInt(v.substring(1, s));
                return this.set(x);
            } catch (NumberFormatException ex) {
                // Throw a GdxRuntimeException
            }
        }
        throw new GdxRuntimeException("Malformed ScalarInt: " + v);
    }

    @Override
    public int hashCode () {
        return x;
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        return x == ((ScalarInt)obj).x;
    }
}
