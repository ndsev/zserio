<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}(enum.Enum):
<#list items as item>
    ${item.name} = ${item.value}
</#list>

    @classmethod
    def fromReader(cls: typing.Type['${name}'], reader: zserio.BitStreamReader) -> '${name}':
        return cls(reader.read${runtimeFunction.suffix}(${runtimeFunction.arg!}))

    def bitSizeOf(self, _bitPosition: int = 0) -> int:
<#if bitSize??>
        return ${bitSize}
<#else>
        return zserio.bitsizeof.getBitSizeOf${runtimeFunction.suffix}(self.value)
</#if>
<#if withWriterCode>

    def initializeOffsets(self, bitPosition: int) -> int:
        return bitPosition + self.bitSizeOf(bitPosition)

    def write(self, writer: zserio.BitStreamWriter) -> None:
        writer.write${runtimeFunction.suffix}(self.value<#rt>
                                              <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>)
</#if>
