<#include "ArrayTraits.inc.ftl"/>
<#macro compound_compare_field field>
    <#-- if optional is not auto and is used the other should be is used as well because all previous paramaters and fields were the same. -->
    <#if field.optional??>(not other.${field.optional.isUsedIndicatorName}() if not self.${field.optional.isUsedIndicatorName}() else </#if><#t>
(self.<@field_member_name field/> == other.<@field_member_name field/>)<#if field.optional??>)</#if><#rt>
</#macro>

<#macro compound_hashcode_field field indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if self.${field.optional.isUsedIndicatorName}():
    <@compound_hashcode_field_inner field, indent+1/>
    <#else>
        <@compound_hashcode_field_inner field, indent/>
    </#if>
</#macro>

<#macro compound_hashcode_field_inner field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}result = zserio.hashcode.calc_hashcode_${field.typeInfo.hashCodeFunc.suffix}(<#rt>
        <#lt>result, self.<@field_member_name field/>)
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

<#macro compound_setter_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array??>
        <#if field.optional??>
${I}if <@field_argument_name field/> is None:
${I}    self.<@field_member_name field/> = None
${I}else:
${I}    self.<@field_member_name field/> = <@array_field_constructor field/>
        <#else>
${I}self.<@field_member_name field/> = <@array_field_constructor field/>
        </#if>
    <#else>
${I}self.<@field_member_name field/> = <@field_argument_name field/>
    </#if>
</#macro>

<#macro choice_tag_name field>
    CHOICE_${field.snakeCaseName?upper_case}<#t>
</#macro>

<#macro compound_bitsizeof_field field indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isExtended>
${I}if self.${field.isPresentIndicatorName}():
${I}    end_bitposition = zserio.bitposition.alignto(8, end_bitposition)
        <@compound_bitsizeof_field_optional field, indent+1, packed/>
    <#else>
    <@compound_bitsizeof_field_optional field, indent, packed/>
    </#if>
</#macro>

<#macro compound_bitsizeof_field_optional field indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if !field.optional.clause??>
            <#-- auto optional field -->
${I}end_bitposition += 1
        </#if>
${I}if self.${field.optional.isUsedIndicatorName}():
<@compound_bitsizeof_field_inner field, indent+1, packed/>
    <#else>
<@compound_bitsizeof_field_inner field, indent, packed/>
    </#if>
</#macro>

<#macro compound_bitsizeof_field_inner field indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <@compound_align_field field, indent/>
    <#if packed && uses_field_packing_context(field)>
        <#if field.typeInfo.isBuiltin>
