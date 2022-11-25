typealias ${module.moduleName}Pointer = CPointer<${module.git2Name}>

typealias ${module.moduleName}SecondaryPointer = CPointerVar<${module.git2Name}>

typealias ${module.moduleName}Initial = ${module.moduleName}SecondaryPointer.(Memory) -> Unit

class ${module.moduleName}Raw(
    memory: Memory,
    handler: ${module.moduleName}Pointer,
) : Raw<${module.git2Name}>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: ${module.moduleName}SecondaryPointer = memory.allocPointerTo(),
        <#if module.shouldFreeOnFailure>shouldFreeOnFailure: Boolean = true,</#if>
        initializer: ${module.moduleName}Initial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initializer?.invoke(this, memory)
        }.onFailure {
            <#if module.freeOnFailure?has_content>
            <#if module.shouldFreeOnFailure>
            if (shouldFreeOnFailure) {
                ${module.freeOnFailure}(handler.value)
            }
            <#else>
            ${module.freeOnFailure}(handler.value)
            </#if>
            </#if>
            memory.free()
        }.getOrThrow()
    }.value!!)
    <#if module.freeOnFailure?has_content>

    override val beforeFree: BeforeFree = {
        ${module.freeOnFailure}(handler)
    }
    </#if>
}
