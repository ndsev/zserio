import zserio

import ChoiceTypes

class ConstantInChoiceCaseTest(ChoiceTypes.TestCase):
    def testWriteRead(self):
        constantInChoiceCase = self.api.ConstantInChoiceCase(self.api.UINT8_CONST)
        constantInChoiceCase.const_case = 42

        bitBuffer = zserio.serialize(constantInChoiceCase)

        readConstantInChoiceCase = zserio.deserialize(
            self.api.ConstantInChoiceCase, bitBuffer, self.api.UINT8_CONST)
        self.assertEqual(constantInChoiceCase, readConstantInChoiceCase)
