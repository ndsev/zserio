#include "gtest/gtest.h"

#include <sstream>
#include "zserio/JsonParser.h"

namespace zserio
{

namespace
{

class DummyObserver : public JsonParser::IObserver
{
public:
    const std::vector<std::string>& getReport() const
    {
        return m_report;
    }

    virtual void beginObject() override
    {
        m_report.push_back("beginObject");
    }

    virtual void endObject() override
    {
        m_report.push_back("endObject");
    }

    virtual void beginArray() override
    {
        m_report.push_back("beginArray");
    }

    virtual void endArray() override
    {
        m_report.push_back("endArray");
    }

    virtual void visitKey(StringView stringValue) override
    {
        m_report.push_back("visitKey: " + toString(stringValue));
    }

    virtual void visitValue(std::nullptr_t) override
    {
        m_report.push_back("visitValue: null");
    }

    virtual void visitValue(bool boolValue) override
    {
        m_report.push_back("visitValue: " + toString(boolValue));
    }

    virtual void visitValue(int64_t intValue) override
    {
        m_report.push_back("visitValue: " + toString(intValue));
    }

    virtual void visitValue(uint64_t uintValue) override
    {
        m_report.push_back("visitValue: " + toString(uintValue));
    }

    virtual void visitValue(double doubleValue) override
    {
        m_report.push_back("visitValue: " + std::to_string(doubleValue));
    }

