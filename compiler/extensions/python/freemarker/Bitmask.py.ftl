<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}:
    def __init__(self) -> None:
        self._value = 0

    @classmethod
    def fromValue(cls: typing.Type['${name}'], value: int) -> '${name}':
        if value < ${lowerBound} or value > ${upperBound}:
            raise zserio.PythonRuntimeException("Value for bitmask '${name}' out of bounds: %d!" % value)

        instance = cls()
        instance._value = value
        return instance

    @classmethod
    def fromReader(cls: typing.Type['${name}'], reader: zserio.BitStreamReader) -> '${name}':
        instance = cls()
        instance._value = reader.read${runtimeFunction.suffix}(${runtimeFunction.arg!})
        return instance

    def __eq__(self, other: object) -> bool:
        if isinstance(other, ${name}):
            return self._value == other._value

        return False

    def __hash__(self) -> int:
        result = zserio.hashcode.HASH_SEED

        result = zserio.hashcode.calcHashCode(result, hash(self._value))

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
        return ${name}.fromValue(self._value | other._value)

    def __and__(self, other: '${name}') -> '${name}':
        return ${name}.fromValue(self._value & other._value)

    def __xor__(self, other: '${name}') -> '${name}':
        return ${name}.fromValue(self._value ^ other._value)

    def __invert__(self) -> '${name}':
        return ${name}.fromValue(~self._value & ${upperBound})

    def bitSizeOf(self, _bitPosition: int = 0) -> int:
<#if bitSize??>
        return ${bitSize}
<#else>
        return zserio.bitsizeof.getBitSizeOf${runtimeFunction.suffix}(self._value)
</#if>
<#if withWriterCode>

    def initializeOffsets(self, bitPosition: int) -> int:
        return bitPosition + self.bitSizeOf(bitPosition)

    def write(self, writer: zserio.BitStreamWriter) -> None:
        writer.write${runtimeFunction.suffix}(self._value<#rt>
                                              <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>)
</#if>

    def getValue(self) -> int:
        return self._value

    class Values:
<#list values as value>
        ${value.name} = None # type: '${name}'
</#list>

<#list values as value>
${name}.Values.${value.name} = ${name}.fromValue(${value.value})
</#list>
