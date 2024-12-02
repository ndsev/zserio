<#macro type_info typeInfo>
    <#if typeInfo.typeInfoGetter??>
        ::zserio::BuiltinTypeInfo<allocator_type>::get${typeInfo.typeInfoGetter.suffix}(<#t>
                <#if typeInfo.typeInfoGetter.arg??>${typeInfo.typeInfoGetter.arg}</#if>)<#t>
    <#else>
        <#if typeInfo.isEnum>
        ::zserio::enumTypeInfo<${typeInfo.typeFullName}, allocator_type>()<#t>
        <#else>
        ${typeInfo.typeFullName}::typeInfo()<#t>
        </#if>
    </#if>
</#macro>

<#macro info_array_type infoType infoListSize>
    <#if infoListSize != 0>
        ::std::array<${infoType}, ${infoListSize}><#t>
    <#else>
<#--
We have to use Span instead of empty std::array, because MSVC complains that the infoType class doesn't have
user declared default constructor.
See https://developercommunity.visualstudio.com/t/Empty-std::array-constructor-requires-on/382468.
-->
        ::zserio::Span<${infoType}><#t>
    </#if>
</#macro>

<#macro field_info_array_var varName fieldList indent=1>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}static const <@info_array_type "::zserio::BasicFieldInfo<allocator_type>", fieldList?size/> ${varName}<#rt>
    <#if fieldList?has_content>
        <#lt> = {
        <#list fieldList as field>
        <@field_info field, field?has_next, indent+1/>
        </#list>
${I}};
    <#else>
        <#lt>;
    </#if>
</#macro>

<#macro parameter_info_array_var varName parameterList indent=1>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}static const <@info_array_type "::zserio::BasicParameterInfo<allocator_type>", parameterList?size/> ${varName}<#rt>
    <#if parameterList?has_content>
        <#lt> = {
        <#list parameterList as parameter>
        <@parameter_info parameter, parameter?has_next, indent+1/>
        </#list>
${I}};
    <#else>
        <#lt>;
    </#if>
</#macro>

<#macro function_info_array_var varName functionList indent=1>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}static const <@info_array_type "::zserio::BasicFunctionInfo<allocator_type>", functionList?size/> ${varName}<#rt>
    <#if functionList?has_content>
        <#lt> = {
        <#list functionList as function>
        <@function_info function, function?has_next, indent+1/>
        </#list>
${I}};
    <#else>
        <#lt>;
    </#if>
</#macro>

<#macro case_info_array_var varName caseMemberList defaultMember indent=1>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}static const <@info_array_type "::zserio::BasicCaseInfo<allocator_type>", caseMemberList?size + (defaultMember?has_content)?then(1, 0)/> ${varName}<#rt>
    <#if caseMemberList?has_content || defaultMember?has_content>
        <#lt> = {
        <@case_info_array caseMemberList, defaultMember, indent+1/>
${I}};
    <#else>
        <#lt>;
    </#if>
</#macro>

<#macro underlying_type_info_type_arguments_var varName bitSize indent=1>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}static const <@info_array_type "::zserio::StringView", (bitSize?has_content && bitSize.isDynamicBitField)?then(1, 0)/> ${varName}<#rt>
    <#if bitSize?has_content && bitSize.isDynamicBitField>
        <#lt> = {
        ::zserio::makeStringView("${bitSize.value?j_string}")
${I}};
    <#else>
        <#lt>;
    </#if>
</#macro>

<#macro item_info_array_var varName items indent=1>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}static const <@info_array_type "::zserio::ItemInfo", items?size/> ${varName}<#rt>
    <#if items?has_content>
        <#lt> = {
        <#list items as item>
        <@item_info item.schemaName, item.value, item.isDeprecated, item.isRemoved, item?has_next, indent+1/>
        </#list>
${I}};
    <#else>
        <#lt>;
    </#if>
</#macro>

<#macro field_info field comma indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}::zserio::BasicFieldInfo<allocator_type>{
${I}    ::zserio::makeStringView("${field.name}"), // schemaName
    <#if (field.optional?? && field.optional.isRecursive) || (field.array?? && field.array.elementIsRecursive)>
${I}    <@field_info_recursive_type_info_var_name field/>, // typeInfo
    <#elseif field.array??>
${I}    <@type_info field.array.elementTypeInfo/>, // typeInfo
    <#else>
${I}    <@type_info field.typeInfo/>, // typeInfo
    </#if>
    <#if field_info_type_arguments_count(field) != 0>
${I}    <@field_info_type_arguments_var_name field/>, // typeArguments
    <#else>
${I}    {}, // typeArguments
    </#if>
