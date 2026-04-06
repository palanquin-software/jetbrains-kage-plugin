package com.palanquinsoftware.kage

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class KageCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet,
                ) {
                    KEYWORDS.forEach { kw ->
                        result.addElement(LookupElementBuilder.create(kw).bold())
                    }
                    TYPES.forEach { t ->
                        result.addElement(
                            LookupElementBuilder.create(t)
                                .bold()
                                .withTypeText("type", true)
                        )
                    }
                    BUILTINS.forEach { (name, sig) ->
                        result.addElement(
                            LookupElementBuilder.create(name)
                                .withTailText(sig, true)
                                .withTypeText("builtin", true)
                        )
                    }
                    IMAGE_FUNCS.forEach { (name, sig) ->
                        result.addElement(
                            LookupElementBuilder.create(name)
                                .withTailText(sig, true)
                                .withTypeText("image", true)
                        )
                    }
                }
            }
        )
    }

    companion object {
        private val KEYWORDS = listOf(
            "package", "func", "var", "const", "type", "return",
            "if", "else", "for", "break", "continue", "discard",
            "struct", "true", "false",
        )

        private val TYPES = listOf(
            "bool", "int", "float",
            "vec2", "vec3", "vec4",
            "ivec2", "ivec3", "ivec4",
            "mat2", "mat3", "mat4",
        )

        private val BUILTINS = listOf(
            "sin" to "(x) float", "cos" to "(x) float", "tan" to "(x) float",
            "asin" to "(x) float", "acos" to "(x) float",
            "atan" to "(y_over_x) float", "atan2" to "(y, x) float",
            "sinh" to "(x) float", "cosh" to "(x) float", "tanh" to "(x) float",
            "radians" to "(degrees) float", "degrees" to "(radians) float",
            "pow" to "(x, y) float", "exp" to "(x) float", "log" to "(x) float",
            "exp2" to "(x) float", "log2" to "(x) float",
            "sqrt" to "(x) float", "inversesqrt" to "(x) float",
            "abs" to "(x) float", "sign" to "(x) float",
            "floor" to "(x) float", "ceil" to "(x) float", "fract" to "(x) float",
            "mod" to "(x, y) float",
            "min" to "(x, y) float", "max" to "(x, y) float",
            "clamp" to "(x, minVal, maxVal) float",
            "mix" to "(x, y, a) float", "step" to "(edge, x) float",
            "smoothstep" to "(edge0, edge1, x) float",
            "length" to "(x) float", "distance" to "(p0, p1) float",
            "dot" to "(x, y) float", "cross" to "(x, y) vec3",
            "normalize" to "(x) vec",
            "faceforward" to "(n, i, nref) vec",
            "reflect" to "(i, n) vec", "refract" to "(i, n, eta) vec",
            "transpose" to "(m) mat",
            "dfdx" to "(p) vec", "dfdy" to "(p) vec", "fwidth" to "(p) vec",
            "frontfacing" to "() bool",
            "len" to "(a) int", "cap" to "(a) int",
        )

        private val IMAGE_FUNCS = listOf(
            "imageSrc0At" to "(pos vec2) vec4",
            "imageSrc1At" to "(pos vec2) vec4",
            "imageSrc2At" to "(pos vec2) vec4",
            "imageSrc3At" to "(pos vec2) vec4",
            "imageSrc0UnsafeAt" to "(pos vec2) vec4",
            "imageSrc1UnsafeAt" to "(pos vec2) vec4",
            "imageSrc2UnsafeAt" to "(pos vec2) vec4",
            "imageSrc3UnsafeAt" to "(pos vec2) vec4",
            "imageSrc0Origin" to "() vec2",
            "imageSrc1Origin" to "() vec2",
            "imageSrc2Origin" to "() vec2",
            "imageSrc3Origin" to "() vec2",
            "imageSrc0Size" to "() vec2",
            "imageSrc1Size" to "() vec2",
            "imageSrc2Size" to "() vec2",
            "imageSrc3Size" to "() vec2",
            "imageDstOrigin" to "() vec2",
            "imageDstSize" to "() vec2",
        )
    }
}
