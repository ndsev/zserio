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
${I}result = zserio.hashcode.calcHashCode(result, hash(self.<@field_member_name field/>))
</#macro>

<#macro compound_getter_field field>
    <#if field.array??>
        return None if self.<@field_member_name field/> is None else self.<@field_member_name field/>.getRawArray()
    <#else>
        return self.<@field_member_name field/>
    </#if>
</#macro>

<#macro compound_setter_field field>
    <#if field.array??>
        self.<@field_member_name field/> = zserio.array.Array(<@array_field_constructor_parameters field true/>)
    <#else>
        self.<@field_member_name field/> = <@field_argument_name field/>
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
${I}endBitPosition += zserio.bitsizeof.getBitSizeOf${field.bitSize.runtimeFunction.suffix}(self.<@field_member_name field/>)
    <#else>
${I}endBitPosition += self.<@field_member_name field/>.bitSizeOf(endBitPosition)
    </#if>
</#macro>

<#macro compound_align_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}endBitPosition = zserio.bitposition.alignTo(${field.alignmentValue}, endBitPosition)
    </#if>
    <#if field.offset??>
        <#if field.offset.containsIndex>
            <#-- align to bytes only if the array is non-empty to match read/write behavior -->
${I}if self.<@field_member_name field/>:
${I}    endBitPosition = zserio.bitposition.alignTo(8, endBitPosition)
        <#else>
${I}endBitPosition = zserio.bitposition.alignTo(8, endBitPosition)
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
${I}value = zserio.bitposition.bitsToBytes(endBitPosition)
${I}${field.offset.setter}
    </#if>
    <#if field.bitSize.value??>
${I}endBitPosition += ${field.bitSize.value}
    <#elseif field.bitSize.runtimeFunction??>
${I}endBitPosition += zserio.bitsizeof.getBitSizeOf${field.bitSize.runtimeFunction.suffix}(self.<@field_member_name field/>)
    <#elseif field.compound?? || field.array??>
${I}endBitPosition = self.<@field_member_name field/>.initializeOffsets(endBitPosition)
    <#else>
${I}endBitPosition += self.<@field_member_name field/>.bitSizeOf(endBitPosition)
    </#if>
</#macro>

<#macro compound_read_field field compoundName withWriterCode indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if field.optional.clause??>
${I}if self.${field.optional.indicatorName}():
        <#else>
${I}if reader.readBool():
        </#if>
<@compound_read_field_inner field, compoundName, withWriterCode, indent + 1/>
    <#else>
<@compound_read_field_inner field, compoundName, withWriterCode, indent/>
    </#if>
</#macro>

<#macro compound_read_field_inner field compoundName withWriterCode indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}reader.alignTo(${field.alignmentValue})
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}reader.alignTo(8)
        <@compound_check_offset_field field, compoundName, "reader.getBitPosition()", indent/>
    </#if>
    <#if field.array??>
${I}self.<@field_member_name field/> = zserio.array.Array.fromReader(<@array_field_from_reader_parameters field, withWriterCode/>)
    <#elseif field.runtimeFunction??>
${I}self.<@field_member_name field/> = reader.read${field.runtimeFunction.suffix}(${field.runtimeFunction.arg!})
    <#else>
        <#local fromReaderArguments><#if field.compound??><@compound_field_constructor_parameters field.compound/></#if></#local>
${I}self.<@field_member_name field/> = ${field.pythonTypeName}.fromReader(reader<#rt>
        <#lt><#if fromReaderArguments?has_content>, ${fromReaderArguments}</#if>)
    </#if>
    <@compound_check_constraint_field field, compoundName, indent/>
</#macro>

<#macro array_field_traits_parameters field>
    <#if field.array.requiresElementBitSize>
        ${field.array.elementBitSize.value}<#t>
    <#elseif field.array.requiresElementCreator>
        self.<@element_creator_name field/><#t>
    </#if>
</#macro>

<#macro array_field_parameters field forReader withWriterCode>
    zserio.array.${field.array.traitsName}(<@array_field_traits_parameters field/>)<#t>
    <#if forReader>
        , reader<#t>
        <#if field.array.length??>, ${field.array.length}</#if><#t>
    <#else>
        , <@field_argument_name field/><#t>
    </#if>
    <#if field.array.isImplicit>, isImplicit=True<#t>
    <#elseif !field.array.length??>, isAuto=True<#t>
    </#if>
    <#if field.offset?? && field.offset.containsIndex>
        <#if withWriterCode>, setOffsetMethod=self.<@offset_setter_name field/></#if><#t>
        , checkOffsetMethod=self.<@offset_checker_name field/><#t>
    </#if>
</#macro>

<#macro array_field_from_reader_parameters field withWriterCode>
    <@array_field_parameters field, true, withWriterCode/>
</#macro>

<#macro array_field_constructor_parameters field withWriterCode>
    <@array_field_parameters field, false, withWriterCode/>
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
${I}    writer.writeBool(True)
        </#if>
<@compound_write_field_inner field, compoundName, indent + 1/>
        <#if !field.optional.clause??>
${I}else:
${I}    writer.writeBool(False)
        </#if>
    <#else>
<@compound_write_field_inner field, compoundName, indent/>
    </#if>
</#macro>

<#macro compound_write_field_inner field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}writer.alignTo(${field.alignmentValue})
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}writer.alignTo(8)
        <@compound_check_offset_field field, compoundName, "writer.getBitPosition()", indent/>
    </#if>
    <@compound_check_constraint_field field, compoundName, indent/>
    <@compound_check_array_length_field field, compoundName, indent/>
    <@compound_check_range_field field, compoundName, indent/>
    <#if field.runtimeFunction??>
