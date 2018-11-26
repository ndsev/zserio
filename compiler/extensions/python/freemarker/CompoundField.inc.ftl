<#macro compound_compare_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>(not self.${field.optional.indicatorName}() or </#if><#t>
self.<@field_member_name field/> == other.<@field_member_name field/><#if field.optional??>)</#if><#rt>
    <#if field_has_next>
 and
${I}<#rt>
    </#if>
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

<#macro compound_bitsizeof_field field>
    <#if field.optional??>
        if self.${field.optional.indicatorName}():
            <@compound_bitsizeof_field_inner field, 3/>
    <#else>
        <@compound_bitsizeof_field_inner field, 2/>
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

<#macro compound_initialize_offsets_field field>
    <#if field.optional??>
        if self.${field.optional.indicatorName}():
            <@compound_initialize_offsets_field_inner field, 3/>
    <#else>
        <@compound_initialize_offsets_field_inner field, 2/>
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

<#macro compound_read_field field compoundName>
    <#if field.optional??>
        <#if field.optional.clause??>
        if self.${field.optional.indicatorName}():
        <#else>
        if reader.readBool():
        </#if>
            <@compound_read_field_inner field, compoundName, 3/>
    <#else>
        <@compound_read_field_inner field, compoundName, 2/>
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
    <#local fieldMemberName><@field_member_name field.name/></#local>

    <#if field.array??>
${I}${fieldMemberName} = zserio.Array.fromReader(${field.array.traitsName}(), reader, <#rt>
        <#lt><#if field.array.length??>${field.array.length}<#rt>
        <#lt><#elseif field.array.isImplicit>isImplicit=True<#rt>
        <#lt><#else>isAuto=True</#if><#rt>
        <#if field.offset?? && field.offset.containsIndex>
            <#lt>, setOffsetMethod=None, checkOffsetMethod=None<#rt>  <#-- TODO -->
        </#if>
        <#lt>)
    <#elseif field.runtimeFunction??>
${I}${fieldMemberName} = reader.read${field.runtimeFunction.suffix}(${field.runtimeFunction.arg!});
    <#else>
        <#local fromReaderArguments><#if field.compound??><@compound_field_compound_ctor_params field.compound/></#if></#local>
${I}${fieldMemberName} = ${field.pythonTypeName}.fromReader(reader<#rt>
        <#lt><#if fromReaderArguments?has_content>, ${fromReaderArguments}</#if>)
    </#if>
</#macro>

<#macro compound_read_field_offset_check field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}reader.alignTo(8)
${I}if reader.getBitPosition() != 8 * ${field.offset.getter}:
${I}    raise PythonRuntimeException("Read: Wrong offset for field ${compoundName}.${field.name}: %d != %d!" %
${I}                                 (reader.getBitPosition(), 8 * ${field.offset.getter})
</#macro>

<#macro compound_field_compound_ctor_params compound>
    <#list compound.instantiatedParameters as parameter>
        ${parameter.expression}<#if parameter_has_next>, </#if><#t>
    </#list>
</#macro>

<#macro compound_write_field field compoundName>
    <#if field.optional??>
        if <@field_optional_condition field/>:
        <#if !field.optional.clause??>
            writer.writeBool(true)
        </#if>
            <@compound_write_field_inner field, compoundName, 3/>
        <#if !field.optional.clause??>
        else:
            writer.writeBool(false)
        </#if>
    <#else>
        <@compound_write_field_inner field, compoundName, 2/>
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
        <#if field.array?? && field.array.length??>
${I}if ${field.getterName}().length() != ${field.array.length}:
${I}    raise PythonRuntimeException("Write: Wrong array length for field ${compoundName}.${field.name}: %d != %d!" %
${I}                                (${field.getterName}().length(), ${field.array.length})
        </#if>
${I}${field.getterName}().write(writer);
    </#if>
</#macro>

<#macro field_optional_condition field>
    <#if field.optional.clause??>${field.optional.clause}<#else>${field.getterName}() != None</#if><#t>
</#macro>

<#macro compound_write_field_offset_check field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}writer.alignTo(8)
${I}if writer.getBitPosition() != 8 * ${field.offset.getter}:
${I}    raise PythonRuntimeException("Write: Wrong offset for field ${compoundName}.${field.name}: %d != %d!" %
${I}                                 (writer.getBitPosition(), 8 * ${field.offset.getter})
</#macro>

<#macro compound_check_constraint_field field compoundName>
    <#if field.constraint??>
        if <#if field.optional??>(<@field_optional_condition field/>) && </#if>!(${field.constraint}):
            raise PythonRuntimeException("Constraint violated at ${compoundName}.${field.name}!");
    </#if>
</#macro>

<#macro field_member_name field>
_${field.name}_<#rt>
</#macro>
