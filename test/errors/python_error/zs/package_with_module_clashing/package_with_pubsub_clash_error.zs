package package_with_module_clashing.package_with_pubsub_clash_error;

import package_with_module_clashing.package_with_pubsub_clash_error.clashing_name.some_package.*;

pubsub ClashingName
{
    topic("clashing_pubsub") Empty clashingPubusubTopic;
};
