package explicit_parameters;

import explicit_parameters.explicit_blob_param.*;
import explicit_parameters.explicit_simple_param.*;
import explicit_parameters.multiple_explicit_params.*;

sql_database ExplicitParametersDb
{
    BlobParamTable      blobParamTable;
    SimpleParamTable    simpleParamTable;
    MultipleParamsTable multipleParamsTable;
};
