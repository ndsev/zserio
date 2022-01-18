<#include "FileHeader.inc.ftl">
<#include "TypeInfo.inc.ftl">
<@standard_header generatorDescription, packageName/>
<#macro bitmask_array_traits arrayableInfo bitSize>
new ${arrayableInfo.arrayTraits.name}(<#rt>
        <#if arrayableInfo.arrayTraits.requiresElementBitSize>
        ${bitSize.value}<#t>
        </#if>
        )<#t>
</#macro>

public class ${name} implements <#if withWriterCode>zserio.runtime.io.InitializeOffsetsWriter, </#if>zserio.runtime.SizeOf
{
    public ${name}()
    {
        this(<#if underlyingTypeInfo.isSimple>(${underlyingTypeInfo.typeFullName})0<#else>java.math.BigInteger.ZERO</#if>);
    }

    public ${name}(${underlyingTypeInfo.typeFullName} value)
    {
<#if lowerBound?? || checkUpperBound>
    <#if underlyingTypeInfo.isSimple>
        if (<#if lowerBound??>value < ${lowerBound}</#if><#rt>
                <#lt><#if checkUpperBound><#if lowerBound??> || </#if>value > ${upperBound}</#if>)
    <#else>
        if (<#if lowerBound??>value.compareTo(${lowerBound}) < 0</#if><#rt>
                <#lt><#if checkUpperBound><#if lowerBound??> || </#if>value.compareTo(${upperBound}) > 0</#if>)
    </#if>
        {
            throw new java.lang.IllegalArgumentException(
                    "Value for bitmask '${name}' out of bounds: " + value + "!");
        }
</#if>
        this.value = value;
    }

    public ${name}(zserio.runtime.io.BitStreamReader in) throws java.io.IOException
    {
        value = <#if runtimeFunction.javaReadTypeName??>(${runtimeFunction.javaReadTypeName})</#if><#rt>
                <#lt>in.read${runtimeFunction.suffix}(${runtimeFunction.arg!});
    }

    public ${name}(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
            throws java.io.IOException
    {
        value = ((${underlyingTypeInfo.arrayableInfo.arrayElement})
                contextNode.getContext().read(
                        <@bitmask_array_traits underlyingTypeInfo.arrayableInfo, bitSize!/>, in)).get();
    }
<#if withTypeInfoCode>

    public static zserio.runtime.typeinfo.TypeInfo typeInfo()
    {
        return new zserio.runtime.typeinfo.TypeInfo.BitmaskTypeInfo(
                "${schemaTypeName}",
                <@type_info underlyingTypeInfo/>,
                <@underlying_type_info_type_arguments bitSize!/>,
                java.util.Arrays.asList(
    <#list values as value>
                        <@item_info value.name, value.value/><#if value?has_next>,</#if>
    </#list>
                )
        );
    }
</#if>

    public static void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
    {
        contextNode.createContext();
    }

    @Override
    public void initPackingContext(zserio.runtime.array.PackingContextNode contextNode)
    {
        contextNode.getContext().init(
                <@bitmask_array_traits underlyingTypeInfo.arrayableInfo, bitSize!/>,
                new ${underlyingTypeInfo.arrayableInfo.arrayElement}(value));
    }

    @Override
    public int bitSizeOf()
    {
        return bitSizeOf(0);
    }

    @Override
    public int bitSizeOf(long bitPosition)
    {
<#if bitSize??>
        return ${bitSize.value};
<#else>
        return zserio.runtime.BitSizeOfCalculator.getBitSizeOf${runtimeFunction.suffix}(value);
</#if>
    }

    @Override
    public int bitSizeOf(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
    {
        return contextNode.getContext().bitSizeOf(
                <@bitmask_array_traits underlyingTypeInfo.arrayableInfo, bitSize!/>,
                new ${underlyingTypeInfo.arrayableInfo.arrayElement}(value));
    }
<#if withWriterCode>

    @Override
    public long initializeOffsets(long bitPosition)
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    @Override
    public long initializeOffsets(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
    {
        return bitPosition + bitSizeOf(contextNode, bitPosition);
    }
</#if>

    @Override
    public boolean equals(java.lang.Object other)
    {
        if (!(other instanceof ${name}))
            return false;

        final ${name} other${name} = (${name})other;
        return <#if underlyingTypeInfo.isSimple>value == other${name}.value<#else>value.equals(other${name}.value)</#if>;
    }

    @Override
    public int hashCode()
    {
        int result = zserio.runtime.Util.HASH_SEED;

<#if underlyingTypeInfo.isSimple>
    <#if underlyingTypeInfo.isLong>
        <#-- use shifting -->
        result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + (int)(value ^ (value >>> 32));
    <#else>
        <#-- use implicit casting to int -->
        result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + value;
    </#if>
<#else>
        <#-- big integer - use hashCode() -->
        result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + value.hashCode();
</#if>

        return result;
    }

    @Override
    public java.lang.String toString()
    {
        final java.lang.StringBuilder builder = new java.lang.StringBuilder();

<#list values as value>
    <#if !value.isZero>
        if (this.and(${name}.Values.${value.name}).equals(${name}.Values.${value.name}))
            builder.append(builder.length() == 0 ? "${value.name}" : " | ${value.name}");
    <#else>
        <#assign zeroValueName=value.name/><#--  may be there only once -->
    </#if>
</#list>
<#if zeroValueName??>
        if (builder.length() == 0 && <#if underlyingTypeInfo.isSimple>value == 0<#else>value.equals(java.math.BigInteger.ZERO)</#if>)
            builder.append("${zeroValueName}");
</#if>

        return java.lang.String.valueOf(value) + "[" + builder.toString() + "]";
    }
<#if withWriterCode>

    @Override
    public void write(zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
    {
        write(out, false);
    }

    @Override
    public void write(zserio.runtime.io.BitStreamWriter out, boolean callInitializeOffsets)
            throws java.io.IOException
    {
        out.write${runtimeFunction.suffix}(value<#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>);
    }

    @Override
    public void write(zserio.runtime.array.PackingContextNode contextNode,
            zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
    {
        contextNode.getContext().write(
                <@bitmask_array_traits underlyingTypeInfo.arrayableInfo, bitSize!/>, out,
                new ${underlyingTypeInfo.arrayableInfo.arrayElement}(value));
    }
</#if>

    public ${underlyingTypeInfo.typeFullName} getValue()
    {
        return value;
    }

    public ${name} or(${name} other)
    {
<#if underlyingTypeInfo.isSimple>
        return new ${name}((${underlyingTypeInfo.typeFullName})(value | other.value));
<#else>
        <#-- big integer -->
        return new ${name}(value.or(other.value));
</#if>
    }

    public ${name} and(${name} other)
    {
<#if underlyingTypeInfo.isSimple>
        return new ${name}((${underlyingTypeInfo.typeFullName})(value & other.value));
<#else>
        <#-- big integer -->
        return new ${name}(value.and(other.value));
</#if>
    }

    public ${name} xor(${name} other)
    {
<#if underlyingTypeInfo.isSimple>
        return new ${name}((${underlyingTypeInfo.typeFullName})(value ^ other.value));
<#else>
        <#-- big integer -->
        return new ${name}(value.xor(other.value));
</#if>
    }

    public ${name} not()
    {
<#if underlyingTypeInfo.isSimple>
        return new ${name}((${underlyingTypeInfo.typeFullName})(~value<#if upperBound??> & ${upperBound}</#if>));
<#else>
        <#-- big integer -->
        return new ${name}(value.not().and(${upperBound}));
</#if>
    }

    public static final class Values
    {
<#list values as value>
        public static final ${name} ${value.name} = new ${name}(${value.value});
</#list>
    }

    private ${underlyingTypeInfo.typeFullName} value;
}
