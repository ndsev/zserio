import zserio

import ParameterizedTypes


class ParameterizedNestedInArrayTest(ParameterizedTypes.TestCase):
    def testWriteRead(self):
        holder = self.api.Holder(
            [self.api.Element(self.api.Parameterized(5, 6))], [self.api.Element(self.api.Parameterized(5, 6))]
        )
        bitBuffer = zserio.serialize(holder)
        readHolder = zserio.deserialize(self.api.Holder, bitBuffer)
        self.assertEqual(holder, readHolder)

    def testParameterCheckExceptionInArray(self):
        holder = self.api.Holder([self.api.Element(self.api.Parameterized(6, 7))], [])
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            holder.write(writer)

    def testParameterCheckExceptionInPackedArray(self):
        holder = self.api.Holder([], [self.api.Element(self.api.Parameterized(6, 7))])
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            holder.write(writer)
