"""
The module contains classes for type info.
"""

import typing
import enum

class TypeInfo:
    """
    Type info class which provides information about generated types.
    """

    def __init__(self, schema_name: str, py_type: typing.Type, *,
                 attributes: typing.Dict['TypeAttribute', typing.Any] = None):
        """
        Type info constructor.

        :param schema_name: Zserio schema full type name.
        :param py_type: Reference to the generated type.
        :param attributes: List of type attributes.
        """

        self._schema_name = schema_name
        self._py_type = py_type
        self._attributes = attributes if attributes is not None else {}

    @property
    def schema_name(self) -> str:
        """
        Returns the full type name as is defined in Zserio schema.

        :returns: Zserio schema full type name.
        """

        return self._schema_name

    @property
    def py_type(self) -> typing.Type:
        """
        Gets Python type generated for this Zserio type.

        :returns: Python type.
        """

        return self._py_type

    @property
    def attributes(self) -> typing.Dict['TypeAttribute', typing.Any]:
        """
        Gets dictionary with type attributes.

        Attribute is a an arbitrary value which type is given by the key, which is TypeAttribute enumeration.

        * `(TypeAttribute.UNDERLYING_TYPE, TypeInfo(...))`

          * denotes that the type has an underlying type (e.g. enum or bitmask),
            the value is a TypeInfo of the underlying type

        * `(TypeAttribute.UNDERLYING_TYPE_ARGUMENTS, [lambda: 5])`

          * keeps type arguments of the underlying type when it is a dynamic bit field,
            the value is a lambda function which returns the argument constant value

        * `(TypeAttribute.ENUM_ITEMS, [ItemInfo(...), ItemInfo(...), ...])`

          * denotes that the type is an enumeration, the value contains list of enum items ItemInfo

        * `(TypeAttribute.BITMASK_VALUES, [ItemInfo(...), ItemInfo(...), ...])`

          * denotes that the type is a bitmask, the value contains list of bitmask values ItemInfo

        * `(TypeAttribute.FIELDS,  [MemberInfo(...), MemberInfo(...), ...])`

          * denotes that the type is a compound type, the value contains list of fields MemberInfo,
            the attribute is present even for empty compounds and then it contains the empty list

        * `(TypeAttribute.PARAMETERS, [MemberInfo(...), MemberInfo(...), ...])`

          * denotes that the compound type is parameterized type, the value contains non-empty list of
            parameters MemberInfo, for non-parameterized types the attribute is not present

        * `(TypeAttribute.FUNCTIONS, [MemberInfo(...), MemberInfo(...), ...])`

          * denotes that the compound type has functions, the value contains non-empty list of functions
            MemberInfo, for compounds without functions the attribute is not present

        * `(TypeAttribute.SELECTOR, None`) `(TypeAttribute.SELECTOR, lambda self: self.param1)`

          * denotes that the type is either a union (when the value is None) or choice when the
            value contains the selector expression as a lambda function taking single parent argument

        * `(TypeAttribute.CASES, [CaseInfo(...), CaseInfo(...), ...])`

          * denotes that the type is a choice, the value contains list of CaseInfo for each choice case
          * note that the TypeAttribute.FIELDS attribute is present also in choices

        * `(TypeAttribute.TEMPLATE_NAME, 'TemplatedStructure')`

          * denotes that the type is a template instantiation, the value contains the template name

        * `(TypeAttribute.TEMPLATE_ARGUMENTS, [test.TemplateArg.type_info(), ...])`

          * present when the type is a template instantiation, the value contains list of template arguments
            TypeInfo

        * `(TypeAttribute.COLUMNS, [MemberInfo(...), MemberInfo(...), ...])`

          * denotes that the type is a SQL table, the value contains list of columns MemberInfo

        * `(TypeAttribute.TABLES, [MemberInfo(...), MemberInfo(...), ...])`

          * denotes that the type is a SQL database, the value contain list of tables MemberInfo

        * `(TypeAttribute.SQL_CONSTRAINT, 'PRIMARY KEY(columnA)')`

          * denotes that the SQL table contains a SQL constraint

        * `(TypeAttribute.VIRTUAL_TABLE_USING, 'fts4')`

          * denotes that the SQL table is a virtual table, the value contains the used virtual table module

        * `(TypeAttribute.WITHOUT_ROWID, None)`

          * denotes that the SQL table is a WITHOUT ROWID table, the value is always None

        * `(TypeAttribute.MESSAGES, [MemberInfo(...), MemberInfo(...), ...])`

          * denotes that the type is a pub-sub, the value contains list of messages MemberInfo

        * `(TypeAttribute.METHODS, [MemberInfo(...), MemberInfo(...), ...])`

          * denotes that the type is a service, the value contains list of methods MemberInfo

        :returns Type attributes.
        """

        return self._attributes

