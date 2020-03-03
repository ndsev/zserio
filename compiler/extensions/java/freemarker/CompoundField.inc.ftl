<#macro compound_field_get_offset field>
    <#if field.offset.requiresBigInt>
        ${field.offset.getter}.longValue()<#t>
    <#else>
        ${field.offset.getter}<#t>
    </#if>
</#macro>

<#macro compound_read_field_offset_check field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}__in.alignTo(java.lang.Byte.SIZE);
${I}if (__in.getBytePosition() != <@compound_field_get_offset field/>)
${I}{
${I}    throw new zserio.runtime.ZserioError("Read: Wrong offset for field ${compoundName}.${field.name}: " +
${I}            __in.getBytePosition() + " != " + <@compound_field_get_offset field/> + "!");
${I}}
</#macro>

<#macro compound_read_field_inner field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}__in.alignTo(${field.alignmentValue});
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
        <@compound_read_field_offset_check field, compoundName, indent/>
    </#if>
    <#local fieldMemberName><#if field.usesObjectChoice>__objectChoice<#else>${field.name}</#if></#local>
    <#if field.array??>
${I}${fieldMemberName} = new ${field.javaTypeName}(__in,
        <#if field.array.length??>
${I}        (int)(${field.array.length})<#rt>
        <#elseif field.array.isImplicit>
${I}        zserio.runtime.array.Array.IMPLICIT_LENGTH<#rt>
        <#else>
${I}        zserio.runtime.array.Array.AUTO_LENGTH<#rt>
        </#if>
        <#if field.array.requiresElementBitSize>
            , ${field.array.elementBitSize.value}<#t>
        </#if>
        <#if field.array.requiresElementFactory>
            <#lt>,
${I}        new <@element_factory_name field.name/>()<#rt>
        </#if>
        <#if field.offset?? && field.offset.containsIndex>
            , new <@offset_checker_name field.name/>()<#t>
        </#if>
        <#lt>);
    <#elseif field.runtimeFunction??>
${I}${fieldMemberName} = <#if field.runtimeFunction.javaReadTypeName??>(${field.runtimeFunction.javaReadTypeName})</#if><#rt>
        <#lt>__in.read${field.runtimeFunction.suffix}(${field.runtimeFunction.arg!});
    <#elseif field.isEnum>
${I}${fieldMemberName} = ${field.javaTypeName}.readEnum(__in);
    <#else>
        <#-- compound or bitmask -->
        <#local compoundParamsArguments>
            <#if field.compound??><#-- can be a bitmask -->
                <@compound_field_compound_ctor_params field.compound/>
            </#if>
        </#local>
        <#local compoundArguments>__in<#if compoundParamsArguments?has_content>, ${compoundParamsArguments}</#if></#local>
${I}${fieldMemberName} = new ${field.javaTypeName}(${compoundArguments});
    </#if>
</#macro>

<#macro compound_field_compound_ctor_params compound>
    <#list compound.instantiatedParameters as parameter>
        <#if parameter.isSimpleType>(${parameter.javaTypeName})(</#if>${parameter.expression}<#if parameter.isSimpleType>)</#if><#t>
        <#if parameter_has_next>, </#if><#t>
    </#list>
</#macro>

<#macro compound_read_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#if field.optional.clause??>
${I}if (${field.optional.clause})
        <#else>
${I}if (__in.readBool())
        </#if>
${I}{
        <@compound_read_field_inner field, compoundName, indent + 1/>
${I}}
    <#else>
        <@compound_read_field_inner field, compoundName, indent/>
    </#if>
</#macro>

<#macro compound_write_field_offset_check field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}__out.alignTo(java.lang.Byte.SIZE);
${I}if (__out.getBytePosition() != <@compound_field_get_offset field/>)
${I}{
${I}    throw new zserio.runtime.ZserioError("Write: Wrong offset for field ${compoundName}.${field.name}: " +
${I}            __out.getBytePosition() + " != " + <@compound_field_get_offset field/> + "!");
${I}}
</#macro>

<#macro compound_write_field_inner field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}__out.alignTo(${field.alignmentValue});
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
        <@compound_write_field_offset_check field, compoundName, indent/>
    </#if>
    <#if field.array??>
        <#if field.array.length??>
${I}if (${field.getterName}().length() != (int)(${field.array.length}))
${I}{
${I}    throw new zserio.runtime.ZserioError("Write: Wrong array length for field ${compoundName}.${field.name}: " +
${I}            ${field.getterName}().length() + " != " + (int)(${field.array.length}) + "!");
${I}}
        </#if>
${I}${field.getterName}().write<@array_runtime_function_name_postfix field/>(__out<#rt>
        <#if field.array.requiresElementBitSize>
            , ${field.array.elementBitSize.value}<#t>
        </#if>
        <#if field.offset?? && field.offset.containsIndex>
            , new <@offset_checker_name field.name/>()<#t>
        </#if>
        <#lt>);
    <#elseif field.runtimeFunction??>
${I}__out.write${field.runtimeFunction.suffix}(${field.getterName}()<#if field.runtimeFunction.arg??>, ${field.runtimeFunction.arg}</#if>);
    <#else>
        <#-- enum or compound -->
${I}${field.getterName}().write(__out, false);
    </#if>
</#macro>

<#macro field_optional_condition field>
    <#if field.optional.clause??>${field.optional.clause}<#else>${field.name} != null</#if><#t>
</#macro>

<#macro compound_write_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if (<@field_optional_condition field/>)
${I}{
        <#if !field.optional.clause??>
${I}    __out.writeBool(true);
        </#if>
        <@compound_write_field_inner field, compoundName, indent + 1/>
${I}}
        <#if !field.optional.clause??>
${I}else
${I}{
${I}    __out.writeBool(false);
${I}}
        </#if>
    <#else>
        <@compound_write_field_inner field, compoundName, indent/>
    </#if>
</#macro>

<#macro compound_check_constraint_field field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.constraint??>
${I}if (<#if field.optional??>(<@field_optional_condition field/>) && </#if>!(${field.constraint}))
${I}    throw new zserio.runtime.ConstraintError("Constraint violated at ${compoundName}.${field.name}!");
    </#if>
</#macro>

<#macro compound_compare_field field>
    <#if field.isSimpleType>
        <#if field.isFloat>
            <#-- float type: compare by floatToIntBits() to get rid of SpotBugs -->
java.lang.Float.floatToIntBits(this.${field.name}) == java.lang.Float.floatToIntBits(__that.${field.name})<#rt>
        <#elseif field.isDouble>
            <#-- double type: compare by doubleToLongBits() to get rid of SpotBugs -->
java.lang.Double.doubleToLongBits(this.${field.name}) == java.lang.Double.doubleToLongBits(__that.${field.name})<#rt>
        <#else>
            <#-- simple type: compare by == -->
this.${field.name} == __that.${field.name}<#rt>
        </#if>
    <#elseif field.isEnum>
        <#-- enum type: compare by getValue() and == -->
((this.${field.name} == null) ? __that.${field.name} == null : this.${field.name}.getValue() == __that.${field.name}.getValue())<#rt>
    <#else>
        <#-- complex type: compare by equals() but account for possible null -->
((this.${field.name} == null) ? __that.${field.name} == null : this.${field.name}.equals(__that.${field.name}))<#rt>
    </#if>
</#macro>

<#macro array_runtime_function_name_postfix field>
    <#if field.offset?? && field.offset.containsIndex>
        Aligned<#t>
    </#if>
    <#if !field.array.length?? && !field.array.isImplicit>
        Auto<#t>
    </#if>
</#macro>

<#macro compound_bitsizeof_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array??>
${I}__endBitPosition += ${field.getterName}().bitSizeOf<@array_runtime_function_name_postfix field/>(__endBitPosition<#rt>
        <#if field.array.requiresElementBitSize>
            , ${field.array.elementBitSize.value}<#t>
        </#if>
        <#lt>);
    <#elseif field.bitSize.value??>
${I}__endBitPosition += ${field.bitSize.value};
    <#elseif field.bitSize.runtimeFunction??>
${I}__endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOf${field.bitSize.runtimeFunction.suffix}(${field.getterName}());
    <#else>
${I}__endBitPosition += ${field.getterName}().bitSizeOf(__endBitPosition);
    </#if>
</#macro>

<#macro compound_field_initialize_offsets field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array??>
${I}__endBitPosition = ${field.getterName}().initializeOffsets<@array_runtime_function_name_postfix field/>(__endBitPosition<#rt>
        <#if field.array.requiresElementBitSize>
            , ${field.array.elementBitSize.value}<#t>
        </#if>
        <#if field.offset?? && field.offset.containsIndex>
            , new <@offset_setter_name field.name/>()<#t>
        </#if>
        <#lt>);
    <#elseif field.bitSize.value??>
${I}__endBitPosition += ${field.bitSize.value};
    <#elseif field.bitSize.runtimeFunction??>
${I}__endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOf${field.bitSize.runtimeFunction.suffix}(${field.getterName}());
    <#elseif field.compound??>
${I}__endBitPosition = ${field.getterName}().initializeOffsets(__endBitPosition);
    <#else>
${I}__endBitPosition += ${field.getterName}().bitSizeOf(__endBitPosition);
    </#if>
</#macro>

<#macro compound_hashcode_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isSimpleType>
        <#if field.isLong>
            <#-- long type: use shifting -->
${I}__result = zserio.runtime.Util.HASH_PRIME_NUMBER * __result + (int)(${field.name} ^ (${field.name} >>> 32));
        <#elseif field.isFloat>
            <#-- float type: use floatToIntBits() -->
${I}__result = zserio.runtime.Util.HASH_PRIME_NUMBER * __result + java.lang.Float.floatToIntBits(${field.name});
        <#elseif field.isDouble>
            <#-- double type: use doubleToLongBits() -->
${I}__result = zserio.runtime.Util.HASH_PRIME_NUMBER * __result +
${I}        (int)(java.lang.Double.doubleToLongBits(${field.name}) ^
${I}                (java.lang.Double.doubleToLongBits(${field.name}) >>> 32));
        <#elseif field.isBool>
            <#-- bool type: convert it to int -->
${I}__result = zserio.runtime.Util.HASH_PRIME_NUMBER * __result + (${field.name} ? 1 : 0);
        <#else>
            <#-- others: use implicit casting to int -->
${I}__result = zserio.runtime.Util.HASH_PRIME_NUMBER * __result + ${field.name};
        </#if>
    <#else>
        <#-- complex type: use hashCode() but account for possible null -->
${I}__result = zserio.runtime.Util.HASH_PRIME_NUMBER * __result +
${I}        ((${field.name} == null) ? 0 : ${field.name}.hashCode());
    </#if>
</#macro>

<#macro offset_checker_name fieldName>
    __OffsetChecker_${fieldName}<#t>
</#macro>

<#macro define_offset_checker compoundName field>
    private final class <@offset_checker_name field.name/> implements zserio.runtime.array.OffsetChecker
    {
        @Override
        public void checkOffset(int __index, long __byteOffset) throws zserio.runtime.ZserioError
        {
            if (__byteOffset != <@compound_field_get_offset field/>)
            {
                throw new zserio.runtime.ZserioError("Wrong offset for field ${compoundName}.${field.name}: " +
                        __byteOffset + " != " + <@compound_field_get_offset field/> + "!");
            }
        }
    }
</#macro>

<#macro offset_setter_name fieldName>
    __OffsetSetter_${fieldName}<#t>
</#macro>

<#macro define_offset_setter compoundName field>
    private final class <@offset_setter_name field.name/> implements zserio.runtime.array.OffsetSetter
    {
        @Override
        public void setOffset(int __index, long __byteOffset)
        {
            final ${field.offset.typeName} __value = <#rt>
    <#if field.offset.requiresBigInt>
                    <#lt>java.math.BigInteger.valueOf(__byteOffset);
    <#else>
                    <#lt>(${field.offset.typeName})__byteOffset;
    </#if>
            ${field.offset.setter};
        }
    }
</#macro>

<#macro element_factory_name fieldName>
    __ElementFactory_${fieldName}<#t>
</#macro>

<#macro define_element_factory field>
    <#local extraConstructorArguments>
        <#if field.array.elementCompound??>
            <@compound_field_compound_ctor_params field.array.elementCompound/><#t>
        </#if>
    </#local>
    private <#if !field.array.requiresParentContext>static </#if>final class <@element_factory_name field.name/> <#rt>
        <#lt>implements zserio.runtime.array.ElementFactory<${field.array.elementJavaTypeName}>
    {
        @Override
        public ${field.array.elementJavaTypeName} create(zserio.runtime.io.BitStreamReader __in, int __index)
            throws java.io.IOException, zserio.runtime.ZserioError
        {
    <#if field.array.isElementEnum>
            return ${field.array.elementJavaTypeName}.readEnum(__in);
    <#else>
            return new ${field.array.elementJavaTypeName}(__in<#if extraConstructorArguments?has_content>, ${extraConstructorArguments}</#if>);
    </#if>
        }
    }
</#macro>

<#macro define_field_helper_classes compoundName field>
    <#if field.array??>
        <#if field.offset?? && field.offset.containsIndex>

            <@define_offset_checker compoundName, field/>
            <#if withWriterCode>

                <@define_offset_setter name, field/>
            </#if>
        </#if>
        <#if field.array.requiresElementFactory>

            <@define_element_factory field/>
        </#if>
    </#if>
</#macro>

<#macro choice_tag_name field>
    CHOICE_${field.name}<#t>
</#macro>
