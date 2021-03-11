#!/bin/bash

${WORK_DIR}/redis-6.0.1/src/redis-server ${WORK_DIR}/redis-6.0.1/redis.conf | tee ${WORK_DIR}/logs/redis.log

cd ${WORK_DIR}
sleep 10

while :
do
    sleep 100
    # For Debugging, Docker should alive!
done
echo "redis services started ..."
