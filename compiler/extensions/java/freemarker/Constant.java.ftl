<#include "FileHeader.inc.ftl">
<#include "DocComment.inc.ftl">
<@standard_header generatorDescription, packageName/>

<#if withCodeComments>
/** Class which holds constant {@link #${name}}. */
</#if>
public final class ${name}
{
<#if withCodeComments && docComments??>
    <@doc_comments docComments, 1/>
</#if>
    public static final ${typeInfo.typeFullName} ${name} = ${value};
}
