<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}():
    def __init__(self):
        self._value = 0

    @classmethod
    def fromValue(cls, value):
        if value < ${lowerBound} or value > ${upperBound}:
            raise zserio.PythonRuntimeException("Value for bitmask '${name}' out of bounds: %d!" % value)

        instance = cls()
        instance._value = value
        return instance

    @classmethod
    def fromReader(cls, reader):
        instance = cls()
        instance._value = reader.read${runtimeFunction.suffix}(${runtimeFunction.arg!})
        return instance

    def __eq__(self, other):
        if isinstance(other, ${name}):
            return self._value == other._value

        return False

    def __hash__(self):
        result = zserio.hashcode.HASH_SEED

        result = zserio.hashcode.calcHashCode(result, hash(self._value))

        return result

    def __str__(self):
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

    def __or__(self, other):
        return ${name}.fromValue(self._value | other._value)

    def __and__(self, other):
        return ${name}.fromValue(self._value & other._value)

    def __xor__(self, other):
        return ${name}.fromValue(self._value ^ other._value)

    def __invert__(self):
        return ${name}.fromValue(~self._value & ${upperBound})

    def bitSizeOf(self, _bitPosition=0):
<#if bitSize??>
        return ${bitSize}
<#else>
        return zserio.bitsizeof.getBitSizeOf${runtimeFunction.suffix}(self._value)
</#if>
<#if withWriterCode>

    def initializeOffsets(self, bitPosition):
        return bitPosition + self.bitSizeOf(bitPosition)

    def write(self, writer):
        writer.write${runtimeFunction.suffix}(self._value<#rt>
                                              <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>)
</#if>

    def getValue(self):
        return self._value

    class Values():
        pass

<#list values as value>
${name}.Values.${value.name} = ${name}.fromValue(${value.value})
</#list>
