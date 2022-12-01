package com.kgit2.rebase

import cnames.structs.git_tree
import com.kgit2.annotations.Raw
import com.kgit2.checkout.CheckoutOptions
import com.kgit2.checkout.CheckoutOptionsRaw
import com.kgit2.commit.Commit
import com.kgit2.commit.CommitCreateCallback
import com.kgit2.commit.CommitCreateCallbackPayload
import com.kgit2.commit.staticCommitCreateCallback
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.memory.CallbackAble
import com.kgit2.memory.ICallbacksPayload
import com.kgit2.memory.RawWrapper
import com.kgit2.memory.createCleaner
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
) : RawWrapper<git_rebase_options, RebaseOptionsRaw>(raw), CallbackAble<git_rebase_options, RebaseOptionsRaw, RebaseOptions.CallbacksPayload> {
    inner class CallbacksPayload: ICallbacksPayload, CommitCreateCallbackPayload {
        override var commitCreateCallback: CommitCreateCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.commit_create_cb = value?.let { staticCommitCreateCallback }
            }
    }

    override val callbacksPayload: CallbacksPayload = CallbacksPayload()
    override val stableRef: StableRef<CallbacksPayload> = callbacksPayload.asStableRef()

    init {
        raw.handler.pointed.payload = stableRef.asCPointer()
    }

    override val cleaner: Cleaner = createCleaner()

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

    var commitCreateCallback: CommitCreateCallback? by callbacksPayload::commitCreateCallback
}
