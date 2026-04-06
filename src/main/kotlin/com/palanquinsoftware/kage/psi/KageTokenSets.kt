package com.palanquinsoftware.kage.psi

import com.intellij.psi.tree.TokenSet

object KageTokenSets {

    @JvmField
    val KEYWORDS = TokenSet.create(
        KageTokenTypes.PACKAGE, KageTokenTypes.FUNC, KageTokenTypes.VAR,
        KageTokenTypes.CONST, KageTokenTypes.TYPE, KageTokenTypes.RETURN,
        KageTokenTypes.IF, KageTokenTypes.ELSE, KageTokenTypes.FOR,
        KageTokenTypes.BREAK, KageTokenTypes.CONTINUE, KageTokenTypes.DISCARD,
        KageTokenTypes.STRUCT, KageTokenTypes.TRUE, KageTokenTypes.FALSE,
    )

    @JvmField
    val COMMENTS = TokenSet.create(
        KageTokenTypes.LINE_COMMENT, KageTokenTypes.BLOCK_COMMENT, KageTokenTypes.DIRECTIVE,
    )

    @JvmField
    val IDENTIFIERS = TokenSet.create(KageTokenTypes.IDENTIFIER)
}
