import unittest

from testutils import getZserioApi

class ParamStructureSubtypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "subtypes.zs").param_structure_subtype

    def testSubtype(self):
        # just check that ParameterizedSubtype is defined and that it's same as the ParameterizedStruct
        parameterizedSubtypeStruct = self.api.ParameterizedSubtypeStruct()
        length = 2
        parameterizedSubtypeStruct.setLength(length)

        parameterizedSubtype = self.api.ParameterizedSubtype(length, list(range(length)))
        parameterizedSubtypeStruct.setParameterizedSubtype(parameterizedSubtype)
        self.assertEqual(parameterizedSubtype, parameterizedSubtypeStruct.getParameterizedSubtype())

        parameterizedSubtypeStruct.setParameterizedSubtypeArray([parameterizedSubtype for i in range(length)])
        for i in range(length):
            self.assertEqual(parameterizedSubtype, parameterizedSubtypeStruct.getParameterizedSubtypeArray()[i])
