<#include "InstantiateTemplate.inc.ftl">
<#include "Inspector.inc.ftl">
<#macro compound_read_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if field.optional.clause??>
${I}if (${field.optional.clause})
        <#else>
${I}if (_in.readBool())
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
    <#if field.runtimeFunction??>
        <#local constructorArguments>_in.read${field.runtimeFunction.suffix}(${field.runtimeFunction.arg!})</#local>
    <#elseif field.array??>
        <#local constructorArguments>_in<@array_read_length field.array/><@array_element_factory compoundName, field/><#rt>
            <#lt><@array_offset_checker field/><@array_element_bit_size field.array/></#local>
    <#elseif field.isEnum>
        <#local constructorArguments>_in</#local>
    <#else>
        <#-- compound -->
        <#local compoundParamsArguments><@compound_field_compound_ctor_params field.compound, false/></#local>
        <#local constructorArguments>_in<#if compoundParamsArguments?has_content>, ${compoundParamsArguments}</#if></#local>
    </#if>
    <@compound_read_set_field field, constructorArguments, indent/>
</#macro>

<#macro compound_read_set_field field constructorArguments indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice.reset(new (<@instantiate_template "m_objectChoice.getResetStorage", field.cppTypeName/>())
${I}        ${field.cppTypeName}(${constructorArguments}));
    <#elseif field.optionalHolder??>
${I}m_${field.name}.reset(new (m_${field.name}.getResetStorage())
${I}        ${field.cppTypeName}(${constructorArguments}));
    <#elseif field.array?? || field.compound??>
${I}m_${field.name}.read(${constructorArguments});
    <#else>
${I}m_${field.name} = (${field.cppTypeName})${constructorArguments};
    </#if>
</#macro>

<#macro compound_field_compound_ctor_params compound useIndirectExpression>
    <#list compound.instantiatedParameters as parameter>
        <#if useIndirectExpression>${parameter.indirectExpression}<#else>${parameter.expression}</#if><#t>
        <#if parameter_has_next>, </#if><#t>
    </#list>
</#macro>

<#macro compound_read_field_prolog field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}_in.alignTo(${field.alignmentValue});
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}_in.alignTo(UINT32_C(8));
${I}if (_in.getBitPosition() != zserio::bytesToBits(${field.offset.getter}))
${I}{
${I}    throw zserio::CppRuntimeException("Read: Wrong offset for field ${compoundName}.${field.name}: " +
${I}            zserio::convertToString(_in.getBitPosition()) + " != " +
${I}            zserio::convertToString(zserio::bytesToBits(${field.offset.getter})) + "!");
${I}}
    </#if>
</#macro>

<#macro compound_pre_write_actions needsRangeCheck needsChildrenInitialization hasFieldWithOffset>
    <#if needsRangeCheck>
    if ((_preWriteAction & zserio::PRE_WRITE_CHECK_RANGES) != 0)
        checkRanges();
    </#if>
    <#if needsChildrenInitialization>
    if ((_preWriteAction & zserio::PRE_WRITE_INITIALIZE_CHILDREN) != 0)
        initializeChildren();
    </#if>
    <#if hasFieldWithOffset>
    if ((_preWriteAction & zserio::PRE_WRITE_INITIALIZE_OFFSETS) != 0)
        initializeOffsets(_out.getBitPosition());
    </#if>
</#macro>

<#macro field_optional_condition field>
    <#if field.optional.clause??>${field.optional.clause}<#else>m_${field.name}.isSet()</#if><#t>
</#macro>

<#macro compound_write_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if (<@field_optional_condition field/>)
${I}{
        <#if !field.optional.clause??>
${I}    _out.writeBool(true);
        </#if>
        <@compound_write_field_inner field, compoundName, indent + 1/>
${I}}
        <#if !field.optional.clause??>
