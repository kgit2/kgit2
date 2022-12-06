package com.kgit2.signature

import com.kgit2.common.error.GitError
import com.kgit2.common.kgitRunTest
import com.kgit2.time.Time
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SignatureTest {
    /**
     * #[test]
     *     fn smoke() {
     *         Signature::new("foo", "bar", &Time::new(89, 0)).unwrap();
     *         Signature::now("foo", "bar").unwrap();
     *         assert!(Signature::new("<foo>", "bar", &Time::new(89, 0)).is_err());
     *         assert!(Signature::now("<foo>", "bar").is_err());
     *
     *         let s = Signature::now("foo", "bar").unwrap();
     *         assert_eq!(s.name(), Some("foo"));
     *         assert_eq!(s.email(), Some("bar"));
     *
     *         drop(s.clone());
     *         drop(s.to_owned());
     *     }
     */
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
