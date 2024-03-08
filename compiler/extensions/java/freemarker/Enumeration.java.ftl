<#include "FileHeader.inc.ftl">
<#include "TypeInfo.inc.ftl">
<#include "DocComment.inc.ftl">
<@standard_header generatorDescription, packageName/>
<#macro enum_array_traits arrayableInfo bitSize>
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
public enum ${name} implements <#if withWriterCode>zserio.runtime.io.<#if usedInPackedArray>Packable</#if>Writer, </#if><#rt>
        <#lt>zserio.runtime.<#if usedInPackedArray>Packable</#if>SizeOf,
        zserio.runtime.ZserioEnum
{
<#list items as item>
    <#if withCodeComments && item.docComments??>
    <@doc_comments item.docComments, 1/>
    </#if>
    <#if item.isDeprecated>
    @Deprecated
    </#if>
    ${item.name}(${item.value})<#if item?has_next>,<#else>;</#if>
</#list>

    private ${name}(${underlyingTypeInfo.typeFullName} value)
    {
        this.value = value;
    }

<#if withCodeComments>
    /**
     * Gets the enumeration raw value.
     *
     * @return Raw value which holds this enumeration.
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
<#if withTypeInfoCode>

    <#if withCodeComments>
    /**
     * Gets static information about this enumeration useful for generic introspection.
     *
     * @return Zserio type information.
     */
    </#if>
    public static zserio.runtime.typeinfo.TypeInfo typeInfo()
    {
        return new zserio.runtime.typeinfo.TypeInfo.EnumTypeInfo(
                "${schemaTypeName}",
                ${name}.class,
                <@type_info underlyingTypeInfo/>,
                <@underlying_type_info_type_arguments bitSize!/>,
                java.util.Arrays.asList(
    <#list items as item>
                        <@item_info item.schemaName, item.value, isBigInteger, item.isDeprecated, item.isRemoved/><#if item?has_next>,</#if>
    </#list>
                )
            );
    }
</#if>
<#if usedInPackedArray>

    @Override
    public void initPackingContext(zserio.runtime.array.PackingContext context)
    {
        final zserio.runtime.array.DeltaContext deltaContext = context.cast();
        deltaContext.init(
                <@enum_array_traits underlyingTypeInfo.arrayableInfo, bitSize!/>,
                new ${underlyingTypeInfo.arrayableInfo.arrayElement}(value));
    }
</#if>

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
<#if usedInPackedArray>

    @Override
    public int bitSizeOf(zserio.runtime.array.PackingContext context, long bitPosition)
    {
        final zserio.runtime.array.DeltaContext deltaContext = context.cast();
        return deltaContext.bitSizeOf(
                <@enum_array_traits underlyingTypeInfo.arrayableInfo, bitSize!/>,
                new ${underlyingTypeInfo.arrayableInfo.arrayElement}(value));
    }
</#if>
<#if withWriterCode>

    @Override
    public long initializeOffsets()
    {
        return initializeOffsets(0);
    }

    @Override
    public long initializeOffsets(long bitPosition) throws zserio.runtime.ZserioError
    {
        return bitPosition + bitSizeOf(bitPosition);
    }
    <#if usedInPackedArray>

    @Override
    public long initializeOffsets(zserio.runtime.array.PackingContext context, long bitPosition)
    {
        return bitPosition + bitSizeOf(context, bitPosition);
    }
    </#if>

<#function has_removed_items items>
    <#list items as item>
        <#if item.isRemoved>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>
<#macro removed_items_check items>
    <#local isFirst=true/>
        if (<#rt>
    <#list items as item>
        <#if item.isRemoved>
            <#if isFirst>
                <#local isFirst=false/>
                this == ${item.name}<#t>
            <#else>
                <#lt> ||
                this == ${item.name}<#rt>
            </#if>
        </#if>
    </#list>
        <#lt>)
        {
            throw new zserio.runtime.ZserioError("Trying to write removed enumeration item '" + this + "'!");
        }
</#macro>
    @Override
    public void write(zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
    {
    <#if has_removed_items(items)>
        <@removed_items_check items/>
    </#if>
        out.write${runtimeFunction.suffix}(getValue()<#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>);
    }
    <#if usedInPackedArray>

    @Override
    public void write(zserio.runtime.array.PackingContext context, zserio.runtime.io.BitStreamWriter out)
            throws java.io.IOException
    {
        final zserio.runtime.array.DeltaContext deltaContext = context.cast();
    <#if has_removed_items(items)>
        <@removed_items_check items/>
    </#if>
        deltaContext.write(
                <@enum_array_traits underlyingTypeInfo.arrayableInfo, bitSize!/>, out,
                new ${underlyingTypeInfo.arrayableInfo.arrayElement}(value));
    }
    </#if>

</#if>
<#if withCodeComments>
    /**
     * Reads enumeration from the bit stream.
     *
     * @param in Bit stream reader to use.
     *
     * @throws IOException If the reading from the bit stream failed.
     */
</#if>
    public static ${name} readEnum(zserio.runtime.io.BitStreamReader in) throws java.io.IOException
    {
        return toEnum(<#if runtimeFunction.javaReadTypeName??>(${runtimeFunction.javaReadTypeName})</#if><#rt>
                <#lt>in.read${runtimeFunction.suffix}(${runtimeFunction.arg!}));
    }
<#if usedInPackedArray>

    <#if withCodeComments>
    /**
     * Reads enumeration from the bit stream.
     * <p>
     * Called only internally if packed arrays are used.
     *
     * @param context Context for packed arrays.
     * @param in Bit stream reader to use.
     *
     * @throws IOException If the reading from the bit stream failed.
     */
    </#if>
    public static ${name} readEnum(zserio.runtime.array.PackingContext context,
            zserio.runtime.io.BitStreamReader in) throws java.io.IOException
    {
        final zserio.runtime.array.DeltaContext deltaContext = context.cast();
        return toEnum(((${underlyingTypeInfo.arrayableInfo.arrayElement})
                deltaContext.read(
                        <@enum_array_traits underlyingTypeInfo.arrayableInfo, bitSize!/>, in)).get());
    }
</#if>

<#if withCodeComments>
    /**
     * Converts raw value to the enumeration.
     *
     * @param value Raw value to convert.
     *
     * @return The enumeration which holds given raw value.
     */
</#if>
    public static ${name} toEnum(${underlyingTypeInfo.typeFullName} value)
    {
<#if underlyingTypeInfo.isLong || isBigInteger>
    <#-- can't use switch for long and for BigInteger -->
    <#list items as item>
        <#if isBigInteger>
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

<#if withCodeComments>
    /**
     * Converts enumeration item name to the enumeration item.
     *
     * @param value Enumeration item name to convert.
     *
     * @return The enumeration item matching its string name.
     */
</#if>
    public static ${name} toEnum(java.lang.String itemName)
    {
<#list items as item>
        if (itemName.equals("${item.name}"))
            return ${item.name};
</#list>
        throw new java.lang.IllegalArgumentException(
                "Enum item '" + itemName + "' doesn't exist in enumeration ${name}!");
    }

    private ${underlyingTypeInfo.typeFullName} value;
}
