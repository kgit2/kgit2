package com.kgit2

import kotlin.test.*

class KGit2Test {
    companion object {
        @BeforeClass
        fun beforeClass() {
            KGit2.initLibGit2()
        }

        @AfterClass
        fun afterClass() {
            KGit2.shutdown()
        }
    }

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
        val feature = KGit2.feature
        assertEquals(feature.value, 15U)
        assert(feature.enableThreads())
        assert(feature.enableHttps())
        assert(feature.enableSSH())
        assert(feature.enableNSEC())
    }
}
