"""
The module implements generic walker through given zserio object tree.
"""
import functools
import re
import typing

from zserio.exception import PythonRuntimeException
from zserio.typeinfo import RecursiveTypeInfo, TypeAttribute, MemberInfo, MemberAttribute

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

        def begin_array(self, array: typing.List[typing.Any], member_info: MemberInfo) -> None:
            """
            Called at the beginning of an array.

            :param array: Zserio array.
            :param member_info: Array member info.
            """

            raise NotImplementedError()

        def end_array(self, array: typing.List[typing.Any], member_info: MemberInfo) -> None:
            """
            Called at the end of an array.

            :param array: Zserio array.
            :param member_info: Array member info.
            """

            raise NotImplementedError()

        def begin_compound(self, compound: typing.Any, member_info: MemberInfo) -> None:
            """
            Called at the beginning of an compound field object.

            Note that for uninitialized compounds (i.e. None) the visit_value method is called instead!

            :param compound: Compound zserio object.
            :param member_info: Compound member info.
            """
            raise NotImplementedError()

        def end_compound(self, compound: typing.Any, member_info: MemberInfo) -> None:
            """
            Called at the end of just walked compound object.

            :param compound: Compound zserio object.
            :param member_info: Compound member info.
            """
            raise NotImplementedError()

        def visit_value(self, value: typing.Any, member_info: MemberInfo) -> None:
            """
            Called when a simple (or an unset compound - i.e. None) value is reached.

            :param value: Simple value.
            :param member_info: Member info.
            """
            raise NotImplementedError()

    class Filter:
        """
        Interface for filters which can influence the walking.
        """

        def before_array(self, array: typing.List[typing.Any], member_info: MemberInfo) -> bool:
            """
            Called before an array.

            :param array: Zserio array.
            :param member_info: Array member info.

            :return: True when the walking should continue to the array.
            """
            raise NotImplementedError()

        def after_array(self, array: typing.List[typing.Any], member_info: MemberInfo) -> bool:
            """
            Called after an array.

            :param array: Zserio array.
            :param member_info: Array member info.

            :returns: True when the walking should continue to a next sibling, False to return to the parent.
            """

            raise NotImplementedError()

        def before_compound(self, compound: typing.Any, member_info: MemberInfo) -> bool:
            """
            Called before a compound object.

            Note that for uninitialized compounds (i.e. None) the before_value method is called instead!

            :param compound: Compound zserio object.
            :param member_info: Compound member info.

            :returns: True when the walking should continue into the compound object, False otherwise.
            """
            raise NotImplementedError()

        def after_compound(self, compound: typing.Any, member_info: MemberInfo) -> bool:
            """
            Called after a compound object.

            :param compound: Compound zserio object.
            :param member_info: Compound member info.

            :returns: True when the walking should continue to a next sibling, False to return to the parent.
            """
            raise NotImplementedError()

        def before_value(self, value: typing.Any, member_info: MemberInfo) -> bool:
            """
            Called before a simple (or an unset compound - i.e. None) value.

            :param value: Simple value.
            :param member_info: Member info.

            :returns: True when the walking should continue to the simple value, False otherwise.
            """
            raise NotImplementedError()

        def after_value(self, value: typing.Any, member_info: MemberInfo) -> bool:
            """
            Called after a simple value.

            :param value: Simple value.
            :param member_info: Member info.

            :returns: True when the walking should continue to a next sibling, False to return to the parent.
            """
            raise NotImplementedError()

    def __init__(self, observer: Observer, walk_filter: typing.Optional[Filter] = None) -> None:
        """
        Constructor.

        :param observer: Observer to use during walking.
        :param walk_filter: Walk filter to use.
        """

        self._observer = observer
        self._walk_filter = walk_filter if walk_filter is not None else WalkFilter()

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
            if self._walk_filter.before_array(zserio_object, member_info):
                self._observer.begin_array(zserio_object, member_info)
                for element in zserio_object:
                    if not self._walk_field_value(element, member_info):
                        break
                self._observer.end_array(zserio_object, member_info)
            return self._walk_filter.after_array(zserio_object, member_info)
        else:
            return self._walk_field_value(zserio_object, member_info)

    def _walk_field_value(self, zserio_object: typing.Any, member_info: MemberInfo) -> bool:
        type_info = member_info.type_info
        if TypeAttribute.FIELDS not in type_info.attributes or zserio_object is None: # simple field
            if self._walk_filter.before_value(zserio_object, member_info):
                self._observer.visit_value(zserio_object, member_info)
            return self._walk_filter.after_value(zserio_object, member_info)
        else:
            if self._walk_filter.before_compound(zserio_object, member_info):
                self._observer.begin_compound(zserio_object, member_info)
                self._walk_fields(zserio_object, type_info)
                self._observer.end_compound(zserio_object, member_info)
            return self._walk_filter.after_compound(zserio_object, member_info)

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

