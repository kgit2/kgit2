package com.kgit2.blame

import com.kgit2.common.kgitRunTest
import com.kgit2.utils.initRepository
import com.kgit2.utils.withTempDir
import io.github.aakira.napier.Napier
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals

class BlameTest {
    @Test
    fun smoke() = kgitRunTest {
        withTempDir { tempDir ->
            val (repoPath, repository) = initRepository(tempDir)
            val index = repository.Index.index()

            val root = repository.workDir!!
            FileSystem.SYSTEM.createDirectories(root.toPath() / "foo", mustCreate = true)
            FileSystem.SYSTEM.write(root.toPath() / "foo" / "bar", mustCreate = true) {
                writeUtf8("")
            }
            index.addPath("foo/bar")

            val treeId = index.writeTree()
            val tree = repository.Tree.findTree(treeId)
            val signature = repository.Signature.signature()
            val headId = repository.Oid.refNameToOid("HEAD")
            val parent = repository.Commit.findCommit(headId)
            val commit = repository.Commit.commit("HEAD", signature, signature, "commit", tree, listOf(parent))

            val blame = repository.blame("foo/bar")
            assertEquals(1, blame.size)
            assertEquals(1, blame.iterator().asSequence().count())

            val hunk = blame[0]
            assertEquals(commit, hunk.finalCommitId)
            assertEquals(signature, hunk.finalSignature)
            assertEquals(1UL, hunk.finalStartLineNumber)
            assertEquals("foo/bar", hunk.originPath)
            assertEquals(0UL, hunk.linesCount)
            assert(!hunk.isBoundary)
        }
    }
}
