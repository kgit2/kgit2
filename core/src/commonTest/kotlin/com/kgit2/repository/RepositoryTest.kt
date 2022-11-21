package com.kgit2.repository

import com.kgit2.kgitRunTest
import com.kgit2.utils.withTempDir
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RepositoryTest {
    @Test
    fun initOpts() = kgitRunTest {
        withTempDir { repoPath ->
            val opts = RepositoryInitOptions().bare(true)
            val repository = Repository.initialExt(repoPath.toString(), opts)
            assertTrue(repository.isBare)
        }
    }

    @Test
    fun path() = kgitRunTest {
        withTempDir { repoPath ->
            val repository = Repository.initial(repoPath.toString())
            assertEquals(repoPath.toString(), repository.path?.replace("/.git/", "")?.replace("/private", ""))
        }
    }

    @Test
    fun gitNoteDefaultRef() = kgitRunTest {
        withTempDir { repoPath ->
            val repository = Repository.initial(repoPath.toString())
            val noteDefaultRef = repository.noteDefaultRef
            assertEquals("refs/notes/commits", noteDefaultRef)
        }
    }
}
