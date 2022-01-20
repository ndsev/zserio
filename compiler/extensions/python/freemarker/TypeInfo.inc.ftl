<#macro member_info_field field comma indent=3>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}zserio.typeinfo.MemberInfo(
${I}    '${field.name}', <#rt>
    <#if field.array??>
        <#if field.array.elementIsRecursive>
            <#lt>zserio.typeinfo.RecursiveTypeInfo(${field.array.elementTypeInfo.typeFullName}.type_info),
        <#else>
            <#lt><@type_info field.array.elementTypeInfo/>,
        </#if>
    <#else>
        <#if field.optional?? && field.optional.isRecursive>
            <#lt>zserio.typeinfo.RecursiveTypeInfo(${field.typeInfo.typeFullName}.type_info),
        <#else>
            <#lt><@type_info field.typeInfo/>,
        </#if>
    </#if>
        <@member_info_field_attributes field, indent+1/><#t>
${I})<#if comma>,</#if>
</#macro>

<#macro member_info_parameter parameter comma>
            zserio.typeinfo.MemberInfo(
                '${parameter.name}', <@type_info parameter.typeInfo/>,
                <@member_info_parameter_attributes parameter/><#t>
            )<#if comma>,</#if>
</#macro>

<#macro member_info_function function comma>
            zserio.typeinfo.MemberInfo(
                '${function.schemaName}', <@type_info function.returnTypeInfo/>,
                <@member_info_function_attributes function/><#t>
            )<#if comma>,</#if>
</#macro>

<#macro member_info_database_field field comma>
            zserio.typeinfo.MemberInfo(
                '${field.name}', <@type_info field.typeInfo/>,
                <@member_info_database_field_attributes field/><#t>
            )<#if comma>,</#if>
</#macro>

<#macro member_info_table_field field comma>
            zserio.typeinfo.MemberInfo(
                '${field.name}', <@type_info field.typeInfo/>,
                <@member_info_table_field_attributes field/><#t>
            )<#if comma>,</#if>
</#macro>

<#macro member_info_message message comma>
            zserio.typeinfo.MemberInfo(
                '${message.name}', <@type_info message.typeInfo/>,
                <@member_info_message_attributes message/><#t>
            )<#if comma>,</#if>
</#macro>

<#macro member_info_method method comma>
            zserio.typeinfo.MemberInfo(
                '${method.name}', <@type_info method.responseTypeInfo/>,
                <@member_info_method_attributes method/><#t>
            )<#if comma>,</#if>
</#macro>

<#macro type_info_template_instantiation_attributes templateInstantiation>
            zserio.typeinfo.TypeAttribute.TEMPLATE_NAME : '${templateInstantiation.templateName}',
            zserio.typeinfo.TypeAttribute.TEMPLATE_ARGUMENTS : [<#rt>
        <#list templateInstantiation.templateArgumentTypeInfos as templateArgumentTypeInfo>
                <@type_info templateArgumentTypeInfo/><#if templateArgumentTypeInfo?has_next>, </#if><#t>
        </#list>
                ]<#t>
</#macro>

<#macro type_info typeInfo>
    <#if typeInfo.hasTypeInfo>
        ${typeInfo.typeFullName}.type_info()<#t>
    <#else>
        zserio.typeinfo.TypeInfo('${typeInfo.schemaTypeFullName}', ${typeInfo.typeFullName})<#t>
    </#if>
</#macro>

<#macro cases_info caseMemberList defaultMember fieldListVarName="field_list">
    <#local fieldIndex=0>
    <#list caseMemberList as caseMember>
            zserio.typeinfo.CaseInfo(
                [
        <#list caseMember.expressionList as expression>
                    "${expression}"<#if expression?has_next>,</#if>
        </#list>
                ],
                <#if caseMember.compoundField??>${fieldListVarName}[${fieldIndex}]<#local fieldIndex+=1><#else>None</#if>
            )<#if caseMember?has_next || defaultMember?has_content>,</#if>
    </#list>
    <#if defaultMember?has_content>
            zserio.typeinfo.CaseInfo(
                [],
                <#if defaultMember.compoundField??>${fieldListVarName}[${fieldIndex}]<#local fieldIndex+=1><#else>None</#if>
            )
    </#if>
</#macro>

