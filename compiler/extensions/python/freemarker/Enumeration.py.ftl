<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}(enum.Enum):
<#list items as item>
    ${item.name} = ${item.value}
</#list>

    @classmethod
    def from_reader(cls: typing.Type['${name}'], reader: zserio.BitStreamReader) -> '${name}':
        return cls(reader.read_${runtimeFunction.suffix}(${runtimeFunction.arg!}))

    def bitsizeof(self, _bitposition: int = 0) -> int:
<#if bitSize??>
        return ${bitSize}
<#else>
        return zserio.bitsizeof.bitsizeof_${runtimeFunction.suffix}(self.value)
</#if>
<#if withWriterCode>

    def initialize_offsets(self, bitposition: int) -> int:
        return bitposition + self.bitsizeof(bitposition)

    def write(self, writer: zserio.BitStreamWriter) -> None:
        writer.write_${runtimeFunction.suffix}(self.value<#rt>
                                               <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>)
</#if>
