<#if printHeader>
<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>
</#if>
<#if name??>
from .${name} import ${name}
</#if>
