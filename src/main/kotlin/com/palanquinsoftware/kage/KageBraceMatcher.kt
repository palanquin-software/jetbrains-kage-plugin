package com.palanquinsoftware.kage

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.palanquinsoftware.kage.psi.KageTokenTypes

class KageBraceMatcher : PairedBraceMatcher {

    private val pairs = arrayOf(
        BracePair(KageTokenTypes.LBRACE, KageTokenTypes.RBRACE, true),
        BracePair(KageTokenTypes.LPAREN, KageTokenTypes.RPAREN, false),
        BracePair(KageTokenTypes.LBRACKET, KageTokenTypes.RBRACKET, false),
    )

    override fun getPairs() = pairs
    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true
    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int) = openingBraceOffset
}
