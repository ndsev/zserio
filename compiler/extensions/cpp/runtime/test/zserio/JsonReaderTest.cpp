#include "gtest/gtest.h"

#include "zserio/JsonReader.h"
#include "zserio/CreatorTestObject.h"

namespace zserio
{

TEST(JsonReaderTest, readObject)
{
    std::stringstream str(
        "{\n"
        "    \"value\": 13,\n"
        "    \"nested\": {\n"
        "        \"value\": 10,\n"
        "        \"text\": \"nested\",\n"
        "        \"data\": {\n"
        "             \"buffer\": [\n"
        "                 203,\n"
        "                 240\n"
        "             ],\n"
        "             \"bitSize\": 12\n"
        "        },\n"
        "        \"dummyEnum\": 0,\n"
        "        \"dummyBitmask\": 1\n"
        "    },\n"
        "    \"text\": \"test\",\n"
        "    \"nestedArray\": [\n"
        "        {\n"
        "            \"value\": 5,\n"
        "            \"text\": \"nestedArray\",\n"
        "            \"data\": {\n"
        "                 \"buffer\": [\n"
        "                     202,\n"
        "                     254\n"
        "                 ],"
        "                 \"bitSize\": 15\n"
        "            },\n"
        "            \"dummyEnum\": 1,\n"
        "            \"dummyBitmask\": 2\n"
        "        }\n"
        "    ],\n"
        "    \"textArray\": [\n"
        "        \"this\",\n"
        "        \"is\",\n"
        "        \"text\",\n"
        "        \"array\"\n"
        "    ],\n"
        "    \"externArray\": [\n"
        "        {\n"
        "            \"buffer\": [\n"
        "                222,\n"
        "                209\n"
        "            ],"
        "            \"bitSize\": 13\n"
        "        }\n"
        "    ],\n"
        "    \"optionalBool\": null\n"
        "}"
    );

    JsonReader jsonReader(str);
    IReflectablePtr reflectable = jsonReader.read(DummyObject::typeInfo());
    ASSERT_TRUE(reflectable);

}

} // namespace zserio
