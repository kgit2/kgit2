package com.kgit2.repository

import com.kgit2.common.callback.CallbackResult
import com.kgit2.common.kgitRunTest
import com.kgit2.utils.withTempDir
// import io.github.aakira.napier.Napier
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

    @Test
    fun clone() = kgitRunTest {
        withTempDir { repoPath ->
            var progressCount = 0
            val progressPaths = mutableSetOf<String>()
            val cloneOptions = CloneOptions() {
                /**
                 * @sample
                 * ```
                 * repositoryCreateCallback = { path, bare ->
                 *     println("repositoryCreateCallback")
                 *     println("path: $path bare: $bare")
                 *     null to CallbackResult.Ok
                 * }
                 * remoteCreateCallback = { repository, name, url ->
                 *     println("remoteCreateCallback")
                 *     println("repository: $repository name: $name url: $url")
                 *     null to CallbackResult.Ok
                 * }
                 * fetchOptions.remoteCallbacks.credentialCallback = { url, usernameFromUrl, allowedTypes ->
                 *     println("credentials")
                 *     println("url: $url usernameFromUrl: $usernameFromUrl allowedTypes: $allowedTypes")
                 *     Credential()
                 * }
                 * ```
                 */
                checkoutOptions.progressCallback = { path, _, _ ->
                    if (progressCount == 0) {
                        // Napier.d("progressCallback")
                    }
                    progressCount++
                    path?.let { progressPaths.add(it) }
                    CallbackResult.Ok
                }
                fetchOptions.remoteCallbacks.transferProgress = { stats ->
                    // Napier.d("transferProgress")
                    // Napier.d("stats: $stats")
                    CallbackResult.Ok
                }
//                fetchOptions.proxyOptions.url = "127.0.0.1:7890"
            }
            val repository = Repository.clone(
                "https://github.com/kgit2/test_repo.git",
                repoPath.toString(),
                cloneOptions
            )
            assertEquals(2001, progressCount)
            assertEquals(List(2000) { "file_$it" }.toSet(), progressPaths)
        }
    }
}
