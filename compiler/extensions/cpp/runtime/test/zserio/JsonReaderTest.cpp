#include "gtest/gtest.h"

#include "zserio/JsonReader.h"

namespace zserio
{

TEST(JsonReaderTest, test)
{
    std::stringstream str("");
    JsonReader jsonReader(str);
}

} // namespace zserio
