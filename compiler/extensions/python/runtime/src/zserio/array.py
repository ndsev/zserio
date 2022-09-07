"""
The module implements abstraction for arrays used by Zserio python extension.
"""

import typing

from zserio.bitposition import alignto
from zserio.bitsizeof import (bitsizeof_varuint16, bitsizeof_varuint32, bitsizeof_varuint64, bitsizeof_varuint,
                              bitsizeof_varint16, bitsizeof_varint32, bitsizeof_varint64, bitsizeof_varint,
                              bitsizeof_varsize, bitsizeof_string, bitsizeof_bitbuffer)
from zserio.bitreader import BitStreamReader
from zserio.bitwriter import BitStreamWriter
from zserio.bitbuffer import BitBuffer
from zserio.hashcode import (HASH_SEED, calc_hashcode_bool_array, calc_hashcode_int_array,
                             calc_hashcode_float32_array, calc_hashcode_float64_array,
                             calc_hashcode_string_array, calc_hashcode_object_array)
from zserio.exception import PythonRuntimeException

class Array:
    """
    Abstraction for arrays to which Zserio arrays are mapped in python.
    """

    def __init__(self,
                 array_traits: typing.Any,
                 raw_array: typing.Optional[typing.List] = None,
                 *,
                 is_auto: bool = False,
                 is_implicit: bool = False,
                 set_offset_method: typing.Optional[typing.Callable[[int, int], None]] = None,
                 check_offset_method: typing.Optional[typing.Callable[[int, int], None]] = None) -> None:
        """
        Constructor.

        :param array_traits: Array traits which specify the array type.
        :param raw_array: Native python list which will be hold by this abstraction.
        :param is_auto: True if mapped Zserio array is auto array.
        :param is_implicit: True if mapped Zserio array is implicit array.
        :param set_offset_method:  Set offset method if mapped Zserio array is indexed offset array.
        :param check_offset_method: Check offset method if mapped Zserio array is indexed offset array.
        """

        self._raw_array: typing.List = [] if raw_array is None else raw_array
        self._array_traits: typing.Any = array_traits
        self._packed_array_traits : typing.Any = array_traits.packed_traits
        self._is_auto: bool = is_auto
        self._is_implicit: bool = is_implicit
        self._set_offset_method: typing.Optional[typing.Callable[[int, int], None]] = set_offset_method
        self._check_offset_method: typing.Optional[typing.Callable[[int, int], None]] = check_offset_method

    @classmethod
    def from_reader(cls: typing.Type['Array'],
                    array_traits: typing.Any,
                    reader: BitStreamReader,
                    size: int = 0,
                    *,
                    is_auto: bool = False,
                    is_implicit: bool = False,
                    set_offset_method: typing.Optional[typing.Callable[[int, int], None]] = None,
                    check_offset_method: typing.Optional[typing.Callable[[int, int], None]] = None) -> 'Array':
        """
        Constructs array and reads elements from the given bit stream reader.

        :param array_traits: Array traits which specify the array type.
        :param reader: Bit stream from which to read.
        :param size: Number of elements to read or None in case of implicit or auto arrays.
        :param raw_array: Native python list which will be hold by this abstraction.
        :param is_auto: True if mapped Zserio array is auto array.
        :param is_implicit: True if mapped Zserio array is implicit array.
        :param set_offset_method:  Set offset method if mapped Zserio array is indexed offset array.
        :param check_offset_method: Check offset method if mapped Zserio array is indexed offset array.
        :returns: Array instance filled using given bit stream reader.
        """

        instance = cls(array_traits, is_auto=is_auto, is_implicit=is_implicit,
                       set_offset_method=set_offset_method, check_offset_method=check_offset_method)
        instance.read(reader, size)

        return instance

    @classmethod
    def from_reader_packed(
            cls: typing.Type['Array'],
            array_traits: typing.Any,
            reader: BitStreamReader,
            size: int = 0,
            *,
            is_auto: bool = False,
            set_offset_method: typing.Optional[typing.Callable[[int, int], None]] = None,
            check_offset_method:
            typing.Optional[typing.Callable[[int, int], None]] = None) -> 'Array':
        """
        Constructs packed array and reads elements from the given bit stream reader.

        :param array_traits: Array traits which specify the array type.
        :param reader: Bit stream from which to read.
        :param size: Number of elements to read or None in case of implicit or auto arrays.
        :param raw_array: Native python list which will be hold by this abstraction.
        :param is_auto: True if mapped Zserio array is auto array.
        :param set_offset_method:  Set offset method if mapped Zserio array is indexed offset array.
        :param check_offset_method: Check offset method if mapped Zserio array is indexed offset array.
        :returns: Array instance filled using given bit stream reader.
        """

        instance = cls(array_traits, is_auto=is_auto, is_implicit=False, set_offset_method=set_offset_method,
                       check_offset_method=check_offset_method)
        instance.read_packed(reader, size)

        return instance

    def __eq__(self, other: object) -> bool:
        # it's enough to check only raw_array because compound types which call this are always the same type
        if isinstance(other, Array):
            return self._raw_array == other._raw_array

        return False

    def __hash__(self) -> int:
        return self._array_traits.CALC_HASHCODE_FUNC(HASH_SEED, self._raw_array)

    def __len__(self) -> int:
        return len(self._raw_array)

    def __getitem__(self, key: int) -> typing.Any:
        return self._raw_array[key]

    def __setitem__(self, key: int, value: typing.Any) -> None:
        self._raw_array[key] = value

    @property
    def raw_array(self) -> typing.List:
        """
        Gets raw array.

        :returns: Native python list which is hold by the array.
        """

        return self._raw_array

    def bitsizeof(self, bitposition: int) -> int:
        """
        Returns length of array stored in the bit stream in bits.

        :param bitposition: Current bit stream position.
        :returns: Length of the array stored in the bit stream in bits.
        """

        end_bitposition = bitposition
        size = len(self._raw_array)
        if self._is_auto:
            end_bitposition += bitsizeof_varsize(size)

        if self._array_traits.HAS_BITSIZEOF_CONSTANT and size > 0:
            element_size = self._array_traits.bitsizeof()
            if self._set_offset_method is None:
                end_bitposition += size * element_size
            else:
                end_bitposition = alignto(8, end_bitposition)
                end_bitposition += element_size + (size - 1) * alignto(8, element_size)
        else:
            for element in self._raw_array:
                if self._set_offset_method is not None:
                    end_bitposition = alignto(8, end_bitposition)
                if self._array_traits.NEEDS_BITSIZEOF_POSITION:
                    end_bitposition += self._array_traits.bitsizeof(end_bitposition, element)
                else:
                    end_bitposition += self._array_traits.bitsizeof(element)

        return end_bitposition - bitposition

    def bitsizeof_packed(self, bitposition: int) -> int:
        """
        Returns length of the packed array stored in the bit stream in bits.

        :param bitposition: Current bit stream position.
        :returns: Length of the array stored in the bit stream in bits.
        """

        end_bitposition = bitposition
        size = len(self._raw_array)
        if self._is_auto:
            end_bitposition += bitsizeof_varsize(size)

        if size > 0:
            context_node = self._packed_array_traits.create_context()
            for element in self._raw_array:
                self._packed_array_traits.init_context(context_node, element)

            for element in self._raw_array:
                if self._set_offset_method is not None:
                    end_bitposition = alignto(8, end_bitposition)
                end_bitposition += self._packed_array_traits.bitsizeof(context_node, end_bitposition, element)

        return end_bitposition - bitposition

    def initialize_offsets(self, bitposition: int) -> int:
        """
        Initializes indexed offsets for the array.

        :param bitposition: Current bit stream position.
        :returns: Updated bit stream position which points to the first bit after the array.
        """

        end_bitposition = bitposition
        size = len(self._raw_array)
        if self._is_auto:
            end_bitposition += bitsizeof_varsize(size)

        for index in range(size):
            if self._set_offset_method is not None:
                end_bitposition = alignto(8, end_bitposition)
                self._set_offset_method(index, end_bitposition)
            end_bitposition = self._array_traits.initialize_offsets(end_bitposition, self._raw_array[index])

        return end_bitposition

    def initialize_offsets_packed(self, bitposition: int) -> int:
        """
        Initializes indexed offsets for the packed array.

        :param bitposition: Current bit stream position.
        :returns: Updated bit stream position which points to the first bit after the array.
        """

        end_bitposition = bitposition
        size = len(self._raw_array)
        if self._is_auto:
            end_bitposition += bitsizeof_varsize(size)

        if size > 0:
            context_node = self._packed_array_traits.create_context()
            for element in self._raw_array:
                self._packed_array_traits.init_context(context_node, element)

            for index in range(size):
                if self._set_offset_method is not None:
                    end_bitposition = alignto(8, end_bitposition)
                    self._set_offset_method(index, end_bitposition)
                end_bitposition = self._packed_array_traits.initialize_offsets(context_node, end_bitposition,
                                                                               self._raw_array[index])

        return end_bitposition

    def read(self, reader: BitStreamReader, size: int = 0) -> None:
        """
        Reads array from the bit stream.

        :param reader: Bit stream from which to read.
        :param size: Number of elements to read or None in case of implicit or auto arrays.

        :raises PythonRuntimeException: If the implicit array does not have elements with constant bit size.
        """

        self._raw_array.clear()

        if self._is_implicit:
            if not self._array_traits.HAS_BITSIZEOF_CONSTANT:
                raise PythonRuntimeException("Array: Implicit array elements must have constant bit size!")

            element_size = self._array_traits.bitsizeof()
            remaining_bits = reader.buffer_bitsize - reader.bitposition
            read_size = remaining_bits // element_size
            for index in range(read_size):
                # we know that no traits NEEDS_READ_INDEX here
                self._raw_array.append(self._array_traits.read(reader))
        else:
            if self._is_auto:
                read_size = reader.read_varsize()
            else:
                read_size = size

            for index in range(read_size):
                if self._check_offset_method is not None:
                    reader.alignto(8)
                    self._check_offset_method(index, reader.bitposition)
                if self._array_traits.NEEDS_READ_INDEX:
                    self._raw_array.append(self._array_traits.read(reader, index))
                else:
                    self._raw_array.append(self._array_traits.read(reader))

    def read_packed(self, reader: BitStreamReader, size: int = 0) -> None:
        """
        Reads packed array from the bit stream.

        :param reader: Bit stream from which to read.
        :param size: Number of elements to read or 0 in case of auto arrays.
        """

        self._raw_array.clear()

        if self._is_implicit:
            raise PythonRuntimeException("Array: Implicit array cannot be packed!")

        if self._is_auto:
            read_size = reader.read_varsize()
        else:
            read_size = size

        if read_size > 0:
            context_node = self._packed_array_traits.create_context()

            for index in range(read_size):
                if self._check_offset_method is not None:
                    reader.alignto(8)
                    self._check_offset_method(index, reader.bitposition)
                self._raw_array.append(self._packed_array_traits.read(context_node, reader, index))

    def write(self, writer: BitStreamWriter) -> None:
        """
        Writes array to the bit stream.

        :param writer: Bit stream where to write.
        """

        size = len(self._raw_array)
        if self._is_auto:
            writer.write_varsize(size)

        for index in range(size):
            if self._check_offset_method is not None:
                writer.alignto(8)
                self._check_offset_method(index, writer.bitposition)
            self._array_traits.write(writer, self._raw_array[index])

    def write_packed(self, writer: BitStreamWriter) -> None:
        """
        Writes packed array to the bit stream.

        :param writer: Bit stream where to write.
        """

        size = len(self._raw_array)

        if self._is_auto:
            writer.write_varsize(size)

        if size > 0:
            context_node = self._packed_array_traits.create_context()
            for element in self._raw_array:
                self._packed_array_traits.init_context(context_node, element)

            for index in range(size):
                if self._check_offset_method is not None:
                    writer.alignto(8)
                    self._check_offset_method(index, writer.bitposition)
                self._packed_array_traits.write(context_node, writer, self._raw_array[index])

