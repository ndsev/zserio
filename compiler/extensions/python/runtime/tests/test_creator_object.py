import typing
import enum

from zserio.array import Array, ObjectArrayTraits, StringArrayTraits, BitBufferArrayTraits
from zserio.typeinfo import TypeInfo, TypeAttribute, MemberInfo, MemberAttribute, ItemInfo
from zserio.bitbuffer import BitBuffer

class DummyEnum(enum.Enum):
    ONE = 0
    TWO = 1

    @staticmethod
    def type_info():
        attribute_list = {
            TypeAttribute.UNDERLYING_TYPE : TypeInfo('uint8', int),
            TypeAttribute.ENUM_ITEMS: [
                ItemInfo('ONE', DummyEnum.ONE),
                ItemInfo('TWO', DummyEnum.TWO)
            ]
        }

        return TypeInfo('DummyEnum', DummyEnum, attributes=attribute_list)

class DummyBitmask:
    def __init__(self) -> None:
        self._value = 0

    @classmethod
    def from_value(cls: typing.Type['DummyBitmask'], value: int) -> 'DummyBitmask':
        instance = cls()
        instance._value = value
        return instance

    @staticmethod
    def type_info():
        attribute_list = {
            TypeAttribute.UNDERLYING_TYPE : TypeInfo('uint8', int),
            TypeAttribute.BITMASK_VALUES: [
                ItemInfo('READ', DummyBitmask.Values.READ),
                ItemInfo('WRITE', DummyBitmask.Values.WRITE)
            ]
        }

        return TypeInfo('DummyBitmask', DummyBitmask, attributes=attribute_list)

    def __eq__(self, other: object) -> bool:
        return self._value == other._value

    @property
    def value(self):
        return self._value

    class Values:
        READ: 'DummyBitmask' = None
        WRITE: 'DummyBitmask' = None

DummyBitmask.Values.READ = DummyBitmask.from_value(1)
DummyBitmask.Values.WRITE = DummyBitmask.from_value(2)

class DummyNested:
    def __init__(self, param_ : int, value_ : int = int(),  text_ : str = str(),
                 data_: typing.Union[BitBuffer, None] = None,
                 dummy_enum_: typing.Union[DummyEnum, None] = None,
                 dummy_bitmask_: typing.Union[DummyBitmask, None] = None) -> None:
        self._param_ = param_
        self._value_ = value_
        self._text_ = text_
        self._data_ = data_
        self._dummy_enum_ = dummy_enum_
        self._dummy_bitmask_ = dummy_bitmask_

    @staticmethod
    def type_info() -> TypeInfo:
        field_list: typing.List[MemberInfo] = [
            MemberInfo(
                'value', TypeInfo('uint32', int),
                attributes = {
                    MemberAttribute.PROPERTY_NAME : 'value',
                    MemberAttribute.CONSTRAINT : 'self.value < self.param'
                }
            ),
            MemberInfo(
                'text', TypeInfo('string', str),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'text'
                }
            ),
            MemberInfo(
                'data', TypeInfo('extern', BitBuffer),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'data'
                }
            ),
            MemberInfo(
                'dummyEnum', DummyEnum.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'dummy_enum'
                }
            ),
            MemberInfo(
                'dummyBitmask', DummyBitmask.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'dummy_bitmask'
                }
            )
        ]
        parameter_list: typing.List[MemberInfo] = [
            MemberInfo(
                'param', TypeInfo('uint32', int),
                attributes = {
                    MemberAttribute.PROPERTY_NAME : 'param'
                }
            )
        ]
        attribute_list = {
            TypeAttribute.FIELDS : field_list,
            TypeAttribute.PARAMETERS : parameter_list
        }

        return TypeInfo("DummyNested", DummyNested, attributes=attribute_list)

    @property
    def param(self) -> int:
        return self._param_

    @property
    def value(self) -> int:
        return self._value_

    @value.setter
    def value(self, value_: int) -> None:
        self._value_ = value_

    @property
    def text(self) -> str:
        return self._text_

    @text.setter
    def text(self, text) -> None:
        self._text_ = text

    @property
    def data(self) -> typing.Union[BitBuffer, None]:
        return self._data_

    @data.setter
    def data(self, data_: typing.Union[BitBuffer, None]) -> None:
        self._data_ = data_

    @property
    def dummy_enum(self) -> typing.Union[DummyEnum, None]:
        return self._dummy_enum_

    @dummy_enum.setter
    def dummy_enum(self, dummy_enum_: typing.Union[DummyEnum, None]) -> None:
        self._dummy_enum_ = dummy_enum_

    @property
    def dummy_bitmask(self) -> typing.Union[DummyBitmask, None]:
        return self._dummy_bitmask_

    @dummy_bitmask.setter
    def dummy_bitmask(self, dummy_bitmask_: typing.Union[DummyBitmask, None]) -> None:
        self._dummy_bitmask_ = dummy_bitmask_

