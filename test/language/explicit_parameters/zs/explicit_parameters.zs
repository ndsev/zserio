package explicit_parameters;

import explicit_parameters.explicit_blob_param.*;
import explicit_parameters.explicit_same_as_field.*;
import explicit_parameters.explicit_simple_param.*;
import explicit_parameters.multiple_explicit_params.*;

sql_database ExplicitParametersDb
{
    BlobParamTable      blobParamTable;
    SameAsFieldTable    sameAsFieldTable;
    SimpleParamTable    simpleParamTable;
    MultipleParamsTable multipleParamsTable;
};
