package package_with_module_clashing.package_with_service_clash_error;

import package_with_module_clashing.package_with_service_clash_error.clashing_name.some_package.*;

service ClashingName
{
    Empty clashingServiceMethod(Empty);
};