class WalkFilter(Walker.Filter):
    """
    Walk filter which implements composition of particular filters.

    The filters are called sequentially and logical and is applied on theirs results.
    """

    def __init__(self) -> None:
        """
        Constructor.
        """

        self._filters: typing.List[Walker.Filter] = []

    def add(self, walk_filter: Walker.Filter) -> Walker.Filter:
        """
        Adds filter.

        :param walk_filter: Filter to apply in this walk filter.
        :returns: Self for easier chaining.
        """

        self._filters.append(walk_filter)
        return self

    def before_array(self, array: typing.List[typing.Any], member_info: MemberInfo) -> bool:
        return self._apply_filters(lambda x: x.before_array(array, member_info))

    def after_array(self, array: typing.List[typing.Any], member_info: MemberInfo) -> bool:
        return self._apply_filters(lambda x: x.after_array(array, member_info))

    def before_compound(self, compound: typing.Any, member_info: MemberInfo) -> bool:
        return self._apply_filters(lambda x: x.before_compound(compound, member_info))

    def after_compound(self, compound: typing.Any, member_info: MemberInfo) -> bool:
        return self._apply_filters(lambda x: x.after_compound(compound, member_info))

    def before_value(self, value: typing.Any, member_info: MemberInfo) -> bool:
        return self._apply_filters(lambda x: x.before_value(value, member_info))

    def after_value(self, value: typing.Any, member_info: MemberInfo) -> bool:
        return self._apply_filters(lambda x: x.after_value(value, member_info))

    def _apply_filters(self, method):
        return functools.reduce(lambda x, y: x and y, map(method, self._filters), True)

    class Depth(Walker.Filter):
        """
        Walk filter which allows to walk only to the given maximum depth.
        """

        def __init__(self, max_depth: int):
            """
            Constructor.

            :param max_depth: Maximum depth to walk to.
            """

            self._max_depth = max_depth
            self._depth = 1

        def before_array(self, _array: typing.List[typing.Any], _member_info: MemberInfo) -> bool:
            enter = self._depth <= self._max_depth
            self._depth += 1
            return enter

        def after_array(self, _array: typing.List[typing.Any], _member_info: MemberInfo) -> bool:
            self._depth -= 1
            return True

        def before_compound(self, _compound: typing.Any, _member_info: MemberInfo) -> bool:
            enter = self._depth <= self._max_depth
            self._depth += 1
            return enter

        def after_compound(self, _compound: typing.Any, _member_info: MemberInfo) -> bool:
            self._depth -= 1
            return True

        def before_value(self, _value: typing.Any, _member_info: MemberInfo) -> bool:
            return self._depth <= self._max_depth

        def after_value(self, _value: typing.Any, _member_info: MemberInfo) -> bool:
            return True

    class Regex(Walker.Filter):
        """
        Walk filter which allows to walk only paths matching given regex.

        The path is constructed from field names within the root object, thus the root object
        itself is not part of the path.

        Currently arrays are processed based on field names and it's not possible to specify an index.
        """

        def __init__(self, path_regex: str) -> None:
            """
            Constructor.

            :param path_regex: Path regex to use for filtering.
            """

            self._path_regex = re.compile(path_regex)
            self._current_path: typing.List[str] = []

        def before_array(self, _array: typing.List[typing.Any], member_info: MemberInfo) -> bool:
            self._current_path.append(member_info.schema_name)
            return self._match(member_info)

        def after_array(self, _array: typing.List[typing.Any], _member_info: MemberInfo) -> bool:
            self._current_path.pop()
            return True

        def before_compound(self, _compound: typing.Any, member_info: MemberInfo) -> bool:
            self._current_path.append(member_info.schema_name)
            return self._match(member_info)

        def after_compound(self, _compound: typing.Any, _member_info: MemberInfo) -> bool:
            self._current_path.pop()
            return True

        def before_value(self, _value: typing.Any, member_info: MemberInfo) -> bool:
            self._current_path.append(member_info.schema_name)
            return self._match(member_info)

        def after_value(self, _value: typing.Any, _member_info: MemberInfo) -> bool:
            self._current_path.pop()
            return True

        def _match(self, member_info: MemberInfo) -> bool:
            return self._match_paths_recursive(self._current_path.copy(), member_info)

        def _match_paths_recursive(self, current_path: typing.List[str], member_info: MemberInfo) -> bool:
            type_info = member_info.type_info
            if TypeAttribute.FIELDS in type_info.attributes:
                for field in type_info.attributes[TypeAttribute.FIELDS]:
                    if not isinstance(field.type_info, RecursiveTypeInfo):
                        current_path.append(field.schema_name)
                        matched = self._match_paths_recursive(current_path, field)
                        current_path.pop()
                        if matched:
                            return True
                return False
            else:
                return self._path_regex.match(self.PATH_SEPARATOR.join(current_path)) is not None

        PATH_SEPARATOR = "."

    class ArrayLength(Walker.Filter):
        """
        Walk filter which allows to walk only to the given maximum array length.
        """

        def __init__(self, max_array_length: int):
            """
            Constructor.

            :param max_array_length: Maximum array length to walk to.
            """

            self._max_array_length = max_array_length
            self._array_length_stack: typing.List[int] = []

        def before_array(self, _array: typing.List[typing.Any], _member_info: MemberInfo) -> bool:
            self._array_length_stack.append(0)
            return True

        def after_array(self, _array: typing.List[typing.Any], _member_info: MemberInfo) -> bool:
            self._array_length_stack.pop()
            return True

        def before_compound(self, _compound: typing.Any, member_info: MemberInfo) -> bool:
            return self._before_array_element(member_info)

        def after_compound(self, _compound: typing.Any, member_info: MemberInfo) -> bool:
            return self._after_array_element(member_info)

        def before_value(self, _value: typing.Any, member_info: MemberInfo) -> bool:
            return self._before_array_element(member_info)

        def after_value(self, _value: typing.Any, member_info: MemberInfo) -> bool:
            return self._after_array_element(member_info)

        @staticmethod
        def _is_array_element(member_info: MemberInfo) -> bool:
            return MemberAttribute.ARRAY_LENGTH in member_info.attributes

        def _before_array_element(self, member_info: MemberInfo) -> bool:
            if self._is_array_element(member_info):
                self._array_length_stack[-1] += 1
                return self._array_length_stack[-1] <= self._max_array_length

            return True

        def _after_array_element(self, member_info: MemberInfo) -> bool:
            if self._is_array_element(member_info):
                return self._array_length_stack[-1] <= self._max_array_length

            return True
