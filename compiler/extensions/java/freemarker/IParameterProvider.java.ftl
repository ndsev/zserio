<#include "FileHeader.inc.ftl">
<@standard_header generatorDescription, rootPackageName, javaMajorVersion, []/>
<#if sqlTableParameters?has_content>
<@imports ["java.sql.ResultSet"]/>
</#if>

<@class_header generatorDescription/>
public interface IParameterProvider
{
<#list sqlTableParameters as sqlTableParameter>
    <#if sqlTableParameter.isExplicit>
    ${sqlTableParameter.javaTypeName} get${sqlTableParameter.tableName}_${sqlTableParameter.expression}(ResultSet resultSet);
    </#if>
</#list>
};
