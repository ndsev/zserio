import unittest
import zserio

from testutils import getZserioApi

class ExpressionEnumTemplateArgumentConflictTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").expression_enum_template_argument_conflict

    def testReadWrite(self):
        enumTemplateArgumentConflict_Letters = self.api.EnumTemplateArgumentConflict_Letters.fromFields(False,
                                                                                                        10)
        enumTemplateArgumentConflictHolder = (self.api.EnumTemplateArgumentConflictHolder.
                                              fromFields(enumTemplateArgumentConflict_Letters))
        self.assertTrue(enumTemplateArgumentConflictHolder.getEnumTemplateArgumentConflict().
                        hasExpressionField())

        writer = zserio.BitStreamWriter()
        enumTemplateArgumentConflictHolder.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readEnumTemplateArgumentConflictHolder = self.api.EnumTemplateArgumentConflictHolder()
        readEnumTemplateArgumentConflictHolder.read(reader)
        self.assertEqual(enumTemplateArgumentConflictHolder, readEnumTemplateArgumentConflictHolder)
