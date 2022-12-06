package com.kgit2.signature

import com.kgit2.common.error.GitError
import com.kgit2.common.kgitRunTest
import com.kgit2.time.Time
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SignatureTest {
    @Test
    fun smoke() = kgitRunTest {
        Signature("foo", "bar", Time(89, 0))
        Signature("foo", "bar")
        assertFailsWith<GitError> {
            Signature("<foo>", "bar", Time(89, 0))
        }
        assertFailsWith<GitError> {
            Signature("<foo>", "bar")
        }

        val signature = Signature("foo", "bar")
        assertEquals("foo", signature.name)
        assertEquals("bar", signature.email)
    }
}
