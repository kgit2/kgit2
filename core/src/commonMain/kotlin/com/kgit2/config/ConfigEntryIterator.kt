package com.kgit2.config

// open class ConfigEntryIterator(
//     config: Config,
//     glob: String? = null,
//     name: String? = null,
//     regexp: String? = null
// ) : GitBase<CPointer<git_config_iterator>>, Iterator<ConfigEntry> {
//     override val arena: Arena = Arena()
//     override val handler: CPointer<git_config_iterator> = memScoped {
//         val pointer = allocPointerTo<git_config_iterator>()
//         when {
//             !glob.isNullOrEmpty() -> git_config_iterator_glob_new(pointer.ptr, config.handler, glob).errorCheck()
//             !name.isNullOrEmpty() && !regexp.isNullOrEmpty() -> git_config_multivar_iterator_new(
//                 pointer.ptr,
//                 config.handler,
//                 name,
//                 regexp
//             ).errorCheck()
//
//             else -> git_config_iterator_new(pointer.ptr, config.handler).errorCheck()
//         }
//         pointer.value!!
//     }
//     open var next: ConfigEntry? = null
//
//     override fun free() {
//         git_config_iterator_free(handler)
//         super.free()
//     }
//
//     override fun next(): ConfigEntry {
//         return next!!
//     }
//
//     override fun hasNext(): Boolean {
//         return memScoped {
//             val entry = allocPointerTo<git_config_entry>()
//             runCatching {
//                 git_config_next(entry.ptr, handler).errorCheck()
//             }.onSuccess { next = ConfigEntry.fromPointer(entry.value!!) }
//             next != null
//         }
//     }
// }
