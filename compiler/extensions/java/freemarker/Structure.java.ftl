<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "RangeCheck.inc.ftl">
<#include "TypeInfo.inc.ftl">
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
            ${field.typeInfo.typeFullName} <@field_argument_name field/><#if field_has_next>,<#else>)</#if>
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
<#if withTypeInfoCode>
    public static zserio.runtime.typeinfo.TypeInfo typeInfo()
    {
    <#list fieldList as field>
        <@field_info_recursive_type_info_getter field/>
    </#list>
        final java.lang.String templateName = <@template_info_template_name templateInstantiation!/>;
        final java.util.List<zserio.runtime.typeinfo.TypeInfo> templateArguments =
                <@template_info_template_arguments templateInstantiation!/>
        final java.util.List<zserio.runtime.typeinfo.FieldInfo> fieldList =
                <@fields_info fieldList/>
        final java.util.List<zserio.runtime.typeinfo.ParameterInfo> parameterList =
                <@parameters_info compoundParametersData.list/>
        final java.util.List<zserio.runtime.typeinfo.FunctionInfo> functionList =
                <@functions_info compoundFunctionsData.list/>

        return new zserio.runtime.typeinfo.TypeInfo.StructTypeInfo(
                "${schemaTypeName}", templateName, templateArguments,
                fieldList, parameterList, functionList
        );
    }

</#if>
    public static void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
    {
<#list fieldList as field>
    <@compound_create_packing_context_field field/>
</#list>
    }

    @Override
    public void initPackingContext(zserio.runtime.array.PackingContextNode contextNode)
    {
<#list fieldList as field>
    <@compound_init_packing_context_field field, field?index, 2/>
</#list>
    }

    @Override
    public int bitSizeOf()
    {
        return bitSizeOf(0);
    }

    @Override
    public int bitSizeOf(long bitPosition)
    {
<#if fieldList?has_content>
        long endBitPosition = bitPosition;

    <#list fieldList as field>
        <@compound_bitsizeof_field field, 2/>
    </#list>

        return (int)(endBitPosition - bitPosition);
<#else>
        return 0;
</#if>
    }

    @Override
    public int bitSizeOf(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
    {
<#if fieldList?has_content>
        long endBitPosition = bitPosition;

    <#list fieldList as field>
        <@compound_bitsizeof_field field, 2, true, field?index/>
    </#list>

        return (int)(endBitPosition - bitPosition);
<#else>
        return 0;
</#if>
    }

    <@compound_parameter_accessors compoundParametersData/>
<#list fieldList as field>
    public ${field.typeInfo.typeFullName} ${field.getterName}()
    {
    <#if field.array??>
        <#if field.optional??>
        return (<@field_member_name field/> == null) ? null : <@field_member_name field/>.getRawArray();
        <#else>
        return <@field_member_name field/>.getRawArray();
        </#if>
    <#else>
        return <@field_member_name field/>;
    </#if>
    }

    <#if withWriterCode>
    public void ${field.setterName}(${field.typeInfo.typeFullName} <@field_argument_name field/>)
    {
        <@range_check field.rangeCheckData, name/>
        <#if field.array??>
            <#assign rawArray><@field_argument_name field/></#assign>
            <#if field.optional??>
        if (<@field_argument_name field/> == null)
        {
            this.<@field_member_name field/> = null;
        }
        else
        {
            this.<@field_member_name field/> = <@array_wrapper_raw_constructor field, rawArray, 5/>;
        }
            <#else>
        this.<@field_member_name field/> = <@array_wrapper_raw_constructor field, rawArray, 4/>;
            </#if>
        <#else>
        this.<@field_member_name field/> = <@field_argument_name field/>;
        </#if>
    }

    </#if>
    <#if field.optional??>
    public boolean ${field.optional.isUsedIndicatorName}()
    {
        return <#if field.optional.clause??>(${field.optional.clause});<#else>${field.optional.isSetIndicatorName}();</#if>
    }

        <#if withWriterCode>
    public boolean ${field.optional.isSetIndicatorName}()
    {
        return (<@field_member_name field/> != null);
    }

    public void ${field.optional.resetterName}()
    {
        <@field_member_name field/> = null;
    }

        </#if>
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
        <#if field.optional??>
            <#-- if optional is not auto and is used the that should be is used as well because all previous paramaters and fields were the same. -->
                    ((!${field.optional.isUsedIndicatorName}()) ? !that.${field.optional.isUsedIndicatorName}() :
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
    <#if field.optional??>
        if (${field.optional.isUsedIndicatorName}())
            <@compound_hashcode_field field, 3/>
    <#else>
        <@compound_hashcode_field field, 2/>
    </#if>
</#list>

        return result;
    }

    public void read(zserio.runtime.io.BitStreamReader in)
            throws java.io.IOException
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

    public void read(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
            throws java.io.IOException
    {
<#if fieldList?has_content>
    <#list fieldList as field>
    <@compound_read_field field, name, 2, true, field?index/>
        <#if field_has_next>

        </#if>
    </#list>
    <#if hasFieldWithConstraint>

        checkConstraints();
    </#if>
</#if>
    }
<#if withWriterCode>

    @Override
    public long initializeOffsets(long bitPosition)
    {
    <#if fieldList?has_content>
        long endBitPosition = bitPosition;

        <#list fieldList as field>
        <@compound_initialize_offsets_field field, 2/>
        </#list>

        return endBitPosition;
    <#else>
        return bitPosition;
    </#if>
    }

    @Override
    public long initializeOffsets(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
    {
    <#if fieldList?has_content>
        long endBitPosition = bitPosition;

        <#list fieldList as field>
        <@compound_initialize_offsets_field field, 2, true, field?index/>
        </#list>

        return endBitPosition;
    <#else>
        return bitPosition;
    </#if>
    }

    public void write(java.io.File file) throws java.io.IOException
    {
        try (final zserio.runtime.io.FileBitStreamWriter out = new zserio.runtime.io.FileBitStreamWriter(file))
        {
            write(out);
        }
    }

    @Override
    public void write(zserio.runtime.io.BitStreamWriter out)
            throws java.io.IOException
    {
        write(out, true);
    }

    @Override
    public void write(zserio.runtime.io.BitStreamWriter out, boolean callInitializeOffsets)
            throws java.io.IOException
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

    @Override
    public void write(zserio.runtime.array.PackingContextNode contextNode,
            zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
    {
    <#if fieldList?has_content>
        <#if hasFieldWithConstraint>
        checkConstraints();

        </#if>
        <#list fieldList as field>
        <@compound_write_field field, name, 2, true, field?index/>
            <#if field_has_next>

            </#if>
        </#list>
    </#if>
    }
</#if>
<#if hasFieldWithConstraint>

    private void checkConstraints()
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
<#macro field_java_type_member_name field>
    <#if field.array??>
        ${field.array.wrapperJavaTypeName}<#t>
    <#else>
        ${field.typeInfo.typeFullName}<#t>
    </#if>
</#macro>
<#list fieldList as field>
    private <@field_java_type_member_name field/> <@field_member_name field/><#rt>
    <#if field.initializer??>
        <#lt> = ${field.initializer}<#rt>
    </#if>
    <#lt>;
</#list>
}
