<#macro field_member_name field>
    m_${field.name}_<#t>
</#macro>

<#macro field_argument_name field>
    ${field.name}_<#t>
</#macro>

<#macro field_cpp_type_name field>
    <#if field.array??>
        <@array_typedef_name field/><#t>
    <#else>
        ${field.cppTypeName}<#t>
    </#if>
</#macro>

<#macro field_raw_cpp_type_name field>
    <#if field.array??>
        <@vector_type_name field.array.elementCppTypeName/><#t>
    <#else>
        ${field.cppTypeName}<#t>
    </#if>
</#macro>

<#macro field_raw_cpp_argument_type_name field>
    <#if field.array??>
        const <@vector_type_name field.array.elementCppTypeName/>&<#t>
    <#else>
        ${field.cppArgumentTypeName}<#t>
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

<#macro compound_read_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if field.optional.clause??>
${I}if (${field.optional.clause})
        <#else>
${I}if (in.readBool())
        </#if>
${I}{
        <@compound_read_field_inner field, compoundName, indent + 1/>
${I}}

${I}return <@field_member_type_name field/>(::zserio::NullOpt<#if field.holderNeedsAllocator>, allocator</#if>);
    <#else>
    <@compound_read_field_inner field, compoundName, indent/>
    </#if>
</#macro>

<#macro compound_read_field_inner field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <@compound_read_field_prolog field, compoundName, indent/>
    <#if field.runtimeFunction??>
        <#local readCommandArgs>
            ${field.runtimeFunction.arg!}<#if field.needsAllocator><#if field.runtimeFunction.arg??>, </#if>allocator</#if><#t>
        </#local>
        <#local readCommand><#lt>static_cast<<@field_cpp_type_name field/>>(in.read${field.runtimeFunction.suffix}(${readCommandArgs}))</#local>
    <#elseif field.isEnum>
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
${I}<@array_typedef_name field/> readField(<@array_traits field/>, allocator);
${I}readField.read(in<#rt>
        <#if field.array.length??>, static_cast<size_t>(${field.array.length})</#if><#t>
        <#if field.array.requiresElementFactory>, <@element_factory_name field.name/>(*this)</#if><#t>
        <#if field.offset?? && field.offset.containsIndex>, <@offset_checker_name field.name/>(*this)</#if><#t>
        <#lt>);
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
    <#list compound.instantiatedParameters as parameter>
        <#if parameter.isSimpleType>static_cast<${parameter.cppTypeName}>(</#if><#t>
                <#if useIndirectExpression>${parameter.indirectExpression}<#else>${parameter.expression}</#if><#t>
        <#if parameter.isSimpleType>)</#if><#t>
        <#if parameter?has_next>, </#if><#t>
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
${I}if (::zserio::bitsToBytes(${streamObjectName}.getBitPosition()) != (${field.offset.getter}))
${I}{
${I}    throw ::zserio::CppRuntimeException("${actionName}: Wrong offset for field ${compoundName}.${field.name}: ") +
${I}            ::zserio::bitsToBytes(${streamObjectName}.getBitPosition()) + " != " + ${field.offset.getter} + "!";
${I}}
</#macro>

<#macro compound_pre_write_actions needsChildrenInitialization hasFieldWithOffset>
    <#if needsChildrenInitialization>
    if ((preWriteAction & ::zserio::PRE_WRITE_INITIALIZE_CHILDREN) != 0)
        initializeChildren();
    </#if>
    <#if hasFieldWithOffset>
    if ((preWriteAction & ::zserio::PRE_WRITE_INITIALIZE_OFFSETS) != 0)
        initializeOffsets(out.getBitPosition());
    </#if>
</#macro>

<#macro field_optional_condition field>
    <#if field.optional.clause??>${field.optional.clause}<#else><@field_member_name field/>.hasValue()</#if><#t>
</#macro>

<#macro compound_write_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if (<@field_optional_condition field/>)
${I}{
        <#if !field.optional.clause??>
${I}    out.writeBool(true);
        </#if>
        <@compound_write_field_inner field, compoundName, indent + 1/>
${I}}
        <#if !field.optional.clause??>
${I}else
${I}{
${I}    out.writeBool(false);
${I}}
        </#if>
    <#else>
    <@compound_write_field_inner field, compoundName, indent/>
    </#if>
</#macro>

<#macro compound_write_field_inner field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <@compound_write_field_prolog field, compoundName, indent/>
    <#if field.runtimeFunction??>
${I}out.write${field.runtimeFunction.suffix}(<@compound_get_field field/><#if field.runtimeFunction.arg??>,<#rt>
        <#lt> ${field.runtimeFunction.arg}</#if>);
    <#elseif field.isEnum>
${I}::zserio::write(out, <@compound_get_field field/>);
    <#elseif field.array??>
${I}<@compound_get_field field/>.write(out<#rt>
        <#lt><#if field.offset?? && field.offset.containsIndex>, <@offset_checker_name field.name/>(*this)</#if>);
    <#else>
${I}<@compound_get_field field/>.write(out, ::zserio::NO_PRE_WRITE_ACTION);
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
${I}if (<@compound_get_field field/>.getRawArray().size() != static_cast<size_t>(${field.array.length}))
${I}{
${I}    throw ::zserio::CppRuntimeException("Write: Wrong array length for field ${compoundName}.${field.name}: ") +
${I}            <@compound_get_field field/>.getRawArray().size() + " != " +
${I}            static_cast<size_t>(${field.array.length}) + "!";
${I}}
    </#if>
</#macro>

<#macro compound_check_range_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.withRangeCheckCode>
        <#if field.integerRange??>
${I}// check range
            <#local fieldValue><@compound_get_field field/></#local>
${I}{
        <@compound_check_range_value fieldValue, field.name, compoundName, field.cppTypeName, field.integerRange,
                indent + 1/>
${I}}
        <#elseif field.array?? && field.array.elementIntegerRange??>
${I}// check ranges
${I}for (auto it = <@compound_get_field field/>.getRawArray().begin(); <#rt>
            <#lt>it != <@compound_get_field field/>.getRawArray().end(); ++it)
${I}{
        <@compound_check_range_value "*it", field.name, compoundName, field.array.elementCppTypeName,
                field.array.elementIntegerRange, indent + 1/>
${I}}
        </#if>
    </#if>
</#macro>

<#macro compound_check_range_value value valueName compoundName cppTypeName integerRange indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if integerRange.bitFieldLength??>
${I}const size_t bitFieldLength = static_cast<size_t>(${integerRange.bitFieldLength});
${I}const ${cppTypeName} lowerBound = static_cast<${cppTypeName}><#rt>
        <#lt>(::zserio::getBitFieldLowerBound(bitFieldLength, <#if integerRange.isSigned>true<#else>false</#if>));
${I}const ${cppTypeName} upperBound = static_cast<${cppTypeName}><#rt>
        <#lt>(::zserio::getBitFieldUpperBound(bitFieldLength, <#if integerRange.isSigned>true<#else>false</#if>));
    <#else>
${I}const ${cppTypeName} lowerBound = ${integerRange.lowerBound};
${I}const ${cppTypeName} upperBound = ${integerRange.upperBound};
    </#if>
${I}if (<#if integerRange.checkLowerBound>${value} < lowerBound || </#if>${value} > upperBound)
${I}    throw ::zserio::CppRuntimeException("Value ") + ${value} +
${I}            " of ${compoundName}.${valueName} exceeds the range of <" +
${I}            lowerBound + ".." + upperBound + ">!";
</#macro>

<#macro array_typedef_name field>
    ArrayType_${field.name}<#t>
</#macro>

<#macro array_type_name field>
    ${field.cppTypeName}<<#t>
            <@vector_type_name field.array.elementCppTypeName/>, <@array_traits_type_name field/>, <#t>
            <@array_type_enum field/><#t>
    <#if field.offset?? && field.offset.containsIndex>
            , <@offset_checker_name field.name/><#t>
            <#if field.withWriterCode>, <@offset_initializer_name field.name/></#if><#t>
    </#if>
    ><#t>
</#macro>

<#macro array_traits_type_name field>
    ${field.array.traitsName}<#t>
    <#if field.array.hasTemplatedTraits>
        <${field.array.elementCppTypeName}<#t>
        <#if field.array.requiresElementFactory>, <@element_factory_name field.name/></#if>><#t>
    </#if>
</#macro>

<#macro array_traits field>
    <@array_traits_type_name field/>
        (<#t>
    <#if field.array.elementBitSize??>
        <#if field.array.elementBitSize.isDynamicBitField>
            static_cast<uint8_t>(${field.array.elementBitSize.value})<#t>
        <#else>
            ${field.array.elementBitSize.value}<#t>
        </#if>
    </#if>
        )<#t>
</#macro>

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

<#macro offset_checker_name fieldName>
    OffsetChecker_${fieldName}<#t>
</#macro>

<#macro declare_offset_checker compoundName field>
    class <@offset_checker_name field.name/>
    {
    public:
        explicit <@offset_checker_name field.name/>(${compoundName}& owner);

        void checkOffset(size_t index, size_t byteOffset) const;

    private:
        ${compoundName}& m_owner;
    };
</#macro>

<#macro define_offset_checker_methods compoundName field>
${compoundName}::<@offset_checker_name field.name/>::<@offset_checker_name field.name/>(${compoundName}& owner) :
        m_owner(owner)
{}

void ${compoundName}::<@offset_checker_name field.name/>::checkOffset(size_t index, size_t byteOffset) const
{
    if (byteOffset != ${field.offset.indirectGetter})
    {
        throw ::zserio::CppRuntimeException("Wrong offset for field ${compoundName}.${field.name}: ") +
                byteOffset + " != " + ${field.offset.indirectGetter} + "!";
    }
}
</#macro>

<#function needs_offset_initializer field>
    <#return field.array?? && field.offset?? && field.offset.containsIndex>
</#function>

<#macro offset_initializer_name fieldName>
    OffsetInitializer_${fieldName}<#t>
</#macro>

<#macro declare_offset_initializer compoundName field>
    class <@offset_initializer_name field.name/>
    {
    public:
        explicit <@offset_initializer_name field.name/>(${compoundName}& owner);

        void initializeOffset(size_t index, size_t byteOffset) const;

    private:
        ${compoundName}& m_owner;
    };
</#macro>

<#macro define_offset_initializer_methods compoundName field>
${compoundName}::<@offset_initializer_name field.name/>::<@offset_initializer_name field.name/>(<#rt>
        <#lt>${compoundName}& owner) :
        m_owner(owner)
{}

void ${compoundName}::<@offset_initializer_name field.name/>::initializeOffset(size_t index, size_t byteOffset) const
{
    const ${field.offset.typeName} value = static_cast<${field.offset.typeName}>(byteOffset);
    ${field.offset.indirectSetter};
}
</#macro>

<#function needs_field_element_factory field>
    <#return field.array?? && field.array.requiresElementFactory>
</#function>

<#macro element_factory_name fieldName>
    ElementFactory_${fieldName}<#t>
</#macro>

<#macro declare_element_factory compoundName field>
    class <@element_factory_name field.name/>
    {
    public:
        explicit <@element_factory_name field.name/>(${compoundName}& owner);

        void create(<@vector_type_name field.array.elementCppTypeName/>& array, <#rt>
                <#lt>::zserio::BitStreamReader& in, size_t index) const;

    private:
        ${compoundName}& m_owner;
    };
</#macro>

<#macro define_element_factory_methods compoundName field>
    <#local extraConstructorArguments>
        <#if field.array.elementCompound??>
            <@compound_field_compound_ctor_params field.array.elementCompound, true/><#t>
        </#if>
    </#local>
${compoundName}::<@element_factory_name field.name/>::<@element_factory_name field.name/>(${compoundName}& owner) :
        m_owner(owner)
{}

void ${compoundName}::<@element_factory_name field.name/>::create(<#rt>
        <#lt><@vector_type_name field.array.elementCppTypeName/>& array, <#rt>
        <#lt>::zserio::BitStreamReader& in, size_t index) const
{
    (void)index;
    array.emplace_back(in<#rt>
    <#if extraConstructorArguments?has_content>
            , ${extraConstructorArguments}<#t>
    </#if>
            <#lt>, array.get_allocator());
}
</#macro>

<#macro element_initializer_name fieldName>
    ElementInitializer_${fieldName}<#t>
</#macro>

<#macro declare_element_initializer compoundName field>
    class <@element_initializer_name field.name/>
    {
    public:
        explicit <@element_initializer_name field.name/>(${compoundName}& owner);

    void initialize(${field.array.elementCppTypeName}& element, size_t index) const;

    private:
        ${compoundName}& m_owner;
    };
</#macro>

<#macro define_element_initializer_methods compoundName field>
${compoundName}::<@element_initializer_name field.name/>::<@element_initializer_name field.name/>(<#rt>
        <#lt>${compoundName}& owner) :
        m_owner(owner)
{}

void ${compoundName}::<@element_initializer_name field.name/>::initialize(<#rt>
        <#lt>${field.array.elementCppTypeName}& element, size_t index) const
{
    (void)index;
    element.initialize(<@compound_field_compound_ctor_params field.array.elementCompound, true/>);
}
</#macro>

<#macro element_children_initializer_name fieldName>
    ElementChildrenInitializer_${fieldName}<#t>
</#macro>

<#macro declare_element_children_initializer field>
    class <@element_children_initializer_name field.name/>
    {
    public:
        void initialize(${field.array.elementCppTypeName}& element, size_t) const;
    };
</#macro>

<#macro define_element_children_initializer_methods compoundName field>
void ${compoundName}::<@element_children_initializer_name field.name/>::initialize(<#rt>
        <#lt>${field.array.elementCppTypeName}& element, size_t) const
{
    element.initializeChildren();
}
</#macro>

<#macro inner_classes_declaration compoundName fieldList>
    <#list fieldList as field>
        <#if needs_field_offset_checker(field)>
    <@declare_offset_checker compoundName, field/>

            <#if field.withWriterCode>
    <@declare_offset_initializer compoundName, field/>

            </#if>
        </#if>
        <#if needs_field_element_factory(field)>
    <@declare_element_factory compoundName, field/>
            <#if field.array.elementCompound??>
                <#if needs_field_initialization(field.array.elementCompound)>
    <@declare_element_initializer compoundName, field/>

                <#elseif field.array.elementCompound.needsChildrenInitialization>
    <@declare_element_children_initializer field/>

                </#if>
            </#if>
        </#if>
    </#list>
</#macro>

<#macro inner_classes_definition compoundName fieldList>
    <#list fieldList as field>
        <#if needs_field_offset_checker(field)>
<@define_offset_checker_methods compoundName, field/>

            <#if field.withWriterCode>
<@define_offset_initializer_methods compoundName, field/>

            </#if>
        </#if>
        <#if needs_field_element_factory(field)>
<@define_element_factory_methods compoundName, field/>

            <#if field.array.elementCompound??>
                <#if needs_field_initialization(field.array.elementCompound)>
<@define_element_initializer_methods compoundName, field/>

                <#elseif field.array.elementCompound.needsChildrenInitialization>
<@define_element_children_initializer_methods compoundName, field/>

                </#if>
            </#if>
        </#if>
    </#list>
</#macro>

<#macro arrays_typedefs fieldList>
    <#list fieldList as field>
        <#if field.array??>
    using <@array_typedef_name field/> = <@array_type_name field/>;
        </#if>
    </#list>
</#macro>

<#macro top_private_section_declarations compoundName fieldList>
    <#local innerClasses><@inner_classes_declaration compoundName, fieldList/></#local>
    <#local arraysTypedefs><@arrays_typedefs fieldList/></#local>
    <#if innerClasses?has_content || arraysTypedefs?has_content>
private:
        <#if innerClasses?has_content>
    ${innerClasses}<#t>
        </#if>
        <#if arraysTypedefs?has_content>
    ${arraysTypedefs}<#t>

        </#if>
    </#if>
</#macro>

<#macro compound_bitsizeof_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isEnum>
${I}endBitPosition += ::zserio::bitSizeOf(<@compound_get_field field/>);
    <#elseif field.bitSize??>
        <#if field.bitSize.isDynamicBitField>
${I}endBitPosition += static_cast<uint8_t>(${field.bitSize.value});
        <#else>
${I}endBitPosition += ${field.bitSize.value};
        </#if>
    <#elseif field.runtimeFunction??>
${I}endBitPosition += ::zserio::bitSizeOf${field.runtimeFunction.suffix}(<@compound_get_field field/>);
    <#elseif field.array??>
${I}endBitPosition += <@compound_get_field field/>.bitSizeOf(endBitPosition);
    <#else>
${I}endBitPosition += <@compound_get_field field/>.bitSizeOf(endBitPosition);
    </#if>
</#macro>

<#macro compound_initialize_offsets_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isEnum>
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
${I}endBitPosition = <@compound_get_field field/>.initializeOffsets(endBitPosition<#rt>
        <#lt><#if field.offset?? && field.offset.containsIndex>, <@offset_initializer_name field.name/>(*this)</#if>);
    <#else>
${I}endBitPosition = <@compound_get_field field/>.initializeOffsets(endBitPosition);
    </#if>
</#macro>

<#macro compound_field_accessors_declaration field>
    <#if needs_field_getter(field)>
    <@field_raw_cpp_type_name field/>& ${field.getterName}();
    </#if>
    <@field_raw_cpp_argument_type_name field/> ${field.getterName}() const;
    <#if needs_field_setter(field)>
    void ${field.setterName}(<@field_raw_cpp_argument_type_name field/> <@field_argument_name field/>);
    </#if>
    <#if needs_field_rvalue_setter(field)>
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
                    (std::forward<ZSERIO_T_${field.name}>(<@field_argument_name field/>), <#t>
                    <@array_traits field/>)<#t>
    <#else>
        std::forward<ZSERIO_T_${field.name}>(<@field_argument_name field/>)<#t>
    </#if>
</#macro>

<#macro compound_setter_field_value field>
    <#if field.array??>
        <@array_typedef_name field/>(<@field_argument_name field/>, <#t>
                <@array_traits field/>)<#t>
    <#else>
        <@field_argument_name field/><#t>
    </#if>
</#macro>

<#macro compound_setter_field_rvalue field>
    <#if field.array??>
        <@array_typedef_name field/>(std::move(<@field_argument_name field/>), <#t>
                <@array_traits field/>)<#t>
    <#else>
        std::move(<@field_argument_name field/>)<#t>
    </#if>
</#macro>

<#macro compound_copy_constructor_initializer_field field hasNext indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice(other.m_objectChoice)
    <#else>
${I}<@field_member_name field/>(other.<@field_member_name field/>)<#if hasNext>,</#if>
    </#if>
</#macro>

<#macro compound_move_constructor_initializer_field field hasNext indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice(::std::move(other.m_objectChoice))
    <#else>
${I}<@field_member_name field/>(::std::move(other.<@field_member_name field/>))<#if hasNext>,</#if>
    </#if>
</#macro>

<#macro compound_assignment_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice = other.m_objectChoice;
    <#else>
${I}<@field_member_name field/> = other.<@field_member_name field/>;
    </#if>
</#macro>

<#macro compound_move_assignment_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice = ::std::move(other.m_objectChoice);
    <#else>
${I}<@field_member_name field/> = ::std::move(other.<@field_member_name field/>);
    </#if>
</#macro>

<#macro compound_allocator_propagating_copy_constructor_initializer_field field hasNext indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice(other.copyObject(allocator))
    <#else>
${I}<@field_member_name field/>(::zserio::allocatorPropagatingCopy(<#rt>
        <#lt>other.<@field_member_name field/>, allocator))<#if hasNext>,</#if>
    </#if>
</#macro>

<#macro compound_initialize_children_declaration>
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
        <#local initializeCommand>
            <#if needs_field_initialization(field.array.elementCompound)>
                <@compound_get_field field/>.initializeElements(<@element_initializer_name field.name/>(*this));<#t>
            <#elseif field.array.elementCompound.needsChildrenInitialization>
                <@compound_get_field field/>.initializeElements(<@element_children_initializer_name field.name/>());<#t>
            </#if>
        </#local>
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
            <#if !field.isSimpleType || field.optional??>
                <#if numTemplateArgs != 0>
            <#lt>,
            typename ZSERIO_T_${field.name}<#rt>
                <#else>
            typename ZSERIO_T_${field.name}<#t>
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
        <#if !field.isSimpleType || field.optional??>
${I}ZSERIO_T_${field.name}&& <#t>
        <#else>
${I}${field.cppArgumentTypeName} <#t>
        </#if>
        <@field_argument_name field/><#t>
        <#if field?has_next>
        <#lt>,
        </#if>
    </#list>
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

<#function needs_field_initialization field>
    <#if field.instantiatedParameters?has_content>
        <#return true>
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

<#function needs_field_any_write_check_code field compoundName indent>
    <#local checkCode>
        <#if field.offset?? && !field.offset.containsIndex>
    <@compound_check_offset_field field, compoundName, "Write", "out", indent/>
        </#if>
    <@compound_check_constraint_field field, compoundName, "Write", indent/>
    <@compound_check_array_length_field field, compoundName, indent/>
    <@compound_check_range_field field, compoundName, indent/>
    </#local>
    <#if checkCode == "">
        <#return false>
    </#if>

    <#return true>
</#function>

<#function needs_field_getter field>
    <#if field.withWriterCode && !field.isSimpleType>
        <#return true>
    </#if>
    <#return false>
</#function>

<#function needs_field_setter field>
    <#if field.withWriterCode>
        <#return true>
    </#if>
    <#return false>
</#function>

<#function needs_field_rvalue_setter field>
    <#if field.withWriterCode && !field.isSimpleType>
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
