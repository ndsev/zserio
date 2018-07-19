<#include "FileHeader.inc.ftl">
<@standard_header generatorDescription, packageName, javaMajorVersion, []/>

<@class_header generatorDescription/>
public class ${name}
{
<#list items as item>
    public static final ${item.javaTypeName} ${item.name} = ${item.value};
</#list>
}
