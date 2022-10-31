package instantiate_type_imported_cycle_error;

import instantiate_type_imported_cycle_error.pkg.*;

// ok via full name
instantiate instantiate_type_imported_cycle_error.pkg.OtherTemplate<int32> OtherTemplate;

instantiate Template<uint32> Template;
