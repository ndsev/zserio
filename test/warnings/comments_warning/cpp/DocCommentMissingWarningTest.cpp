#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "test_utils/ZserioErrorOutput.h"

class DocCommentMissingWarningTest : public ::testing::Test
{
protected:
    DocCommentMissingWarningTest()
    :   zserioWarnings("warnings/comments_warning", "zserio_log_doc_comment_missing.txt")
    {}

    const test_utils::ZserioErrorOutput zserioWarnings;
};

TEST_F(DocCommentMissingWarningTest, compatibilityVersion)
{
    ASSERT_TRUE(zserioWarnings.isPresent("doc_comment_missing_warning.zs:3:30: "
            "Missing documentation comment for compatibility version."));
}

TEST_F(DocCommentMissingWarningTest, package)
{
    ASSERT_TRUE(zserioWarnings.isPresent("doc_comment_missing_warning.zs:5:9: "
            "Missing documentation comment for package."));
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:1:9: "
            "Missing documentation comment for package."));
}

TEST_F(DocCommentMissingWarningTest, import)
{
    ASSERT_TRUE(zserioWarnings.isPresent("doc_comment_missing_warning.zs:7:8: "
            "Missing documentation comment for import."));
}

TEST_F(DocCommentMissingWarningTest, constant)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:13:14: "
            "Missing documentation comment for constant 'CONSTANT'."));
}

TEST_F(DocCommentMissingWarningTest, subtype)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:23:16: "
            "Missing documentation comment for subtype 'Subtype'."));
}

TEST_F(DocCommentMissingWarningTest, instantiateType)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:20:35: "
            "Missing documentation comment for instantiate type 'StructureTypeU32'."));
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:21:35: "
            "Missing documentation comment for instantiate type 'StructureTypeSTR'."));
}

TEST_F(DocCommentMissingWarningTest, bitmaskType)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:3:15: "
            "Missing documentation comment for bitmask 'BitmaskType'."));
}

TEST_F(DocCommentMissingWarningTest, bitmaskValue)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:5:5: "
            "Missing documentation comment for bitmask value 'BITMASK_VALUE'."));
}

TEST_F(DocCommentMissingWarningTest, enumType)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:8:12: "
            "Missing documentation comment for enumeration 'EnumType'."));
}

TEST_F(DocCommentMissingWarningTest, enumItem)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:10:5: "
            "Missing documentation comment for enum item 'ENUM_ITEM'."));
}

TEST_F(DocCommentMissingWarningTest, structureType)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:15:8: "
            "Missing documentation comment for structure 'StructureType'."));
}

TEST_F(DocCommentMissingWarningTest, choiceType)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:25:8: "
            "Missing documentation comment for choice 'ChoiceType'."));
}

TEST_F(DocCommentMissingWarningTest, unionType)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:38:7: "
            "Missing documentation comment for union 'UnionType'."));
}

TEST_F(DocCommentMissingWarningTest, field)
{
    // structure field
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:17:7: "
            "Missing documentation comment for field 'field'."));

    // choice field
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:28:16: "
            "Missing documentation comment for field 'field'."));

    // union field
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:40:12: "
            "Missing documentation comment for field 'fieldU32'."));
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:41:12: "
            "Missing documentation comment for field 'fieldSTR'."));

    // sql table field
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:46:12: "
            "Missing documentation comment for field 'id'."));

    // sql database field
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:51:18: "
            "Missing documentation comment for field 'sqlTable'."));
}

TEST_F(DocCommentMissingWarningTest, function)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:32:21: "
            "Missing documentation comment for function 'getField'."));
}

TEST_F(DocCommentMissingWarningTest, choiceCaseExpression)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:27:5: "
            "Missing documentation comment for choice case expression."));
}

TEST_F(DocCommentMissingWarningTest, choiceDefault)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:29:5: "
            "Missing documentation comment for choice default."));
}

TEST_F(DocCommentMissingWarningTest, sqlTable)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:44:11: "
            "Missing documentation comment for SQL table 'SqlTableType'."));
}

TEST_F(DocCommentMissingWarningTest, sqlDatabase)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:49:14: "
            "Missing documentation comment for SQL database 'SqlDatabaseType'."));
}

TEST_F(DocCommentMissingWarningTest, ruleGroup)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:54:12: "
            "Missing documentation comment for rule group 'Rules'."));
}

TEST_F(DocCommentMissingWarningTest, rule)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:56:10: "
            "Missing documentation comment for rule 'test-rule'."));
}

TEST_F(DocCommentMissingWarningTest, serviceType)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:59:9: "
            "Missing documentation comment for service 'ServiceType'."));
}

TEST_F(DocCommentMissingWarningTest, serviceMethod)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:61:22: "
            "Missing documentation comment for method 'serviceMethod'."));
}

TEST_F(DocCommentMissingWarningTest, pubsubType)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:64:8: "
            "Missing documentation comment for pubsub 'PubsubType'."));
}

TEST_F(DocCommentMissingWarningTest, pubsubMessage)
{
    ASSERT_TRUE(zserioWarnings.isPresent("all_nodes.zs:66:46: "
            "Missing documentation comment for message 'pubsubMessage'."));
}