class DeltaContext:
    """
    Context for delta packing created for each packable field.

    Contexts are always newly created for each array operation (bitsizeof, initialize_offsets, read, write).
    They must be initialized at first via calling the init method for each packable element present in the
    array. After the full initialization, only a single method (bitsizeof, read, write) can be repeatedly
    called for exactly the same sequence of packable elements.

    Example::

        context = DeltaContext(array_traits)
        for element in array:
            context.init(element) # initialization step, needed for max_bit_number calculation
        context.write_descriptor(writer) # finishes the initialization
        for element in array:
            context.write(writer, element)
    """

    def __init__(self) -> None:
        """ Constructor. """

        self._is_packed = False
        self._max_bit_number = 0
        self._previous_element: typing.Optional[int] = None
        self._processing_started = False
        self._unpacked_bitsize = 0
        self._first_element_bitsize = 0
        self._num_elements = 0

    def init(self, array_traits: typing.Any, element: int) -> None:
        """
        Makes initialization step for the provided array element.

        :param array_traits: Standard array traits.
        :param element: Current element of the array.
        """

        self._num_elements += 1
        self._unpacked_bitsize += DeltaContext._bitsizeof_unpacked(array_traits, element)

        if self._previous_element is None:
            self._previous_element = element
            self._first_element_bitsize = self._unpacked_bitsize
        else:
            if self._max_bit_number <= self._MAX_BIT_NUMBER_LIMIT:
                self._is_packed = True

                delta = element - self._previous_element
                max_bit_number = delta.bit_length()
                # if delta is negative, we need one bit more because of sign
                # if delta is positive, we need one bit more because delta are treated as signed number
                # if delta is zero, we need one bit more because bit_length() returned zero
                if max_bit_number > self._max_bit_number:
                    self._max_bit_number = max_bit_number
                    if max_bit_number > self._MAX_BIT_NUMBER_LIMIT:
                        self._is_packed = False

                self._previous_element = element

    def bitsizeof(self, array_traits: typing.Any, element: int) -> int:
        """
        Returns length of the element representation stored in the bit stream in bits.

        :param array_traits: Standard integral array traits.
        :param element: Current element.

        :returns: Length of the element representation stored in the bit stream in bits.
        """

        if not self._processing_started:
            self._processing_started = True
            self._finish_init()

            return self._bitsizeof_descriptor() + DeltaContext._bitsizeof_unpacked(array_traits, element)
        elif not self._is_packed:
            return DeltaContext._bitsizeof_unpacked(array_traits, element)
        else:
            return self._max_bit_number + 1 if self._max_bit_number > 0 else 0

    def read(self, array_traits: typing.Any, reader: BitStreamReader) -> int:
        """
        Reads the packed element from the bit stream.

        :param array_traits: Standard array traits.
        :param reader: Bit stream reader.
        """

        if not self._processing_started:
            self._processing_started = True
            self._read_descriptor(reader)

            return self._read_unpacked(array_traits, reader)
        elif not self._is_packed:
            return self._read_unpacked(array_traits, reader)
        else:
            assert self._previous_element is not None
            if self._max_bit_number > 0:
                delta = reader.read_signed_bits(self._max_bit_number + 1)
                self._previous_element += delta
            return self._previous_element

    def write(self, array_traits: typing.Any, writer: BitStreamWriter, element: int) -> None:
        """
        Writes the packed element representation to the bit stream.

        :param array_traits: Standard array traits.
        :param writer: Bit stream writer.
        :param element: Element to write.
        """

        if not self._processing_started:
            self._processing_started = True
            self._finish_init()
            self._write_descriptor(writer)

            self._write_unpacked(array_traits, writer, element)
        elif not self._is_packed:
            self._write_unpacked(array_traits, writer, element)
        else: # packed and not first
            assert self._previous_element is not None
            if self._max_bit_number > 0:
                delta = element - self._previous_element
                writer.write_signed_bits(delta, self._max_bit_number + 1)
                self._previous_element = element

    def _finish_init(self) -> None:
        if self._is_packed:
            delta_bitsize = self._max_bit_number + 1 if self._max_bit_number > 0 else 0
            packed_bitsize_with_descriptor = (
                1 + self._MAX_BIT_NUMBER_BITS + # descriptor
                self._first_element_bitsize + (self._num_elements - 1) * delta_bitsize
            )
            unpacked_bitsize_with_descriptor = 1 + self._unpacked_bitsize
            if packed_bitsize_with_descriptor >= unpacked_bitsize_with_descriptor:
                self._is_packed = False

    def _bitsizeof_descriptor(self) -> int:
        if self._is_packed:
            return 1 + self._MAX_BIT_NUMBER_BITS
        else:
            return 1

    @staticmethod
    def _bitsizeof_unpacked(array_traits : typing.Any, element : int) -> int:
        if array_traits.HAS_BITSIZEOF_CONSTANT:
            return array_traits.bitsizeof()
        else: # we know that NEEDS_BITSIZEOF_POSITION is False here
            return array_traits.bitsizeof(element)

    def _read_descriptor(self, reader: BitStreamReader) -> None:
        self._is_packed = reader.read_bool()
        if self._is_packed:
            self._max_bit_number = reader.read_bits(self._MAX_BIT_NUMBER_BITS)

    def _read_unpacked(self, array_traits: typing.Any, reader: BitStreamReader) -> int:
        element = array_traits.read(reader)
        self._previous_element = element
        return element

    def _write_descriptor(self, writer: BitStreamWriter) -> None:
        writer.write_bool(self._is_packed)
        if self._is_packed:
            writer.write_bits(self._max_bit_number, self._MAX_BIT_NUMBER_BITS)

    def _write_unpacked(self, array_traits: typing.Any, writer: BitStreamWriter, element: int) -> None:
        self._previous_element = element
        array_traits.write(writer, element)

    _MAX_BIT_NUMBER_BITS = 6
    _MAX_BIT_NUMBER_LIMIT = 62

