package org.jetbrains.jewel.buildlogic.palette

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.util.internal.GUtil
import java.net.URL

class IntUiPaletteGeneratePlugin : BaseJewelPaletteGeneratePlugin() {

    override fun createExtension(project: Project): PaletteGenerateContainer =
        PaletteGenerateContainer(project.container(PaletteGeneration::class.java) {
            PaletteGeneration(it, project).apply {
                targetDir.set(project.layout.buildDirectory.dir("generated/palette"))
                ideaVersion.set("232.6734")
            }
        }).apply {
            project.extensions.add("intUiPaletteGenerator", this)
        }

    override fun createGenerateTask(project: Project, extension: PaletteGenerateContainer): List<TaskProvider<out BasePaletteGenerateTask>> =
        extension.map { config ->
            project.tasks.register("generate${GUtil.toCamelCase(config.name)}Palette", IntUiPaletteGenerateTask::class.java).apply {
                configure {
                    output.set(config.targetDir.file(config.paletteClassName.map {
                        val className = ClassName.bestGuess(it)
                        className.packageName.replace('.', '/') + "/${className.simpleName}.kt"
                    }))
                    paletteClassName.set(config.paletteClassName)
                    ideaVersion.set(config.ideaVersion)
                    themeFile.set(config.themeFile)
                }
            }
        }
}

open class IntUiPaletteGenerateTask : BasePaletteGenerateTask() {

    private val colorGroups = setOf(
        "Grey", "Blue", "Green", "Red", "Yellow", "Orange", "Purple", "Teal"
    )

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun doGenerate() {
        val url = buildString {
            append("https://raw.githubusercontent.com/JetBrains/intellij-community/")
            append(ideaVersion.get())
            append("/")
            append(themeFile.get())
        }

        val theme = URL(url).openStream().use {
            json.decodeFromString<IntellijThemeDescriptor>(it.reader().readText())
        }

        val className = ClassName.bestGuess(paletteClassName.get())

        val file = FileSpec.builder(className).apply {
            indent("    ")
            addFileComment("Generated by the Jewel IntUI Palette Generator\n")
            addFileComment("Generated from the IntelliJ Platform version ${ideaVersion.get()}\n")
            addFileComment("Source: $url")

            addType(TypeSpec.objectBuilder(className).apply {
                addSuperinterface(ClassName.bestGuess("org.jetbrains.jewel.themes.intui.core.IntUiColorPalette"))

                theme.colors.forEach { (name, value) ->
                    generateColor(name, value)
                }

                theme.colors.entries.groupBy {
                    it.key.replace("""\d+""".toRegex(), "")
                }.filterKeys {
                    colorGroups.contains(it)
                }.forEach { (group, colors) ->
                    generateColorGroup(className, group, colors)
                }

                addFunction(
                    FunSpec.builder("isDark")
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(Boolean::class.java)
                        .addStatement("return %L", theme.dark)
                        .build()
                )
            }.build())
        }.build()

        output.get().asFile.bufferedWriter().use {
            file.writeTo(it)
        }
    }

    private fun TypeSpec.Builder.generateColor(name: String, value: String) {
        addProperty(PropertySpec.builder(GUtil.toLowerCamelCase(name), colorClassName).apply {
            initializer("Color(%L)", value.replace("#", "0xFF"))
        }.build())
    }

    private fun TypeSpec.Builder.generateColorGroup(paletteClassName: ClassName, group: String, colors: List<Map.Entry<String, String>>) {
        val funcName = paletteClassName.member(GUtil.toLowerCamelCase(group))

        addFunction(FunSpec.builder(funcName).apply {
            returns(list.parameterizedBy(colorClassName))
            addModifiers(KModifier.OVERRIDE)
            addCode(CodeBlock.builder().apply {
                when {
                    colors.isEmpty() -> addStatement("return emptyList()")
                    colors.size <= 5 -> {
                        add("return listOf(")
                        colors.forEachIndexed { index, (name, _) ->
                            if (index != 0) add(",")
                            add("%N", paletteClassName.member(GUtil.toLowerCamelCase(name)))
                        }
                        add(")")
                    }

                    else -> {
                        add("return listOf(\n").indent()
                        colors.forEach { (name, _) ->
                            add("%N,\n", paletteClassName.member(GUtil.toLowerCamelCase(name)))
                        }
                        unindent().add(")")
                    }
                }
            }.build())
        }.build())

        addFunction(FunSpec.builder(funcName).apply {
            returns(colorClassName)
            addModifiers(KModifier.OVERRIDE)
            addParameter("index", Int::class)
            addCode(CodeBlock.builder().apply {
                addStatement("return %N()[index - 1]", funcName)
            }.build())
        }.build())
    }

    private val list = ClassName("kotlin.collections", "List")
    private val colorClassName = ClassName("androidx.compose.ui.graphics", "Color")
}