${I}writer.write${field.runtimeFunction.suffix}(self.<@field_member_name field/><#if field.runtimeFunction.arg??>, ${field.runtimeFunction.arg}</#if>)
    <#else>
${I}self.<@field_member_name field/>.write(writer<#if field.compound??>, callInitializeOffsets=False</#if>)
    </#if>
</#macro>

<#macro compound_check_offset_field field compoundName bitPositionName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.offset??>
${I}# check offset
${I}if ${bitPositionName} != zserio.bitposition.bytesToBits(${field.offset.getter}):
${I}    raise zserio.PythonRuntimeException("Wrong offset for field ${compoundName}.${field.name}: %d != %d!" %
${I}                                        (${bitPositionName}, zserio.bitposition.bytesToBits(${field.offset.getter})))
    </#if>
</#macro>

<#macro compound_check_constraint_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.constraint??>
${I}# check constraint
${I}if <#if field.optional??>self.${field.optional.indicatorName}() and </#if>not (${field.constraint}):
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
${I}lowerBound = zserio.bitfield.get<#if rangeCheck.bitFieldWithExpression.isSigned>Signed</#if>BitFieldLowerBound(length)
${I}upperBound = zserio.bitfield.get<#if rangeCheck.bitFieldWithExpression.isSigned>Signed</#if>BitFieldUpperBound(length)
        <#else>
${I}lowerBound = ${rangeCheck.lowerBound}
${I}upperBound = ${rangeCheck.upperBound}
        </#if>
${I}if self.<@field_member_name field/> < lowerBound or self.<@field_member_name field/> > upperBound:
${I}    raise zserio.PythonRuntimeException("Value %d for field ${compoundName}.${field.name} is out range: <%d, %d>!" %
${I}                                        (self.<@field_member_name field/>, lowerBound, upperBound))
    </#if>
</#macro>

<#macro offset_checker_name field>
    _offsetChecker_${field.name}<#t>
</#macro>

<#macro define_offset_checker compoundName field>
    <#if field.array?? && field.offset?? && field.offset.containsIndex>

    def <@offset_checker_name field/>(self, index, bitOffset):
        <@compound_check_offset_field field, compoundName, "bitOffset", 2/>
    </#if>
</#macro>

<#macro offset_setter_name field>
    _offsetSetter_${field.name}<#t>
</#macro>

<#macro define_offset_setter field>
    <#if field.array?? && field.offset?? && field.offset.containsIndex>

    def <@offset_setter_name field/>(self, index, bitOffset):
        value = zserio.bitposition.bitsToBytes(bitOffset)
        ${field.offset.setter}
    </#if>
</#macro>

<#macro element_creator_name field>
    _elementCreator_${field.name}<#t>
</#macro>

<#macro define_element_creator field>
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

    def <@element_creator_name field/>(self, reader, <#if !usesElementCreatorIndex>_</#if>index):
        return ${field.array.elementPythonTypeName}.fromReader(reader<#if extraConstructorArguments?has_content>, ${extraConstructorArguments}</#if>)
    </#if>
</#macro>

<#macro field_member_name field>
    <#if field.usesChoiceMember>_choice<#else>_${field.name}_</#if><#t>
</#macro>

<#macro field_argument_name field>
    ${field.name}_<#t>
</#macro>

<#macro choice_tag_name field>
    CHOICE_${field.name}<#t>
</#macro>

<#function has_field_any_read_check_code field compoundName indent>
    <#local checkCode>
        <#if field.offset?? && !field.offset.containsIndex>
            <@compound_check_offset_field field, compoundName, "reader.getBitPosition()", indent/>
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
            <@compound_check_offset_field field, compoundName, "writer.getBitPosition()", indent/>
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
