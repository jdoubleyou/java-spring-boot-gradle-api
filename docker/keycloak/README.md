```json

{
  "credentials": {
    "id": "some_uuid_here",
    "type": "password",
    "value": "user_password_here"
  }
}

```


```shell


#!/usr/bin/env bash

set -e

# jq is required

EXPORT_FILE_NAME="full_export.json"
TMP_FILE_NAME="tmp_${EXPORT_FILE_NAME}"
HOST_EXPORT_FOLDER="./docker/keycloak"

CONTAINER_ID=$(podman ps -aqf "name=java-spring-boot-gradle-api-keycloak-1")
printf "Looking for container: %s" "$CONTAINER_ID"

podman exec --interactive --tty "$CONTAINER_ID" bash -c "\/opt/keycloak/bin/kc.sh export --users realm_file --realm auth_realm_name_here --file /opt/keycloak/$TMP_FILE_NAME"
docker cp "$CONTAINER_ID":/opt/keycloak/"${TMP_FILE_NAME}" ./docker/keycloak/

jq "del(.authorizationSettings)" "$HOST_EXPORT_FOLDER"/"$TMP_FILE_NAME" > /dev/null
mv "$HOST_EXPORT_FOLDER"/"$TMP_FILE_NAME" "$HOST_EXPORT_FOLDER"/"$EXPORT_FILE_NAME"

printf "\nExport successfully written to %s" "$HOST_EXPORT_FOLDER"/"$EXPORT_FILE_NAME"

```