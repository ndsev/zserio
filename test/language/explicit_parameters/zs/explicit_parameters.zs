package explicit_parameters;

import explicit_parameters.explicit_bitmask_param.*;
import explicit_parameters.explicit_blob_param.*;
import explicit_parameters.explicit_enum_param.*;
import explicit_parameters.explicit_same_as_field.*;
import explicit_parameters.explicit_simple_param.*;
import explicit_parameters.multiple_explicit_params.*;
import explicit_parameters.multiple_with_same_name.*;

sql_database ExplicitParametersDb
{
    BitmaskParamTable           bitmaskParamTable;
    BlobParamTable              blobParamTable;
    EnumParamTable              enumParamTable;
    SameAsFieldTable            sameAsFieldTable;
    SimpleParamTable            simpleParamTable;
    MultipleParamsTable         multipleParamsTable;
    MultipleWithSameNameTable   multipleWithSameNameTable;
};
