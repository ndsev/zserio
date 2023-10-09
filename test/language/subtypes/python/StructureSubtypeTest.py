import Subtypes

class StructureSubtypeTest(Subtypes.TestCase):
    def testSubtype(self):
        identifier = 0xFFFF
        name = "Name"
        student = self.api.Student()
        student.identifier = identifier
        student.name = name

        subtypeStructure = self.api.SubtypeStructure()
        subtypeStructure.student = student
        readStudent = subtypeStructure.student

        self.assertEqual(student, readStudent)
