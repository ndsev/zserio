<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "RangeCheck.inc.ftl">
<@standard_header generatorDescription, packageName/>

public class ${name} implements <#if withWriterCode>zserio.runtime.io.InitializeOffsetsWriter, </#if>zserio.runtime.SizeOf
{
    <@compound_constructors compoundConstructorsData/>
    @Override
    public int bitSizeOf() throws zserio.runtime.ZserioError
    {
        return bitSizeOf(0);
    }

    @Override
    public int bitSizeOf(long __bitPosition) throws zserio.runtime.ZserioError
    {
<#if fieldList?has_content>
        long __endBitPosition = __bitPosition;

        __endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfVarUInt64(__choiceTag);

        switch (__choiceTag)
        {
        <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_bitsizeof_field field, 3/>
            break;
        </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        }

        return (int)(__endBitPosition - __bitPosition);
<#else>
        return 0;
</#if>
    }

    public int choiceTag()
    {
        return __choiceTag;
    }

<@compound_parameter_accessors compoundParametersData/>
<#list fieldList as field>
    <#if field.isObjectArray>@java.lang.SuppressWarnings("unchecked")</#if>
    public ${field.javaTypeName} ${field.getterName}() throws zserio.runtime.ZserioError
    {
        return (${field.javaNullableTypeName}) this.__objectChoice;
    }

    <#if withWriterCode>
    public void ${field.setterName}(${field.javaTypeName} ${field.name})
    {
        <@range_check field.rangeCheckData, name/>
        this.__choiceTag = <@choice_tag_name field/>;
        this.__objectChoice = ${field.name};
    }

        <#if field.array?? && field.array.generateListSetter>
    public void ${field.setterName}(java.util.List<${field.array.elementJavaTypeName}> ${field.name})
    {
        ${field.setterName}(new ${field.javaTypeName}(${field.name}));
    }

        </#if>
    </#if>
</#list>
<@compound_functions compoundFunctionsData/>
    @Override
    public boolean equals(java.lang.Object obj)
    {
        if (obj instanceof ${name})
        {
            final ${name} __that = (${name})obj;

            return
<#list compoundParametersData.list as parameter>
                    <@compound_compare_parameter parameter/> &&
</#list>
                    this.__choiceTag == __that.__choiceTag &&
                    (
                        (this.__objectChoice == null && __that.__objectChoice == null) ||
                        (this.__objectChoice != null && this.__objectChoice.equals(__that.__objectChoice))
                    );
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        int __result = zserio.runtime.Util.HASH_SEED;

<#list compoundParametersData.list as parameter>
        <@compound_hashcode_parameter parameter/>
</#list>
        __result = zserio.runtime.Util.HASH_PRIME_NUMBER * __result + __choiceTag;
        __result = zserio.runtime.Util.HASH_PRIME_NUMBER * __result +
                ((__objectChoice == null) ? 0 : __objectChoice.hashCode());

        return __result;
    }

    public void read(final zserio.runtime.io.BitStreamReader __in)
            throws java.io.IOException, zserio.runtime.ZserioError
    {
<#if fieldList?has_content>
        __choiceTag = zserio.runtime.VarUInt64Util.convertVarUInt64ToInt(__in.readVarUInt64());

        switch (__choiceTag)
        {
        <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_read_field field, name, 3/>
            <@compound_check_constraint_field field, name, 3/>
            break;
        </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        }
</#if>
    }
<#if withWriterCode>

    public long initializeOffsets(long __bitPosition) throws zserio.runtime.ZserioError
    {
    <#if fieldList?has_content>
        long __endBitPosition = __bitPosition;

        __endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfVarUInt64(__choiceTag);

        switch (__choiceTag)
        {
        <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_field_initialize_offsets field, 3/>
            break;
        </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        }

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
        __out.writeVarUInt64(__choiceTag);

        switch (__choiceTag)
        {
        <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_check_constraint_field field, name, 3/>
            <@compound_write_field field, name, 3/>
            break;
        </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        };
    </#if>
    }
</#if>

<#list fieldList as field>
    public static final int <@choice_tag_name field/> = ${field_index};
</#list>
    public static final int CHOICE_UNDEFINED = -1;
<#list fieldList as field>
    <@define_field_helper_classes name, field/>
</#list>

<@compound_parameter_members compoundParametersData/>
    private java.lang.Object __objectChoice;
    private int __choiceTag = CHOICE_UNDEFINED;
}
