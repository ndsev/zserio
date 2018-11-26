<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "RangeCheck.inc.ftl">
<@standard_header generatorDescription, packageName, javaMajorVersion, [
        "java.io.IOException",
        "java.io.File",
        "zserio.runtime.SizeOf",
        "zserio.runtime.io.BitStreamReader",
        "zserio.runtime.io.FileBitStreamReader",
        "zserio.runtime.ZserioError",
        "zserio.runtime.Util"
]/>
<#if withWriterCode>
<@imports [
        "zserio.runtime.io.BitStreamWriter",
        "zserio.runtime.io.FileBitStreamWriter",
        "zserio.runtime.io.InitializeOffsetsWriter"
]/>
</#if>
<#assign hasFieldsWithConstraint=false/>
<#list fieldList as field>
    <#if field.constraint??>
        <#assign hasFieldsWithConstraint=true/>
<@imports ["zserio.runtime.ConstraintError"]/>
        <#break>
    </#if>
</#list>
<#list fieldList as field>
    <#if field.alignmentValue?? || field.offset??>
<@imports ["zserio.runtime.BitPositionUtil"]/>
        <#break>
    </#if>
</#list>
<#list fieldList as field>
    <#if need_field_runtime_function(field)>
<@imports ["zserio.runtime.BitSizeOfCalculator"]/>
        <#break>
    </#if>
</#list>

<@class_header generatorDescription/>
public class ${name} implements <#if withWriterCode>InitializeOffsetsWriter, </#if>SizeOf
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
${I}__endBitPosition = BitPositionUtil.alignTo(${field.alignmentValue}, __endBitPosition);
    </#if>
    <#if field.offset??>
        <#if field.offset.containsIndex>
            <#-- align to bytes only if the array is non-empty to match read/write behavior -->
${I}if (${field.name}.length() > 0)
${I}    __endBitPosition = BitPositionUtil.alignTo(Byte.SIZE, __endBitPosition);
        <#else>
${I}__endBitPosition = BitPositionUtil.alignTo(Byte.SIZE, __endBitPosition);
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
    public boolean equals(Object obj)
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
        int __result = Util.HASH_SEED;

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

    public void read(final BitStreamReader __in) throws IOException, ZserioError
    {
<#if fieldList?has_content>
    <#list fieldList as field>
    <@compound_read_field field, name, 2/>
        <#if field_has_next>

        </#if>
    </#list>
    <#if hasFieldsWithConstraint>

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
                <#lt>java.math.BigInteger.valueOf(BitPositionUtil.bitsToBytes(__endBitPosition));
            <#else>
                <#lt>(${field.offset.typeName})BitPositionUtil.bitsToBytes(__endBitPosition);
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

    public void write(File __file) throws IOException, ZserioError
    {
        FileBitStreamWriter __out = new FileBitStreamWriter(__file);
        write(__out);
        __out.close();
    }

    @Override
    public void write(BitStreamWriter __out) throws IOException, ZserioError
    {
        write(__out, true);
    }

    @Override
    public void write(BitStreamWriter __out, boolean __callInitializeOffsets) throws IOException, ZserioError
    {
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        final long __startBitPosition = __out.getBitPosition();

        if (__callInitializeOffsets)
        {
            initializeOffsets(__startBitPosition);
        }

        </#if>
        <#if hasFieldsWithConstraint>
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

<#if hasFieldsWithConstraint>
    private void __checkConstraints() throws ZserioError
    {
    <#list fieldList as field>
        <@compound_check_constraint_field field, name, 2/>
    </#list>
    }

</#if>
<#list fieldList as field>
    <@define_field_helper_classes name, field/>
</#list>
<@compound_parameter_members compoundParametersData/>
<#list fieldList as field>
    private ${field.javaTypeName} ${field.name}<#if field.initializer??> = ${field.initializer}</#if>;
</#list>
}
