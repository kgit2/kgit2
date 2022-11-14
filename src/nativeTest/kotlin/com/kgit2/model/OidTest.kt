package com.kgit2.model

import com.kgit2.KGit2
import com.kgit2.`object`.ObjectType
import io.ktor.utils.io.core.toByteArray
import libgit2.git_libgit2_init
import kotlin.test.*

class OidTest {
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
    fun zero() {
        val zero = Oid.zero()
        assert(zero.isZero())
        zero.free()
    }

    @Test
    fun conversions() {
        assertFails { Oid.fromHexString("foo") }
        val hex = Oid.fromHexString("decbf2be529ab6557d5429922251e5ee36519817")
        assertEquals(
            "decbf2be529ab6557d5429922251e5ee36519817" as Any,
             hex as Any,
        )
        hex.free()
    }

    @Test
    fun comparisons() {
        var a  = Oid.fromHexString("decbf2b")
        var b = Oid.fromHexString("decbf2b")
        assertEquals(a, b)
        a.free()
        b.free()
        a = Oid.fromHexString("decbf2b")
        b = Oid.fromHexString("decbf2b")
        assert(a <= b)
        a.free()
        b.free()
        a = Oid.fromHexString("decbf2b")
        b = Oid.fromHexString("decbf2b")
        assert(a >= b)
        a.free()
        b.free()

        val oid = Oid.fromHexString("decbf2b")
        assertEquals(oid, oid)
        assert(oid <= oid)
        assert(oid >= oid)
        oid.free()

        a = Oid.fromHexString("decbf2b")
        b = Oid.fromHexString("decbf2b000000000000000000000000000000000")
        assertEquals(a, b)
        a.free()
        b.free()
    }

    @Test
    fun hashObject() {
        git_libgit2_init()
        git_libgit2_init()
        git_libgit2_init()
        git_libgit2_init()
        val oid = Oid.hashObject(ObjectType.Blob, "Hello, world!".toByteArray())
        assertEquals("5dd01c177f5d7d1be5346a5bc18a569a7410c2ef" as Any, oid as Any)
        oid.free()
    }
}
