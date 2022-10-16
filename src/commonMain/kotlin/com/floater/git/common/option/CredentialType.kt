package com.floater.git.common.option

import libgit2.git_credential_t

enum class CredentialType(val value: git_credential_t) {
    /**
     * A vanilla user/password request
     * @see git_credential_userpass_plaintext_new
     */
    GIT_CREDENTIAL_USERPASS_PLAINTEXT(1u shl 0),

    /**
     * An SSH key-based authentication request
     * @see git_credential_ssh_key_new
     */
    GIT_CREDENTIAL_SSH_KEY(1u shl 1),

    /**
     * An SSH key-based authentication request, with a custom signature
     * @see git_credential_ssh_custom_new
     */
    GIT_CREDENTIAL_SSH_CUSTOM(1u shl 2),

    /**
     * An NTLM/Negotiate-based authentication request.
     * @see git_credential_default
     */
    GIT_CREDENTIAL_DEFAULT(1u shl 3),

    /**
     * An SSH interactive authentication request
     * @see git_credential_ssh_interactive_new
     */
    GIT_CREDENTIAL_SSH_INTERACTIVE(1u shl 4),

    /**
     * Username-only authentication request
     *
     * Used as a pre-authentication step if the underlying transport
     * (eg. SSH, with no username in its URL) does not know which username
     * to use.
     *
     * @see git_credential_username_new
     */
    GIT_CREDENTIAL_USERNAME(1u shl 5),

    /**
     * An SSH key-based authentication request
     *
     * Allows credentials to be read from memory instead of files.
     * Note that because of differences in crypto backend support, it might
     * not be functional.
     *
     * @see git_credential_ssh_key_memory_new
     */
    GIT_CREDENTIAL_SSH_MEMORY(1u shl 6)
}
