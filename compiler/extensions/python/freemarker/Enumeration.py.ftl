<#include "FileHeader.inc.ftl"/>
<#include "ArrayTraits.inc.ftl"/>
<#include "DocComment.inc.ftl">
<#if withTypeInfoCode>
    <#include "TypeInfo.inc.ftl"/>
</#if>
<@file_header generatorDescription/>
<@future_annotations/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}(enum.Enum):
<#if withCodeComments && docComments??>
<@doc_comments docComments, 1/>

</#if>
<#list items as item>
    ${item.name} = ${item.value}
    <#if withCodeComments && item.docComments??>
    <@doc_comments item.docComments, 1/>
    </#if>
</#list>

    @classmethod
    def from_reader(cls: typing.Type['${name}'], reader: zserio.BitStreamReader) -> '${name}':
<#if withCodeComments>
        """
        Returns new object instance constructed from bit stream reader.

        :param reader: Bit stream reader to use.
        """

</#if>
        return cls(reader.read_${runtimeFunction.suffix}(${runtimeFunction.arg!}))

    @classmethod
    def from_reader_packed(cls: typing.Type['${name}'],
                           context_node: zserio.array.PackingContextNode,
                           reader: zserio.BitStreamReader) -> '${name}':
<#if withCodeComments>
        """
        Returns new object instance constructed from bit stream reader.

        Called only internally if packed arrays are used.

        :param context_node: Context for packed arrays.
        :param reader: Bit stream reader to use.
        """

</#if>
        return cls(context_node.context.read(<@array_traits_create underlyingTypeInfo.arrayTraits, bitSize!/>, reader))
<#if withTypeInfoCode>

    @staticmethod
    def type_info():
    <#if withCodeComments>
        """
        Gets static information about this enumeration type useful for generic introspection.

        :returns: Zserio type information.
        """

    </#if>
        attribute_list = {
            zserio.typeinfo.TypeAttribute.UNDERLYING_TYPE : <@type_info underlyingTypeInfo/>,
    <#if underlyingTypeInfo.isDynamicBitField>
            zserio.typeinfo.TypeAttribute.UNDERLYING_TYPE_ARGUMENTS: [(lambda: ${bitSize})],
    </#if>
            zserio.typeinfo.TypeAttribute.ENUM_ITEMS: [
    <#list items as item>
                zserio.typeinfo.ItemInfo('${item.schemaName}', ${name}.${item.name})<#if item?has_next>,</#if>
    </#list>
            ]
        }

        return zserio.typeinfo.TypeInfo('${schemaTypeFullName}', ${name}, attributes=attribute_list)
</#if>

    <#-- we need custom hash implementation to get deterministic hash codes -->
    def __hash__(self) -> int:
        result = zserio.hashcode.HASH_SEED
        result = zserio.hashcode.calc_hashcode_${underlyingTypeInfo.hashCodeFunc.suffix}(result, self.value)
        return result

    @staticmethod
    def create_packing_context(context_node: zserio.array.PackingContextNode) -> None:
<#if withCodeComments>
        """
        Creates context for packed arrays.

        Called only internally if packed arrays are used.

        :param context_node: Context for packed arrays.
        """

</#if>
        context_node.create_context()

    def init_packing_context(self, context_node: zserio.array.PackingContextNode) -> None:
<#if withCodeComments>
        """
        Initializes context for packed arrays.

        Called only internally if packed arrays are used.

        :param context_node: Context for packed arrays.
        """

</#if>
        context_node.context.init(<@array_traits_create underlyingTypeInfo.arrayTraits, bitSize!/>,
                                  self.value)

    def bitsizeof(self, _bitposition: int = 0) -> int:
<#if withCodeComments>
        """
        Calculates size of the serialized object in bits.

        :param _bitposition: Bit stream position calculated from zero where the object will be serialized.

        :returns: Number of bits which are needed to store serialized object.
        """

</#if>
<#if bitSize??>
        return ${bitSize}
<#else>
        return zserio.bitsizeof.bitsizeof_${runtimeFunction.suffix}(self.value)
</#if>

    def bitsizeof_packed(self, context_node: zserio.array.PackingContextNode,
                         _bitposition: int) -> int:
<#if withCodeComments>
        """
        Calculates size of the serialized object in bits for packed arrays.

        Called only internally if packed arrays are used.

        :param context_node: Context for packed arrays.
        :param _bitposition: Bit stream position calculated from zero where the object will be serialized.

        :returns: Number of bits which are needed to store serialized object.
        """

</#if>
        return context_node.context.bitsizeof(<@array_traits_create underlyingTypeInfo.arrayTraits, bitSize!/>,
                                              self.value)
<#if withWriterCode>

    def initialize_offsets(self, bitposition: int) -> int:
    <#if withCodeComments>
        """
        Initializes offsets in this enumeration object.

        Enumeration objects cannot have any offsets, thus this method just update bit stream position.

        :param bitposition: Bit stream position calculated from zero where the object will be serialized.

        :returns: Bit stream position calculated from zero updated to the first byte after serialized object.
        """

    </#if>
        return bitposition + self.bitsizeof(bitposition)

    def initialize_offsets_packed(self, context_node: zserio.array.PackingContextNode,
                                  bitposition: int) -> int:
    <#if withCodeComments>
        """
        Initializes offsets in this enumeration object.

        Enumeration objects cannot have any offsets, thus this method just update bit stream position.
        Called only internally if packed arrays are used.

        :param context_node: Context for packed arrays.
        :param bitposition: Bit stream position calculated from zero where the object will be serialized.

        :returns: Bit stream position calculated from zero updated to the first byte after serialized object.
        """

    </#if>
        return bitposition + self.bitsizeof_packed(context_node, bitposition)

    def write(self, writer: zserio.BitStreamWriter) -> None:
    <#if withCodeComments>
        """
        Serializes this enumeration object to the bit stream.

        :param writer: Bit stream writer where to serialize this enumeration object.
        """

    </#if>
        writer.write_${runtimeFunction.suffix}(self.value<#rt>
                                               <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>)

    def write_packed(self, context_node: zserio.array.PackingContextNode,
                     writer: zserio.BitStreamWriter) -> None:
    <#if withCodeComments>
        """
        Serializes this enumeratin object to the bit stream.

        Called only internally if packed arrays are used.

        :param zserio_context_node: Context for packed arrays.
        :param writer: Bit stream writer where to serialize this enumeration object.
        """

    </#if>
        context_node.context.write(<@array_traits_create underlyingTypeInfo.arrayTraits, bitSize!/>, writer, self.value)
</#if>
