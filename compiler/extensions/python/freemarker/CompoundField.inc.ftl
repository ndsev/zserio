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

<#macro compound_bitsizeof_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if self.${field.optional.indicatorName}():
<@compound_bitsizeof_field_inner field, indent + 1/>
    <#else>
<@compound_bitsizeof_field_inner field, indent/>
    </#if>
</#macro>

<#macro compound_bitsizeof_field_inner field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.bitSize.value??>
${I}endBitPosition += ${field.bitSize.value}
    <#elseif field.bitSize.runtimeFunction??>
${I}endBitPosition += zserio.bitsizeof.getBitSizeOf${field.bitSize.runtimeFunction.suffix}(${field.getterName}())
    <#else>
${I}endBitPosition += ${field.getterName}().bitSizeOf(endBitPosition)
    </#if>
</#macro>

<#macro compound_initialize_offsets_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if self.${field.optional.indicatorName}():
    <@compound_initialize_offsets_field_inner field, indent + 1/>
    <#else>
<@compound_initialize_offsets_field_inner field, indent/>
    </#if>
</#macro>

<#macro compound_initialize_offsets_field_inner field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.bitSize.value??>
${I}endBitPosition += ${field.bitSize.value}
    <#elseif field.bitSize.runtimeFunction??>
${I}endBitPosition += zserio.bitsizeof.getBitSizeOf${field.bitSize.runtimeFunction.suffix}(${field.getterName}())
    <#elseif field.compound?? || field.array??>
${I}endBitPosition = ${field.getterName}().initializeOffsets(__endBitPosition)
    <#else>
${I}endBitPosition += ${field.getterName}().bitSizeOf(__endBitPosition)
    </#if>
</#macro>

<#macro compound_read_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if field.optional.clause??>
${I}if self.${field.optional.indicatorName}():
        <#else>
${I}if reader.readBool():
        </#if>
<@compound_read_field_inner field, compoundName, indent + 1/>
    <#else>
<@compound_read_field_inner field, compoundName, indent/>
    </#if>
</#macro>

<#macro compound_read_field_inner field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}reader.alignTo(${field.alignmentValue})
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
        <@compound_read_field_offset_check field, compoundName, indent/>
    </#if>
    <#local fieldMemberName><@field_member_name field/></#local>
    <#if field.array??>
${I}self.${fieldMemberName} = zserio.Array.fromReader(<@array_field_from_reader_parameters field/>)
    <#elseif field.runtimeFunction??>
${I}self.${fieldMemberName} = reader.read${field.runtimeFunction.suffix}(${field.runtimeFunction.arg!})
    <#else>
        <#local fromReaderArguments><#if field.compound??><@compound_field_constructor_parameters field.compound/></#if></#local>
