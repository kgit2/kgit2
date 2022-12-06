package com.kgit2.transaction

import cnames.structs.git_transaction
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import com.kgit2.reflog.Reflog
import com.kgit2.repository.Repository
import com.kgit2.signature.Signature
import kotlinx.cinterop.ptr
import libgit2.git_transaction_commit
import libgit2.git_transaction_lock_ref
import libgit2.git_transaction_new
import libgit2.git_transaction_remove
import libgit2.git_transaction_set_reflog
import libgit2.git_transaction_set_symbolic_target
import libgit2.git_transaction_set_target

@Raw(
    base = git_transaction::class,
    free = "git_transaction_free",
)
class Transaction(raw: TransactionRaw) : RawWrapper<git_transaction, TransactionRaw>(raw) {
    constructor(secondaryInitial: TransactionSecondaryInitial) : this(TransactionRaw(secondaryInitial = secondaryInitial))

    constructor(repository: Repository) : this(secondaryInitial = {
        git_transaction_new(this.ptr, repository.raw.handler).errorCheck()
    })

    fun lockRef(refname: String) {
        git_transaction_lock_ref(raw.handler, refname).errorCheck()
    }

    fun setTarget(refname: String, target: Oid, signature: Signature, message: String) {
        git_transaction_set_target(raw.handler, refname, target.raw.handler, signature.raw.handler, message).errorCheck()
    }

    fun setSymbolicTarget(refname: String, target: String, signature: Signature, message: String) {
        git_transaction_set_symbolic_target(raw.handler, refname, target, signature.raw.handler, message).errorCheck()
    }

    fun setReflog(refname: String, reflog: Reflog) {
        git_transaction_set_reflog(raw.handler, refname, reflog.raw.handler).errorCheck()
    }

    fun remove(refname: String) {
        git_transaction_remove(raw.handler, refname).errorCheck()
    }

    fun commit() {
        git_transaction_commit(raw.handler).errorCheck()
    }
}
