<#include "FileHeader.inc.ftl">
<@standard_header generatorDescription, packageName/>
<#macro enum_array_traits arrayableInfo bitSize>
new ${arrayableInfo.arrayTraits.name}(<#rt>
        <#if arrayableInfo.arrayTraits.requiresElementBitSize>
            ${bitSize}<#t>
        </#if>
            )<#t>
</#macro>

public enum ${name} implements <#if withWriterCode>zserio.runtime.io.InitializeOffsetsWriter,
        </#if>zserio.runtime.SizeOf, zserio.runtime.ZserioEnum
{
<#list items as item>
    ${item.name}(${item.value})<#if item_has_next>,<#else>;</#if>
</#list>

    private ${name}(${baseJavaTypeName} value)
    {
        this.value = value;
    }

    public ${baseJavaTypeName} getValue()
    {
        return value;
    }

    @Override
    public java.lang.Number getGenericValue()
    {
        return value;
    }

    public static void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
    {
        contextNode.createContext();
    }

    @Override
    public void initPackingContext(zserio.runtime.array.PackingContextNode contextNode)
    {
        contextNode.getContext().init(new ${arrayableInfo.arrayElement}(value));
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
        return ${bitSize};
<#else>
        return zserio.runtime.BitSizeOfCalculator.getBitSizeOf${runtimeFunction.suffix}(value);
</#if>
    }

    @Override
    public int bitSizeOf(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
    {
        return contextNode.getContext().bitSizeOf(
                <@enum_array_traits arrayableInfo, bitSize!/>, bitPosition,
                new ${arrayableInfo.arrayElement}(value));
    }
<#if withWriterCode>

    @Override
    public long initializeOffsets(long bitPosition) throws zserio.runtime.ZserioError
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    @Override
    public long initializeOffsets(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
    {
        return bitPosition + bitSizeOf(contextNode, bitPosition);
    }

    @Override
    public void write(zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
    {
        write(out, false);
    }

    @Override
    public void write(zserio.runtime.io.BitStreamWriter out, boolean callInitializeOffsets)
            throws java.io.IOException
    {
        out.write${runtimeFunction.suffix}(getValue()<#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>);
    }
    
    @Override
    public void write(zserio.runtime.array.PackingContextNode contextNode,
            zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
    {
        contextNode.getContext().write(
                <@enum_array_traits arrayableInfo, bitSize!/>, out,
                new ${arrayableInfo.arrayElement}(value));
    }

</#if>
    public static ${name} readEnum(zserio.runtime.io.BitStreamReader in) throws java.io.IOException
    {
        return toEnum(<#if runtimeFunction.javaReadTypeName??>(${runtimeFunction.javaReadTypeName})</#if><#rt>
                <#lt>in.read${runtimeFunction.suffix}(${runtimeFunction.arg!}));
    }

    public static ${name} readEnum(zserio.runtime.array.PackingContextNode contextNode,
            zserio.runtime.io.BitStreamReader in) throws java.io.IOException
    {
        return toEnum(((${arrayableInfo.arrayElement})
                contextNode.getContext().read(
                        <@enum_array_traits arrayableInfo, bitSize!/>, in)).get());
    }

    public static ${name} toEnum(${baseJavaTypeName} value)
    {
<#if baseJavaTypeName == "long" || baseJavaTypeName == "java.math.BigInteger">
    <#-- can't use switch for long and for BigInteger -->
    <#list items as item>
        <#if baseJavaTypeName == "java.math.BigInteger">
        if (value.compareTo(${item.value}) == 0)
        <#else>
        if (value == ${item.value})
        </#if>
            return ${item.name};
    </#list>

        throw new java.lang.IllegalArgumentException("Unknown value for enumeration ${name}: " + value + "!");
<#else>
        switch (value)
        {
    <#list items as item>
            case ${item.value}:
                return ${item.name};
    </#list>
            default:
                throw new java.lang.IllegalArgumentException(
                        "Unknown value for enumeration ${name}: " + value + "!");
        }
</#if>
    }

    private ${baseJavaTypeName} value;
}
