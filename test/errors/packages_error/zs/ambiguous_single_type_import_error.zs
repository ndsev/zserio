package ambiguous_single_type_import_error;

import ambiguous_single_type_import_error.imported.Structure;

struct Structure
{
    string field;
};

struct SingleTypeImportError
{
    Structure field; // ambiguous!
};
