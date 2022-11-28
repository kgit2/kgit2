<#if module.flagsMutable>
<#assign flagsMutable = "var">
<#else>
<#assign flagsMutable = "val">
</#if>
interface ${module.className}Mask<T: ${module.className}Mask<T>> {
    ${flagsMutable} flags: ${module.flagsType}
    <#if module.flagsMutable>

    val onFlagsChanged: ((${module.flagsType}) -> Unit)?

    fun flag(value: ${module.flagsType}, on: Boolean): T {
        if (on) {
            this.flags = this.flags or value
        } else {
            this.flags = this.flags and value.inv()
        }
        this.onFlagsChanged?.invoke(this.flags)
        return this as T
    }
    </#if>

    operator fun contains(value: ${module.flagsType}): Boolean {
        return (this.flags and value) == value
    }
    <#list module.methods as method>
    <#if module.flagsMutable>

    fun ${method.name}(on: Boolean = true): T {
        return flag(${method.value}, on)
    }
    </#if>

    fun has${method.upperName}(): Boolean {
        return ${method.value} in this
    }
    </#list>
}
