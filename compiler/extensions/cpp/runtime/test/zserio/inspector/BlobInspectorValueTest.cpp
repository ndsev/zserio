#include <string>

#include "zserio/CppRuntimeException.h"
#include "zserio/inspector/BlobInspectorValue.h"
#include "zserio/inspector/IBlobInspectorValueListener.h"

#include "gtest/gtest.h"

namespace zserio
{

class BlobInspectorValueListener : public IBlobInspectorValueListener
{
public:
    explicit BlobInspectorValueListener(bool expectedBool) : m_expectedValueType(EVT_BOOL),
            m_expectedValue(expectedBool)
    {
    }

    explicit BlobInspectorValueListener(int64_t expectedInt64) : m_expectedValueType(EVT_INT64),
            m_expectedValue(expectedInt64)
    {
    }

    explicit BlobInspectorValueListener(uint64_t expectedUInt64) : m_expectedValueType(EVT_UINT64),
            m_expectedValue(expectedUInt64)
    {
    }

    explicit BlobInspectorValueListener(float expectedFloat) : m_expectedValueType(EVT_FLOAT),
            m_expectedValue(expectedFloat)
    {
    }

    explicit BlobInspectorValueListener(double expectedDouble) : m_expectedValueType(EVT_DOUBLE),
            m_expectedValue(expectedDouble)
    {
    }

    explicit BlobInspectorValueListener(const std::string& expectedString) : m_expectedValueType(EVT_STRING),
            m_expectedString(expectedString)
    {
    }

    BlobInspectorValueListener(int64_t expectedEnumValue, const std::string& expectedEnumSymbol) :
            m_expectedValueType(EVT_INT64_ENUM), m_expectedValue(expectedEnumValue),
            m_expectedString(expectedEnumSymbol)
    {
    }

    BlobInspectorValueListener(uint64_t expectedEnumValue, const std::string& expectedEnumSymbol) :
            m_expectedValueType(EVT_UINT64_ENUM), m_expectedValue(expectedEnumValue),
            m_expectedString(expectedEnumSymbol)
    {
    }

    virtual void onValue(bool value)
    {
        EXPECT_EQ(EVT_BOOL, m_expectedValueType);
        EXPECT_EQ(m_expectedValue.boolValue, value);
    }

    virtual void onValue(int64_t value)
    {
        EXPECT_EQ(EVT_INT64, m_expectedValueType);
        EXPECT_EQ(m_expectedValue.int64Value, value);
    }

    virtual void onValue(uint64_t value)
    {
        EXPECT_EQ(EVT_UINT64, m_expectedValueType);
        EXPECT_EQ(m_expectedValue.uint64Value, value);
    }

    virtual void onValue(float value)
    {
        EXPECT_EQ(EVT_FLOAT, m_expectedValueType);
        EXPECT_EQ(m_expectedValue.floatValue, value);
    }

    virtual void onValue(double value)
    {
        EXPECT_EQ(EVT_DOUBLE, m_expectedValueType);
        EXPECT_EQ(m_expectedValue.doubleValue, value);
    }

    virtual void onValue(const std::string& value)
    {
        EXPECT_EQ(EVT_STRING, m_expectedValueType);
        EXPECT_EQ(m_expectedString, value);
    }

    virtual void onValue(int64_t enumValue, const std::string& enumSymbol)
    {
        EXPECT_EQ(EVT_INT64_ENUM, m_expectedValueType);
        EXPECT_EQ(m_expectedValue.int64Value, enumValue);
        EXPECT_EQ(m_expectedString, enumSymbol);
    }

    virtual void onValue(uint64_t enumValue, const std::string& enumSymbol)
    {
        EXPECT_EQ(EVT_UINT64_ENUM, m_expectedValueType);
        EXPECT_EQ(m_expectedValue.uint64Value, enumValue);
        EXPECT_EQ(m_expectedString, enumSymbol);
    }

private:
    enum ExpectedValueType
    {
        EVT_UNDEFINED,
        EVT_BOOL,
        EVT_INT64,
        EVT_UINT64,
        EVT_FLOAT,
        EVT_DOUBLE,
        EVT_STRING,
        EVT_INT64_ENUM,
        EVT_UINT64_ENUM
    };

    union ExpectedValue
    {
        ExpectedValue() {}
        explicit ExpectedValue(bool expectedBool) : boolValue(expectedBool) {}
        explicit ExpectedValue(int64_t expectedInt64) : int64Value(expectedInt64) {}
        explicit ExpectedValue(uint64_t expectedUInt64) : uint64Value(expectedUInt64) {}
        explicit ExpectedValue(float expectedFloat) : floatValue(expectedFloat) {}
        explicit ExpectedValue(double expectedDouble) : doubleValue(expectedDouble) {}

