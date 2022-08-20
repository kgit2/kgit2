package com.floater.git.model.impl

import com.floater.git.model.Version

class VersionImpl(
    override val major: Int,
    override val minor: Int,
    override val patch: Int,
) : Version {
    override fun toString(): String {
        return "$major.$minor.$patch"
    }
}
