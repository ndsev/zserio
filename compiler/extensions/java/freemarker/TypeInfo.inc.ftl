<#macro type_info typeInfo>
    <#if typeInfo.typeInfoGetter??>
        zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.get${typeInfo.typeInfoGetter.suffix}(<#t>
                <#if typeInfo.typeInfoGetter.arg??>(byte)${typeInfo.typeInfoGetter.arg}</#if>)<#t>
    <#else>
        ${typeInfo.javaTypeName}.typeInfo()<#t>
    </#if>
</#macro>

<#macro fields_info fieldList indent=4>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if fieldList?has_content>
${I}java.util.Arrays.asList(
        <#list fieldList as field>
            <@field_info field field?has_next indent+2/>
        </#list>
${I});
    <#else>
${I}new java.util.ArrayList<zserio.runtime.typeinfo.FieldInfo>();
    </#if>
</#macro>

<#macro field_info field comma indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}new zserio.runtime.typeinfo.FieldInfo(
${I}        "${field.name}", // schemaName
    <#if (field.optional?? && field.optional.isRecursive) || (field.array?? && field.array.elementIsRecursive)>
${I}        new zserio.runtime.typeinfo.TypeInfo.RecursiveTypeInfo(<#rt>
                    <#lt>new <@field_info_recursive_type_info_getter_name field/>()), // typeInfo
    <#else>
${I}        <@type_info field.typeInfo/>, // typeInfo
    </#if>
${I}        <@field_info_type_arguments field/>, // typeArguments
${I}        "${(field.alignmentValue!"")?j_string}", // alignment
${I}        "<#if field.offset??>${field.offset.getter?j_string}</#if>", // offset
${I}        "${(field.initializer!"")?j_string}", // initializer
${I}        ${(field.optional??)?c}, // isOptional
${I}        "<#if field.optional??>${(field.optional.clause!"")?j_string}</#if>", // optionalCondition
${I}        "${(field.constraint!"")?j_string}", // constraint
${I}        ${(field.array??)?c}, // isArray
${I}        <#if field.array??>"${(field.array.length!"")?j_string}"<#else>""</#if>, // arrayLength
${I}        ${(field.array?? && field.array.isPacked)?c}, // isPacked
${I}        ${(field.array?? && field.array.isImplicit)?c} // isImplicit
${I})<#if comma>,</#if>
</#macro>

<#macro field_info_recursive_type_info_getter_name field>
     RecursiveTypeInfoGetter_${field.name}<#t>
</#macro>

<#macro field_info_recursive_type_info_getter field indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if (field.optional?? && field.optional.isRecursive) || (field.array?? && field.array.elementIsRecursive)>
${I}class <@field_info_recursive_type_info_getter_name field/>
${I}        implements zserio.runtime.typeinfo.TypeInfo.RecursiveTypeInfo.TypeInfoGetter
${I}{
${I}    @Override
${I}    public zserio.runtime.typeinfo.TypeInfo get()
${I}    {
${I}        return ${field.typeInfo.javaTypeName}.typeInfo();
${I}    }
${I}}

    </#if>
</#macro>

<#macro field_info_type_arguments field>
    <#if field.array?? && field.array.elementCompound??>
        <@field_info_compound_type_arguments field.array.elementCompound.instantiatedParameters/>
    <#elseif field.array?? && field.array.elementBitSize?? && field.array.elementBitSize.isDynamicBitField>
        java.util.Arrays.asList("${field.array.elementBitSize.value?j_string}")<#t>
    <#elseif field.compound??>
        <@field_info_compound_type_arguments field.compound.instantiatedParameters/>
    <#elseif field.bitSize?? && field.bitSize.isDynamicBitField>
        java.util.Arrays.asList("${field.bitSize.value?j_string}")<#t>
    <#else>
        new java.util.ArrayList<java.lang.String>()<#t>
    </#if>
</#macro>

<#macro field_info_compound_type_arguments parameters>
    java.util.Arrays.asList(<#t>
    <#list parameters as parameter>
        "${parameter.expression?j_string}"<#if parameter?has_next>, </#if><#t>
    </#list>
    )<#t>
</#macro>

<#macro parameters_info parametersList indent=4>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if parametersList?has_content>
${I}java.util.Arrays.asList(
        <#list parametersList as parameter>
            <@parameter_info parameter parameter?has_next indent+2/>
        </#list>
${I});
    <#else>
${I}new java.util.ArrayList<zserio.runtime.typeinfo.ParameterInfo>();
    </#if>
</#macro>

<#macro parameter_info parameter comma indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}new zserio.runtime.typeinfo.ParameterInfo(
${I}        "${parameter.name}", // schemaName
${I}        <@type_info parameter.typeInfo/> // typeInfo
${I})<#if comma>,</#if>
</#macro>

<#macro functions_info functionList indent=4>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if functionList?has_content>
${I}java.util.Arrays.asList(
        <#list functionList as function>
            <@function_info function function?has_next indent+2/>
        </#list>
${I});
    <#else>
${I}new java.util.ArrayList<zserio.runtime.typeinfo.FunctionInfo>();
    </#if>
</#macro>

<#macro function_info function comma indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}new zserio.runtime.typeinfo.FunctionInfo(
${I}        "${function.schemaName}", // schemaName
${I}        <@type_info function.returnTypeInfo/>, // typeInfo
${I}        "${function.resultExpression}" // result expression
${I})<#if comma>,</#if>
</#macro>

<#macro cases_info caseMemberList defaultMember isSwitchAllowed indent=4>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local fieldIndex=0>
    <#if caseMemberList?has_content || defaultMember?has_content>
