<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "RangeCheck.inc.ftl">
<@standard_header generatorDescription, packageName/>
<#assign hasFieldWithConstraint=false/>
<#list fieldList as field>
    <#if field.constraint??>
        <#assign hasFieldWithConstraint=true/>
        <#break>
    </#if>
</#list>

public class ${name} implements <#if withWriterCode>zserio.runtime.io.InitializeOffsetsWriter, </#if>zserio.runtime.SizeOf
{
    <@compound_constructors compoundConstructorsData/>
<#if withWriterCode && fieldList?has_content>
    <#assign constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData/></#assign>
    public ${name}(<#if constructorArgumentTypeList?has_content>${constructorArgumentTypeList},</#if>
    <#list fieldList as field>
        ${field.javaTypeName} ${field.name}<#if field_has_next>,<#else>)</#if>
    </#list>
    {
    <#if constructorArgumentTypeList?has_content>
        this(<@compound_constructor_argument_list compoundConstructorsData/>);
    </#if>
    <#list fieldList as field>
        ${field.setterName}(${field.name});
    </#list>
    }

</#if>
    @Override
    public int bitSizeOf()
    {
        return bitSizeOf(0);
    }

<#macro structure_align_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}__endBitPosition = zserio.runtime.BitPositionUtil.alignTo(${field.alignmentValue}, __endBitPosition);
    </#if>
    <#if field.offset??>
        <#if field.offset.containsIndex>
            <#-- align to bytes only if the array is non-empty to match read/write behavior -->
${I}if (${field.name}.length() > 0)
${I}    __endBitPosition = zserio.runtime.BitPositionUtil.alignTo(java.lang.Byte.SIZE, __endBitPosition);
        <#else>
${I}__endBitPosition = zserio.runtime.BitPositionUtil.alignTo(java.lang.Byte.SIZE, __endBitPosition);
        </#if>
    </#if>
</#macro>
<#macro structure_bitsizeof_inner field indent>
    <@structure_align_field field, indent/>
    <@compound_bitsizeof_field field, indent/>
</#macro>
    @Override
    public int bitSizeOf(long __bitPosition)
    {
<#if fieldList?has_content>
        long __endBitPosition = __bitPosition;

    <#list fieldList as field>
        <#if field.optional??>
            <#if !field.optional.clause??>
                <#-- auto optional field -->
        __endBitPosition += 1;
            </#if>
        if (<@field_optional_condition field/>)
        {
            <@structure_bitsizeof_inner field, 3/>
        }
        <#else>
        <@structure_bitsizeof_inner field, 2/>
        </#if>
    </#list>

        return (int)(__endBitPosition - __bitPosition);
<#else>
        return 0;
</#if>
    }

<@compound_parameter_accessors compoundParametersData/>
<#list fieldList as field>
    public ${field.javaTypeName} ${field.getterName}()
    {
        return this.${field.name};
    }

    <#if withWriterCode>
    public void ${field.setterName}(${field.javaTypeName} ${field.name})
    {
        <@range_check field.rangeCheckData, name/>
        this.${field.name} = ${field.name};
    }

        <#if field.array?? && field.array.generateListSetter>
    public void ${field.setterName}(java.util.List<${field.array.elementJavaTypeName}> ${field.name})
    {
        this.${field.name} = new ${field.javaTypeName}(${field.name});
    }

        </#if>
    </#if>
    <#if field.optional??>
    public boolean ${field.optional.indicatorName}()
    {
        return (<@field_optional_condition field/>);
    }

    </#if>
</#list>
<@compound_functions compoundFunctionsData/>
    @Override
    public boolean equals(java.lang.Object obj)
    {
<#if compoundParametersData.list?has_content || fieldList?has_content>
        if (obj instanceof ${name})
        {
            final ${name} __that = (${name})obj;

            return
    <#list compoundParametersData.list as parameter>
                    <@compound_compare_parameter parameter/><#if parameter_has_next || fieldList?has_content> &&<#else>;</#if>
    </#list>
    <#list fieldList as field>
        <#if field.optional?? && field.optional.clause??>
                    (!(${field.optional.clause}) ||
                    <@compound_compare_field field/>)<#if field_has_next> &&<#else>;</#if>
        <#else>
                    <@compound_compare_field field/><#if field_has_next> &&<#else>;</#if>
        </#if>
    </#list>
        }

        return false;
<#else>
        return obj instanceof ${name};
</#if>
    }

    @Override
    public int hashCode()
    {
        int __result = zserio.runtime.Util.HASH_SEED;

<#list compoundParametersData.list as parameter>
        <@compound_hashcode_parameter parameter/>
</#list>
<#list fieldList as field>
    <#if field.optional?? && field.optional.clause??>
        if (${field.optional.clause})
            <@compound_hashcode_field field, 3/>
    <#else>
        <@compound_hashcode_field field, 2/>
    </#if>
</#list>

        return __result;
    }

    public void read(final zserio.runtime.io.BitStreamReader __in)
            throws java.io.IOException, zserio.runtime.ZserioError
    {
<#if fieldList?has_content>
    <#list fieldList as field>
    <@compound_read_field field, name, 2/>
        <#if field_has_next>

        </#if>
    </#list>
    <#if hasFieldWithConstraint>

        __checkConstraints();
    </#if>
</#if>
    }
<#if withWriterCode>

    <#macro structure_initialize_offsets_inner field indent>
        <@structure_align_field field, indent/>
        <#if field.offset?? && !field.offset.containsIndex>
            <#local I>${""?left_pad(indent * 4)}</#local>
${I}{
${I}    final ${field.offset.typeName} __value = <#rt>
            <#if field.offset.requiresBigInt>
                <#lt>java.math.BigInteger.valueOf(zserio.runtime.BitPositionUtil.bitsToBytes(__endBitPosition));
            <#else>
                <#lt>(${field.offset.typeName})zserio.runtime.BitPositionUtil.bitsToBytes(__endBitPosition);
            </#if>
${I}    ${field.offset.setter};
${I}}
        </#if>
        <@compound_field_initialize_offsets field, indent/>
    </#macro>
    public long initializeOffsets(long __bitPosition)
    {
    <#if fieldList?has_content>
        long __endBitPosition = __bitPosition;

        <#list fieldList as field>
            <#if field.optional??>
                <#if !field.optional.clause??>
                    <#-- auto optional field -->
        __endBitPosition += 1;
                </#if>
        if (<@field_optional_condition field/>)
        {
            <@structure_initialize_offsets_inner field, 3/>
        }
            <#else>
        <@structure_initialize_offsets_inner field, 2/>
            </#if>
        </#list>

        return __endBitPosition;
    <#else>
        return __bitPosition;
    </#if>
    }

    public void write(java.io.File __file) throws java.io.IOException, zserio.runtime.ZserioError
    {
        zserio.runtime.io.FileBitStreamWriter __out = new zserio.runtime.io.FileBitStreamWriter(__file);
        write(__out);
        __out.close();
    }

    @Override
    public void write(zserio.runtime.io.BitStreamWriter __out)
            throws java.io.IOException, zserio.runtime.ZserioError
    {
        write(__out, true);
    }

    @Override
    public void write(zserio.runtime.io.BitStreamWriter __out, boolean __callInitializeOffsets)
            throws java.io.IOException, zserio.runtime.ZserioError
    {
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        final long __startBitPosition = __out.getBitPosition();

        if (__callInitializeOffsets)
        {
            initializeOffsets(__startBitPosition);
        }

        </#if>
        <#if hasFieldWithConstraint>
        __checkConstraints();
        </#if>
        <#list fieldList as field>
        <@compound_write_field field, name, 2/>
            <#if field_has_next>

            </#if>
        </#list>
    </#if>
    }
</#if>
<#if hasFieldWithConstraint>

    private void __checkConstraints() throws zserio.runtime.ZserioError
    {
    <#list fieldList as field>
        <@compound_check_constraint_field field, name, 2/>
    </#list>
    }
</#if>
<#list fieldList as field>
    <@define_field_helper_classes name, field/>
</#list>
<#if compoundParametersData.list?has_content || fieldList?has_content>

</#if>
<@compound_parameter_members compoundParametersData/>
<#list fieldList as field>
    private ${field.javaTypeName} ${field.name}<#if field.initializer??> = ${field.initializer}</#if>;
</#list>
}
