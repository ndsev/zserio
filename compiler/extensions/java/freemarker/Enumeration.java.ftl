<#include "FileHeader.inc.ftl">
<@standard_header generatorDescription, packageName/>

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
<#if withWriterCode>

    @Override
    public long initializeOffsets(long bitPosition) throws zserio.runtime.ZserioError
    {
        return bitPosition + bitSizeOf(bitPosition);
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

</#if>
    public static ${name} readEnum(zserio.runtime.io.BitStreamReader in) throws java.io.IOException
    {
        return toEnum(<#if runtimeFunction.javaReadTypeName??>(${runtimeFunction.javaReadTypeName})</#if><#rt>
                <#lt>in.read${runtimeFunction.suffix}(${runtimeFunction.arg!}));
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