class DummyObject:
    def __init__(self, value_: int = int(),
                 nested_: typing.Optional[DummyNested] = None,
                 text_: str = str(),
                 nested_array_: typing.List[DummyNested] = None,
                 text_array_: typing.List[str] = None) -> None:
        self._value_ = value_
        self._nested_ = nested_
        self._text_ = text_
        self._nested_array_ = Array(ObjectArrayTraits(None, None, None), nested_array_, is_auto=True)
        self._text_array = Array(StringArrayTraits(), text_array_, is_auto=True)
        self._extern_array_ = None
        self._optional_bool_ = None
        self._optional_nested_ = None

    @staticmethod
    def type_info() -> TypeInfo:
        field_list: typing.List[MemberInfo] = [
            MemberInfo(
                'value', TypeInfo('uint32', int),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'value'
                }
            ),
            MemberInfo(
                'nested', DummyNested.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'nested',
                    MemberAttribute.TYPE_ARGUMENTS : [(lambda self, zserio_index: self.value)]
                }
            ),
            MemberInfo(
                'text', TypeInfo('string', str),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'text'
                }
            ),
            MemberInfo(
                'nestedArray', DummyNested.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'nested_array',
                    MemberAttribute.ARRAY_LENGTH : None,
                    MemberAttribute.TYPE_ARGUMENTS : [(lambda self, zserio_index: self.value)]
                }
            ),
            MemberInfo(
                'textArray', TypeInfo('string', str),
                attributes={
                    MemberAttribute.PROPERTY_NAME: 'text_array',
                    MemberAttribute.ARRAY_LENGTH: None
                }
            ),
            MemberInfo(
                'externArray', TypeInfo('extern', BitBuffer),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'extern_array',
                    MemberAttribute.ARRAY_LENGTH : None,
                    MemberAttribute.OPTIONAL : None,
                    MemberAttribute.IS_USED_INDICATOR_NAME : 'is_extern_array_used',
                    MemberAttribute.IS_SET_INDICATOR_NAME : 'is_extern_array_set'
                }
            ),
            MemberInfo(
                'optionalBool', TypeInfo('bool', bool),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'optional_bool',
                    MemberAttribute.OPTIONAL : None,
                    MemberAttribute.IS_USED_INDICATOR_NAME : 'is_optional_bool_used',
                    MemberAttribute.IS_SET_INDICATOR_NAME : 'is_optional_bool_set'
                }
            ),
            MemberInfo(
                'optionalNested', DummyNested.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'optional_nested',
                    MemberAttribute.OPTIONAL : None,
                    MemberAttribute.IS_USED_INDICATOR_NAME : 'is_optional_nested_used',
                    MemberAttribute.IS_SET_INDICATOR_NAME : 'is_optional_nested_set',
                    MemberAttribute.TYPE_ARGUMENTS : [(lambda self, zserio_index: self.value)]
                }
            )
        ]
        attribute_list = {
            TypeAttribute.FIELDS : field_list
        }

        return TypeInfo("DummyObject", DummyObject, attributes=attribute_list)

    @property
    def value(self) -> int:
        return self._value_

    @value.setter
    def value(self, value) -> None:
        self._value_ = value

    @property
    def nested(self) -> DummyNested:
        return self._nested_

    @nested.setter
    def nested(self, nested) -> None:
        self._nested_ = nested

    @property
    def text(self) -> str:
        return self._text_

    @text.setter
    def text(self, text) -> None:
        self._text_ = text

    @property
    def nested_array(self) -> typing.List[DummyNested]:
        return self._nested_array_.raw_array

    @nested_array.setter
    def nested_array(self, nested_array_: typing.List[DummyNested]) -> None:
        self._nested_array_ = Array(ObjectArrayTraits(None, None, None), nested_array_, is_auto=True)

    @property
    def text_array(self) -> typing.List[str]:
        return self._text_array.raw_array

    @text_array.setter
    def text_array(self, text_array_: typing.List[str]) -> None:
        self._text_array = Array(StringArrayTraits(), text_array_, is_auto=True)

    @property
    def extern_array(self) -> typing.List[BitBuffer]:
        return self._extern_array_.raw_array

    @extern_array.setter
    def extern_array(self, extern_array_: typing.List[BitBuffer]) -> None:
        self._extern_array_ = Array(BitBufferArrayTraits(), extern_array_, is_auto=True)

    @property
    def optional_bool(self) -> typing.Optional[bool]:
        return self._optional_bool_

    @optional_bool.setter
    def optional_bool(self, optional_bool_: typing.Optional[bool]) -> None:
        self._optional_bool_ = optional_bool_

    @property
    def optional_nested(self) -> typing.Optional[DummyNested]:
        return self._optional_nested_

    @optional_nested.setter
    def optional_nested(self, optional_nested_: typing.Optional[DummyNested]) -> None:
        self._optional_nested_ = optional_nested_
