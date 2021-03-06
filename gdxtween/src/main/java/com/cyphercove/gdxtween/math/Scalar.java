/* ******************************************************************************
 * Copyright 2019 See AUTHORS file.
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

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;

import java.io.Serializable;

/**
 * A mutable float wrapper, or 1D vector.
 */
public class Scalar implements Serializable, Vector<Scalar> { // Vector is implemented for the sake of inheriting docs.
    public static final long serialVersionUID = -3345823286435278757L;

    public final static Scalar Zero = new Scalar(0);

    /**
     * The value of the vector.
     */
    public float x;

    public Scalar() {
    }

    public Scalar(float x) {
        this.x = x;
    }

    public Scalar(Scalar scalar) {
        this.x = scalar.x;
    }


    @Override
    public Scalar cpy() {
        return new Scalar(x);
    }

    @Override
    public float len() {
        return Math.abs(x);
    }

    @Override
    public float len2() {
        return x * x;
    }

    @Override
    public Scalar limit(float limit) {
        if (x > limit)
            x = limit;
        else if (x < -limit)
            x = -limit;
        return this;
    }

    /**
     * Limits the length of this vector, based on the desired maximum length squared.
     *
     * @param limit2 Squared size limit
     * @return This vector.
     * @deprecated This method is not faster for a 1D vector. Use {@link #limit} instead.
     */
    @Override
    @Deprecated
    public Scalar limit2(float limit2) {
        return limit((float) Math.sqrt(limit2));
    }

    @Override
    public Scalar setLength(float len) {
        x = Math.signum(x) * len;
        return this;
    }

    /**
     * ets the length of this vector, based on the square of the desired length. Does nothing if this vector is zero.
     *
     * @param len2 Squared length to set.
     * @return This vector.
     * @deprecated This method is not faster for a 1D vector. Use {@link #setLength} instead.
     */
    @Override
    @Deprecated
    public Scalar setLength2(float len2) {
        if (len2 == 0f)
            return setLength(0f);
        return setLength((float) Math.sqrt(len2));
    }

    @Override
    public Scalar clamp(float min, float max) {
        float abs = Math.abs(x);
        if (abs < min)
            x = Math.signum(x) * min;
        else if (abs > max)
            x = Math.signum(x) * max;
        return this;
    }

    @Override
    public Scalar set(Scalar v) {
        x = v.x;
        return this;
    }

    public Scalar set(float x) {
        this.x = x;
        return this;
    }

    @Override
    public Scalar sub(Scalar v) {
        x -= v.x;
        return this;
    }

    public Scalar sub(float x) {
        this.x -= x;
        return this;
    }

    @Override
    public Scalar nor() {
        x = Math.signum(x);
        return this;
    }

    @Override
    public Scalar add(Scalar v) {
        x += v.x;
        return this;
    }

    public Scalar add(float x) {
        this.x += x;
        return this;
    }

    @Override
    public float dot(Scalar v) {
        return x * v.x;
    }

    @Override
    public Scalar scl(float scalar) {
        x *= scalar;
        return this;
    }

    @Override
    public Scalar scl(Scalar v) {
        x *= v.x;
        return this;
    }

    @Override
    public float dst(Scalar v) {
        return Math.abs(x - v.x);
    }

    @Override
    public float dst2(Scalar v) {
        float dst = dst(v);
        return dst * dst;
    }

    @Override
    public Scalar lerp(Scalar target, float alpha) {
        final float invAlpha = 1.0f - alpha;
        this.x = (x * invAlpha) + (target.x * alpha);
        return this;
    }

    public Scalar lerp(float target, float alpha) {
        final float invAlpha = 1.0f - alpha;
        this.x = (x * invAlpha) + (target * alpha);
        return this;
    }

    @Override
    public Scalar interpolate(Scalar target, float alpha, Interpolation interpolation) {
        return lerp(target, interpolation.apply(alpha));
    }

    @Override
    public Scalar setToRandomDirection() {
        x = MathUtils.randomSign();
        return this;
    }

    @Override
    public boolean isUnit() {
        return isUnit(0.000000001f);
    }

    @Override
    public boolean isUnit(float margin) {
        return Math.abs(len() - 1f) < margin;
    }

    @Override
    public boolean isZero() {
        return x == 0;
    }

    @Override
    public boolean isZero(float margin) {
        return Math.abs(x) < margin;
    }

    @Override
    public boolean isOnLine(Scalar other, float epsilon) {
        return true;
    }

    @Override
    public boolean isOnLine(Scalar other) {
        return true;
    }

    @Override
    public boolean isCollinear(Scalar other, float epsilon) {
        return true;
    }

    @Override
    public boolean isCollinear(Scalar other) {
        return true;
    }

    @Override
    public boolean isCollinearOpposite(Scalar other) {
        return !hasSameDirection(other);
    }

    @Override
    public boolean isCollinearOpposite(Scalar other, float epsilon) {
        return hasOppositeDirection(other);
    }

    @Override
    public boolean isPerpendicular(Scalar other) {
        return false;
    }

    @Override
    public boolean isPerpendicular(Scalar other, float epsilon) {
        return false;
    }

    @Override
    public boolean hasSameDirection(Scalar other) {
        return Math.signum(x) == Math.signum(other.x);
    }

    @Override
    public boolean hasOppositeDirection(Scalar other) {
        return Math.signum(x) != Math.signum(other.x);
    }

    @Override
    public boolean epsilonEquals(Scalar other, float epsilon) {
        return (Math.abs(x - other.x) <= epsilon);
    }

    public boolean epsilonEquals(Scalar other) {
        return epsilonEquals(other, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    public boolean epsilonEquals(float other) {
        return (Math.abs(x - other) <= MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Override
    public Scalar mulAdd(Scalar v, float scalar) {
        x += v.x * scalar;
        return this;
    }

    @Override
    public Scalar mulAdd(Scalar v, Scalar mulVec) {
        x += v.x * mulVec.x;
        return this;
    }

    @Override
    public Scalar setZero() {
        x = 0;
        return this;
    }

    /**
     * Converts this {@code Scalar} to a string in the format {@code (x)}.
     *
     * @return a string representation of this object.
     */
    @Override
    public String toString() {
        return "(" + x + ")";
    }

    /**
     * Sets this {@code Scalar} to the value represented by the specified string according to the format of {@link #toString()}.
     *
     * @param v the string.
     * @return this vector for chaining
     */
    public Scalar fromString(String v) {
        int s = v.indexOf(',', 1);
        if (s != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
            try {
                float x = Float.parseFloat(v.substring(1, s));
                return this.set(x);
            } catch (NumberFormatException ex) {
                // Throw a GdxRuntimeException
            }
        }
        throw new GdxRuntimeException("Malformed Scalar: " + v);
    }

    @Override
    public int hashCode() {
        return NumberUtils.floatToIntBits(x);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Scalar other = (Scalar) obj;
        return NumberUtils.floatToIntBits(x) == NumberUtils.floatToIntBits(other.x);
    }
}
