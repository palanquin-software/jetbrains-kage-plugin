# Kage Language Plugin Implementation Plan

**Goal:** Build a JetBrains plugin providing syntax highlighting, brace matching, commenting, and autocompletion for the Kage shader language (`.kage` files).

**Architecture:** JFlex lexer tokenizes all Kage grammar elements. A flat ParserDefinition wraps lexer output in a single file node (no AST). SyntaxHighlighter maps token types to editor colors. CompletionContributor offers keyword/builtin/type/image-function completions with type signatures.

**Tech Stack:** Kotlin 2.x, JFlex, IntelliJ Platform Gradle Plugin 2.x (`org.jetbrains.intellij.platform`), JDK 21 (Temurin), Gradle 8.14.4, target IntelliJ 2024.2+ (`com.intellij.modules.platform`).

---

## Task 1: Gradle Project Scaffolding

**Files:**
- Create: `settings.gradle.kts`
- Create: `gradle.properties`
- Create: `gradle/libs.versions.toml`
- Create: `build.gradle.kts`

**Step 1: Create `settings.gradle.kts`**

```kotlin
rootProject.name = "kage"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
```

**Step 2: Create `gradle/libs.versions.toml`**

```toml
[versions]
intelliJPlatform = "2.13.1"
kotlin = "2.1.20"

[plugins]
intelliJPlatform = { id = "org.jetbrains.intellij.platform", version.ref = "intelliJPlatform" }
kotlin = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
```

**Step 3: Create `gradle.properties`**

```properties
pluginGroup = com.palanquinsoftware
pluginName = Kage
pluginVersion = 0.1.0
pluginSinceBuild = 242
platformVersion = 2024.2
platformBundledPlugins =
platformBundledModules = com.intellij.modules.platform
kotlin.stdlib.default.dependency = false
org.gradle.configuration-cache = true
org.gradle.caching = true
```

**Step 4: Create `build.gradle.kts`**

```kotlin
import org.jetbrains.intellij.platform.gradle.tasks.GenerateLexerTask

plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intelliJPlatform)
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(providers.gradleProperty("platformVersion"))

        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map {
            it.split(',').filter(String::isNotBlank)
        })
        bundledModules(providers.gradleProperty("platformBundledModules").map {
            it.split(',').filter(String::isNotBlank)
        })
    }
}

intellijPlatform {
    pluginConfiguration {
        id = "com.palanquinsoftware.kage"
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")

        vendor {
            name = "Palanquin Software"
            url = "https://palanquinsoftware.com"
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
        }
    }
}

val generateKageLexer by tasks.registering(GenerateLexerTask::class) {
    sourceFile = layout.projectDirectory.file("src/main/flex/Kage.flex")
    targetOutputDir = layout.buildDirectory.dir("generated/sources/flex/com/palanquinsoftware/kage")
    purgeOldFiles = true
}

tasks.named("compileKotlin") {
    dependsOn(generateKageLexer)
}

sourceSets.main {
    java.srcDir(layout.buildDirectory.dir("generated/sources/flex"))
}
```

**Step 5: Install Gradle wrapper**

Run: `cd /Users/pandich/go/src/github.com/palanquin-software/jetbrains-kage-plugin && gradle wrapper`
Expected: `gradle/wrapper/` directory created with `gradle-wrapper.jar` and `gradle-wrapper.properties`.

**Step 6: Verify Gradle resolves**

Run: `mise run build` (will fail — no source yet — but Gradle should resolve dependencies)
Expected: Fails with compilation error (no sources), NOT with dependency resolution errors.

**Step 7: Commit**

```
feat: scaffold Gradle project with IntelliJ Platform Plugin 2.x
```

---

## Task 2: Plugin Icon

**Files:**
- Create: `src/main/resources/icons/kage.svg`

**Step 1: Create a 16x16 SVG icon**

A simple diamond/crystal shape representing a shader:

```svg
<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
  <defs>
    <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#7C4DFF"/>
      <stop offset="100%" stop-color="#448AFF"/>
    </linearGradient>
  </defs>
  <path d="M8 1L14 6L8 15L2 6Z" fill="url(#g)" stroke="#5C6BC0" stroke-width="0.5"/>
  <path d="M8 1L5 6H11Z" fill="#B388FF" opacity="0.5"/>
</svg>
```

**Step 2: Commit**

```
feat: add Kage file type icon
```

---

## Task 3: Language + FileType + Icons

**Files:**
- Create: `src/main/kotlin/com/palanquinsoftware/kage/KageLanguage.kt`
- Create: `src/main/kotlin/com/palanquinsoftware/kage/KageFileType.kt`
- Create: `src/main/kotlin/com/palanquinsoftware/kage/KageIcons.kt`

