/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.well.ndemo.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.well.ndemo.R;


/**
 * A transition that animates the elevation of a View from a given value down to zero.
 * <p>
 * Useful for creating parent↔child navigation transitions (https://www.google.com/design/spec/patterns/navigational-transitions.html#navigational-transitions-parent-to-child)
 * when combined with a {@link android.transition.ChangeBounds} on a shared element.
 */
public class LiftOff extends Transition {

    private static final String PROPNAME_ELEVATION = "plaid:liftoff:elevation";

    private static final String[] transitionProperties = {
            PROPNAME_ELEVATION
    };

    private final float lift;

    public LiftOff(float lift) {
        this.lift = lift;
    }

    public LiftOff(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LiftOff);
        lift = ta.getDimensionPixelSize(R.styleable.LiftOff_android_elevation, 0);
        ta.recycle();
    }

    @Override
    public String[] getTransitionProperties() {
        return transitionProperties;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_ELEVATION, 0f);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_ELEVATION, lift);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
                                   TransitionValues endValues) {
        return ObjectAnimator.ofFloat(endValues.view, View.TRANSLATION_Z, lift, 0f);
    }

}
