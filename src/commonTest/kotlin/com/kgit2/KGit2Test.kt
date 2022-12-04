package com.kgit2

import com.kgit2.common.kgitRunTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KGit2Test {
    @Test
    fun version() = kgitRunTest {
        val version = KGit2.version()
        assertEquals(version.toString(), "1.5.0")
    }

    @Test
    fun preRelease() = kgitRunTest {
        assertNull(KGit2.preRelease())
    }

    @Test
    fun features() = kgitRunTest {
        val feature = KGit2.feature
        assertEquals(feature.flags, 15U)
        assertTrue(feature.enableThreads())
        assertTrue(feature.enableHttps())
        assertTrue(feature.enableSSH())
        assertTrue(feature.enableNSEC())
    }
}