<#macro member_info_field_attributes field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local attributes=["zserio.typeinfo.MemberAttribute.PROPERTY_NAME : '${field.propertyName}'"]>
    <#if field.array??>
        <#if field.array.length??>
            <#local attributes+=["zserio.typeinfo.MemberAttribute.ARRAY_LENGTH : '${field.array.length}'"]>
        <#else>
            <#local attributes+=["zserio.typeinfo.MemberAttribute.ARRAY_LENGTH : None"]>
        </#if>
        <#if field.array.isImplicit>
            <#local attributes+=["zserio.typeinfo.MemberAttribute.IMPLICIT : None"]>
        </#if>
        <#if field.array.isPacked>
            <#local attributes+=["zserio.typeinfo.MemberAttribute.PACKED : None"]>
        </#if>
        <#if field.array.elementCompound??>
            <#local typeArguments><@member_info_compound_type_arguments field.array.elementCompound/></#local>
        <#elseif field.array.elementTypeInfo.isDynamicBitField>
            <#local typeArguments="['${field.array.elementBitSize.value}']">
        </#if>
    <#else>
        <#if field.compound??>
            <#local typeArguments><@member_info_compound_type_arguments field.compound/></#local>
        <#elseif field.typeInfo.isDynamicBitField>
            <#local typeArguments="['${field.bitSize.value}']">
        </#if>
    </#if>
    <#if typeArguments?has_content>
        <#local attributes+=["zserio.typeinfo.MemberAttribute.TYPE_ARGUMENTS : ${typeArguments}"]>
    </#if>
    <#if field.alignmentValue??>
        <#local attributes+=["zserio.typeinfo.MemberAttribute.ALIGN : '${field.alignmentValue}'"]>
    </#if>
    <#if field.offset??>
        <#local attributes+=["zserio.typeinfo.MemberAttribute.OFFSET : '${field.offset.getter}'"]>
    </#if>
    <#if field.initializer??>
        <#local attributes+=["zserio.typeinfo.MemberAttribute.INITIALIZER : '${field.initializer}'"]>
    </#if>
    <#if field.optional??>
        <#local optionalClause><#if field.optional.clause??>'${field.optional.clause}'<#else>None</#if></#local>
        <#local attributes+=["zserio.typeinfo.MemberAttribute.OPTIONAL : ${optionalClause}"]>
    </#if>
    <#if field.constraint??>
        <#local attributes+=["zserio.typeinfo.MemberAttribute.CONSTRAINT : '${field.constraint}'"]>
    </#if>
${I}attributes={
    <#list attributes as attribute>
${I}    ${attribute}<#if attribute?has_next>,</#if>
    </#list>
${I}}
</#macro>

<#macro member_info_parameter_attributes parameter>
                attributes={
                    zserio.typeinfo.MemberAttribute.PROPERTY_NAME : '${parameter.propertyName}'
                }
</#macro>

<#macro member_info_function_attributes function>
                attributes={
                    zserio.typeinfo.MemberAttribute.FUNCTION_NAME : '${function.functionName}',
                    zserio.typeinfo.MemberAttribute.FUNCTION_RESULT : '${function.resultExpression}'
                }
</#macro>

<#macro member_info_database_field_attributes field>
                attributes={
                    zserio.typeinfo.MemberAttribute.PROPERTY_NAME : '${field.propertyName}'
                }
</#macro>

<#macro member_info_table_field_attributes field>
    <#local attributes=["zserio.typeinfo.MemberAttribute.SQL_TYPE_NAME : '${field.sqlTypeData.name}'"]>
    <#if field.parameters?has_content>
        <#local typeArguments><@member_info_table_field_type_arguments field/></#local>
        <#local attributes+=["zserio.typeinfo.MemberAttribute.TYPE_ARGUMENTS : ${typeArguments}"]>
    <#elseif field.typeInfo.isDynamicBitField>
        <#local attributes+=["zserio.typeinfo.MemberAttribute.TYPE_ARGUMENTS : '${field.bitSize}'"]>
    </#if>
    <#if field.sqlConstraint??>
        <#local attributes+=["zserio.typeinfo.MemberAttribute.SQL_CONSTRAINT : ${field.sqlConstraint}"]>
    </#if>
    <#if field.isVirtual>
        <#local attributes+=["zserio.typeinfo.MemberAttribute.VIRTUAL : None"]>
    </#if>
                attributes={
        <#list attributes as attribute>
                    ${attribute}<#if attribute?has_next>,</#if>
        </#list>
                }
</#macro>

<#macro member_info_message_attributes message>
    <#local attributes=["zserio.typeinfo.MemberAttribute.TOPIC : ${message.topicDefinition}"]>
    <#if message.isPublished>
        <#local attributes+=["zserio.typeinfo.MemberAttribute.PUBLISH : 'publish_${message.snakeCaseName}'"]>
    </#if>
    <#if message.isSubscribed>
        <#local attributes+=["zserio.typeinfo.MemberAttribute.SUBSCRIBE : 'subscribe_${message.snakeCaseName}'"]>
    </#if>
                attributes={
    <#list attributes as attribute>
                    ${attribute}<#if attribute?has_next>,</#if>
    </#list>
                }
</#macro>

<#macro member_info_method_attributes method>
                attributes={
                    zserio.typeinfo.MemberAttribute.REQUEST_TYPE : <@type_info method.requestTypeInfo/>,
                    zserio.typeinfo.MemberAttribute.CLIENT_METHOD_NAME : '${method.clientMethodName}'
                }
</#macro>

<#macro member_info_compound_type_arguments compound>
    <#if compound.instantiatedParameters?has_content>
        [<#t>
        <#list compound.instantiatedParameters as instantiatedParameter>
            '${instantiatedParameter.expression}'<#if instantiatedParameter?has_next>, </#if><#t>
        </#list>
        ]<#t>
    </#if>
</#macro>

<#macro member_info_table_field_type_arguments field>
    [<#t>
    <#list field.parameters as parameter>
        '<#if parameter.isExplicit>explicit </#if>${parameter.expression}'<#if parameter?has_next>,</#if><#t>
    </#list>
    ]<#t>
</#macro>