**Step 1: Create `KageLanguage.kt`**

```kotlin
package com.palanquinsoftware.kage

import com.intellij.lang.Language

class KageLanguage private constructor() : Language("Kage") {
    companion object {
        @JvmField
        val INSTANCE = KageLanguage()
    }
}
```

Note: `class` not `object` — required for dynamic plugin compatibility.

**Step 2: Create `KageIcons.kt`**

```kotlin
package com.palanquinsoftware.kage

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object KageIcons {
    @JvmField
    val FILE: Icon = IconLoader.getIcon("/icons/kage.svg", KageIcons::class.java)
}
```

**Step 3: Create `KageFileType.kt`**

```kotlin
package com.palanquinsoftware.kage

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class KageFileType private constructor() : LanguageFileType(KageLanguage.INSTANCE) {

    override fun getName(): String = "Kage"
    override fun getDescription(): String = "Kage shader language"
    override fun getDefaultExtension(): String = "kage"
    override fun getIcon(): Icon = KageIcons.FILE

    companion object {
        @JvmField
        val INSTANCE = KageFileType()
    }
}
```

**Step 4: Commit**

```
feat: add KageLanguage, KageFileType, and KageIcons
```

---

## Task 4: Token Types and Token Sets

**Files:**
- Create: `src/main/kotlin/com/palanquinsoftware/kage/psi/KageElementType.kt`
- Create: `src/main/kotlin/com/palanquinsoftware/kage/psi/KageTokenTypes.kt`
- Create: `src/main/kotlin/com/palanquinsoftware/kage/psi/KageTokenSets.kt`

**Step 1: Create `KageElementType.kt`**

```kotlin
package com.palanquinsoftware.kage.psi

import com.intellij.psi.tree.IElementType
import com.palanquinsoftware.kage.KageLanguage

class KageElementType(debugName: String) : IElementType(debugName, KageLanguage.INSTANCE)
```

**Step 2: Create `KageTokenTypes.kt`**

```kotlin
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
```

**Step 3: Create `KageTokenSets.kt`**

```kotlin
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
```

**Step 4: Commit**

```
feat: define Kage token types and token sets
```

---

## Task 5: JFlex Lexer Specification

**Files:**
- Create: `src/main/flex/Kage.flex`

**Step 1: Create `Kage.flex`**

```flex
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
  "<<"                              { return KageTokenTypes.LSHIFT; }
  ">>"                              { return KageTokenTypes.RSHIFT; }
  "&^="                             { return KageTokenTypes.AND_NOT_ASSIGN; }
  "&^"                              { return KageTokenTypes.AND_NOT; }
  "<<="                             { return KageTokenTypes.LSHIFT_ASSIGN; }
  ">>="                             { return KageTokenTypes.RSHIFT_ASSIGN; }
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
```

**Step 2: Generate the lexer**

Run: `mise run lex`
Expected: `build/generated/sources/flex/com/palanquinsoftware/kage/KageLexer.java` is generated.

**Step 3: Commit**

```
feat: add JFlex lexer specification for Kage grammar
```

---

## Task 6: Lexer Adapter

**Files:**
- Create: `src/main/kotlin/com/palanquinsoftware/kage/KageLexerAdapter.kt`

**Step 1: Create `KageLexerAdapter.kt`**

```kotlin
package com.palanquinsoftware.kage

import com.intellij.lexer.FlexAdapter

class KageLexerAdapter : FlexAdapter(KageLexer(null))
```

**Step 2: Commit**

```
feat: add KageLexerAdapter
```

---

## Task 7: PSI File

**Files:**
- Create: `src/main/kotlin/com/palanquinsoftware/kage/psi/KageFile.kt`

**Step 1: Create `KageFile.kt`**

```kotlin
package com.palanquinsoftware.kage.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import com.palanquinsoftware.kage.KageFileType
import com.palanquinsoftware.kage.KageLanguage

class KageFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, KageLanguage.INSTANCE) {
    override fun getFileType() = KageFileType.INSTANCE
    override fun toString() = "Kage File"
}
```

**Step 2: Commit**

```
feat: add KageFile PSI class
```

---

## Task 8: Parser Definition (flat, no AST)

**Files:**
- Create: `src/main/kotlin/com/palanquinsoftware/kage/KageParserDefinition.kt`

**Step 1: Create `KageParserDefinition.kt`**

