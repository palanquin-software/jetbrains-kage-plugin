package com.palanquinsoftware.kage.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import com.palanquinsoftware.kage.KageFileType
import com.palanquinsoftware.kage.KageLanguage

class KageFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, KageLanguage.INSTANCE) {
    override fun getFileType() = KageFileType.INSTANCE
    override fun toString() = "Kage File"
}
