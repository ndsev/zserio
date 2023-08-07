# Zserio C++ runtime library test.
#
# This CMake file defines an executable which contains the zserio C++ runtime tests.
#
# This CMake file is NOT designed to be included directly without any further dependencies.
#

cmake_minimum_required(VERSION 3.6.0)

project(ZserioCppRuntimeTest)

# add gtest library
include(gtest_utils)
gtest_add_library("${ZSERIO_PROJECT_ROOT}/3rdparty/cpp/googletest")

# add SQLite3 library
include(sqlite_utils)
sqlite_add_library(${ZSERIO_PROJECT_ROOT})

compiler_set_warnings()
compiler_set_warnings_as_errors()
if (SANITIZERS_ENABLED)
    compiler_set_undefined_sanitizer()
endif ()

set(TEST_OBJECT_SRCS
    test_object/polymorphic_allocator/ArrayBitmask.cpp
    test_object/polymorphic_allocator/ArrayBitmask.h
    test_object/polymorphic_allocator/ArrayEnum.cpp
    test_object/polymorphic_allocator/ArrayEnum.h
    test_object/polymorphic_allocator/ArrayObject.cpp
    test_object/polymorphic_allocator/ArrayObject.h
    test_object/polymorphic_allocator/CreatorBitmask.cpp
    test_object/polymorphic_allocator/CreatorBitmask.h
    test_object/polymorphic_allocator/CreatorEnum.cpp
    test_object/polymorphic_allocator/CreatorEnum.h
    test_object/polymorphic_allocator/CreatorNested.cpp
    test_object/polymorphic_allocator/CreatorNested.h
    test_object/polymorphic_allocator/CreatorObject.cpp
    test_object/polymorphic_allocator/CreatorObject.h
    test_object/polymorphic_allocator/CreatorUnsignedEnum.cpp
    test_object/polymorphic_allocator/CreatorUnsignedEnum.h
    test_object/polymorphic_allocator/DebugStringObject.cpp
    test_object/polymorphic_allocator/DebugStringObject.h
    test_object/polymorphic_allocator/DebugStringParamObject.cpp
    test_object/polymorphic_allocator/DebugStringParamObject.h
    test_object/polymorphic_allocator/ReflectableBitmask.cpp
    test_object/polymorphic_allocator/ReflectableBitmask.h
    test_object/polymorphic_allocator/ReflectableEnum.cpp
    test_object/polymorphic_allocator/ReflectableEnum.h
    test_object/polymorphic_allocator/ReflectableNested.cpp
    test_object/polymorphic_allocator/ReflectableNested.h
    test_object/polymorphic_allocator/ReflectableObject.cpp
    test_object/polymorphic_allocator/ReflectableObject.h
    test_object/polymorphic_allocator/ReflectableUtilBitmask.cpp
    test_object/polymorphic_allocator/ReflectableUtilBitmask.h
    test_object/polymorphic_allocator/ReflectableUtilEnum.cpp
    test_object/polymorphic_allocator/ReflectableUtilEnum.h
    test_object/polymorphic_allocator/ReflectableUtilChoice.cpp
    test_object/polymorphic_allocator/ReflectableUtilChoice.h
    test_object/polymorphic_allocator/ReflectableUtilObject.cpp
    test_object/polymorphic_allocator/ReflectableUtilObject.h
    test_object/polymorphic_allocator/ReflectableUtilUnion.cpp
    test_object/polymorphic_allocator/ReflectableUtilUnion.h
    test_object/polymorphic_allocator/SerializeEnum.cpp
    test_object/polymorphic_allocator/SerializeEnum.h
    test_object/polymorphic_allocator/SerializeNested.cpp
    test_object/polymorphic_allocator/SerializeNested.h
    test_object/polymorphic_allocator/SerializeObject.cpp
    test_object/polymorphic_allocator/SerializeObject.h
    test_object/polymorphic_allocator/WalkerBitmask.cpp
    test_object/polymorphic_allocator/WalkerBitmask.h
    test_object/polymorphic_allocator/WalkerChoice.cpp
    test_object/polymorphic_allocator/WalkerChoice.h
    test_object/polymorphic_allocator/WalkerNested.cpp
    test_object/polymorphic_allocator/WalkerNested.h
    test_object/polymorphic_allocator/WalkerObject.cpp
    test_object/polymorphic_allocator/WalkerObject.h
    test_object/polymorphic_allocator/WalkerUnion.cpp
    test_object/polymorphic_allocator/WalkerUnion.h

    test_object/std_allocator/ArrayBitmask.cpp
    test_object/std_allocator/ArrayBitmask.h
    test_object/std_allocator/ArrayEnum.cpp
    test_object/std_allocator/ArrayEnum.h
    test_object/std_allocator/ArrayObject.cpp
    test_object/std_allocator/ArrayObject.h
    test_object/std_allocator/CreatorBitmask.cpp
    test_object/std_allocator/CreatorBitmask.h
    test_object/std_allocator/CreatorEnum.cpp
    test_object/std_allocator/CreatorEnum.h
    test_object/std_allocator/CreatorNested.cpp
    test_object/std_allocator/CreatorNested.h
    test_object/std_allocator/CreatorObject.cpp
    test_object/std_allocator/CreatorObject.h
    test_object/std_allocator/CreatorUnsignedEnum.cpp
    test_object/std_allocator/CreatorUnsignedEnum.h
    test_object/std_allocator/DebugStringObject.cpp
    test_object/std_allocator/DebugStringObject.h
    test_object/std_allocator/DebugStringParamObject.cpp
    test_object/std_allocator/DebugStringParamObject.h
    test_object/std_allocator/ReflectableBitmask.cpp
    test_object/std_allocator/ReflectableBitmask.h
    test_object/std_allocator/ReflectableEnum.cpp
    test_object/std_allocator/ReflectableEnum.h
    test_object/std_allocator/ReflectableNested.cpp
    test_object/std_allocator/ReflectableNested.h
    test_object/std_allocator/ReflectableObject.cpp
    test_object/std_allocator/ReflectableObject.h
    test_object/std_allocator/ReflectableUtilBitmask.cpp
    test_object/std_allocator/ReflectableUtilBitmask.h
    test_object/std_allocator/ReflectableUtilEnum.cpp
    test_object/std_allocator/ReflectableUtilEnum.h
    test_object/std_allocator/ReflectableUtilChoice.cpp
    test_object/std_allocator/ReflectableUtilChoice.h
    test_object/std_allocator/ReflectableUtilObject.cpp
    test_object/std_allocator/ReflectableUtilObject.h
    test_object/std_allocator/ReflectableUtilUnion.cpp
    test_object/std_allocator/ReflectableUtilUnion.h
    test_object/std_allocator/SerializeEnum.cpp
    test_object/std_allocator/SerializeEnum.h
    test_object/std_allocator/SerializeNested.cpp
    test_object/std_allocator/SerializeNested.h
    test_object/std_allocator/SerializeObject.cpp
    test_object/std_allocator/SerializeObject.h
    test_object/std_allocator/WalkerBitmask.cpp
    test_object/std_allocator/WalkerBitmask.h
    test_object/std_allocator/WalkerChoice.cpp
    test_object/std_allocator/WalkerChoice.h
    test_object/std_allocator/WalkerNested.cpp
    test_object/std_allocator/WalkerNested.h
    test_object/std_allocator/WalkerObject.cpp
    test_object/std_allocator/WalkerObject.h
    test_object/std_allocator/WalkerUnion.cpp
    test_object/std_allocator/WalkerUnion.h
)

