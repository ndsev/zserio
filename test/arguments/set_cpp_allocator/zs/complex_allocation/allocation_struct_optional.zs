package complex_allocation.allocation_struct_optional;

struct AllocationStructOptional
{
    optional string             names[] : lengthof(names) > 0; // InPlaceOptionalHolder
    bool                        hasNext;
    AllocationStructOptional    others if hasNext : lengthof(others.names) > 0; // HeapOptionalHolder
};
