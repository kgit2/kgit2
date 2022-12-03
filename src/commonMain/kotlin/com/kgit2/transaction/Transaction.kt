package com.kgit2.transaction

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import com.kgit2.reflog.Reflog
import com.kgit2.signature.Signature
import libgit2.*

@Raw(
    base = git_transaction::class,
    free = "git_transaction_free",
)
class Transaction(raw: TransactionRaw) : RawWrapper<git_transaction, TransactionRaw>(raw) {
    constructor(secondaryInitial: TransactionSecondaryInitial) : this(TransactionRaw(secondaryInitial = secondaryInitial))

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