class RecursiveTypeInfo:
    """
    Type info for recursive types used as a wrapper around generated static type_info method to prevent
    infinite recursion in type info definition.
    """

    def __init__(self, type_info_func : typing.Callable[[], TypeInfo]):
        """
        Constructor.

        :param type_info_func: Generated static type_info method to wrap.
        """

        self._type_info_func = type_info_func
        self._type_info = None

    @property
    def schema_name(self) -> str:
        """
        See :py:attr:`TypeInfo.schema_name`.
        """

        return self._get_type_info().schema_name

    @property
    def py_type(self) -> typing.Type:
        """
        See :py:attr:`TypeInfo.py_type`.
        """

        return self._get_type_info().py_type

    @property
    def attributes(self) -> typing.Dict['TypeAttribute', typing.Any]:
        """
        See :py:attr:`TypeInfo.attributes`.
        """

        return self._get_type_info().attributes

    def _get_type_info(self):
        if self._type_info is None:
            self._type_info = self._type_info_func()
        return self._type_info

class TypeAttribute(enum.Enum):
    """
    Type attribute type to be used in TypeInfo.

    Determines type of the second element in the attribute tuple returned in attributes list from TypeInfo.
    """

    UNDERLYING_TYPE = enum.auto()
    UNDERLYING_TYPE_ARGUMENTS = enum.auto()
    ENUM_ITEMS = enum.auto()
    BITMASK_VALUES = enum.auto()
    FIELDS = enum.auto()
    PARAMETERS = enum.auto()
    FUNCTIONS = enum.auto()
    SELECTOR = enum.auto()
    CASES = enum.auto()
    TEMPLATE_NAME = enum.auto()
    TEMPLATE_ARGUMENTS= enum.auto()
    COLUMNS = enum.auto()
    TABLES = enum.auto()
    SQL_CONSTRAINT = enum.auto()
    VIRTUAL_TABLE_USING = enum.auto()
    WITHOUT_ROWID = enum.auto()
    MESSAGES = enum.auto()
    METHODS = enum.auto()

class MemberInfo:
    """
    Member info class which provides information about members of compound types.
    """

    def __init__(self, schema_name: str, typeinfo: typing.Union[TypeInfo, RecursiveTypeInfo], *,
                 attributes: typing.Dict['MemberAttribute', typing.Any] = None):
        """
        Member info constructor.

        :param schema_name: Name of the member as is defined in Zserio schema.
        :param type_info: Type info of the member.
        :param attributes: List of member attributes.
        """

        self._schema_name = schema_name
        self._type_info = typeinfo
        self._attributes = attributes if attributes is not None else {}

    @property
    def schema_name(self) -> str:
        """
        Gets name of the member as is defined in Zserio schema.

        :returns: Member name in Zserio schema.
        """

        return self._schema_name

    @property
    def type_info(self) -> typing.Union[TypeInfo, RecursiveTypeInfo]:
        """
        Gets type info of this member.

        :returns: Type info.
        """

        return self._type_info

    @property
    def attributes(self) -> typing.Dict['MemberAttribute', typing.Any]:
        """
        Gets dictionary with member attributes.

        Attribute is a an arbitrary value which type is given by the key, which is MemberAttribute enumeration.
        All expressions are stored as strings.

        **Possible attributes:**

        * `(MemberAttribute.PROPERTY_NAME, 'field1')`

          * contains name of the property generated in Python

        * `(MemberAttribute.TYPE_ARGUMENTS, [(lambda self, zserio_index: self.field1), ...])`

          * for compound type members, keeps type arguments for parameterized types or dynamic bit fields,
            the value contains list of lambda functions evaluating particular arguments expression,
            where the lambdas take parent and an element index (which can be None if not used) as arguments

          * for members of sql tables, keeps type arguments for columns, the value contains list of
            lambdas where the lambdas take either single explicit parameter argument for explicit parameters or
            single 'self' argument, which is an object providing property-like getters for column names
            used in expressions

        * `(MemberAttribute.ALIGN, lambda: 8)`

          * denotes that the member field has an alignment, the value is a lambda function which returns the
            alignment constant value

        * `(MemberAttribute.OFFSET, lambda self: self.offset_field)`

          * denotes that the member field has an offset, the value contains the offset expression
            as a lambda function taking single parent argument

        * `(MemberAttribute.INITIALIZER, lambda: 10)`

          * denotes that the member field has an initializer, the value is a lambda function which returns the
            the initializer constant value

        * `(MemberAttribute.OPTIONAL, None)`, `(MemberAttribute.OPTIONAL, lambda self: self.field1 != 0)`

          * denotes that the member is an optional, when the value is None, then it's an auto optional,
            otherwise it contains the optional clause as a lambda function taking single parent argument

        * `(MemberAttribute.IS_USED_INDICATOR_NAME, 'is_field_used)`

          * if the member is an optional, the value contains the "is_used" indicator name generated in Python

        * `(MemberAttribute.IS_SET_INDICATOR_NAME, 'is_field_set)`

          * if the member is an optional, the value contains the "is_set" indicator name generated in Python

        * `(MemberAttribute.CONSTRAINT, lambda self: field > 10)`

          * denotes that the member has a constraint, the value contains the constraint expression
            as a lambda function taking single parent argument

        * `(MemberAttribute.FUNCTION_NAME, 'function_name')`

          * keeps the generated function name

        * `MemberAttribute.FUNCTION_RESULT, lambda self: self.field1 + 5)`

          * keeps the result expression of a function as a lambda function taking single parent argument

        * `(MemberAttribute.ARRAY_LENGTH, None)`, `(MemberAttribute.ARRAY_LENGTH, lambda self: self.field1 + 1)`

          * denotes that the member is an array, when the value is None, then it's an auto array,
            otherwise it contains the length expression as a lambda function taking single parent argument

        * `(MemberAttribute.IMPLICIT, None)`

          * denotes that the member is an implicit array, the value is always None

        * `(MemberAttribute.PACKED, None)`

          * denotes that the member is a packed array, the value is always None

        * `(MemberAttribute.SQL_TYPE_NAME, 'INTEGER')`

          * keeps SQLite type name used for this column

        * `(MemberAttribute.SQL_CONSTRAINT, 'PRIMARY KEY NOT NULL')`

          * denotes that the member has a SQL constraint

        * `(MemberAttribute.VIRTUAL, None)`

          * denotes that the column in a SQL table is virtual

        * `(MemberAttribute.TOPIC, 'topic/definition')`

          * keeps the topic definition of a pub-sub message

        * `(MemberAttribute.PUBLISH, 'publish_message_name')`

          * denotes that the pub-sub message is published, the value contains the publishing method name

        * `(MemberAttribute.SUBSCRIBE, 'subscribe_message_name')`

          * denotes that the pub-sub message is subscribed, the value contains the subscribing method name

        * `(MemberAttribute.CLIENT_METHOD_NAME, 'client_method_name')`

          * keeps the name of the method in the generated Client class

        * `(MemberAttribute.REQUEST_TYPE, request_type.type_info())`

          * keeps the request type TypeInfo, note that response type is in the method TypeInfo

        :returns: Member attributes.
        """

        return self._attributes