set(ZSERIO_CPP_RUNTIME_TEST_SRCS
    zserio/AllocatorHolderTest.cpp
    zserio/AllocatorPropagatingCopyTest.cpp
    zserio/AnyHolderTest.cpp
    zserio/ArrayTest.cpp
    zserio/BitBufferTest.cpp
    zserio/BitFieldUtilTest.cpp
    zserio/BitPositionUtilTest.cpp
    zserio/BitSizeOfCalculatorTest.cpp
    zserio/BitStreamReaderTest.cpp
    zserio/BitStreamTest.cpp
    zserio/BitStreamWriterTest.cpp
    zserio/BuiltInOperatorsTest.cpp
    zserio/ConstraintExceptionTest.cpp
    zserio/CppRuntimeExceptionTest.cpp
    zserio/DebugStringUtilTest.cpp
    zserio/EnumsTest.cpp
    zserio/FloatUtilTest.cpp
    zserio/HashCodeUtilTest.cpp
    zserio/HeapOptionalHolderTest.cpp
    zserio/InplaceOptionalHolderTest.cpp
    zserio/JsonEncoderTest.cpp
    zserio/JsonDecoderTest.cpp
    zserio/JsonParserTest.cpp
    zserio/JsonReaderTest.cpp
    zserio/JsonTokenizerTest.cpp
    zserio/JsonWriterTest.cpp
    zserio/FileUtilTest.cpp
    zserio/MemoryResourceTest.cpp
    zserio/NewDeleteResourceTest.cpp
    zserio/PolymorphicAllocatorTest.cpp
    zserio/PubsubExceptionTest.cpp
    zserio/ReflectableTest.cpp
    zserio/ReflectableUtilTest.cpp
    zserio/SerializeUtilTest.cpp
    zserio/SpanTest.cpp
    zserio/ServiceExceptionTest.cpp
    zserio/SqliteConnectionTest.cpp
    zserio/StringConvertUtilTest.cpp
    zserio/StringViewTest.cpp
    zserio/TraitsTest.cpp
    zserio/TypeInfoTest.cpp
    zserio/TypeInfoUtilTest.cpp
    zserio/UniquePtrTest.cpp
    zserio/ValidationSqliteUtilTest.cpp
    zserio/SizeConvertUtilTest.cpp
    zserio/WalkerTest.cpp
    zserio/ZserioTreeCreatorTest.cpp
)

