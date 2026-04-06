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
