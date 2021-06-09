#!/bin/sh

REDISID=`ps -ef | grep redis-server | grep -v grep | awk '{print $2}'`

echo "Redis Id : " $REDISID
if [ "" != "$REDISID" ]; then
   kill -9 $REDISID
   echo "Redis service killed.Starting a new one."
else
   echo "No Redis service killed.Starting a new one."
fi
sleep 5
${WORK_DIR}/redis-6.0.1/src/redis-server ${WORK_DIR}/redis-6.0.1/redis.conf | tee ${WORK_DIR}/logs/redis.log
echo "Redis service Started"

