package com.kgit2.common.option.mutually

import libgit2.GIT_PACKBUILDER_ADDING_OBJECTS
import libgit2.GIT_PACKBUILDER_DELTAFICATION
import libgit2.git_packbuilder_stage_t

enum class PackBuilderStage(val value: git_packbuilder_stage_t) {
    // Adding objects to the pack
    AddingObjects(GIT_PACKBUILDER_ADDING_OBJECTS),

    // Deltafication of the pack
    Deltafication(GIT_PACKBUILDER_DELTAFICATION);

    fun toRaw(): git_packbuilder_stage_t {
        return value
    }

    companion object {
        fun fromRaw(raw: git_packbuilder_stage_t): PackBuilderStage {
            return when (raw) {
                GIT_PACKBUILDER_ADDING_OBJECTS -> AddingObjects
                GIT_PACKBUILDER_DELTAFICATION -> Deltafication
                else -> error("Unknown value: $raw")
            }
        }
    }
}
