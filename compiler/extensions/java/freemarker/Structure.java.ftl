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
        ${field.javaTypeName} <@field_argument_name field/><#if field_has_next>,<#else>)</#if>
    </#list>
    {
    <#if constructorArgumentTypeList?has_content>
        this(<@compound_constructor_argument_list compoundConstructorsData/>);
    </#if>
    <#list fieldList as field>
        ${field.setterName}(<@field_argument_name field/>);
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
${I}endBitPosition = zserio.runtime.BitPositionUtil.alignTo(${field.alignmentValue}, endBitPosition);
    </#if>
    <#if field.offset??>
        <#if field.offset.containsIndex>
            <#-- align to bytes only if the array is non-empty to match read/write behavior -->
${I}if (this.<@field_member_name field/>.length() > 0)
${I}    endBitPosition = zserio.runtime.BitPositionUtil.alignTo(java.lang.Byte.SIZE, endBitPosition);
        <#else>
${I}endBitPosition = zserio.runtime.BitPositionUtil.alignTo(java.lang.Byte.SIZE, endBitPosition);
        </#if>
    </#if>
</#macro>
<#macro structure_bitsizeof_inner field indent>
    <@structure_align_field field, indent/>
    <@compound_bitsizeof_field field, indent/>
</#macro>
    @Override
    public int bitSizeOf(long bitPosition)
    {
<#if fieldList?has_content>
        long endBitPosition = bitPosition;

    <#list fieldList as field>
        <#if field.optional??>
            <#if !field.optional.clause??>
                <#-- auto optional field -->
        endBitPosition += 1;
            </#if>
        if (<@field_optional_condition field/>)
        {
            <@structure_bitsizeof_inner field, 3/>
        }
        <#else>
        <@structure_bitsizeof_inner field, 2/>
        </#if>
    </#list>

        return (int)(endBitPosition - bitPosition);
<#else>
        return 0;
</#if>
    }

<@compound_parameter_accessors compoundParametersData/>
<#list fieldList as field>
    public ${field.javaTypeName} ${field.getterName}()
    {
        return this.<@field_member_name field/>;
    }

    <#if withWriterCode>
    public void ${field.setterName}(${field.javaTypeName} <@field_argument_name field/>)
    {
        <@range_check field.rangeCheckData, name/>
        this.<@field_member_name field/> = <@field_argument_name field/>;
    }

        <#if field.array?? && field.array.generateListSetter>
    public void ${field.setterName}(java.util.List<${field.array.elementJavaTypeName}> <@field_argument_name field/>)
    {
        this.<@field_member_name field/> = new ${field.javaTypeName}(<@field_argument_name field/>);
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
            final ${name} that = (${name})obj;

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
        int result = zserio.runtime.Util.HASH_SEED;

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

        return result;
    }

    public void read(final zserio.runtime.io.BitStreamReader in)
            throws java.io.IOException, zserio.runtime.ZserioError
    {
<#if fieldList?has_content>
    <#list fieldList as field>
    <@compound_read_field field, name, 2/>
        <#if field_has_next>

        </#if>
    </#list>
    <#if hasFieldWithConstraint>

        checkConstraints();
    </#if>
</#if>
    }
<#if withWriterCode>

    <#macro structure_initialize_offsets_inner field indent>
        <@structure_align_field field, indent/>
        <#if field.offset?? && !field.offset.containsIndex>
            <#local I>${""?left_pad(indent * 4)}</#local>
${I}{
${I}    final ${field.offset.typeName} value = <#rt>
            <#if field.offset.requiresBigInt>
                <#lt>java.math.BigInteger.valueOf(zserio.runtime.BitPositionUtil.bitsToBytes(endBitPosition));
            <#else>
                <#lt>(${field.offset.typeName})zserio.runtime.BitPositionUtil.bitsToBytes(endBitPosition);
            </#if>
${I}    ${field.offset.setter};
${I}}
        </#if>
        <@compound_field_initialize_offsets field, indent/>
    </#macro>
    public long initializeOffsets(long bitPosition)
    {
    <#if fieldList?has_content>
        long endBitPosition = bitPosition;

        <#list fieldList as field>
            <#if field.optional??>
                <#if !field.optional.clause??>
                    <#-- auto optional field -->
        endBitPosition += 1;
                </#if>
        if (<@field_optional_condition field/>)
        {
            <@structure_initialize_offsets_inner field, 3/>
        }
            <#else>
        <@structure_initialize_offsets_inner field, 2/>
            </#if>
        </#list>

        return endBitPosition;
    <#else>
        return bitPosition;
    </#if>
    }

    public void write(java.io.File file) throws java.io.IOException, zserio.runtime.ZserioError
    {
        zserio.runtime.io.FileBitStreamWriter out = new zserio.runtime.io.FileBitStreamWriter(file);
        write(out);
        out.close();
    }

    @Override
    public void write(zserio.runtime.io.BitStreamWriter out)
            throws java.io.IOException, zserio.runtime.ZserioError
    {
        write(out, true);
    }

    @Override
    public void write(zserio.runtime.io.BitStreamWriter out, boolean callInitializeOffsets)
            throws java.io.IOException, zserio.runtime.ZserioError
    {
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        final long startBitPosition = out.getBitPosition();

        if (callInitializeOffsets)
        {
            initializeOffsets(startBitPosition);
        }

        </#if>
        <#if hasFieldWithConstraint>
        checkConstraints();
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

    private void checkConstraints() throws zserio.runtime.ZserioError
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
    private ${field.javaTypeName} <@field_member_name field/><#if field.initializer??> = ${field.initializer}</#if>;
</#list>
}
