package com.floater.git

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KGit2Test {
    @Test
    fun version() {
        val version = KGit2.version()
        assertEquals(version.toString(), "1.5.0")
    }

    @Test
    fun preRelease() {
        assertNull(KGit2.preRelease())
    }

    @Test
    fun features() {
        assertEquals(KGit2.features(), 15)
        assert(KGit2.enableHttpsFeature())
        assert(KGit2.enableSSHFeature())
        assert(KGit2.enableNSecFeature())
        assert(KGit2.enableThreadsFeature())
    }
}
