package ambiguous_single_symbol_import_error;

import ambiguous_single_symbol_import_error.imported.CONST;

const uint32 CONST = 42;

struct SingleSymbolImportError
{
    uint32 value = CONST; // ambiguous CONST!
};