    virtual void visitValue(StringView stringValue) override
    {
        m_report.push_back("visitValue: " + toString(stringValue));
    }

private:
    std::vector<std::string> m_report;
};

void assertStartsWith(const std::string& expectedStart, const char* cstr)
{
    std::string str(cstr);
    if (str.size() > expectedStart.size())
        str.resize(expectedStart.size());
    ASSERT_EQ(expectedStart, str);
}

} // namespace

TEST(JsonParserTest, empty)
{
    std::stringstream str("");
    DummyObserver observer;
    JsonParser jsonParser(str, observer);
    ASSERT_TRUE(jsonParser.parse());
    ASSERT_EQ(0, observer.getReport().size());
}

TEST(JsonParserTest, oneString)
{
    std::stringstream str("\"text\"");
    DummyObserver observer;
    JsonParser jsonParser(str, observer);
    ASSERT_TRUE(jsonParser.parse());
    ASSERT_EQ(std::vector<std::string>{{"visitValue: text"}}, observer.getReport());
}

TEST(JsonParserTest, twoStrings)
{
    std::stringstream str("\"text\"\"second\"");
    DummyObserver observer;
    JsonParser jsonParser(str, observer);
    ASSERT_FALSE(jsonParser.parse());
    std::vector<std::string> expectedReport = {{"visitValue: text"}};
    ASSERT_EQ(expectedReport, observer.getReport());
    ASSERT_TRUE(jsonParser.parse());
    expectedReport.push_back("visitValue: second");
    ASSERT_EQ(expectedReport, observer.getReport());
}

TEST(JsonParserTest, parse)
{
    std::stringstream str("{\"array\":\n[\n{\"key1\":\n10, \"key2\":\n\"text\"}, {}]}");
    DummyObserver observer;
    JsonParser jsonParser(str, observer);
    ASSERT_TRUE(jsonParser.parse());

    std::vector<std::string> expectedReport = {{
        {"beginObject"},
        {"visitKey: array"},
        {"beginArray"},
        {"beginObject"},
        {"visitKey: key1"},
        {"visitValue: 10"},
        {"visitKey: key2"},
        {"visitValue: text"},
        {"endObject"},
        {"beginObject"},
        {"endObject"},
        {"endArray"},
        {"endObject"}
    }};
    ASSERT_EQ(expectedReport, observer.getReport());
}

TEST(JsonParserTest, valueTypes)
{
    std::stringstream str("null true 10 -10 1.1 \"str\"");
    DummyObserver observer;
    JsonParser jsonParser(str, observer);
    while (!jsonParser.parse())
    {}

    std::vector<std::string> expectedReport = {{
        {"visitValue: null"},
        {"visitValue: true"},
        {"visitValue: 10"},
        {"visitValue: -10"},
        {"visitValue: 1.100000"},
        {"visitValue: str"}
    }};
    ASSERT_EQ(expectedReport, observer.getReport());
}

TEST(JsonParserTest, unexpectedObject)
{
    std::stringstream str("{\n\n{\n\n");
    DummyObserver observer;
    JsonParser jsonParser(str, observer);
    ASSERT_THROW({
        try
        {
            jsonParser.parse();
        }
        catch (const JsonParserException& e)
        {
            assertStartsWith("JsonParser line 3:", e.what());
            throw;
        }
    }, JsonParserException);

    std::vector<std::string> expectedReport = {{
        "beginObject"
    }};
    ASSERT_EQ(expectedReport, observer.getReport());
}

TEST(JsonParserTest, unexpectedObjectAfterItemSeparator)
{
    std::stringstream str("{\n  \"key\": 10,\n  {\n");
    DummyObserver observer;
    JsonParser jsonParser(str, observer);
    ASSERT_THROW({
        try
        {
            jsonParser.parse();
        }
        catch (const JsonParserException& e)
        {
            assertStartsWith("JsonParser line 3:", e.what());
            throw;
        }
    }, JsonParserException);

    std::vector<std::string> expectedReport = {{
        {"beginObject"},
        {"visitKey: key"},
        {"visitValue: 10"}
    }};
    ASSERT_EQ(expectedReport, observer.getReport());
}

TEST(JsonParserTest, missingObjectItemSeparator)
{
    std::stringstream str("{\n\"item1\":\"text\"\n\"item2\":\"text\"\n}");
    DummyObserver observer;
    JsonParser jsonParser(str, observer);
    ASSERT_THROW({
        try
        {
            jsonParser.parse();
        }
        catch (const JsonParserException& e)
        {
            assertStartsWith("JsonParser line 3:", e.what());
            throw;
        }
    }, JsonParserException);

    std::vector<std::string> expectedReport = {{
        {"beginObject"},
        {"visitKey: item1"},
        {"visitValue: text"}
    }};
    ASSERT_EQ(expectedReport, observer.getReport());
}

TEST(JsonParserTest, wrongKeyType)
{
    std::stringstream str("{\n10:\"text\"\n}");
    DummyObserver observer;
    JsonParser jsonParser(str, observer);
    ASSERT_THROW({
        try
        {
            jsonParser.parse();
        }
        catch (const JsonParserException& e)
        {
            assertStartsWith("JsonParser line 2:", e.what());
            throw;
        }
    }, JsonParserException);

    std::vector<std::string> expectedReport = {{
        "beginObject"
    }};
    ASSERT_EQ(expectedReport, observer.getReport());
}

TEST(JsonParserTest, unexpectedElementToken)
{
    std::stringstream str("{\n\"item\":}");
    DummyObserver observer;
    JsonParser jsonParser(str, observer);
    ASSERT_THROW({
        try
        {
            jsonParser.parse();
        }
        catch (const JsonParserException& e)
        {
            assertStartsWith("JsonParser line 2:", e.what());
            throw;
        }
    }, JsonParserException);

    std::vector<std::string> expectedReport = {{
        {"beginObject"},
        {"visitKey: item"}
    }};
    ASSERT_EQ(expectedReport, observer.getReport());
}

TEST(JsonParserTest, missingArrayElementSeparator)
{
    std::stringstream str("{\n\"array\":\n[10\n20\n]}");
    DummyObserver observer;
    JsonParser jsonParser(str, observer);
    ASSERT_THROW({
        try
        {
            jsonParser.parse();
        }
        catch (const JsonParserException& e)
        {
            assertStartsWith("JsonParser line 4:", e.what());
            throw;
        }
    }, JsonParserException);

    std::vector<std::string> expectedReport = {{
        {"beginObject"},
        {"visitKey: array"},
        {"beginArray"},
        {"visitValue: 10"}
    }};
    ASSERT_EQ(expectedReport, observer.getReport());
}

TEST(JsonParserTest, unknownToken)
{
    std::stringstream str("\\\n");
    DummyObserver observer;
    JsonParser jsonParser(str, observer);
    ASSERT_THROW({
        try
        {
            jsonParser.parse();
        }
        catch (const JsonParserException& e)
        {
            assertStartsWith("JsonParser line 1:", e.what());
            throw;
        }
    }, JsonParserException);

    ASSERT_TRUE(observer.getReport().empty());
}

} // namespace zserio
