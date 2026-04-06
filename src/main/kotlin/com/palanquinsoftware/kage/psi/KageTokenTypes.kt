package com.palanquinsoftware.kage.psi

object KageTokenTypes {

    // Comments
    @JvmField val LINE_COMMENT = KageElementType("LINE_COMMENT")
    @JvmField val BLOCK_COMMENT = KageElementType("BLOCK_COMMENT")
    @JvmField val DIRECTIVE = KageElementType("DIRECTIVE")

    // Literals
    @JvmField val INT_LITERAL = KageElementType("INT_LITERAL")
    @JvmField val FLOAT_LITERAL = KageElementType("FLOAT_LITERAL")

    // Keywords
    @JvmField val PACKAGE = KageElementType("PACKAGE")
    @JvmField val FUNC = KageElementType("FUNC")
    @JvmField val VAR = KageElementType("VAR")
    @JvmField val CONST = KageElementType("CONST")
    @JvmField val TYPE = KageElementType("TYPE")
    @JvmField val RETURN = KageElementType("RETURN")
    @JvmField val IF = KageElementType("IF")
    @JvmField val ELSE = KageElementType("ELSE")
    @JvmField val FOR = KageElementType("FOR")
    @JvmField val BREAK = KageElementType("BREAK")
    @JvmField val CONTINUE = KageElementType("CONTINUE")
    @JvmField val DISCARD = KageElementType("DISCARD")
    @JvmField val STRUCT = KageElementType("STRUCT")
    @JvmField val TRUE = KageElementType("TRUE")
    @JvmField val FALSE = KageElementType("FALSE")

    // Rejected Go keywords (highlighted as errors)
    @JvmField val REJECTED_KEYWORD = KageElementType("REJECTED_KEYWORD")

    // Types (also serve as constructor calls)
    @JvmField val TYPE_NAME = KageElementType("TYPE_NAME")

    // Builtin functions
    @JvmField val BUILTIN_FUNC = KageElementType("BUILTIN_FUNC")

    // Image functions
    @JvmField val IMAGE_FUNC = KageElementType("IMAGE_FUNC")

    // Entry points
    @JvmField val ENTRY_POINT = KageElementType("ENTRY_POINT")

    // Identifiers
    @JvmField val IDENTIFIER = KageElementType("IDENTIFIER")

    // Operators
    @JvmField val PLUS = KageElementType("PLUS")
    @JvmField val MINUS = KageElementType("MINUS")
    @JvmField val STAR = KageElementType("STAR")
    @JvmField val SLASH = KageElementType("SLASH")
    @JvmField val PERCENT = KageElementType("PERCENT")
    @JvmField val AMPERSAND = KageElementType("AMPERSAND")
    @JvmField val PIPE = KageElementType("PIPE")
    @JvmField val CARET = KageElementType("CARET")
    @JvmField val LSHIFT = KageElementType("LSHIFT")
    @JvmField val RSHIFT = KageElementType("RSHIFT")
    @JvmField val AND_NOT = KageElementType("AND_NOT")
    @JvmField val PLUS_ASSIGN = KageElementType("PLUS_ASSIGN")
    @JvmField val MINUS_ASSIGN = KageElementType("MINUS_ASSIGN")
    @JvmField val STAR_ASSIGN = KageElementType("STAR_ASSIGN")
    @JvmField val SLASH_ASSIGN = KageElementType("SLASH_ASSIGN")
    @JvmField val PERCENT_ASSIGN = KageElementType("PERCENT_ASSIGN")
    @JvmField val AMP_ASSIGN = KageElementType("AMP_ASSIGN")
    @JvmField val PIPE_ASSIGN = KageElementType("PIPE_ASSIGN")
    @JvmField val CARET_ASSIGN = KageElementType("CARET_ASSIGN")
    @JvmField val LSHIFT_ASSIGN = KageElementType("LSHIFT_ASSIGN")
    @JvmField val RSHIFT_ASSIGN = KageElementType("RSHIFT_ASSIGN")
    @JvmField val AND_NOT_ASSIGN = KageElementType("AND_NOT_ASSIGN")
    @JvmField val LAND = KageElementType("LAND")
    @JvmField val LOR = KageElementType("LOR")
    @JvmField val INC = KageElementType("INC")
    @JvmField val DEC = KageElementType("DEC")
    @JvmField val EQ = KageElementType("EQ")
    @JvmField val NEQ = KageElementType("NEQ")
    @JvmField val LT = KageElementType("LT")
    @JvmField val GT = KageElementType("GT")
    @JvmField val LTE = KageElementType("LTE")
    @JvmField val GTE = KageElementType("GTE")
    @JvmField val ASSIGN = KageElementType("ASSIGN")
    @JvmField val SHORT_ASSIGN = KageElementType("SHORT_ASSIGN")
    @JvmField val NOT = KageElementType("NOT")

    // Delimiters
    @JvmField val LPAREN = KageElementType("LPAREN")
    @JvmField val RPAREN = KageElementType("RPAREN")
    @JvmField val LBRACE = KageElementType("LBRACE")
    @JvmField val RBRACE = KageElementType("RBRACE")
    @JvmField val LBRACKET = KageElementType("LBRACKET")
    @JvmField val RBRACKET = KageElementType("RBRACKET")
    @JvmField val COMMA = KageElementType("COMMA")
    @JvmField val DOT = KageElementType("DOT")
    @JvmField val SEMICOLON = KageElementType("SEMICOLON")
    @JvmField val COLON = KageElementType("COLON")
}
