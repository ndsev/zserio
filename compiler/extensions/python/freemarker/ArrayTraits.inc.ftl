<#macro array_traits_create arrayTraits bitSize>
    zserio.array.${arrayTraits.name}(<#if arrayTraits.requiresElementBitSize>${bitSize}</#if>)<#t>
</#macro>

<#macro array_traits_create_field field>
    zserio.array.${field.arrayTraits.name}(<#if field.arrayTraits.requiresElementBitSize>${field.bitSize.value}</#if>)<#t>
</#macro>