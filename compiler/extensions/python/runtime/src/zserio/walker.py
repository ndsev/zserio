"""
The module implements generic walker through given zserio object tree.
"""
import typing

from zserio.exception import PythonRuntimeException
from zserio.typeinfo import TypeAttribute, MemberInfo, MemberAttribute

class Walker:
    """
    Walker through zserio objects, based on generated type info (see -withTypeInfoCode).
    """

    class Observer:
        """
        Interface for observers which are called by the walker.
        """

        def begin_root(self, compound: typing.Any) -> None:
            """
            Called for the root compound zserio object which is to be walked-through.

            :param compound: Root compound zserio object.
            """
            raise NotImplementedError()

        def end_root(self, compound: typing.Any) -> None:
            """
            Called at the end of just walked root compound zserio object.

            :param compound: Root compound zserio object.
            """
            raise NotImplementedError()

        def begin_array(self, array: typing.List[typing.Any], member_info: MemberInfo) -> bool:
            """
            :param array: Zserio array.
            :param member_info: Array member info.

            :return: True when the walking should continue to the array elements.
            """

            raise NotImplementedError()

        def end_array(self, array: typing.List[typing.Any], member_info: MemberInfo) -> bool:
            """
            :param array: Zserio array.
            :param member_info: Array member info.

            :returns: True when the walking should continue to a next sibling, False to return to the parent.
            """

            raise NotImplementedError()

        def begin_compound(self, compound: typing.Any, member_info: MemberInfo) -> bool:
            """
            Called at the beginning of an compound field object.

            Note that for uninitialized compounds (i.e. None) the visit_value method is called instead!

            Note that end_compound will be called regardless of the return value to allow finer control
            of the walking.

            :param compound: Compound zserio object.
            :param member_info: Compound member info.

            :returns: True when the walking should continue into the compound object, False otherwise.
            """
            raise NotImplementedError()

        def end_compound(self, compound: typing.Any, member_info: MemberInfo) -> bool:
            """
            Called at the end of just walked compound object.

            :param compound: Compound zserio object.
            :param member_info: Compound member info.

            :returns: True when the walking should continue to a next sibling, False to return to the parent.
            """
            raise NotImplementedError()

        def visit_value(self, value: typing.Any, member_info: MemberInfo) -> bool:
            """
            Called when a simple (or an unset compound - i.e. None) value is reached.

            :param value: Simple value.
            :param member_info: Member info.

            :returns: True when the walking should continue to a next sibling, False to return to the parent.
            """
            raise NotImplementedError()

    def __init__(self, observer: Observer) -> None:
        """
        Constructor.

        :param observer: Observer to use during walking.
        """

        self._observer = observer

    def walk(self, zserio_object: typing.Any) -> None:
        """
        Walks given zserio object which must be generated with type_info (see -withTypeInfoCode options).

        :param zserio_object: Zserio object to walk.
        """

        if not hasattr(zserio_object, "type_info"):
            raise PythonRuntimeException("Type info must be enabled (see zserio option -withTypeInfoCode)!")

        self._observer.begin_root(zserio_object)
        self._walk_fields(zserio_object, zserio_object.type_info())
        self._observer.end_root(zserio_object)

    def _walk_field(self, zserio_object: typing.Any, member_info: MemberInfo) -> bool:
        if MemberAttribute.ARRAY_LENGTH in member_info.attributes and zserio_object is not None:
            if self._observer.begin_array(zserio_object, member_info):
                for element in zserio_object:
                    if not self._walk_field_value(element, member_info):
                        break
            return self._observer.end_array(zserio_object, member_info)
        else:
            return self._walk_field_value(zserio_object, member_info)

    def _walk_field_value(self, zserio_object: typing.Any, member_info: MemberInfo) -> bool:
        type_info = member_info.type_info
        if TypeAttribute.FIELDS not in type_info.attributes or zserio_object is None: # simple field
            return self._observer.visit_value(zserio_object, member_info)
        else:
            if self._observer.begin_compound(zserio_object, member_info):
                self._walk_fields(zserio_object, type_info)
            return self._observer.end_compound(zserio_object, member_info)

    def _walk_fields(self, zserio_object, type_info):
        if TypeAttribute.FIELDS in type_info.attributes:
            fields = type_info.attributes[TypeAttribute.FIELDS]
            if TypeAttribute.SELECTOR in type_info.attributes:
                # union or choice
                choice_tag = zserio_object.choice_tag
                if choice_tag != zserio_object.UNDEFINED_CHOICE:
                    field = fields[choice_tag]
                    self._walk_field(getattr(zserio_object, field.attributes[MemberAttribute.PROPERTY_NAME]),
                                     field)
                # else: uninitialized or empty branch
            else:
                # structure
                for field in fields:
                    if not self._walk_field(getattr(zserio_object,
                                                    field.attributes[MemberAttribute.PROPERTY_NAME]),
                                            field):
                        break
