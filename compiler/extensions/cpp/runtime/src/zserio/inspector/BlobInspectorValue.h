#ifndef ZSERIO_BLOB_INSPECTOR_VALUE_H_INC
#define ZSERIO_BLOB_INSPECTOR_VALUE_H_INC

#include <string>

#include "../Types.h"

namespace zserio
{

class IBlobInspectorValueListener;

class BlobInspectorValue
{
public:
    enum ValueType
    {
        VT_UNDEFINED,
        VT_BOOL,
        VT_INT8,
        VT_INT16,
        VT_INT32,
        VT_INT64,
        VT_UINT8,
        VT_UINT16,
        VT_UINT32,
        VT_UINT64,
        VT_INT8_ENUM,
        VT_INT16_ENUM,
        VT_INT32_ENUM,
        VT_INT64_ENUM,
        VT_UINT8_ENUM,
        VT_UINT16_ENUM,
        VT_UINT32_ENUM,
        VT_UINT64_ENUM,
        VT_FLOAT,
        VT_STRING
    };

    BlobInspectorValue();
    explicit BlobInspectorValue(bool boolValue);
    explicit BlobInspectorValue(int8_t int8Value);
    explicit BlobInspectorValue(int16_t int16Value);
    explicit BlobInspectorValue(int32_t int32Value);
    explicit BlobInspectorValue(int64_t int64Value);
    explicit BlobInspectorValue(uint8_t uint8Value);
    explicit BlobInspectorValue(uint16_t uint16Value);
    explicit BlobInspectorValue(uint32_t uint32Value);
    explicit BlobInspectorValue(uint64_t uint64Value);
    explicit BlobInspectorValue(float floatValue);
    explicit BlobInspectorValue(const std::string& stringValue);
    BlobInspectorValue(int8_t enumValue, const std::string& enumSymbol);
    BlobInspectorValue(int16_t enumValue, const std::string& enumSymbol);
    BlobInspectorValue(int32_t enumValue, const std::string& enumSymbol);
    BlobInspectorValue(int64_t enumValue, const std::string& enumSymbol);
    BlobInspectorValue(uint8_t enumValue, const std::string& enumSymbol);
    BlobInspectorValue(uint16_t enumValue, const std::string& enumSymbol);
    BlobInspectorValue(uint32_t enumValue, const std::string& enumSymbol);
    BlobInspectorValue(uint64_t enumValue, const std::string& enumSymbol);

    // default destructor, copy constructor and copy assignment operator is fine

    void set(bool boolValue);
    void set(int8_t int8Value);
    void set(int16_t int16Value);
    void set(int32_t int32Value);
    void set(int64_t int64Value);
    void set(uint8_t uint8Value);
    void set(uint16_t uint16Value);
    void set(uint32_t uint32Value);
    void set(uint64_t uint64Value);
    void set(float floatValue);
    void set(const std::string& stringValue);
    void set(int8_t enumValue, const std::string& enumSymbol);
    void set(int16_t enumValue, const std::string& enumSymbol);
    void set(int32_t enumValue, const std::string& enumSymbol);
    void set(int64_t enumValue, const std::string& enumSymbol);
    void set(uint8_t enumValue, const std::string& enumSymbol);
    void set(uint16_t enumValue, const std::string& enumSymbol);
    void set(uint32_t enumValue, const std::string& enumSymbol);
    void set(uint64_t enumValue, const std::string& enumSymbol);

    bool operator==(const BlobInspectorValue& other) const;

    ValueType getType() const;

    void get(bool& boolValue) const;
    void get(int8_t& int8Value) const;
    void get(int16_t& int16Value) const;
    void get(int32_t& int32Value) const;
    void get(int64_t& int64Value) const;
    void get(uint8_t& uint8Value) const;
    void get(uint16_t& uint16Value) const;
    void get(uint32_t& uint32Value) const;
    void get(uint64_t& uint64Value) const;
    void get(float& floatValue) const;
    void get(std::string& stringValue) const;
    void get(int8_t& enumValue, std::string& enumSymbol) const;
    void get(int16_t& enumValue, std::string& enumSymbol) const;
    void get(int32_t& enumValue, std::string& enumSymbol) const;
    void get(int64_t& enumValue, std::string& enumSymbol) const;
    void get(uint8_t& enumValue, std::string& enumSymbol) const;
    void get(uint16_t& enumValue, std::string& enumSymbol) const;
    void get(uint32_t& enumValue, std::string& enumSymbol) const;
    void get(uint64_t& enumValue, std::string& enumSymbol) const;

    void get(IBlobInspectorValueListener& listener) const;

    void getGeneric(int64_t& int64Value) const;
    void getGeneric(uint64_t& uint64Value) const;
    void getGeneric(int64_t& enumValue, std::string& enumSymbol) const;
    void getGeneric(uint64_t& enumValue, std::string& enumSymbol) const;

    std::string toString() const;

private:
    union Value
    {
        bool        boolValue;
        int8_t      int8Value;
        int16_t     int16Value;
        int32_t     int32Value;
        int64_t     int64Value;
        uint8_t     uint8Value;
        uint16_t    uint16Value;
        uint32_t    uint32Value;
        uint64_t    uint64Value;
        float       floatValue;
    };

    void checkValueType(ValueType expectedValueType) const;
    static const char* convertValueTypeToString(ValueType valueType);

    ValueType   m_type;
    Value       m_value;
    std::string m_stringValue;
};

} // namespace zserio

#endif // ifndef ZSERIO_BLOB_INSPECTOR_VALUE_H_INC
