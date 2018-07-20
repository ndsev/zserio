#include <cstdio>
#include <string>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "with_inspector_code/MasterDatabase.h"
#include "with_inspector_code/InspectorParameterProvider.h"
#include "with_inspector_code/last_union_with_auto_optional/LastUnion.h"

namespace with_inspector_code
{
namespace last_union_with_auto_optional
{

class LastUnionWithAutoOptionalTest : public ::testing::Test
{
protected:
    void fillLastUnion(LastUnion& lastUnion, bool hasAutoOptionalField)
    {
        AutoOptionalStructure autoOptionalStructure;
        if (hasAutoOptionalField)
            autoOptionalStructure.setAutoOptionalField(AUTO_OPTIONAL_STRUCTURE_AUTO_OPTIONAL_FIELD);

        lastUnion.setAutoOptionalStructure(autoOptionalStructure);
    }

    void convertBlobTreeToBitStream(bool hasAutoOptionalField)
    {
        LastUnion lastUnion;
        fillLastUnion(lastUnion, hasAutoOptionalField);

        zserio::BitStreamWriter writer;
        lastUnion.write(writer);

        size_t writerBufferByteSize;
        const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
        zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
        InspectorParameterProvider inspectorParameterProvider;
        zserio::BlobInspectorTree tree;
        const MasterDatabase masterDatabase;
        masterDatabase.convertBitStreamToBlobTree("lastUnionTable", "lastUnion", reader,
                inspectorParameterProvider, tree);

        zserio::BitStreamWriter treeWriter;
        masterDatabase.convertBlobTreeToBitStream("lastUnionTable", "lastUnion", tree,
                inspectorParameterProvider, treeWriter);

        size_t treeWriterBufferByteSize;
        const uint8_t* treeWriterBuffer = treeWriter.getWriteBuffer(treeWriterBufferByteSize);
        ASSERT_EQ(writerBufferByteSize, treeWriterBufferByteSize);
        for (size_t i = 0; i < treeWriterBufferByteSize; ++i)
            ASSERT_EQ(writerBuffer[i], treeWriterBuffer[i]);
    }

    static const uint32_t   AUTO_OPTIONAL_STRUCTURE_AUTO_OPTIONAL_FIELD;
};

const uint32_t  LastUnionWithAutoOptionalTest::AUTO_OPTIONAL_STRUCTURE_AUTO_OPTIONAL_FIELD = 0xDEADBEEF;

TEST_F(LastUnionWithAutoOptionalTest, convertBlobTreeToBitStreamWithoutOptional)
{
    convertBlobTreeToBitStream(false);
}

TEST_F(LastUnionWithAutoOptionalTest, convertBlobTreeToBitStreamWithOptional)
{
    convertBlobTreeToBitStream(true);
}

} // namespace last_union_with_auto_optional
} // namespace with_inspector_code
