# Authorization Server For Local

...

```shell

curl http://localhost:8081/realms/custom_realm/protocol/openid-connect/token \
  -d client_id=test_client \
  -d client_secret=test_client_secret \
  -d username=test_user \
  -d password=test_user_password \
  -d grant_type=password -d scope="openid profile email roles" \
| jq .

```