#!/bin/bash

PYTHONDONTWRITEBYTECODE=1 PYTHONPATH=../../../../distr/runtime_libs/python:. \
python zserio_service_http/http_server.py &
http_server_pid=$!

sleep 1

echo -en '\x00\x00\x00\x02' | \
curl -s -N --request POST --data-binary @- http://localhost:5000/SimpleService/powerOfTwo --output - | \
hexdump -v -e '/1 "\\x%02X"'; echo

kill $http_server_pid