class PackingContextNode:
    """
    Packing context node which allows to keep packing contexts in a tree which corresponds with
    the user defined Zserio tree.
    """

    def __init__(self) -> None:
        """
        Constructor.

        :param children: Child context nodes.
        :param context: Packing context in case of leaf node.
        """

        self._children: typing.List['PackingContextNode'] = []
        self._context: typing.Optional[DeltaContext] = None

    def create_child(self) -> 'PackingContextNode':
        """
        Creates a new child.

        :returns: The child which was just created.
        """
        self._children.append(PackingContextNode())
        return self._children[-1]

    @property
    def children(self) -> typing.List['PackingContextNode']:
        """
        Gets children of the current node.

        :returns: Child context nodes.
        """

        return self._children

    def create_context(self) -> None:
        """
        Creates a new packing context within the current node.
        """

        self._context = DeltaContext()

    @property
    def context(self) -> DeltaContext:
        """
        Gets packing context for leaf nodes.

        :returns: Packing context for leafs, None otherwise.
        :raises PythonRuntimeException: When called on a node which is not leaf.
        """

        if self._context is None:
            raise PythonRuntimeException("PackingContextNode is not a leaf!")
        return self._context

class PackedArrayTraits:
    """
    Packed array traits.

    Packed array traits are used for all built-in types.
    """

    def __init__(self, array_traits: typing.Any) -> None:
        """
        Constructor.

        :param array_traits: Standard array traits.
        """

        self._array_traits = array_traits

    @staticmethod
    def create_context() -> PackingContextNode:
        """
        Creates packing context.

        :returns: List of packing contexts.
        """

        packing_context_node = PackingContextNode()
        packing_context_node.create_context()
        return packing_context_node

    def init_context(self, context_node: PackingContextNode, element: int) -> None:
        """
        Calls context initialization step for the current element.

        :param context_node: Packing context node.
        :param element: Current element.
        """

        context = context_node.context
        context.init(self._array_traits, element)

    def bitsizeof(self, context_node: PackingContextNode, _bitposition: int, element: int) -> int:
        """
        Returns length of the array element stored in the bit stream in bits.

        :param context_node: Packing context node.
        :param _bitposition: Current bit stream position (not used).
        :param elemnet: Current element.
        :returns: Length of the array element stored in the bit stream in bits.
        """

        context = context_node.context
        return context.bitsizeof(self._array_traits, element)

    def initialize_offsets(self, context_node: PackingContextNode, bitposition: int, element: int) -> int:
        """
        Calls indexed offsets initialization for the current element.

        :param context_node: Packing context node.
        :param _bitposition: Current bit stream position.
        :param element: Current element.
        :returns: Updated bit stream position which points to the first bit after this element.
        """

        context = context_node.context
        return bitposition + context.bitsizeof(self._array_traits, element)

    def write(self, context_node: PackingContextNode, writer: BitStreamWriter, element: int) -> None:
        """
        Writes the element to the bit stream.

        :param context_node: Packing context node.
        :param writer: Bit stream writer.
        :param element: Element to write.
        """

        context = context_node.context
        context.write(self._array_traits, writer, element)

    def read(self, context_node: PackingContextNode, reader: BitStreamReader, _index: int) -> int:
        """
        Read an element from the bit stream.

        :param context_node: Packing context node.
        :param reader: Bit stream reader.
        :param _index: Not used.
        :returns: Read element value.
        """

        context = context_node.context
        return context.read(self._array_traits, reader)

