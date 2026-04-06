package com.palanquinsoftware.kage;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.palanquinsoftware.kage.psi.KageTokenTypes;

%%

%class KageLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

%eof{  return;
%eof}

// ── Macros ──────────────────────────────────────────────────────────

WHITE_SPACE     = [ \t\f]+
NEWLINE         = \r\n | \r | \n
LINE_COMMENT    = "//"[^\r\n]*
BLOCK_COMMENT   = "/*" [^*]* \*+ ( [^/*] [^*]* \*+ )* "/"
DIRECTIVE       = "//kage:" [^\r\n]*

DECIMAL_LIT     = [1-9][0-9_]* | "0"
HEX_LIT         = "0" [xX] [0-9a-fA-F_]+
OCTAL_LIT       = "0" [oO] [0-7_]+
LEGACY_OCTAL    = "0" [0-7]+
BINARY_LIT      = "0" [bB] [01_]+
INT_LITERAL     = {HEX_LIT} | {BINARY_LIT} | {OCTAL_LIT} | {LEGACY_OCTAL} | {DECIMAL_LIT}

DECIMALS        = [0-9] [0-9_]*
EXPONENT        = [eE] [+-]? {DECIMALS}
HEX_MANTISSA    = "0" [xX] ( [0-9a-fA-F_]* "." [0-9a-fA-F_]+ | [0-9a-fA-F_]+ "."? )
HEX_EXPONENT    = [pP] [+-]? {DECIMALS}
FLOAT_LITERAL   = {DECIMALS} "." {DECIMALS}? {EXPONENT}?
                | {DECIMALS} {EXPONENT}
                | "." {DECIMALS} {EXPONENT}?
                | {HEX_MANTISSA} {HEX_EXPONENT}

IDENTIFIER      = [a-zA-Z_][a-zA-Z0-9_]*

%%

// ── Rules ───────────────────────────────────────────────────────────

<YYINITIAL> {

  // Whitespace
  {WHITE_SPACE}                     { return TokenType.WHITE_SPACE; }
  {NEWLINE}                         { return TokenType.WHITE_SPACE; }

  // Comments — directive before line comment (both start with //)
  {DIRECTIVE}                       { return KageTokenTypes.DIRECTIVE; }
  {BLOCK_COMMENT}                   { return KageTokenTypes.BLOCK_COMMENT; }
  {LINE_COMMENT}                    { return KageTokenTypes.LINE_COMMENT; }

  // Keywords
  "package"                         { return KageTokenTypes.PACKAGE; }
  "func"                            { return KageTokenTypes.FUNC; }
  "var"                             { return KageTokenTypes.VAR; }
  "const"                           { return KageTokenTypes.CONST; }
  "type"                            { return KageTokenTypes.TYPE; }
  "return"                          { return KageTokenTypes.RETURN; }
  "if"                              { return KageTokenTypes.IF; }
  "else"                            { return KageTokenTypes.ELSE; }
  "for"                             { return KageTokenTypes.FOR; }
  "break"                           { return KageTokenTypes.BREAK; }
  "continue"                        { return KageTokenTypes.CONTINUE; }
  "discard"                         { return KageTokenTypes.DISCARD; }
  "struct"                          { return KageTokenTypes.STRUCT; }
  "true"                            { return KageTokenTypes.TRUE; }
  "false"                           { return KageTokenTypes.FALSE; }

  // Rejected Go keywords
  "chan"                             { return KageTokenTypes.REJECTED_KEYWORD; }
  "defer"                           { return KageTokenTypes.REJECTED_KEYWORD; }
  "go"                              { return KageTokenTypes.REJECTED_KEYWORD; }
  "goto"                            { return KageTokenTypes.REJECTED_KEYWORD; }
  "import"                          { return KageTokenTypes.REJECTED_KEYWORD; }
  "interface"                       { return KageTokenTypes.REJECTED_KEYWORD; }
  "map"                             { return KageTokenTypes.REJECTED_KEYWORD; }
  "range"                           { return KageTokenTypes.REJECTED_KEYWORD; }
  "select"                          { return KageTokenTypes.REJECTED_KEYWORD; }
  "switch"                          { return KageTokenTypes.REJECTED_KEYWORD; }
  "case"                            { return KageTokenTypes.REJECTED_KEYWORD; }
  "default"                         { return KageTokenTypes.REJECTED_KEYWORD; }
  "fallthrough"                     { return KageTokenTypes.REJECTED_KEYWORD; }

  // Types (also constructors)
  "bool"                            { return KageTokenTypes.TYPE_NAME; }
  "int"                             { return KageTokenTypes.TYPE_NAME; }
  "float"                           { return KageTokenTypes.TYPE_NAME; }
  "vec2"                            { return KageTokenTypes.TYPE_NAME; }
  "vec3"                            { return KageTokenTypes.TYPE_NAME; }
  "vec4"                            { return KageTokenTypes.TYPE_NAME; }
  "ivec2"                           { return KageTokenTypes.TYPE_NAME; }
  "ivec3"                           { return KageTokenTypes.TYPE_NAME; }
  "ivec4"                           { return KageTokenTypes.TYPE_NAME; }
  "mat2"                            { return KageTokenTypes.TYPE_NAME; }
  "mat3"                            { return KageTokenTypes.TYPE_NAME; }
  "mat4"                            { return KageTokenTypes.TYPE_NAME; }

  // Entry points
  "Fragment"                        { return KageTokenTypes.ENTRY_POINT; }
  "Vertex"                          { return KageTokenTypes.ENTRY_POINT; }

  // Image functions
  "imageSrc0At"                     { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc1At"                     { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc2At"                     { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc3At"                     { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc0UnsafeAt"               { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc1UnsafeAt"               { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc2UnsafeAt"               { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc3UnsafeAt"               { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc0Origin"                 { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc1Origin"                 { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc2Origin"                 { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc3Origin"                 { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc0Size"                   { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc1Size"                   { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc2Size"                   { return KageTokenTypes.IMAGE_FUNC; }
  "imageSrc3Size"                   { return KageTokenTypes.IMAGE_FUNC; }
  "imageDstOrigin"                  { return KageTokenTypes.IMAGE_FUNC; }
  "imageDstSize"                    { return KageTokenTypes.IMAGE_FUNC; }

  // Builtin functions — math
  "sin"                             { return KageTokenTypes.BUILTIN_FUNC; }
  "cos"                             { return KageTokenTypes.BUILTIN_FUNC; }
  "tan"                             { return KageTokenTypes.BUILTIN_FUNC; }
  "asin"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "acos"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "atan"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "atan2"                           { return KageTokenTypes.BUILTIN_FUNC; }
  "sinh"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "cosh"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "tanh"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "radians"                         { return KageTokenTypes.BUILTIN_FUNC; }
  "degrees"                         { return KageTokenTypes.BUILTIN_FUNC; }
  "pow"                             { return KageTokenTypes.BUILTIN_FUNC; }
  "exp"                             { return KageTokenTypes.BUILTIN_FUNC; }
  "log"                             { return KageTokenTypes.BUILTIN_FUNC; }
  "exp2"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "log2"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "sqrt"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "inversesqrt"                     { return KageTokenTypes.BUILTIN_FUNC; }
  "abs"                             { return KageTokenTypes.BUILTIN_FUNC; }
  "sign"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "floor"                           { return KageTokenTypes.BUILTIN_FUNC; }
  "ceil"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "fract"                           { return KageTokenTypes.BUILTIN_FUNC; }
  "mod"                             { return KageTokenTypes.BUILTIN_FUNC; }
  "min"                             { return KageTokenTypes.BUILTIN_FUNC; }
  "max"                             { return KageTokenTypes.BUILTIN_FUNC; }
  "clamp"                           { return KageTokenTypes.BUILTIN_FUNC; }
  "mix"                             { return KageTokenTypes.BUILTIN_FUNC; }
  "step"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "smoothstep"                      { return KageTokenTypes.BUILTIN_FUNC; }
  "length"                          { return KageTokenTypes.BUILTIN_FUNC; }
  "distance"                        { return KageTokenTypes.BUILTIN_FUNC; }
  "dot"                             { return KageTokenTypes.BUILTIN_FUNC; }
  "cross"                           { return KageTokenTypes.BUILTIN_FUNC; }
  "normalize"                       { return KageTokenTypes.BUILTIN_FUNC; }
  "faceforward"                     { return KageTokenTypes.BUILTIN_FUNC; }
  "reflect"                         { return KageTokenTypes.BUILTIN_FUNC; }
  "refract"                         { return KageTokenTypes.BUILTIN_FUNC; }
  "transpose"                       { return KageTokenTypes.BUILTIN_FUNC; }
  "dfdx"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "dfdy"                            { return KageTokenTypes.BUILTIN_FUNC; }
  "fwidth"                          { return KageTokenTypes.BUILTIN_FUNC; }
  "frontfacing"                     { return KageTokenTypes.BUILTIN_FUNC; }
  "len"                             { return KageTokenTypes.BUILTIN_FUNC; }
  "cap"                             { return KageTokenTypes.BUILTIN_FUNC; }

  // Literals — float before int (float patterns are more specific)
  {FLOAT_LITERAL}                   { return KageTokenTypes.FLOAT_LITERAL; }
  {INT_LITERAL}                     { return KageTokenTypes.INT_LITERAL; }

  // Identifiers (catch-all for names)
  {IDENTIFIER}                      { return KageTokenTypes.IDENTIFIER; }

  // Multi-char operators (longest match first)
  "&&"                              { return KageTokenTypes.LAND; }
  "||"                              { return KageTokenTypes.LOR; }
  "&^="                             { return KageTokenTypes.AND_NOT_ASSIGN; }
  "<<="                             { return KageTokenTypes.LSHIFT_ASSIGN; }
  ">>="                             { return KageTokenTypes.RSHIFT_ASSIGN; }
  "&^"                              { return KageTokenTypes.AND_NOT; }
  "<<"                              { return KageTokenTypes.LSHIFT; }
  ">>"                              { return KageTokenTypes.RSHIFT; }
  "+="                              { return KageTokenTypes.PLUS_ASSIGN; }
  "-="                              { return KageTokenTypes.MINUS_ASSIGN; }
  "*="                              { return KageTokenTypes.STAR_ASSIGN; }
  "/="                              { return KageTokenTypes.SLASH_ASSIGN; }
  "%="                              { return KageTokenTypes.PERCENT_ASSIGN; }
  "&="                              { return KageTokenTypes.AMP_ASSIGN; }
  "|="                              { return KageTokenTypes.PIPE_ASSIGN; }
  "^="                              { return KageTokenTypes.CARET_ASSIGN; }
  ":="                              { return KageTokenTypes.SHORT_ASSIGN; }
  "++"                              { return KageTokenTypes.INC; }
  "--"                              { return KageTokenTypes.DEC; }
  "=="                              { return KageTokenTypes.EQ; }
  "!="                              { return KageTokenTypes.NEQ; }
  "<="                              { return KageTokenTypes.LTE; }
  ">="                              { return KageTokenTypes.GTE; }

  // Single-char operators
  "+"                               { return KageTokenTypes.PLUS; }
  "-"                               { return KageTokenTypes.MINUS; }
  "*"                               { return KageTokenTypes.STAR; }
  "/"                               { return KageTokenTypes.SLASH; }
  "%"                               { return KageTokenTypes.PERCENT; }
  "&"                               { return KageTokenTypes.AMPERSAND; }
  "|"                               { return KageTokenTypes.PIPE; }
  "^"                               { return KageTokenTypes.CARET; }
  "<"                               { return KageTokenTypes.LT; }
  ">"                               { return KageTokenTypes.GT; }
  "="                               { return KageTokenTypes.ASSIGN; }
  "!"                               { return KageTokenTypes.NOT; }

  // Delimiters
  "("                               { return KageTokenTypes.LPAREN; }
  ")"                               { return KageTokenTypes.RPAREN; }
  "{"                               { return KageTokenTypes.LBRACE; }
  "}"                               { return KageTokenTypes.RBRACE; }
  "["                               { return KageTokenTypes.LBRACKET; }
  "]"                               { return KageTokenTypes.RBRACKET; }
  ","                               { return KageTokenTypes.COMMA; }
  "."                               { return KageTokenTypes.DOT; }
  ";"                               { return KageTokenTypes.SEMICOLON; }
  ":"                               { return KageTokenTypes.COLON; }

  // Catch-all
  [^]                               { return TokenType.BAD_CHARACTER; }
}
