# To Know Git version
git --version

# To Know Docker version
docker --version

# To Run Gradale Build need to have Java Vesion 1.11.+
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

# Setting JAVA_HOME to System PATH
export PATH=$PATH:$JAVA_HOME

# Get the Shorter format of Git-SHA 
export GITHASH=`git rev-parse --short HEAD`

# Get the BUILD Date
export BUILDDATE=`date -u +"%Y%m%d%H%M"`

# For Assiging the Gradle Resources
export GRADLE_OPTS="-Xmx6g -Xms6g"

# The Current Build ID 
echo "Build id is --------------------- $BUILD_ID"

# Gradle command  to Produce the Dependant targetfiles for Docker build
./gradlew gate-web:installDist -x test

cp docker/custom-plugin.json custom-plugin.json

   # Assigning Rhel Image Name according to Quay.io Details
   IMAGENAME="quay.io/opsmxpublic/ubi8-oes-gate:${GITHASH}-${BUILD_NUMBER}"
   
   # Assigning Rhel Image Name according to Docker.io Details
   RELEASE_IMAGENAME="opsmx11/ubi8-oes-gate:${GITHASH}-${BUILD_NUMBER}"  

   
   # To Build Docker image with Given Docker File
   docker build -t $IMAGENAME .  -f  ${DOCKERFILE_PATH} --no-cache  --build-arg CUSTOMPLUGIN_RELEASEVERSION=${CUSTOMPLUGIN_RELEASEVERSION} 
   
   # Create new Image Tag for Docker.io with the previous Build
   docker tag $IMAGENAME $RELEASE_IMAGENAME
   
   # Quay.io login
   docker login -u $quay_user -p $quay_pass quay.io
   
   # To Push the Docker image into Quay.io
   docker push $IMAGENAME

   # Docker.io login
   docker login -u $docker_user -p $docker_pass docker.io
   
   # To Push the Docker image into Quay.io
   docker push $RELEASE_IMAGENAME
   
   echo "Gate: ${IMAGENAME}"

# Quay Image Name as Artifact
echo \"Gate\": \"${IMAGENAME}\" > file.properties;
