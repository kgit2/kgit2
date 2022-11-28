package com.kgit2.credential

import com.kgit2.annotations.FlagMask
import libgit2.git_credential_t

@FlagMask(
    flagsType = git_credential_t::class,
    "GIT_CREDENTIAL_USERPASS_PLAINTEXT",
    "GIT_CREDENTIAL_SSH_KEY",
    "GIT_CREDENTIAL_SSH_CUSTOM",
    "GIT_CREDENTIAL_DEFAULT",
    "GIT_CREDENTIAL_SSH_INTERACTIVE",
    "GIT_CREDENTIAL_USERNAME",
    "GIT_CREDENTIAL_SSH_MEMORY",
    flagsMutable = false,
)
data class CredentialType(
    override var flags: git_credential_t,
) : CredentialTypeMask<CredentialType>