class ObjectPackedArrayTraits:
    """
    Packed array traits for Zserio objects.

    This traits are used for Zserio objects which must implement special *_packed* methods to allow itself
    to be used in a packed array.
    """

    def __init__(self, packed_object_creator: typing.Callable[[PackingContextNode, BitStreamReader, int],
                                                              typing.Any],
                 packing_context_creator: typing.Callable[[PackingContextNode], None]):
        """
        Constructor.

        :param packed_object_creator: Creator which creates packed object from the element index.
        :param packing_context_creator: Creator which creates contexts for object packing.
        """

        self._packed_object_creator = packed_object_creator
        self._packing_context_creator = packing_context_creator

    def create_context(self) -> PackingContextNode:
        """
        Creates packing context.

        :returns: List of delta packing contexts.
        """

        packing_context_node = PackingContextNode()
        self._packing_context_creator(packing_context_node)
        return packing_context_node

    @staticmethod
    def init_context(context_node: PackingContextNode, element: typing.Any) -> None:
        """
        Calls context initialization step for the current element.

        :param context_node: Packing context node.
        :param element: Current element.
        """

        element.init_packing_context(context_node)

    @staticmethod
    def bitsizeof(context_node: PackingContextNode, bitposition: int, element: typing.Any) -> int:
        """
        Returns length of the array element stored in the bit stream in bits.

        :param context_node: Packing context node.
        :param bitposition: Current bit stream position.
        :param elemnet: Current element.
        :returns: Length of the array element stored in the bit stream in bits.
        """

        return element.bitsizeof_packed(context_node, bitposition)

    @staticmethod
    def initialize_offsets(context_node: PackingContextNode,
                           bitposition: int, element: typing.Any) -> int:
        """
        Calls indexed offsets initialization for the current element.

        :param context_node: Packing context node.
        :param bitposition: Current bit stream position.
        :param element: Current element.
        :returns: Updated bit stream position which points to the first bit after this element.
        """

        return element.initialize_offsets_packed(context_node, bitposition)

    @staticmethod
    def write(context_node: PackingContextNode, writer: BitStreamWriter, element: typing.Any) -> None:
        """
        Writes the element to the bit stream.

        :param context_node: Packing context node.
        :param writer: Bit stream writer.
        :param element: Element to write.
        """

        element.write_packed(context_node, writer)

    def read(self, context_node: PackingContextNode, reader: BitStreamReader, index: int) -> typing.Any:
        """
        Read an element from the bit stream.

        :param context_node: Packing context node.
        :param reader: Bit stream reader.
        :param index: Index of the current element.
        :returns: Read element value.
        """

        return self._packed_object_creator(context_node, reader, index)

