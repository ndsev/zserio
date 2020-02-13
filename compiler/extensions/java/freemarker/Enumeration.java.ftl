<#include "FileHeader.inc.ftl">
<@standard_header generatorDescription, packageName, [
        "java.io.IOException",
        "zserio.runtime.SizeOf",
        "zserio.runtime.ZserioEnum",
        "zserio.runtime.ZserioError",
        "zserio.runtime.io.BitStreamReader"
]/>
<#if withWriterCode>
<@imports [
        "zserio.runtime.io.BitStreamWriter",
        "zserio.runtime.io.InitializeOffsetsWriter"
]/>
</#if>
<#if !bitSize??>
<@imports ["zserio.runtime.BitSizeOfCalculator"]/>
</#if>

public enum ${name} implements <#if withWriterCode>InitializeOffsetsWriter, </#if>SizeOf, ZserioEnum
{
<#list items as item>
    ${item.name}(${item.value})<#if item_has_next>,<#else>;</#if>
</#list>

    private ${name}(${baseJavaTypeName} __value)
    {
        this.__value = __value;
    }

    public ${baseJavaTypeName} getValue()
    {
        return __value;
    }

    @Override
    public Number getGenericValue()
    {
        return __value;
    }

    @Override
    public int bitSizeOf()
    {
        return bitSizeOf(0);
    }

    @Override
    public int bitSizeOf(long __bitPosition)
    {
<#if bitSize??>
        return ${bitSize};
<#else>
        return BitSizeOfCalculator.getBitSizeOf${runtimeFunction.suffix}(__value);
</#if>
    }
<#if withWriterCode>

    @Override
    public long initializeOffsets(long __bitPosition) throws ZserioError
    {
        return __bitPosition + bitSizeOf(__bitPosition);
    }

    @Override
    public void write(BitStreamWriter __out) throws IOException
    {
        write(__out, false);
    }

    @Override
    public void write(BitStreamWriter __out, boolean __callInitializeOffsets) throws IOException
    {
        __out.write${runtimeFunction.suffix}(getValue()<#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>);
    }

</#if>
    public static ${name} readEnum(BitStreamReader __in) throws IOException
    {
        return toEnum(<#if runtimeFunction.javaReadTypeName??>(${runtimeFunction.javaReadTypeName})</#if><#rt>
                <#lt>__in.read${runtimeFunction.suffix}(${runtimeFunction.arg!}));
    }

    public static ${name} toEnum(${baseJavaTypeName} __value)
    {
<#if baseJavaTypeName == "long" || baseJavaTypeName == "java.math.BigInteger">
    <#-- can't use switch for long and for BigInteger -->
    <#list items as item>
        <#if baseJavaTypeName == "java.math.BigInteger">
        if (__value.compareTo(${item.value}) == 0)
        <#else>
        if (__value == ${item.value})
        </#if>
            return ${item.name};
    </#list>

        throw new IllegalArgumentException("Unknown value for enumeration ${name}: " + __value + "!");
<#else>
        switch (__value)
        {
    <#list items as item>
            case ${item.value}:
                return ${item.name};
    </#list>
            default:
                throw new IllegalArgumentException("Unknown value for enumeration ${name}: " + __value + "!");
        }
</#if>
    }

    private ${baseJavaTypeName} __value;
}
