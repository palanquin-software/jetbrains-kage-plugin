package com.palanquinsoftware.kage

import com.intellij.lexer.FlexAdapter

class KageLexerAdapter : FlexAdapter(KageLexer(null))