${I}end_bitposition += zserio_context.${field.propertyName}.bitsizeof(<#rt>
        <#lt><@array_traits_create_field field/>, self.<@field_member_name field/>)
        <#else>
${I}end_bitposition += self.<@field_member_name field/>.bitsizeof_packed(<#rt>
        <#lt>zserio_context.${field.propertyName}, end_bitposition)
        </#if>
    <#else>
        <#if field.bitSize.value??>
${I}end_bitposition += ${field.bitSize.value}
        <#elseif field.bitSize.runtimeFunction??>
${I}end_bitposition += zserio.bitsizeof.bitsizeof_${field.bitSize.runtimeFunction.suffix}(self.<@field_member_name field/>)
        <#elseif field.array??>
${I}end_bitposition += self.<@field_member_name field/>.bitsizeof<@array_field_packed_suffix field, packed/>(end_bitposition)
        <#else>
${I}end_bitposition += self.<@field_member_name field/>.bitsizeof(end_bitposition)
        </#if>
    </#if>
</#macro>

<#macro compound_align_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}end_bitposition = zserio.bitposition.alignto(${field.alignmentValue}, end_bitposition)
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}end_bitposition = zserio.bitposition.alignto(8, end_bitposition)
    </#if>
</#macro>

<#macro compound_initialize_offsets_field field indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isExtended>
${I}if self.${field.isPresentIndicatorName}():
${I}    end_bitposition = zserio.bitposition.alignto(8, end_bitposition)
        <@compound_initialize_offsets_field_optional field, indent+1, packed/>
    <#else>
    <@compound_initialize_offsets_field_optional field, indent, packed/>
    </#if>
</#macro>

<#macro compound_initialize_offsets_field_optional field indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if !field.optional.clause??>
            <#-- auto optional field -->
${I}end_bitposition += 1
        </#if>
${I}if self.${field.optional.isUsedIndicatorName}():
    <@compound_initialize_offsets_field_inner field, indent+1, packed/>
    <#else>
<@compound_initialize_offsets_field_inner field, indent, packed/>
    </#if>
</#macro>

<#macro compound_initialize_offsets_field_inner field indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <@compound_align_field field, indent/>
    <#if field.offset?? && !field.offset.containsIndex>
${I}# initialize offset
${I}value = zserio.bitposition.bits_to_bytes(end_bitposition)
${I}${field.offset.setter}
    </#if>
    <#if packed && uses_field_packing_context(field)>
        <#if field.typeInfo.isBuiltin>
${I}end_bitposition += zserio_context.${field.propertyName}.bitsizeof(<#rt>
        <#lt><@array_traits_create_field field/>, self.<@field_member_name field/>)
        <#else>
${I}end_bitposition = self.<@field_member_name field/>.initialize_offsets_packed(<#rt>
        <#lt>zserio_context.${field.propertyName}, end_bitposition)
        </#if>
    <#else>
        <#if field.bitSize.value??>
${I}end_bitposition += ${field.bitSize.value}
        <#elseif field.bitSize.runtimeFunction??>
${I}end_bitposition += zserio.bitsizeof.bitsizeof_${field.bitSize.runtimeFunction.suffix}(self.<@field_member_name field/>)
        <#elseif field.array??>
${I}end_bitposition = self.<@field_member_name field/>.initialize_offsets<@array_field_packed_suffix field, packed/>(end_bitposition)
        <#else>
${I}end_bitposition = self.<@field_member_name field/>.initialize_offsets(end_bitposition)
        </#if>
    </#if>
</#macro>

<#macro compound_read_field field compoundName indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if field.optional.clause??>
${I}if self.${field.optional.isUsedIndicatorName}():
        <#else>
${I}if zserio_reader.read_bool():
        </#if>
<@compound_read_field_inner field, compoundName, indent+1, packed/>
${I}else:
${I}    self.<@field_member_name field/> = None
    <#else>
<@compound_read_field_inner field, compoundName, indent, packed/>
    </#if>
</#macro>

<#macro compound_read_field_inner field compoundName indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}zserio_reader.alignto(${field.alignmentValue})
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}zserio_reader.alignto(8)
        <@compound_check_offset_field field, compoundName, "zserio_reader.bitposition", indent/>
    </#if>
    <#if packed && uses_field_packing_context(field)>
        <#if field.typeInfo.isBuiltin>
${I}self.<@field_member_name field/> = zserio_context.${field.propertyName}.read(<#rt>
        <#lt><@array_traits_create_field field/>, zserio_reader)
        <#else>
            <#local fromReaderArguments><#if field.compound??><@compound_field_constructor_parameters field.compound/></#if></#local>
${I}self.<@field_member_name field/> = ${field.typeInfo.typeFullName}.from_reader_packed(<#rt>
        zserio_context.${field.propertyName}, <#t>
        <#lt>zserio_reader<#if fromReaderArguments?has_content>, ${fromReaderArguments}</#if>)
        </#if>
    <#else>
        <#if field.array??>
${I}self.<@field_member_name field/> = <@array_field_from_reader field, packed/>
        <#elseif field.runtimeFunction??>
${I}self.<@field_member_name field/> = zserio_reader.read_${field.runtimeFunction.suffix}(${field.runtimeFunction.arg!})
        <#else>
            <#local fromReaderArguments><#if field.compound??><@compound_field_constructor_parameters field.compound/></#if></#local>
${I}self.<@field_member_name field/> = ${field.typeInfo.typeFullName}.from_reader(zserio_reader<#rt>
        <#lt><#if fromReaderArguments?has_content>, ${fromReaderArguments}</#if>)
        </#if>
    </#if>
    <@compound_check_constraint_field field, compoundName, indent/>
</#macro>

<#macro array_field_traits_parameter field>
    zserio.array.${field.typeInfo.arrayTraits.name}(<#t>
        <#if field.typeInfo.arrayTraits.requiresElementBitSize>
            ${field.array.elementBitSize.value}<#t>
        <#elseif field.typeInfo.arrayTraits.requiresElementFactory>
            self.<@element_factory_name field/>(<#if field.array.requiresOwnerContext>self</#if>)<#t>
        </#if>
    )<#t>
</#macro>

<#macro array_field_keyword_parameters field>
    <#if field.array.isImplicit>, is_implicit=True<#t>
    <#elseif !field.array.length??>, is_auto=True<#t>
    </#if>
    <#if field.offset?? && field.offset.containsIndex>
        <#if withWriterCode>, set_offset_method=self.<@offset_setter_name field/></#if><#t>
        , check_offset_method=self.<@offset_checker_name field/><#t>
    </#if>
</#macro>

<#macro array_field_from_reader field packed=false>
    zserio.array.Array.from_reader<@array_field_packed_suffix field, packed/>(<#t>
            <@array_field_traits_parameter field/>, zserio_reader<#t>
            <#if field.array.length??>, ${field.array.length}</#if><#t>
            <@array_field_keyword_parameters field/>)<#t>
</#macro>

<#macro array_field_packed_suffix field packed>
    <#if field.isPackable && (packed || field.array.isPacked)>
        _packed<#t>
    </#if>
</#macro>

<#macro array_field_constructor field>
    zserio.array.Array(<@array_field_traits_parameter field/>, <#t>
            <@field_argument_name field/><@array_field_keyword_parameters field/>)<#t>
</#macro>

<#macro compound_field_constructor_parameters compound>
    <#list compound.instantiatedParameters as instantiatedParameter>
        ${instantiatedParameter.expression}<#if instantiatedParameter_has_next>, </#if><#t>
    </#list>
</#macro>

<#macro compound_field_element_factory_parameters compound>
    <#list compound.instantiatedParameters as instantiatedParameter>
        ${instantiatedParameter.indirectExpression}<#if instantiatedParameter_has_next>, </#if><#t>
    </#list>
</#macro>

<#macro compound_write_field field compoundName indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isExtended>
${I}if self.${field.isPresentIndicatorName}():
${I}    zserio_writer.alignto(8)
        <@compound_write_field_optional field, compoundName, indent+1, packed/>
    <#else>
    <@compound_write_field_optional field, compoundName, indent, packed/>
    </#if>
</#macro>

<#macro compound_write_field_optional field compoundName indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if self.${field.optional.isUsedIndicatorName}():
        <#if !field.optional.clause??>
${I}    zserio_writer.write_bool(True)
        </#if>
<@compound_write_field_inner field, compoundName, indent+1, packed/>
        <#if !field.optional.clause??>
${I}else:
${I}    zserio_writer.write_bool(False)
        </#if>
    <#else>
<@compound_write_field_inner field, compoundName, indent, packed/>
    </#if>
</#macro>

<#macro compound_write_field_inner field compoundName indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}zserio_writer.alignto(${field.alignmentValue})
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}zserio_writer.alignto(8)
        <@compound_check_offset_field field, compoundName, "zserio_writer.bitposition", indent/>
    </#if>
    <@compound_check_constraint_field field, compoundName, indent/>
    <@compound_check_array_length_field field, compoundName, indent/>
    <@compound_check_parameterized_field field, compoundName, indent/>
    <@compound_check_range_field field, compoundName, indent/>
    <#if packed && uses_field_packing_context(field)>
        <#if field.typeInfo.isBuiltin>
${I}zserio_context.${field.propertyName}.write(<@array_traits_create_field field/>, <#rt>
        <#lt>zserio_writer, self.<@field_member_name field/>)
        <#else>
${I}self.<@field_member_name field/>.write_packed(zserio_context.${field.propertyName}, zserio_writer)
        </#if>
    <#else>
        <#if field.runtimeFunction??>
${I}zserio_writer.write_${field.runtimeFunction.suffix}(self.<@field_member_name field/><#if field.runtimeFunction.arg??>, ${field.runtimeFunction.arg}</#if>)
        <#elseif field.array??>
${I}self.<@field_member_name field/>.write<@array_field_packed_suffix field, packed/>(zserio_writer)
        <#else>
${I}self.<@field_member_name field/>.write(zserio_writer)
        </#if>
    </#if>
</#macro>

<#macro compound_check_offset_field field compoundName bitPositionName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.offset??>
${I}# check offset
${I}if ${bitPositionName} != zserio.bitposition.bytes_to_bits(${field.offset.getter}):
${I}    raise zserio.PythonRuntimeException("Wrong offset for field ${compoundName}.${field.name}: "
${I}                                        f"{${bitPositionName}} != {zserio.bitposition.bytes_to_bits(${field.offset.getter})}!")
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
${I}if len(self.<@field_member_name field/>) != (${field.array.length}):
${I}    raise zserio.PythonRuntimeException("Wrong array length for field ${compoundName}.${field.name}: "
${I}                                        f"{len(self.<@field_member_name field/>)} != {${field.array.length}}!")
    </#if>
</#macro>

<#macro compound_check_parameterized_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.compound?? && field.compound.instantiatedParameters?has_content>
${I}# check parameters
        <#list field.compound.instantiatedParameters as instantiatedParameter>
            <#local parameter=field.compound.parameters.list[instantiatedParameter?index]/>
            <#local compareOperator><#if parameter.typeInfo.isBuiltin>!=<#else>is not</#if><#t></#local>
${I}if self.<@field_member_name field/>.${parameter.propertyName} ${compareOperator} (${instantiatedParameter.expression}):
${I}    raise zserio.PythonRuntimeException("Wrong parameter ${parameter.name} for field ${compoundName}.${field.name}: "
${I}                                        f"{self.<@field_member_name field/>.${parameter.propertyName}} != {${instantiatedParameter.expression}}!")
        </#list>
    </#if>
</#macro>

<#macro compound_check_range_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.rangeCheck??>
        <#local rangeCheck=field.rangeCheck/>
${I}# check range
        <#if rangeCheck.bitFieldWithExpression??>
${I}length = ${rangeCheck.bitFieldWithExpression.lengthExpression}
${I}lowerbound = zserio.bitfield.<#if rangeCheck.bitFieldWithExpression.isSigned>signed_</#if>bitfield_lowerbound(length)
${I}upperbound = zserio.bitfield.<#if rangeCheck.bitFieldWithExpression.isSigned>signed_</#if>bitfield_upperbound(length)
        <#else>
${I}lowerbound = ${rangeCheck.lowerBound}
${I}upperbound = ${rangeCheck.upperBound}
        </#if>
${I}if self.<@field_member_name field/> < lowerbound or self.<@field_member_name field/> > upperbound:
${I}    raise zserio.PythonRuntimeException(f"Value {self.<@field_member_name field/>} for field "
${I}                                        "${compoundName}.${field.name} is out of range: "
${I}                                        f"<{lowerbound}, {upperbound}>!")
    </#if>
</#macro>

<#macro field_annotation_type_name field compoundName>
    <#if field.optional??>
        <#if field.optional.isRecursive>
        typing.Optional['${compoundName}']<#t>
        <#else>
        typing.Optional[${field.typeInfo.typeFullName}]<#t>
        </#if>
    <#elseif field.typeInfo.isBuiltin || field.array??>
        ${field.typeInfo.typeFullName}<#t>
    <#else>
        typing.Union[${field.typeInfo.typeFullName}, None]<#t>
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
        typing.List[${fieldArray.elementTypeInfo.typeFullName}]<#t>
    </#if>
</#macro>

<#macro field_annotation_argument_type_choice_name field compoundName>
    <#if field.array??>
        typing.Union[<@field_annotation_array_argument_type_name field.array, compoundName/>, None]<#t>
    <#else>
        typing.Union[${field.typeInfo.typeFullName}, None]<#t>
    </#if>
</#macro>

<#macro offset_checker_name field>
    _offset_checker_${field.snakeCaseName}<#t>
</#macro>

<#macro define_offset_checker field compoundName>
    <#if field.array?? && field.offset?? && field.offset.containsIndex>

    def <@offset_checker_name field/>(self, zserio_index: int, bitoffset: int) -> None:
        <@compound_check_offset_field field, compoundName, "bitoffset", 2/>
    </#if>
</#macro>

<#macro offset_setter_name field>
    _offset_setter_${field.snakeCaseName}<#t>
</#macro>

<#macro define_offset_setter field>
    <#if field.array?? && field.offset?? && field.offset.containsIndex>

    def <@offset_setter_name field/>(self, zserio_index: int, bitoffset: int) -> None:
        value = zserio.bitposition.bits_to_bytes(bitoffset)
        ${field.offset.setter}
    </#if>
</#macro>

<#macro element_factory_name field>
    _ZserioElementFactory_${field.snakeCaseName}<#t>
</#macro>

<#macro define_element_factory field compoundName>
   <#if field.array?? && field.typeInfo.arrayTraits.requiresElementFactory>
        <#local usesElementFactoryIndex = false/>
        <#local extraConstructorArguments>
            <#if field.array.elementCompound??>
                <@compound_field_element_factory_parameters field.array.elementCompound/><#t>
                <#list field.array.elementCompound.instantiatedParameters as parameter>
                    <#if parameter.containsIndex>
                        <#local usesElementFactoryIndex = true/>
                        <#break>
                    </#if>
                </#list>
            </#if>
        </#local>

    class <@element_factory_name field/>:
        IS_OBJECT_PACKABLE = <#if field.isPackable && field.array.elementUsedInPackedArray>True<#else>False</#if>
        <#if field.array.requiresOwnerContext>

        def __init__(self, owner):
            self._owner = owner
        </#if>

        <#if !field.array.requiresOwnerContext>
        @staticmethod
        </#if>
        def create(<#if field.array.requiresOwnerContext>self, </#if><#rt>
                   zserio_reader: zserio.BitStreamReader, zserio_index: int) -> <#t>
        <#if field.array.elementIsRecursive>
                <#lt>'${compoundName}':
        <#else>
                <#lt>${field.array.elementTypeInfo.typeFullName}:
        </#if>
        <#if !usesElementFactoryIndex>
            del zserio_index
        </#if>
            return ${field.array.elementTypeInfo.typeFullName}.from_reader(zserio_reader<#if extraConstructorArguments?has_content>, ${extraConstructorArguments}</#if>)
        <#if field.isPackable && field.array.elementUsedInPackedArray>

        @staticmethod
        def create_packing_context() -> <@field_packing_context_type_name field/>:
            return <@field_packing_context_type_name field/>()

        <#if !field.array.requiresOwnerContext>
        @staticmethod
        </#if>
        def create_packed(<#if field.array.requiresOwnerContext>self, </#if><#rt>
                          <#lt>zserio_context: <@field_packing_context_type_name field/>,
                          zserio_reader: zserio.BitStreamReader, zserio_index: int) -> <#rt>
            <#if field.array.elementIsRecursive>
                <#lt>'${compoundName}':
            <#else>
                <#lt>${field.array.elementTypeInfo.typeFullName}:
            </#if>
            <#if !usesElementFactoryIndex>
            del zserio_index
            </#if>
            return ${field.array.elementTypeInfo.typeFullName}.from_reader_packed(zserio_context, zserio_reader<#if extraConstructorArguments?has_content>, ${extraConstructorArguments}</#if>)
        </#if>
    </#if>
</#macro>

<#macro field_member_name field>
    <#if field.usesChoiceMember>_choice<#else>_${field.snakeCaseName}_</#if><#t>
</#macro>

<#macro field_argument_name field>
    ${field.snakeCaseName}_<#t>
</#macro>

<#macro compound_init_packing_context_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if uses_field_packing_context(field)>
        <#if field.isExtended>
${I}if self.${field.isPresentIndicatorName}():
        <@compound_init_packing_context_field_optional field, indent+1/>
        <#else>
    <@compound_init_packing_context_field_optional field, indent/>
        </#if>
    </#if>
</#macro>

<#macro compound_init_packing_context_field_optional field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if self.${field.optional.isUsedIndicatorName}():
    <@compound_init_packing_context_field_inner field, indent+1/>
    <#else>
<@compound_init_packing_context_field_inner field, indent/>
    </#if>
</#macro>

<#macro compound_init_packing_context_field_inner field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.typeInfo.isBuiltin>
${I}zserio_context.${field.propertyName}.init(<@array_traits_create_field field/>, <#rt>
        <#lt>self.<@field_member_name field/>)
    <#else>
${I}self.<@field_member_name field/>.init_packing_context(zserio_context.${field.propertyName})
    </#if>
</#macro>

<#macro field_packing_context_type_name field>
    <#if field.compound??>
        ${field.typeInfo.typeFullName}.ZserioPackingContext<#t>
    <#elseif field.array?? && field.array.elementCompound??>
        ${field.array.elementTypeInfo.typeFullName}.ZserioPackingContext<#t>
    <#else>
        zserio.array.DeltaContext<#t>
    </#if>
</#macro>

<#macro define_packing_context fieldList hasChoiceTag=false>
    class ZserioPackingContext:
    <#if withCodeComments>
        """
        Defines context structure which keeps additional data needed for packed arrays during compression.
        """
    </#if>
    <#if hasChoiceTag || uses_packing_context(fieldList)>
        <#if withCodeComments>

        </#if>
        def __init__(self):
            <#local has_packing_context_member=false/>
        <#if hasChoiceTag>
            <#local has_packing_context_member=true/>
            self._choice_tag = zserio.array.DeltaContext()
        </#if>
        <#list fieldList as field>
            <#if uses_field_packing_context(field) && !(field.optional?? && field.optional.isRecursive)>
                <#local has_packing_context_member=true/>
            self._${field.snakeCaseName}_ = <@field_packing_context_type_name field/>()
            </#if>
        </#list>
        <#if !has_packing_context_member>
            pass
        </#if>
        <#if hasChoiceTag>

        @property
        def choice_tag(self):
            return self._choice_tag
        </#if>
        <#list fieldList as field>
            <#if uses_field_packing_context(field)>

        @property
        def ${field.propertyName}(self):
                <#if field.optional?? && field.optional.isRecursive>
            return self
                <#else>
            return self._${field.snakeCaseName}_
                </#if>
            </#if>
        </#list>
    <#elseif !withCodeComments>
        pass
    </#if>
</#macro>

<#function has_field_any_read_check_code field compoundName indent>
    <#local checkCode>
        <#if field.offset?? && !field.offset.containsIndex>
            <@compound_check_offset_field field, compoundName, "zserio_reader.bitposition", indent/>
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
            <@compound_check_offset_field field, compoundName, "zserio_writer.bitposition", indent/>
        </#if>
        <@compound_check_constraint_field field, compoundName, indent/>
        <@compound_check_array_length_field field, compoundName, indent/>
        <@compound_check_parameterized_field field, compoundName, indent/>
        <@compound_check_range_field field, compoundName, indent/>
    </#local>
    <#if checkCode == "">
        <#return false>
    </#if>

    <#return true>
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
