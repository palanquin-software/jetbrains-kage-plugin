package com.palanquinsoftware.kage

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class KageColorSettingsPage : ColorSettingsPage {

    private val descriptors = arrayOf(
        AttributesDescriptor("Keyword", KageSyntaxHighlighter.KEYWORD),
        AttributesDescriptor("Type", KageSyntaxHighlighter.TYPE_NAME),
        AttributesDescriptor("Builtin function", KageSyntaxHighlighter.BUILTIN_FUNC),
        AttributesDescriptor("Image function", KageSyntaxHighlighter.IMAGE_FUNC),
        AttributesDescriptor("Entry point", KageSyntaxHighlighter.ENTRY_POINT),
        AttributesDescriptor("Number", KageSyntaxHighlighter.NUMBER),
        AttributesDescriptor("Line comment", KageSyntaxHighlighter.LINE_COMMENT),
        AttributesDescriptor("Block comment", KageSyntaxHighlighter.BLOCK_COMMENT),
        AttributesDescriptor("Directive", KageSyntaxHighlighter.DIRECTIVE),
        AttributesDescriptor("Identifier", KageSyntaxHighlighter.IDENTIFIER),
        AttributesDescriptor("Braces", KageSyntaxHighlighter.BRACES),
        AttributesDescriptor("Brackets", KageSyntaxHighlighter.BRACKETS),
        AttributesDescriptor("Parentheses", KageSyntaxHighlighter.PARENS),
        AttributesDescriptor("Operator", KageSyntaxHighlighter.OPERATOR),
        AttributesDescriptor("Comma", KageSyntaxHighlighter.COMMA),
        AttributesDescriptor("Semicolon", KageSyntaxHighlighter.SEMICOLON),
        AttributesDescriptor("Dot", KageSyntaxHighlighter.DOT),
        AttributesDescriptor("Rejected keyword", KageSyntaxHighlighter.REJECTED),
        AttributesDescriptor("Bad character", KageSyntaxHighlighter.BAD_CHAR),
    )

    override fun getAttributeDescriptors() = descriptors
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    override fun getDisplayName() = "Kage"
    override fun getIcon(): Icon = KageIcons.FILE
    override fun getHighlighter(): SyntaxHighlighter = KageSyntaxHighlighter()
    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getDemoText() = """
        //kage:unit pixels

        package main

        // Fragment is the entry point for the fragment shader.
        func Fragment(dstPos vec4, srcPos vec2, color vec4) vec4 {
            // Sample the source image
            clr := imageSrc0At(srcPos)

            // Apply brightness
            brightness := 1.5
            result := vec4(
                clamp(clr.x * brightness, 0.0, 1.0),
                clamp(clr.y * brightness, 0.0, 1.0),
                clamp(clr.z * brightness, 0.0, 1.0),
                clr.w,
            )

            /* Multi-line comment:
               Compute distance from center */
            origin := imageDstOrigin()
            size := imageDstSize()
            center := origin + size / 2.0
            dist := distance(dstPos.xy, center)

            if dist < length(size) * 0.5 {
                return mix(result, vec4(0.0), smoothstep(0.0, 1.0, dist))
            }
            return result
        }
    """.trimIndent()
}
