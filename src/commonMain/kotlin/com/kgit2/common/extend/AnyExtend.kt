package com.kgit2.common.extend

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.StableRef

inline fun <reified T: Any> T.asStableRef(): StableRef<T> = StableRef.create(this)

inline fun <reified T: Any> T.asCPointer(): COpaquePointer = this.asStableRef().asCPointer()
