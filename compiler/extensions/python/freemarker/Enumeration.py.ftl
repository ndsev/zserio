<#include "FileHeader.inc.ftl"/>
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
                           context_iterator: zserio.packed_array.PackingContextIterator,
                           reader: zserio.BitStreamReader) -> '${name}':
        context = next(context_iterator)
        return cls(context.read(reader))

    @staticmethod
    def create_packing_context(context_builder: zserio.packed_array.PackingContextBuilder) -> None:
        context_builder.add_context(zserio.array.${arrayTraits.name}(<#rt>
                <#lt><#if arrayTraits.requiresElementBitSize>${bitSize}</#if>))

    def init_packing_context(self, context_iterator: zserio.packed_array.PackingContextIterator) -> None:
        context = next(context_iterator)
        context.init(self.value)

    def bitsizeof(self, _bitposition: int = 0) -> int:
<#if bitSize??>
        return ${bitSize}
<#else>
        return zserio.bitsizeof.bitsizeof_${runtimeFunction.suffix}(self.value)
</#if>

    def bitsizeof_packed(self, context_iterator: zserio.packed_array.PackingContextIterator,
                         bitposition: int) -> int:
        context = next(context_iterator)
        return context.bitsizeof(bitposition, self.value)
<#if withWriterCode>

    def initialize_offsets(self, bitposition: int) -> int:
        return bitposition + self.bitsizeof(bitposition)

    def initialize_offsets_packed(self, context_iterator: zserio.packed_array.PackingContextIterator,
                                  bitposition: int) -> int:
        return bitposition + self.bitsizeof_packed(context_iterator, bitposition)

    def write(self, writer: zserio.BitStreamWriter) -> None:
        writer.write_${runtimeFunction.suffix}(self.value<#rt>
                                               <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>)

    def write_packed(self, context_iterator: zserio.packed_array.PackingContextIterator,
                     writer: zserio.BitStreamWriter) -> None:
        context = next(context_iterator)
        context.write(writer, self.value)
</#if>
