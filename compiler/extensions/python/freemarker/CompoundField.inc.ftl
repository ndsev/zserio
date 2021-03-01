<#macro compound_compare_field field>
    <#if field.optional??>(not self.${field.optional.indicatorName}() or </#if><#t>
self.<@field_member_name field/> == other.<@field_member_name field/><#if field.optional??>)</#if><#rt>
</#macro>

<#macro compound_hashcode_field field>
    <#if field.optional??>
        if self.${field.optional.indicatorName}():
            <@compound_hashcode_field_inner field, 3/>
    <#else>
        <@compound_hashcode_field_inner field, 2/>
    </#if>
</#macro>

<#macro compound_hashcode_field_inner field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}result = zserio.hashcode.calc_hashcode(result, hash(self.<@field_member_name field/>))
</#macro>

<#macro compound_getter_field field>
    <#if field.array??>
        <#if field.optional??>
        return None if self.<@field_member_name field/> is None else self.<@field_member_name field/>.raw_array
        <#else>
        return self.<@field_member_name field/>.raw_array
        </#if>
    <#else>
        return self.<@field_member_name field/>
    </#if>
</#macro>

<#macro compound_setter_field field withWriterCode indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array??>
        <#if field.optional??>
${I}if <@field_argument_name field/> is None:
${I}    self.<@field_member_name field/> = None
${I}else:
${I}    self.<@field_member_name field/> = zserio.array.Array(<@array_field_constructor_parameters field, withWriterCode/>)
        <#else>
${I}self.<@field_member_name field/> = zserio.array.Array(<@array_field_constructor_parameters field, withWriterCode/>)
        </#if>
    <#else>
${I}self.<@field_member_name field/> = <@field_argument_name field/>
    </#if>
</#macro>

<#macro compound_bitsizeof_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if !field.optional.clause??>
            <#-- auto optional field -->
${I}endBitPosition += 1
        </#if>
${I}if self.${field.optional.indicatorName}():
<@compound_bitsizeof_field_inner field, indent + 1/>
    <#else>
<@compound_bitsizeof_field_inner field, indent/>
    </#if>
</#macro>

<#macro compound_bitsizeof_field_inner field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <@compound_align_field field, indent/>
    <#if field.bitSize.value??>
${I}endBitPosition += ${field.bitSize.value}
    <#elseif field.bitSize.runtimeFunction??>
${I}endBitPosition += zserio.bitsizeof.bitsizeof_${field.bitSize.runtimeFunction.suffix}(self.<@field_member_name field/>)
    <#elseif field.array??><#-- TODO[mikir] should be removed after renaming -->
${I}endBitPosition += self.<@field_member_name field/>.bitsizeof(endBitPosition)
    <#else>
${I}endBitPosition += self.<@field_member_name field/>.bitSizeOf(endBitPosition)
    </#if>
</#macro>

<#macro compound_align_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}endBitPosition = zserio.bitposition.alignto(${field.alignmentValue}, endBitPosition)
    </#if>
    <#if field.offset??>
        <#if field.offset.containsIndex>
            <#-- align to bytes only if the array is non-empty to match read/write behavior -->
${I}if self.<@field_member_name field/>:
${I}    endBitPosition = zserio.bitposition.alignto(8, endBitPosition)
        <#else>
${I}endBitPosition = zserio.bitposition.alignto(8, endBitPosition)
        </#if>
    </#if>
</#macro>

<#macro compound_initialize_offsets_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if !field.optional.clause??>
            <#-- auto optional field -->
${I}endBitPosition += 1
        </#if>
${I}if self.${field.optional.indicatorName}():
    <@compound_initialize_offsets_field_inner field, indent + 1/>
    <#else>
<@compound_initialize_offsets_field_inner field, indent/>
    </#if>
</#macro>

<#macro compound_initialize_offsets_field_inner field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <@compound_align_field field, indent/>
    <#if field.offset?? && !field.offset.containsIndex>