set(ZSERIO_CPP_RUNTIME_TEST_HEADERS
    zserio/TrackingAllocator.h
)

if (MSVC)
    set_property(SOURCE zserio/ReflectableTest.cpp zserio/ArrayTest.cpp APPEND PROPERTY COMPILE_OPTIONS /bigobj)
endif ()

if (CMAKE_SIZEOF_VOID_P EQUAL 8)
    # allows bit stream reader and var size util optimizations for 64bit platforms
    set_property(SOURCE zserio/SizeConvertUtilTest.cpp
                 APPEND PROPERTY COMPILE_DEFINITIONS ZSERIO_RUNTIME_64BIT)
endif ()

add_executable(${PROJECT_NAME}
    ${TEST_OBJECT_SRCS}
    ${ZSERIO_CPP_RUNTIME_TEST_SRCS}
    ${ZSERIO_CPP_RUNTIME_TEST_HEADERS})

set_target_properties(${PROJECT_NAME} PROPERTIES CXX_STANDARD 11 CXX_STANDARD_REQUIRED YES CXX_EXTENSIONS NO)
target_include_directories(${PROJECT_NAME} PRIVATE .)
target_include_directories(${PROJECT_NAME} PRIVATE ../src)
target_include_directories(${PROJECT_NAME} SYSTEM PRIVATE ${SQLITE_INCDIR})
target_link_libraries(${PROJECT_NAME} PRIVATE ZserioCppRuntime gtest_main ${SQLITE_LIBRARY})

gtest_add_tests(${PROJECT_NAME} "--gtest_output=xml" ${ZSERIO_CPP_RUNTIME_TEST_SRCS})
