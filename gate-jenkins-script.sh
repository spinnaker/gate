#!/bin/sh
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH=$PATH:$JAVA_HOME

#export GITHASH=`git rev-parse HEAD`
export GITHASH=`git rev-parse --short HEAD`
export BUILDDATE=`date -u +"%Y%m%d%H%M"`

export GIT_COMMIT_REV=$(git log -n 1 --pretty=format:'%H')
echo "GIT_COMMIT_REV" $GIT_COMMIT_REV

mkdir -p version-info
echo ${GITHASH} > ${WORKSPACE}/version-info/git-hash.txt
echo ${TAGNAME} > ${WORKSPACE}/version-info/git-tag.txt

java -version
echo ${CUSTOMPLUGIN_RELEASEVERSION}
echo $JAVA_HOME


 
  git checkout master

  cp -R docker_build /tmp/docker_build
 
  cp -R jaeger /tmp/jaeger
 

 git checkout tags/${TAGNAME}
 
 cp -R /tmp/docker_build docker_build
 
 cp -R /tmp/jaeger jaeger
 
 
# Build OES-GATE


#sed -i 's/7.110.0/7.109.1/g;s/8.11.0/8.15.0/g;s/1.27.0/1.27.1/g' gradle.properties

#sed -i 's/7.139.0/7.136.0-SNAPSHOT/g' gradle.properties




cat gradle.properties


sh -c "echo gate service building..."

#./gradlew --no-daemon gate-web:installDist -x test
./gradlew clean install -x test



# Build the Docker image

case "${REGISTRY_USERNAME}" in
  opsmx11)
      echo "Pushing to DEVELOPMENT docker repo: ${DOCKER_HUB_USERNAME}"
      
      IMAGENAME="opsmx11/gate:${TAGNAME}"
      
      echo "Image Name:" $IMAGENAME
      
    
     # sudo docker build --build-arg CUSTOMPLUGIN_RELEASEVERSION=${CUSTOMPLUGIN_RELEASEVERSION} -t $IMAGENAME -f Dockerfile.rhel8-ubi8 .
      sudo docker build --no-cache --build-arg CUSTOMPLUGIN_RELEASEVERSION=${CUSTOMPLUGIN_RELEASEVERSION} -t $IMAGENAME -f docker_build/Dockerfile.rhel8-ubi8 .
     # sudo docker images ${IMAGENAME}
     
      # Push the image
      sudo docker login --username ${REGISTRY_USERNAME} --password ${REGISTRY_PASSWORD}
      sudo docker push $IMAGENAME
      ;;  
  ksrinimba)
      echo "Pushing to CUSTOMER PRODUCTION quay repo: ${REGISTRY_USERNAME}"
      IMAGENAME="quay.io/opsmxpublic/ubi8-gate:${TAGNAME}"
      
      sudo docker build --build-arg CUSTOMPLUGIN_RELEASEVERSION=${CUSTOMPLUGIN_RELEASEVERSION} -t ${IMAGENAME} -f docker_build/Dockerfile.rhel8-ubi8 .
      #sudo docker images ${IMAGENAME}
      
      # Push the image to quay      
      sudo docker login quay.io --username ${REGISTRY_USERNAME} --password ${REGISTRY_PASSWORD}
      sudo docker push ${IMAGENAME}
      ;;
      
  *)
      echo "Cannot determine repository from credentials: username = ${REGISTRY_USERNAME}"
      exit 1
esac
echo "Buildnumber": "${IMAGENAME}" > file.properties
echo "Gitcommitid": "$GIT_COMMIT_REV" >> file.properties