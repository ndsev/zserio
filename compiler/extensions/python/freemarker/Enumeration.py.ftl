<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>

import enum
<@all_imports packageImports typeImports/>

class ${name}(enum.Enum):
<#list items as item>
    ${item.name} = ${item.value}
</#list>

    @classmethod
    def fromReader(cls, reader):
        return cls(reader.read${runtimeFunction.suffix}(${runtimeFunction.arg!}))

    def bitSizeOf(self, _bitPosition=0):
<#if bitSize??>
        return ${bitSize}
<#else>
        return zserio.bitsizeof.getBitSizeOf${runtimeFunction.suffix}(self.value)
</#if>
<#if withWriterCode>

    def initializeOffsets(self, bitPosition):
        return bitPosition + self.bitSizeOf(bitPosition)

    def write(self, writer):
        writer.write${runtimeFunction.suffix}(self.value<#rt>
                                              <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>)
</#if>
