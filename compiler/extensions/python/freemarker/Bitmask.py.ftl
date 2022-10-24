<#include "FileHeader.inc.ftl"/>
<#include "ArrayTraits.inc.ftl"/>
<#include "DocComment.inc.ftl">
<#if withTypeInfoCode>
    <#include "TypeInfo.inc.ftl"/>
</#if>
<@file_header generatorDescription/>
<@future_annotations/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}:
<#if withCodeComments && docComments??>
<@doc_comments docComments, 1/>

</#if>
    def __init__(self) -> None:
<#if withCodeComments>
        """
        Default constructor.
        """

</#if>
        self._value = 0

    @classmethod
    def from_value(cls: typing.Type['${name}'], value: int) -> '${name}':
<#if withCodeComments>
        """
        Returns new object instance constructed from bitmask value.

        :param value: Bitmask value to construct from.
        """

</#if>
        if value < ${lowerBound} or value > ${upperBound}:
            raise zserio.PythonRuntimeException(f"Value for bitmask '${name}' out of bounds: {value}!")

        instance = cls()
        instance._value = value
        return instance

    @classmethod
    def from_reader(cls: typing.Type['${name}'], reader: zserio.BitStreamReader) -> '${name}':
<#if withCodeComments>
        """
        Returns new object instance constructed from bit stream reader.

        :param reader: Bit stream reader to use.
        """

</#if>
        instance = cls()
        instance._value = reader.read_${runtimeFunction.suffix}(${runtimeFunction.arg!})
        return instance

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
        instance = cls()
        instance._value = context_node.context.read(<@array_traits_create underlyingTypeInfo.arrayTraits, bitSize!/>, reader)
        return instance
<#if withTypeInfoCode>

    @staticmethod
    def type_info():
    <#if withCodeComments>
        """
        Gets static information about this bitmask type useful for generic introspection.

        :returns: Zserio type information.
        """

    </#if>
        attribute_list = {
            zserio.typeinfo.TypeAttribute.UNDERLYING_TYPE : <@type_info underlyingTypeInfo/>,
    <#if underlyingTypeInfo.isDynamicBitField>
            zserio.typeinfo.TypeAttribute.UNDERLYING_TYPE_ARGUMENTS: [(lambda: ${bitSize})],
    </#if>
            zserio.typeinfo.TypeAttribute.BITMASK_VALUES: [
    <#list values as value>
                zserio.typeinfo.ItemInfo('${value.schemaName}', ${name}.Values.${value.name})<#if value?has_next>,</#if>
    </#list>
            ]
        }

        return zserio.typeinfo.TypeInfo('${schemaTypeFullName}', ${name}, attributes=attribute_list)
</#if>

    def __eq__(self, other: object) -> bool:
        if isinstance(other, ${name}):
            return self._value == other._value

        return False

    def __hash__(self) -> int:
        result = zserio.hashcode.HASH_SEED
        result = zserio.hashcode.calc_hashcode_${underlyingTypeInfo.hashCodeFunc.suffix}(result, self._value)
        return result

    def __str__(self) -> str:
        result = ""

<#list values as value>
    <#if !value.isZero>
        if (self & ${name}.Values.${value.name}) == ${name}.Values.${value.name}:
            result += "${value.name}" if not result else " | ${value.name}"
    <#else>
        <#assign zeroValueName=value.name/><#-- may be there only once -->
    </#if>
</#list>
<#if zeroValueName??>
        if not result and self._value == 0:
            result += "${zeroValueName}"
</#if>

        return str(self._value) + "[" + result + "]"

    def __or__(self, other: '${name}') -> '${name}':
        return ${name}.from_value(self._value | other._value)

    def __and__(self, other: '${name}') -> '${name}':
        return ${name}.from_value(self._value & other._value)

    def __xor__(self, other: '${name}') -> '${name}':
        return ${name}.from_value(self._value ^ other._value)

    def __invert__(self) -> '${name}':
        return ${name}.from_value(~self._value & ${upperBound})

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
                                  self._value)

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
        return zserio.bitsizeof.bitsizeof_${runtimeFunction.suffix}(self._value)
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
                                              self._value)
<#if withWriterCode>

    def initialize_offsets(self, bitposition: int) -> int:
    <#if withCodeComments>
        """
        Initializes offsets in this bitmask object.

        Bitmask objects cannot have any offsets, thus this method just update bit stream position.

        :param bitposition: Bit stream position calculated from zero where the object will be serialized.

        :returns: Bit stream position calculated from zero updated to the first byte after serialized object.
        """

    </#if>
        return bitposition + self.bitsizeof(bitposition)

    def initialize_offsets_packed(self, context_node: zserio.array.PackingContextNode,
                                  bitposition: int) -> int:
    <#if withCodeComments>
        """
        Initializes offsets in this bitmask object.

        Bitmask objects cannot have any offsets, thus this method just update bit stream position.
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
        Serializes this bitmask object to the bit stream.

        :param writer: Bit stream writer where to serialize this bitmask object.
        """

    </#if>
        writer.write_${runtimeFunction.suffix}(self._value<#rt>
                                               <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>)

    def write_packed(self, context_node: zserio.array.PackingContextNode,
                     writer: zserio.BitStreamWriter) -> None:
    <#if withCodeComments>
        """
        Serializes this bitmask object to the bit stream.

        Called only internally if packed arrays are used.

        :param zserio_context_node: Context for packed arrays.
        :param writer: Bit stream writer where to serialize this bitmask object.
        """

    </#if>
        context_node.context.write(<@array_traits_create underlyingTypeInfo.arrayTraits, bitSize!/>, writer, self._value)
</#if>

    @property
    def value(self) -> int:
<#if withCodeComments>
        """
        Gets the bitmask raw value.

        :returns: Raw value which holds this bitmask.
        """

</#if>
        return self._value

    class Values:
<#list values as value>
        ${value.name}: '${name}' = None
    <#if withCodeComments && value.docComments??>
        <@doc_comments value.docComments, 2/>
    </#if>
</#list>

<#list values as value>
${name}.Values.${value.name} = ${name}.from_value(${value.value})
</#list>
