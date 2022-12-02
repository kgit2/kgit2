package com.kgit2.diff

import cnames.structs.git_diff_stats
import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import com.kgit2.model.Buf
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.convert
import libgit2.git_diff_stats_deletions
import libgit2.git_diff_stats_files_changed
import libgit2.git_diff_stats_insertions
import libgit2.git_diff_stats_to_buf

@Raw(
    base = git_diff_stats::class,
    free = "git_diff_stats_free"
)
class DiffStats(raw: DiffStatsRaw) : RawWrapper<git_diff_stats, DiffStatsRaw>(raw) {
    constructor(
        memory: Memory = Memory(),
        secondary: DiffStatsSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: DiffStatsSecondaryInitial? = null,
    ) : this(DiffStatsRaw(memory, secondary, secondaryInitial))

    val filesChanged: ULong = git_diff_stats_files_changed(raw.handler)

    val insertions: ULong = git_diff_stats_insertions(raw.handler)

    val deletions: ULong = git_diff_stats_deletions(raw.handler)

    fun toBuf(formatType: DiffStatsFormatType, width: ULong): Buf = Buf {
        git_diff_stats_to_buf(this, raw.handler, formatType.value, width.convert())
    }

    override fun toString(): String {
        return "DiffStats(filesChanged=$filesChanged, insertions=$insertions, deletions=$deletions)"
    }
}
