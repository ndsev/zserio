"""
The module provides custom zserio Enum which allows to mark enum items deprecated.
"""

import enum
import typing
import warnings


class _EnumType(enum.EnumMeta):
    """
    Special enum meta class which fires a warning whenever a deprecated enum item is accessed.
    """

    def __getattribute__(cls, name):
        obj = super().__getattribute__(name)
        if isinstance(obj, enum.Enum) and obj._is_deprecated:
            warnings.warn(DeprecationWarning(f"Enum item '{obj}' is deprecated!"), stacklevel=2)
        return obj

    def __getitem__(cls, name):
        member = super().__getitem__(name)
        if member._is_deprecated:
            warnings.warn(DeprecationWarning(f"Enum item '{member}' is deprecated!"), stacklevel=2)
        return member

    def __call__(cls, value, names=None):
        # Python 3.12.3 has changed default value for 'names' from 'None' to '_not_given'
        if names is None:
            obj = super().__call__(value)
        else:
            obj = super().__call__(value, names=names)
        if isinstance(obj, enum.Enum) and obj._is_deprecated:
            warnings.warn(DeprecationWarning(f"Enum item '{obj}' is deprecated!"), stacklevel=2)
        return obj


class DeprecatedItem:
    """
    Marker used to make enum items deprecated.

    Just use the class instead of creating an instance.

    Example:

    .. code:: python

        import zserio

        class MyEnum(zserio.Enum):
            STABLE = 1,
            OLD = 2, zserio.DeprecatedItem
            NEW = 3
    """


class Enum(enum.Enum, metaclass=_EnumType):
    """
    Custom zserio enum base class which allows to mark items deprecated.
    """

    def __new__(cls, value: typing.Any, deprecated: typing.Optional[DeprecatedItem] = None):
        """
        Creator method which allows to mark the item as deprecated.

        :param value: The enum item value.
        :param deprecated: DeprecatedItem or None.

        :returns: Instance of the enum item.
        """

        member = object.__new__(cls)
        member._value_ = value
        if deprecated is not None and deprecated != DeprecatedItem:
            raise ValueError(
                f"Invalid argument 'deprecated', which is {deprecated}! " f"Expecting {DeprecatedItem} or None."
            )
        member._is_deprecated = deprecated is not None  # type: ignore[attr-defined]
        return member
