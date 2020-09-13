# gdx-tween
gdx-tween is a library for [libGDX](https://github.com/libgdx/libgdx) used for inbetweening (or tweening) values, 
interpolating them over time.

# Introduction
The main goals of this library:

 * Provide an easy way to create tweens with concise syntax (no need to create interface implementations in most cases).
 * Rely on existing libGDX classes and idioms so integration in a libGDX project is natural. 
 * Support second-order tweening so tweens can be interrupted smoothly, without a sudden change in speed.
 * Handle pooling automatically to minimize garbage collection.

 Planned features not yet implemented:
 * Usable in Scene2D so the second order interrupting behavior can be used in Actions.

## Installation
When stable, gdx-tween will be available on JCenter. For now, you can publish to Maven local and use:

    implementation "com.cyphercove.gdxtween:gdxtween:0.1.0"
    
If using Kotlin, use gdx-tween-kt instead:

    implementation "com.cyphercove.gdxtween:gdxtween-kt:0.1.0"

See [CHANGES.md](CHANGES.md) for the change log, which lists breaking changes and libGDX version increases.

## Usage

### The basics

First, you need a TweenRunner to run your tweens with. A TweenRunner is responsible for starting tweens, interrupting
tweens that target the same object as a new tween, stepping through the animation of the tweens, and freeing the tweens
and related objects to a pool when they are finished.

```java
private final TweenRunner tweenRunner = new TweenRunner();
```

The `step` method must be called one time somewhere in the game loop:

```java
public void render(float deltaTime)
    tweenRunner.step(deltaTime);
    //...
}
```

A single tween operates on a target object. The target is the object whose values the tween changes over ime. Start a 
tween by selecting a `to` method from `Tweens`, customizing it, and calling `start()`:

```java
Tweens.to(myVector2, 1f, 1f).duration(3f)
    .start(tweenRunner);
```

A tween's parameters must not be modified after starting it or adding it to a group. These parameter changes are ignored, 
but log a warning.

### GroupTweens

The API of setting up Tween sequences is heavily inspired by 
[UniversalTweenEngine's](https://github.com/AurelienRibon/universal-tween-engine) Timeline API.

Tweens can be built into complex series of events using `Tweens.inSequence()` and `Tweens.inParallel()`:

```java
Tweens.inSequence()
    .run(Tweens.to(playerPosition, 1f, 1f).duration(1f))
    .run(Tweens.to(playerAlpha, 0.5f).duration(1f))
    .delay(0.3f)
    .inParallel()
        .run(Tweens.to(playerPosition, -1f, -1f).duration(1f))
        .run(Tweens.to(playerAlpha, 1f).duration(1f))
    .then()
    .run(Tweens.to(playerPosition, 1f, 1f).duration(1f))
    .start(tweenRunner);
```

Calling `.duration()`, `.ease()`, or `.using()` on a GroupTween sets a default value to use for the children if they
have none set. This is especially useful for ParallelTweens, since they often run several tweens of the same length.

```java
Tweens.inParallel()
    .duration(3f)
    .run(Tweens.to(playerPosition, -1f, -1f))
    .run(Tweens.toAlpha(playerColor, 1f))
    .start(tweenRunner);
```

A sequence can also be created using `then()` on a tween if it isn't already part of a sequence. If it is already the 
direct child of a sequence, its parent sequence is returned.

```java
Tween.to(playerPosition, 1f, 1f, 1f).duration(0.5f)
    .then().run(Tweens.to(playerPosition, 0f, 1f, 1f).duration(0.5f))
    .start(tweenRunner);
```

Calling `start()` on a tween that is the child of a group will actually submit the top level parent of the group:

```java
Tween.inSequence()
    .run(Tweens.to(playerPosition, 1f, 1f, 1f).ease(Ease.cubic()))
    .inParallel()
        .run(Tweens.to(playerPosition, -1f, -1f, 1f).ease(Ease.cubic()))
        .run(Tweens.toAlpha(playerColor, 1f, 1f).ease(Ease.cubic()))
    // .then() OK to omit this line. The parent sequence will be started.
    .start(tweenRunner);
```

### Automatic interruption

Tweens that modify a single object (e.g. not SequenceTween, ParallelTween or DelayTween) are called TargetTweens, and they
automatically interrupt other running TargetTweens that modify the same object.

TweenRunner automatically finds running TargetTweens that are a match for the newly submitted TargetTween and cancels 
them.

When a SequenceTween is submitted, only its first child is eligible to automatically interrupt running tweens. (This is
to avoid the ambiguous behavior of when later children in the sequence should interrupt currently-running tweens.)

When a ParallelTween is submitted, all of its children are eligible to interrupt. If a ParallelTween is the first child of a 
SequenceTween, then all its children are eligible to interrupt.

By default, if any child in the hierarchy of a GroupTween is interrupted, the whole hierarchy is canceled. This behavior
can be changed to only mute the specific children that are interrupted, using `GroupTween.childInterruptionBehavior()`.

### Eases and blends

TODO ...

### Color and alpha

ColorTween and AlphaTween are unique from the other TargetTweens, because they work independently on different fields of
a Color object and do not interrupt each other. ColorTween modifies only the RGB components. This allows transparency to 
be treated separately from RGB.

The RGB components can be interpolated in various color spaces. This means the start and end values are converted to a 
different color format before interpolation, and then converted back. The RGB fields of a Color are stored with 
gamma-corrected values, but this can lead to visually uneven transitions where brightness or hue do not appear to 
change at a constant rate. So, other color spaces may be preferred for interpolations. In fact, the default ColorSpace
of a ColorTween is not the basic gamma-corrected RGB that is used by `Color.lerp` and scene2D's `ColorAction`, but 
rather `ColorSpace.LinearRgb`.

ColorSpace can be selected by using `ColorAction.colorSpace()`. The available types:

 * *Rgb*: Gamma-corrected color space. This provides direct interpolation of RGB values and is the least computationally
 expensive, but may produce very uneven-looking or muddy blends.
 * *LinearRgb*: (The default) Linear color space. This results in significantly smoother transitions at slightly more
 computational cost.
 * *Hsv*: Hue, saturation, and value color space. This can prevent desaturated color from appearing in the middle when 
 interpolating between two saturated colors, but has a tendency to introduce intermediate hues which can produce a 
 rainbow effect. Moderate computational cost.
 * *Lab*: CIELAB color space. Lab color space was designed specifically to make even changes in the color parameters look
 even to the human eye. It produces extremely smooth-looking blends. However, for certain transitions it may produce
 faint hints of intermediate hues. It has a high computational cost.



### Callbacks

TweenCompletionListener can be added to any tween using `.completionListener()`. It fires when a tween reaches its end.
Children of a GroupTween can have their own individual listeners that fire as they are completed.

TargetInterruptionListener can be added to TargetTweens using `.interruptionListener()`. It fires when another 
TargetTween interrupts its owner.

## Kotlin

If you use Kotlin, you can use the `-kt` version of the library to get some helper extension functions that improve 
conciseness and readability.

### Target extensions

Target object extension functions are available for all the library TargetTweens. These can be used to start tweens 
with more natural syntax. For example:

```kotlin
val myTween = myPosition.tweenTo(4f, 5f).duration(2f)
```

### TweenManager

You can use the TweenManager with a DelegateTweenManager to simplify some calls. It results in an already-populated 
`tweenRunner` property, one which will rarely need to be used directly, because the interface provides some extension
functions that implicitly use it.

```kotlin
class MyScreen: Screen, TweenManager by DelegateTweenManager() {

    // ...

    override fun render(deltaTime: Float) {
        stepTweens(deltaTime)
        // ...
    }

    fun startSomeTween() {
        Tweens.to(someVector, 1f, 1f, 1f).start() // Don't have to pass TweenRunner to start()
    }
}
```

### Building GroupTweens

Top level functions `inSequence` and `inBuilder` allow creation of complex tweens using lambda functions, which is more
concise and allows the IDE to indent nested tweens for you. Extension functions for creating and adding all the library 
TargetTweens are also provided for GroupTween, meaning they can be added without wrapping them in `run()`, and 
ParallelTweens don't need to be followed by a call to `.then()`. (Note `run()` is still required if using the target 
extension functions above.) GroupTween also has extensions for the library Eases, so the `Ease.` prefix can be omitted
inside the lambda.

```kotlin
inSequence { 
    ease(cubic())
    to(playerPosition, 1f, 1f).duration(1f)
    toAlpha(playerColor, 0.5f).duration(0.5f)
    delay(0.3f)
    inParallel {
        using(1f, cubic())
        to(playerPosition, -1f, -1f)
        toAlpha(playerColor, 1f, 1f)
        inSequence {
            delay(0.2f)
            toRgb(playerColor, Color.RED)
            toRgb(playerColor, Color.WHITE)
        }
    }
    to(playerPosition, 1f, 1f, 1f)
}.start(tweenRunner)
```

## License

gdx-tween is covered by the [Apache 2.0 license](LICENSE.md). Substantial portions of the GtColor class were 
ported from [ac-colors](https://github.com/vinaypillai/ac-colors), which is covered by the 
[MIT license](LICENSE-AC-COLORS.md).

The example uses [VisUI](https://github.com/kotcrab/vis-ui) and the default VisUI skin.

