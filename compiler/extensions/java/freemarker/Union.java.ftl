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
    public int bitSizeOf(long bitPosition) throws zserio.runtime.ZserioError
    {
<#if fieldList?has_content>
        long endBitPosition = bitPosition;

        endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfVarUInt64(choiceTag);

        switch (choiceTag)
        {
        <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_bitsizeof_field field, 3/>
            break;
        </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        }

        return (int)(endBitPosition - bitPosition);
<#else>
        return 0;
</#if>
    }

    public int choiceTag()
    {
        return choiceTag;
    }

<@compound_parameter_accessors compoundParametersData/>
<#list fieldList as field>
    <#if field.isObjectArray>@java.lang.SuppressWarnings("unchecked")</#if>
    public ${field.javaTypeName} ${field.getterName}() throws zserio.runtime.ZserioError
    {
        return (${field.javaNullableTypeName}) this.objectChoice;
    }

    <#if withWriterCode>
    public void ${field.setterName}(${field.javaTypeName} <@field_argument_name field/>)
    {
        <@range_check field.rangeCheckData, name/>
        this.choiceTag = <@choice_tag_name field/>;
        this.objectChoice = <@field_argument_name field/>;
    }

        <#if field.array?? && field.array.generateListSetter>
    public void ${field.setterName}(java.util.List<${field.array.elementJavaTypeName}> <@field_argument_name field/>)
    {
        ${field.setterName}(new ${field.javaTypeName}(<@field_argument_name field/>));
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
            final ${name} that = (${name})obj;

            return
<#list compoundParametersData.list as parameter>
                    <@compound_compare_parameter parameter/> &&
</#list>
                    this.choiceTag == that.choiceTag &&
                    (
                        (this.objectChoice == null && that.objectChoice == null) ||
                        (this.objectChoice != null && this.objectChoice.equals(that.objectChoice))
                    );
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        int result = zserio.runtime.Util.HASH_SEED;

<#list compoundParametersData.list as parameter>
        <@compound_hashcode_parameter parameter/>
</#list>
        result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + choiceTag;
        result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
                ((this.objectChoice == null) ? 0 : this.objectChoice.hashCode());

        return result;
    }

    public void read(final zserio.runtime.io.BitStreamReader in)
            throws java.io.IOException, zserio.runtime.ZserioError
    {
<#if fieldList?has_content>
        choiceTag = zserio.runtime.VarUInt64Util.convertVarUInt64ToInt(in.readVarUInt64());

        switch (choiceTag)
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

    public long initializeOffsets(long bitPosition) throws zserio.runtime.ZserioError
    {
    <#if fieldList?has_content>
        long endBitPosition = bitPosition;

        endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfVarUInt64(choiceTag);

        switch (choiceTag)
        {
        <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_field_initialize_offsets field, 3/>
            break;
        </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        }

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
        out.writeVarUInt64(choiceTag);

        switch (choiceTag)
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
    public static final int UNDEFINED_CHOICE = -1;
<#list fieldList as field>
    <@define_field_helper_classes name, field/>
</#list>

<@compound_parameter_members compoundParametersData/>
    private java.lang.Object objectChoice;
    private int choiceTag = UNDEFINED_CHOICE;
}
