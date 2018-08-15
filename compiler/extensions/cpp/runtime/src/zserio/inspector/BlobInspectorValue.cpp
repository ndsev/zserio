#include "../CppRuntimeException.h"
#include "../StringConvertUtil.h"

#include "IBlobInspectorValueListener.h"
#include "BlobInspectorValue.h"

namespace zserio
{

BlobInspectorValue::BlobInspectorValue() : m_type(VT_UNDEFINED)
{
}

BlobInspectorValue::BlobInspectorValue(bool boolValue)
{
    set(boolValue);
}

BlobInspectorValue::BlobInspectorValue(int8_t int8Value)
{
    set(int8Value);
}

BlobInspectorValue::BlobInspectorValue(int16_t int16Value)
{
    set(int16Value);
}

BlobInspectorValue::BlobInspectorValue(int32_t int32Value)
{
    set(int32Value);
}

BlobInspectorValue::BlobInspectorValue(int64_t int64Value)
{
    set(int64Value);
}

BlobInspectorValue::BlobInspectorValue(uint8_t uint8Value)
{
    set(uint8Value);
}

BlobInspectorValue::BlobInspectorValue(uint16_t uint16Value)
{
    set(uint16Value);
}

BlobInspectorValue::BlobInspectorValue(uint32_t uint32Value)
{
    set(uint32Value);
}

BlobInspectorValue::BlobInspectorValue(uint64_t uint64Value)
{
    set(uint64Value);
}

BlobInspectorValue::BlobInspectorValue(float floatValue)
{
    set(floatValue);
}

BlobInspectorValue::BlobInspectorValue(double doubleValue)
{
    set(doubleValue);
}

BlobInspectorValue::BlobInspectorValue(const std::string& stringValue)
{
    set(stringValue);
}

BlobInspectorValue::BlobInspectorValue(int8_t enumValue, const std::string& enumSymbol)
{
    set(enumValue, enumSymbol);
}

BlobInspectorValue::BlobInspectorValue(int16_t enumValue, const std::string& enumSymbol)
{
    set(enumValue, enumSymbol);
}

BlobInspectorValue::BlobInspectorValue(int32_t enumValue, const std::string& enumSymbol)
{
    set(enumValue, enumSymbol);
}

BlobInspectorValue::BlobInspectorValue(int64_t enumValue, const std::string& enumSymbol)
{
    set(enumValue, enumSymbol);
}

BlobInspectorValue::BlobInspectorValue(uint8_t enumValue, const std::string& enumSymbol)
{
    set(enumValue, enumSymbol);
}

BlobInspectorValue::BlobInspectorValue(uint16_t enumValue, const std::string& enumSymbol)
{
    set(enumValue, enumSymbol);
}

BlobInspectorValue::BlobInspectorValue(uint32_t enumValue, const std::string& enumSymbol)
{
    set(enumValue, enumSymbol);
}

BlobInspectorValue::BlobInspectorValue(uint64_t enumValue, const std::string& enumSymbol)
{
    set(enumValue, enumSymbol);
}

void BlobInspectorValue::set(bool boolValue)
{
    m_type = VT_BOOL;
    m_value.boolValue = boolValue;
}

void BlobInspectorValue::set(int8_t int8Value)
{
    m_type = VT_INT8;
    m_value.int8Value = int8Value;
}

void BlobInspectorValue::set(int16_t int16Value)
{
    m_type = VT_INT16;
    m_value.int16Value = int16Value;
}

void BlobInspectorValue::set(int32_t int32Value)
{
    m_type = VT_INT32;
    m_value.int32Value = int32Value;
}

void BlobInspectorValue::set(int64_t int64Value)
{
    m_type = VT_INT64;
    m_value.int64Value = int64Value;
}

void BlobInspectorValue::set(uint8_t uint8Value)
{
    m_type = VT_UINT8;
    m_value.uint8Value = uint8Value;
}

void BlobInspectorValue::set(uint16_t uint16Value)
{
    m_type = VT_UINT16;
    m_value.uint16Value = uint16Value;
}

void BlobInspectorValue::set(uint32_t uint32Value)
{
    m_type = VT_UINT32;
    m_value.uint32Value = uint32Value;
}

void BlobInspectorValue::set(uint64_t uint64Value)
{
    m_type = VT_UINT64;
    m_value.uint64Value = uint64Value;
}

void BlobInspectorValue::set(float floatValue)
{
    m_type = VT_FLOAT;
    m_value.floatValue = floatValue;
}

void BlobInspectorValue::set(double doubleValue)
{
    m_type = VT_DOUBLE;
    m_value.doubleValue = doubleValue;
}

void BlobInspectorValue::set(const std::string& stringValue)
{
    m_type = VT_STRING;
    m_stringValue = stringValue;
}

void BlobInspectorValue::set(int8_t enumValue, const std::string& enumSymbol)
{
    m_type = VT_INT8_ENUM;
    m_value.int8Value = enumValue;
    m_stringValue = enumSymbol;
}

void BlobInspectorValue::set(int16_t enumValue, const std::string& enumSymbol)
{
    m_type = VT_INT16_ENUM;
    m_value.int16Value = enumValue;
    m_stringValue = enumSymbol;
}

void BlobInspectorValue::set(int32_t enumValue, const std::string& enumSymbol)
{
    m_type = VT_INT32_ENUM;
    m_value.int32Value = enumValue;
    m_stringValue = enumSymbol;
}

void BlobInspectorValue::set(int64_t enumValue, const std::string& enumSymbol)
{
    m_type = VT_INT64_ENUM;
    m_value.int64Value = enumValue;
    m_stringValue = enumSymbol;
}

void BlobInspectorValue::set(uint8_t enumValue, const std::string& enumSymbol)
{
    m_type = VT_UINT8_ENUM;
    m_value.uint8Value = enumValue;
    m_stringValue = enumSymbol;
}

void BlobInspectorValue::set(uint16_t enumValue, const std::string& enumSymbol)
{
    m_type = VT_UINT16_ENUM;
    m_value.uint16Value = enumValue;
    m_stringValue = enumSymbol;
}

void BlobInspectorValue::set(uint32_t enumValue, const std::string& enumSymbol)
{
    m_type = VT_UINT32_ENUM;
    m_value.uint32Value = enumValue;
    m_stringValue = enumSymbol;
}

void BlobInspectorValue::set(uint64_t enumValue, const std::string& enumSymbol)
{
    m_type = VT_UINT64_ENUM;
    m_value.uint64Value = enumValue;
    m_stringValue = enumSymbol;
}

bool BlobInspectorValue::operator==(const BlobInspectorValue& other) const
{
    if (m_type != other.m_type)
        return false;

    switch (m_type)
    {
    case VT_BOOL:
        if (m_value.boolValue != other.m_value.boolValue)
            return false;
        break;

    case VT_INT8:
        if (m_value.int8Value != other.m_value.int8Value)
            return false;
        break;

    case VT_INT16:
        if (m_value.int16Value != other.m_value.int16Value)
            return false;
        break;

    case VT_INT32:
        if (m_value.int32Value != other.m_value.int32Value)
            return false;
        break;

    case VT_INT64:
        if (m_value.int64Value != other.m_value.int64Value)
            return false;
        break;

    case VT_UINT8:
        if (m_value.uint8Value != other.m_value.uint8Value)
            return false;
        break;

    case VT_UINT16:
        if (m_value.uint16Value != other.m_value.uint16Value)
            return false;
        break;

    case VT_UINT32:
        if (m_value.uint32Value != other.m_value.uint32Value)
            return false;
        break;

    case VT_UINT64:
        if (m_value.uint64Value != other.m_value.uint64Value)
            return false;
        break;

    case VT_FLOAT:
        if (m_value.floatValue != other.m_value.floatValue)
            return false;
        break;

    case VT_DOUBLE:
        if (m_value.doubleValue != other.m_value.doubleValue)
            return false;
        break;

    case VT_STRING:
        if (m_stringValue != other.m_stringValue)
            return false;
        break;

    case VT_INT8_ENUM:
        if (m_value.int8Value != other.m_value.int8Value || m_stringValue != other.m_stringValue)
            return false;
        break;

    case VT_INT16_ENUM:
        if (m_value.int16Value != other.m_value.int16Value || m_stringValue != other.m_stringValue)
            return false;
        break;

    case VT_INT32_ENUM:
        if (m_value.int32Value != other.m_value.int32Value || m_stringValue != other.m_stringValue)
            return false;
        break;

    case VT_INT64_ENUM:
        if (m_value.int64Value != other.m_value.int64Value || m_stringValue != other.m_stringValue)
            return false;
        break;

    case VT_UINT8_ENUM:
        if (m_value.uint8Value != other.m_value.uint8Value || m_stringValue != other.m_stringValue)
            return false;
        break;

    case VT_UINT16_ENUM:
        if (m_value.uint16Value != other.m_value.uint16Value || m_stringValue != other.m_stringValue)
            return false;
        break;

    case VT_UINT32_ENUM:
        if (m_value.uint32Value != other.m_value.uint32Value || m_stringValue != other.m_stringValue)
            return false;
        break;

    case VT_UINT64_ENUM:
        if (m_value.uint64Value != other.m_value.uint64Value || m_stringValue != other.m_stringValue)
            return false;
        break;

    case VT_UNDEFINED:
    default:
        break;
    }

    return true;
}

BlobInspectorValue::ValueType BlobInspectorValue::getType() const
{
    return m_type;
}

void BlobInspectorValue::get(bool& boolValue) const
{
    checkValueType(VT_BOOL);
    boolValue = m_value.boolValue;
}

void BlobInspectorValue::get(int8_t& int8Value) const
{
    checkValueType(VT_INT8);
    int8Value = m_value.int8Value;
}

void BlobInspectorValue::get(int16_t& int16Value) const
{
    checkValueType(VT_INT16);
    int16Value = m_value.int16Value;
}

void BlobInspectorValue::get(int32_t& int32Value) const
{
    checkValueType(VT_INT32);
    int32Value = m_value.int32Value;
}

void BlobInspectorValue::get(int64_t& int64Value) const
{
    checkValueType(VT_INT64);
    int64Value = m_value.int64Value;
}

void BlobInspectorValue::get(uint8_t& uint8Value) const
{
    checkValueType(VT_UINT8);
    uint8Value = m_value.uint8Value;
}

void BlobInspectorValue::get(uint16_t& uint16Value) const
{
    checkValueType(VT_UINT16);
    uint16Value = m_value.uint16Value;
}

void BlobInspectorValue::get(uint32_t& uint32Value) const
{
    checkValueType(VT_UINT32);
    uint32Value = m_value.uint32Value;
}

void BlobInspectorValue::get(uint64_t& uint64Value) const
{
    checkValueType(VT_UINT64);
    uint64Value = m_value.uint64Value;
}

void BlobInspectorValue::get(float& floatValue) const
{
    checkValueType(VT_FLOAT);
    floatValue = m_value.floatValue;
}

void BlobInspectorValue::get(double& doubleValue) const
{
    checkValueType(VT_DOUBLE);
    doubleValue = m_value.doubleValue;
}

void BlobInspectorValue::get(std::string& stringValue) const
{
    checkValueType(VT_STRING);
    stringValue = m_stringValue;
}

void BlobInspectorValue::get(int8_t& enumValue, std::string& enumSymbol) const
{
    checkValueType(VT_INT8_ENUM);
    enumValue = m_value.int8Value;
    enumSymbol = m_stringValue;
}

void BlobInspectorValue::get(int16_t& enumValue, std::string& enumSymbol) const
{
    checkValueType(VT_INT16_ENUM);
    enumValue = m_value.int16Value;
    enumSymbol = m_stringValue;
}

void BlobInspectorValue::get(int32_t& enumValue, std::string& enumSymbol) const
{
    checkValueType(VT_INT32_ENUM);
    enumValue = m_value.int32Value;
    enumSymbol = m_stringValue;
}

void BlobInspectorValue::get(int64_t& enumValue, std::string& enumSymbol) const
{
    checkValueType(VT_INT64_ENUM);
    enumValue = m_value.int64Value;
    enumSymbol = m_stringValue;
}

void BlobInspectorValue::get(uint8_t& enumValue, std::string& enumSymbol) const
{
    checkValueType(VT_UINT8_ENUM);
    enumValue = m_value.uint8Value;
    enumSymbol = m_stringValue;
}

void BlobInspectorValue::get(uint16_t& enumValue, std::string& enumSymbol) const
{
    checkValueType(VT_UINT16_ENUM);
    enumValue = m_value.uint16Value;
    enumSymbol = m_stringValue;
}

void BlobInspectorValue::get(uint32_t& enumValue, std::string& enumSymbol) const
{
    checkValueType(VT_UINT32_ENUM);
    enumValue = m_value.uint32Value;
    enumSymbol = m_stringValue;
}

void BlobInspectorValue::get(uint64_t& enumValue, std::string& enumSymbol) const
{
    checkValueType(VT_UINT64_ENUM);
    enumValue = m_value.uint64Value;
    enumSymbol = m_stringValue;
}

void BlobInspectorValue::get(IBlobInspectorValueListener& listener) const
{
    switch (m_type)
    {
    case VT_BOOL:
        listener.onValue(m_value.boolValue);
        break;

    case VT_INT8:
        listener.onValue(static_cast<int64_t>(m_value.int8Value));
        break;

    case VT_INT16:
        listener.onValue(static_cast<int64_t>(m_value.int16Value));
        break;

    case VT_INT32:
        listener.onValue(static_cast<int64_t>(m_value.int32Value));
        break;

    case VT_INT64:
        listener.onValue(m_value.int64Value);
        break;

    case VT_UINT8:
        listener.onValue(static_cast<uint64_t>(m_value.uint8Value));
        break;

    case VT_UINT16:
        listener.onValue(static_cast<uint64_t>(m_value.uint16Value));
        break;

    case VT_UINT32:
        listener.onValue(static_cast<uint64_t>(m_value.uint32Value));
        break;

    case VT_UINT64:
        listener.onValue(m_value.uint64Value);
        break;

    case VT_FLOAT:
        listener.onValue(m_value.floatValue);
        break;

    case VT_DOUBLE:
        listener.onValue(m_value.doubleValue);
        break;

    case VT_STRING:
        listener.onValue(m_stringValue);
        break;

    case VT_INT8_ENUM:
        listener.onValue(static_cast<int64_t>(m_value.int8Value), m_stringValue);
        break;

    case VT_INT16_ENUM:
        listener.onValue(static_cast<int64_t>(m_value.int16Value), m_stringValue);
        break;

    case VT_INT32_ENUM:
        listener.onValue(static_cast<int64_t>(m_value.int32Value), m_stringValue);
        break;

    case VT_INT64_ENUM:
        listener.onValue(m_value.int64Value, m_stringValue);
        break;

    case VT_UINT8_ENUM:
        listener.onValue(static_cast<uint64_t>(m_value.uint8Value), m_stringValue);
        break;

    case VT_UINT16_ENUM:
        listener.onValue(static_cast<uint64_t>(m_value.uint16Value), m_stringValue);
        break;

    case VT_UINT32_ENUM:
        listener.onValue(static_cast<uint64_t>(m_value.uint32Value), m_stringValue);
        break;

    case VT_UINT64_ENUM:
        listener.onValue(m_value.uint64Value, m_stringValue);
        break;

    case VT_UNDEFINED:
    default:
        throw CppRuntimeException("Undefined type of blob inspector value");
    }
}

void BlobInspectorValue::getGeneric(int64_t& int64Value) const
{
    switch (m_type)
    {
    case VT_INT8:
        int64Value = m_value.int8Value;
        break;

    case VT_INT16:
        int64Value = m_value.int16Value;
        break;

    case VT_INT32:
        int64Value = m_value.int32Value;
        break;

    case VT_INT64:
        int64Value = m_value.int64Value;
        break;

    default:
        std::string realType(convertValueTypeToString(m_type));
        throw CppRuntimeException("Unexpected type of blob inspector value (real: " + realType +
                " != expected: Signed Integer Type");
    }
}

void BlobInspectorValue::getGeneric(uint64_t& uint64Value) const
{
    switch (m_type)
    {
    case VT_UINT8:
        uint64Value = m_value.uint8Value;
        break;

    case VT_UINT16:
        uint64Value = m_value.uint16Value;
        break;

    case VT_UINT32:
        uint64Value = m_value.uint32Value;
        break;

    case VT_UINT64:
        uint64Value = m_value.uint64Value;
        break;

    default:
        std::string realType(convertValueTypeToString(m_type));
        throw CppRuntimeException("Unexpected type of blob inspector value (real: " + realType +
                " != expected: Unsigned Integer Type");
    }
}

void BlobInspectorValue::getGeneric(int64_t& enumValue, std::string& enumSymbol) const
{
    switch (m_type)
    {
    case VT_INT8_ENUM:
        enumValue = m_value.int8Value;
        break;

    case VT_INT16_ENUM:
        enumValue = m_value.int16Value;
        break;

    case VT_INT32_ENUM:
        enumValue = m_value.int32Value;
        break;

    case VT_INT64_ENUM:
        enumValue = m_value.int64Value;
        break;

    default:
        std::string realType(convertValueTypeToString(m_type));
        throw CppRuntimeException("Unexpected type of blob inspector value (real: " + realType +
                " != expected: Signed Integer Enumeration Type");
    }

    enumSymbol = m_stringValue;
}

void BlobInspectorValue::getGeneric(uint64_t& enumValue, std::string& enumSymbol) const
{
    switch (m_type)
    {
    case VT_UINT8_ENUM:
        enumValue = m_value.uint8Value;
        break;

    case VT_UINT16_ENUM:
        enumValue = m_value.uint16Value;
        break;

    case VT_UINT32_ENUM:
        enumValue = m_value.uint32Value;
        break;

    case VT_UINT64_ENUM:
        enumValue = m_value.uint64Value;
        break;

    default:
        std::string realType(convertValueTypeToString(m_type));
        throw CppRuntimeException("Unexpected type of blob inspector value (real: " + realType +
                " != expected: Unsigned Integer Enumeration Type");
    }

    enumSymbol = m_stringValue;
}

std::string BlobInspectorValue::toString() const
{
    switch (m_type)
    {
    case VT_BOOL:
        return convertToString(m_value.boolValue);

    case VT_INT8:
        return convertToString(m_value.int8Value);

    case VT_INT16:
        return convertToString(m_value.int16Value);

    case VT_INT32:
        return convertToString(m_value.int32Value);

    case VT_INT64:
        return convertToString(m_value.int64Value);

    case VT_UINT8:
        return convertToString(m_value.uint8Value);

    case VT_UINT16:
        return convertToString(m_value.uint16Value);

    case VT_UINT32:
        return convertToString(m_value.uint32Value);

    case VT_UINT64:
        return convertToString(m_value.uint64Value);

    case VT_FLOAT:
        return convertToString(m_value.floatValue);

    case VT_DOUBLE:
        return convertToString(m_value.doubleValue);

    case VT_STRING:
        return m_stringValue;

    case VT_INT8_ENUM:
        return m_stringValue + " (" + convertToString(m_value.int8Value) + ")";

    case VT_INT16_ENUM:
        return m_stringValue + " (" + convertToString(m_value.int16Value) + ")";

    case VT_INT32_ENUM:
        return m_stringValue + " (" + convertToString(m_value.int32Value) + ")";

    case VT_INT64_ENUM:
        return m_stringValue + " (" + convertToString(m_value.int64Value) + ")";

    case VT_UINT8_ENUM:
        return m_stringValue + " (" + convertToString(m_value.uint8Value) + ")";

    case VT_UINT16_ENUM:
        return m_stringValue + " (" + convertToString(m_value.uint16Value) + ")";

    case VT_UINT32_ENUM:
        return m_stringValue + " (" + convertToString(m_value.uint32Value) + ")";

    case VT_UINT64_ENUM:
        return m_stringValue + " (" + convertToString(m_value.uint64Value) + ")";

    case VT_UNDEFINED:
    default:
        return "UNDEFINED";
    }
}

void BlobInspectorValue::checkValueType(ValueType expectedValueType) const
{
    if (m_type != expectedValueType)
    {
        std::string realType(convertValueTypeToString(m_type));
        std::string expectedType(convertValueTypeToString(expectedValueType));
        throw CppRuntimeException("Unexpected type of blob inspector value (real: " + realType +
                " != expected: " + expectedType + ")");
    }
}

const char* BlobInspectorValue::convertValueTypeToString(ValueType valueType)
{
    switch (valueType)
    {
    case VT_BOOL:
        return "VT_BOOL";

    case VT_INT8:
        return "VT_INT8";

    case VT_INT16:
        return "VT_INT16";

    case VT_INT32:
        return "VT_INT32";

    case VT_INT64:
        return "VT_INT64";

    case VT_UINT8:
        return "VT_UINT8";

    case VT_UINT16:
        return "VT_UINT16";

    case VT_UINT32:
        return "VT_UINT32";

    case VT_UINT64:
        return "VT_UINT64";

    case VT_FLOAT:
        return "VT_FLOAT";

    case VT_DOUBLE:
        return "VT_DOUBLE";

    case VT_STRING:
        return "VT_STRING";

    case VT_INT8_ENUM:
        return "VT_INT8_ENUM";

    case VT_INT16_ENUM:
        return "VT_INT16_ENUM";

    case VT_INT32_ENUM:
        return "VT_INT32_ENUM";

    case VT_INT64_ENUM:
        return "VT_INT64_ENUM";

    case VT_UINT8_ENUM:
        return "VT_UINT8_ENUM";

    case VT_UINT16_ENUM:
        return "VT_UINT16_ENUM";

    case VT_UINT32_ENUM:
        return "VT_UINT32_ENUM";

    case VT_UINT64_ENUM:
        return "VT_UINT64_ENUM";

    case VT_UNDEFINED:
    default:
        return "VT_UNDEFINED";
    }
}

} // namespace zserio