class BitFieldArrayTraits:
    """
    Array traits for unsigned fixed integer Zserio types (uint16, uint32, uint64, bit:5, etc...).
    """

    HAS_BITSIZEOF_CONSTANT = True
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_int_array)

    def __init__(self, numbits: int) -> None:
        """
        Constructor.

        :param numbits: Number of bits for unsigned fixed integer Zserio type.
        """

        self._numbits = numbits

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    def bitsizeof(self) -> int:
        """
        Returns length of unsigned fixed integer Zserio type stored in the bit stream in bits.

        :returns: Length of unsigned fixed integer Zserio type in bits.
        """

        return self._numbits

    def initialize_offsets(self, bitposition: int, _value: int) -> int:
        """
        Initializes indexed offsets for unsigned fixed integer Zserio type.

        :param bitposition: Current bit stream position.
        :param _value: Not used.
        :returns: Updated bit stream position which points to the first bit after unsigned fixed integer type.
        """

        return bitposition + self.bitsizeof()

    def read(self, reader: BitStreamReader) -> int:
        """
        Reads unsigned fixed integer Zserio type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_bits(self._numbits)

    def write(self, writer: BitStreamWriter, value: int) -> None:
        """
        Writes unsigned fixed integer Zserio type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Unsigned fixed integer Zserio type to write.
        """

        writer.write_bits(value, self._numbits)

class SignedBitFieldArrayTraits:
    """
    Array traits for signed fixed integer Zserio types (int16, int32, int64, int:5, etc...).
    """

    HAS_BITSIZEOF_CONSTANT = True
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_int_array)

    def __init__(self, numbits: int) -> None:
        """
        Constructor.

        :param numbits: Number of bits for signed fixed integer Zserio type.
        """

        self._numbits = numbits

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    def bitsizeof(self) -> int:
        """
        Returns length of signed fixed integer Zserio type stored in the bit stream in bits.

        :returns: Length of signed fixed integer Zserio type in bits.
        """

        return self._numbits

    def initialize_offsets(self, bitposition: int, _value: int) -> int:
        """
        Initializes indexed offsets for signed fixed integer Zserio type.

        :param bitposition: Current bit stream position.
        :param _value: Not used.
        :returns: Updated bit stream position which points to the first bit after signed fixed integer type.
        """

        return bitposition + self.bitsizeof()

    def read(self, reader: BitStreamReader) -> int:
        """
        Reads signed fixed integer Zserio type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_signed_bits(self._numbits)

    def write(self, writer: BitStreamWriter, value: int) -> None:
        """
        Writes signed fixed integer Zserio type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Signed fixed integer Zserio type to write.
        """

        writer.write_signed_bits(value, self._numbits)

