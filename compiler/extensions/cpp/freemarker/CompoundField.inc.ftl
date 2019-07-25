<#macro field_member_name fieldName>
    m_${fieldName}_<#t>
</#macro>

<#macro field_argument_name fieldName>
    ${fieldName}_<#t>
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
    <#else>
    <@compound_read_field_inner field, compoundName, indent/>
    </#if>
</#macro>

<#macro compound_read_field_inner field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <@compound_read_field_prolog field, compoundName, indent/>
    <#if field.array??>
        <#if field.usesAnyHolder || field.optional??>
${I}<@compound_field_storage field/> = ${field.cppTypeName}();
        </#if>
${I}zserio::read<@array_runtime_function_suffix field/><#rt>
        <#lt><#if field.offset?? && field.offset.containsIndex><<@offset_checker_name field.name/>></#if><#rt>
        <#lt>(<@array_traits field, true/>, <@compound_get_field field/>, in<#rt>
        <#lt><@array_offset_checker field/>);
    <#elseif field.runtimeFunction??>
${I}<@compound_field_storage field/> = static_cast<${field.cppTypeName}>(in.read${field.runtimeFunction.suffix}(${field.runtimeFunction.arg!}));
    <#elseif field.isEnum>
${I}<@compound_field_storage field/> = zserio::read<${field.cppTypeName}>(in);
    <#else>
        <#-- compound -->
        <#local compoundParamsArguments><@compound_field_compound_ctor_params field.compound, false/></#local>
        <#local constructorArguments>in<#if compoundParamsArguments?has_content>, ${compoundParamsArguments}</#if></#local>
        <#if field.usesAnyHolder || field.optional??>
${I}<@compound_field_storage field/> = ${field.cppTypeName}(${constructorArguments});
        <#else>
${I}<@field_member_name field/>.read(${constructorArguments});
        </#if>
    </#if>
</#macro>

<#macro compound_field_compound_ctor_params compound useIndirectExpression>
    <#list compound.instantiatedParameters as parameter>
        <#if useIndirectExpression>${parameter.indirectExpression}<#else>${parameter.expression}</#if><#t>
        <#if parameter?has_next>, </#if><#t>
    </#list>
</#macro>

<#macro compound_read_field_prolog field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}in.alignTo(${field.alignmentValue});
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}in.alignTo(UINT32_C(8));
${I}if (in.getBitPosition() != zserio::bytesToBits(${field.offset.getter}))
${I}{
${I}    throw zserio::CppRuntimeException("Read: Wrong offset for field ${compoundName}.${field.name}: " +
${I}            zserio::convertToString(in.getBitPosition()) + " != " +
${I}            zserio::convertToString(zserio::bytesToBits(${field.offset.getter})) + "!");
${I}}
    </#if>
</#macro>

<#macro compound_pre_write_actions needsRangeCheck needsChildrenInitialization hasFieldWithOffset>
    <#if needsRangeCheck>
    if ((preWriteAction & zserio::PRE_WRITE_CHECK_RANGES) != 0)
        checkRanges();
    </#if>
    <#if needsChildrenInitialization>
    if ((preWriteAction & zserio::PRE_WRITE_INITIALIZE_CHILDREN) != 0)
        initializeChildren();
    </#if>
    <#if hasFieldWithOffset>
    if ((preWriteAction & zserio::PRE_WRITE_INITIALIZE_OFFSETS) != 0)
        initializeOffsets(out.getBitPosition());
    </#if>
</#macro>

<#macro field_optional_condition field>
    <#if field.optional.clause??>${field.optional.clause}<#else><@field_member_name field.name/>.hasValue()</#if><#t>
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
    <#elseif field.array??>
    <@compound_write_field_array_prolog field, compoundName, indent/>
${I}zserio::write<@array_runtime_function_suffix field/><#rt>
        <#lt><#if field.offset?? && field.offset.containsIndex><<@offset_checker_name field.name/>></#if><#rt>
        <#lt>(<@array_traits field/>, <#rt>
        <#lt><@compound_get_field field/>, out<@array_offset_checker field/>);
    <#else>