```kotlin
package com.palanquinsoftware.kage

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.palanquinsoftware.kage.psi.KageFile
import com.palanquinsoftware.kage.psi.KageTokenSets

class KageParserDefinition : ParserDefinition {

    companion object {
        @JvmField
        val FILE = IFileElementType(KageLanguage.INSTANCE)
    }

    override fun createLexer(project: Project): Lexer = KageLexerAdapter()

    override fun createParser(project: Project): PsiParser = PsiParser { root, builder ->
        val marker = builder.mark()
        while (!builder.eof()) builder.advanceLexer()
        marker.done(root)
        builder.treeBuilt
    }

    override fun getFileNodeType(): IFileElementType = FILE
    override fun getCommentTokens(): TokenSet = KageTokenSets.COMMENTS
    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement =
        throw UnsupportedOperationException("No composite elements in flat parser")

    override fun createFile(viewProvider: FileViewProvider): PsiFile = KageFile(viewProvider)
}
```

**Step 2: Commit**

```
feat: add flat KageParserDefinition
```

---

## Task 9: Syntax Highlighter

**Files:**
- Create: `src/main/kotlin/com/palanquinsoftware/kage/KageSyntaxHighlighter.kt`
- Create: `src/main/kotlin/com/palanquinsoftware/kage/KageSyntaxHighlighterFactory.kt`

**Step 1: Create `KageSyntaxHighlighter.kt`**

```kotlin
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
        isOperator(tokenType)                       -> pack(OPERATOR)
        else                                        -> TextAttributesKey.EMPTY_ARRAY
    }

    private fun isOperator(tokenType: IElementType): Boolean = tokenType in setOf(
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
```

**Step 2: Create `KageSyntaxHighlighterFactory.kt`**

```kotlin
package com.palanquinsoftware.kage

import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class KageSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) =
        KageSyntaxHighlighter()
}
```

**Step 3: Commit**

```
feat: add syntax highlighter with token-to-color mapping
```

---

## Task 10: Brace Matcher + Commenter

**Files:**
- Create: `src/main/kotlin/com/palanquinsoftware/kage/KageBraceMatcher.kt`
- Create: `src/main/kotlin/com/palanquinsoftware/kage/KageCommenter.kt`

**Step 1: Create `KageBraceMatcher.kt`**

```kotlin
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
```

**Step 2: Create `KageCommenter.kt`**

```kotlin
package com.palanquinsoftware.kage

import com.intellij.lang.Commenter

class KageCommenter : Commenter {
    override fun getLineCommentPrefix() = "//"
    override fun getBlockCommentPrefix() = "/*"
    override fun getBlockCommentSuffix() = "*/"
    override fun getCommentedBlockCommentPrefix(): String? = null
    override fun getCommentedBlockCommentSuffix(): String? = null
}
```

**Step 3: Commit**

```
feat: add brace matcher and commenter
```

---

## Task 11: Completion Contributor

**Files:**
- Create: `src/main/kotlin/com/palanquinsoftware/kage/KageCompletionContributor.kt`

**Step 1: Create `KageCompletionContributor.kt`**

```kotlin
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
                    // Keywords
                    KEYWORDS.forEach { kw ->
                        result.addElement(LookupElementBuilder.create(kw).bold())
                    }
                    // Types
                    TYPES.forEach { t ->
                        result.addElement(
                            LookupElementBuilder.create(t)
                                .bold()
                                .withTypeText("type", true)
                        )
                    }
                    // Builtin functions
                    BUILTINS.forEach { (name, sig) ->
                        result.addElement(
                            LookupElementBuilder.create(name)
                                .withTailText(sig, true)
                                .withTypeText("builtin", true)
                        )
                    }
                    // Image functions
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
            // Trig
            "sin" to "(x) float", "cos" to "(x) float", "tan" to "(x) float",
            "asin" to "(x) float", "acos" to "(x) float",
            "atan" to "(y_over_x) float", "atan2" to "(y, x) float",
            "sinh" to "(x) float", "cosh" to "(x) float", "tanh" to "(x) float",
            "radians" to "(degrees) float", "degrees" to "(radians) float",
            // Exponential
            "pow" to "(x, y) float", "exp" to "(x) float", "log" to "(x) float",
            "exp2" to "(x) float", "log2" to "(x) float",
            "sqrt" to "(x) float", "inversesqrt" to "(x) float",
            // Common math
            "abs" to "(x) float", "sign" to "(x) float",
            "floor" to "(x) float", "ceil" to "(x) float", "fract" to "(x) float",
            "mod" to "(x, y) float",
            "min" to "(x, y) float", "max" to "(x, y) float",
            "clamp" to "(x, minVal, maxVal) float",
            // Interpolation
            "mix" to "(x, y, a) float", "step" to "(edge, x) float",
            "smoothstep" to "(edge0, edge1, x) float",
            // Geometric
            "length" to "(x) float", "distance" to "(p0, p1) float",
            "dot" to "(x, y) float", "cross" to "(x, y) vec3",
            "normalize" to "(x) vec",
            "faceforward" to "(n, i, nref) vec",
            "reflect" to "(i, n) vec", "refract" to "(i, n, eta) vec",
            // Matrix
            "transpose" to "(m) mat",
            // Derivatives
            "dfdx" to "(p) vec", "dfdy" to "(p) vec", "fwidth" to "(p) vec",
            "frontfacing" to "() bool",
            // Array
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
```

