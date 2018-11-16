<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>

import enum
<@all_imports packageImports typeImports/>

class ${name}(enum.Enum):
<#list items as item>
    ${item.name} = ${item.value}
</#list>

    def bitSizeOf(self, _bitPosition = 0):
<#if bitSize??>
        return ${bitSize}
<#else>
        return zserio.bitsizeof.getBitSizeOf${runtimeFunction.suffix}(self.value)
</#if>

    def initializeOffsets(self, bitPosition = 0):
        return bitPosition + self.bitSizeOf(bitPosition)

    def write(self, writer):
        writer.write${runtimeFunction.suffix}(self.value<#rt>
                                              <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>)

    @staticmethod
    def read(reader):
        return ${name}(reader.read${runtimeFunction.suffix}(${runtimeFunction.arg!}))
