package com.floater.git.repository

import com.floater.git.KGit2
import com.floater.git.common.option.RepositoryInitOptions
import com.floater.git.utils.withTempDir
import kotlin.test.BeforeClass
import kotlin.test.Test
import kotlin.test.assertEquals


class RepositoryTest {
    @Test
    fun initOpts() {
        withTempDir { repoPath ->
            val opts = RepositoryInitOptions().bare(true)
            val repository = KGit2.initOpts(repoPath, opts)
            assert(repository.isBare())
        }

    }

    @Test
    fun path() {
        withTempDir { repoPath ->
            val repository = KGit2.initRepository(repoPath)
            assertEquals(repoPath, repository.path()?.replace("/.git/", "")?.replace("/private", ""))
        }
    }

    @Test
    fun gitNoteDefaultRef() {
        withTempDir { repoPath ->
            val repository = KGit2.initRepository(repoPath)
            val noteDefaultRef = repository.noteDefaultRef()
            assertEquals("refs/notes/commits", noteDefaultRef)
        }
    }
}
