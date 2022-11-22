package com.kgit2.odb

import com.kgit2.common.memory.Memory
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import libgit2.git_odb_stream

typealias OdbStreamPointer = CPointer<git_odb_stream>

typealias OdbStreamSecondaryPointer = CPointerVar<git_odb_stream>

typealias OdbStreamInitial = OdbStreamSecondaryPointer.(Memory) -> Unit

class OdbStream {
}
