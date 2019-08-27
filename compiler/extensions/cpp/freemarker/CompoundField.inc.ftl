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
${I}
${I}return ${field.cppTypeName}();
    <#else>
    <@compound_read_field_inner field, compoundName, indent/>
    </#if>
</#macro>

<#macro compound_read_field_inner field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <@compound_read_field_prolog field, compoundName, indent/>
    <#local cppTypeName><#if field.optional??>${field.optional.cppRawTypeName}<#else>${field.cppTypeName}</#if></#local>
    <#if field.array??>
${I}${cppTypeName} readField;
${I}::zserio::read<@array_runtime_function_suffix field, true/>(<@array_traits field, true/>, readField, in<#rt>
        <#lt><#if field.array.length??>, ${field.array.length}</#if><#rt>
        <#lt><#if field.offset?? && field.offset.containsIndex>, <@offset_checker_name field.name/>(*this)</#if><#rt>
        <#lt>);
        <#local readCommand="readField"/>
    <#elseif field.runtimeFunction??>
        <#local readCommand>static_cast<${cppTypeName}>(in.read${field.runtimeFunction.suffix}(${field.runtimeFunction.arg!}))</#local>
    <#elseif field.isEnum>
        <#local readCommand>::zserio::read<${cppTypeName}>(in)</#local>
    <#else>
        <#-- compound -->
        <#local compoundParamsArguments><@compound_field_compound_ctor_params field.compound, false/></#local>
        <#local constructorArguments>in<#if compoundParamsArguments?has_content>, ${compoundParamsArguments}</#if></#local>
        <#local readCommand>${cppTypeName}(${constructorArguments})</#local>
    </#if>
    <#if field.constraint??>
        <#if !field.array??>
${I}const ${cppTypeName} readField = ${readCommand};
        </#if>
    <@compound_check_constraint_field field, name, "Read", indent/>
${I}
${I}return <#if field.usesAnyHolder>::zserio::AnyHolder(</#if>readField<#if field.usesAnyHolder>)</#if>;
    <#else>
${I}return <#if field.usesAnyHolder>::zserio::AnyHolder(</#if>${readCommand}<#if field.usesAnyHolder>)</#if>;
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
    <@compound_check_offset_field field, compoundName, "Read", "in.getBitPosition()", indent/>
    </#if>
</#macro>

<#macro compound_check_offset_field field compoundName actionName bitPositionName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}// check offset
${I}if (${bitPositionName} != ::zserio::bytesToBits(${field.offset.getter}))
${I}{
${I}    throw ::zserio::CppRuntimeException("${actionName}: Wrong offset for field ${compoundName}.${field.name}: " +
${I}            ::zserio::convertToString(${bitPositionName}) + " != " +
${I}            ::zserio::convertToString(::zserio::bytesToBits(${field.offset.getter})) + "!");
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
${I}::zserio::write<@array_runtime_function_suffix field/><#rt>
        <#lt>(<@array_traits field/>, <@compound_get_field field/>, out<#rt>
        <#lt><#if field.offset?? && field.offset.containsIndex>, <@offset_checker_name field.name/>(*this)</#if><#rt>
        <#lt>);
    <#elseif field.isEnum>
${I}::zserio::write(out, <@compound_get_field field/>);
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
${I}out.alignTo(UINT32_C(8));
    <@compound_check_offset_field field, compoundName, "Write", "out.getBitPosition()", indent/>
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
${I}if (<#if field.optional??>(<@field_optional_condition field/>) && </#if>!(${constraintExpresssion}))
${I}    throw ::zserio::ConstraintException("${actionName}: Constraint violated at ${compoundName}.${field.name}!");
    </#if>
</#macro>

<#macro compound_check_array_length_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array?? && field.array.length??>
${I}// check array length
${I}if (<@compound_get_field field/>.size() != static_cast<size_t>(${field.array.length}))
${I}{
${I}    throw ::zserio::CppRuntimeException("Write: Wrong array length for field ${compoundName}.${field.name}: " +
${I}            ::zserio::convertToString(<@compound_get_field field/>.size()) + " != " +
${I}            ::zserio::convertToString(static_cast<size_t>(${field.array.length})) + "!");
${I}}
    </#if>
</#macro>

<#macro compound_check_range_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.withRangeCheckCode>
        <#local cppTypeName><#if field.optional??>${field.optional.cppRawTypeName}<#else>${field.cppTypeName}</#if></#local>
        <#if field.integerRange?? && !field.integerRange.hasFullRange>
${I}// check range
            <#local fieldValue><@compound_get_field field/></#local>
${I}{
        <@compound_check_range_value fieldValue, field.name, compoundName, cppTypeName, field.integerRange,
                indent + 1/>
${I}}
        <#elseif field.array?? && field.array.elementIntegerRange?? && !field.array.elementIntegerRange.hasFullRange>
${I}// check ranges
${I}for (${cppTypeName}::const_iterator it = <@compound_get_field field/>.begin(); <#rt>
            <#lt>it != <@compound_get_field field/>.end(); ++it)
${I}{
        <@compound_check_range_value "*it", field.name, compoundName, field.array.elementCppTypeName,
                field.array.elementIntegerRange, indent + 1/>
${I}}
        </#if>
    </#if>
</#macro>

<#macro compound_check_range_value value valueName compoundName cppTypeName integerRange indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local lowerBoundVarName>lowerBound</#local>
    <#local upperBoundVarName>upperBound</#local>
    <#if !integerRange.lowerBound?? || !integerRange.upperBound??>
        <#local lengthVarName>length</#local>
${I}const int ${lengthVarName} = ${integerRange.bitFieldLength};
${I}const ${cppTypeName} ${lowerBoundVarName} = static_cast<${cppTypeName}><#rt>
        <#lt>(::zserio::getBitFieldLowerBound(${lengthVarName}, <#if integerRange.checkLowerBound>true<#else>false</#if>));
${I}const ${cppTypeName} ${upperBoundVarName} = static_cast<${cppTypeName}><#rt>
        <#lt>(::zserio::getBitFieldUpperBound(${lengthVarName}, <#if integerRange.checkLowerBound>true<#else>false</#if>));
    <#else>
${I}const ${cppTypeName} ${lowerBoundVarName} = ${integerRange.lowerBound};
${I}const ${cppTypeName} ${upperBoundVarName} = ${integerRange.upperBound};
    </#if>
${I}if (<#if integerRange.checkLowerBound>${value} < ${lowerBoundVarName} || </#if>${value} > ${upperBoundVarName})
${I}    throw ::zserio::CppRuntimeException("Value " + ::zserio::convertToString(${value}) +
${I}            " of ${compoundName}.${valueName} exceeds the range of <" +
${I}            ::zserio::convertToString(${lowerBoundVarName}) + ".." +
${I}            ::zserio::convertToString(${upperBoundVarName}) + ">!");
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

<#macro array_runtime_function_suffix field isInRead=false>
    <#if field.offset?? && field.offset.containsIndex>
        Aligned<#t>
    </#if>
    <#if !field.array.length??>
        <#if field.array.isImplicit>
            <#if isInRead>
            Implicit<#t>
            </#if>
        <#else>
            Auto<#t>
        </#if>
    </#if>
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
            throw ::zserio::CppRuntimeException("Wrong offset for field ${compoundName}.${field.name}: " +
                    ::zserio::convertToString(byteOffset) + " != " +
                    ::zserio::convertToString(${field.offset.indirectGetter}) + "!");
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

    <#local cppTypeName><#if field.optional??>${field.optional.cppRawTypeName}<#else>${field.cppTypeName}</#if></#local>
    void create(${cppTypeName}& array, ::zserio::BitStreamReader& in, size_t index) const
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

    void initialize(${field.array.elementCppTypeName}& element, size_t index) const
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

    void initialize(${field.array.elementCppTypeName}& element, size_t) const
    {
        element.initializeChildren();
    }
};
</#macro>

<#macro inner_classes_declaration fieldList>
    <#local hasAny=false/>
    <#list fieldList as field>
        <#if field.array??>
            <#if field.offset?? && field.offset.containsIndex>
                <#local hasAny=true/>
    class <@offset_checker_name field.name/>;
                <#if field.withWriterCode>
                    <#local hasAny=true/>
    class <@offset_initializer_name field.name/>;
                </#if>
            </#if>
            <#if field.array.requiresElementFactory>
                <#local hasAny=true/>
    class <@element_factory_name field.name/>;
                <#if field.array.elementCompound??>
                    <#if needs_field_initialization(field.array.elementCompound)>
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

<#macro inner_classes_definition fieldList>
    <#list fieldList as field>
        <#if field.array??>
            <#if field.offset?? && field.offset.containsIndex>
<@define_offset_checker name, field/>

                <#if field.withWriterCode>
<@define_offset_initializer name, field/>

                </#if>
            </#if>
            <#if field.array.requiresElementFactory>
<@define_element_factory name, field/>

                <#if field.array.elementCompound??>
                    <#if needs_field_initialization(field.array.elementCompound)>
<@define_element_initializer name, field/>

                    <#elseif field.array.elementCompound.needsChildrenInitialization>
<@define_element_children_initializer name, field/>

                    </#if>
                </#if>
            </#if>
        </#if>
    </#list>
</#macro>

<#macro compound_bitsizeof_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array??>
${I}endBitPosition += ::zserio::bitSizeOf<@array_runtime_function_suffix field/>(<@array_traits field/>, <#rt>
        <#lt><@compound_get_field field/>, endBitPosition);
    <#elseif field.isEnum>
${I}endBitPosition += ::zserio::bitSizeOf(<@compound_get_field field/>);
    <#elseif field.bitSizeValue??>
${I}endBitPosition += ${field.bitSizeValue};
    <#elseif field.runtimeFunction??>
${I}endBitPosition += ::zserio::bitSizeOf${field.runtimeFunction.suffix}(<@compound_get_field field/>);
    <#else>
${I}endBitPosition += <@compound_get_field field/>.bitSizeOf(endBitPosition);
    </#if>
</#macro>

<#macro compound_initialize_offsets_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array??>
${I}endBitPosition = ::zserio::initializeOffsets<@array_runtime_function_suffix field/><#rt>
        <#lt>(<@array_traits field/>, <@compound_get_field field/>, endBitPosition<#rt>
        <#lt><#if field.offset?? && field.offset.containsIndex>, <@offset_initializer_name field.name/>(*this)</#if><#rt>
        <#lt>);
    <#elseif field.isEnum>
${I}endBitPosition = ::zserio::initializeOffsets(endBitPosition, <@compound_get_field field/>);
    <#elseif field.bitSizeValue??>
${I}endBitPosition += ${field.bitSizeValue};
    <#elseif field.runtimeFunction??>
${I}endBitPosition += ::zserio::bitSizeOf${field.runtimeFunction.suffix}(<@compound_get_field field/>);
    <#else>
${I}endBitPosition = <@compound_get_field field/>.initializeOffsets(endBitPosition);
    </#if>
</#macro>

<#macro compound_field_accessors_declaration field>
    <#if needs_field_getter(field)>
    ${field.cppTypeName}& ${field.getterName}();
    </#if>
    ${field.cppArgumentTypeName} ${field.getterName}() const;
    <#if needs_field_setter(field)>
    void ${field.setterName}(${field.cppArgumentTypeName} <@field_argument_name field.name/>);
    </#if>
    <#if needs_field_rvalue_setter(field)>
    void ${field.setterName}(${field.cppTypeName}&& <@field_argument_name field.name/>);
    </#if>
</#macro>

<#macro compound_field_getter_definition field compoundName returnFieldMacroName>
    <#if field.withWriterCode && (field.optional?? || !field.isSimpleType)>
${field.cppTypeName}& ${compoundName}::${field.getterName}()
{
<@.vars[returnFieldMacroName] field/>
}

    </#if>
</#macro>

<#macro compound_field_const_getter_definition field compoundName returnFieldMacroName>
${field.cppArgumentTypeName} ${compoundName}::${field.getterName}() const
{
<@.vars[returnFieldMacroName] field/>
}

</#macro>

<#macro compound_field_setter_definition field compoundName setFieldMacroName>
    <#if field.withWriterCode>
void ${compoundName}::${field.setterName}(${field.cppArgumentTypeName} <@field_argument_name field.name/>)
{
<@.vars[setFieldMacroName] field/>
}

    </#if>
</#macro>

<#macro compound_field_rvalue_setter_definition field compoundName setFieldMacroName>
    <#if field.withWriterCode && !field.isSimpleType><#-- no sense to move an optional holding a simple type -->
void ${compoundName}::${field.setterName}(${field.cppTypeName}&& <@field_argument_name field.name/>)
{
<@.vars[setFieldMacroName] field/>
}

    </#if>
</#macro>

<#macro compound_get_field field>
    <#if field.usesAnyHolder>
        m_objectChoice.get<${field.cppTypeName}>()<#t>
    <#elseif field.optional??>
        <@field_member_name field.name/>.value()<#t>
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
${I}m_objectChoice(::std::move(other.m_objectChoice))
    <#else>
${I}<@field_member_name field.name/>(::std::move(other.<@field_member_name field.name/>))<#if hasNext>,</#if>
    </#if>
</#macro>

<#macro compound_field_constructor_initializer_field field hasNext indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice(::std::forward<ZSERIO_T>(value))
    <#else>
${I}<@field_member_name field.name/>(<#rt>
        <#if field.isSimpleType>
            <@field_argument_name field.name/><#t>
        <#else>
            ::std::forward<ZSERIO_T_${field.name}>(<@field_argument_name field.name/>)<#t>
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
${I}m_objectChoice = ::std::move(other.m_objectChoice);
    <#else>
${I}<@field_member_name field.name/> = ::std::move(other.<@field_member_name field.name/>);
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
        <#if needs_field_initialization(field.array.elementCompound)>
            <#local initializeCommand>::zserio::initializeElements(<#rt>
                    <#lt><@compound_get_field field/>, <#rt>
                    <#lt><@element_initializer_name field.name/>(*this));</#local>
        <#elseif field.array.elementCompound.needsChildrenInitialization>
            <#local initializeCommand>::zserio::initializeElements(<#rt>
                    <#lt><@compound_get_field field/>, <#rt>
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

<#macro compound_field_constructor_template_arg_list compoundName fieldList>
    <#local numTemplateArgs=0/>
    <#local firstTemplateArgName=""/>
    <#local templateArgList>
        <#list fieldList as field>
            <#if field.usesAnyHolder>
            typename ZSERIO_T<#t>
                <#local numTemplateArgs=1/>
                <#local firstTemplateArgName="ZSERIO_T"/>
                <#break/>
            <#else>
                <#if !field.isSimpleType>
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
            </#if>
        </#list>
    </#local>
    <#if templateArgList?has_content>
        <#if firstTemplateArgName != "">
            <#if numTemplateArgs == 1>
    template <${templateArgList},
            typename ::std::enable_if<!::std::is_same<typename ::std::decay<${firstTemplateArgName}>::type, ${compoundName}>::value,
                    int>::type = 0>
            <#else>
    template <${templateArgList},
            typename ::std::enable_if<!::std::is_same<typename ::std::decay<${firstTemplateArgName}>::type, ::zserio::BitStreamReader>::value,
                    int>::type = 0>
            </#if>
        <#else>
    template <${templateArgList}>
        </#if>
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
    <@compound_check_offset_field field, compoundName, "Write", "out.getBitPosition()", indent/>
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
    <#if field.withWriterCode && (field.optional?? || !field.isSimpleType)>
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
    <#if field.withWriterCode && (field.optional?? || !field.isSimpleType)>
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
