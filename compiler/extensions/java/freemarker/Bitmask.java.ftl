<#include "FileHeader.inc.ftl">
<#include "TypeInfo.inc.ftl">
<#include "DocComment.inc.ftl">
<@standard_header generatorDescription, packageName/>
<#macro bitmask_array_traits arrayableInfo bitSize>
new ${arrayableInfo.arrayTraits.name}(<#rt>
        <#if arrayableInfo.arrayTraits.requiresElementBitSize>
        ${bitSize.value}<#t>
        </#if>
        )<#t>
</#macro>
<#assign isBigInteger=!underlyingTypeInfo.isSimple>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
public class ${name} implements <#if withWriterCode>zserio.runtime.io.InitializeOffsetsWriter,
        </#if>zserio.runtime.SizeOf, zserio.runtime.ZserioBitmask
{
<#if withCodeComments>
    /** Default constructor. */
</#if>
    public ${name}()
    {
        this(<#if isBigInteger>java.math.BigInteger.ZERO<#else>(${underlyingTypeInfo.typeFullName})0</#if>);
    }

<#if withCodeComments>
    /**
     * Bitmask value constructor.
     *
     * @param value Bitmask value to construct from.
     */
</#if>
    public ${name}(${underlyingTypeInfo.typeFullName} value)
    {
<#if lowerBound?? || checkUpperBound>
    <#if isBigInteger>
        if (<#if lowerBound??>value.compareTo(${lowerBound}) < 0</#if><#rt>
                <#lt><#if checkUpperBound><#if lowerBound??> || </#if>value.compareTo(${upperBound}) > 0</#if>)
    <#else>
        if (<#if lowerBound??>value < ${lowerBound}</#if><#rt>
                <#lt><#if checkUpperBound><#if lowerBound??> || </#if>value > ${upperBound}</#if>)
    </#if>
        {
            throw new java.lang.IllegalArgumentException(
                    "Value for bitmask '${name}' out of bounds: " + value + "!");
        }
</#if>
        this.value = value;
    }

<#if withCodeComments>
    /**
     * Read constructor.
     *
     * @param in Bit stream reader to use.
     *
     * @throws IOException If the reading from the bit stream failed.
     */
</#if>
    public ${name}(zserio.runtime.io.BitStreamReader in) throws java.io.IOException
    {
        value = <#if runtimeFunction.javaReadTypeName??>(${runtimeFunction.javaReadTypeName})</#if><#rt>
                <#lt>in.read${runtimeFunction.suffix}(${runtimeFunction.arg!});
    }

<#if withCodeComments>
    /**
     * Read constructor.
     * <p>
     * Called only internally if packed arrays are used.
     *
     * @param contextNode Context for packed arrays.
     * @param in Bit stream reader to use.
     *
     * @throws IOException If the reading from the bit stream failed.
     */
</#if>
    public ${name}(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
            throws java.io.IOException
    {
        value = ((${underlyingTypeInfo.arrayableInfo.arrayElement})
                contextNode.getContext().read(
                        <@bitmask_array_traits underlyingTypeInfo.arrayableInfo, bitSize!/>, in)).get();
    }
<#if withTypeInfoCode>

    <#if withCodeComments>
    /**
     * Gets static information about this bitmask useful for generic introspection.
     *
     * @return Zserio type information.
     */
    </#if>
    public static zserio.runtime.typeinfo.TypeInfo typeInfo()
    {
        return new zserio.runtime.typeinfo.TypeInfo.BitmaskTypeInfo(
                "${schemaTypeName}",
                ${name}.class,
                <@type_info underlyingTypeInfo/>,
                <@underlying_type_info_type_arguments bitSize!/>,
                java.util.Arrays.asList(
    <#list values as value>
                        <@item_info value.name, value.value, isBigInteger/><#if value?has_next>,</#if>
    </#list>
                )
        );
    }
</#if>

<#if withCodeComments>
    /**
     * Creates context for packed arrays.
     * <p>
     * Called only internally if packed arrays are used.
     *
     * @param contextNode Context for packed arrays.
     */
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
        return <#if isBigInteger>value.equals(other${name}.value)<#else>value == other${name}.value</#if>;
    }

    @Override
    public int hashCode()
    {
        int result = zserio.runtime.HashCodeUtil.HASH_SEED;
        result = zserio.runtime.HashCodeUtil.calcHashCode(result, value);
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
        if (builder.length() == 0 && <#if isBigInteger>value.equals(java.math.BigInteger.ZERO)<#else>value == 0</#if>)
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

<#if withCodeComments>
    /**
     * Gets the bitmask raw value.
     *
     * @return Raw value which holds this bitmask.
     */
</#if>
    public ${underlyingTypeInfo.typeFullName} getValue()
    {
        return value;
    }

    @Override
    public java.lang.Number getGenericValue()
    {
        return value;
    }

<#if withCodeComments>
    /**
     * Defines operator 'or' for the bitmask '${name}'.
     *
     * @param other Bitmask to be or'ed with this bitmask.
     *
     * \return Bitmask which contains result after applying the operator 'or' on this and other.
     */
</#if>
    public ${name} or(${name} other)
    {
<#if isBigInteger>
        <#-- big integer -->
        return new ${name}(value.or(other.value));
<#else>
        return new ${name}((${underlyingTypeInfo.typeFullName})(value | other.value));
</#if>
    }

<#if withCodeComments>
    /**
     * Defines operator 'and' for the bitmask '${name}'.
     *
     * @param other Bitmask to be and'ed with this bitmask.
     *
     * \return Bitmask which contains result after applying the operator 'and' on this and other.
     */
</#if>
    public ${name} and(${name} other)
    {
<#if isBigInteger>
        <#-- big integer -->
        return new ${name}(value.and(other.value));
<#else>
        return new ${name}((${underlyingTypeInfo.typeFullName})(value & other.value));
</#if>
    }

<#if withCodeComments>
    /**
     * Defines operator 'xor' for the bitmask '${name}'.
     *
     * @param other Bitmask to be xor'ed with this bitmask.
     *
     * \return Bitmask which contains result after applying the operator 'xor' on this and other.
     */
</#if>
    public ${name} xor(${name} other)
    {
<#if isBigInteger>
        <#-- big integer -->
        return new ${name}(value.xor(other.value));
<#else>
        return new ${name}((${underlyingTypeInfo.typeFullName})(value ^ other.value));
</#if>
    }

<#if withCodeComments>
    /**
     * Defines operator 'not' for the bitmask '${name}'.
     *
     * \return Bitmask whose value is ~this.
     */
</#if>
    public ${name} not()
    {
<#if isBigInteger>
        <#-- big integer -->
        return new ${name}(value.not().and(${upperBound}));
<#else>
        return new ${name}((${underlyingTypeInfo.typeFullName})(~value<#if upperBound??> & ${upperBound}</#if>));
</#if>
    }

<#if withCodeComments>
    /** Definition of all bitmask values. */
</#if>
    public static final class Values
    {
<#list values as value>
    <#if withCodeComments && value.docComments??>
        <@doc_comments value.docComments, 2/>
    </#if>
        public static final ${name} ${value.name} = new ${name}(${value.value});
</#list>
    }

    private ${underlyingTypeInfo.typeFullName} value;
}
