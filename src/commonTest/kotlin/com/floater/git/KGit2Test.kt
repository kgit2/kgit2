package com.floater.git

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KGit2Test {
    private val kGit2: KGit2 = KGit2()

    @Test
    fun version() {
        val version = kGit2.version()
        assertEquals(version.toString(), "1.5.0")
    }

    @Test
    fun preRelease() {
        assertNull(kGit2.preRelease())
    }
}
