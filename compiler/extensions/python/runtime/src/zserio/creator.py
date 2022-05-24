"""
The module implements creator of zserio object tree based on type info.
"""

import typing
import enum

from zserio.typeinfo import TypeInfo, RecursiveTypeInfo, TypeAttribute, MemberInfo, MemberAttribute
from zserio.exception import PythonRuntimeException

class ZserioTreeCreator:
    """
    Allows to build zserio object tree defined by the given type info.
    """

    def __init__(self, type_info: typing.Union[TypeInfo, RecursiveTypeInfo]) -> None:
        """
        Constructor.

        :param type_info: Type info defining the tree.
        """

        self._type_info = type_info
        self._member_info_stack: typing.List[MemberInfo] = []
        self._value_stack: typing.List[typing.Any] = []
        self._state = ZserioTreeCreator._State.BEFORE_ROOT

    def begin_root(self) -> None:
        """
        Creates the top level compound element and move to state of building its children.
        """

        if self._state != ZserioTreeCreator._State.BEFORE_ROOT:
            raise PythonRuntimeException(f"ZserioTreeCreator: Cannot begin root in state '{self._state}'!")

        self._value_stack.append(self._type_info.py_type())

        self._state = ZserioTreeCreator._State.IN_COMPOUND

    def end_root(self) -> typing.Any:
        """
        Finishes building and returns the created tree.

        :returns: Zserio object tree.
        :raises PythonRuntimeException: When the creator is not in state of building the root object.
        """

        if self._state != ZserioTreeCreator._State.IN_COMPOUND or len(self._value_stack) != 1:
            raise PythonRuntimeException(f"ZserioTreeCreator: Cannot end root in state '{self._state}'!")

        self._state = ZserioTreeCreator._State.BEFORE_ROOT
        return self._value_stack.pop()

    def begin_array(self, name: str) -> None:
        """
        Creates an array field within the current compound.

        :param name: Name of the array field.
        :raises PythonRuntimeException: When the field doesn't exists or when the creator is not in a compound.
        """

        if self._state != ZserioTreeCreator._State.IN_COMPOUND:
            raise PythonRuntimeException(f"ZserioTreeCreator: Cannot begin array in state '{self._state}'!")

        member_info = self._find_member_info(self._get_type_info(), name)
        if not MemberAttribute.ARRAY_LENGTH in member_info.attributes:
            raise PythonRuntimeException("ZserioTreeCreator: Member "
                                         f"'{member_info.schema_name}' is not an array!")

        self._member_info_stack.append(member_info)
        self._value_stack.append([])
        self._state = ZserioTreeCreator._State.IN_ARRAY

    def end_array(self):
        """
        Finishes the array field.

        :raises PythonRuntimeException: When the creator is not in an array field.
        """
        if self._state != ZserioTreeCreator._State.IN_ARRAY:
            raise PythonRuntimeException(f"ZserioTreeCreator: Cannot end array in state '{self._state}'!")

        member_info = self._member_info_stack.pop()
        value = self._value_stack.pop()
        setattr(self._value_stack[-1], member_info.attributes[MemberAttribute.PROPERTY_NAME], value)
        self._state = ZserioTreeCreator._State.IN_COMPOUND

    def begin_compound(self, name):
        """
        Creates a compound field within the current compound.

        :param name: Name of the compound field.
        :raises PythonRuntimeException: When the field doesn't exits or when the creator is not in a compound.
        """

        if self._state != ZserioTreeCreator._State.IN_COMPOUND:
            raise PythonRuntimeException(f"ZserioTreeCreator: Cannot begin compound in state '{self._state}'!")

        member_info = self._find_member_info(self._get_type_info(), name)

        if MemberAttribute.ARRAY_LENGTH in member_info.attributes:
            raise PythonRuntimeException("ZserioTreeCreator: Member "
                                         f"'{member_info.schema_name}' is an array!")

        if not TypeAttribute.FIELDS in member_info.type_info.attributes:
            raise PythonRuntimeException(f"ZserioTreeCreator: Member "
                                         f"'{member_info.schema_name}' is not a compound!")

        compound = self._create_object(member_info, self._value_stack[-1])

        self._member_info_stack.append(member_info)
        self._value_stack.append(compound)
        self._state = ZserioTreeCreator._State.IN_COMPOUND

    def end_compound(self):
        """
        Finishes the compound.

        :raises PythonRuntimeException: When the creator is not in a compound field.
        """

        if self._state != ZserioTreeCreator._State.IN_COMPOUND or not self._member_info_stack:
            raise PythonRuntimeException(f"ZserioTreeCreator: Cannot end compound in state '{self._state}'" +
                                         (", expecting end_root!" if not self._member_info_stack else "!"))

        if MemberAttribute.ARRAY_LENGTH in self._member_info_stack[-1].attributes:
            raise PythonRuntimeException("ZserioTreeCreator: Cannot end compound, it's an array element!")

        member_info = self._member_info_stack.pop()
        value = self._value_stack.pop()
        setattr(self._value_stack[-1], member_info.attributes[MemberAttribute.PROPERTY_NAME], value)

        self._state = ZserioTreeCreator._State.IN_COMPOUND

    def set_value(self, name: str, value: typing.Any):
        """
        Sets field value within the current compound.

        :param name: Name of the field.
        :param value: Value to set.

        :raises PythonRuntimeException: When the field doesn't exits or when the creator is not in a compound.
        """

        if self._state != ZserioTreeCreator._State.IN_COMPOUND:
            raise PythonRuntimeException(f"ZserioTreeCreator: Cannot set value in state '{self._state}'!")

        member_info = self._find_member_info(self._get_type_info(), name)

        if MemberAttribute.ARRAY_LENGTH in member_info.attributes:
            raise PythonRuntimeException("ZserioTreeCreator: Expecting array in member "
                                         f"'{member_info.schema_name}'!")

        if value is not None and not isinstance(value, member_info.type_info.py_type):
            raise PythonRuntimeException(f"ZserioTreeCreator: Unexpected value type '{type(value)}', expected "
                                         f"{member_info.type_info.py_type}")

        setattr(self._value_stack[-1], member_info.attributes[MemberAttribute.PROPERTY_NAME], value)

    def begin_compound_element(self):
        """
        Creates compound array element within the current array.

        :raises PythonRuntimeException: When the creator is not in an array of compounds.
        """

        if self._state != ZserioTreeCreator._State.IN_ARRAY:
            raise PythonRuntimeException("ZserioTreeCreator: Cannot begin compound element in state "
                                         f"'{self._state}'!")

        member_info = self._member_info_stack[-1]

        if not TypeAttribute.FIELDS in member_info.type_info.attributes:
            raise PythonRuntimeException(f"ZserioTreeCreator: Member "
                                         f"'{self._member_info_stack[-1].schema_name}' is not a compound!")

        compound = self._create_object(member_info, self._value_stack[-2], len(self._value_stack[-1]))

        self._value_stack.append(compound)
        self._state = ZserioTreeCreator._State.IN_COMPOUND

    def end_compound_element(self) -> None:
        """
        Finishes the compound element.

        :raises PythonRuntimeException: When the creator is not in a compound element.
        """

        if self._state != ZserioTreeCreator._State.IN_COMPOUND or not self._member_info_stack:
            raise PythonRuntimeException("ZserioTreeCreator: Cannot end compound element in state "
                                         f"'{self._state}'!" +
                                         (", expecting end_root!" if not self._member_info_stack else "!"))

        if not MemberAttribute.ARRAY_LENGTH in self._member_info_stack[-1].attributes:
            raise PythonRuntimeException("ZserioTreeCreator: Cannot end compound element, not in array!")

        value = self._value_stack.pop()
        self._value_stack[-1].append(value)
        self._state = ZserioTreeCreator._State.IN_ARRAY

    def add_value_element(self, value: typing.Any) -> None:
        """
        Adds the value to the array.

        :raises PythonRuntimeException: When the creator is not in an array of simple values.
        """

        if self._state != ZserioTreeCreator._State.IN_ARRAY:
            raise PythonRuntimeException("ZserioTreeCreator: Cannot add value element in state "
                                         f"'{self._state}'!")

        member_info = self._member_info_stack[-1]
        if value is not None and not isinstance(value, member_info.type_info.py_type):
            raise PythonRuntimeException(f"ZserioTreeCreator: Unexpected value type '{type(value)}', expected "
                                         f"{member_info.type_info.py_type}")

        self._value_stack[-1].append(value)

    def _get_type_info(self) -> typing.Union[TypeInfo, RecursiveTypeInfo]:
        return self._member_info_stack[-1].type_info if self._member_info_stack else self._type_info

    @staticmethod
    def _find_member_info(type_info: typing.Union[TypeInfo, RecursiveTypeInfo], name: str):
        members = type_info.attributes[TypeAttribute.FIELDS]
        for member in members:
            if member.schema_name == name:
                return member
        raise PythonRuntimeException(f"ZserioTreeCreator: Member '{name}' not found in "
                                     f"'{type_info.schema_name}'!")

    @staticmethod
    def _create_object(member_info: MemberInfo, parent: typing.Any,
                       element_index: typing.Optional[int]=None) -> typing.Any:
        args = []
        if MemberAttribute.TYPE_ARGUMENTS in member_info.attributes:
            for argument_lambda in member_info.attributes[MemberAttribute.TYPE_ARGUMENTS]:
                args.append(argument_lambda(parent, element_index))

        return member_info.type_info.py_type(*args)

    class _State(enum.Enum):
        BEFORE_ROOT = enum.auto()
        IN_COMPOUND = enum.auto()
        IN_ARRAY = enum.auto()
        DONE = enum.auto()
