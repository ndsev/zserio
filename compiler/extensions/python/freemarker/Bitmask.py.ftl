<#include "FileHeader.inc.ftl"/>
<#include "ArrayTraits.inc.ftl"/>
<#if withTypeInfoCode>
    <#include "TypeInfo.inc.ftl"/>
</#if>
<@file_header generatorDescription/>
<@future_annotations/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}:
    def __init__(self) -> None:
        self._value = 0

    @classmethod
    def from_value(cls: typing.Type['${name}'], value: int) -> '${name}':
        if value < ${lowerBound} or value > ${upperBound}:
            raise zserio.PythonRuntimeException("Value for bitmask '${name}' out of bounds: %d!" % value)

        instance = cls()
        instance._value = value
        return instance

    @classmethod
    def from_reader(cls: typing.Type['${name}'], reader: zserio.BitStreamReader) -> '${name}':
        instance = cls()
        instance._value = reader.read_${runtimeFunction.suffix}(${runtimeFunction.arg!})
        return instance

    @classmethod
    def from_reader_packed(cls: typing.Type['${name}'],
                           context_node: zserio.array.PackingContextNode,
                           reader: zserio.BitStreamReader) -> '${name}':
        instance = cls()
        instance._value = context_node.context.read(<@array_traits_create arrayTraits, bitSize!/>, reader)
        return instance
<#if withTypeInfoCode>

    @staticmethod
    def type_info():
        attributes = {
            zserio.typeinfo.TypeAttribute.UNDERLYING_TYPE : <@type_info underlyingType/>,
    <#if underlyingType.isDynamicBitField>
            zserio.typeinfo.TypeAttribute.UNDERLYING_TYPE_ARGUMENTS: ['${bitSize}'],
    </#if>
            zserio.typeinfo.TypeAttribute.BITMASK_VALUES: [
    <#list values as value>
                zserio.typeinfo.ItemInfo('${value.schemaName}', ${name}.Values.${value.name})<#if value?has_next>,</#if>
    </#list>
            ]
        }

        return zserio.typeinfo.TypeInfo('${schemaTypeName}', ${name}, attributes=attributes)
</#if>

    def __eq__(self, other: object) -> bool:
        if isinstance(other, ${name}):
            return self._value == other._value

        return False

    def __hash__(self) -> int:
        result = zserio.hashcode.HASH_SEED

        result = zserio.hashcode.calc_hashcode(result, hash(self._value))

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
        context_node.create_context()

    def init_packing_context(self, context_node: zserio.array.PackingContextNode) -> None:
        context_node.context.init(<@array_traits_create arrayTraits, bitSize!/>,
                                  self._value)

    def bitsizeof(self, _bitposition: int = 0) -> int:
<#if bitSize??>
        return ${bitSize}
<#else>
        return zserio.bitsizeof.bitsizeof_${runtimeFunction.suffix}(self._value)
</#if>

    def bitsizeof_packed(self, context_node: zserio.array.PackingContextNode,
                         _bitposition: int) -> int:
        return context_node.context.bitsizeof(<@array_traits_create arrayTraits, bitSize!/>,
                                              self._value)
<#if withWriterCode>

    def initialize_offsets(self, bit_position: int) -> int:
        return bit_position + self.bitsizeof(bit_position)

    def initialize_offsets_packed(self, context_node: zserio.array.PackingContextNode,
                                  bitposition: int) -> int:
        return bitposition + self.bitsizeof_packed(context_node, bitposition)

    def write(self, writer: zserio.BitStreamWriter, *, zserio_call_initialize_offsets: bool = True) -> None:
        del zserio_call_initialize_offsets
        writer.write_${runtimeFunction.suffix}(self._value<#rt>
                                               <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>)

    def write_packed(self, context_node: zserio.array.PackingContextNode,
                     writer: zserio.BitStreamWriter) -> None:
        context_node.context.write(<@array_traits_create arrayTraits, bitSize!/>, writer, self._value)
</#if>

    @property
    def value(self) -> int:
        return self._value

    class Values:
<#list values as value>
        ${value.name}: '${name}' = None
</#list>

<#list values as value>
${name}.Values.${value.name} = ${name}.from_value(${value.value})
</#list>
