package com.kgit2.rebase

import cnames.structs.git_tree
import com.kgit2.annotations.Raw
import com.kgit2.checkout.CheckoutOptions
import com.kgit2.checkout.CheckoutOptionsRaw
import com.kgit2.commit.Commit
import com.kgit2.commit.CommitCreateCallback
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.merge.MergeOptions
import com.kgit2.merge.MergeOptionsRaw
import com.kgit2.oid.Oid
import com.kgit2.signature.Signature
import com.kgit2.tree.Tree
import kotlinx.cinterop.*
import libgit2.*
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

@Raw(
    base = git_rebase_options::class,
)
class RebaseOptions(
    raw: RebaseOptionsRaw = RebaseOptionsRaw(initial = {
        git_rebase_options_init(this, GIT_REBASE_OPTIONS_VERSION)
    }),
) : GitBase<git_rebase_options, RebaseOptionsRaw>(raw) {
    inner class CallbackPayload {
        var commitCreateCallback: CommitCreateCallback? = null
    }

    private val callbackPayload = CallbackPayload()
    private val stableRef = StableRef.create(callbackPayload)

    init {
        raw.handler.pointed.payload = stableRef.asCPointer()
    }

    override val cleaner: Cleaner = createCleaner(raw to stableRef) {
        it.second.dispose()
        it.first.free()
    }

    var quiet: Boolean = raw.handler.pointed.quiet.toBoolean()
        set(value) {
            field = value
            raw.handler.pointed.quiet = value.toInt()
        }

    var inMemory: Boolean = raw.handler.pointed.inmemory.toBoolean()
        set(value) {
            field = value
            raw.handler.pointed.inmemory = value.toInt()
        }

    var rewriteNotesRef: String? = raw.handler.pointed.rewrite_notes_ref?.toKString()
        set(value) {
            field = value
            raw.handler.pointed.rewrite_notes_ref = value?.cstr?.getPointer(raw.memory)
        }

    val mergeOptions: MergeOptions = MergeOptions(raw = MergeOptionsRaw(Memory(), raw.handler.pointed.merge_options))

    val checkoutOptions: CheckoutOptions =
        CheckoutOptions(raw = CheckoutOptionsRaw(Memory(), raw.handler.pointed.checkout_options))

    var commitCreateCallback: CommitCreateCallback?
        get() = callbackPayload.commitCreateCallback
        set(value) {
            callbackPayload.commitCreateCallback = value
            if (value != null) {
                raw.handler.pointed.commit_create_cb = staticCFunction {
                        id: CPointer<git_oid>?,
                        author: CPointer<git_signature>?,
                        committer: CPointer<git_signature>?,
                        messageEncoding: CPointer<ByteVar>?,
                        message: CPointer<ByteVar>?,
                        tree: CPointer<git_tree>?,
                        parent_count: ULong,
                        parents,
                        payload,
                    ->
                    val callbackPayload = payload!!.asStableRef<CallbackPayload>().get()
                    callbackPayload.commitCreateCallback!!.invoke(
                        Oid(handler = id!!),
                        author?.let { Signature(handler = it) },
                        committer?.let { Signature(handler = it) },
                        messageEncoding?.toKString(),
                        message?.toKString(),
                        Tree(handler = tree!!),
                        List(parent_count.convert()) { Commit(handler = parents!![it]!!) },
                    ).value
                }
            }
        }
}
