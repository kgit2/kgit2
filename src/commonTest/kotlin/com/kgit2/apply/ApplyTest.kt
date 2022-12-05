package com.kgit2.apply

import com.kgit2.common.callback.CallbackResult
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.kgitRunTest
import com.kgit2.utils.initRepository
import com.kgit2.utils.withTempDir
import kotlinx.cinterop.pointed
import kotlinx.cinterop.staticCFunction
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplyTest {
    @Test
    fun smokeTest() = kgitRunTest {
        withTempDir {
            val (tempDir, repository) = initRepository(it)
            val diff = repository.Diff.diffTreeToWorkdir()
            var countHunks = 0
            var countDelta = 0
            val options = ApplyOptions() {
                hunkCallback = {
                    countHunks++
                    CallbackResult.Ok
                }
                deltaCallback = {
                    countDelta++
                    CallbackResult.Ok
                }
            }
            repository.Apply.apply(diff, ApplyLocation.Both, options)
            assertEquals(0, countHunks)
            assertEquals(0, countDelta)
        }
    }

    @Test
    fun applyHunksAndDelta() = kgitRunTest {
        withTempDir {
            val filePath = "foo.txt".toPath()
            val (tempDir, repository) = initRepository(it)
            FileSystem.SYSTEM.write(tempDir / filePath, mustCreate = true) {
                writeUtf8("bar")
            }
            repository.Index.index().addPath(filePath.toString())
            repository.Index.index().write()
            FileSystem.SYSTEM.write(tempDir / filePath, mustCreate = false) {
                writeUtf8("foo\nbar")
            }
            val diff = repository.Diff.diffIndexToWorkdir()
            assertEquals(1, diff.size)
            var countHunks = 0
            var countDelta = 0
            val options = ApplyOptions() {
                hunkCallback = {
                    countHunks++
                    CallbackResult.Ok
                }
                deltaCallback = {
                    countDelta++
                    CallbackResult.Ok
                }
            }
            repository.Apply.apply(diff, ApplyLocation.Index, options)
            assertEquals(1, countHunks)
            assertEquals(1, countDelta)
        }
    }
}