        bool        boolValue;
        int64_t     int64Value;
        uint64_t    uint64Value;
        float       floatValue;
        double      doubleValue;
    };

    ExpectedValueType   m_expectedValueType;
    ExpectedValue       m_expectedValue;
    std::string         m_expectedString;
};

TEST(BlobInspectorValueTest, BoolType)
{
    const bool boolValue = true;
    BlobInspectorValue blobValue(boolValue);

    bool readBoolValue;
    blobValue.get(readBoolValue);
    EXPECT_EQ(boolValue, readBoolValue);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(boolValue);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, Int8Type)
{
    const int8_t int8Value = -128;
    BlobInspectorValue blobValue(int8Value);

    int8_t readInt8Value;
    blobValue.get(readInt8Value);
    EXPECT_EQ(int8Value, readInt8Value);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(static_cast<int64_t>(int8Value));
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, Int16Type)
{
    const int16_t int16Value = -16384;
    BlobInspectorValue blobValue(int16Value);

    int16_t readInt16Value;
    blobValue.get(readInt16Value);
    EXPECT_EQ(int16Value, readInt16Value);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(static_cast<int64_t>(int16Value));
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, Int32Type)
{
    const int32_t int32Value = -65536;
    BlobInspectorValue blobValue(int32Value);

    int32_t readInt32Value;
    blobValue.get(readInt32Value);
    EXPECT_EQ(int32Value, readInt32Value);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(static_cast<int64_t>(int32Value));
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, Int64Type)
{
    const int64_t int64Value = -65537;
    BlobInspectorValue blobValue(int64Value);

    int64_t readInt64Value;
    blobValue.get(readInt64Value);
    EXPECT_EQ(int64Value, readInt64Value);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(int64Value);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, GenericInt64Type)
{
    const int8_t int8Value = -5;
    BlobInspectorValue blobValue(int8Value);

    int64_t readInt64Value;
    blobValue.getGeneric(readInt64Value);
    EXPECT_EQ(static_cast<int64_t>(int8Value), readInt64Value);

    uint64_t readUInt64Value;
    EXPECT_THROW(blobValue.getGeneric(readUInt64Value), zserio::CppRuntimeException);
}

TEST(BlobInspectorValueTest, UInt8Type)
{
    const uint8_t uint8Value = 255;
    BlobInspectorValue blobValue(uint8Value);

    uint8_t readUInt8Value;
    blobValue.get(readUInt8Value);
    EXPECT_EQ(uint8Value, readUInt8Value);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(static_cast<uint64_t>(uint8Value));
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, UInt16Type)
{
    const uint16_t uint16Value = 65535;
    BlobInspectorValue blobValue(uint16Value);

    uint16_t readUInt16Value;
    blobValue.get(readUInt16Value);
    EXPECT_EQ(uint16Value, readUInt16Value);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(static_cast<uint64_t>(uint16Value));
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, UInt32Type)
{
    const uint32_t uint32Value = 65536;
    BlobInspectorValue blobValue(uint32Value);

    uint32_t readUInt32Value;
    blobValue.get(readUInt32Value);
    EXPECT_EQ(uint32Value, readUInt32Value);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(static_cast<uint64_t>(uint32Value));
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, UInt64Type)
{
    const uint64_t uint64Value = 65537;
    BlobInspectorValue blobValue(uint64Value);

    uint64_t readUInt64Value;
    blobValue.get(readUInt64Value);
    EXPECT_EQ(uint64Value, readUInt64Value);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(uint64Value);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, GenericUInt64Type)
{
    const uint16_t uint16Value = 65535;
    BlobInspectorValue blobValue(uint16Value);

    uint64_t readUInt64Value;
    blobValue.getGeneric(readUInt64Value);
    EXPECT_EQ(static_cast<uint64_t>(uint16Value), readUInt64Value);

    int64_t readInt64Value;
    EXPECT_THROW(blobValue.getGeneric(readInt64Value), zserio::CppRuntimeException);
}

TEST(BlobInspectorValueTest, FloatType)
{
    const float floatValue = 3.14f;
    BlobInspectorValue blobValue(floatValue);

    float readFloatValue;
    blobValue.get(readFloatValue);
    EXPECT_EQ(floatValue, readFloatValue);

    bool readBoolValue;
    EXPECT_THROW(blobValue.get(readBoolValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(floatValue);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, DoubleType)
{
    const double doubleValue = 3.1415;
    BlobInspectorValue blobValue(doubleValue);

    double readDoubleValue;
    blobValue.get(readDoubleValue);
    EXPECT_EQ(doubleValue, readDoubleValue);

    bool readBoolValue;
    EXPECT_THROW(blobValue.get(readBoolValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(doubleValue);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, StringType)
{
    const std::string stringValue = "String";
    BlobInspectorValue blobValue(stringValue);

    std::string readStringValue;
    blobValue.get(readStringValue);
    EXPECT_EQ(stringValue, readStringValue);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(stringValue);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, Int8EnumType)
{
    const int8_t int8EnumValue = -128;
    const std::string int8EnumSymbol = "INT8_ENUM_SYMBOL";
    BlobInspectorValue blobValue(int8EnumValue, int8EnumSymbol);

    int8_t readInt8EnumValue;
    std::string readInt8EnumSymbol;
    blobValue.get(readInt8EnumValue, readInt8EnumSymbol);
    EXPECT_EQ(int8EnumValue, readInt8EnumValue);
    EXPECT_EQ(int8EnumSymbol, readInt8EnumSymbol);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(static_cast<int64_t>(int8EnumValue), int8EnumSymbol);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, Int16EnumType)
{
    const int16_t int16EnumValue = -16384;
    const std::string int16EnumSymbol = "INT16_ENUM_SYMBOL";
    BlobInspectorValue blobValue(int16EnumValue, int16EnumSymbol);

    int16_t readInt16EnumValue;
    std::string readInt16EnumSymbol;
    blobValue.get(readInt16EnumValue, readInt16EnumSymbol);
    EXPECT_EQ(int16EnumValue, readInt16EnumValue);
    EXPECT_EQ(int16EnumSymbol, readInt16EnumSymbol);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(static_cast<int64_t>(int16EnumValue), int16EnumSymbol);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, Int32EnumType)
{
    const int32_t int32EnumValue = -65536;
    const std::string int32EnumSymbol = "INT32_ENUM_SYMBOL";
    BlobInspectorValue blobValue(int32EnumValue, int32EnumSymbol);

    int32_t readInt32EnumValue;
    std::string readInt32EnumSymbol;
    blobValue.get(readInt32EnumValue, readInt32EnumSymbol);
    EXPECT_EQ(int32EnumValue, readInt32EnumValue);
    EXPECT_EQ(int32EnumSymbol, readInt32EnumSymbol);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(static_cast<int64_t>(int32EnumValue), int32EnumSymbol);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, Int64EnumType)
{
    const int64_t int64EnumValue = -65537;
    const std::string int64EnumSymbol = "INT64_ENUM_SYMBOL";
    BlobInspectorValue blobValue(int64EnumValue, int64EnumSymbol);

    int64_t readInt64EnumValue;
    std::string readInt64EnumSymbol;
    blobValue.get(readInt64EnumValue, readInt64EnumSymbol);
    EXPECT_EQ(int64EnumValue, readInt64EnumValue);
    EXPECT_EQ(int64EnumSymbol, readInt64EnumSymbol);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(int64EnumValue, int64EnumSymbol);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, GenericInt64EnumType)
{
    const int32_t int32EnumValue = -65537;
    const std::string int32EnumSymbol = "INT32_ENUM_SYMBOL";
    BlobInspectorValue blobValue(int32EnumValue, int32EnumSymbol);

    int64_t readInt64EnumValue;
    std::string readInt64EnumSymbol;
    blobValue.getGeneric(readInt64EnumValue, readInt64EnumSymbol);
    EXPECT_EQ(static_cast<int64_t>(int32EnumValue), readInt64EnumValue);
    EXPECT_EQ(int32EnumSymbol, readInt64EnumSymbol);

    uint64_t readUInt64EnumValue;
    std::string readUInt64EnumSymbol;
    EXPECT_THROW(blobValue.getGeneric(readUInt64EnumValue, readUInt64EnumSymbol),
            zserio::CppRuntimeException);
}

TEST(BlobInspectorValueTest, UInt8EnumType)
{
    const uint8_t uint8EnumValue = 255;
    const std::string uint8EnumSymbol = "UINT8_ENUM_SYMBOL";
    BlobInspectorValue blobValue(uint8EnumValue, uint8EnumSymbol);

    uint8_t readUInt8EnumValue;
    std::string readUInt8EnumSymbol;
    blobValue.get(readUInt8EnumValue, readUInt8EnumSymbol);
    EXPECT_EQ(uint8EnumValue, readUInt8EnumValue);
    EXPECT_EQ(uint8EnumSymbol, readUInt8EnumSymbol);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(static_cast<uint64_t>(uint8EnumValue), uint8EnumSymbol);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, UInt16EnumType)
{
    const uint16_t uint16EnumValue = 65535;
    const std::string uint16EnumSymbol = "UINT16_ENUM_SYMBOL";
    BlobInspectorValue blobValue(uint16EnumValue, uint16EnumSymbol);

    uint16_t readUInt16EnumValue;
    std::string readUInt16EnumSymbol;
    blobValue.get(readUInt16EnumValue, readUInt16EnumSymbol);
    EXPECT_EQ(uint16EnumValue, readUInt16EnumValue);
    EXPECT_EQ(uint16EnumSymbol, readUInt16EnumSymbol);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(static_cast<uint64_t>(uint16EnumValue), uint16EnumSymbol);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, UInt32EnumType)
{
    const uint32_t uint32EnumValue = 65536;
    const std::string uint32EnumSymbol = "UINT32_ENUM_SYMBOL";
    BlobInspectorValue blobValue(uint32EnumValue, uint32EnumSymbol);

    uint32_t readUInt32EnumValue;
    std::string readUInt32EnumSymbol;
    blobValue.get(readUInt32EnumValue, readUInt32EnumSymbol);
    EXPECT_EQ(uint32EnumValue, readUInt32EnumValue);
    EXPECT_EQ(uint32EnumSymbol, readUInt32EnumSymbol);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(static_cast<uint64_t>(uint32EnumValue), uint32EnumSymbol);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, UInt64EnumType)
{
    const uint64_t uint64EnumValue = 65537;
    const std::string uint64EnumSymbol = "UINT64_ENUM_SYMBOL";
    BlobInspectorValue blobValue(uint64EnumValue, uint64EnumSymbol);

    uint64_t readUInt64EnumValue;
    std::string readUInt64EnumSymbol;
    blobValue.get(readUInt64EnumValue, readUInt64EnumSymbol);
    EXPECT_EQ(uint64EnumValue, readUInt64EnumValue);
    EXPECT_EQ(uint64EnumSymbol, readUInt64EnumSymbol);

    float readFloatValue;
    EXPECT_THROW(blobValue.get(readFloatValue), zserio::CppRuntimeException);

    BlobInspectorValueListener valueListener(uint64EnumValue, uint64EnumSymbol);
    blobValue.get(valueListener);
}

TEST(BlobInspectorValueTest, GeneriUInt64EnumType)
{
    const uint32_t uint32EnumValue = 65537;
    const std::string uint32EnumSymbol = "UINT32_ENUM_SYMBOL";
    BlobInspectorValue blobValue(uint32EnumValue, uint32EnumSymbol);

    uint64_t readUInt64EnumValue;
    std::string readUInt64EnumSymbol;
    blobValue.getGeneric(readUInt64EnumValue, readUInt64EnumSymbol);
    EXPECT_EQ(static_cast<uint64_t>(uint32EnumValue), readUInt64EnumValue);
    EXPECT_EQ(uint32EnumSymbol, readUInt64EnumSymbol);

    int64_t readInt64EnumValue;
    std::string readInt64EnumSymbol;
    EXPECT_THROW(blobValue.getGeneric(readInt64EnumValue, readInt64EnumSymbol),
            zserio::CppRuntimeException);
}

TEST(BlobInspectorValueTest, EqualityOperator)
{
    BlobInspectorValue blobValue1(true);
    BlobInspectorValue blobValue2(false);
    EXPECT_FALSE(blobValue1 == blobValue2);

    BlobInspectorValue blobValue3(true);
    EXPECT_TRUE(blobValue1 == blobValue3);

    BlobInspectorValue blobValue4(3.14f);
    EXPECT_FALSE(blobValue1 == blobValue4);

    BlobInspectorValue blobValue5(3.1415);
    EXPECT_FALSE(blobValue1 == blobValue5);
}

TEST(BlobInspectorValueTest, ToString)
{
    BlobInspectorValue blobValue1(true);
    EXPECT_EQ("true", blobValue1.toString());

    BlobInspectorValue blobValue2(1234);
    EXPECT_EQ("1234", blobValue2.toString());

    BlobInspectorValue blobValue3(std::string("string"));
    EXPECT_EQ("string", blobValue3.toString());

    BlobInspectorValue blobValue4(3.14f);
    EXPECT_EQ("3.14", blobValue4.toString());

    BlobInspectorValue blobValue5(3.1415);
    EXPECT_EQ("3.1415", blobValue5.toString());

    BlobInspectorValue blobValue6(2, "RED");
    EXPECT_EQ("RED (2)", blobValue6.toString());
}

} // namespace zserio
