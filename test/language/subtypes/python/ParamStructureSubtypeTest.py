import Subtypes


class ParamStructureSubtypeTest(Subtypes.TestCase):
    def testSubtype(self):
        # just check that ParameterizedSubtype is defined and that it's same as the ParameterizedStruct
        parameterizedSubtypeStruct = self.api.ParameterizedSubtypeStruct()
        length = 2
        parameterizedSubtypeStruct.length = length

        parameterizedSubtype = self.api.ParameterizedSubtype(length, list(range(length)))
        parameterizedSubtypeStruct.parameterized_subtype = parameterizedSubtype
        self.assertEqual(parameterizedSubtype, parameterizedSubtypeStruct.parameterized_subtype)

        parameterizedSubtypeStruct.another_parameterized_subtype_array = [
            parameterizedSubtype for i in range(length)
        ]
        for i in range(length):
            self.assertEqual(
                parameterizedSubtype, parameterizedSubtypeStruct.another_parameterized_subtype_array[i]
            )
