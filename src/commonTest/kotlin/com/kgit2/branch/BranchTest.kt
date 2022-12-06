package com.kgit2.branch

import com.kgit2.common.kgitRunTest
import com.kgit2.utils.initRepository
import com.kgit2.utils.withTempDir
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BranchTest {
    @Test
    fun smoke() = kgitRunTest {
        withTempDir { tempDir ->
            val (repoPath, repository) = initRepository(tempDir)
            val head = repository.Checkout.head()
            val target = head.target!!
            val commit = repository.Commit.findCommit(target)

            val b1 = repository.Branch.createBranch("foo", commit, false)
            assert(!b1.isHead())
            repository.Branch.createBranch("foo2", commit, false)

            assertEquals(3, repository.Branch.branches(BranchType.All).asSequence().count())
            repository.Branch.findBranch("foo", BranchType.Local)
            val b2 = b1.rename("bar", false)
            assertEquals("bar", b2.name)
            assertNull(b2.upstream)
            b2.setUpstream("main")
            assertNotNull(b2.upstream)
            b2.setUpstream(null)
            assertNull(b2.upstream)
            b2.delete()
        }
    }

    @Test
    fun nameIsValid() = kgitRunTest {
        assert(Branch.validName("foo"))
        assert(!Branch.validName(""))
        assert(!Branch.validName("with spaces"))
        assert(!Branch.validName("~tilde"))
    }
}
