package com.kgit2.memory

import kotlinx.cinterop.CPointed
import kotlinx.cinterop.StableRef
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

interface ICallbacksPayload {}

interface CallbackAble<T : CPointed, R : Raw<T>, P: ICallbacksPayload> : CleanAble, RawWrapperBase<T, R> {
    val callbacksPayload: P

    val stableRef: StableRef<P>
}

inline fun <P : ICallbacksPayload, T : CPointed, R : Raw<T>> CallbackAble<T, R, P>.createCleaner(): Cleaner = createCleaner(raw to stableRef) {
    it.second.dispose()
    it.first.free()
}
