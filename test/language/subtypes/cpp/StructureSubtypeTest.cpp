#include <cstdio>
#include <string>
#include <fstream>

#include "gtest/gtest.h"

#include "subtypes/structure_subtype/SubtypeStructure.h"
#include "subtypes/structure_subtype/Student.h"

#include "zserio/RebindAlloc.h"

namespace subtypes
{
namespace structure_subtype
{

using allocator_type = Student::allocator_type;
using string_type = zserio::string<allocator_type>;

TEST(StructureSubtypeTest, testSubtype)
{
    const uint32_t identifier = 0xFFFF;
    const string_type name = "Name";
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
