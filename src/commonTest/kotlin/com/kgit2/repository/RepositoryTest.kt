package com.kgit2.repository

import com.kgit2.common.kgitRunTest
import com.kgit2.utils.withTempDir
import kotlinx.cinterop.convert
import libgit2.GIT_REPOSITORY_INIT_BARE
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RepositoryTest {
    @Test
    fun initOpts() = kgitRunTest {
        withTempDir { repoPath ->
            val opts = RepositoryInitOptions() {
                flags.repositoryInitBare(true)
            }
            val repository = Repository.initialExt(repoPath.toString(), opts)
            assertTrue(repository.isBare)
        }
    }

    @Test
    fun path() = kgitRunTest {
        withTempDir { repoPath ->
            val repository = Repository.initial(repoPath.toString())
            assertEquals(repoPath.toString(), repository.path.replace("/.git/", "")?.replace("/private", ""))
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
