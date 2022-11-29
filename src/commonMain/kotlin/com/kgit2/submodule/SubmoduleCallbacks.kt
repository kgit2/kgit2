package com.kgit2.submodule

import com.kgit2.common.error.GitErrorCode

/**
 * Function pointer to receive each submodule
 *
 * @param submodule git_submodule currently being visited
 * @param name name of the submodule
 * @return 0 on success or error code
 */
typealias SubmoduleCallback = (submodule: Submodule, name: String) -> GitErrorCode