${I}    <#if field.isExtended>true<#else>false</#if>, // isExtended
${I}    <#if field.alignmentValue??>::zserio::makeStringView("${field.alignmentValue?j_string}")<#else>{}</#if>, // alignment
${I}    <#if field.offset??>::zserio::makeStringView("${field.offset.getter?j_string}")<#else>{}</#if>, // offset
<#-- We need to use j_string builtin here because initializer can be string literal with quotes. -->
${I}    <#if field.initializer??>::zserio::makeStringView("${field.initializer?j_string}")<#else>{}</#if>, // initializer
${I}    <#if field.optional??>true<#else>false</#if>, // isOptional
${I}    <#if field.optional?? && field.optional.clause??><#rt>
                <#lt>::zserio::makeStringView("${field.optional.clause?j_string}")<#else>{}</#if>, // optionalClause
${I}    <#if field.constraint??>::zserio::makeStringView("${field.constraint.writeConstraint?j_string}")<#else>{}</#if>, // constraint
${I}    <#if field.array??>true<#else>false</#if>, // isArray
${I}    <#if field.array?? && field.array.length??><#rt>
                <#lt>::zserio::makeStringView("${field.array.length?j_string}")<#else>{}</#if>, // arrayLength
${I}    <#if field.array?? && field.array.isPacked>true<#else>false</#if>, // isPacked
${I}    <#if field.array?? && field.array.isImplicit>true<#else>false</#if> // isImplicit
${I}}<#if comma>,</#if>
</#macro>

<#macro field_info_recursive_type_info_var_name field>
    ${field.name}RecursiveTypeInfo<#t>
</#macro>

<#macro field_info_recursive_type_info_var field>
    <#if field.optional?? && field.optional.isRecursive>
    static const ::zserio::RecursiveTypeInfo<allocator_type> <@field_info_recursive_type_info_var_name field/>(
            &${field.typeInfo.typeFullName}::typeInfo);
    <#elseif field.array?? && field.array.elementIsRecursive>
    static const ::zserio::RecursiveTypeInfo<allocator_type> <@field_info_recursive_type_info_var_name field/>(
            &${field.array.elementTypeInfo.typeFullName}::typeInfo);
    </#if>
</#macro>

<#function field_info_type_arguments_count field>
    <#if field.array??>
        <#if field.array.elementCompound??>
            <#return field.array.elementCompound.instantiatedParameters?size/>
        <#elseif field.array.elementBitSize?? && field.array.elementBitSize.isDynamicBitField>
            <#return 1/>
        </#if>
    <#elseif field.compound??>
        <#return field.compound.instantiatedParameters?size/>
    <#elseif field.bitSize?? && field.bitSize.isDynamicBitField>
        <#return 1/>
    </#if>
    <#return 0/>
</#function>

<#macro field_info_type_arguments_var_name field>
    ${field.name}TypeArguments<#t>
</#macro>

<#macro field_info_type_arguments_var field>
    <#local count=field_info_type_arguments_count(field)>
    <#if count != 0>
    static const ::std::array<::zserio::StringView, ${count}> <@field_info_type_arguments_var_name field/> = {
        <#if field.array??>
            <#if field.array.elementCompound??>
        <@field_info_compound_type_arguments field.array.elementCompound.instantiatedParameters/>
            <#elseif field.array.elementBitSize?? && field.array.elementBitSize.isDynamicBitField>
        ::zserio::makeStringView("${field.array.elementBitSize.value?j_string}")
            </#if>
        <#elseif field.compound??>
        <@field_info_compound_type_arguments field.compound.instantiatedParameters/>
        <#elseif field.bitSize?? && field.bitSize.isDynamicBitField>
        ::zserio::makeStringView("${field.bitSize.value?j_string}")
        </#if>
    };
    </#if>
</#macro>

<#macro field_info_compound_type_arguments parameters>
    <#list parameters as parameter>
        ::zserio::makeStringView("${parameter.expression?j_string}")<#if parameter?has_next>, </#if>
    </#list>
</#macro>

<#macro parameter_info parameter comma indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}::zserio::BasicParameterInfo<allocator_type>{
${I}    ::zserio::makeStringView("${parameter.name}"),
${I}    <@type_info parameter.typeInfo/>
${I}}<#if comma>,</#if>
</#macro>

<#macro function_info function comma indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}::zserio::BasicFunctionInfo<allocator_type>{
${I}    ::zserio::makeStringView("${function.schemaName}"),
${I}    <@type_info function.returnTypeInfo/>,
${I}    ::zserio::makeStringView("${function.resultExpression?j_string}")
${I}}<#if comma>,</#if>
</#macro>

<#macro case_info_case_expressions_var_name index>
    case${index}Expressions<#t>
</#macro>

<#macro case_info_case_expressions_var caseMember index indent=1>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}static const std::array<::zserio::StringView, ${caseMember.expressionList?size}> <#rt>
            <#lt><@case_info_case_expressions_var_name index/> = {
    <#list caseMember.expressionList as caseExpression>
${I}    ::zserio::makeStringView("${caseExpression?j_string}")<#if caseExpression?has_next>,</#if>
    </#list>
${I}};
</#macro>

<#macro case_info_array caseMemberList defaultMember indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local fieldIndex=0>
    <#list caseMemberList as caseMember>
