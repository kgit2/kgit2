package com.kgit2.repository

import com.kgit2.KGit2
import com.kgit2.utils.withTempDir
import kotlin.test.AfterClass
import kotlin.test.BeforeClass
import kotlin.test.Test
import kotlin.test.assertEquals

class RepositoryTest {
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
    fun initOpts() {
        withTempDir { repoPath ->
            val opts = RepositoryInitOptions().bare(true)
            val repository = Repository.initialExt(repoPath.toString(), opts)
            assert(repository.isBare)
            repository.free()
        }
    }

    @Test
    fun path() {
        withTempDir { repoPath ->
            val repository = Repository.initial(repoPath.toString())
            assertEquals(repoPath.toString(), repository.path?.replace("/.git/", "")?.replace("/private", ""))
            repository.free()
        }
    }

    @Test
    fun gitNoteDefaultRef() {
        withTempDir { repoPath ->
            val repository = Repository.initial(repoPath.toString())
            val noteDefaultRef = repository.noteDefaultRef
            assertEquals("refs/notes/commits", noteDefaultRef)
            repository.free()
        }
    }
}
