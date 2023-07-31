<#macro type_info typeInfo>
    <#if typeInfo.typeInfoGetter??>
        zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.get${typeInfo.typeInfoGetter.suffix}(<#t>
                <#if typeInfo.typeInfoGetter.arg??>(byte)${typeInfo.typeInfoGetter.arg}</#if>)<#t>
    <#else>
        ${typeInfo.typeFullName}.typeInfo()<#t>
    </#if>
</#macro>

<#macro fields_info fieldList indent=4>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if fieldList?has_content>
${I}java.util.Arrays.asList(
        <#list fieldList as field>
            <@field_info field, field?has_next, indent+2/>
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
${I}        "${field.getterName}", // getterName
${I}        "<#if withWriterCode>${field.setterName}</#if>", // setterName
    <#if (field.optional?? && field.optional.isRecursive) || (field.array?? && field.array.elementIsRecursive)>
${I}        new zserio.runtime.typeinfo.TypeInfo.RecursiveTypeInfo(<#rt>
                    <#lt>new <@field_info_recursive_type_info_getter_name field/>()), // typeInfo
    <#elseif field.array??>
${I}        <@type_info field.array.elementTypeInfo/>, // typeInfo
    <#else>
${I}        <@type_info field.typeInfo/>, // typeInfo
    </#if>
${I}        <@field_info_type_arguments field/>, // typeArguments
${I}        ${field.isExtended?c}, // isExtended
${I}        <#if field.alignmentValue??>() -> ${field.alignmentValue}<#else>null</#if>, // alignment
${I}        <#if field.offset??>(obj, index) -> ${field.offset.lambdaGetter}<#else>null</#if>, // offset
${I}        <#if field.initializer??>() -> ${field.initializer}<#else>null</#if>, // initializer
${I}        ${(field.optional??)?c}, // isOptional
${I}        <#if field.optional?? && field.optional.lambdaClause??>(obj) -> ${field.optional.lambdaClause}<#else>null</#if>, // optionalCondition
${I}        "<#if field.optional??>${field.optional.isUsedIndicatorName}</#if>", // isUsedIndicatorName
${I}        "<#if field.optional?? && withWriterCode>${field.optional.isSetIndicatorName}</#if>", // isSetIndicatorName
${I}        <#if field.lambdaConstraint??>(obj) -> ${field.lambdaConstraint}<#else>null</#if>, // constraint
${I}        ${(field.array??)?c}, // isArray
${I}        <#if field.array?? && field.array.lambdaLength??>(obj) -> (int)(${field.array.lambdaLength})<#else>null</#if>, // arrayLength
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
        <#if field.array??>
${I}        return ${field.array.elementTypeInfo.typeFullName}.typeInfo();
        <#else>
${I}        return ${field.typeInfo.typeFullName}.typeInfo();
        </#if>
${I}    }
${I}}

    </#if>
</#macro>

<#macro field_info_type_arguments field>
    <#if field.array?? && field.array.elementCompound??>
        <@field_info_compound_type_arguments field.array.elementCompound.instantiatedParameters/>
    <#elseif field.array?? && field.array.elementBitSize?? && field.array.elementBitSize.isDynamicBitField>
        java.util.Arrays.asList((obj, index) -> ${field.array.elementBitSize.lambdaValue})<#t>
    <#elseif field.compound??>
        <@field_info_compound_type_arguments field.compound.instantiatedParameters/>
    <#elseif field.bitSize?? && field.bitSize.isDynamicBitField>
        java.util.Arrays.asList((obj, index) -> ${field.bitSize.lambdaValue})<#t>
    <#else>
        new java.util.ArrayList<java.util.function.BiFunction<java.lang.Object, java.lang.Integer, java.lang.Object>>()<#t>
    </#if>
</#macro>

<#macro field_info_compound_type_arguments parameters>
    java.util.Arrays.asList(<#t>
    <#list parameters as parameter>
        (obj, index) -> ${parameter.lambdaExpression}<#if parameter?has_next>, </#if><#t>
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
${I}        (obj) -> ${function.lambdaResultExpression} // result expression
${I})<#if comma>,</#if>
</#macro>

<#macro cases_info caseMemberList defaultMember indent=4 fieldListVarName="fieldList">
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local fieldIndex=0>
    <#if caseMemberList?has_content || defaultMember?has_content>
${I}java.util.Arrays.asList(
        <#list caseMemberList as caseMember>
${I}        new zserio.runtime.typeinfo.CaseInfo(
                    <@case_info_case_expressions caseMember.caseList, indent+4/>
${I}                <#if caseMember.compoundField??>${fieldListVarName}.get(${fieldIndex})<#local fieldIndex+=1><#else>null</#if>
${I}        )<#if caseMember?has_next || defaultMember?has_content>,</#if>
        </#list>
        <#if defaultMember?has_content>
${I}        new zserio.runtime.typeinfo.CaseInfo(
${I}                new java.util.ArrayList<java.util.function.Supplier<java.lang.Object>>(),
${I}                <#if defaultMember.compoundField??>${fieldListVarName}.get(${fieldIndex})<#local fieldIndex+=1><#else>null</#if>
${I}        )
        </#if>
${I});
    <#else>
${I}new java.util.ArrayList<zserio.runtime.typeinfo.CaseInfo>();
    </#if>
</#macro>

<#macro case_info_case_expressions caseList indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}java.util.Arrays.asList(
    <#list caseList as case>
${I}        () -> ${case.expressionForIf}<#if case?has_next>,</#if>
    </#list>
${I}),
</#macro>

<#macro item_info name value isBigInteger>
    new zserio.runtime.typeinfo.ItemInfo("${name}", <#rt>
            <#lt><#if isBigInteger>${value}<#else>java.math.BigInteger.valueOf(${value})</#if>)<#t>
</#macro>

<#macro underlying_type_info_type_arguments bitSize>
    <#if bitSize?has_content && bitSize.isDynamicBitField>
    java.util.Arrays.asList(() -> ${bitSize.value})<#t>
    <#else>
    new java.util.ArrayList<java.util.function.Supplier<java.lang.Object>>()<#t>
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
            (<#if parameter.isExplicit>${parameter.expression}<#else>row</#if>) -> ${parameter.lambdaExpression}<#if parameter?has_next>, </#if><#t>
        </#list>
        )<#t>
    <#else>
        new java.util.ArrayList<java.util.function.Function<java.lang.Object, java.lang.Object>>()<#t>
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
