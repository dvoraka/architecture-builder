#!/usr/bin/env bash

PROJECTS=~/projects
SERVICE=budget-service

SERVICE_DIR=${PROJECTS}/architecture-builder/builder/${SERVICE}


if [ -d "${SERVICE_DIR}" ]
then
    if [ -d ${SERVICE} ]
    then
        echo -n "Removing existing $SERVICE directory... "
        rm -r ${SERVICE}
        echo "done"
    fi
    mv ${SERVICE_DIR} .
else
    echo "No service directory."
    exit 1
fi

cd ${SERVICE}

echo "Starting new service..."

./gradlew bootRun
