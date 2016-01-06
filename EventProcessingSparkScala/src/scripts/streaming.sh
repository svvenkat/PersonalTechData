#/usr/bin/bash


export SOURCE_DIR=/cygdrive/c/Users/venkat/IdeaProjects/EventProcessingSparkScala/src/main/resources
export DESTINATION_DIR=/cygdrive/c/Users/venkat/tmp/SparkStreaming

export BATCH_PERIOD=30

rm $DESTINATION_DIR/*.*

echo "Starting the test. Enter when the driver is ready.."
read x 


for i in  `ls $SOURCE_DIR/*.txt`
do
echo "Copying $i  now"
cp $i $DESTINATION_DIR
sleep $BATCH_PERIOD
done