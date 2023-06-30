package org.jetbrains.jewel.themes.intui.standalone.styling

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.styling.LazyTreeColors
import org.jetbrains.jewel.styling.LazyTreeIcons
import org.jetbrains.jewel.styling.LazyTreeMetrics
import org.jetbrains.jewel.styling.LazyTreeStyle
import org.jetbrains.jewel.themes.intui.core.palette.IntUiDarkPalette
import org.jetbrains.jewel.themes.intui.core.palette.IntUiLightPalette

@Stable
data class IntUiLazyTreeStyle(
    override val colors: IntUiLazyTreeColors,
    override val metrics: IntUiLazyTreeMetrics,
    override val icons: IntUiLazyTreeIcons
) : LazyTreeStyle {

    companion object {

        @Composable
        fun light(
            colors: IntUiLazyTreeColors = IntUiLazyTreeColors.light(),
            metrics: IntUiLazyTreeMetrics = IntUiLazyTreeMetrics(),
            icons: IntUiLazyTreeIcons = IntUiLazyTreeIcons()
        ) = IntUiLazyTreeStyle(colors, metrics, icons)

        @Composable
        fun dark(
            colors: IntUiLazyTreeColors = IntUiLazyTreeColors.dark(),
            metrics: IntUiLazyTreeMetrics = IntUiLazyTreeMetrics(),
            icons: IntUiLazyTreeIcons = IntUiLazyTreeIcons()
        ) = IntUiLazyTreeStyle(colors, metrics, icons)
    }
}

@Immutable
data class IntUiLazyTreeColors(
    override val elementBackgroundFocused: Color,
    override val elementBackgroundSelected: Color,
    override val elementBackgroundSelectedFocused: Color,
    override val chevronTint: Color,
    override val chevronTintSelected: Color,
    override val chevronTintFocused: Color,
    override val chevronTintSelectedFocused: Color
) : LazyTreeColors {

    companion object {

        @Composable
        fun light(
            nodeBackgroundFocused: Color = Color.Unspecified,
            nodeBackgroundSelected: Color = IntUiLightPalette.grey(11),
            nodeBackgroundSelectedFocused: Color = IntUiLightPalette.blue(11),
            chevronTint: Color = IntUiLightPalette.grey(7),
            chevronTintSelected: Color = chevronTint,
            chevronTintFocused: Color = chevronTint,
            chevronTintSelectedFocused: Color = chevronTint
        ) = IntUiLazyTreeColors(
            nodeBackgroundFocused,
            nodeBackgroundSelected,
            nodeBackgroundSelectedFocused,
            chevronTint,
            chevronTintSelected,
            chevronTintFocused,
            chevronTintSelectedFocused
        )

        @Composable
        fun dark(
            nodeBackgroundFocused: Color = Color.Unspecified,
            nodeBackgroundSelected: Color = IntUiDarkPalette.grey(4),
            nodeBackgroundSelectedFocused: Color = IntUiDarkPalette.blue(2),
            chevronTint: Color = IntUiDarkPalette.grey(10),
            chevronTintSelected: Color = chevronTint,
            chevronTintFocused: Color = chevronTint,
            chevronTintSelectedFocused: Color = chevronTint
        ) = IntUiLazyTreeColors(
            nodeBackgroundFocused,
            nodeBackgroundSelected,
            nodeBackgroundSelectedFocused,
            chevronTint,
            chevronTintSelected,
            chevronTintFocused,
            chevronTintSelectedFocused
        )
    }
}

@Stable
data class IntUiLazyTreeMetrics(
    override val indentSize: Dp = 8.dp,
    override val elementBackgroundCornerSize: CornerSize = CornerSize(4.dp),
    override val elementPadding: PaddingValues = PaddingValues(horizontal = 12.dp),
    override val elementContentPadding: PaddingValues = PaddingValues(4.dp),
    override val elementMinHeight: Dp = 24.dp,
    override val chevronContentGap: Dp = 2.dp
) : LazyTreeMetrics

@Immutable
data class IntUiLazyTreeIcons(
    override val nodeChevron: String = "icons/intui/chevronRight.svg"
) : LazyTreeIcons
