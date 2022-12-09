package com.kgit2

import com.kgit2.common.kgitRunTest
import com.kgit2.config.ConfigLevel
import kotlinx.cinterop.toKString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
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

    @Test
    fun addExtension() = kgitRunTest {
        // Get
        assertEquals(1, KGit2.Options.extensions.size)
        assertEquals("noop", KGit2.Options.extensions[0])

        // Add
        KGit2.Options.addExtensions(listOf("custom"))
        assertEquals(2, KGit2.Options.extensions.size)
        assertEquals(setOf("noop", "custom"), KGit2.Options.extensions.toSet())
    }

    @Test
    fun removeExtension() = kgitRunTest {
        // Remove
        KGit2.Options.addExtensions(listOf("custom", "!ignore", "!noop", "other"))

        val extensions = KGit2.Options.extensions

        assertEquals(2, extensions.size)
        assertEquals("custom", extensions[0])
        assertEquals("other", extensions[1])
    }

    @Test
    fun searchPath() = kgitRunTest {
        val path = "fake_path";
        val original = KGit2.Options.getSearchPath(ConfigLevel.Global)
        assertNotEquals(path, original.buffer?.toKString())

        // Set
        KGit2.Options.setSearchPath(ConfigLevel.Global, path)
        assertEquals(path, KGit2.Options.getSearchPath(ConfigLevel.Global).buffer?.toKString())

        // Append
        val paths = "${"$"}PATH:$path"
        KGit2.Options.setSearchPath(ConfigLevel.Global, paths)
        assertEquals("$path:$path", KGit2.Options.getSearchPath(ConfigLevel.Global).buffer?.toKString())

        // Reset
        KGit2.Options.resetSearchPath(ConfigLevel.Global)
        assertEquals(original.buffer?.toKString(), KGit2.Options.getSearchPath(ConfigLevel.Global).buffer?.toKString())
    }
}
