#include <cstdio>
#include <string>
#include <fstream>

#include "gtest/gtest.h"

#include "subtypes/structure_subtype/SubtypeStructure.h"
#include "subtypes/structure_subtype/Student.h"

namespace subtypes
{
namespace structure_subtype
{

TEST(StructureSubtypeTest, TestSubtype)
{
    const uint32_t identifier = 0xFFFF;
    const std::string name = "Name";
    Student student;
    student.setIdentifier(identifier);
    student.setName(name);

    SubtypeStructure subtypeStructure;
    subtypeStructure.setStudent(student);
    const Student readStudent = subtypeStructure.getStudent();

    ASSERT_EQ(student, readStudent);
}

} // namespace structure_subtype
} // namespace subtypes
