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
