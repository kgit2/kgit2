package ${packageName}

import libgit2.*
import kotlinx.cinterop.*

<#list modules as module>
<#include "flag-mask.ftl">
</#list>
