#include <cstdio>
#include <string>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "with_inspector_code/InspectorParameterProvider.h"
#include "with_inspector_code/last_auto_optional_field/LastAutoOptionalDatabase.h"

namespace with_inspector_code
{
namespace last_auto_optional_field
{

class LastAutoOptionalFieldTest : public ::testing::Test
{
protected:
    void fillLastAutoOptionalStructure(LastAutoOptionalStructure& lastAutoOptionalStructure,
            bool hasAutoOptionalField)
    {
        lastAutoOptionalStructure.setValue(LAST_AUTO_OPTIONAL_STRUCTURE_VALUE);
        if (hasAutoOptionalField)
            lastAutoOptionalStructure.setAutoOptionalField(LAST_AUTO_OPTIONAL_STRUCTURE_AUTO_OPTIONAL_FIELD);
    }

    void convertBlobTreeToBitStream(bool hasAutoOptionalField)
    {
        LastAutoOptionalStructure lastAutoOptionalStructure;
        fillLastAutoOptionalStructure(lastAutoOptionalStructure, hasAutoOptionalField);

        zserio::BitStreamWriter writer;
        lastAutoOptionalStructure.write(writer);

        size_t writerBufferByteSize;
        const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
        zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
        InspectorParameterProvider inspectorParameterProvider;
        zserio::BlobInspectorTree tree;
        const LastAutoOptionalDatabase lastAutoOptionalDatabase;
        lastAutoOptionalDatabase.convertBitStreamToBlobTree("lastAutoOptionalTable",
                "lastAutoOptionalStructure", reader,
                inspectorParameterProvider, tree);

        zserio::BitStreamWriter treeWriter;
        lastAutoOptionalDatabase.convertBlobTreeToBitStream("lastAutoOptionalTable",
                "lastAutoOptionalStructure", tree,
                inspectorParameterProvider, treeWriter);

        size_t treeWriterBufferByteSize;
        const uint8_t* treeWriterBuffer = treeWriter.getWriteBuffer(treeWriterBufferByteSize);
        ASSERT_EQ(writerBufferByteSize, treeWriterBufferByteSize);
        for (size_t i = 0; i < treeWriterBufferByteSize; ++i)
            ASSERT_EQ(writerBuffer[i], treeWriterBuffer[i]);
    }

    static const int32_t    LAST_AUTO_OPTIONAL_STRUCTURE_VALUE;
    static const uint32_t   LAST_AUTO_OPTIONAL_STRUCTURE_AUTO_OPTIONAL_FIELD;
};

const int32_t   LastAutoOptionalFieldTest::LAST_AUTO_OPTIONAL_STRUCTURE_VALUE = 0x12345678;
const uint32_t  LastAutoOptionalFieldTest::LAST_AUTO_OPTIONAL_STRUCTURE_AUTO_OPTIONAL_FIELD = 0xDEADBEEF;

TEST_F(LastAutoOptionalFieldTest, convertBlobTreeToBitStreamWithoutOptional)
{
    convertBlobTreeToBitStream(false);
}

TEST_F(LastAutoOptionalFieldTest, convertBlobTreeToBitStreamWithOptional)
{
    convertBlobTreeToBitStream(true);
}

} // namespace last_auto_optional_field
} // namespace with_inspector_code
