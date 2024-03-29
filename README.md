# gdx-tween
gdx-tween is a library for [libGDX](https://github.com/libgdx/libgdx) used for in-betweening (or tweening) values, 
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
The library is not yet stable. The API is rapidly evolving with breaking changes. The GtColor class is stable.

<a href="https://central.sonatype.com/artifact/com.cyphercove.gdxtween/gdxtween" alt="Maven Central">
    <img src="https://img.shields.io/maven-central/v/com.cyphercove.gdxtween/gdxtween?color=6d7ac5" /></a>
<a href="https://www.libgdx.com" alt="libGDX">
    <img src="https://img.shields.io/badge/libgdx-1.12.0-e74a45" /></a>

gdx-tween is available via Maven Central:

    implementation "com.cyphercove.gdxtween:gdxtween:0.1.8"
    
If using Kotlin, use gdx-tween-kt instead:

    implementation "com.cyphercove.gdxtween:gdxtween-kt:0.1.8"
    
To use with GWT, add this to the `.gwt.xml` file:

    <inherits name="com.cyphercove.gdxtween"/>
    

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
different color format before interpolation, and then converted back. The RGB fields of a Color are typically stored with 
gamma-corrected values (aka sRGB), but some color spaces only produce even blends when they are applied to linear RGB. 
Many color math equations expect linear RGB. 

gdx-tween's color interpolations allow you to choose to keep your Color objects in sRGB or linear space and select
interpolations that either expand gamma correction to linear space to perform the math, or leave it alone. 

ColorSpace can be selected by using `ColorAction.colorSpace()`. For each color space, there is a direct version, and a
"Degamma" version. Using the Degamma version of a color space means that it assumes that the Color object is 
in gamma-corrected sRGB space (as is typical in libGDX if using the color with SpriteBatch's default shader), and that
gamma correction should be removed for the interpolation and then reapplied on the result.

The following are the available color spaces. They can be compared in real time using 
[this web app](https://cyphercove.github.io/ColorInterpolationComparison/).

 * **RGB**: This provides direct interpolation of a Color's RGB values and is the least computationally expensive, but may 
 produce muddy blends. If `DegammaRgb` is used on an sRGB Color, the blend will be smooth in terms of light 
 energy, but it will not appear even to the eye.
 * **HSV**: Hue, saturation, and value color space. This can prevent desaturated color from appearing in the middle when 
 interpolating between two saturated colors, but has a tendency to introduce intermediate hues which can produce a 
 rainbow effect. Moderate computational cost.
 * **HCL**: Hue, chroma and lightness color space, as defined by HSL. This has a similar effect as HSV, but treats 
 "whiteness" as distinct from saturation, so blends between whitish or dark colors to pure colors may appear more stable. 
 * **HSL**: Hue, saturation and lightness. This is similar to HCL and is provided for completeness, but it can have
 surprising intermediate bright colors when blending between colors that are very close to black and white but aren't
 very saturated.
 * **Lab**: CIELAB color space. Lab color space was designed specifically to make even changes in the color parameters look
 even to the human eye. It produces extremely smooth-looking blends. However, for certain transitions it may produce
 faint hints of intermediate hues. Lab should always be performed on linear RGB, so if using sRGB Color, use `DegammaLab`.
 * **LCH**: This is a cylindrical transformation of Lab color space, so it has an angular hue component analogous to that of
 HSV. Blends are very smooth, but may contain intermediate hues, producing a rainbow effect.
 * **IPT**: IPT color space. It produces extremely smooth-looking blends and has better hue stability than Lab. IPT 
 should always be performed on linear RGB, so if using sRGB Color, use `DegammaLch`.
 * **LMS Compressed**: This is the gamma compressed LMS color space that is the intermediate step of transforming XYZ color
 to IPT color space, as defined by IPT's forward transform. It also produces very smooth-looking blends, but omits a
 matrix multiplication step as compared to IPT, so it is less expensive. LMS Compressed should always be performed on 
 linear RGB, so if using sRGB Color, use `DegammaLmsCompressed`.

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

Top level functions `inSequence` and `inParallel` allow creation of complex tweens using lambda functions, which is more
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

gdx-tween is covered by the [Apache 2.0 license](LICENSE.md). The GtColor class contains an optimization in the  
`fromLab()` method ported from [ac-colors](https://github.com/vinaypillai/ac-colors), which is covered by the 
[MIT license](LICENSE-AC-COLORS.md).

The example uses [VisUI](https://github.com/kotcrab/vis-ui) and the default VisUI skin.

