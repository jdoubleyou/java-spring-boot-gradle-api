#!/usr/bin/env bash

# Dynamo is set to be "running" in the health endpoint once we have actually hit it. Otherwise it is just "available".

echo "Listing Tables..."
awslocal dynamodb list-tables
