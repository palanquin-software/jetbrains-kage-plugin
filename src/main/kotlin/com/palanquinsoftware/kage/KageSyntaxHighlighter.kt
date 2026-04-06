package com.palanquinsoftware.kage

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.palanquinsoftware.kage.psi.KageTokenSets
import com.palanquinsoftware.kage.psi.KageTokenTypes

class KageSyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        val KEYWORD = createTextAttributesKey("KAGE_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val TYPE_NAME = createTextAttributesKey("KAGE_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME)
        val BUILTIN_FUNC = createTextAttributesKey("KAGE_BUILTIN", DefaultLanguageHighlighterColors.STATIC_METHOD)
        val IMAGE_FUNC = createTextAttributesKey("KAGE_IMAGE_FUNC", DefaultLanguageHighlighterColors.STATIC_METHOD)
        val ENTRY_POINT = createTextAttributesKey("KAGE_ENTRY_POINT", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
        val NUMBER = createTextAttributesKey("KAGE_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val LINE_COMMENT = createTextAttributesKey("KAGE_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val BLOCK_COMMENT = createTextAttributesKey("KAGE_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
        val DIRECTIVE = createTextAttributesKey("KAGE_DIRECTIVE", DefaultLanguageHighlighterColors.METADATA)
        val IDENTIFIER = createTextAttributesKey("KAGE_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        val BRACES = createTextAttributesKey("KAGE_BRACES", DefaultLanguageHighlighterColors.BRACES)
        val BRACKETS = createTextAttributesKey("KAGE_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
        val PARENS = createTextAttributesKey("KAGE_PARENS", DefaultLanguageHighlighterColors.PARENTHESES)
        val OPERATOR = createTextAttributesKey("KAGE_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val COMMA = createTextAttributesKey("KAGE_COMMA", DefaultLanguageHighlighterColors.COMMA)
        val SEMICOLON = createTextAttributesKey("KAGE_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
        val DOT = createTextAttributesKey("KAGE_DOT", DefaultLanguageHighlighterColors.DOT)
        val REJECTED = createTextAttributesKey("KAGE_REJECTED", HighlighterColors.BAD_CHARACTER)
        val BAD_CHAR = createTextAttributesKey("KAGE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val OPERATORS = setOf(
            KageTokenTypes.PLUS, KageTokenTypes.MINUS, KageTokenTypes.STAR,
            KageTokenTypes.SLASH, KageTokenTypes.PERCENT, KageTokenTypes.AMPERSAND,
            KageTokenTypes.PIPE, KageTokenTypes.CARET, KageTokenTypes.LSHIFT,
            KageTokenTypes.RSHIFT, KageTokenTypes.AND_NOT, KageTokenTypes.LAND,
            KageTokenTypes.LOR, KageTokenTypes.INC, KageTokenTypes.DEC,
            KageTokenTypes.EQ, KageTokenTypes.NEQ, KageTokenTypes.LT,
            KageTokenTypes.GT, KageTokenTypes.LTE, KageTokenTypes.GTE,
            KageTokenTypes.ASSIGN, KageTokenTypes.SHORT_ASSIGN, KageTokenTypes.NOT,
            KageTokenTypes.PLUS_ASSIGN, KageTokenTypes.MINUS_ASSIGN,
            KageTokenTypes.STAR_ASSIGN, KageTokenTypes.SLASH_ASSIGN,
            KageTokenTypes.PERCENT_ASSIGN, KageTokenTypes.AMP_ASSIGN,
            KageTokenTypes.PIPE_ASSIGN, KageTokenTypes.CARET_ASSIGN,
            KageTokenTypes.LSHIFT_ASSIGN, KageTokenTypes.RSHIFT_ASSIGN,
            KageTokenTypes.AND_NOT_ASSIGN, KageTokenTypes.COLON,
        )
    }

    override fun getHighlightingLexer(): Lexer = KageLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = when {
        KageTokenSets.KEYWORDS.contains(tokenType) -> pack(KEYWORD)
        tokenType == KageTokenTypes.TYPE_NAME       -> pack(TYPE_NAME)
        tokenType == KageTokenTypes.BUILTIN_FUNC    -> pack(BUILTIN_FUNC)
        tokenType == KageTokenTypes.IMAGE_FUNC      -> pack(IMAGE_FUNC)
        tokenType == KageTokenTypes.ENTRY_POINT     -> pack(ENTRY_POINT)
        tokenType == KageTokenTypes.INT_LITERAL ||
        tokenType == KageTokenTypes.FLOAT_LITERAL   -> pack(NUMBER)
        tokenType == KageTokenTypes.LINE_COMMENT    -> pack(LINE_COMMENT)
        tokenType == KageTokenTypes.BLOCK_COMMENT   -> pack(BLOCK_COMMENT)
        tokenType == KageTokenTypes.DIRECTIVE       -> pack(DIRECTIVE)
        tokenType == KageTokenTypes.IDENTIFIER      -> pack(IDENTIFIER)
        tokenType == KageTokenTypes.LBRACE ||
        tokenType == KageTokenTypes.RBRACE          -> pack(BRACES)
        tokenType == KageTokenTypes.LBRACKET ||
        tokenType == KageTokenTypes.RBRACKET        -> pack(BRACKETS)
        tokenType == KageTokenTypes.LPAREN ||
        tokenType == KageTokenTypes.RPAREN          -> pack(PARENS)
        tokenType == KageTokenTypes.COMMA           -> pack(COMMA)
        tokenType == KageTokenTypes.SEMICOLON       -> pack(SEMICOLON)
        tokenType == KageTokenTypes.DOT             -> pack(DOT)
        tokenType == KageTokenTypes.REJECTED_KEYWORD -> pack(REJECTED)
        tokenType == TokenType.BAD_CHARACTER        -> pack(BAD_CHAR)
        tokenType in OPERATORS                      -> pack(OPERATOR)
        else                                        -> TextAttributesKey.EMPTY_ARRAY
    }
}