${I}<@compound_get_field field/>.write(out, zserio::NO_PRE_WRITE_ACTION);
    </#if>
</#macro>

<#macro compound_write_field_prolog field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}out.alignTo(${field.alignmentValue});
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}out.alignTo(UINT32_C(8));
${I}if (out.getBitPosition() != zserio::bytesToBits(${field.offset.getter}))
${I}{
${I}    throw zserio::CppRuntimeException("Write: Wrong offset for field ${compoundName}.${field.name}: " +
${I}            zserio::convertToString(out.getBitPosition()) + " != " +
${I}            zserio::convertToString(zserio::bytesToBits(${field.offset.getter})) + "!");
${I}}
    </#if>
</#macro>

<#macro compound_write_field_array_prolog field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array.length??>
${I}if (<@compound_get_field field/>.size() != static_cast<size_t>(${field.array.length}))
${I}{
${I}    throw zserio::CppRuntimeException("Write: Wrong array length for field ${compoundName}.${field.name}: " +
${I}            zserio::convertToString(<@compound_get_field field/>.size()) + " != " +
${I}            zserio::convertToString(static_cast<size_t>(${field.array.length})) + "!");
${I}}
    </#if>
</#macro>

<#macro compound_check_constraint_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.constraint??>
${I}if (<#if field.optional??>(<@field_optional_condition field/>) && </#if>!(${field.constraint}))
${I}    throw zserio::ConstraintException("Constraint violated at ${compoundName}.${field.name}!");
    </#if>
</#macro>

<#macro compound_check_range_field field compoundName indent mayNotBeEmptyCommand=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if needs_field_range_check(field)>
        <#if field.optional??>
${I}if (<@field_optional_condition field/>)
${I}{
        <@compound_check_range_field_inner field, compoundName, indent + 1/>
${I}}
        <#else>
    <@compound_check_range_field_inner field, compoundName, indent/>
        </#if>
    <#elseif mayNotBeEmptyCommand>
${I};
    </#if>
</#macro>

<#macro compound_check_range_field_inner field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.integerRange?? && !field.integerRange.hasFullRange>
        <#local fieldValue><@compound_get_field field/></#local>
        <@compound_check_range_value fieldValue, field.name, compoundName, field.cppTypeName,
                field.integerRange, indent/>
    <#elseif field.array?? && field.array.elementIntegerRange?? && !field.array.elementIntegerRange.hasFullRange>
${I}for (${field.cppTypeName}::const_iterator it = <@compound_get_field field/>.begin(); it != <@compound_get_field field/>.end(); ++it)
${I}{
        <@compound_check_range_value "*it", field.name, compoundName, field.array.elementCppTypeName,
            field.array.elementIntegerRange, indent + 1/>
${I}}
    </#if>
</#macro>

<#macro compound_check_range_value value valueName compoundName cppTypeName integerRange indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local lowerBoundVarName>_${valueName}LowerBound</#local>
    <#local upperBoundVarName>_${valueName}UpperBound</#local>
    <#if !integerRange.lowerBound?? || !integerRange.upperBound??>
        <#local lengthVarName>_${valueName}Length</#local>
${I}const int ${lengthVarName} = ${integerRange.bitFieldLength};
${I}const ${cppTypeName} ${lowerBoundVarName} = static_cast<${cppTypeName}><#rt>
        <#lt>(zserio::getBitFieldLowerBound(${lengthVarName}, <#if integerRange.checkLowerBound>true<#else>false</#if>));
${I}const ${cppTypeName} ${upperBoundVarName} = static_cast<${cppTypeName}><#rt>
        <#lt>(zserio::getBitFieldUpperBound(${lengthVarName}, <#if integerRange.checkLowerBound>true<#else>false</#if>));
    <#else>
${I}const ${cppTypeName} ${lowerBoundVarName} = ${integerRange.lowerBound};
${I}const ${cppTypeName} ${upperBoundVarName} = ${integerRange.upperBound};
    </#if>
${I}if (<#if integerRange.checkLowerBound>${value} < ${lowerBoundVarName} || </#if>${value} > ${upperBoundVarName})
${I}    throw zserio::CppRuntimeException("Value " + zserio::convertToString(${value}) +
${I}            " of ${compoundName}.${valueName} exceeds the range of <" +
${I}            zserio::convertToString(${lowerBoundVarName}) + ".." +
${I}            zserio::convertToString(${upperBoundVarName}) + ">!");
</#macro>

<#macro array_traits field isInRead=false>
    <#local array=field.array/>
    ${array.traitsName}<#t>
    <#if array.hasTemplatedTraits>
        <${array.elementCppTypeName}<#if isInRead && array.requiresElementFactory>, <@element_factory_name field.name/></#if>><#t>
    </#if>
        (<#t>
    <#if array.elementBitSizeValue??>
        ${array.elementBitSizeValue}<#t>
    </#if>
    <#if isInRead && array.requiresElementFactory>
        <@element_factory_name field.name/>(*this)<#t>
    </#if>
        )<#t>
</#macro>

<#macro array_runtime_function_suffix field>
    <#if field.offset?? && field.offset.containsIndex>Aligned</#if><#t>
    <#if !field.array.length?? && !field.array.isImplicit>Auto</#if><#t>
</#macro>

<#macro offset_checker_name fieldName>
    OffsetChecker_${fieldName}<#t>
</#macro>

<#macro define_offset_checker compoundName field>
class ${compoundName}::<@offset_checker_name field.name/>
{
public:
    explicit <@offset_checker_name field.name/>(${compoundName}& owner) : m_owner(owner) {}

    void checkOffset(size_t index, size_t byteOffset) const
    {
        if (byteOffset != ${field.offset.indirectGetter})
            throw zserio::CppRuntimeException("Wrong offset for field ${compoundName}.${field.name}: " +
                    zserio::convertToString(byteOffset) + " != " +
                    zserio::convertToString(${field.offset.indirectGetter}) + "!");
    }

private:
    ${compoundName}& m_owner;
};
</#macro>

<#macro offset_initializer_name fieldName>
    OffsetInitializer_${fieldName}<#t>
</#macro>

<#macro define_offset_initializer compoundName field>
class ${compoundName}::<@offset_initializer_name field.name/>
{
public:
    explicit <@offset_initializer_name field.name/>(${compoundName}& owner) : m_owner(owner) {}

    void initializeOffset(size_t index, size_t byteOffset) const
    {
        const ${field.offset.typeName} value = (${field.offset.typeName})byteOffset;
        ${field.offset.indirectSetter};
    }

private:
    ${compoundName}& m_owner;
};
</#macro>

<#macro element_factory_name fieldName>
    ElementFactory_${fieldName}<#t>
</#macro>

<#macro define_element_factory compoundName field>
    <#local extraConstructorArguments>
        <#if field.array.elementCompound??>
            <@compound_field_compound_ctor_params field.array.elementCompound, true/><#t>
        </#if>
    </#local>
class ${compoundName}::<@element_factory_name field.name/>
{
public:
    explicit <@element_factory_name field.name/>(${compoundName}& owner) : m_owner(owner) {}

    void create(${field.cppTypeName}& array, zserio::BitStreamReader& in, size_t index) const
    {
        (void)index;
        array.emplace_back(in<#if extraConstructorArguments?has_content>, ${extraConstructorArguments}</#if>);
    }

private:
    ${compoundName}& m_owner;
};
</#macro>

<#macro element_initializer_name fieldName>
    ElementInitializer_${fieldName}<#t>
</#macro>

<#macro define_element_initializer compoundName field>
class ${compoundName}::<@element_initializer_name field.name/>
{
public:
    explicit <@element_initializer_name field.name/>(${compoundName}& owner) : m_owner(owner) {}

    void initialize(${field.array.elementCppTypeName}& element, size_t index)
    {
        (void)index;
        element.initialize(<@compound_field_compound_ctor_params field.array.elementCompound, true/>);
    }

private:
    ${compoundName}& m_owner;
};
</#macro>

<#macro element_children_initializer_name fieldName>
    ElementChildrenInitializer_${fieldName}<#t>
</#macro>

<#macro define_element_children_initializer compoundName field>
class ${compoundName}::<@element_children_initializer_name field.name/>
{
public:
    <@element_children_initializer_name field.name/>() {}

    void initialize(${field.array.elementCppTypeName}& element, size_t)
    {
        element.initializeChildren();
    }
};
</#macro>

<#macro declare_inner_classes fieldList>
    <#local hasAny=false/>
    <#list fieldList as field>
        <#if field.array??>
            <#if field.offset?? && field.offset.containsIndex>
                <#local hasAny=true/>
    class <@offset_checker_name field.name/>;
                <#if withWriterCode>
                    <#local hasAny=true/>
    class <@offset_initializer_name field.name/>;
                </#if>
            </#if>
            <#if field.array.requiresElementFactory>
                <#local hasAny=true/>
    class <@element_factory_name field.name/>;
                <#if field.array.elementCompound??>
                    <#if needs_compound_field_initialization(field.array.elementCompound)>
                        <#local hasAny=true/>
    class <@element_initializer_name field.name/>;
                    <#elseif field.array.elementCompound.needsChildrenInitialization>
                        <#local hasAny=true/>
    class <@element_children_initializer_name field.name/>;
                    </#if>
                </#if>
            </#if>
        </#if>
    </#list>
    <#if hasAny>

    </#if>
</#macro>

<#macro define_inner_classes fieldList>
    <#list fieldList as field>
        <#if field.array??>
            <#if field.offset?? && field.offset.containsIndex>
<@define_offset_checker name, field/>

                <#if withWriterCode>
<@define_offset_initializer name, field/>

                </#if>
            </#if>
            <#if field.array.requiresElementFactory>
<@define_element_factory name, field/>

                <#if field.array.elementCompound??>
                    <#if needs_compound_field_initialization(field.array.elementCompound)>
<@define_element_initializer name, field/>

                    <#elseif field.array.elementCompound.needsChildrenInitialization>
<@define_element_children_initializer name, field/>

                    </#if>
                </#if>
            </#if>
        </#if>
    </#list>
</#macro>

<#macro array_offset_checker field>
    <#if field.offset?? && field.offset.containsIndex>, <@offset_checker_name field.name/>(*this)</#if><#t>
</#macro>

<#macro array_element_bit_size array>
    <#if array.requiresElementBitSize>
        , ${array.elementBitSizeValue}<#t>
    <#elseif array.offset?? && array.offset.containsIndex && !array.requiresElementFactory>
        <#-- non-ObjectArrays require a dummy 0 for numBits argument when offset checker is used -->
        , 0<#t>
    </#if>
    <#-- else: no argument needed -->
</#macro>

<#macro compound_bitsizeof_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array??>
${I}endBitPosition += zserio::bitSizeOf<@array_runtime_function_suffix field/>(<@array_traits field/>, <#rt>
        <#lt><@compound_get_field field/>, endBitPosition);
    <#elseif field.bitSizeValue??>
${I}endBitPosition += ${field.bitSizeValue};
    <#elseif field.runtimeFunction??>
${I}endBitPosition += zserio::getBitSizeOf${field.runtimeFunction.suffix}(<@compound_get_field field/>);
    <#else>
${I}endBitPosition += <@compound_get_field field/>.bitSizeOf(endBitPosition);
    </#if>
</#macro>

<#macro compound_initialize_offsets_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array??>
${I}endBitPosition = zserio::initializeOffsets<@array_runtime_function_suffix field/><#rt>
        <#lt><#if field.offset?? && field.offset.containsIndex><<@offset_initializer_name field.name/>></#if><#rt>
        <#lt>(<@array_traits field/>, <#rt>
        <#lt><@compound_get_field field/>, endBitPosition<#rt>
        <#if field.offset?? && field.offset.containsIndex>, <@offset_initializer_name field.name/>(*this)</#if><#t>
        <#lt>);
    <#elseif field.bitSizeValue??>
${I}endBitPosition += ${field.bitSizeValue};
    <#elseif field.runtimeFunction??>
${I}endBitPosition += zserio::getBitSizeOf${field.runtimeFunction.suffix}(<@compound_get_field field/>);
    <#else>
${I}endBitPosition = <@compound_get_field field/>.initializeOffsets(endBitPosition);
    </#if>
</#macro>

<#macro compound_field_accessors_declaration field>
    <#local fieldOrOptional=field.optional!field>
    <#if field.withWriterCode && (field.optional?? || !field.isSimpleType)>
        <#-- non-const getter is neccessary for setting of offsets -->
    ${fieldOrOptional.cppTypeName}& ${field.getterName}();
    </#if>
    ${fieldOrOptional.cppArgumentTypeName} ${field.getterName}() const;
    <#if field.withWriterCode>
    void ${field.setterName}(${fieldOrOptional.cppArgumentTypeName} <@field_argument_name field.name/>);
        <#if field.optional?? || !field.isSimpleType>
    void ${field.setterName}(${fieldOrOptional.cppTypeName}&& <@field_argument_name field.name/>);
        </#if>
    </#if>
</#macro>

<#macro compound_field_getter_definition field compoundName returnFieldMacroName>
    <#if field.withWriterCode && (field.optional?? || !field.isSimpleType)>
    <#local fieldOrOptional=field.optional!field>
${fieldOrOptional.cppTypeName}& ${compoundName}::${field.getterName}()
{
<@.vars[returnFieldMacroName] field/>
}

    </#if>
</#macro>

<#macro compound_field_const_getter_definition field compoundName returnFieldMacroName>
    <#local fieldOrOptional=field.optional!field>
${fieldOrOptional.cppArgumentTypeName} ${compoundName}::${field.getterName}() const
{
<@.vars[returnFieldMacroName] field/>
}

</#macro>

<#macro compound_field_setter_definition field compoundName setFieldMacroName>
    <#if field.withWriterCode>
    <#local fieldOrOptional=field.optional!field>
void ${compoundName}::${field.setterName}(${fieldOrOptional.cppArgumentTypeName} <@field_argument_name field.name/>)
{
<@.vars[setFieldMacroName] field/>
}

    </#if>
</#macro>

<#macro compound_field_rvalue_setter_definition field compoundName setFieldMacroName>
    <#if field.withWriterCode && !field.isSimpleType><#-- no sense to move an optional holding a simple type -->
    <#local fieldOrOptional=field.optional!field>
void ${compoundName}::${field.setterName}(${fieldOrOptional.cppTypeName}&& <@field_argument_name field.name/>)
{
<@.vars[setFieldMacroName] field/>
}

    </#if>
</#macro>

<#macro compound_return_field field>
    return <@compound_get_field field/>;
</#macro>

<#macro compound_field_storage field>
    <#if field.usesAnyHolder>m_objectChoice<#else><@field_member_name field.name/></#if><#t>
</#macro>

<#macro compound_set_field field>
    <@compound_field_storage field/> = <@field_argument_name field.name/>;
</#macro>

<#macro compound_rvalue_set_field field>
    <@compound_field_storage field/> = std::move(<@field_argument_name field.name/>);
</#macro>

<#macro compound_get_field field>
    <#if field.usesAnyHolder>
        m_objectChoice.get<${field.cppTypeName}>()<#t>
    <#elseif field.optional??>
        *<@field_member_name field.name/><#t>
    <#else>
        <@field_member_name field.name/><#t>
    </#if>
</#macro>

<#macro compound_copy_constructor_initializer_field field hasNext indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice(other.m_objectChoice)
    <#else>
${I}<@field_member_name field.name/>(other.<@field_member_name field.name/>)<#if hasNext>,</#if>
    </#if>
</#macro>

<#macro compound_move_constructor_initializer_field field hasNext indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice(std::move(other.m_objectChoice))
    <#else>
${I}<@field_member_name field.name/>(std::move(other.<@field_member_name field.name/>))<#if hasNext>,</#if>
    </#if>
</#macro>

<#macro compound_field_constructor_initializer_field field hasNext indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice(std::forward<ZSERIO_T>(value))
    <#else>
${I}<@field_member_name field.name/>(<#rt>
        <#if field.isSimpleType>
            <@field_argument_name field.name/><#t>
        <#else>
            std::forward<ZSERIO_T_${field.name}>(<@field_argument_name field.name/>)<#t>
        </#if>
        <#lt>)<#if hasNext>,</#if>
    </#if>
</#macro>

<#macro compound_assignment_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice = other.m_objectChoice;
    <#else>
${I}<@field_member_name field.name/> = other.<@field_member_name field.name/>;
    </#if>
</#macro>

<#macro compound_move_assignment_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice = std::move(other.m_objectChoice);
    <#else>
${I}<@field_member_name field.name/> = std::move(other.<@field_member_name field.name/>);
    </#if>
</#macro>

<#macro compound_initialize_children_declaration>
    void initializeChildren();
</#macro>

<#macro compound_initialize_children_field field compoundName indent mayNotBeEmptyCommand=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.compound??>
        <#if needs_compound_field_initialization(field.compound)>
            <#local initializeCommand><@compound_get_field field/>.initialize(<#rt>
                <#lt><@compound_field_compound_ctor_params field.compound, false/>);</#local>
        <#elseif field.compound.needsChildrenInitialization>
            <#local initializeCommand><@compound_get_field field/>.initializeChildren();</#local>
        </#if>
    <#elseif field.array?? && field.array.elementCompound??>
        <#if needs_compound_field_initialization(field.array.elementCompound)>
            <#local initializeCommand><@compound_get_field field/>.initializeElements(<#rt>
                <#lt><@element_initializer_name field.name/>(*this));</#local>
        <#elseif field.array.elementCompound.needsChildrenInitialization>
            <#local initializeCommand><@compound_get_field field/>.initializeElements(<#rt>
                <#lt><@element_children_initializer_name field.name/>());</#local>
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

<#macro compound_field_constructor_template_arg_list fieldList>
    <#local hasTemplateArg=false/>
    <#local templateArgList>
        <#list fieldList as field>
            <#if field.usesAnyHolder>
                typename ZSERIO_T<#t>
                <#local hasTemplateArg=true/>
                <#break/>
            <#else>
                <#if !field.isSimpleType>
                    <#if hasTemplateArg>, </#if>typename ZSERIO_T_${field.name}<#t>
                    <#local hasTemplateArg=true/>
                </#if>
            </#if>
        </#list>
    </#local>
    <#if templateArgList?has_content>
    template <${templateArgList}>
    </#if>
</#macro>

<#macro compound_field_constructor_type_list fieldList indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list fieldList as field>
        <#if field.usesAnyHolder>
            ${I}ZSERIO_T&& value<#t>
            <#break/>
        <#else>
            <#if field.isSimpleType>
                <#if field.optional??>
                    ${I}${field.optional.cppArgumentTypeName}<#t>
                <#else>
                    ${I}${field.cppArgumentTypeName}<#t>
                </#if>
            <#else>
                ${I}ZSERIO_T_${field.name}&&<#t>
            </#if>
            <#lt> <@field_argument_name field.name/><#rt>
            <#if field?has_next>
                <#lt>,
            </#if>
        </#if>
    </#list>
</#macro>

<#function has_field_with_constraint fieldList>
    <#list fieldList as field>
        <#if field.constraint??>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function needs_field_range_check field>
    <#if (field.integerRange?? && !field.integerRange.hasFullRange) ||
            (field.array?? && field.array.elementIntegerRange?? && !field.array.elementIntegerRange.hasFullRange)>
        <#return true>
    </#if>
    <#return false>
</#function>

<#function has_field_with_range_check fieldList>
    <#list fieldList as field>
        <#if needs_field_range_check(field)>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function needs_compound_field_initialization compoundField>
    <#if compoundField.instantiatedParameters?has_content>
        <#return true>
    </#if>
    <#return false>
</#function>

<#function has_field_with_initialization fieldList>
    <#list fieldList as field>
        <#if field.compound??>
            <#if needs_compound_field_initialization(field.compound)>
                <#return true>
            </#if>
        <#elseif field.array??>
            <#if field.array.elementCompound?? &&
                    needs_compound_field_initialization(field.array.elementCompound)>
                <#return true>
            </#if>
        </#if>
    </#list>
    <#return false>
</#function>
