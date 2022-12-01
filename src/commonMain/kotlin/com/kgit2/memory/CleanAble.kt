package com.kgit2.memory

import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

interface CleanAble {
    val cleaner: Cleaner
}
