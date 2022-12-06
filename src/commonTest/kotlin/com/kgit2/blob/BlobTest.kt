package com.kgit2.blob

import com.kgit2.common.kgitRunTest
import com.kgit2.repository.Repository
import com.kgit2.utils.withTempDir
import io.ktor.utils.io.core.*
import kotlinx.cinterop.invoke
import kotlinx.cinterop.pointed
import okio.Buffer
import okio.FileSystem
import okio.buffer
import kotlin.test.Test
import kotlin.test.assertEquals

class BlobTest {
    @Test
    fun buffer() = kgitRunTest {
        withTempDir { tempDir ->
            val repository = Repository.initial(tempDir.toString())
            val id = repository.Blob.blob(byteArrayOf(5, 4, 6))
            println(id)
            val blob = repository.Blob.findBlob(id)

            assertEquals(id, blob.id)
            assertEquals(3UL, blob.size)
            assertEquals(byteArrayOf(5, 4, 6).toList(), blob.content.toList())
            assertEquals(true, blob.isBinary)

            repository.Object.findObject(id).peelToBlob()
        }
    }

    @Test
    fun path() = kgitRunTest {
        withTempDir { tempDir ->
            val path = tempDir / "foo"
            FileSystem.SYSTEM.write(path, mustCreate = true) {
                write(byteArrayOf(7, 8, 9))
            }
            val repository = Repository.initial(tempDir.toString())
            val id = repository.Blob.createByPath(path.toString())
            val blob = repository.Blob.findBlob(id)

            assertEquals(id, blob.id)
            assertEquals(3UL, blob.size)
            assertEquals(byteArrayOf(7, 8, 9).toList(), blob.content.toList())

            val obj = blob.asObject()
        }
    }

    @Test
    fun stream() = kgitRunTest {
        withTempDir { tempDir ->
            val repository = Repository.initial(tempDir.toString())
            val writer = repository.Blob.writer("foo")
            writer.write("Hello".toByteArray())

            val id = writer.commit()
            println(id)
            val blob = repository.Blob.findBlob(id)

            assertEquals(id, blob.id)
            assertEquals(5UL, blob.size)
            assertEquals("Hello".toByteArray().toList(), blob.content.toList())

            val obj = blob.asObject()
        }
    }
}
