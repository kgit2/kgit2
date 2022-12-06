package com.kgit2.model

import com.kgit2.common.kgitRunTest
import com.kgit2.`object`.ObjectType
import com.kgit2.oid.Oid
import io.ktor.utils.io.core.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class OidTest {
    @Test
    fun zero() = kgitRunTest {
        val zero = Oid.zero
        assertTrue(zero.isZero())
    }

    @Test
    fun conversions() = kgitRunTest {
        assertFails { Oid("foo") }
        val hex = Oid("decbf2be529ab6557d5429922251e5ee36519817")
        assertEquals(
            "decbf2be529ab6557d5429922251e5ee36519817" as Any,
             hex as Any,
        )
    }

    @Test
    fun comparisons() = kgitRunTest {
        var a  = Oid("decbf2b")
        var b = Oid("decbf2b")
        assertEquals(a, b)
        a = Oid("decbf2b")
        b = Oid("decbf2b")
        assertTrue(a <= b)
        a = Oid("decbf2b")
        b = Oid("decbf2b")
        assertTrue(a >= b)

        val oid = Oid("decbf2b")
        assertEquals(oid, oid)
        assertTrue(oid <= oid)
        assertTrue(oid >= oid)

        a = Oid("decbf2b")
        b = Oid("decbf2b000000000000000000000000000000000")
        assertEquals(a, b)
    }

    @Test
    fun hashObject() = kgitRunTest {
        val oid = Oid(ObjectType.Blob, "Hello, world!".toByteArray())
        assertEquals("5dd01c177f5d7d1be5346a5bc18a569a7410c2ef" as Any, oid as Any)
    }
}
