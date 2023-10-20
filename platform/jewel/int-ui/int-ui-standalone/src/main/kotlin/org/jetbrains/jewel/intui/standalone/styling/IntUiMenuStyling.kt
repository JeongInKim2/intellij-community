package org.jetbrains.jewel.intui.standalone.styling

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.intui.core.theme.IntUiDarkTheme
import org.jetbrains.jewel.intui.core.theme.IntUiLightTheme
import org.jetbrains.jewel.intui.standalone.standalonePainterProvider
import org.jetbrains.jewel.painter.PainterProvider
import org.jetbrains.jewel.styling.MenuColors
import org.jetbrains.jewel.styling.MenuIcons
import org.jetbrains.jewel.styling.MenuItemColors
import org.jetbrains.jewel.styling.MenuItemMetrics
import org.jetbrains.jewel.styling.MenuMetrics
import org.jetbrains.jewel.styling.MenuStyle
import org.jetbrains.jewel.styling.SubmenuMetrics

@Composable
fun MenuStyle.Companion.light(
    colors: MenuColors = MenuColors.light(),
    metrics: MenuMetrics = MenuMetrics.defaults(),
    icons: MenuIcons = MenuIcons.defaults(),
) = MenuStyle(colors, metrics, icons)

@Composable
fun MenuStyle.Companion.dark(
    colors: MenuColors = MenuColors.dark(),
    metrics: MenuMetrics = MenuMetrics.defaults(),
    icons: MenuIcons = MenuIcons.defaults(),
) = MenuStyle(colors, metrics, icons)

@Composable
fun MenuColors.Companion.light(
    background: Color = IntUiLightTheme.colors.grey(14),
    border: Color = IntUiLightTheme.colors.grey(9),
    shadow: Color = Color(0x78919191), // Not a palette color
    itemColors: MenuItemColors = MenuItemColors.light(),
) = MenuColors(background, border, shadow, itemColors)

@Composable
fun MenuColors.Companion.dark(
    background: Color = IntUiDarkTheme.colors.grey(2),
    border: Color = IntUiDarkTheme.colors.grey(3),
    shadow: Color = Color(0x66000000), // Not a palette color
    itemColors: MenuItemColors = MenuItemColors.dark(),
) = MenuColors(background, border, shadow, itemColors)

@Composable
fun MenuItemColors.Companion.light(
    background: Color = IntUiLightTheme.colors.grey(14),
    backgroundDisabled: Color = IntUiLightTheme.colors.grey(14),
    backgroundFocused: Color = IntUiLightTheme.colors.blue(11),
    backgroundPressed: Color = background,
    backgroundHovered: Color = backgroundFocused,
    content: Color = IntUiLightTheme.colors.grey(1),
    contentDisabled: Color = IntUiLightTheme.colors.grey(8),
    contentFocused: Color = content,
    contentPressed: Color = content,
    contentHovered: Color = content,
    iconTint: Color = IntUiLightTheme.colors.grey(7),
    iconTintDisabled: Color = iconTint,
    iconTintFocused: Color = iconTint,
    iconTintPressed: Color = iconTint,
    iconTintHovered: Color = iconTint,
    separator: Color = IntUiLightTheme.colors.grey(12),
) = MenuItemColors(
    background,
    backgroundDisabled,
    backgroundFocused,
    backgroundPressed,
    backgroundHovered,
    content,
    contentDisabled,
    contentFocused,
    contentPressed,
    contentHovered,
    iconTint,
    iconTintDisabled,
    iconTintFocused,
    iconTintPressed,
    iconTintHovered,
    separator,
)

@Composable
fun MenuItemColors.Companion.dark(
    background: Color = IntUiDarkTheme.colors.grey(2),
    backgroundDisabled: Color = IntUiDarkTheme.colors.grey(2),
    backgroundFocused: Color = IntUiDarkTheme.colors.blue(2),
    backgroundPressed: Color = background,
    backgroundHovered: Color = background,
    content: Color = IntUiDarkTheme.colors.grey(12),
    contentDisabled: Color = IntUiDarkTheme.colors.grey(7),
    contentFocused: Color = content,
    contentPressed: Color = content,
    contentHovered: Color = content,
    iconTint: Color = IntUiDarkTheme.colors.grey(10),
    iconTintDisabled: Color = iconTint,
    iconTintFocused: Color = iconTint,
    iconTintPressed: Color = iconTint,
    iconTintHovered: Color = iconTint,
    separator: Color = IntUiDarkTheme.colors.grey(3),
) = MenuItemColors(
    background,
    backgroundDisabled,
    backgroundFocused,
    backgroundPressed,
    backgroundHovered,
    content,
    contentDisabled,
    contentFocused,
    contentPressed,
    contentHovered,
    iconTint,
    iconTintDisabled,
    iconTintFocused,
    iconTintPressed,
    iconTintHovered,
    separator,
)

fun MenuMetrics.Companion.defaults(
    cornerSize: CornerSize = CornerSize(8.dp),
    menuMargin: PaddingValues = PaddingValues(vertical = 6.dp),
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    offset: DpOffset = DpOffset((-6).dp, 2.dp),
    shadowSize: Dp = 12.dp,
    borderWidth: Dp = 1.dp,
    itemMetrics: MenuItemMetrics = MenuItemMetrics.defaults(),
    submenuMetrics: SubmenuMetrics = SubmenuMetrics.defaults(),
) = MenuMetrics(cornerSize, menuMargin, contentPadding, offset, shadowSize, borderWidth, itemMetrics, submenuMetrics)

fun MenuItemMetrics.Companion.defaults(
    selectionCornerSize: CornerSize = CornerSize(4.dp),
    outerPadding: PaddingValues = PaddingValues(horizontal = 4.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
    separatorPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
    separatorThickness: Dp = 1.dp,
) = MenuItemMetrics(selectionCornerSize, outerPadding, contentPadding, separatorPadding, separatorThickness)

fun SubmenuMetrics.Companion.defaults(
    offset: DpOffset = DpOffset(0.dp, (-8).dp),
) = SubmenuMetrics(offset)

fun MenuIcons.Companion.defaults(
    submenuChevron: PainterProvider = standalonePainterProvider("expui/general/chevronRight.svg"),
) = MenuIcons(submenuChevron)
