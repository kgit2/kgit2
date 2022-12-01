<#if module.structVar>
typealias ${module.moduleName}Value = CValue<${module.git2Name}>

</#if>
typealias ${module.moduleName}Pointer = CPointer<${module.git2Name}>

typealias ${module.moduleName}Initial = ${module.moduleName}Pointer.(Memory) -> Unit

typealias ${module.moduleName}SecondaryPointer = CPointerVar<${module.git2Name}>

typealias ${module.moduleName}SecondaryInitial = ${module.moduleName}SecondaryPointer.(Memory) -> Unit

class ${module.moduleName}Raw(
    memory: Memory,
    handler: ${module.moduleName}Pointer,
) : Raw<${module.git2Name}>(memory, handler) {
    constructor(
        memory: Memory,
        struct: ${module.git2Name},
    ) : this(memory, struct.ptr)

    <#if module.structVar>
    constructor(
        memory: Memory,
        value: ${module.moduleName}Value,
    ) : this(memory, value.getPointer(memory))

    constructor(
        memory: Memory = Memory(),
        handler: ${module.moduleName}Pointer = memory.alloc<${module.git2Name}>().ptr,
        <#if module.shouldFreeOnFailure>shouldFreeOnFailure: Boolean = true,</#if>
        initial: ${module.moduleName}Initial?,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            <#if module.pointerFree?has_content>
            <#if module.shouldFreeOnFailure>
            if (shouldFreeOnFailure) {
                ${module.pointerFree}
            }
            <#else>
            ${module.pointerFree}
            </#if>
            </#if>
            memory.free()
        }.getOrThrow()
    })
    </#if>

    constructor(
        memory: Memory = Memory(),
        secondary: ${module.moduleName}SecondaryPointer = memory.allocPointerTo(),
        <#if module.shouldFreeOnFailure>shouldFreeOnFailure: Boolean = true,</#if>
        secondaryInitial: ${module.moduleName}SecondaryInitial? = null,
    ) : this(memory, secondary.apply {
        runCatching {
            secondaryInitial?.invoke(secondary, memory)
        }.onFailure {
        <#if module.secondaryFree?has_content>
            <#if module.shouldFreeOnFailure>
            if (shouldFreeOnFailure) {
                ${module.secondaryFree}
            }
            <#else>
            ${module.secondaryFree}
            </#if>
        <#elseif module.pointerFree?has_content>
            <#if module.shouldFreeOnFailure>
            if (shouldFreeOnFailure) {
                ${module.pointerFree}
            }
            <#else>
            ${module.pointerFree}
            </#if>
        </#if>
            memory.free()
        }.getOrThrow()
    }.value!!)
    <#if module.beforeFree?has_content>

    override var beforeFree: BeforeFree? = {
        ${module.beforeFree}
    }
    </#if>
}