${I}# initialize offset
${I}value = zserio.bitposition.bits_to_bytes(endBitPosition)
${I}${field.offset.setter}
    </#if>
    <#if field.bitSize.value??>
${I}endBitPosition += ${field.bitSize.value}
    <#elseif field.bitSize.runtimeFunction??>
${I}endBitPosition += zserio.bitsizeof.bitsizeof_${field.bitSize.runtimeFunction.suffix}(self.<@field_member_name field/>)
    <#elseif field.array??><#-- TODO[mikir] should be removed after renaming -->
${I}endBitPosition = self.<@field_member_name field/>.initialize_offsets(endBitPosition)
    <#else>
${I}endBitPosition = self.<@field_member_name field/>.initializeOffsets(endBitPosition)
    </#if>
</#macro>

<#macro compound_read_field field compoundName withWriterCode indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if field.optional.clause??>
${I}if self.${field.optional.indicatorName}():
        <#else>
${I}if reader.read_bool():
        </#if>
<@compound_read_field_inner field, compoundName, withWriterCode, indent + 1/>
    <#else>
<@compound_read_field_inner field, compoundName, withWriterCode, indent/>
    </#if>
</#macro>

<#macro compound_read_field_inner field compoundName withWriterCode indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}reader.alignto(${field.alignmentValue})
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}reader.alignto(8)
        <@compound_check_offset_field field, compoundName, "reader.bitposition", indent/>
    </#if>
    <#if field.array??>
${I}self.<@field_member_name field/> = zserio.array.Array.from_reader(<@array_field_from_reader_parameters field, withWriterCode/>)
    <#elseif field.runtimeFunction??>
${I}self.<@field_member_name field/> = reader.read_${field.runtimeFunction.suffix}(${field.runtimeFunction.arg!})
    <#else>
        <#local fromReaderArguments><#if field.compound??><@compound_field_constructor_parameters field.compound/></#if></#local>
${I}self.<@field_member_name field/> = ${field.pythonTypeName}.fromReader(reader<#rt>
        <#lt><#if fromReaderArguments?has_content>, ${fromReaderArguments}</#if>)
    </#if>
    <@compound_check_constraint_field field, compoundName, indent/>
</#macro>

<#macro array_field_traits_parameter field>
    zserio.array.${field.array.traitsName}<#t>
        (<#t>
    <#if field.array.requiresElementBitSize>
        ${field.array.elementBitSize.value}<#t>
    <#elseif field.array.requiresElementCreator>
        self.<@element_creator_name field/><#t>
    </#if>
        )<#t>
</#macro>

<#macro array_field_keyword_parameters field withWriterCode>
    <#if field.array.isImplicit>, is_implicit=True<#t>
    <#elseif !field.array.length??>, is_auto=True<#t>
    </#if>
    <#if field.offset?? && field.offset.containsIndex>
        <#if withWriterCode>, set_offset_method=self.<@offset_setter_name field/></#if><#t>
        , check_offset_method=self.<@offset_checker_name field/><#t>
    </#if>
</#macro>

<#macro array_field_from_reader_parameters field withWriterCode>
    <@array_field_traits_parameter field/><#t>
    , reader<#t>
    <#if field.array.length??>, ${field.array.length}</#if><#t>
    <@array_field_keyword_parameters field, withWriterCode/><#t>
</#macro>

<#macro array_field_constructor_parameters field withWriterCode>
    <@array_field_traits_parameter field/><#t>
    , <@field_argument_name field/><#t>
    <@array_field_keyword_parameters field, withWriterCode/><#t>
</#macro>

<#macro compound_field_constructor_parameters compound>
    <#list compound.instantiatedParameters as parameter>
        ${parameter.expression}<#if parameter_has_next>, </#if><#t>
    </#list>
</#macro>

<#macro compound_write_field field compoundName, indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if self.${field.optional.indicatorName}():
        <#if !field.optional.clause??>
${I}    writer.write_bool(True)
        </#if>
<@compound_write_field_inner field, compoundName, indent + 1/>
        <#if !field.optional.clause??>
${I}else:
${I}    writer.write_bool(False)
        </#if>
    <#else>
<@compound_write_field_inner field, compoundName, indent/>
    </#if>
</#macro>

<#macro compound_write_field_inner field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}writer.alignto(${field.alignmentValue})
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}writer.alignto(8)
        <@compound_check_offset_field field, compoundName, "writer.bitposition", indent/>
    </#if>
    <@compound_check_constraint_field field, compoundName, indent/>
    <@compound_check_array_length_field field, compoundName, indent/>
    <@compound_check_range_field field, compoundName, indent/>
    <#if field.runtimeFunction??>