class VarUInt16ArrayTraits:
    """
    Array traits for Zserio varuint16 type.
    """

    HAS_BITSIZEOF_CONSTANT = False
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_int_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof(value: int) -> int:
        """
        Returns length of Zserio varuint16 type stored in the bit stream in bits.

        :param value: Zserio varuint16 type value.
        :returns: Length of given Zserio varuint16 type in bits.
        """

        return bitsizeof_varuint16(value)

    @staticmethod
    def initialize_offsets(bitposition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varuint16 type.

        :param bitposition: Current bit stream position.
        :param value: Zserio varuint16 type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varuint16 type.
        """

        return bitposition + VarUInt16ArrayTraits.bitsizeof(value)

    @staticmethod
    def read(reader: BitStreamReader) -> int:
        """
        Reads Zserio varuint16 type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_varuint16()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varuint16 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varuint16 type to write.
        """

        writer.write_varuint16(value)

class VarUInt32ArrayTraits:
    """
    Array traits for Zserio varuint32 type.
    """

    HAS_BITSIZEOF_CONSTANT = False
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_int_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof(value: int) -> int:
        """
        Returns length of Zserio varuint32 type stored in the bit stream in bits.

        :param value: Zserio varuint32 type value.
        :returns: Length of given Zserio varuint32 type in bits.
        """

        return bitsizeof_varuint32(value)

    @staticmethod
    def initialize_offsets(bitposition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varuint32 type.

        :param bitposition: Current bit stream position.
        :param value: Zserio varuint32 type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varuint32 type.
        """

        return bitposition + VarUInt32ArrayTraits.bitsizeof(value)

    @staticmethod
    def read(reader: BitStreamReader) -> int:
        """
        Reads Zserio varuint32 type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_varuint32()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varuint32 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varuint32 type to write.
        """

        writer.write_varuint32(value)

class VarUInt64ArrayTraits:
    """
    Array traits for Zserio varuint64 type.
    """

    HAS_BITSIZEOF_CONSTANT = False
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_int_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof(value: int) -> int:
        """
        Returns length of Zserio varuint64 type stored in the bit stream in bits.

        :param value: Zserio varuint64 type value.
        :returns: Length of given Zserio varuint64 type in bits.
        """

        return bitsizeof_varuint64(value)

    @staticmethod
    def initialize_offsets(bitposition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varuint64 type.

        :param value: Zserio varuint64 type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varuint64 type.
        """

        return bitposition + VarUInt64ArrayTraits.bitsizeof(value)

    @staticmethod
    def read(reader: BitStreamReader) -> int:
        """
        Reads Zserio varuint64 type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_varuint64()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varuint64 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varuint64 type to write.
        """

        writer.write_varuint64(value)

class VarUIntArrayTraits:
    """
    Array traits for Zserio varuint type.
    """

    HAS_BITSIZEOF_CONSTANT = False
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_int_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof(value: int) -> int:
        """
        Returns length of Zserio varuint type stored in the bit stream in bits.

        :param value: Zserio varuint type value.
        :returns: Length of given Zserio varuint type in bits.
        """

        return bitsizeof_varuint(value)

    @staticmethod
    def initialize_offsets(bitposition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varuint type.

        :param bitposition: Current bit stream position.
        :param value: Zserio varuint type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varuint type.
        """

        return bitposition + VarUIntArrayTraits.bitsizeof(value)

    @staticmethod
    def read(reader: BitStreamReader) -> int:
        """
        Reads Zserio varuint type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_varuint()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varuint type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varuint type to write.
        """

        writer.write_varuint(value)

class VarSizeArrayTraits:
    """
    Array traits for Zserio varsize type.
    """

    HAS_BITSIZEOF_CONSTANT = False
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_int_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof(value: int) -> int:
        """
        Returns length of Zserio varsize type stored in the bit stream in bits.

        :param value: Zserio varsize type value.
        :returns: Length of given Zserio varsize type in bits.
        """

        return bitsizeof_varsize(value)

    @staticmethod
    def initialize_offsets(bitposition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varsize type.

        :param bitposition: Current bit stream position.
        :param value: Zserio varsize type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varsize type.
        """

        return bitposition + VarSizeArrayTraits.bitsizeof(value)

    @staticmethod
    def read(reader: BitStreamReader) -> int:
        """
        Reads Zserio varsize type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_varsize()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varsize type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varsize type to write.
        """

        writer.write_varsize(value)

class VarInt16ArrayTraits:
    """
    Array traits for Zserio varint16 type.
    """

    HAS_BITSIZEOF_CONSTANT = False
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_int_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof(value: int) -> int:
        """
        Returns length of Zserio varint16 type stored in the bit stream in bits.

        :param value: Zserio varint16 type value.
        :returns: Length of given Zserio varint16 type in bits.
        """

        return bitsizeof_varint16(value)

    @staticmethod
    def initialize_offsets(bitposition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varint16 type.

        :param bitposition: Current bit stream position.
        :param value: Zserio varint16 type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varint16 type.
        """

        return bitposition + VarInt16ArrayTraits.bitsizeof(value)

    @staticmethod
    def read(reader: BitStreamReader) -> int:
        """
        Reads Zserio varint16 type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_varint16()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varint16 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varint16 type to write.
        """

        writer.write_varint16(value)

class VarInt32ArrayTraits:
    """
    Array traits for Zserio varint32 type.
    """

    HAS_BITSIZEOF_CONSTANT = False
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_int_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof(value: int) -> int:
        """
        Returns length of Zserio varint32 type stored in the bit stream in bits.

        :param value: Zserio varint32 type value.
        :returns: Length of given Zserio varint32 type in bits.
        """

        return bitsizeof_varint32(value)

    @staticmethod
    def initialize_offsets(bitposition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varint32 type.

        :param bitposition: Current bit stream position.
        :param value: Zserio varint32 type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varint32 type.
        """

        return bitposition + VarInt32ArrayTraits.bitsizeof(value)

    @staticmethod
    def read(reader: BitStreamReader) -> int:
        """
        Reads Zserio varint32 type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_varint32()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varint32 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varint32 type to write.
        """

        writer.write_varint32(value)

class VarInt64ArrayTraits:
    """
    Array traits for Zserio varint64 type.
    """

    HAS_BITSIZEOF_CONSTANT = False
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_int_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof(value: int) -> int:
        """
        Returns length of Zserio varint64 type stored in the bit stream in bits.

        :param value: Zserio varint64 type value.
        :returns: Length of given Zserio varint64 type in bits.
        """

        return bitsizeof_varint64(value)

    @staticmethod
    def initialize_offsets(bitposition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varint64 type.

        :param bitposition: Current bit stream position.
        :param value: Zserio varint64 type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varint64 type.
        """

        return bitposition + VarInt64ArrayTraits.bitsizeof(value)

    @staticmethod
    def read(reader: BitStreamReader) -> int:
        """
        Reads Zserio varint64 type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_varint64()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varint64 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varint64 type to write.
        """

        writer.write_varint64(value)

class VarIntArrayTraits:
    """
    Array traits for Zserio varint type.
    """

    HAS_BITSIZEOF_CONSTANT = False
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_int_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof(value: int) -> int:
        """
        Returns length of Zserio varint type stored in the bit stream in bits.

        :param value: Zserio varint type value.
        :returns: Length of given Zserio varint type in bits.
        """

        return bitsizeof_varint(value)

    @staticmethod
    def initialize_offsets(bitposition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varint type.

        :param bitposition: Current bit stream position.
        :param value: Zserio varint type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varint type.
        """

        return bitposition + VarIntArrayTraits.bitsizeof(value)

    @staticmethod
    def read(reader: BitStreamReader) -> int:
        """
        Reads Zserio varint type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_varint()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varint type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varint type to write.
        """

        writer.write_varint(value)

class Float16ArrayTraits:
    """
    Array traits for Zserio float16 type.
    """

    HAS_BITSIZEOF_CONSTANT = True
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_float32_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof() -> int:
        """
        Returns length of Zserio float16 type stored in the bit stream in bits.

        :returns: Length of Zserio float16 type in bits.
        """

        return 16

    @staticmethod
    def initialize_offsets(bitposition: int, _value: float) -> int:
        """
        Initializes indexed offsets for Zserio float16 type.

        :param bitposition: Current bit stream position.
        :param _value: Not used.
        :returns: Updated bit stream position which points to the first bit after Zserio float16 type.
        """

        return bitposition + Float16ArrayTraits.bitsizeof()

    @staticmethod
    def read(reader: BitStreamReader) -> float:
        """
        Reads Zserio float16 type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_float16()

    @staticmethod
    def write(writer: BitStreamWriter, value: float) -> None:
        """
        Writes Zserio float16 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio float16 type to write.
        """

        writer.write_float16(value)

class Float32ArrayTraits:
    """
    Array traits for Zserio float32 type.
    """

    HAS_BITSIZEOF_CONSTANT = True
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_float32_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof() -> int:
        """
        Returns length of Zserio float32 type stored in the bit stream in bits.

        :returns: Length of Zserio float32 type in bits.
        """

        return 32

    @staticmethod
    def initialize_offsets(bitposition: int, _value: float) -> int:
        """
        Initializes indexed offsets for Zserio float32 type.

        :param bitposition: Current bit stream position.
        :param _value: Not used.
        :returns: Updated bit stream position which points to the first bit after Zserio float32 type.
        """

        return bitposition + Float32ArrayTraits.bitsizeof()

    @staticmethod
    def read(reader: BitStreamReader) -> float:
        """
        Reads Zserio float32 type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_float32()

    @staticmethod
    def write(writer: BitStreamWriter, value: float) -> None:
        """
        Writes Zserio float32 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio float32 type to write.
        """

        writer.write_float32(value)

class Float64ArrayTraits:
    """
    Array traits for Zserio float64 type.
    """

    HAS_BITSIZEOF_CONSTANT = True
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_float64_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof() -> int:
        """
        Returns length of Zserio float64 type stored in the bit stream in bits.

        :returns: Length of Zserio float64 type in bits.
        """

        return 64

    @staticmethod
    def initialize_offsets(bitposition: int, _value: float) -> int:
        """
        Initializes indexed offsets for Zserio float64 type.

        :param bitposition: Current bit stream position.
        :param _value: Not used.
        :returns: Updated bit stream position which points to the first bit after Zserio float64 type.
        """

        return bitposition + Float64ArrayTraits.bitsizeof()

    @staticmethod
    def read(reader: BitStreamReader) -> float:
        """
        Reads Zserio float64 type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_float64()

    @staticmethod
    def write(writer: BitStreamWriter, value: float) -> None:
        """
        Writes Zserio float64 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio float64 type to write.
        """

        writer.write_float64(value)

class StringArrayTraits:
    """
    Array traits for Zserio string type.
    """

    HAS_BITSIZEOF_CONSTANT = False
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_string_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof(value: str) -> int:
        """
        Returns length of Zserio string type stored in the bit stream in bits.

        :param value: Zserio string type value.
        :returns: Length of given Zserio string type in bits.
        """

        return bitsizeof_string(value)

    @staticmethod
    def initialize_offsets(bitposition: int, value: str) -> int:
        """
        Initializes indexed offsets for Zserio string type.

        :param bitposition: Current bit stream position.
        :param value: Zserio string type value.
        :returns: Updated bit stream position which points to the first bit after Zserio string type.
        """

        return bitposition + StringArrayTraits.bitsizeof(value)

    @staticmethod
    def read(reader: BitStreamReader) -> str:
        """
        Reads Zserio string type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_string()

    @staticmethod
    def write(writer: BitStreamWriter, value: str) -> None:
        """
        Writes Zserio string type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio string type to write.
        """

        writer.write_string(value)

class BoolArrayTraits:
    """
    Array traits for Zserio bool type.
    """

    HAS_BITSIZEOF_CONSTANT = True
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_bool_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof() -> int:
        """
        Returns length of Zserio bool type stored in the bit stream in bits.

        :returns: Length of Zserio bool type in bits.
        """

        return 1

    @staticmethod
    def initialize_offsets(bitposition: int, _value: bool) -> int:
        """
        Initializes indexed offsets for Zserio bool type.

        :param bitposition: Current bit stream position.
        :param _value: Not used.
        :returns: Updated bit stream position which points to the first bit after Zserio bool type.
        """

        return bitposition + BoolArrayTraits.bitsizeof()

    @staticmethod
    def read(reader: BitStreamReader) -> bool:
        """
        Reads Zserio bool type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_bool()

    @staticmethod
    def write(writer: BitStreamWriter, value: bool) -> None:
        """
        Writes Zserio bool type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio bool type to write.
        """

        writer.write_bool(value)

class BitBufferArrayTraits:
    """
    Array traits for Zserio extern bit buffer type.
    """

    HAS_BITSIZEOF_CONSTANT = False
    NEEDS_BITSIZEOF_POSITION = False
    NEEDS_READ_INDEX = False
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_object_array)

    @property
    def packed_traits(self) -> PackedArrayTraits:
        """
        Gets packed array traits.

        :returns: PackedArrayTraits instance.
        """

        return PackedArrayTraits(self)

    @staticmethod
    def bitsizeof(value: BitBuffer) -> int:
        """
        Returns length of Zserio extern bit buffer type stored in the bit stream in bits.

        :param value: Zserio extern bit buffer type value.
        :returns: Length of given Zserio string type in bits.
        """

        return bitsizeof_bitbuffer(value)

    @staticmethod
    def initialize_offsets(bitposition: int, value: BitBuffer) -> int:
        """
        Initializes indexed offsets for Zserio extern bit buffer type.

        :param bitposition: Current bit stream position.
        :param value: Zserio extern bit buffer type value.
        :returns: Updated bit stream position which points to the first bit after Zserio extern bit buffer type.
        """

        return bitposition + BitBufferArrayTraits.bitsizeof(value)

    @staticmethod
    def read(reader: BitStreamReader) -> BitBuffer:
        """
        Reads Zserio extern bit buffer type from the bit stream.

        :param reader: Bit stream from which to read.
        """

        return reader.read_bitbuffer()

    @staticmethod
    def write(writer: BitStreamWriter, value: BitBuffer) -> None:
        """
        Writes Zserio extern bit buffer type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio extern bit buffer type to write.
        """

        writer.write_bitbuffer(value)

class ObjectArrayTraits:
    """
    Array traits for Zserio structure, choice, union and enum types.
    """

    HAS_BITSIZEOF_CONSTANT = False
    NEEDS_BITSIZEOF_POSITION = True
    NEEDS_READ_INDEX = True
    CALC_HASHCODE_FUNC = staticmethod(calc_hashcode_object_array)

    def __init__(self, object_creator: typing.Callable[[BitStreamReader, int], typing.Any],
                 packed_object_creator: typing.Callable[[PackingContextNode, BitStreamReader, int],
                                                         typing.Any],
                 packing_context_creator: typing.Callable[[PackingContextNode], None]) -> None:
        """
        Constructor.

        :param object_creator: Creator which creates object from the element index.
        """

        self._object_creator = object_creator
        self._packed_traits = ObjectPackedArrayTraits(packed_object_creator, packing_context_creator)

    @property
    def packed_traits(self) -> ObjectPackedArrayTraits:
        """
        Gets packed array traits.

        :returns: ObjectPackedArrayTraits instance.
        """

        return self._packed_traits

    @staticmethod
    def bitsizeof(bitposition: int, value: typing.Any) -> int:
        """
        Returns length of Zserio object type stored in the bit stream in bits.

        :param bitposition: Current bit position in bit stream.
        :param value: Zserio object type value.
        :returns: Length of given Zserio object type in bits.
        """

        return value.bitsizeof(bitposition)

    @staticmethod
    def initialize_offsets(bitposition: int, value: typing.Any) -> int:
        """
        Initializes indexed offsets for the Zserio object type.

        :param bitposition: Current bit stream position.
        :param value: Zserio object type value.
        :returns: Updated bit stream position which points to the first bit after the Zserio object type.
        """

        return value.initialize_offsets(bitposition)

    def read(self, reader: BitStreamReader, index: int) -> typing.Any:
        """
        Reads Zserio object type from the bit stream.

        :param reader: Bit stream from which to read.
        :param index: Element index in the array.
        """

        return self._object_creator(reader, index)

    @staticmethod
    def write(writer: BitStreamWriter, value: typing.Any) -> None:
        """
        Writes Zserio object type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio object type to write.
        """

        value.write(writer, zserio_call_initialize_offsets=False)
