<#include "FileHeader.inc.ftl">
<@standard_header generatorDescription, packageName, [
        "java.io.IOException",
        "zserio.runtime.SizeOf",
        "zserio.runtime.io.BitStreamReader"
        "zserio.runtime.ZserioError",
        "zserio.runtime.Util"
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

public class ${name} implements <#if withWriterCode>InitializeOffsetsWriter, </#if>SizeOf
{
    public ${name}()
    {
        this(<#if isSimpleType>(${baseJavaTypeName})0<#else>java.math.BigInteger.ZERO</#if>);
    }

    public ${name}(${baseJavaTypeName} value)
    {
<#if lowerBound?? || checkUpperBound>
    <#if isSimpleType>
        if (<#if lowerBound??>value < ${lowerBound}</#if><#rt>
                <#lt><#if checkUpperBound><#if lowerBound??> || </#if>value > ${upperBound}</#if>)
    <#else>
        if (<#if lowerBound??>value.compareTo(${lowerBound}) < 0</#if><#rt>
                <#lt><#if checkUpperBound><#if lowerBound??> || </#if>value.compareTo(${upperBound}) > 0</#if>)
    </#if>
            throw new IllegalArgumentException("Value for bitmask '${name}' out of bounds: " + value + "!");
</#if>
        this.value = value;
    }

    public ${name}(BitStreamReader in) throws IOException
    {
        value = <#if runtimeFunction.javaReadTypeName??>(${runtimeFunction.javaReadTypeName})</#if><#rt>
                <#lt>in.read${runtimeFunction.suffix}(${runtimeFunction.arg!});
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
        return BitSizeOfCalculator.getBitSizeOf${runtimeFunction.suffix}(value);
</#if>
    }
<#if withWriterCode>

    @Override
    public long initializeOffsets(long bitPosition) throws ZserioError
    {
        return bitPosition + bitSizeOf(bitPosition);
    }
</#if>

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof ${name}))
            return false;

        final ${name} other${name} = (${name})other;
        return <#if isSimpleType>value == other${name}.value<#else>value.equals(other${name}.value)</#if>;
    }

    @Override
    public int hashCode()
    {
        int result = Util.HASH_SEED;

<#if isSimpleType>
    <#if isLong>
        <#-- use shifting -->
        result = Util.HASH_PRIME_NUMBER * result + (int)(value ^ (value >>> 32));
    <#else>
        <#-- use implicit casting to int -->
        result = Util.HASH_PRIME_NUMBER * result + value;
    </#if>
<#else>
        <#-- big integer - use hashCode() -->
        result = Util.HASH_PRIME_NUMBER * result + value.hashCode();
</#if>

        return result;
    }

    @Override
    public java.lang.String toString()
    {
        final StringBuilder builder = new StringBuilder();

<#list values as value>
    <#if !value.isZero>
        if (this.and(${name}.Values.${value.name}).equals(${name}.Values.${value.name}))
            builder.append(builder.length() == 0 ? "${value.name}" : " | ${value.name}");
    <#else>
        <#assign zeroValueName=value.name/><#--  may be there only once -->
    </#if>
</#list>
<#if zeroValueName??>
        if (builder.length() == 0 && <#if isSimpleType>value == 0<#else>value.equals(java.math.BigInteger.ZERO)</#if>)
            builder.append("${zeroValueName}");
</#if>

        return java.lang.String.valueOf(value) + "[" + builder.toString() + "]";
    }
<#if withWriterCode>

    @Override
    public void write(BitStreamWriter out) throws IOException
    {
        write(out, false);
    }

    @Override
    public void write(BitStreamWriter out, boolean callInitializeOffsets) throws IOException
    {
        out.write${runtimeFunction.suffix}(value<#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>);
    }
</#if>

    public ${baseJavaTypeName} getValue()
    {
        return value;
    }

    public ${name} or(${name} other)
    {
<#if isSimpleType>
        return new ${name}((${baseJavaTypeName})(value | other.value));
<#else>
        <#-- big integer -->
        return new ${name}(value.or(other.value));
</#if>
    }

    public ${name} and(${name} other)
    {
<#if isSimpleType>
        return new ${name}((${baseJavaTypeName})(value & other.value));
<#else>
        <#-- big integer -->
        return new ${name}(value.and(other.value));
</#if>
    }

    public ${name} xor(${name} other)
    {
<#if isSimpleType>
        return new ${name}((${baseJavaTypeName})(value ^ other.value));
<#else>
        <#-- big integer -->
        return new ${name}(value.xor(other.value));
</#if>
    }

    public ${name} not()
    {
<#if isSimpleType>
        return new ${name}((${baseJavaTypeName})(~value<#if upperBound??> & ${upperBound}</#if>));
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

    private ${baseJavaTypeName} value;
}
