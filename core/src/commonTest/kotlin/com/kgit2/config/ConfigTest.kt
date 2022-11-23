package com.kgit2.config

import com.kgit2.kgitRunTest
import com.kgit2.utils.withTempDir
import kotlin.test.*

class ConfigTest {
    @Test
    fun smoke() = kgitRunTest {
        val config = Config()
        var path = Config.findGlobal()
        path = Config.findSystem()
        path = Config.findXDG()
    }

    @Test
    fun persisted() = kgitRunTest {
        withTempDir {
            var config = openConfig(it, "foo")

            runCatching {
                config.getBool("foo.bar")
            }.onFailure {
                assertTrue(true)
            }.onSuccess {
                assertTrue(false)
            }

            config.setBool("foo.k1", true)
            config.setInt32("foo.k2", 1)
            config.setInt64("foo.k3", 2)
            config.setString("foo.k4", "bar")
            config.snapshot()

            config = Config((it / "foo").toString())
            var snapshot = config.snapshot()
            assertTrue(config.getBool("foo.k1"))
            assertEquals(1, config.getInt32("foo.k2"))
            assertEquals(2L, config.getInt64("foo.k3"))
            assertEquals("bar", config.getStringBuf("foo.k4"))

            val entries = config.getEntries()
            assertEquals(4, entries.list.size)

            snapshot = config.snapshot()
            assertEquals("bar", snapshot.getString("foo.k4"))
        }
    }

    @Test
    fun multiVar() = kgitRunTest {
        withTempDir {
            val config = openConfig(it, "foo")
            config.setMultiVar("foo.bar", "^$", "baz")
            config.setMultiVar("foo.bar", "^$", "qux")
            config.setMultiVar("foo.bar", "^$", "quux")
            config.setMultiVar("foo.baz", "^$", "oki")

            val entries = config.getEntries("foo.bar").list.map(ConfigEntry::value)
            val expectList = listOf("baz", "qux", "quux")
            assertEquals(expectList, entries)

            val multiVar = config.getMultiVar("foo.bar").list.map(ConfigEntry::value)
            assertEquals(expectList, multiVar)

            val multiVar2 = config.getMultiVar("foo.bar", "qu.*x").list.map(ConfigEntry::value)
            val expectList2 = listOf("qux", "quux")
            assertEquals(expectList2, multiVar2)

            config.removeMultiVar("foo.bar", ".*")
            assertEquals(0, config.getEntries("foo.bar").list.size)
            assertEquals(0, config.getMultiVar("foo.bar").list.size)
        }
    }

    @Test
    fun parse() = kgitRunTest {
        assertEquals(false, Config.parseBool(""))
        assertEquals(false, Config.parseBool("false"))
        assertEquals(false, Config.parseBool("no"))
        assertEquals(false, Config.parseBool("off"))
        assertEquals(false, Config.parseBool("0"))

        assertEquals(true, Config.parseBool("true"))
        assertEquals(true, Config.parseBool("yes"))
        assertEquals(true, Config.parseBool("on"))
        assertEquals(true, Config.parseBool("1"))
        assertEquals(true, Config.parseBool("42"))

        assertNull(Config.parseBool(" "))
        assertNull(Config.parseBool("some-string"))
        assertNull(Config.parseBool("-"))

        assertEquals(0, Config.parseInt32("0"))
        assertEquals(1, Config.parseInt32("1"))
        assertEquals(100, Config.parseInt32("100"))
        assertEquals(-1, Config.parseInt32("-1"))
        assertEquals(-100, Config.parseInt32("-100"))
        assertEquals(1024, Config.parseInt32("1k"))
        assertEquals(4096, Config.parseInt32("4k"))
        assertEquals(1048576, Config.parseInt32("1M"))
        assertEquals(1024 * 1024 * 1024, Config.parseInt32("1G"))

        assertEquals(0, Config.parseInt64("0"))
        assertEquals(1, Config.parseInt64("1"))
        assertEquals(100, Config.parseInt64("100"))
        assertEquals(-1, Config.parseInt64("-1"))
        assertEquals(-100, Config.parseInt64("-100"))
        assertEquals(1024, Config.parseInt64("1k"))
        assertEquals(4096, Config.parseInt64("4k"))
        assertEquals(1048576, Config.parseInt64("1M"))
        assertEquals(1024 * 1024 * 1024, Config.parseInt64("1G"))
        assertEquals(100 * 1024 * 1024 * 1024L, Config.parseInt64("100G"))
    }
}
