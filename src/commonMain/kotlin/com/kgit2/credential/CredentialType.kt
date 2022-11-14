package com.kgit2.credential

import com.kgit2.common.option.BaseMultiple
import libgit2.*

data class CredentialType(val value: git_credential_t) : BaseMultiple<CredentialType>() {
    companion object {
        val UserPassPlaintext = CredentialType(GIT_CREDENTIAL_USERPASS_PLAINTEXT)
        val SSHKey = CredentialType(GIT_CREDENTIAL_SSH_KEY)
        val SSHCustom = CredentialType(GIT_CREDENTIAL_SSH_CUSTOM)
        val Default = CredentialType(GIT_CREDENTIAL_DEFAULT)
        val SSHInteractive = CredentialType(GIT_CREDENTIAL_SSH_INTERACTIVE)
        val Username = CredentialType(GIT_CREDENTIAL_USERNAME)
        val SSHMemory = CredentialType(GIT_CREDENTIAL_SSH_MEMORY)
    }

    override val longValue: ULong
        get() = value.toULong()
}
