import unittest

from testutils import getZserioApi, assertWarningsPresent

class CommentsWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        cls.api = getZserioApi(__file__, "doc_comment_missing_warning.zs",
                               extraArgs=["-withWarnings", "doc-comment-missing"],
                               expectedWarnings=32, errorOutputDict=cls.warnings)


    def testCompatibilityVersion(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "doc_comment_missing_warning.zs:3:30: "
                "Missing documentation comment for compatibility version."
            ]
        )

    def testPackage(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "doc_comment_missing_warning.zs:5:9: "
                "Missing documentation comment for package."
            ]
        )

        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:1:9: "
                "Missing documentation comment for package."
            ]
        )

    def testImport(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "doc_comment_missing_warning.zs:7:8: "
                "Missing documentation comment for import."
            ]
        )

    def testConstant(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:13:14: "
                "Missing documentation comment for constant 'CONSTANT'."
            ]
        )

    def testSubtype(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:23:16: "
                "Missing documentation comment for subtype 'Subtype'."
            ]
        )

    def testInstantiateType(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:20:35: "
                "Missing documentation comment for instantiate type 'StructureTypeU32'."
            ]
        )

        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:21:35: "
                "Missing documentation comment for instantiate type 'StructureTypeSTR'."
            ]
        )

    def testBitmaskType(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:3:15: "
                "Missing documentation comment for bitmask 'BitmaskType'."
            ]
        )

    def testBitmaskValue(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:5:5: "
                "Missing documentation comment for bitmask value 'BITMASK_VALUE'."
            ]
        )

    def testEnumType(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:8:12: "
                "Missing documentation comment for enumeration 'EnumType'."
            ]
        )

    def testEnumItem(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:10:5: "
                "Missing documentation comment for enum item 'ENUM_ITEM'."
            ]
        )

    def testStructureType(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:15:8: "
                "Missing documentation comment for structure 'StructureType'."
            ]
        )

    def testChoiceType(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:25:8: "
                "Missing documentation comment for choice 'ChoiceType'."
            ]
        )

    def testUnionType(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:38:7: "
                "Missing documentation comment for union 'UnionType'."
            ]
        )

    def testField(self):
        # structure field
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:17:7: "
                "Missing documentation comment for field 'field'."
            ]
        )

        # choice field
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:28:16: "
                "Missing documentation comment for field 'field'."
            ]
        )

        # union field
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:40:12: "
                "Missing documentation comment for field 'fieldU32'."
            ]
        )
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:41:12: "
                "Missing documentation comment for field 'fieldSTR'."
            ]
        )

        # sql table field
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:46:12: "
                "Missing documentation comment for field 'id'."
            ]
        )

        # sql database field
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:51:18: "
                "Missing documentation comment for field 'sqlTable'."
            ]
        )

    def testFunction(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:32:21: "
                "Missing documentation comment for function 'getField'."
            ]
        )

    def testChoiceCaseExpression(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:27:5: "
                "Missing documentation comment for choice case expression."
            ]
        )

    def testChoiceDefault(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:29:5: "
                "Missing documentation comment for choice default."
            ]
        )

    def testSqlTable(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:44:11: "
                "Missing documentation comment for SQL table 'SqlTableType'."
            ]
        )

    def testSqlDatabase(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:49:14: "
                "Missing documentation comment for SQL database 'SqlDatabaseType'."
            ]
        )

    def testRuleGroup(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:54:12: "
                "Missing documentation comment for rule group 'Rules'."
            ]
        )

    def testRule(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:56:10: "
                "Missing documentation comment for rule 'test-rule'."
            ]
        )

    def testServiceType(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:59:9: "
                "Missing documentation comment for service 'ServiceType'."
            ]
        )

    def testServiceMethod(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:61:22: "
                "Missing documentation comment for method 'serviceMethod'."
            ]
        )

    def testPubsubType(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:64:8: "
                "Missing documentation comment for pubsub 'PubsubType'."
            ]
        )

    def testPubsubMessage(self):
        assertWarningsPresent(self,
            "doc_comment_missing_warning.zs",
            [
                "all_nodes.zs:66:46: "
                "Missing documentation comment for message 'pubsubMessage'."
            ]
        )
