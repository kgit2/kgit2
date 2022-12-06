package com.kgit2.attr

import com.kgit2.common.callback.CallbackResult
import com.kgit2.common.kgitRunTest
import com.kgit2.utils.initRepository
import com.kgit2.utils.withTempDir
import okio.FileSystem
import kotlin.test.Test
import kotlin.test.assertEquals

class AttrTest {
    fun assertAttrValueType() {
    }

    @Test
    fun testAttr() = kgitRunTest {
        withTempDir { tempDir ->
            val (repoPath, repository) = initRepository(tempDir)
            FileSystem.SYSTEM.write(repoPath / ".gitattributes", mustCreate = true) {
                writeUtf8("""
                    |*.kt diff=kotlin -crlf myAttr
                    |NoMyAttr.kotlin !myAttr
                    |README caveat=unspecified
                """.trimMargin())
            }
            val attr = mutableMapOf<String, String>()
            repository.Attribute.attrForeach("com/kgit2/kgit2.kt", AttrCheckFlags()) { name, value ->
                attr[name] = value!!
                CallbackResult.Ok
            }
            val expect = mapOf(
                "diff" to "kotlin",
                "crlf" to "[internal]__FALSE__",
                "myAttr" to "[internal]__TRUE__",
            )
            assertEquals(expect, attr)
            val expectValueType = mapOf(
                "diff" to AttrValueType.String,
                "crlf" to AttrValueType.False,
                "myAttr" to AttrValueType.True,
            )
            assertEquals(expectValueType, attr.mapValues { AttrValueType.detect(it.value) })
        }
    }
}
