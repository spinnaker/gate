#!/bin/sh

GATEID=`ps -ef | grep java | grep -v grep | awk '{print $2}'`

echo "Gate Id : " $GATEID
if [ "" != "$GATEID" ]; then
  kill -9 $GATEID
  echo "Gate service killed.Starting a new one."
else
  echo "No Gate service.Starting a new one."
fi
sleep 5
${WORK_DIR}/gate/bin/gate | tee ${WORK_DIR}/logs/gate.log
echo "Gate service Started"
