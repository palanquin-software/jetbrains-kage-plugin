package com.palanquinsoftware.kage

import com.intellij.lang.Language

class KageLanguage private constructor() : Language("Kage") {
    companion object {
        @JvmField
        val INSTANCE = KageLanguage()
    }
}
