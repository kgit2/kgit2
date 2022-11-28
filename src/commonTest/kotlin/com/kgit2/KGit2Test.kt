package com.kgit2

import com.kgit2.common.extend.asCPointer
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
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

fun kgitRunTest(testBody: () -> Unit) {
    KGit2.initial()
    testBody.invoke()
    KGit2.shutdown()
}
