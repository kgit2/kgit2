package com.kgit2.memory

import kotlin.native.internal.Cleaner

interface CleanAble {
    val cleaner: Cleaner
}
