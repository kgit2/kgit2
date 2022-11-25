package com.kgit2.commit

import cnames.structs.git_annotated_commit
import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.reference.Reference
import com.kgit2.repository.Repository
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.*

@Raw(
    base = "git_annotated_commit",
    free = "git_annotated_commit_free",
)
class AnnotatedCommit(raw: AnnotatedCommitRaw) : GitBase<git_annotated_commit, AnnotatedCommitRaw>(raw) {
    constructor(memory: Memory, handler: AnnotatedCommitPointer) : this(AnnotatedCommitRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: AnnotatedCommitSecondaryPointer = memory.allocPointerTo(),
        initial: AnnotatedCommitInitial? = null,
    ) : this(AnnotatedCommitRaw(memory, handler, initial))

    constructor(repository: Repository, reference: Reference) : this(initial = {
        git_annotated_commit_from_ref(this.ptr, repository.raw.handler, reference.raw.handler)
    })

    constructor(repository: Repository, branchName: String, remoteUrl: String, id: Oid) : this(initial = {
        git_annotated_commit_from_fetchhead(this.ptr, repository.raw.handler, branchName, remoteUrl, id.raw.handler)
    })

    constructor(repository: Repository, refspec: String) : this(initial = {
        git_annotated_commit_from_revspec(this.ptr, repository.raw.handler, refspec)
    })

    val id: Oid = Oid(Memory(), git_annotated_commit_id(raw.handler)!!)

    val refName: String? = git_annotated_commit_ref(raw.handler)?.toKString()
}
