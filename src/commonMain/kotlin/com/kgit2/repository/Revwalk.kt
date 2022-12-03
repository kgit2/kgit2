package com.kgit2.repository

import cnames.structs.git_revwalk
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.extend.errorCheck
import com.kgit2.memory.CallbackAble
import com.kgit2.memory.ICallbacksPayload
import com.kgit2.memory.IteratorBase
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import kotlinx.cinterop.StableRef
import libgit2.*

@Raw(
    base = git_revwalk::class,
    free = "git_revwalk_free",
)
class Revwalk(
    raw: RevwalkRaw,
) : RawWrapper<git_revwalk, RevwalkRaw>(raw), Iterable<Oid>, CallbackAble<git_revwalk, RevwalkRaw, Revwalk.CallbacksPayload> {
    constructor(secondaryInitial: RevwalkSecondaryInitial) : this(RevwalkRaw(secondaryInitial = secondaryInitial))

    override val callbacksPayload: CallbacksPayload = CallbacksPayload()

    override val stableRef: StableRef<CallbacksPayload> = callbacksPayload.asStableRef()

    var hideCallback: RevwalkHideCallback? by callbacksPayload::revwalkHideCallback

    init {
        git_revwalk_add_hide_cb(raw.handler, staticRevwalkHideCallback, stableRef.asCPointer())
    }

    /**
     * This will clear all the pushed and hidden commits,
     * and leave the walker in a blank state (just like at creation) ready to receive new commit pushes and start a new walk.
     * The revision walk is automatically reset when a walk is over.
     */
    fun reset() {
        git_revwalk_reset(raw.handler).errorCheck()
    }

    fun push(oid: Oid) {
        git_revwalk_push(raw.handler, oid.raw.handler).errorCheck()
    }

    fun hide(oid: Oid) {
        git_revwalk_hide(raw.handler, oid.raw.handler).errorCheck()
    }

    fun pushGlob(glob: String) {
        git_revwalk_push_glob(raw.handler, glob).errorCheck()
    }

    fun hideGlob(glob: String) {
        git_revwalk_hide_glob(raw.handler, glob).errorCheck()
    }

    fun pushHead() {
        git_revwalk_push_head(raw.handler).errorCheck()
    }

    fun hideHead() {
        git_revwalk_hide_head(raw.handler).errorCheck()
    }

    fun pushRef(refname: String) {
        git_revwalk_push_ref(raw.handler, refname).errorCheck()
    }

    fun hideRef(refname: String) {
        git_revwalk_hide_ref(raw.handler, refname).errorCheck()
    }

    /**
     * Changing the sorting mode resets the walker.
     */
    fun sorting(sortType: SortType) {
        git_revwalk_sorting(raw.handler, sortType.value).errorCheck()
    }

    /**
     * Push and hide the respective endpoints of the given range.
     * The range should be of the form [com.kgit2.commit.Commit]..[com.kgit2.commit.Commit] where
     * each [com.kgit2.commit.Commit] is in the form accepted by [RevParseMode.Single].
     * The left-hand commit will be hidden and the right-hand commit pushed.
     */
    fun pushRange(range: String) {
        git_revwalk_push_range(raw.handler, range).errorCheck()
    }

    fun simplifyFirstParent() {
        git_revwalk_simplify_first_parent(raw.handler).errorCheck()
    }

    override fun iterator(): Iterator<Oid> = InnerIterator()

    inner class InnerIterator : IteratorBase<Oid>() {
        override fun nextRaw(): Result<Oid> = runCatching {
            Oid {
                git_revwalk_next(this, raw.handler).errorCheck()
            }
        }
    }

    inner class CallbacksPayload : ICallbacksPayload, RevwalkHideCallbackPayload {
        override var revwalkHideCallback: RevwalkHideCallback? = null
    }
}
