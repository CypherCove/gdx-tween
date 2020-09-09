# gdx-tween
gdx-tween is a library for [LibGDX](https://github.com/libgdx/libgdx) used for inbetweening (or tweening) values, 
interpolating them over time.

# Introduction
The main goals of this library:

 * Provide an easy way to create tweens with concise syntax (no need to create interface implementations in most cases).
 * Rely on existing LibGDX classes and idioms so integration in a LibGDX project is natural. 
 * Support second order tweening so tweens can be interrupted smoothly, without a sudden change in speed.
 * Handle pooling automatically to minimize garbage collection.

 Planned features not yet implemented:
 * Usable in Scene2D so the second order interrupting behavior can be used in Actions.
 
For clean Kotlin interop: nullability annotations, functional interfaces, trailing lambdas, etc. have been used.

Note: gdx-tween uses Jetbrains' nullability annotations. If you're using Java and don't want to use them, you can disable
them in IntelliJ at **Settings | Editor | Inspections | Java | Probable bugs**.

## Installation
When stable, gdx-tween will be available on JCenter. For now, you can publish to Maven local and use:

    implementation "com.cyphercove.gdx-tween:gdx-tween:0.1.0"
    
If using Kotlin, use gdx-tween-kt instead, which includes features for streamlined usage:

    implementation "com.cyphercove.gdx-tween:gdx-tween-kt:0.1.0"

See [CHANGES.md](CHANGES.md) for the change log, which lists breaking changes and LibGDX version increases.

## Usage

### The basics

First, you need a TweenRunner to run your tweens with. A TweenRunner is responsible for setting up tweens, interrupting
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

In gdx-tween, a single tween operates on a target object. The target is the object whose values the tween changes over 
time. Start a tween by selecting a `to` method from `Tweens`, customizing it, and calling `start()`:

```java
Tweens.to(myVector2, 1f, 1f, 3f)
    .start(tweenRunner);
```

### GroupTweens

The API of setting up Tween sequences is heavily inspired by 
[UniversalTweenEngine's](https://github.com/AurelienRibon/universal-tween-engine) Timeline API.

Tweens can be built into complex series of events using `Tween.inSequence()` and `Tween.inParallel()`:

```java
Tweens.inSequence()
    .run(Tweens.to(playerPosition, 1f, 1f, 1f))
    .run(Tweens.to(playerAlpha, 0.5f, 1f))
    .delay(0.3f)
    .thenInParallel()
        .run(Tweens.to(playerPosition, -1f, -1f, 1f))
        .run(Tweens.to(playerAlpha, 1f, 1f))
    .then()
    .run(Tweens.to(playerPosition, 1f, 1f, 1f))
    .start(tweenManager);
```

A sequence can also be created using `then()` on a tween if it isn't already part of a sequence. If it is the direct 
child of a sequence, its parent sequence is returned.

```java
Tween.to(playerPosition, 1f, 1f, 1f))
    .then().run(Tween.to(playerPosition, 1f, 1f, 1f))
    .start(tweenManager);
```

Calling `start()` on a tween that is the child of a group will actually submit the top level parent of the group:

```java
Tween.inSequence()
    .run(Tweens.to(playerPosition, 1f, 1f, 1f).ease(Ease.cubic()))
    .thenInParallel()
        .run(Tweens.to(playerPosition, -1f, -1f, 1f).ease(Ease.cubic()))
        .run(Tweens.to(playerAlpha, 1f, 1f).ease(Ease.cubic()))
    // .then() OK to omit this line. The parent sequence will be started.
    .start(tweenManager);
```

### Automatic interruption

Tweens that modify a single object (e.g. not SequenceTween, ParallelTween or DelayTween) are called TargetTweens, and they
automatically interrupt other running TargetTweens that modify the same object.

TweenRunner automatically finds running TargetTweens that are a match for the newly submitted TargetTween and cancels 
them.

When a SequenceTween is submitted, only its first child is eligible to automatically interrupt running tweens. (This is
to avoid the ambiguous behavior of when later children in the sequence should interrupt currently-running tweens.)

When a ParallelTween is submitted, all of its children are eligible. If a ParallelTween is the first child of a 
SequenceTween, then all its children are eligible.

By default, if any child in the hierarchy of a GroupTween is interrupted, the whole hierarchy is canceled. This behavior
can be changed to only mute the specific children that are interrupted, using `childInterruptionBehavior()`.

### Eases and blends

TODO ...

### Color and alpha

TODO ...

### Callbacks

TODO ...

## Kotlin

If you use Kotlin, you can use the `-kt` version of the library to get some helper extension functions that improve 
conciseness.

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

### TweenBuilder

The `tween` function can be used to build a tween using a `TweenBuilder`, which is utility class with access to all the
members of `Tweens` and `Ease`, thereby allowing you to omit the `Tweens.` and `Ease.` prefixes within the passed lambda
function (without having to do static imports that pollute autocomplete throughout the file you're working on).

```kotlin
tween { 
    inSequence()
    .run(to(playerPosition, 1f, 1f, 1f))
    .run(to(playerAlpha, 0.5f, 1f))
    .delay(0.3f)
    .thenInParallel()
        .run(to(playerPosition, -1f, -1f, 1f).ease(cubic()))
        .run(to(playerAlpha, 1f, 1f).ease(smoothstep))
    .then()
    .run(Tween.to(playerPosition, 1f, 1f, 1f))
}.start(tweenRunner)
```

## License

gdx-tween is covered by the [Apache 2.0 license](LICENSE.md). Substantial portions of the GtColor class were 
ported from [ac-colors](https://github.com/vinaypillai/ac-colors), which is covered by the 
[MIT license](LICENSE-AC-COLORS.md).