${I}::zserio::BasicCaseInfo<allocator_type>{
${I}    <@case_info_case_expressions_var_name caseMember?index/>,
${I}    <#if caseMember.compoundField??>&fields[${fieldIndex}]<#local fieldIndex+=1><#else>nullptr</#if>
${I}}<#if caseMember?has_next || defaultMember?has_content>,</#if>
    </#list>
    <#if defaultMember?has_content>
${I}::zserio::BasicCaseInfo<allocator_type>{
${I}    {}, <#if defaultMember.compoundField??>&fields[${fieldIndex}]<#local fieldIndex+=1><#else>nullptr</#if>
${I}}
    </#if>
</#macro>

<#macro item_info name value isDeprecated isRemoved comma indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}::zserio::ItemInfo{ ::zserio::makeStringView("${name}"), <#rt>
        <#lt>static_cast<uint64_t>(${value}), <#if isDeprecated>true<#else>false</#if>, <#rt>
        <#lt><#if isRemoved>true<#else>false</#if>}<#if comma>,</#if>
</#macro>

<#macro column_info field comma indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}::zserio::BasicColumnInfo<allocator_type>{
${I}    ::zserio::makeStringView("${field.name}"), // schemaName
${I}    <@type_info field.typeInfo/>, // typeInfo
    <#if column_info_type_arguments_count(field) != 0>
${I}    <@field_info_type_arguments_var_name field/>, // typeArguments
    <#else>
${I}    {}, // typeArguments
    </#if>
${I}    ::zserio::makeStringView("${field.sqlTypeData.name}"), // sqlTypeName
${I}    <#if field.sqlConstraint??>${field.sqlConstraint}<#else>{}</#if>, // sqlConstraint
${I}    <#if field.isVirtual>true<#else>false</#if>, // isVirtual
${I}}<#if comma>,</#if>
</#macro>

<#function column_info_type_arguments_count field>
    <#if field.bitSize?? && field.bitSize.isDynamicBitField>
        <#return 1/>
    <#else>
        <#return field.typeParameters?size/>
    </#if>
</#function>

<#macro column_info_type_arguments_var field indent=1>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local count=column_info_type_arguments_count(field)>
    <#if count != 0>
${I}static const ::std::array<::zserio::StringView, ${count}> <@field_info_type_arguments_var_name field/> = {
        <#if field.bitSize?? && field.bitSize.isDynamicBitField>
${I}    ::zserio::makeStringView("${field.bitSize.value?j_string}")
        <#else>
        <@column_info_compound_type_arguments field.typeParameters, indent+1/>
        </#if>
${I}};
    </#if>
</#macro>

<#macro column_info_compound_type_arguments parameters indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list parameters as parameter>
${I}    ::zserio::makeStringView("<#if parameter.isExplicit>explicit </#if>${parameter.expression?j_string}")<#if parameter?has_next>, </#if>
    </#list>
</#macro>

<#macro table_info field comma indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}::zserio::BasicTableInfo<allocator_type>{ <#rt>
        <#lt>::zserio::makeStringView("${field.name}"), <@type_info field.typeInfo/> }<#if comma>,</#if>
</#macro>

<#macro message_info message comma indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}::zserio::BasicMessageInfo<allocator_type>{
${I}    ::zserio::makeStringView("${message.name}"), // schemaName
${I}    <@type_info message.typeInfo/>, // typeInfo
${I}    <#if message.isPublished>true<#else>false</#if>, // isPublished
${I}    <#if message.isSubscribed>true<#else>false</#if>, // isSubscribed
${I}    ${message.topicDefinition}, // topic
${I}}<#if comma>,</#if>
</#macro>

<#macro method_info method comma indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}::zserio::BasicMethodInfo<allocator_type>{
${I}    ::zserio::makeStringView("${method.name}"), // schemaName
${I}    <@type_info method.responseTypeInfo/>, // responseTypeInfo
${I}    <@type_info method.requestTypeInfo/>, // requestTypeInfo
${I}}<#if comma>,</#if>
</#macro>

<#macro template_info_template_name_var varName, templateInstantiation>
    <#if templateInstantiation?has_content>
    static const ::zserio::StringView ${varName} = ::zserio::makeStringView("${templateInstantiation.templateName}");
    <#else>
    static const ::zserio::StringView ${varName};
    </#if>
</#macro>

<#macro template_info_template_arguments_var varName, templateInstantiation>
    <#if templateInstantiation?has_content>
    static const ::std::array<::zserio::BasicTemplateArgumentInfo<allocator_type><#rt>
            <#lt>, ${templateInstantiation.templateArgumentTypeInfos?size}> ${varName} = {
        <#list templateInstantiation.templateArgumentTypeInfos as typeInfo>
        ::zserio::BasicTemplateArgumentInfo<allocator_type>{<@type_info typeInfo/>}<#if typeInfo?has_next>,</#if>
        </#list>
    };
    <#else>
    <#-- See comment in info_array_type macro.  -->
    static const ::zserio::Span<::zserio::BasicTemplateArgumentInfo<allocator_type>> templateArguments;
    </#if>
</#macro>
