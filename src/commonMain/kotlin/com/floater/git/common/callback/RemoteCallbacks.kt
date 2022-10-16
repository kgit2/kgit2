package com.floater.git.common.callback

import com.floater.git.common.option.CredentialType
import com.floater.git.cred.Cred
import libgit2.git_packbuilder_stage_t

data class RemoteCallbacks(
    var pushProgress: PushTransferProgress? = null,
    var progress: IndexerProgress? = null,
//    var packProgress: Option<Box<PackProgress<'a>>>,
//    var credentials: Option<Box<Credentials<'a>>>,
//    var sidebandProgress: Option<Box<TransportMessage<'a>>>,
//    var updateTips: Option<Box<UpdateTips<'a>>>,
//    var certificateCheck: Option<Box<CertificateCheck<'a>>>,
//    var pushUpdateReference: Option<Box<PushUpdateReference<'a>>>,
)

interface PushTransferProgress {
    fun pushTransferProgress(
        current: Int,
        total: Int,
        bytes: Int,
    )
}

interface IndexerProgress {
    fun indexerProgress(
        totalObjects: Int,
        indexedObjects: Int,
        receivingObjects: Int,
        localObjects: Int,
        totalDeltas: Int,
        indexedDeltas: Int,
        receivedBytes: Int,
    )
}

interface PackProgress {
    fun packProgress(
        stage: PackBuilderStage,
        current: Int,
        total: Int,
    )
}

enum class PackBuilderStage(val value: git_packbuilder_stage_t) {
    /// Adding objects to the pack
    GIT_PACKBUILDER_ADDING_OBJECTS(0u),

    /// Deltafication of the pack
    GIT_PACKBUILDER_DELTAFICATION(1u),
}

interface Credentials {
    fun credentials(
        url: String,
        usernameFromUrl: String,
        allowedTypes: CredentialType,
    ): Cred?
}
