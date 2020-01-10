package templates.instantiate_via_import.pkg;

// This checks old error in core which was looking for instantiate types in all imports recursively.
import templates.instantiate_via_import.*;

struct Test<T>
{
    T value;
};

instantiate Test<uint32> U32;
