#!/bin/bash
echo java -jar /tmp/javaclient.jar -i $CLUSTER -b $BUCKET -s $SCOPE -c $COLLECTION -n $N -pc $PC -pu $PU -pd $PD -l $L -dsn $DSN -dpx $DPX -dt $DT -o $O
java -jar /tmp/javaclient.jar -i $CLUSTER -b $BUCKET -s $SCOPE -c $COLLECTION -n $N -pc $PC -pu $PU -pd $PD -l $L -dsn $DSN -dpx $DPX -dt $DT -o $O

