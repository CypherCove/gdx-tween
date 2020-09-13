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
package com.cyphercove.gdxtween.targettweens;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.cyphercove.gdxtween.TargetTween;

/** An AccessorTween uses an Accessor as its target. An Accessor provides an indirect way to target specific fields
 * of an object without making the object the tween's target. This is necessary if an object has fields that should be
 * modified independently.
 */
public class AccessorTween extends TargetTween<AccessorTween, AccessorTween.Accessor> {

    static private final IntMap<Pool<AccessorTween>> accessorPools = new IntMap<Pool<AccessorTween>>();

    static private Pool<AccessorTween> getPool(final int vectorSize) {
        Pool<AccessorTween> pool = accessorPools.get(vectorSize);
        if (pool == null) {
            pool = new Pool<AccessorTween>(100) {
                @Override
                protected AccessorTween newObject() {
                    return new AccessorTween(vectorSize);
                }
            };
            accessorPools.put(vectorSize, pool);
        }
        return pool;
    }

    public static AccessorTween newInstance(int vectorSize) {
        return getPool(vectorSize).obtain();
    }

    public interface Accessor {
        /**
         * Provides the number of values that the accessor will modify. This number is only read when the accessor is
         * attached to an AccessorTween.
         * @return The number of values that this Accessor retrieves and modifies.
         */
        int getNumberOfValues();

        /**
         * Retrieve the current value of the item at the given index.
         * @param index The index corresponding with the item to retrieve.
         * @return The current value for the item.
         */
        float getValue (int index);

        /**
         * Apply the new value to the item at the given index.
         * @param index The index corresponding with the item to set.
         * @param newValue The new value to set.
         */
        void setValue (int index, float newValue);
    }

    public AccessorTween (int vectorSize){
        super(vectorSize);
    }

    public int getVectorSize (){
        return vectorSize;
    }

    protected void begin () {
        super.begin();
        for (int i = 0; i < getVectorSize(); i++) {
            setStartValue(i, target.getValue(i));
        }
    }

    protected void apply (int vectorIndex, float value) {
        target.setValue(vectorIndex, value);
    }

    public AccessorTween end (int vectorIndex, float value){
        super.setEndValue(vectorIndex, value);
        return this;
    }

    public float getEndValue (int vectorIndex){
        return super.getEndValue(vectorIndex);
    }

    @Override
    public Class<Accessor> getTargetType() {
        return Accessor.class;
    }

    @Override
    public void free() {
        super.free();
        getPool(vectorSize).free(this);
    }
}
