# Glass Effect Reference - AndroidLiquidGlass (Backdrop Library)

## Library
- **Package**: `io.github.kyant0:backdrop:1.0.4`
- **Requires**: Kotlin 2.3.0+, Compose BOM 2025.01.00+

## Working Parameters

### Effects Configuration
```kotlin
effects = {
    vibrancy()
    blur(2f.dp.toPx())
    lens(12f.dp.toPx(), 24f.dp.toPx())
}
```

### Architecture Pattern
```kotlin
// 1. Create backdrop at parent level
val backdrop = rememberLayerBackdrop()

// 2. Apply layerBackdrop to VISUAL CONTENT (image, gradient, etc.)
Image(
    painter = ...,
    modifier = Modifier
        .layerBackdrop(backdrop)  // Captures pixels
        .fillMaxSize()
)

// 3. Glass components overlay on top using drawBackdrop
Box(
    modifier = Modifier
        .drawBackdrop(
            backdrop = backdrop,
            shape = { RoundedCornerShape(20.dp) },
            effects = {
                vibrancy()
                blur(2f.dp.toPx())
                lens(12f.dp.toPx(), 24f.dp.toPx())
            },
            onDrawSurface = {
                drawRect(surfaceColor)
            }
        )
)
```

## Key Points

1. **layerBackdrop** must be applied to actual visual content (Image, Canvas with graphics)
2. **NOT** to empty Box with just background color - will crash (SIGSEGV)
3. **blur value**: Keep small (2dp works well)
4. **lens effect**: Creates the "liquid glass" distortion look
5. **vibrancy**: Enhances colors through the glass

## Surface Color
```kotlin
val surfaceColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.6f)
```

## Mesh Gradient Background (for backdrop capture)
Located at: `presentation/components/background/MeshGradientBackground.kt`

```kotlin
MeshGradientBackground(
    modifier = Modifier
        .fillMaxSize()
        .layerBackdrop(backdrop),
    primaryColor = OraColors.AccentBlue,
    secondaryColor = OraColors.AccentCyan,
    backgroundColor = OraColors.Dark300
)
```

## Optional: ContinuousCapsule Shape
The library examples use `ContinuousCapsule` from `com.kyant.capsule` for iOS-style continuous corners.
We use `RoundedCornerShape` which also works.

## Imports
```kotlin
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
```