${I}writer.write_${field.runtimeFunction.suffix}(self.<@field_member_name field/><#if field.runtimeFunction.arg??>, ${field.runtimeFunction.arg}</#if>)
    <#else>
${I}self.<@field_member_name field/>.write(writer<#if field.compound??>, callInitializeOffsets=False</#if>)
    </#if>
</#macro>

<#macro compound_check_offset_field field compoundName bitPositionName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.offset??>
${I}# check offset
${I}if ${bitPositionName} != zserio.bitposition.bytes_to_bits(${field.offset.getter}):
${I}    raise zserio.PythonRuntimeException("Wrong offset for field ${compoundName}.${field.name}: %d != %d!" %
${I}                                        (${bitPositionName}, zserio.bitposition.bytes_to_bits(${field.offset.getter})))
    </#if>
</#macro>

<#macro compound_check_constraint_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.constraint??>
${I}# check constraint
${I}if not (${field.constraint}):
${I}    raise zserio.PythonRuntimeException("Constraint violated for field ${compoundName}.${field.name}!")
    </#if>
</#macro>

<#macro compound_check_array_length_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array?? && field.array.length??>
${I}# check array length
${I}if len(self.<@field_member_name field/>) != ${field.array.length}:
${I}    raise zserio.PythonRuntimeException("Wrong array length for field ${compoundName}.${field.name}: %d != %d!" %
${I}                                        (len(self.<@field_member_name field/>), ${field.array.length}))
    </#if>
</#macro>

<#macro compound_check_range_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.rangeCheck??>
        <#local rangeCheck=field.rangeCheck/>
${I}# check range
        <#if rangeCheck.bitFieldWithExpression??>
${I}length = ${rangeCheck.bitFieldWithExpression.lengthExpression}
${I}lowerBound = zserio.bitfield.<#if rangeCheck.bitFieldWithExpression.isSigned>signed_</#if>bitfield_lowerbound(length)
${I}upperBound = zserio.bitfield.<#if rangeCheck.bitFieldWithExpression.isSigned>signed_</#if>bitfield_upperbound(length)
        <#else>
${I}lowerBound = ${rangeCheck.lowerBound}
${I}upperBound = ${rangeCheck.upperBound}
        </#if>
${I}if self.<@field_member_name field/> < lowerBound or self.<@field_member_name field/> > upperBound:
${I}    raise zserio.PythonRuntimeException("Value %d for field ${compoundName}.${field.name} is out range: <%d, %d>!" %
${I}                                        (self.<@field_member_name field/>, lowerBound, upperBound))
    </#if>
</#macro>

<#macro field_annotation_type_name field compoundName>
    <#if field.optional??>
        <#if field.optional.isRecursive>
        typing.Optional['${compoundName}']<#t>
        <#else>
        typing.Optional[${field.pythonTypeName}]<#t>
        </#if>
    <#elseif field.isBuiltinType || field.array??>
        ${field.pythonTypeName}<#t>
    <#else>
        typing.Union[${field.pythonTypeName}, None]<#t>
    </#if>
</#macro>

<#macro field_annotation_argument_type_name field compoundName>
    <#if field.array??>
        <#local arrayAnnotationType><@field_annotation_array_argument_type_name field.array, compoundName/></#local>
        <#if field.optional??>
            typing.Optional[${arrayAnnotationType}]<#t>
        <#else>
            ${arrayAnnotationType}<#t>
        </#if>
    <#else>
        <@field_annotation_type_name field, compoundName/>
    </#if>
</#macro>

<#macro field_annotation_array_argument_type_name fieldArray compoundName>
    <#if fieldArray.elementIsRecursive>
        typing.List['${compoundName}']<#t>
    <#else>
        typing.List[${fieldArray.elementPythonTypeName}]<#t>
    </#if>
</#macro>

<#macro field_annotation_argument_type_choice_name field compoundName>
    <#if field.array??>
        typing.Union[<@field_annotation_array_argument_type_name field.array, compoundName/>, None]<#t>
    <#else>
        typing.Union[${field.pythonTypeName}, None]<#t>
    </#if>
</#macro>

<#macro offset_checker_name field>
    _offsetChecker_${field.name}<#t>
</#macro>

<#macro define_offset_checker field compoundName>
    <#if field.array?? && field.offset?? && field.offset.containsIndex>

    def <@offset_checker_name field/>(self, index: int, bitOffset: int) -> None:
        <@compound_check_offset_field field, compoundName, "bitOffset", 2/>
    </#if>
</#macro>

<#macro offset_setter_name field>
    _offsetSetter_${field.name}<#t>
</#macro>

<#macro define_offset_setter field>
    <#if field.array?? && field.offset?? && field.offset.containsIndex>

    def <@offset_setter_name field/>(self, index: int, bitOffset: int) -> None:
        value = zserio.bitposition.bits_to_bytes(bitOffset)
        ${field.offset.setter}
    </#if>
</#macro>

<#macro element_creator_name field>
    _elementCreator_${field.name}<#t>
</#macro>

<#macro define_element_creator field compoundName>
   <#if field.array?? && field.array.requiresElementCreator>
        <#local usesElementCreatorIndex = false/>
        <#local extraConstructorArguments>
            <#if field.array.elementCompound??>
                <@compound_field_constructor_parameters field.array.elementCompound/><#t>
                <#list field.array.elementCompound.instantiatedParameters as parameter>
                    <#if parameter.containsIndex>
                        <#local usesElementCreatorIndex = true/>
                        <#break>
                    </#if>
                </#list>
            </#if>
        </#local>

    def <@element_creator_name field/>(self, reader: zserio.BitStreamReader, <#if !usesElementCreatorIndex>_</#if>index: int) -> <#rt>
        <#if field.array.elementIsRecursive>
            <#lt>'${compoundName}':
        <#else>
            <#lt>${field.array.elementPythonTypeName}:
        </#if>
        return ${field.array.elementPythonTypeName}.fromReader(reader<#if extraConstructorArguments?has_content>, ${extraConstructorArguments}</#if>)
    </#if>
</#macro>

<#macro field_member_name field>
    <#if field.usesChoiceMember>_choice<#else>_${field.name}_</#if><#t>
</#macro>

<#macro field_argument_name field>
    ${field.name}_<#t>
</#macro>

<#function has_field_any_read_check_code field compoundName indent>
    <#local checkCode>
        <#if field.offset?? && !field.offset.containsIndex>
            <@compound_check_offset_field field, compoundName, "reader.bitposition", indent/>
        </#if>
        <@compound_check_constraint_field field, compoundName, indent/>
    </#local>
    <#if checkCode == "">
        <#return false>
    </#if>

    <#return true>
</#function>

<#function has_field_any_write_check_code field compoundName indent>
    <#local checkCode>
        <#if field.offset?? && !field.offset.containsIndex>
            <@compound_check_offset_field field, compoundName, "writer.bitposition", indent/>
        </#if>
        <@compound_check_constraint_field field, compoundName, indent/>
        <@compound_check_array_length_field field, compoundName, indent/>
        <@compound_check_range_field field, compoundName, indent/>
    </#local>
    <#if checkCode == "">
        <#return false>
    </#if>

    <#return true>
</#function>
