<#include "DocComment.inc.ftl">
<#macro field_member_name field>
    m_${field.name}_<#t>
</#macro>

<#macro field_storage_type_inner field>
    <#if field.array??>
        <#local storageType>
            ${field.array.elementTypeInfo.typeFullName}<#if field.array.elementCompound??>::Storage</#if><#t>
        </#local>
        <@vector_type_name storageType/><#t>
    <#else>
        ${field.typeInfo.typeFullName}<#if field.compound??>::Storage</#if><#t>
    </#if>
</#macro>

<#macro field_storage_type field>
    <#local storageType>
        <@field_storage_type_inner field/><#t>
    </#local>
    <#if field.optional??>
        <#local storageType>
            <#if field.optional.isRecursive>
                <@heap_optional_type_name storageType/><#t>
            <#else>
                ${types.inplaceOptionalHolder.name}<${storageType}><#t>
            </#if>
        </#local>
    </#if>
    ${storageType}<#t>
</#macro>

<#macro field_view_type field>
    <#if field.array??>
        <@array_typedef_name field/>
    <#else>
        <#if field.typeInfo.isCompound>
            ${field.typeInfo.typeFullName}::View<#t>
        <#else>
            ${field.typeInfo.typeFullName}<#t>
        </#if>
    </#if>
</#macro>

<#macro field_view_get field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local fieldName>
        <#if field.usesAnyHolder>
            any<#t>
        <#else>
            ${field.name}<#t>
        </#if>
    </#local>
    <#local getFromHolder>
        <#if field.usesAnyHolder>
            .get<<@field_storage_type field/>>()<#t>
        <#elseif field.optional??>
            .value()<#t>
        </#if>
    </#local>
    <#if field.typeInfo.isCompound>
${I}return <@field_view_type field/>(<#rt>
        <#if field.compound.instantiatedParameters?has_content>
            <#lt><@compound_field_compound_ctor_params field.compound, false/>,
${I}        m_storage.${fieldName}${getFromHolder});
        <#else>
            <#lt>m_storage.${fieldName}${getFromHolder});
        </#if>
    <#elseif field.array??>
${I}return <@array_typedef_name field/>(<#if array_needs_owner(field)>*this, </#if>m_storage.${fieldName}${getFromHolder});
    <#else>
${I}return m_storage.${fieldName}${getFromHolder};
    </#if>
</#macro>

<#macro field_view_read field indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if field.optional.clause??>
${I}if (${field.optional.clause})
        <#else>
${I}if (reader.readBool())
        </#if>
${I}{
        <@field_view_read_inner field, indent+1, packed/>
${I}}
    <#else>
    <@field_view_read_inner field, indent, packed/>
    </#if>
</#macro>

<#macro field_view_read_inner field indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local fieldName>
        <#if field.usesAnyHolder>
            any<#t>
        <#else>
            ${field.name}<#t>
        </#if>
    </#local>
    <#local getFromHolder>
        <#if field.usesAnyHolder>
            .uncheckedGet<<@field_storage_type_inner field/>>()<#t>
        <#elseif field.optional??>
            .uncheckedValue()<#t>
        </#if>
    </#local>
    <#local compoundParamsArguments>
        <#if field.compound??>
            <@compound_field_compound_ctor_params field.compound, false/>
        </#if>
    </#local>
    <#if field.alignmentValue??>
${I}reader.alignTo(${field.alignmentValue});
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}reader.alignTo(UINT32_C(8));
    </#if>
    <#if packed && uses_field_packing_context(field)>
        <#if field.compound??>
            <#if field.optional?? || field.usesAnyHolder>
${I}storage.${fieldName} = <@field_storage_type_inner field/>(allocator);
            </#if>
${I}<@field_view_type field/>(context.${field.getterName}(), reader, <#rt>
            <#if compoundParamsArguments?has_content>
            <#lt>${compoundParamsArguments},
${I}        storage.${fieldName}${getFromHolder}, allocator);
            <#else>
            <#lt>storage.${fieldName}${getFromHolder}, allocator);
            </#if>
        <#elseif field.typeInfo.isBitmask>
${I}storage.${fieldName} = <@field_view_type field/>(context.${field.getterName}(), reader);
        <#elseif field.typeInfo.isEnum>
${I}storage.${fieldName} = ::zserio::read<<@field_cpp_type_name field/>>(context.${field.getterName}(), reader);
        <#else>
