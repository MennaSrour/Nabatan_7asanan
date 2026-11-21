"""Build-only Firebase configuration: never connect CI to the production project."""
import json
from pathlib import Path

package = "com.menna.nabatan_7asena"
config = {
    "project_info": {
        "project_number": "123456789012",
        "project_id": "nabata-ci-placeholder",
        "storage_bucket": "nabata-ci-placeholder.appspot.com",
    },
    "client": [
        {
            "client_info": {
                "mobilesdk_app_id": f"1:123456789012:android:{index:032x}",
                "android_client_info": {"package_name": application_id},
            },
            "api_key": [{"current_key": "AIzaSyCIPlaceholderNotARealFirebaseAPIKey"}],
        }
        for index, application_id in enumerate((package, f"{package}.debug"), 1)
    ],
    "configuration_version": "1",
}
Path("app/google-services.json").write_text(json.dumps(config))