${I}java.util.Arrays.asList(
        <#list caseMemberList as caseMember>
${I}        new zserio.runtime.typeinfo.CaseInfo(
                    <@case_info_case_expressions caseMember.caseList, isSwitchAllowed, indent+4/>
${I}                <#if caseMember.compoundField??>fields.get(${fieldIndex})<#local fieldIndex+=1><#else>null</#if>
${I}        )<#if caseMember?has_next || defaultMember?has_content>,</#if>
        </#list>
        <#if defaultMember?has_content>
${I}        new zserio.runtime.typeinfo.CaseInfo(
${I}                new java.util.ArrayList<String>(),
${I}                <#if defaultMember.compoundField??>fields.get(${fieldIndex})<#local fieldIndex+=1><#else>null</#if>
${I}        )
        </#if>
${I});
    <#else>
${I}new java.util.ArrayList<zserio.runtime.typeinfo.CaseInfo>();
    </#if>
</#macro>

<#macro case_info_case_expressions caseList isSwitchAllowed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}java.util.Arrays.asList(
    <#list caseList as case>
${I}        "${isSwitchAllowed?then(case.expressionForCase, case.expressionForIf)}"<#if case?has_next>,</#if>
    </#list>
${I}),
</#macro>

<#macro item_info name value>
    new zserio.runtime.typeinfo.ItemInfo("${name}", "${value}")<#t>
</#macro>

<#macro underlying_type_info_type_arguments bitSize>
    <#if bitSize?has_content && bitSize.isDynamicBitField>
    java.util.Arrays.asList("${bitSize.value?j_string}")<#t>
    <#else>
    new java.util.ArrayList<String>()<#t>
    </#if>
</#macro>

<#macro template_info_template_name templateInstantiation>
    "${templateInstantiation.templateName!""}"<#t>
</#macro>

<#macro template_info_template_arguments templateInstantiation indent=4>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if templateInstantiation?has_content>
${I}java.util.Arrays.asList(
        <#list templateInstantiation.templateArgumentTypeInfos as typeInfo>
${I}        <@type_info typeInfo/><#if typeInfo?has_next>,</#if>
        </#list>
${I});
    <#else>
${I}new java.util.ArrayList<zserio.runtime.typeinfo.TypeInfo>();
    </#if>
</#macro>

<#macro columns_info fieldList indent=4>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if fieldList?has_content>
${I}java.util.Arrays.asList(
        <#list fieldList as field>
            <@column_info field field?has_next indent+2/>
        </#list>
${I});
    <#else>
${I}new java.util.ArrayList<zserio.runtime.typeinfo.ColumnInfo>();
    </#if>
</#macro>

<#macro column_info field comma indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}new zserio.runtime.typeinfo.ColumnInfo(
${I}        "${field.name}", // schemaName
${I}        <@type_info field.typeInfo/>, // typeInfo
${I}        <@column_info_type_arguments field/>, // typeArguments
${I}        "${field.sqlTypeData.name}", // sqlTypeName
${I}        ${field.sqlConstraint!"\"\""}, // sqlConstraint
${I}        ${field.isVirtual?c} // isVirtual
${I})<#if comma>,</#if>
</#macro>

<#macro column_info_type_arguments field>
    <#if field.bitSize?? && field.bitSize.isDynamicBitField>
        java.util.Arrays.asList("${field.bitSize.value?j_string}")
    <#elseif field.typeParameters?has_content>
        java.util.Arrays.asList(<#t>
        <#list field.typeParameters as parameter>
            "<#if parameter.isExplicit>explicit </#if>${parameter.expression}"<#if parameter?has_next>, </#if><#t>
        </#list>
        )<#t>
    <#else>
        new java.util.ArrayList<java.lang.String>()<#t>
    </#if>
</#macro>

<#macro tables_info fieldList indent=4>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}java.util.Arrays.asList(
    <#list fieldList as field>
${I}        new zserio.runtime.typeinfo.TableInfo("${field.name}",
${I}                <@type_info field.typeInfo/>)<#if field?has_next>,</#if>
    </#list>
${I});
</#macro>

<#macro messages_info messageList indent=4>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if messageList?has_content>
${I}java.util.Arrays.asList(
        <#list messageList as message>
            <@message_info message message?has_next indent+2/>
        </#list>
${I})
    <#else>
${I}new java.util.ArrayList<zserio.rutnime.typeinfo.MessageInfo>()
    </#if>
</#macro>

<#macro message_info message comma indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}new zserio.runtime.typeinfo.MessageInfo(
${I}        "${message.name}", // schemaName
${I}        <@type_info message.typeInfo/>, // typeInfo
${I}        ${message.isPublished?c}, // isPublished
${I}        ${message.isSubscribed?c}, // isSubscribed
${I}        ${message.topicDefinition} // topicDefinition
${I})<#if comma>,</#if>
</#macro>

<#macro methods_info methodList indent=4>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if methodList?has_content>
${I}java.util.Arrays.asList(
        <#list methodList as method>
            <@method_info method method?has_next indent+2/>
        </#list>
${I})
    <#else>
${I}new java.util.ArrayList<zserio.runtime.typeinfo.MethodInfo>()
    </#if>
</#macro>

<#macro method_info method comma indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}new zserio.runtime.typeinfo.MethodInfo(
${I}        "${method.name}", // schemaName
${I}        <@type_info method.responseTypeInfo/>, // responseType
${I}        <@type_info method.requestTypeInfo/> // requestType
${I})<#if comma>,</#if>
</#macro>