${I}else
${I}{
${I}    _out.writeBool(false);
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
${I}_out.write${field.runtimeFunction.suffix}(<@compound_get_field field/><#if field.runtimeFunction.arg??>,<#rt>
        <#lt> ${field.runtimeFunction.arg}</#if>);
    <#elseif field.array??>
    <@compound_write_field_array_prolog field, compoundName, indent/>
${I}<@compound_get_field field/>.write(_out<@array_auto_length field.array/><@array_offset_checker field/><#rt>
        <#lt><@array_element_bit_size field.array/>);
    <#else>
${I}<@compound_get_field field/>.write(_out, zserio::NO_PRE_WRITE_ACTION);
    </#if>
</#macro>

<#macro compound_write_field_prolog field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}_out.alignTo(${field.alignmentValue});
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}_out.alignTo(UINT32_C(8));
${I}if (_out.getBitPosition() != zserio::bytesToBits(${field.offset.getter}))
${I}{
${I}    throw zserio::CppRuntimeException("Write: Wrong offset for field ${compoundName}.${field.name}: " +
${I}            zserio::convertToString(_out.getBitPosition()) + " != " +
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
${I}const ${cppTypeName} ${lowerBoundVarName} = <@instantiate_template "static_cast", cppTypeName/><#rt>
        <#lt>(zserio::getBitFieldLowerBound(${lengthVarName}, <#if integerRange.checkLowerBound>true<#else>false</#if>));
${I}const ${cppTypeName} ${upperBoundVarName} = <@instantiate_template "static_cast", cppTypeName/><#rt>
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

<#macro compound_read_tree_field field hasNextField compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if field.optional.clause??>
${I}if (${field.optional.clause})
        <#else>
            <#-- check if next tree node exists and if it has auto optional zserio name -->
${I}if (_treeFieldIndex < _tree.getChildren().size() &&
${I}        zserio::getBlobInspectorNode(_tree, _treeFieldIndex).getZserioName().get() ==
${I}        ${rootPackage.name}::InspectorZserioNames::<@inspector_zserio_name field.name/>.get())
        </#if>
${I}{
        <@compound_read_tree_field_inner field, compoundName, indent + 1/>
        <#if hasNextField>
${I}   _treeFieldIndex++;
        </#if>
${I}}
    <#else>
    <@compound_read_tree_field_inner field, compoundName, indent/>
        <#if hasNextField>
${I}_treeFieldIndex++;
        </#if>
    </#if>
</#macro>

<#macro compound_read_tree_field_inner field compoundName indent>
    <#-- be carefull, not to call setters means skip range checking but this is ok for arrays or compounds  -->
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local nodeName>_${field.name}Node</#local>
    <#if field.runtimeFunction??>
        <#local valueName>_${field.name}Value</#local>
${I}const zserio::BlobInspectorNode ${nodeName} = zserio::getBlobInspectorNode(_tree,
${I}    _treeFieldIndex, zserio::BlobInspectorNode::NT_VALUE);
${I}${field.cppTypeName} ${valueName};
${I}${nodeName}.getValue().get(${valueName});
        <@compound_read_set_field field, valueName, indent/>
    <#elseif field.array??>
        <#local arrayArguments>${nodeName}<@array_element_factory compoundName, field/><#rt>
            <#lt><@array_element_bit_size field.array/></#local>
${I}const zserio::BlobInspectorNode ${nodeName} = zserio::getBlobInspectorNode(_tree,
${I}    _treeFieldIndex, zserio::BlobInspectorNode::NT_ARRAY);
        <@compound_read_set_field field, arrayArguments, indent/>
        <@compound_read_tree_field_array_epilog field, compoundName, indent/>
    <#elseif field.isEnum>
        <#local enumValueName>_${field.name}Value</#local>
        <#local enumSymbolName>_${field.name}Symbol</#local>
        <#local enumName>_${field.name}Enum</#local>
${I}const zserio::BlobInspectorNode ${nodeName} = zserio::getBlobInspectorNode(_tree,
${I}    _treeFieldIndex, zserio::BlobInspectorNode::NT_VALUE);
${I}${field.cppTypeName}::_base_type ${enumValueName};
${I}std::string ${enumSymbolName};
${I}${nodeName}.getValue().get(${enumValueName}, ${enumSymbolName});
${I}const ${field.cppTypeName} ${enumName}(${field.cppTypeName}::toEnum(${enumValueName}));
${I}if (${enumSymbolName} != ${enumName}.toString())
${I}{
${I}    throw zserio::CppRuntimeException("Read: Wrong enumeration symbol for field ${compoundName}.${field.name}: " +
${I}            ${enumSymbolName} + " != " + ${enumName}.toString() + "!");
${I}}
        <@compound_read_set_field field, enumName, indent/>
    <#else>
        <#-- compound -->
        <#local compoundParamsArguments><@compound_field_compound_ctor_params field.compound, false/></#local>
        <#local compoundArguments>${nodeName}<#if compoundParamsArguments?has_content>, ${compoundParamsArguments}</#if></#local>
${I}const zserio::BlobInspectorNode ${nodeName} = zserio::getBlobInspectorNode(_tree,
${I}    _treeFieldIndex, zserio::BlobInspectorNode::NT_CONTAINER);
        <@compound_read_set_field field, compoundArguments, indent/>
    </#if>
</#macro>

<#macro compound_read_tree_field_array_epilog field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array.length??>
${I}if (<@compound_get_field field/>.size() != static_cast<size_t>(${field.array.length}))
${I}{
${I}    throw zserio::CppRuntimeException("Read: Wrong array length for field ${compoundName}.${field.name}: " +
${I}            zserio::convertToString(<@compound_get_field field/>.size()) + " != " +
${I}            zserio::convertToString(static_cast<size_t>(${field.array.length})) + "!");
${I}}
    </#if>
</#macro>

<#macro compound_write_tree_field field compoundName rootPackageName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if (<@field_optional_condition field/>)
${I}{
        <@compound_write_tree_field_inner field, compoundName, rootPackageName, indent + 1/>
${I}}
    <#else>
    <@compound_write_tree_field_inner field, compoundName, rootPackageName, indent/>
    </#if>
</#macro>

<#macro compound_write_tree_field_inner field compoundName rootPackageName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <@compound_write_field_prolog field, compoundName, indent/>
    <#local nodeName>_${field.name}Node</#local>
    <#local startBitPositionName>_${field.name}StartBitPosition</#local>
    <#local zserioTypeName>${rootPackageName}::InspectorZserioTypeNames::<@inspector_zserio_type_name field.zserioTypeName/></#local>
    <#local zserioName>${rootPackageName}::InspectorZserioNames::<@inspector_zserio_name field.name/></#local>
${I}const size_t ${startBitPositionName} = _out.getBitPosition();
    <#if field.runtimeFunction??>
${I}zserio::BlobInspectorNode& ${nodeName} = _tree.createChild(zserio::BlobInspectorNode::NT_VALUE,
${I}    ${zserioTypeName},
${I}    ${zserioName});
${I}${nodeName}.getValue().set(<@compound_get_field field/>);
${I}_out.write${field.runtimeFunction.suffix}(<@compound_get_field field/><#if field.runtimeFunction.arg??>,<#rt>
        <#lt> ${field.runtimeFunction.arg}</#if>);
    <#elseif field.array??>
        <@compound_write_field_array_prolog field, compoundName, indent/>
${I}zserio::BlobInspectorNode& ${nodeName} = _tree.createChild(zserio::BlobInspectorNode::NT_ARRAY,
${I}    ${zserioTypeName},
${I}    ${zserioName});
        <#local inspectorElementZserioTypeName><@inspector_zserio_type_name field.array.elementZserioTypeName/></#local>
        <#local elementZserioTypeName>${rootPackageName}::InspectorZserioTypeNames::${inspectorElementZserioTypeName}</#local>
${I}<@compound_get_field field/>.write(_out, ${nodeName},
${I}    ${elementZserioTypeName}<@array_offset_checker field/><@array_element_bit_size field.array/>);
    <#elseif field.isEnum>
${I}zserio::BlobInspectorNode& ${nodeName} = _tree.createChild(zserio::BlobInspectorNode::NT_VALUE,
${I}    ${zserioTypeName},
${I}    ${zserioName});
${I}${nodeName}.getValue().set(<@compound_get_field field/>.getValue(), <#rt>
        <#lt><@compound_get_field field/>.toString());
${I}<@compound_get_field field/>.write(_out, zserio::NO_PRE_WRITE_ACTION);
    <#else>
${I}zserio::BlobInspectorNode& ${nodeName} = _tree.createChild(zserio::BlobInspectorNode::NT_CONTAINER,
${I}    ${zserioTypeName},
${I}    ${zserioName});
${I}<@compound_get_field field/>.write(_out, ${nodeName}, zserio::NO_PRE_WRITE_ACTION);
    </#if>
${I}${nodeName}.setZserioDescriptor(${startBitPositionName}, _out.getBitPosition());
</#macro>

<#macro array_read_length array>
    <#if array.length??>
        , static_cast<size_t>(${array.length})<#t>
    <#elseif array.isImplicit>
        , zserio::ImplicitLength()<#t>
    <#else>
        , zserio::AutoLength()<#t>
    </#if>
</#macro>

<#macro array_auto_length array>
    <#if !array.length?? && !array.isImplicit>
        , zserio::AutoLength()<#t>
    </#if>
</#macro>

<#macro offset_checker_name fieldName>
    _offsetChecker_${fieldName}<#t>
</#macro>

<#macro define_offset_checker compoundName field>
class <@offset_checker_name field.name/>
{
public:
    explicit <@offset_checker_name field.name/>(${compoundName}& owner) : m_owner(owner) {}

    void checkOffset(size_t _index, size_t byteOffset) const
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

<#macro offset_setter_name fieldName>
    _offsetSetter_${fieldName}<#t>
</#macro>

<#macro define_offset_setter compoundName field>
class <@offset_setter_name field.name/>
{
public:
    explicit <@offset_setter_name field.name/>(${compoundName}& owner) : m_owner(owner) {}

    void setOffset(size_t _index, size_t byteOffset) const
    {
        const ${field.offset.typeName} _value = (${field.offset.typeName})byteOffset;
        ${field.offset.indirectSetter};
    }

private:
    ${compoundName}& m_owner;
};
</#macro>

<#macro element_factory_name compoundName fieldName>
    _elementFactory_${compoundName}_${fieldName}<#t>
</#macro>

<#macro define_element_factory compoundName field>
<#local extraConstructorArguments>
    <#if field.array.elementCompound??>
        <@compound_field_compound_ctor_params field.array.elementCompound, true/><#t>
    </#if>
</#local>
class <@element_factory_name compoundName, field.name/>
{
public:
    explicit <@element_factory_name compoundName, field.name/>(${compoundName}& owner) : m_owner(owner) {}

    void create(void* storage, zserio::BitStreamReader& _in, size_t _index)
    {
        (void)_index;
        new (storage) ${field.array.elementCppTypeName}(_in<#if extraConstructorArguments?has_content>, ${extraConstructorArguments}</#if>);
    }

    <#if withInspectorCode>
    void create(void* storage, const zserio::BlobInspectorTree& _tree, size_t _index)
    {
        (void)_index;
        new (storage) ${field.array.elementCppTypeName}(_tree<#if extraConstructorArguments?has_content>, ${extraConstructorArguments}</#if>);
    }

    </#if>
private:
    ${compoundName}& m_owner;
};
</#macro>

<#macro element_initializer_name compoundName fieldName>
    _elementInitializer_${compoundName}_${fieldName}<#t>
</#macro>

<#macro define_element_initializer compoundName field>
class <@element_initializer_name compoundName, field.name/>
{
public:
    explicit <@element_initializer_name compoundName, field.name/>(${compoundName}& owner) : m_owner(owner) {}

    void initialize(${field.array.elementCppTypeName}& element, size_t _index)
    {
        (void)_index;
        element.initialize(<@compound_field_compound_ctor_params field.array.elementCompound, true/>);
    }

private:
    ${compoundName}& m_owner;
};
</#macro>

<#macro element_children_initializer_name compoundName fieldName>
    _elementChildrenInitializer_${compoundName}_${fieldName}<#t>
</#macro>

<#macro define_element_children_initializer compoundName field>
class <@element_children_initializer_name compoundName, field.name/>
{
public:
    <@element_children_initializer_name compoundName, field.name/>() {}

    void initialize(${field.array.elementCppTypeName}& element, size_t)
    {
        element.initializeChildren();
    }
};
</#macro>

<#macro array_offset_checker field>
    <#if field.offset?? && field.offset.containsIndex>, <@offset_checker_name field.name/>(*this)</#if><#t>
</#macro>

<#macro array_element_factory compoundName field>
    <#if field.array?? && field.array.requiresElementFactory>, <@element_factory_name compoundName, field.name/>(*this)</#if><#t>
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
${I}_endBitPosition += <@compound_get_field field/>.bitSizeOf(_endBitPosition<#rt>
        <@array_auto_length field.array/><#t>
        <#if field.offset?? && field.offset.containsIndex>, zserio::Aligned()</#if><#t>
        <#if field.array.requiresElementBitSize>, ${field.array.elementBitSizeValue}</#if><#t>
        <#lt>);
    <#elseif field.bitSizeValue??>
${I}_endBitPosition += ${field.bitSizeValue};
    <#elseif field.runtimeFunction??>
${I}_endBitPosition += zserio::getBitSizeOf${field.runtimeFunction.suffix}(<@compound_get_field field/>);
    <#else>
${I}_endBitPosition += <@compound_get_field field/>.bitSizeOf(_endBitPosition);
    </#if>
</#macro>

<#macro compound_initialize_offsets_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array??>
${I}_endBitPosition = <@compound_get_field field/>.initializeOffsets(_endBitPosition<#rt>
        <@array_auto_length field.array/><#t>
        <#if field.offset?? && field.offset.containsIndex>, <@offset_setter_name field.name/>(*this)</#if><#t>
        <#if field.array.requiresElementBitSize>, ${field.array.elementBitSizeValue}</#if><#t>
        <#lt>);
    <#elseif field.bitSizeValue??>
${I}_endBitPosition += ${field.bitSizeValue};
    <#elseif field.runtimeFunction??>
${I}_endBitPosition += zserio::getBitSizeOf${field.runtimeFunction.suffix}(<@compound_get_field field/>);
    <#else>
${I}_endBitPosition = <@compound_get_field field/>.initializeOffsets(_endBitPosition);
    </#if>
</#macro>

<#macro compound_field_accessors_declaration field>
    <#if field.withWriterCode && !field.isSimpleType>
        <#-- non-const getter is neccessary for setting of offsets -->
    ${field.cppTypeName}& ${field.getterName}();
    </#if>
    ${field.cppArgumentTypeName} ${field.getterName}() const;
    <#if field.withWriterCode>
    void ${field.setterName}(${field.cppArgumentTypeName} ${field.name});
    </#if>
</#macro>

<#macro compound_field_getter_definition field compoundName returnFieldMacroName>
    <#if field.withWriterCode && !field.isSimpleType>
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

void ${compoundName}::${field.setterName}(${field.cppArgumentTypeName} ${field.name})
{
<@.vars[setFieldMacroName] field/>
}
    </#if>
</#macro>

<#macro compound_return_field field>
    return <@compound_get_field field/>;
</#macro>

<#macro compound_set_field field>
    <#if field.usesAnyHolder>
    m_objectChoice.set(${field.name});
    <#elseif field.optionalHolder??>
    m_${field.name}.set(${field.name});
    <#else>
    m_${field.name} = ${field.name};
    </#if>
</#macro>

<#macro compound_get_field field>
    <#if field.usesAnyHolder>
        <@instantiate_template "m_objectChoice.get", field.cppTypeName/>()<#t>
    <#elseif field.optionalHolder??>
        m_${field.name}.get()<#t>
    <#else>
        m_${field.name}<#t>
    </#if>
</#macro>

<#macro compound_copy_constructor_initializer_field field hasNext indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice(_other.m_objectChoice)
    <#else>
${I}m_${field.name}(_other.m_${field.name})<#if hasNext>,</#if>
    </#if>
</#macro>

<#macro compound_assignment_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.usesAnyHolder>
${I}m_objectChoice = _other.m_objectChoice;
    <#else>
${I}m_${field.name} = _other.m_${field.name};
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
                <#lt><@element_initializer_name compoundName, field.name/>(*this));</#local>
        <#elseif field.array.elementCompound.needsChildrenInitialization>
            <#local initializeCommand><@compound_get_field field/>.initializeElements(<#rt>
                <#lt><@element_children_initializer_name compoundName, field.name/>());</#local>
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
