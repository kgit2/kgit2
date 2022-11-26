package com.kgit2.odb

import com.kgit2.annotations.Raw
import libgit2.git_odb_stream

@Raw(
    base = git_odb_stream::class,
    free = "git_odb_stream_free",
)
class OdbStream
