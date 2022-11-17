package com.kgit2.model

import com.kgit2.kgitRunTest
import com.kgit2.`object`.ObjectType
import io.ktor.utils.io.core.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class OidTest {
    @Test
    fun zero() = kgitRunTest {
        val zero = Oid.zero()
        assertTrue(zero.isZero())
        zero.free()
    }

    @Test
    fun conversions() = kgitRunTest {
        assertFails { Oid.fromHexString("foo") }
        val hex = Oid.fromHexString("decbf2be529ab6557d5429922251e5ee36519817")
        assertEquals(
            "decbf2be529ab6557d5429922251e5ee36519817" as Any,
             hex as Any,
        )
        hex.free()
    }

    @Test
    fun comparisons() = kgitRunTest {
        var a  = Oid.fromHexString("decbf2b")
        var b = Oid.fromHexString("decbf2b")
        assertEquals(a, b)
        a.free()
        b.free()
        a = Oid.fromHexString("decbf2b")
        b = Oid.fromHexString("decbf2b")
        assertTrue(a <= b)
        a.free()
        b.free()
        a = Oid.fromHexString("decbf2b")
        b = Oid.fromHexString("decbf2b")
        assertTrue(a >= b)
        a.free()
        b.free()

        val oid = Oid.fromHexString("decbf2b")
        assertEquals(oid, oid)
        assertTrue(oid <= oid)
        assertTrue(oid >= oid)
        oid.free()

        a = Oid.fromHexString("decbf2b")
        b = Oid.fromHexString("decbf2b000000000000000000000000000000000")
        assertEquals(a, b)
        a.free()
        b.free()
    }

    @Test
    fun hashObject() = kgitRunTest {
        val oid = Oid.hashObject(ObjectType.Blob, "Hello, world!".toByteArray())
        assertEquals("5dd01c177f5d7d1be5346a5bc18a569a7410c2ef" as Any, oid as Any)
        oid.free()
    }
}
