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

<#macro array_wrapper_raw_constructor field withWriterCode rawArray indent>
    <@array_wrapper_constructor_inner field, withWriterCode, rawArray, indent/>
</#macro>

<#macro array_wrapper_read_constructor field withWriterCode indent>
    <@array_wrapper_constructor_inner field, withWriterCode, "", indent/>
</#macro>

<#macro array_wrapper_constructor_inner field withWriterCode rawArray indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
new ${field.array.wrapperJavaTypeName}(
${I}new ${field.array.rawHolderJavaTypeName}(<#rt>
    <#if field.array.requiresElementClass>
        ${field.array.elementJavaTypeName}.class<#if rawArray?has_content>, </#if><#t>
    </#if>
    <#if rawArray?has_content>
        ${rawArray}<#t>
    </#if>
    <#lt>),
${I}new ${field.array.traitsJavaTypeName}(<#rt>
    <#if field.array.requiresElementBitSize>
        ${field.array.elementBitSize.value}<#t>
    <#elseif field.array.requiresElementFactory>
        new <@element_factory_name field.name/>()<#t>
    </#if>
    <#lt>),
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

<#macro compound_field_get_offset field>
    <#if field.offset.requiresBigInt>
        ${field.offset.getter}.longValue()<#t>
    <#else>
        ${field.offset.getter}<#t>
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

<#macro compound_read_field_inner field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}in.alignTo(${field.alignmentValue});
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
        <@compound_read_field_offset_check field, compoundName, indent/>
    </#if>
    <#if field.array??>
${I}<@field_member_name field/> = <@array_wrapper_read_constructor field, withWriterCode, indent + 2/>;
${I}<@compound_get_field field/>.read(in<#if field.array.length??>, (int)${field.array.length}</#if>);
    <#elseif field.runtimeFunction??>
${I}<@field_member_name field/> = <#if field.runtimeFunction.javaReadTypeName??>(${field.runtimeFunction.javaReadTypeName})</#if><#rt>
        <#lt>in.read${field.runtimeFunction.suffix}(${field.runtimeFunction.arg!});
    <#elseif field.isEnum>
${I}<@field_member_name field/> = ${field.javaTypeName}.readEnum(in);
    <#else>
        <#-- compound or bitmask -->
        <#local compoundParamsArguments>
            <#if field.compound??><#-- can be a bitmask -->
                <@compound_field_compound_ctor_params field.compound/>
            </#if>
        </#local>
        <#local compoundArguments>in<#if compoundParamsArguments?has_content>, ${compoundParamsArguments}</#if></#local>
${I}<@field_member_name field/> = new ${field.javaTypeName}(${compoundArguments});
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
${I}if (in.readBool())
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
${I}out.alignTo(java.lang.Byte.SIZE);
${I}if (out.getBytePosition() != <@compound_field_get_offset field/>)
${I}{
${I}    throw new zserio.runtime.ZserioError("Write: Wrong offset for field ${compoundName}.${field.name}: " +
${I}            out.getBytePosition() + " != " + <@compound_field_get_offset field/> + "!");
${I}}
</#macro>

<#macro compound_write_field_inner field compoundName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}out.alignTo(${field.alignmentValue});
    </#if>
    <#if field.offset?? && !field.offset.containsIndex>
        <@compound_write_field_offset_check field, compoundName, indent/>
    </#if>
    <#if field.array??>
        <#if field.array.length??>
${I}if (<@compound_get_field field/>.size() != (int)(${field.array.length}))
${I}{
${I}    throw new zserio.runtime.ZserioError("Write: Wrong array length for field ${compoundName}.${field.name}: " +
${I}            <@compound_get_field field/>.size() + " != " + (int)(${field.array.length}) + "!");
${I}}
        </#if>
${I}<@compound_get_field field/>.write(out);
    <#elseif field.runtimeFunction??>
${I}out.write${field.runtimeFunction.suffix}(<@compound_get_field field/><#if field.runtimeFunction.arg??>, ${field.runtimeFunction.arg}</#if>);
    <#else>
        <#-- enum or compound -->
${I}<@compound_get_field field/>.write(out, false);
    </#if>
</#macro>

<#macro field_optional_condition field>
    <#if field.optional.clause??>${field.optional.clause}<#else><@compound_get_field field/> != null</#if><#t>
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
java.lang.Float.floatToIntBits(<@compound_get_field field/>) == java.lang.Float.floatToIntBits(that.<@compound_get_field field/>)<#rt>
        <#elseif field.isDouble>
            <#-- double type: compare by doubleToLongBits() to get rid of SpotBugs -->
java.lang.Double.doubleToLongBits(<@compound_get_field field/>) == java.lang.Double.doubleToLongBits(that.<@compound_get_field field/>)<#rt>
        <#else>
            <#-- simple type: compare by == -->
<@compound_get_field field/> == that.<@compound_get_field field/><#rt>
        </#if>
    <#elseif field.isEnum>
        <#-- enum type: compare by getValue() and == -->
((<@compound_get_field field/> == null) ? that.<@compound_get_field field/> == null : <@compound_get_field field/>.getValue() == that.<@compound_get_field field/>.getValue())<#rt>
    <#else>
        <#-- complex type: compare by equals() but account for possible null -->
((<@compound_get_field field/> == null) ? that.<@compound_get_field field/> == null : <@compound_get_field field/>.equals(that.<@compound_get_field field/>))<#rt>
    </#if>
</#macro>

<#macro compound_bitsizeof_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array??>
${I}endBitPosition += <@compound_get_field field/>.bitSizeOf(endBitPosition);
    <#elseif field.bitSize.value??>
${I}endBitPosition += ${field.bitSize.value};
    <#elseif field.bitSize.runtimeFunction??>
${I}endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOf${field.bitSize.runtimeFunction.suffix}(<@compound_get_field field/>);
    <#else>
${I}endBitPosition += <@compound_get_field field/>.bitSizeOf(endBitPosition);
    </#if>
</#macro>

<#macro compound_field_initialize_offsets field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.array??>
${I}endBitPosition = <@compound_get_field field/>.initializeOffsets(endBitPosition);
    <#elseif field.bitSize.value??>
${I}endBitPosition += ${field.bitSize.value};
    <#elseif field.bitSize.runtimeFunction??>
${I}endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOf${field.bitSize.runtimeFunction.suffix}(<@compound_get_field field/>);
    <#elseif field.compound??>
${I}endBitPosition = <@compound_get_field field/>.initializeOffsets(endBitPosition);
    <#else>
${I}endBitPosition += <@compound_get_field field/>.bitSizeOf(endBitPosition);
    </#if>
</#macro>

<#macro compound_hashcode_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isSimpleType>
        <#if field.isLong>
            <#-- long type: use shifting -->
${I}result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + (int)(<@compound_get_field field/> ^ (<@compound_get_field field/> >>> 32));
        <#elseif field.isFloat>
            <#-- float type: use floatToIntBits() -->
${I}result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + java.lang.Float.floatToIntBits(<@compound_get_field field/>);
        <#elseif field.isDouble>
            <#-- double type: use doubleToLongBits() -->
${I}result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
${I}        (int)(java.lang.Double.doubleToLongBits(<@compound_get_field field/>) ^
${I}                (java.lang.Double.doubleToLongBits(<@compound_get_field field/>) >>> 32));
        <#elseif field.isBool>
            <#-- bool type: convert it to int -->
${I}result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + (<@compound_get_field field/> ? 1 : 0);
        <#else>
            <#-- others: use implicit casting to int -->
${I}result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + <@compound_get_field field/>;
        </#if>
    <#else>
        <#-- complex type: use hashCode() but account for possible null -->
${I}result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
${I}        ((<@compound_get_field field/> == null) ? 0 : <@compound_get_field field/>.hashCode());
    </#if>
</#macro>

<#macro offset_checker_name fieldName>
    OffsetChecker_${fieldName}<#t>
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
    OffsetInitializer_${fieldName}<#t>
</#macro>

<#macro define_offset_initializer field>
    private final class <@offset_initializer_name field.name/> implements zserio.runtime.array.OffsetInitializer
    {
        @Override
        public void setOffset(int index, long byteOffset)
        {
            final ${field.offset.typeName} value = <#rt>
    <#if field.offset.requiresBigInt>
                    <#lt>java.math.BigInteger.valueOf(byteOffset);
    <#else>
                    <#lt>(${field.offset.typeName})byteOffset;
    </#if>
            ${field.offset.setter};
        }
    }
</#macro>

<#macro element_factory_name fieldName>
    ElementFactory_${fieldName}<#t>
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
        public ${field.array.elementJavaTypeName} create(zserio.runtime.io.BitStreamReader in, int index)
                throws java.io.IOException, zserio.runtime.ZserioError
        {
    <#if field.array.isElementEnum>
            return ${field.array.elementJavaTypeName}.readEnum(in);
    <#else>
            return new ${field.array.elementJavaTypeName}(in<#if extraConstructorArguments?has_content>, ${extraConstructorArguments}</#if>);
    </#if>
        }
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
        <#if field.array.requiresElementFactory>

            <@define_element_factory field/>
        </#if>
    </#if>
</#macro>