${I}storage.${fieldName} = context.${field.getterName}().read<<@array_traits_type_name field/>>(<#if array_traits_needs_owner(field)>*this, </#if>reader);
        </#if>
    <#elseif field.typeInfo.isCompound>
        <#if field.optional?? || field.usesAnyHolder>
${I}storage.${fieldName} = <@field_storage_type_inner field/>(allocator);
        </#if>
${I}<@field_view_type field/>(reader, <#rt>
        <#if compoundParamsArguments?has_content>
            <#lt>${compoundParamsArguments},
${I}        storage.${fieldName}${getFromHolder}, allocator);
        <#else>
            <#lt>storage.${fieldName}${getFromHolder}, allocator);
        </#if>
    <#elseif field.runtimeFunction??>
        <#local readCommandArgs>
            ${field.runtimeFunction.arg!}<#if field.needsAllocator><#if field.runtimeFunction.arg??>, </#if>allocator</#if><#t>
        </#local>
${I}storage.${fieldName} = static_cast<<@field_cpp_type_name field/>>(reader.read${field.runtimeFunction.suffix}(${readCommandArgs}));
    <#elseif field.typeInfo.isEnum>
${I}storage.${fieldName} = ::zserio::read<<@field_cpp_type_name field/>>(reader);
    <#elseif !field.array??>
        <#-- bitmask -->
${I}storage.${fieldName} = <@field_cpp_type_name field/>(reader);
    </#if>
    <#if field.array??>
        <#if field.optional?? || field.usesAnyHolder>
${I}storage.${fieldName} = <@field_storage_type_inner field/>(allocator);
        </#if>
${I}<@array_typedef_name field/>(<#lt><#if array_needs_owner(field)>*this, </#if><#rt>
        <#lt>storage.${fieldName}${getFromHolder}, reader<#rt>
        <@array_field_packed_tag field, packed/><#t>
        <#lt><#if field.array.length??>, static_cast<size_t>(${field.array.length})</#if>);
    </#if>
</#macro>

<#macro field_argument_name field>
    ${field.name}_<#t>
</#macro>

<#macro field_cpp_type_name field>
    <#if field.array??>
        <@array_typedef_name field/><#t>
    <#else>
        ${field.typeInfo.typeFullName}<#t>
    </#if>
</#macro>

<#macro field_raw_cpp_type_name field>
    <#if field.array??>
        <@vector_type_name field.array.elementTypeInfo.typeFullName/><#t>
    <#else>
        ${field.typeInfo.typeFullName}<#t>
    </#if>
</#macro>

<#macro field_raw_cpp_argument_type_name field>
    <#if field.array??>
        const <@vector_type_name field.array.elementTypeInfo.typeFullName/>&<#t>
    <#elseif field.typeInfo.isSimple>
        ${field.typeInfo.typeFullName}<#t>
    <#else>
        const ${field.typeInfo.typeFullName}&<#t>
    </#if>
</#macro>

<#macro field_member_type_name field arrayClassPrefix="">
    <#local fieldCppTypeName>
        <#if field.array?? && arrayClassPrefix?has_content>${arrayClassPrefix}::</#if><@field_cpp_type_name field/><#t>
    </#local>
    <#if field.optional??>
        <#if field.optional.isRecursive>
            <@heap_optional_type_name fieldCppTypeName/><#t>
        <#else>
            ${types.inplaceOptionalHolder.name}<${fieldCppTypeName}><#t>
        </#if>
    <#else>
        ${fieldCppTypeName}<#t>
    </#if>
</#macro>

<#macro compound_field_less_than_compare compoundField lhs rhs>
    <#if !compoundField.optional?? && compoundField.typeInfo.isBoolean>
        static_cast<int>(${lhs}) < static_cast<int>(${rhs})<#t>
    <#else>
        ${lhs} < ${rhs}<#t>
    </#if>
</#macro>

<#macro compound_read_field field compoundName indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if field.optional.clause??>
${I}if (${field.optional.clause})
        <#else>
${I}if (in.readBool())
        </#if>
${I}{
        <@compound_read_field_inner field, compoundName, indent+1, packed/>
${I}}

${I}return <@field_member_type_name field/>(::zserio::NullOpt<#if field.holderNeedsAllocator>, allocator</#if>);
    <#else>
    <@compound_read_field_inner field, compoundName, indent, packed/>
    </#if>
</#macro>

<#macro compound_read_field_inner field compoundName indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <@compound_read_field_prolog field, compoundName, indent/>
    <#if packed && uses_field_packing_context(field)>
        <#if field.compound?? || field.typeInfo.isBitmask>
            <#local compoundParamsArguments>
                <#if field.compound??>
                    <@compound_field_compound_ctor_params field.compound, false/>
                </#if>
            </#local>
            <#local constructorArguments>
                in<#if compoundParamsArguments?has_content>, ${compoundParamsArguments}</#if><#t>
                <#if field.compound??>, allocator</#if><#t>
            </#local>
            <#local readCommand><@field_cpp_type_name field/>(context.${field.getterName}(), ${constructorArguments})</#local>
        <#elseif field.typeInfo.isEnum>
            <#local readCommand>::zserio::read<<@field_cpp_type_name field/>>(context.${field.getterName}(), in)</#local>
        <#else>
            <#local readCommand>context.${field.getterName}().read<<@array_traits_type_name field/>>(<#if array_traits_needs_owner(field)>*this, </#if>in)</#local>
        </#if>
    <#elseif field.runtimeFunction??>
        <#local readCommandArgs>
            ${field.runtimeFunction.arg!}<#if field.needsAllocator><#if field.runtimeFunction.arg??>, </#if>allocator</#if><#t>
        </#local>
        <#local readCommand><#lt>static_cast<<@field_cpp_type_name field/>>(in.read${field.runtimeFunction.suffix}(${readCommandArgs}))</#local>
    <#elseif field.typeInfo.isEnum>
        <#local readCommand>::zserio::read<<@field_cpp_type_name field/>>(in)</#local>
    <#elseif field.compound??>
        <#local compoundParamsArguments>
            <@compound_field_compound_ctor_params field.compound, false/>
        </#local>
        <#local constructorArguments>
            in<#if compoundParamsArguments?has_content>, ${compoundParamsArguments}</#if>, allocator<#t>
        </#local>
        <#local readCommand><@field_cpp_type_name field/>(${constructorArguments})</#local>
    <#elseif !field.array??>
        <#-- bitmask -->
        <#local readCommand><@field_cpp_type_name field/>(in)</#local>
    </#if>
    <#if field.array??>
${I}<@array_typedef_name field/> readField(allocator);
${I}readField.read<@array_field_packed_suffix field, packed/>(<#if array_needs_owner(field)>*this, </#if>in<#rt>
        <#lt><#if field.array.length??>, static_cast<size_t>(${field.array.length})</#if>);
    <@compound_check_constraint_field field, name, "Read", indent/>

${I}return <@compound_read_field_retval field, "readField", true/>;
    <#elseif field.constraint??>
${I}<@field_cpp_type_name field/> readField = ${readCommand};
    <@compound_check_constraint_field field, name, "Read", indent/>

${I}return <@compound_read_field_retval field, "readField", true/>;
    <#else>
${I}return <@compound_read_field_retval field, readCommand, false/>;
    </#if>
</#macro>

<#macro compound_read_field_retval field readCommand needsMove>
    <#if field.usesAnyHolder>
        ${types.anyHolder.name}(<#if needsMove>::std::move(</#if>${readCommand}<#if needsMove>)</#if>, allocator)<#t>
    <#elseif field.optional??>
        <#local fieldCppTypeName><@field_cpp_type_name field/></#local>
        <#if field.optional.isRecursive>
            <@heap_optional_type_name fieldCppTypeName/>(<#t>
                    <#lt><#if needsMove>::std::move(</#if>${readCommand}<#if needsMove>)</#if>, allocator)<#t>
        <#else>
            ${types.inplaceOptionalHolder.name}<${fieldCppTypeName}>(<#t>
                    <#lt><#if needsMove>::std::move(</#if>${readCommand}<#if needsMove>)</#if>)<#t>
        </#if>
    <#else>
        ${readCommand}<#t>
    </#if>
</#macro>

<#macro compound_field_compound_ctor_params compound useIndirectExpression>
    <#list compound.instantiatedParameters as instantiatedParameter>
        <#if instantiatedParameter.typeInfo.isSimple>static_cast<${instantiatedParameter.typeInfo.typeFullName}>(</#if><#t>
            <#if useIndirectExpression>
                ${instantiatedParameter.indirectExpression}<#t>
             <#else>
                ${instantiatedParameter.expression}<#t>
             </#if>
        <#if instantiatedParameter.typeInfo.isSimple>)</#if><#t>
        <#if instantiatedParameter?has_next>, </#if><#t>
    </#list>
</#macro>

<#macro compound_read_field_prolog field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}in.alignTo(${field.alignmentValue});
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
    <@compound_check_offset_field field, compoundName, "Read", "in", indent/>
    </#if>
</#macro>

<#macro compound_check_offset_field field compoundName actionName streamObjectName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}${streamObjectName}.alignTo(UINT32_C(8));
${I}// check offset
${I}if (${streamObjectName}.getBitPosition() / 8 != (${field.offset.getter}))
${I}{
${I}    throw ::zserio::CppRuntimeException("${actionName}: Wrong offset for field ${compoundName}.${field.name}: ") <<
${I}            (${streamObjectName}.getBitPosition() / 8) << " != " << (${field.offset.getter}) << "!";
${I}}
</#macro>

<#macro field_optional_condition field>
    <#if field.optional.clause??>${field.optional.clause}<#else>${field.optional.isSetIndicatorName}()</#if><#t>
</#macro>

<#macro compound_write_field field compoundName indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isExtended>
${I}if (${field.isPresentIndicatorName}())
${I}{
${I}    writer.alignTo(UINT32_C(8));
        <@compound_write_field_optional field, compoundName, indent+1, packed/>
${I}}
    <#else>
    <@compound_write_field_optional field, compoundName, indent, packed/>
    </#if>
</#macro>

<#macro compound_write_field_optional field compoundName indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if (<@field_optional_condition field/>)
${I}{
        <#if !field.optional.clause??>
${I}    writer.writeBool(true);
        </#if>
        <@compound_write_field_inner field, compoundName, indent+1, packed/>
${I}}
        <#if !field.optional.clause??>
${I}else
${I}{
${I}    writer.writeBool(false);
${I}}
        </#if>
    <#else>
    <@compound_write_field_inner field, compoundName, indent, packed/>
    </#if>
</#macro>

<#macro compound_write_field_inner field compoundName indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <@compound_write_field_prolog field, compoundName, indent/>
    <#if packed && uses_field_packing_context(field)>
        <#if field.compound?? || field.typeInfo.isBitmask>
${I}${field.getterName}().write(context.${field.getterName}(), writer);
        <#elseif field.typeInfo.isEnum>
${I}::zserio::write(context.${field.getterName}(), writer, ${field.getterName}());
        <#else>
${I}context.${field.getterName}().write<<@array_traits_type_name field/>>(<#rt>
        <#lt><#if array_traits_needs_owner(field)>*this, </#if>writer, ${field.getterName}());
        </#if>
    <#elseif field.runtimeFunction??>
${I}writer.write${field.runtimeFunction.suffix}(${field.getterName}()<#if field.runtimeFunction.arg??>,<#rt>
        <#lt> ${field.runtimeFunction.arg}</#if>);
    <#elseif field.typeInfo.isEnum>
${I}::zserio::write(writer, ${field.getterName}());
    <#elseif field.array??>
${I}${field.getterName}().write<@array_field_packed_suffix field, packed/>(writer);
    <#else>
${I}${field.getterName}().write(writer);
    </#if>
</#macro>

<#macro compound_write_field_prolog field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}out.alignTo(${field.alignmentValue});
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
    <@compound_check_offset_field field, compoundName, "Write", "out", indent/>
    </#if>
    <@compound_check_constraint_field field, compoundName, "Write", indent/>
    <@compound_check_array_length_field field, compoundName, indent/>
    <@compound_check_parameterized_field field, compoundName, indent/>
    <@compound_check_range_field field, compoundName, indent/>
</#macro>

<#macro compound_check_constraint_field field compoundName actionName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.constraint??>
        <#local constraintExpresssion><#if actionName=="Read">${field.constraint.readConstraint}<#else><#rt>
                <#lt>${field.constraint.writeConstraint}</#if></#local>
${I}// check constraint
${I}if (!(${constraintExpresssion}))
${I}    throw ::zserio::ConstraintException("${actionName}: Constraint violated at ${compoundName}.${field.name}!");
    </#if>
</#macro>

<#macro compound_check_array_length_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array?? && field.array.length??>
${I}// check array length
${I}if (${field.getterName}().size() != static_cast<size_t>(${field.array.length}))
${I}{
${I}    throw ::zserio::CppRuntimeException("Write: Wrong array length for field ${compoundName}.${field.name}: ") <<
${I}            ${field.getterName}().size() << " != " <<
${I}            static_cast<size_t>(${field.array.length}) << "!";
${I}}
    </#if>
</#macro>

<#macro compound_check_parameterized_field field compoundName indent>
    <#--
    TODO[Mi-L@]: Not needed to check?
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.compound?? && field.compound.instantiatedParameters?has_content>
${I}// check parameters
        <#list field.compound.instantiatedParameters as instantiatedParameter>
            <#local parameter=field.compound.parameters.list[instantiatedParameter?index]/>
            <#if instantiatedParameter.typeInfo.isSimple>
                <#local instantiatedExpression>
                    static_cast<${instantiatedParameter.typeInfo.typeFullName}>(${instantiatedParameter.expression})<#t>
                </#local>
${I}if (${field.getterName}().${parameter.getterName}() != ${instantiatedExpression})
${I}{
${I}    throw ::zserio::CppRuntimeException("Write: Wrong parameter ${parameter.name} for field ${compoundName}.${field.name}: ") <<
${I}            ${field.getterName}().${parameter.getterName}() << " != " << ${instantiatedExpression} << "!";
${I}}
            <#else>
${I}if (&(${field.getterName}().${parameter.getterName}()) != &(${instantiatedParameter.expression}))
${I}{
${I}    throw ::zserio::CppRuntimeException("Write: Inconsistent parameter ${parameter.name} for field ${compoundName}.${field.name}!");
${I}}
            </#if>
        </#list>
    </#if>
    -->
</#macro>

<#macro compound_check_range_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if withRangeCheckCode>
        <#if field.integerRange??>
${I}// check range
            <#local fieldValue><@compound_get_field field/></#local>
${I}{
        <@compound_check_range_value fieldValue, field.name, compoundName, field.typeInfo.typeFullName,
                field.integerRange, indent+1/>
${I}}
        <#elseif field.array?? && field.array.elementIntegerRange??>
${I}// check ranges
${I}for (auto value : <@compound_get_field field/>.getRawArray())
${I}{
        <@compound_check_range_value "value", field.name, compoundName, field.array.elementTypeInfo.typeFullName,
                field.array.elementIntegerRange, indent+1/>
${I}}
        </#if>
    </#if>
</#macro>

<#macro compound_check_range_value value valueName compoundName typeName integerRange indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if integerRange.bitFieldLength??>
${I}const size_t bitFieldLength = static_cast<size_t>(${integerRange.bitFieldLength});
${I}const ${typeName} lowerBound = static_cast<${typeName}><#rt>
        <#lt>(::zserio::getBitFieldLowerBound(bitFieldLength, <#if integerRange.typeInfo.isSigned>true<#else>false</#if>));
${I}const ${typeName} upperBound = static_cast<${typeName}><#rt>
        <#lt>(::zserio::getBitFieldUpperBound(bitFieldLength, <#if integerRange.typeInfo.isSigned>true<#else>false</#if>));
    <#else>
${I}const ${typeName} lowerBound = ${integerRange.lowerBound};
${I}const ${typeName} upperBound = ${integerRange.upperBound};
    </#if>
${I}if (<#if integerRange.checkLowerBound>${value} < lowerBound || </#if>${value} > upperBound)
${I}{
${I}    throw ::zserio::CppRuntimeException("Value ") << ${value} <<
${I}            " of ${compoundName}.${valueName} exceeds the range of <" <<
${I}            lowerBound << ".." << upperBound << ">!";
${I}}
</#macro>

<#macro array_field_packed_tag field packed>
    <#if field.isPackable && (packed || field.array.isPacked)>
        , ::zserio::PackedTag()<#t>
    </#if>
</#macro>

<#macro array_field_packed_suffix field packed>
    <#if field.isPackable && (packed || field.array.isPacked)>
        Packed<#t>
    </#if>
</#macro>

<#macro array_typedef_name field>
    ZserioArrayType_${field.name}<#t>
</#macro>

<#macro array_type_name field>
    <#local storageType>
        ${field.array.elementTypeInfo.typeFullName}<#if field.array.elementCompound??>::Storage</#if><#t>
    </#local>
    ${field.typeInfo.typeFullName}<<#t>
            <@vector_type_name storageType/>, <@array_traits_type_name field/>, <#t>
            <@array_type_enum field/><#t>
    <#if needs_array_expressions(field)>
            , <@array_expressions_name field.name/><#t>
    </#if>
    ><#t>
</#macro>

<#macro array_traits_type_name field>
    <#if field.array??>
        ${field.array.traits.name}<#t>
        <#if field.array.traits.isTemplated>
                <${field.array.elementTypeInfo.typeFullName}<#t>
                <#if field.array.traits.requiresElementFixedBitSize>, ${field.array.elementBitSize.value}</#if><#t>
                <#if field.array.traits.requiresElementDynamicBitSize>, <@element_bit_size_name field.name/></#if><#t>
                <#if field.array.traits.requiresElementFactory>, <@element_factory_name field.name/></#if>><#t>
        </#if>
    <#else>
        ${field.typeInfo.arrayTraits.name}<#t>
        <#if field.typeInfo.arrayTraits.isTemplated>
                <${field.typeInfo.typeFullName}<#t>
                <#if field.typeInfo.arrayTraits.requiresElementFixedBitSize>, ${field.bitSize.value}</#if><#t>
                <#if field.typeInfo.arrayTraits.requiresElementDynamicBitSize>, <@element_bit_size_name field.name/></#if><#t>
                <#if field.typeInfo.arrayTraits.requiresElementFactory>, <@element_factory_name field.name/></#if>><#t>
        </#if>
    </#if>
</#macro>

<#function array_traits_needs_owner field>
    <#return (field.typeInfo.arrayTraits.requiresElementDynamicBitSize && needs_element_bit_size_owner(field)) ||
            field.typeInfo.arrayTraits.requiresElementFactory>
</#function>

<#function array_needs_owner field>
    <#return (field.array.traits.requiresElementDynamicBitSize && needs_element_bit_size_owner(field)) ||
            field.array.traits.requiresElementFactory ||
            needs_array_expressions(field)>
</#function>

<#macro array_type_enum field>
    ::zserio::ArrayType::<#t>
    <#local arrayType="">
    <#if field.offset?? && field.offset.containsIndex>
        <#local arrayType="ALIGNED">
    </#if>
    <#if !field.array.length??>
        <#if field.array.isImplicit>
            <#local arrayType="IMPLICIT">
        <#else>
            <#if arrayType?has_content><#local arrayType+="_AUTO"><#else><#local arrayType="AUTO"></#if>
        </#if>
    </#if>
    <#if !arrayType?has_content>
        <#local arrayType="NORMAL">
    </#if>
    ${arrayType}<#t>
</#macro>

<#function needs_field_offset_checker field>
    <#if field.array?? && field.offset?? && field.offset.containsIndex>
        <#return true>
    </#if>
    <#return false>
</#function>

<#function needs_array_expressions field>
    <#return needs_field_offset_checker(field) || (field.array?? && field.array.elementCompound?? &&
                (needs_field_initialization(field.array.elementCompound) ||
                        field.array.elementCompound.needsChildrenInitialization))>
</#function>

<#macro array_expressions_name fieldName>
    ZserioArrayExpressions_${fieldName}<#t>
</#macro>

<#macro declare_array_expressions compoundName field>
    class <@array_expressions_name field.name/>
    {
    public:
        using OwnerType = ${compoundName}::View;

    <#if needs_field_offset_checker(field)>
        static void checkOffset(const OwnerType& owner, size_t index, size_t byteOffset);
        <#if withWriterCode>
        static void initializeOffset(OwnerType& owner, size_t index, size_t byteOffset);
        </#if>
    </#if>
    };

</#macro>

<#macro define_array_expressions_methods compoundName field>
    <#if needs_field_offset_checker(field)>

void ${compoundName}::<@array_expressions_name field.name/>::checkOffset(const OwnerType& owner,
        size_t index, size_t byteOffset)
{
    if (byteOffset != (${field.offset.indirectGetter}))
    {
        throw ::zserio::CppRuntimeException("Wrong offset for field ${compoundName}.${field.name}: ") <<
                byteOffset << " != " << (${field.offset.indirectGetter}) << "!";
    }
}
        <#if withWriterCode>

void ${compoundName}::<@array_expressions_name field.name/>::initializeOffset(OwnerType& owner,
        size_t index, size_t byteOffset)
{
    const ${field.offset.typeInfo.typeFullName} value = static_cast<${field.offset.typeInfo.typeFullName}>(byteOffset);
    ${field.offset.indirectSetter};
}
        </#if>
    </#if>
</#macro>

<#function needs_field_element_factory field>
    <#return field.array?? && field.array.traits.requiresElementFactory>
</#function>

<#macro element_factory_name fieldName>
    ZserioElementFactory_${fieldName}<#t>
</#macro>

<#macro declare_element_factory compoundName field>
    class <@element_factory_name field.name/>
    {
    public:
        using OwnerType = ${compoundName}::View;

        static void create(<#if !withWriterCode>const </#if>OwnerType& owner,
                <@field_storage_type_inner field/>& array,
                ::zserio::BitStreamReader& in, size_t index);

        static ${field.array.elementTypeInfo.typeFullName}::View at(const OwnerType& owner,
                const ${field.array.elementTypeInfo.typeFullName}::Storage& element, size_t index);
    <#if field.isPackable && field.array.elementUsedInPackedArray>

        static void create(<#if !withWriterCode>const </#if>OwnerType& owner,
                <@field_storage_type_inner field/>& array,
                ${field.array.elementTypeInfo.typeFullName}::ZserioPackingContext& context,
                ::zserio::BitStreamReader& in, size_t index);
    </#if>
    };

</#macro>

<#macro define_element_factory_methods compoundName field>
    <#local extraConstructorArguments>
        <#if field.array.elementCompound??>
            <@compound_field_compound_ctor_params field.array.elementCompound, true/><#t>
        </#if>
    </#local>

void ${compoundName}::<@element_factory_name field.name/>::create(<#rt>
        <#if !withWriterCode>const </#if>OwnerType&<#t>
        <#lt><#if needs_field_initialization_owner(field.array.elementCompound)> owner</#if>,
        <@field_storage_type_inner field/>& array,
        ::zserio::BitStreamReader& in, size_t<#rt>
        <#lt><#if needs_field_initialization_index(field.array.elementCompound)> index</#if>)
{
    ${field.array.elementTypeInfo.typeFullName}::View(in<#rt>
    <#if extraConstructorArguments?has_content>
            , ${extraConstructorArguments}<#t>
    </#if>
            <#lt>, array.emplace_back(allocator_type(array.get_allocator())), array.get_allocator());
}

${field.array.elementTypeInfo.typeFullName}::View ${compoundName}::<@element_factory_name field.name/>::at(
        const OwnerType& owner, const ${field.array.elementTypeInfo.typeFullName}::Storage& element, size_t index)
{
    return ${field.array.elementTypeInfo.typeFullName}::View(
            <#if extraConstructorArguments?has_content>${extraConstructorArguments}, </#if>
            element);
}

    <#if field.isPackable && field.array.elementUsedInPackedArray>
void ${compoundName}::<@element_factory_name field.name/>::create(<#rt>
        <#if !withWriterCode>const </#if>OwnerType&<#t>
        <#lt><#if needs_field_initialization_owner(field.array.elementCompound)> owner</#if>,
        <@field_storage_type_inner field/>& array,
        ${field.array.elementTypeInfo.typeFullName}::ZserioPackingContext& context, ::zserio::BitStreamReader& in,
        size_t<#if needs_field_initialization_index(field.array.elementCompound)> index</#if>)
{
    ${field.array.elementTypeInfo.typeFullName}::View(context, in<#rt>
        <#if extraConstructorArguments?has_content>
            , ${extraConstructorArguments}<#t>
        </#if>
            <#lt>, array.emplace_back(allocator_type(array.get_allocator())), array.get_allocator());
}
    </#if>
</#macro>

<#function needs_field_element_bit_size field>
    <#return (field.array?? && field.array.traits.requiresElementDynamicBitSize) ||
            (field.typeInfo.arrayTraits?? && field.typeInfo.arrayTraits.requiresElementDynamicBitSize)>
</#function>

<#macro element_bit_size_name fieldName>
    ZserioElementBitSize_${fieldName}<#t>
</#macro>

<#macro declare_element_bit_size compoundName field>
    <#local needsOwner=needs_element_bit_size_owner(field)>
    class <@element_bit_size_name field.name/>
    {
    public:
    <#if needsOwner>
        using OwnerType = ${compoundName}::View;

    </#if>
        static uint8_t get(<#if needsOwner>const OwnerType& owner</#if>);
    };

</#macro>

<#function needs_element_bit_size_owner field>
    <#if field.array??>
        <#return field.array.elementBitSize.needsOwner>
    <#else>
        <#return field.bitSize.needsOwner>
    </#if>
</#function>

<#macro define_element_bit_size_methods compoundName field>

uint8_t ${compoundName}::<@element_bit_size_name field.name/>::get(<#rt>
        <#lt><#if needs_element_bit_size_owner(field)>const OwnerType& owner</#if>)
{
    <#if field.array??>
    return static_cast<uint8_t>(${field.array.elementBitSize.ownerIndirectValue});
    <#else>
    return static_cast<uint8_t>(${field.bitSize.ownerIndirectValue});
    </#if>
}
</#macro>

<#macro inner_classes_declaration compoundName fieldList>
    <#list fieldList as field>
        <#if needs_array_expressions(field)>
    <@declare_array_expressions compoundName, field/>
        </#if>
        <#if needs_field_element_factory(field)>
    <@declare_element_factory compoundName, field/>
        </#if>
        <#if needs_field_element_bit_size(field)>
    <@declare_element_bit_size compoundName, field/>
        </#if>
    </#list>
</#macro>

<#macro inner_classes_definition compoundName fieldList>
    <#list fieldList as field>
        <#if needs_array_expressions(field)>
<@define_array_expressions_methods compoundName, field/>
        </#if>
        <#if needs_field_element_factory(field)>
<@define_element_factory_methods compoundName, field/>
        </#if>
        <#if needs_field_element_bit_size(field)>
<@define_element_bit_size_methods compoundName, field/>
        </#if>
    </#list>
</#macro>

<#function has_inner_classes compoundName fieldList>
    <#local innerClasses><@inner_classes_declaration compoundName, fieldList/></#local>

    <#return innerClasses?has_content>
</#function>

<#macro arrays_typedefs fieldList>
    <#local has_array_field=false/>
    <#list fieldList as field>
        <#if field.array??>
            <#local has_array_field=true/>
    using <@array_typedef_name field/> = <@array_type_name field/>;
        </#if>
    </#list>
    <#if has_array_field>

    </#if>
</#macro>

<#macro private_section_declarations compoundName fieldList>
    <@inner_classes_declaration compoundName, fieldList/>
    <@arrays_typedefs fieldList/>
</#macro>

<#macro compound_align_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}endBitPosition = ::zserio::alignTo(${field.alignmentValue}, endBitPosition);
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}endBitPosition = ::zserio::alignTo(8, endBitPosition);
    </#if>
</#macro>

<#macro compound_bitsizeof_field field indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isExtended>
${I}if (${field.isPresentIndicatorName}())
${I}{
${I}    endBitPosition = ::zserio::alignTo(UINT8_C(8), endBitPosition);
        <@compound_bitsizeof_field_optional field, indent+1, packed/>
${I}}
    <#else>
    <@compound_bitsizeof_field_optional field, indent, packed/>
    </#if>
</#macro>

<#macro compound_bitsizeof_field_optional field indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if !field.optional.clause??>
            <#-- auto optional field -->
${I}endBitPosition += 1;
        </#if>
${I}if (<@field_optional_condition field/>)
${I}{
        <@compound_bitsizeof_field_inner field, indent+1, packed/>
${I}}
        <#else>
    <@compound_bitsizeof_field_inner field, indent, packed/>
        </#if>
</#macro>

<#macro compound_bitsizeof_field_inner field indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <@compound_align_field field, indent/>
    <#if packed && uses_field_packing_context(field)>
        <#if field.compound?? || field.typeInfo.isBitmask>
${I}endBitPosition += ${field.getterName}().bitSizeOf(context.${field.getterName}(), endBitPosition);
        <#elseif field.typeInfo.isEnum>
${I}endBitPosition += ::zserio::bitSizeOf(context.${field.getterName}(), ${field.getterName}());
        <#else>
${I}endBitPosition += context.${field.getterName}().bitSizeOf<<@array_traits_type_name field/>>(<#rt>
        <#lt><#if array_traits_needs_owner(field)>*this, </#if>${field.getterName}());
        </#if>
    <#elseif field.typeInfo.isEnum>
${I}endBitPosition += ::zserio::bitSizeOf(${field.getterName}());
    <#elseif field.bitSize??>
        <#if field.bitSize.isDynamicBitField>
${I}endBitPosition += static_cast<uint8_t>(${field.bitSize.value});
        <#else>
${I}endBitPosition += ${field.bitSize.value};
        </#if>
    <#elseif field.runtimeFunction??>
${I}endBitPosition += ::zserio::bitSizeOf${field.runtimeFunction.suffix}(${field.getterName}());
    <#elseif field.array??>
${I}endBitPosition += ${field.getterName}().bitSizeOf<@array_field_packed_suffix field, packed/>(endBitPosition);
    <#else>
${I}endBitPosition += ${field.getterName}().bitSizeOf(endBitPosition);
    </#if>
</#macro>

<#macro compound_initialize_offsets_field field indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isExtended>
${I}if (${field.isPresentIndicatorName}())
${I}{
${I}    endBitPosition = ::zserio::alignTo(UINT8_C(8), endBitPosition);
        <@compound_initialize_offsets_field_optional field, indent+1, packed/>
${I}}
    <#else>
    <@compound_initialize_offsets_field_optional field, indent, packed/>
    </#if>
</#macro>

<#macro compound_initialize_offsets_field_optional field indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if !field.optional.clause??>
            <#-- auto optional field -->
${I}endBitPosition += 1;
                </#if>
${I}if (<@field_optional_condition field/>)
${I}{
        <@compound_initialize_offsets_field_inner field, indent+1, packed/>
${I}}
    <#else>
    <@compound_initialize_offsets_field_inner field, indent, packed/>
    </#if>
</#macro>

<#macro compound_initialize_offsets_field_inner field indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <@compound_align_field field, indent/>
    <#if field.offset?? && !field.offset.containsIndex>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}{
${I}    const ${field.offset.typeInfo.typeFullName} value =
${I}            static_cast<${field.offset.typeInfo.typeFullName}>(endBitPosition / 8);
${I}    ${field.offset.setter};
${I}}
    </#if>
    <#if packed && uses_field_packing_context(field)>
        <#if field.compound?? || field.typeInfo.isBitmask>
${I}endBitPosition = <@compound_get_field field/>.initializeOffsets(context.${field.getterName}(), endBitPosition);
        <#elseif field.typeInfo.isEnum>
${I}endBitPosition = ::zserio::initializeOffsets(context.${field.getterName}(), endBitPosition,
        <@compound_get_field field/>);
        <#else>
${I}endBitPosition += context.${field.getterName}().bitSizeOf<<@array_traits_type_name field/>>(<#rt>
        <#lt><#if array_traits_needs_owner(field)>*this, </#if><@compound_get_field field/>);
        </#if>
    <#elseif field.typeInfo.isEnum>
${I}endBitPosition = ::zserio::initializeOffsets(endBitPosition, <@compound_get_field field/>);
    <#elseif field.bitSize??>
        <#if field.bitSize.isDynamicBitField>
${I}endBitPosition += static_cast<uint8_t>(${field.bitSize.value});
        <#else>
${I}endBitPosition += ${field.bitSize.value};
        </#if>
    <#elseif field.runtimeFunction??>
${I}endBitPosition += ::zserio::bitSizeOf${field.runtimeFunction.suffix}(<@compound_get_field field/>);
    <#elseif field.array??>
${I}endBitPosition = <@compound_get_field field/>.initializeOffsets<@array_field_packed_suffix field, packed/>(<#rt>
        <#lt><#if array_needs_owner(field)>*this, </#if>endBitPosition);
    <#else>
${I}endBitPosition = <@compound_get_field field/>.initializeOffsets(endBitPosition);
    </#if>
</#macro>

<#macro compound_field_accessors_declaration field>
    <#if withCodeComments>
    /**
     * Gets the value of the field ${field.name}.
     *
        <#if field.docComments??>
     * \b Description
     *
     <@doc_comments_inner field.docComments, 1/>
     *
        </#if>
     * \return Value of the field ${field.name}.
     */
    </#if>
    <@field_raw_cpp_argument_type_name field/> ${field.getterName}() const;
    <#if needs_field_getter(field)>
        <#if withCodeComments>

    /**
     * Gets the reference to the field ${field.name}.
     *
            <#if field.docComments??>
     * \b Description
     *
     <@doc_comments_inner field.docComments, 1/>
     *
            </#if>
     * \return Reference to the field ${field.name}.
     */
        </#if>
    <@field_raw_cpp_type_name field/>& ${field.getterName}();
    </#if>
    <#if needs_field_setter(field)>
        <#if withCodeComments>

    /**
     * Sets the field ${field.name}.
     *
            <#if field.docComments??>
     * \b Description
     *
     <@doc_comments_inner field.docComments, 1/>
     *
            </#if>
     * \param <@field_argument_name field/> Value of the field ${field.name} to set.
     */
        </#if>
    void ${field.setterName}(<@field_raw_cpp_argument_type_name field/> <@field_argument_name field/>);
    </#if>
    <#if needs_field_rvalue_setter(field)>
        <#if withCodeComments>

    /**
     * Sets the field ${field.name} using r-value.
     *
            <#if field.docComments??>
     * \b Description
     *
     <@doc_comments_inner field.docComments, 1/>
     *
            </#if>
     * \param <@field_argument_name field/> R-value of the field ${field.name} to set.
     */
        </#if>
    void ${field.setterName}(<@field_raw_cpp_type_name field/>&& <@field_argument_name field/>);
    </#if>
</#macro>

<#macro compound_get_field field>
    <#if field.usesAnyHolder>
        m_objectChoice.get<<@field_cpp_type_name field/>>()<#t>
    <#elseif field.optional??>
        <@field_member_name field/>.value()<#t>
    <#else>
        <@field_member_name field/><#t>
    </#if>
</#macro>

<#macro compound_setter_field_forward_value field>
    <#if field.array??>
        <#if field.optional??>
            ::zserio::createOptionalArray<<@array_typedef_name field/>><#t>
        <#else>
            <@array_typedef_name field/>
        </#if>
                    (::std::forward<ZSERIO_T_${field.name}>(<@field_argument_name field/>))<#t>
    <#else>
        ::std::forward<ZSERIO_T_${field.name}>(<@field_argument_name field/>)<#t>
    </#if>
</#macro>

<#macro compound_setter_field_value field>
    <#if field.array??>
        <@array_typedef_name field/>(<@field_argument_name field/>)<#t>
    <#else>
        <@field_argument_name field/><#t>
    </#if>
</#macro>

<#macro compound_setter_field_rvalue field>
    <#if field.array??>
        <@array_typedef_name field/>(std::move(<@field_argument_name field/>))<#t>
    <#else>
        ::std::move(<@field_argument_name field/>)<#t>
    </#if>
</#macro>

<#macro compound_copy_constructor_initializer_field field hasNext indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice(::zserio::NoInit, other.m_objectChoice)
    <#elseif has_field_no_init_tag(field)>
${I}<@field_member_name field/>(::zserio::NoInit, other.<@field_member_name field/>)<#if hasNext>,</#if>
    <#else>
${I}<@field_member_name field/>(other.<@field_member_name field/>)<#if hasNext>,</#if>
    </#if>
</#macro>

<#macro compound_move_constructor_initializer_field field hasNext indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice(::zserio::NoInit, ::std::move(other.m_objectChoice))
    <#elseif has_field_no_init_tag(field)>
${I}<@field_member_name field/>(::zserio::NoInit, ::std::move(other.<@field_member_name field/>))<#if hasNext>,</#if>
    <#else>
${I}<@field_member_name field/>(::std::move(other.<@field_member_name field/>))<#if hasNext>,</#if>
    </#if>
</#macro>

<#macro compound_assignment_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice.assign(::zserio::NoInit, other.m_objectChoice);
    <#elseif has_field_no_init_tag(field)>
${I}<@field_member_name field/>.assign(::zserio::NoInit, other.<@field_member_name field/>);
    <#else>
${I}<@field_member_name field/> = other.<@field_member_name field/>;
    </#if>
</#macro>

<#macro compound_move_assignment_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice.assign(::zserio::NoInit, ::std::move(other.m_objectChoice));
    <#elseif has_field_no_init_tag(field)>
${I}<@field_member_name field/>.assign(::zserio::NoInit, ::std::move(other.<@field_member_name field/>));
    <#else>
${I}<@field_member_name field/> = ::std::move(other.<@field_member_name field/>);
    </#if>
</#macro>

<#macro compound_allocator_propagating_copy_constructor_initializer_field field hasNext indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice(::zserio::NoInit, other.copyObject(allocator))
    <#elseif has_field_no_init_tag(field)>
${I}<@field_member_name field/>(::zserio::NoInit, ::zserio::allocatorPropagatingCopy(<#rt>
        <#lt>::zserio::NoInit, other.<@field_member_name field/>, allocator))<#if hasNext>,</#if>
    <#else>
${I}<@field_member_name field/>(::zserio::allocatorPropagatingCopy(<#rt>
        <#lt>other.<@field_member_name field/>, allocator))<#if hasNext>,</#if>
    </#if>
</#macro>

<#macro compound_initialize_children_declaration>
    <#if withCodeComments>
    /**
     * Initializes all fields of this Zserio object.
     *
     * This method sets all parameters for all fields recursively.
     */
    </#if>
    void initializeChildren();
</#macro>

<#macro compound_initialize_children_field field indent mayNotBeEmptyCommand=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.compound??>
        <#if needs_field_initialization(field.compound)>
            <#local initializeCommand><@compound_get_field field/>.initialize(<#rt>
                    <#lt><@compound_field_compound_ctor_params field.compound, false/>);</#local>
        <#elseif field.compound.needsChildrenInitialization>
            <#local initializeCommand><@compound_get_field field/>.initializeChildren();</#local>
        </#if>
    <#elseif field.array?? && field.array.elementCompound??>
        <#if needs_field_initialization(field.array.elementCompound)>
            <#local initializeCommand><@compound_get_field field/>.initializeElements(*this);</#local>
        <#elseif field.array.elementCompound.needsChildrenInitialization>
            <#local initializeCommand><@compound_get_field field/>.initializeElements(*this);</#local>
        </#if>
    </#if>
    <#if initializeCommand??>
        <#if field.optional??>
${I}if (<@field_optional_condition field/>)
${I}    ${initializeCommand}
        <#else>
${I}${initializeCommand}
        </#if>
    <#elseif mayNotBeEmptyCommand>
${I};
    </#if>
</#macro>

<#macro choice_tag_name field>
    CHOICE_${field.name}<#t>
</#macro>

<#macro compound_field_constructor_template_arg_list compoundName fieldList>
    <#local numTemplateArgs=0/>
    <#local firstTemplateArgName=""/>
    <#local templateArgList>
        <#list fieldList as field>
            <#if !field.typeInfo.isSimple || field.optional??>
                <#if numTemplateArgs != 0>
            <#lt>,
            typename ZSERIO_T_${field.name} = <@field_raw_cpp_type_name field/><#rt>
                <#else>
            typename ZSERIO_T_${field.name} = <@field_raw_cpp_type_name field/><#t>
                </#if>
                <#if numTemplateArgs == 0 && field?is_first>
                    <#local firstTemplateArgName="ZSERIO_T_${field.name}"/>
                </#if>
                <#local numTemplateArgs=numTemplateArgs+1/>
            </#if>
        </#list>
    </#local>
    <#if templateArgList?has_content>
        <#if firstTemplateArgName != "">
    template <${templateArgList},
            ::zserio::is_field_constructor_enabled_t<${firstTemplateArgName}, ${compoundName}, allocator_type> = 0>
        <#else>
    template <${templateArgList}>
        </#if>
    </#if>
</#macro>

<#macro compound_field_constructor_type_list fieldList indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list fieldList as field>
        <#if !field.typeInfo.isSimple || field.optional??>
${I}ZSERIO_T_${field.name}&& <#t>
        <#else>
${I}${field.typeInfo.typeFullName} <#t>
        </#if>
        <@field_argument_name field/><#t>
        <#if field?has_next>
        <#lt>,
        </#if>
    </#list>
</#macro>

<#macro compound_init_packing_context_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if uses_field_packing_context(field)>
        <#if field.isExtended>
${I}if (${field.isPresentIndicatorName}())
${I}{
        <@compound_init_packing_context_field_optional field, indent+1/>
${I}}
        <#else>
    <@compound_init_packing_context_field_optional field, indent/>
        </#if>
    </#if>
</#macro>

<#macro compound_init_packing_context_field_optional field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if (<@field_optional_condition field/>)
${I}{
        <@compound_init_packing_context_field_inner field, indent+1/>
${I}}
    <#else>
    <@compound_init_packing_context_field_inner field, indent/>
    </#if>
</#macro>

<#macro compound_init_packing_context_field_inner field indent>
    <#-- arrays are solved in compound_init_packing_context_field -->
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.compound?? || field.typeInfo.isBitmask>
${I}${field.getterName}().initPackingContext(context.${field.getterName}());
    <#elseif field.typeInfo.isEnum>
${I}::zserio::initPackingContext(context.${field.getterName}(), ${field.getterName}());
    <#else>
${I}context.${field.getterName}().init<<@array_traits_type_name field/>>(<#rt>
        <#lt><#if array_traits_needs_owner(field)>*this, </#if>${field.getterName}());
    </#if>
</#macro>

<#macro field_packing_context_type_name field>
    <#if field.compound??>
        ${field.typeInfo.typeFullName}::ZserioPackingContext<#t>
    <#elseif field.array?? && field.array.elementCompound??>
        ${field.array.elementTypeInfo.typeFullName}::ZserioPackingContext<#t>
    <#else>
        ::zserio::DeltaContext<#t>
    </#if>
</#macro>

<#macro compound_declare_packing_context fieldList hasChoiceTag=false>
    <#if withCodeComments>
    /** Defines context structure which keeps additional data needed for packed arrays during compression. */
    </#if>
    class ZserioPackingContext
    {
    <#if hasChoiceTag || uses_packing_context(fieldList)>
    public:
        <#if hasChoiceTag>
        ::zserio::DeltaContext& getChoiceTag()
        {
            return m_choiceTag;
        }

        </#if>
        <#list fieldList as field>
            <#if uses_field_packing_context(field)>
        <@field_packing_context_type_name field/>& ${field.getterName}()
        {
                <#if field.optional?? && field.optional.isRecursive>
            return *this;
                <#else>
            return <@field_member_name field/>;
                </#if>
        }

            </#if>
        </#list>
    private:
        <#if hasChoiceTag>
        ::zserio::DeltaContext m_choiceTag;
        </#if>
        <#list fieldList as field>
            <#if uses_field_packing_context(field) && !(field.optional?? && field.optional.isRecursive)>
        <@field_packing_context_type_name field/> <@field_member_name field/>;
            </#if>
        </#list>
    </#if>
    };
</#macro>

<#function has_optional_field fieldList>
    <#list fieldList as field>
        <#if field.optional??>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function has_optional_recursive_field fieldList>
    <#list fieldList as field>
        <#if field.optional?? && field.optional.isRecursive>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function has_optional_non_recursive_field fieldList>
    <#list fieldList as field>
        <#if field.optional?? && !field.optional.isRecursive>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function has_field_with_constraint fieldList>
    <#list fieldList as field>
        <#if field.constraint??>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function needs_field_initialization compound>
    <#if compound.instantiatedParameters?has_content>
        <#return true>
    </#if>
    <#return false>
</#function>

<#function needs_field_initialization_owner compound>
    <#if compound.instantiatedParameters?has_content>
        <#list compound.instantiatedParameters as instantiatedParameter>
            <#if instantiatedParameter.needsOwner>
                <#return true>
            </#if>
        </#list>
    </#if>
    <#return false>
</#function>

<#function needs_field_initialization_index compound>
    <#if compound.instantiatedParameters?has_content>
        <#list compound.instantiatedParameters as instantiatedParameter>
            <#if instantiatedParameter.needsIndex>
                <#return true>
            </#if>
        </#list>
    </#if>
    <#return false>
</#function>

<#function has_field_with_initialization fieldList>
    <#list fieldList as field>
        <#if field.compound??>
            <#if needs_field_initialization(field.compound)>
                <#return true>
            </#if>
        <#elseif field.array??>
            <#if field.array.elementCompound?? &&
                    needs_field_initialization(field.array.elementCompound)>
                <#return true>
            </#if>
        </#if>
    </#list>
    <#return false>
</#function>

<#function has_field_no_init_tag field>
    <#return (field.compound?? && needs_field_initialization(field.compound)) ||
            (field.array?? && field.array.elementCompound?? && needs_field_initialization(field.array.elementCompound))>
</#function>

<#function needs_field_any_write_check_code field compoundName indent>
    <#local checkCode>
        <#if field.offset?? && !field.offset.containsIndex>
    <@compound_check_offset_field field, compoundName, "Write", "out", indent/>
        </#if>
    <@compound_check_constraint_field field, compoundName, "Write", indent/>
    <@compound_check_parameterized_field field, compoundName, indent/>
    <@compound_check_array_length_field field, compoundName, indent/>
    <@compound_check_range_field field, compoundName, indent/>
    </#local>
    <#if checkCode == "">
        <#return false>
    </#if>

    <#return true>
</#function>

<#function needs_field_getter field>
    <#if withWriterCode && !field.typeInfo.isSimple>
        <#return true>
    </#if>
    <#return false>
</#function>

<#function needs_field_setter field>
    <#if withWriterCode>
        <#return true>
    </#if>
    <#return false>
</#function>

<#function needs_field_rvalue_setter field>
    <#if withWriterCode && !field.typeInfo.isSimple>
        <#return true>
    </#if>
    <#return false>
</#function>

<#function needs_field_read_local_variable field>
    <#if field.array?? || field.constraint??>
        <#return true>
    </#if>
    <#return false>
</#function>

<#function num_extended_fields fieldList>
    <#local numExtended=0/>
    <#list fieldList as field>
        <#if field.isExtended>
            <#local numExtended=numExtended+1/>
        </#if>
    </#list>
    <#return numExtended>
</#function>

<#function uses_field_packing_context field>
    <#if field.isPackable && !field.array??>
        <#return true>
    </#if>
    <#return false>
</#function>

<#function uses_packing_context fieldList>
    <#list fieldList as field>
        <#if uses_field_packing_context(field)>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>