class MemberAttribute(enum.Enum):
    """
    Member attribute type to be used in MemberInfo.

    Determines type of the second element in the attribute tuple returned in attributes list from MemberInfo.
    """

    PROPERTY_NAME = enum.auto()
    TYPE_ARGUMENTS = enum.auto()
    ALIGN = enum.auto()
    OFFSET = enum.auto()
    INITIALIZER = enum.auto()
    OPTIONAL = enum.auto()
    IS_USED_INDICATOR_NAME = enum.auto()
    IS_SET_INDICATOR_NAME = enum.auto()
    CONSTRAINT = enum.auto()
    FUNCTION_NAME = enum.auto()
    FUNCTION_RESULT = enum.auto()
    ARRAY_LENGTH = enum.auto()
    IMPLICIT = enum.auto()
    PACKED = enum.auto()
    SQL_TYPE_NAME = enum.auto()
    SQL_CONSTRAINT = enum.auto()
    VIRTUAL = enum.auto()
    TOPIC = enum.auto()
    PUBLISH = enum.auto()
    SUBSCRIBE = enum.auto()
    CLIENT_METHOD_NAME = enum.auto()
    REQUEST_TYPE = enum.auto()

class CaseInfo:
    """
    Case info class which provides information about choice cases in generated choices.
    """

    def __init__(self, case_expressions: typing.List[typing.Any], field: typing.Optional[MemberInfo]):
        """
        Constructor.

        :param case_expressions: List of case expression in the choice case. When empty, it's a default case.
        :param field: Field associated with the choice case, can be empty.
        """

        self._case_expressions = case_expressions
        self._field = field

    @property
    def case_expressions(self) -> typing.List[typing.Any]:
        """
        Gets case expressions in the choice case. An empty list denotes the default case.

        :returns: List of case expressions as evaluated constant values.
        """

        return self._case_expressions

    @property
    def field(self) -> typing.Optional[MemberInfo]:
        """
        Gets field associated with the choice case. Can be empty.

        :returns: Field MemberInfo.
        """

        return self._field


class ItemInfo:
    """
    Item info class which provides information about items of generated enumerable types.
    """

    def __init__(self, schema_name: str, py_item: typing.Any):
        """
        Constructor.

        :param schema_name: Name of the item as is defined in Zserio schema.
        :param py_item: Reference to the generated item.
        """

        self._schema_name = schema_name
        self._py_item = py_item

    @property
    def schema_name(self) -> str:
        """
        Gets name of the item as is defined in Zserio schema.

        :returns: Item name in Zserio schema.
        """

        return self._schema_name

    @property
    def py_item(self) -> typing.Any:
        """
        Gets reference to the item generated in Python.

        :returns: Python item.
        """

        return self._py_item
