typealias ${module.moduleName}Pointer = CPointer<${module.git2Name}>

typealias ${module.moduleName}SecondaryPointer = CPointerVar<${module.git2Name}>

typealias ${module.moduleName}Initial = ${module.moduleName}Pointer.(Memory) -> Unit

class ${module.moduleName}Raw(
    memory: Memory = Memory(),
    handler: ${module.moduleName}Pointer = memory.alloc<${module.git2Name}>().ptr,
    <#if module.shouldFreeOnFailure>shouldFreeOnFailure: Boolean = true,</#if>
    initial: ${module.moduleName}Initial? = null,
) : Raw<${module.git2Name}>(memory, handler.apply {
    runCatching {
        initial?.invoke(handler, memory)
    }.onFailure {
        <#if module.freeOnFailure?has_content>
        <#if module.shouldFreeOnFailure>
        if (shouldFreeOnFailure) {
            ${module.freeOnFailure}(handler)
        }
        <#else>
        ${module.freeOnFailure}(handler)
        </#if>
        </#if>
        memory.free()
    }.getOrThrow()
})<#if module.freeOnFailure?has_content> {
    override val beforeFree: BeforeFree = {
        ${module.freeOnFailure}(handler)
    }
}
</#if>
