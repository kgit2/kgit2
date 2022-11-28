package ${packageName}

import com.kgit2.common.memory.Memory
import com.kgit2.memory.BeforeFree
import com.kgit2.memory.Raw
import kotlinx.cinterop.*
import libgit2.*

<#list modules as module>
<#include "raw-declare.ftl">
</#list>