${I}self.${fieldMemberName} = ${field.pythonTypeName}.fromReader(reader<#rt>
        <#lt><#if fromReaderArguments?has_content>, ${fromReaderArguments}</#if>)
    </#if>
</#macro>

<#macro compound_read_field_offset_check field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}reader.alignTo(8)
${I}if reader.getBitPosition() != zserio.bitposition.bytesToBits(${field.offset.getter}):
${I}    raise PythonRuntimeException("Read: Wrong offset for field ${compoundName}.${field.name}: %d != %d!" %
${I}                                 (reader.getBitPosition(), zserio.bitposition.bytesToBits(${field.offset.getter}))
</#macro>

<#macro array_field_traits_parameters field>
    <#if field.array.requiresElementBitSize>
        ${field.array.elementBitSize.value}<#t>
    <#elseif field.array.requiresElementCreator>
        <@element_creator_name field/><#t>
    </#if>
</#macro>

<#macro array_field_extra_parameters field>
    <#if field.array.isImplicit>, isImplicit=True<#t>
    <#elseif !field.array.length??>, isAuto=True<#t>
    </#if>
    <#if field.offset?? && field.offset.containsIndex>
        , setOffsetMethod=<@offset_setter_name field/>, checkOffsetMethod=<@offset_checker_name field/><#t>
    </#if>
</#macro>

<#macro array_field_from_reader_parameters field>
    <#local extraArrayParameters><@array_field_extra_parameters field/></#local>
    ${field.array.traitsName}(<@array_field_traits_parameters field/>), reader<#t>
    <#if field.array.length??>, ${field.array.length}</#if><#t>
    <#if extraArrayParameters??>${extraArrayParameters}</#if><#t>
</#macro>

<#macro array_field_constructor_parameters field>
    <#local extraArrayParameters><@array_field_extra_parameters field/></#local>
    ${field.array.traitsName}(<@array_field_traits_parameters field/>), <@field_argument_name field/><#t>
    <#if extraArrayParameters??>${extraArrayParameters}</#if><#t>
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
${I}    writer.writeBool(true)
        </#if>
<@compound_write_field_inner field, compoundName, indent + 1/>
        <#if !field.optional.clause??>
${I}else:
${I}    writer.writeBool(false)
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
        <@compound_write_field_offset_check field, compoundName, indent/>
    </#if>
    <#if field.runtimeFunction??>
${I}writer.write${field.runtimeFunction.suffix}(${field.getterName}()<#if field.runtimeFunction.arg??>, ${field.runtimeFunction.arg}</#if>)
    <#else>
        <#if field.array??>
            <#if field.array.length??>
${I}if ${field.getterName}().length() != ${field.array.length}:
${I}    raise PythonRuntimeException("Write: Wrong array length for field ${compoundName}.${field.name}: %d != %d!" %
${I}                                (${field.getterName}().length(), ${field.array.length})
            </#if>
${I}${field.getterName}().write(writer)
        <#else><#-- enum or compound TODO: Enum does NOT have callInitializeOffsets! -->
${I}${field.getterName}().write(writer, callInitializeOffsets=False)
        </#if>
    </#if>
</#macro>

<#macro compound_write_field_offset_check field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}writer.alignTo(8)
${I}if writer.getBitPosition() != zserio.bitposition.bytesToBits(${field.offset.getter}):
${I}    raise PythonRuntimeException("Write: Wrong offset for field ${compoundName}.${field.name}: %d != %d!" %
${I}                                 (writer.getBitPosition(), zserio.bitposition.bytesToBits(${field.offset.getter}))
</#macro>

<#macro compound_check_constraint_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.constraint??>
${I}if <#if field.optional??>(self.${field.optional.indicatorName}()) && </#if>!(${field.constraint}):
${I}    raise PythonRuntimeException("Constraint violated at ${compoundName}.${field.name}!");
    </#if>
</#macro>

<#macro offset_checker_name field>
    _offsetChecker_${field.name}<#t>
</#macro>

<#macro define_offset_checker compoundName field>
    <#if field.array?? && field.offset?? && field.offset.containsIndex>

    def <@offset_checker_name field/>(index, byteOffset):
        if byteOffset != ${field.offset.getter}:
            raise PythonRuntimeException("Wrong offset for field ${compoundName}.${field.name}: %d != %d!" %
                                        (byteOffset, ${field.offset.getter})
    </#if>
</#macro>

<#macro offset_setter_name field>
    _offsetSetter_${field.name}<#t>
</#macro>

<#macro define_offset_setter field>
    <#if field.array?? && field.offset?? && field.offset.containsIndex>

    def <@offset_setter_name field/>(index, byteOffset):
        value = byteOffset
        ${field.offset.setter}
    </#if>
</#macro>

<#macro element_creator_name field>
    _elementCreator_${field.name}<#t>
</#macro>

<#macro define_element_creator field>
   <#if field.array?? && field.array.requiresElementCreator>
        <#local extraConstructorArguments>
            <#if field.array.elementCompound??>
                <@compound_field_constructor_parameters field.array.elementCompound/><#t>
            </#if>
        </#local>

    def <@element_creator_name field/>(reader, index):
        return ${field.array.elementPythonTypeName}.fromReader(reader<#if extraConstructorArguments?has_content>, ${extraConstructorArguments}</#if>)
    </#if>
</#macro>

<#macro field_member_name field>
_${field.name}_<#rt>
</#macro>

<#macro field_argument_name field>
${field.name}_<#rt>
</#macro>

<#macro choice_tag_name field>
    CHOICE_${field.name}<#t>
</#macro>