**Step 2: Commit**

```
feat: add completion contributor with keywords, types, builtins, image functions
```

---

## Task 12: Color Settings Page

**Files:**
- Create: `src/main/kotlin/com/palanquinsoftware/kage/KageColorSettingsPage.kt`

**Step 1: Create `KageColorSettingsPage.kt`**

```kotlin
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
```

**Step 2: Commit**

```
feat: add color settings page with demo shader
```

---

## Task 13: plugin.xml

**Files:**
- Create: `src/main/resources/META-INF/plugin.xml`

**Step 1: Create `plugin.xml`**

```xml
<idea-plugin require-restart="false">

    <id>com.palanquinsoftware.kage</id>
    <name>Kage</name>

    <vendor url="https://palanquinsoftware.com">Palanquin Software</vendor>

    <description><![CDATA[
        Language support for Kage, the shader language used by Ebitengine.

        Features:
        <ul>
            <li>Syntax highlighting for keywords, types, builtins, operators, and literals</li>
            <li>Brace matching and auto-pairing</li>
            <li>Line and block commenting (Cmd+/ / Ctrl+/)</li>
            <li>Autocompletion for keywords, types, builtin functions, and image functions</li>
            <li>Compiler directive highlighting (//kage:unit)</li>
            <li>Color settings page (Settings → Editor → Color Scheme → Kage)</li>
        </ul>
    ]]></description>

    <depends>com.intellij.modules.platform</depends>

    <extensions defaultExtensionNs="com.intellij">

        <fileType
            name="Kage"
            implementationClass="com.palanquinsoftware.kage.KageFileType"
            fieldName="INSTANCE"
            language="Kage"
            extensions="kage"/>

        <lang.parserDefinition
            language="Kage"
            implementationClass="com.palanquinsoftware.kage.KageParserDefinition"/>

        <lang.syntaxHighlighterFactory
            language="Kage"
            implementationClass="com.palanquinsoftware.kage.KageSyntaxHighlighterFactory"/>

        <lang.braceMatcher
            language="Kage"
            implementationClass="com.palanquinsoftware.kage.KageBraceMatcher"/>

        <lang.commenter
            language="Kage"
            implementationClass="com.palanquinsoftware.kage.KageCommenter"/>

        <completion.contributor
            language="Kage"
            implementationClass="com.palanquinsoftware.kage.KageCompletionContributor"/>

        <colorSettingsPage
            implementation="com.palanquinsoftware.kage.KageColorSettingsPage"/>

    </extensions>

</idea-plugin>
```

**Step 2: Commit**

```
feat: add plugin.xml with all extension registrations
```

---

## Task 14: Build and Verify

**Step 1: Generate lexer**

Run: `mise run lex`
Expected: KageLexer.java generated successfully.

**Step 2: Build the plugin**

Run: `mise run build`
Expected: BUILD SUCCESSFUL. Plugin JAR created in `build/distributions/`.

**Step 3: Verify the plugin**

Run: `mise run verify`
Expected: No compatibility issues reported.

**Step 4: Launch sandbox IDE**

Run: `mise run run`
Expected: IDE launches. Create a `.kage` file — syntax highlighting, brace matching, commenting, and completion should all work.

**Step 5: Commit (if any fixes were needed)**

```
fix: resolve build/verification issues
```

---

## Task 15: Sample Kage File

**Files:**
- Create: `samples/hello.kage`

**Step 1: Create a sample file for testing**

```
//kage:unit pixels

package main

func Fragment(dstPos vec4, srcPos vec2, color vec4) vec4 {
	clr := imageSrc0At(srcPos)

	// Invert colors
	inverted := vec4(1.0 - clr.x, 1.0 - clr.y, 1.0 - clr.z, clr.w)

	// Mix based on horizontal position
	origin := imageDstOrigin()
	size := imageDstSize()
	t := (dstPos.x - origin.x) / size.x

	return mix(clr, inverted, smoothstep(0.4, 0.6, t))
}
```

**Step 2: Commit**

```
feat: add sample Kage shader for testing
```