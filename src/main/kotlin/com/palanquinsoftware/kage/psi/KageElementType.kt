package com.palanquinsoftware.kage.psi

import com.intellij.psi.tree.IElementType
import com.palanquinsoftware.kage.KageLanguage

class KageElementType(debugName: String) : IElementType(debugName, KageLanguage.INSTANCE)
