<#include "FileHeader.inc.ftl"/>
<#include "ArrayTraits.inc.ftl"/>
<#if withTypeInfoCode>
    <#include "TypeInfo.inc.ftl"/>
</#if>
<@file_header generatorDescription/>
<@future_annotations/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}(enum.Enum):
<#list items as item>
    ${item.name} = ${item.value}
</#list>

    @classmethod
    def from_reader(cls: typing.Type['${name}'], reader: zserio.BitStreamReader) -> '${name}':
        return cls(reader.read_${runtimeFunction.suffix}(${runtimeFunction.arg!}))

    @classmethod
    def from_reader_packed(cls: typing.Type['${name}'],
                           context_node: zserio.array.PackingContextNode,
                           reader: zserio.BitStreamReader) -> '${name}':
        return cls(context_node.context.read(<@array_traits_create arrayTraits, bitSize!/>, reader))
<#if withTypeInfoCode>

    @staticmethod
    def type_info():
        attributes = {
            zserio.typeinfo.TypeAttribute.UNDERLYING_TYPE : <@type_info underlyingType/>,
    <#if underlyingType.isDynamicBitField>
            zserio.typeinfo.TypeAttribute.UNDERLYING_TYPE_ARGUMENTS: ['${bitSize}'],
    </#if>
            zserio.typeinfo.TypeAttribute.ENUM_ITEMS: [
    <#list items as item>
                zserio.typeinfo.ItemInfo('${item.schemaName}', ${name}.${item.name})<#if item?has_next>,</#if>
    </#list>
            ]
        }

        return zserio.typeinfo.TypeInfo('${schemaTypeName}', ${name}, attributes=attributes)
</#if>

    @staticmethod
    def create_packing_context(context_node: zserio.array.PackingContextNode) -> None:
        context_node.create_context()

    def init_packing_context(self, context_node: zserio.array.PackingContextNode) -> None:
        context_node.context.init(<@array_traits_create arrayTraits, bitSize!/>,
                                  self.value)

    def bitsizeof(self, _bitposition: int = 0) -> int:
<#if bitSize??>
        return ${bitSize}
<#else>
        return zserio.bitsizeof.bitsizeof_${runtimeFunction.suffix}(self.value)
</#if>

    def bitsizeof_packed(self, context_node: zserio.array.PackingContextNode,
                         _bitposition: int) -> int:
        return context_node.context.bitsizeof(<@array_traits_create arrayTraits, bitSize!/>,
                                              self.value)
<#if withWriterCode>

    def initialize_offsets(self, bitposition: int) -> int:
        return bitposition + self.bitsizeof(bitposition)

    def initialize_offsets_packed(self, context_node: zserio.array.PackingContextNode,
                                  bitposition: int) -> int:
        return bitposition + self.bitsizeof_packed(context_node, bitposition)

    def write(self, writer: zserio.BitStreamWriter, *, zserio_call_initialize_offsets: bool = True) -> None:
        del zserio_call_initialize_offsets
        writer.write_${runtimeFunction.suffix}(self.value<#rt>
                                               <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>)

    def write_packed(self, context_node: zserio.array.PackingContextNode,
                     writer: zserio.BitStreamWriter) -> None:
        context_node.context.write(<@array_traits_create arrayTraits, bitSize!/>, writer, self.value)
</#if>
