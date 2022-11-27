package com.kgit2

import libgit2.git_index_entry
import libgit2.git_oid
import libgit2.git_oid_cmp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KGit2Test {
    @Test
    fun version() = kgitRunTest {
        println(git_index_entry.size)
        println(git_oid.size)
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
        assertEquals(feature.value, 15U)
        assertTrue(feature.enableThreads())
        assertTrue(feature.enableHttps())
        assertTrue(feature.enableSSH())
        assertTrue(feature.enableNSEC())
    }
}

fun kgitRunTest(testBody: () -> Unit) {
    KGit2.initial()
    testBody.invoke()
    KGit2.shutdown()
}
