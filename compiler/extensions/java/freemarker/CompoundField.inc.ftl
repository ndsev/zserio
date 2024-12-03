<#macro field_member_name field>
    <#if field.usesObjectChoice>
        objectChoice<#t>
    <#else>
        ${field.name}_<#t>
    </#if>
</#macro>

<#macro field_argument_name field>
    ${field.name}_<#t>
</#macro>

<#macro compound_get_field field>
    <#if field.usesObjectChoice>
        <#if field.array??>
            ((${field.array.wrapperJavaTypeName})objectChoice)<#t>
        <#else>
            ${field.getterName}()<#t>
        </#if>
    <#else>
        <@field_member_name field/><#t>
    </#if>
</#macro>

<#macro choice_tag_name field>
    CHOICE_${field.name}<#t>
</#macro>

<#macro array_wrapper_raw_constructor field rawArray indent>
    <@array_wrapper_constructor_inner field, rawArray, indent/>
</#macro>

<#macro array_wrapper_read_constructor field indent>
    <@array_wrapper_constructor_inner field, "", indent/>
</#macro>

<#macro array_wrapper_constructor_inner field rawArray indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
new ${field.array.wrapperJavaTypeName}(
${I}new ${field.array.rawHolderJavaTypeName}(<#rt>
    <#if field.array.requiresElementClass>
        ${field.array.elementTypeInfo.typeFullName}.class<#if rawArray?has_content>, </#if><#t>
    </#if>
    <#if rawArray?has_content>
        ${rawArray}<#t>
    </#if>
    <#lt>),
${I}<@array_traits field/>,
    <#if field.array.length??>
${I}zserio.runtime.array.ArrayType.NORMAL<#rt>
    <#elseif field.array.isImplicit>
${I}zserio.runtime.array.ArrayType.IMPLICIT<#rt>
    <#else>
${I}zserio.runtime.array.ArrayType.AUTO<#rt>
    </#if>
    <#if field.offset?? && field.offset.containsIndex>
    <#lt>,
${I}new <@offset_checker_name field.name/>()<#rt>
        <#if withWriterCode>
    <#lt>,
${I}new <@offset_initializer_name field.name/>()<#rt>
        </#if>
    </#if>
    <#lt>)<#rt>
</#macro>

<#macro array_traits field>
    <#if field.array??>
new ${field.array.arrayTraits.name}(<#rt>
        <#if field.array.arrayTraits.requiresElementBitSize>
        (int)(${field.array.elementBitSize.value})<#t>
        <#elseif field.array.arrayTraits.requiresElementFactory>
        new <@element_factory_name field.name/>()<#t>
        </#if>
        )<#t>
    <#else>
new ${field.typeInfo.arrayableInfo.arrayTraits.name}(<#rt>
        <#if field.typeInfo.arrayableInfo.arrayTraits.requiresElementBitSize>
        (int)(${field.bitSize.value})<#t>
        <#elseif field.typeInfo.arrayableInfo.arrayTraits.requiresElementFactory>
        new <@element_factory_name field.name/>()<#t>
        </#if>
        )<#t>
    </#if>
</#macro>

<#macro array_field_packed_suffix field packed>
    <#if field.isPackable && (packed || field.array.isPacked)>
        Packed<#t>
    </#if>
</#macro>

<#macro compound_field_get_offset field>
    <#if field.offset.typeInfo.requiresBigInt>
        (${field.offset.getter}).longValue()<#t>
    <#else>
        (${field.offset.getter})<#t>
    </#if>
</#macro>

<#macro compound_read_field_offset_check field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}in.alignTo(java.lang.Byte.SIZE);
${I}if (in.getBytePosition() != <@compound_field_get_offset field/>)
${I}{
${I}    throw new zserio.runtime.ZserioError("Read: Wrong offset for field ${compoundName}.${field.name}: " +
${I}            in.getBytePosition() + " != " + <@compound_field_get_offset field/> + "!");
${I}}
</#macro>

<#macro compound_field_compound_ctor_params compound>
    <#list compound.instantiatedParameters as instantiatedParameter>
        <#if instantiatedParameter.typeInfo.isSimple>(${instantiatedParameter.typeInfo.typeFullName})(</#if><#t>
            ${instantiatedParameter.expression}<#t>
        <#if instantiatedParameter.typeInfo.isSimple>)</#if><#t>
        <#if instantiatedParameter?has_next>, </#if><#t>
    </#list>
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
        <@compound_read_field_inner field, compoundName, indent + 1, packed/>
${I}}
    <#else>
        <@compound_read_field_inner field, compoundName, indent, packed/>
    </#if>
</#macro>

<#macro compound_read_field_inner field compoundName indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}in.alignTo(${field.alignmentValue});
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
        <@compound_read_field_offset_check field, compoundName, indent/>
    </#if>
    <#if packed && field.isPackable && !field.array??>
        <#if field.typeInfo.isIntegral>
${I}<@field_member_name field/> = ((${field.typeInfo.arrayableInfo.arrayElement})
${I}        zserioContext.${field.getterName}().read(<@array_traits field/>, in)).get();
        <#elseif field.typeInfo.isEnum>
${I}<@field_member_name field/> = ${field.typeInfo.typeFullName}.readEnum(zserioContext.${field.getterName}(), in);
        <#else>
            <#local compoundParamsArguments>
                <#if field.compound??><#-- can be a bitmask -->
                    <@compound_field_compound_ctor_params field.compound/>
                </#if>
            </#local>
${I}<@field_member_name field/> = new ${field.typeInfo.typeFullName}(zserioContext.${field.getterName}(), in<#rt>
        <#lt><#if compoundParamsArguments?has_content>, ${compoundParamsArguments}</#if>);
        </#if>
    <#elseif field.array??>
${I}<@field_member_name field/> = <@array_wrapper_read_constructor field, indent + 2/>;
${I}<@compound_get_field field/>.read<@array_field_packed_suffix field, packed/>(in<#if field.array.length??>, (int)(${field.array.length})</#if>);
    <#elseif field.runtimeFunction??>
${I}<@field_member_name field/> = <#if field.runtimeFunction.javaReadTypeName??>(${field.runtimeFunction.javaReadTypeName})</#if><#rt>
        <#lt>in.read${field.runtimeFunction.suffix}(${field.runtimeFunction.arg!});
    <#elseif field.typeInfo.isEnum>
${I}<@field_member_name field/> = ${field.typeInfo.typeFullName}.readEnum(in);
    <#else>
        <#-- compound or bitmask -->
        <#local compoundParamsArguments>
            <#if field.compound??><#-- can be a bitmask -->
                <@compound_field_compound_ctor_params field.compound/>
            </#if>
        </#local>
${I}<@field_member_name field/> = new ${field.typeInfo.typeFullName}(in<#rt>
        <#lt><#if compoundParamsArguments?has_content>, ${compoundParamsArguments}</#if>);
    </#if>
</#macro>


<#macro compound_check_offset_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}if (out.getBytePosition() != <@compound_field_get_offset field/>)
${I}{
${I}    throw new zserio.runtime.ZserioError("Write: Wrong offset for field ${compoundName}.${field.name}: " +
${I}            out.getBytePosition() + " != " + <@compound_field_get_offset field/> + "!");
${I}}
</#macro>

<#macro compound_write_field field compoundName indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isExtended>
${I}if (${field.isPresentIndicatorName}())
${I}{
${I}    out.alignTo(java.lang.Byte.SIZE);
        <@compound_write_field_optional field, compoundName, indent+1, packed/>
${I}}
    <#else>
    <@compound_write_field_optional field, compoundName, indent, packed/>
    </#if>
</#macro>

<#macro compound_write_field_optional field compoundName indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if (${field.optional.isUsedIndicatorName}())
${I}{
        <#if !field.optional.clause??>
${I}    out.writeBool(true);
        </#if>
        <@compound_write_field_inner field, compoundName, indent + 1, packed/>
${I}}
        <#if !field.optional.clause??>
${I}else
${I}{
${I}    out.writeBool(false);
${I}}
        </#if>
    <#else>
        <@compound_write_field_inner field, compoundName, indent, packed/>
    </#if>
</#macro>

<#macro compound_write_field_inner field compoundName indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}out.alignTo(${field.alignmentValue});
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}out.alignTo(java.lang.Byte.SIZE);
    <@compound_check_offset_field field, compoundName, indent/>
    </#if>
    <@compound_check_array_length_field field, compoundName, indent/>
    <@compound_check_parameterized_field field, compoundName, indent/>
    <#if packed && field.isPackable && !field.array??>
        <#if field.typeInfo.isIntegral>
${I}zserioContext.${field.getterName}().write(<@array_traits field/>, out,
${I}        new ${field.typeInfo.arrayableInfo.arrayElement}(<@compound_get_field field/>));
        <#else>
${I}<@compound_get_field field/>.write(zserioContext.${field.getterName}(), out);
        </#if>
    <#elseif field.array??>
${I}<@compound_get_field field/>.write<@array_field_packed_suffix field, packed/>(out);
    <#elseif field.runtimeFunction??>
${I}out.write${field.runtimeFunction.suffix}(<@compound_get_field field/><#if field.runtimeFunction.arg??>, ${field.runtimeFunction.arg}</#if>);
    <#else>
        <#-- enum or compound -->
${I}<@compound_get_field field/>.write(out);
    </#if>
</#macro>

<#macro compound_check_constraint_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.constraint??>
${I}if (<#if field.optional??>${field.optional.isUsedIndicatorName}() && </#if>!(${field.constraint}))
${I}    throw new zserio.runtime.ConstraintError("Constraint violated at ${compoundName}.${field.name}!");
    </#if>
</#macro>

<#macro compound_check_array_length_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array?? && field.array.length??>
${I}if (<@compound_get_field field/>.size() != (int)(${field.array.length}))
${I}{
${I}    throw new zserio.runtime.ZserioError("Write: Wrong array length for field ${compoundName}.${field.name}: " +
${I}            <@compound_get_field field/>.size() + " != " + (int)(${field.array.length}) + "!");
${I}}
    </#if>
</#macro>

<#macro compound_check_parameterized_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.compound?? && field.compound.instantiatedParameters?has_content>
${I}// check parameters
        <#list field.compound.instantiatedParameters as instantiatedParameter>
            <#local parameter=field.compound.parameters.list[instantiatedParameter?index]/>
            <#if instantiatedParameter.typeInfo.isSimple>
                <#local instantiatedExpression>
                    (${instantiatedParameter.typeInfo.typeFullName})(${instantiatedParameter.expression})<#t>
                </#local>
                <#if instantiatedParameter.typeInfo.isFloat>
                    <#-- float type: compare by floatToIntBits() to get rid of SpotBugs -->
                    <#local parameterValue>java.lang.Float.floatToIntBits(<@compound_get_field field/>.${parameter.getterName}())</#local>
                    <#local instantiatedValue>java.lang.Float.floatToIntBits(${instantiatedExpression})</#local>
                <#elseif instantiatedParameter.typeInfo.isDouble>
                    <#-- double type: compare by doubleToLongBits() to get rid of SpotBugs -->
                    <#local parameterValue>java.lang.Double.doubleToLongBits(<@compound_get_field field/>.${parameter.getterName}())</#local>
                    <#local instantiatedValue>java.lang.Double.doubleToLongBits(${instantiatedExpression})</#local>
                <#else>
                    <#-- other simple types -->
                    <#local parameterValue><@compound_get_field field/>.${parameter.getterName}()</#local>
                    <#local instantiatedValue>${instantiatedExpression}</#local>
                </#if>
${I}if (${parameterValue} != ${instantiatedValue})
${I}{
${I}    throw new zserio.runtime.ZserioError("Write: Wrong parameter ${parameter.name} for field ${compoundName}.${field.name}: " +
${I}            ${parameterValue} + " != " + ${instantiatedValue} + "!");
${I}}
            <#else>
${I}if (<@compound_get_field field/>.${parameter.getterName}() != (${instantiatedParameter.expression}))
${I}{
${I}    throw new zserio.runtime.ZserioError("Write: Inconsistent parameter ${parameter.name} for field ${compoundName}.${field.name}!");
${I}}
            </#if>
        </#list>
    </#if>
</#macro>

<#macro compound_compare_field field>
    <#if field.typeInfo.isSimple>
        <#if field.typeInfo.isFloat>
            <#-- float type: compare by floatToIntBits() to get rid of SpotBugs -->
java.lang.Float.floatToIntBits(<@compound_get_field field/>) == java.lang.Float.floatToIntBits(that.<@compound_get_field field/>)<#rt>
        <#elseif field.typeInfo.isDouble>
            <#-- double type: compare by doubleToLongBits() to get rid of SpotBugs -->
java.lang.Double.doubleToLongBits(<@compound_get_field field/>) == java.lang.Double.doubleToLongBits(that.<@compound_get_field field/>)<#rt>
        <#else>
            <#-- simple type: compare by == -->
<@compound_get_field field/> == that.<@compound_get_field field/><#rt>
        </#if>
    <#elseif field.typeInfo.isBytes>
((<@compound_get_field field/> == null) ? that.<@compound_get_field field/> == null : java.util.Arrays.equals(<@compound_get_field field/>, that.<@compound_get_field field/>))<#rt>
    <#elseif field.typeInfo.isEnum>
        <#-- enum type: compare by getValue() and == -->
((<@compound_get_field field/> == null) ? that.<@compound_get_field field/> == null : <@compound_get_field field/>.getValue() == that.<@compound_get_field field/>.getValue())<#rt>
    <#else>
        <#-- complex type: compare by equals() but check for possible null -->
((<@compound_get_field field/> == null) ? that.<@compound_get_field field/> == null : <@compound_get_field field/>.equals(that.<@compound_get_field field/>))<#rt>
    </#if>
</#macro>

<#macro compound_align_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}endBitPosition = zserio.runtime.BitPositionUtil.alignTo(${field.alignmentValue}, endBitPosition);
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
${I}endBitPosition = zserio.runtime.BitPositionUtil.alignTo(java.lang.Byte.SIZE, endBitPosition);
    </#if>
</#macro>

<#macro compound_bitsizeof_field field indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isExtended>
${I}if (${field.isPresentIndicatorName}())
${I}{
${I}    endBitPosition = zserio.runtime.BitPositionUtil.alignTo(java.lang.Byte.SIZE, endBitPosition);
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
${I}if (${field.optional.isUsedIndicatorName}())
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
    <#if packed && field.isPackable && !field.array??>
        <#if field.typeInfo.isIntegral>
${I}endBitPosition += zserioContext.${field.getterName}().bitSizeOf(<@array_traits field/>,
${I}        new ${field.typeInfo.arrayableInfo.arrayElement}(<@compound_get_field field/>));
        <#else>
${I}endBitPosition += <@compound_get_field field/>.bitSizeOf(zserioContext.${field.getterName}(), endBitPosition);
        </#if>
    <#elseif field.array??>
${I}endBitPosition += <@compound_get_field field/>.bitSizeOf<@array_field_packed_suffix field, packed/>(endBitPosition);
    <#elseif field.bitSize??>
${I}endBitPosition += ${field.bitSize.value};
    <#elseif field.runtimeFunction??>
${I}endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOf${field.runtimeFunction.suffix}(<@compound_get_field field/>);
    <#else>
${I}endBitPosition += <@compound_get_field field/>.bitSizeOf(endBitPosition);
    </#if>
</#macro>

<#macro compound_initialize_offsets_field field indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isExtended>
${I}if (${field.isPresentIndicatorName}())
${I}{
${I}    endBitPosition = zserio.runtime.BitPositionUtil.alignTo(java.lang.Byte.SIZE, endBitPosition);
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
${I}if (${field.optional.isUsedIndicatorName}())
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
${I}{
${I}    final ${field.offset.typeInfo.typeFullName} value = <#rt>
            <#if field.offset.typeInfo.requiresBigInt>
                <#lt>java.math.BigInteger.valueOf(zserio.runtime.BitPositionUtil.bitsToBytes(endBitPosition));
            <#else>
                <#lt>(${field.offset.typeInfo.typeFullName})zserio.runtime.BitPositionUtil.bitsToBytes(endBitPosition);
            </#if>
${I}    ${field.offset.setter};
${I}}
        </#if>
    <#if packed && field.isPackable && !field.array??>
        <#if field.typeInfo.isIntegral>
${I}endBitPosition += zserioContext.${field.getterName}().bitSizeOf(<@array_traits field/>,
${I}        new ${field.typeInfo.arrayableInfo.arrayElement}(<@compound_get_field field/>));
        <#else>
${I}endBitPosition = <@compound_get_field field/>.initializeOffsets(zserioContext.${field.getterName}(), endBitPosition);
        </#if>
    <#elseif field.array??>
${I}endBitPosition = <@compound_get_field field/>.initializeOffsets<@array_field_packed_suffix field, packed/>(endBitPosition);
    <#elseif field.bitSize??>
${I}endBitPosition += ${field.bitSize.value};
    <#elseif field.runtimeFunction??>
${I}endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOf${field.runtimeFunction.suffix}(<@compound_get_field field/>);
    <#elseif field.compound??>
${I}endBitPosition = <@compound_get_field field/>.initializeOffsets(endBitPosition);
    <#else>
${I}endBitPosition += <@compound_get_field field/>.bitSizeOf(endBitPosition);
    </#if>
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
${I}if (${field.optional.isUsedIndicatorName}())
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
    <#if field.typeInfo.isIntegral>
${I}zserioContext.${field.getterName}().init(<@array_traits field/>,
${I}        new ${field.typeInfo.arrayableInfo.arrayElement}(<@compound_get_field field/>));
    <#else>
${I}<@compound_get_field field/>.initPackingContext(zserioContext.${field.getterName}());
    </#if>
</#macro>

<#macro offset_checker_name fieldName>
    ZserioOffsetChecker_${fieldName}<#t>
</#macro>

<#macro define_offset_checker compoundName field>
    private final class <@offset_checker_name field.name/> implements zserio.runtime.array.OffsetChecker
    {
        @Override
        public void checkOffset(int index, long byteOffset) throws zserio.runtime.ZserioError
        {
            if (byteOffset != <@compound_field_get_offset field/>)
            {
                throw new zserio.runtime.ZserioError("Wrong offset for field ${compoundName}.${field.name}: " +
                        byteOffset + " != " + <@compound_field_get_offset field/> + "!");
            }
        }
    }
</#macro>

<#macro offset_initializer_name fieldName>
    ZserioOffsetInitializer_${fieldName}<#t>
</#macro>

<#macro define_offset_initializer field>
    private final class <@offset_initializer_name field.name/> implements zserio.runtime.array.OffsetInitializer
    {
        @Override
        public void setOffset(int index, long byteOffset)
        {
            final ${field.offset.typeInfo.typeFullName} value = <#rt>
    <#if field.offset.typeInfo.requiresBigInt>
                    <#lt>java.math.BigInteger.valueOf(byteOffset);
    <#else>
                    <#lt>(${field.offset.typeInfo.typeFullName})byteOffset;
    </#if>
            ${field.offset.setter};
        }
    }
</#macro>

<#macro element_factory_name fieldName>
    ZserioElementFactory_${fieldName}<#t>
</#macro>

<#macro define_element_factory field>
    <#local extraConstructorArguments>
        <#if field.array.elementCompound??>
            <@compound_field_compound_ctor_params field.array.elementCompound/><#t>
        </#if>
    </#local>
    private <#if !field.array.requiresOwnerContext>static </#if>final class <@element_factory_name field.name/> <#rt>
        <#lt>implements zserio.runtime.array.<#if field.isPackable && field.array.elementUsedInPackedArray>Packable</#if>ElementFactory<${field.array.elementTypeInfo.typeFullName}>
    {
        @Override
        public ${field.array.elementTypeInfo.typeFullName} create(zserio.runtime.io.BitStreamReader in, int index)
                throws java.io.IOException
        {
    <#if field.array.elementTypeInfo.isEnum>
            return ${field.array.elementTypeInfo.typeFullName}.readEnum(in);
    <#else>
            return new ${field.array.elementTypeInfo.typeFullName}(in<#if extraConstructorArguments?has_content>, ${extraConstructorArguments}</#if>);
    </#if>
        }
    <#if field.isPackable && field.array.elementUsedInPackedArray>

        @Override
        public zserio.runtime.array.PackingContext createPackingContext()
        {
            return new <@field_packing_context_type_name field/>();
        }

        @Override
        public ${field.array.elementTypeInfo.typeFullName} create(zserio.runtime.array.PackingContext context,
                zserio.runtime.io.BitStreamReader in, int index) throws java.io.IOException
        {
        <#if field.array.elementTypeInfo.isEnum>
            return ${field.array.elementTypeInfo.typeFullName}.readEnum(context, in);
        <#else>
            return new ${field.array.elementTypeInfo.typeFullName}(context, in<#if extraConstructorArguments?has_content>, ${extraConstructorArguments}</#if>);
        </#if>
        }
    </#if>
    }
</#macro>

<#macro define_field_helper_classes compoundName field>
    <#if field.array??>
        <#if field.offset?? && field.offset.containsIndex>

            <@define_offset_checker compoundName, field/>
            <#if withWriterCode>

                <@define_offset_initializer field/>
            </#if>
        </#if>
        <#if field.array.arrayTraits.requiresElementFactory>

            <@define_element_factory field/>
        </#if>
    </#if>
</#macro>

<#macro field_packing_context_type_name field>
    <#if field.compound??>
        ${field.typeInfo.typeFullName}.ZserioPackingContext<#t>
    <#elseif field.array?? && field.array.elementCompound??>
        ${field.array.elementTypeInfo.typeFullName}.ZserioPackingContext<#t>
    <#else>
        zserio.runtime.array.DeltaContext<#t>
    </#if>
</#macro>

<#macro compound_declare_packing_context fieldList hasChoiceTag=false>
    <#if withCodeComments>
    /** Defines context structure which keeps additional data needed for packed arrays during compression. */
    </#if>
    public static final class ZserioPackingContext extends zserio.runtime.array.PackingContext
    {
    <#if hasChoiceTag || uses_packing_context(fieldList)>
        public ZserioPackingContext()
        {
            <#if hasChoiceTag>
            choiceTag = new zserio.runtime.array.DeltaContext();
            </#if>
            <#list fieldList as field>
                <#if uses_field_packing_context(field) && !(field.optional?? && field.optional.isRecursive)>
            ${field.name}_ = new <@field_packing_context_type_name field/>();
                </#if>
            </#list>
        }

        <#if hasChoiceTag>
        public zserio.runtime.array.DeltaContext choiceTag()
        {
            return choiceTag;
        }

        </#if>
        <#list fieldList as field>
            <#if uses_field_packing_context(field)>
        public <@field_packing_context_type_name field/> ${field.getterName}()
        {
                <#if field.optional?? && field.optional.isRecursive>
            return this;
                <#else>
            return ${field.name}_;
                </#if>
        }

            </#if>
        </#list>
        <#if hasChoiceTag>
        private zserio.runtime.array.DeltaContext choiceTag;
        </#if>
        <#list fieldList as field>
            <#if uses_field_packing_context(field) && !(field.optional?? && field.optional.isRecursive)>
        private <@field_packing_context_type_name field/> ${field.name}_;
            </#if>
        </#list>
    </#if>
    };
</#macro>

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
