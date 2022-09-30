<#include "FileHeader.inc.ftl"/>
<#include "DocComment.inc.ftl">
<@file_header generatorDescription/>
<@all_imports packageImports symbolImports typeImports/>

${name} = ${targetTypeName}
<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
