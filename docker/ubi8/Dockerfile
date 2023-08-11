FROM registry.access.redhat.com/ubi8/ubi:8.3
MAINTAINER sig-platform@spinnaker.io
COPY ./gate-web/build/install/gate /opt/gate
RUN yum -y install java-11-openjdk-headless.x86_64 wget vim curl net-tools nettle
RUN yum -y update
RUN adduser spinnaker
RUN mkdir -p /opt/gate/plugins && mkdir -p /opt/spinnaker/plugins 
####adding customplugin zip 
ARG CUSTOMPLUGIN_RELEASEVERSION
ENV CUSTOMPLUGIN_RELEASEVERSION=$CUSTOMPLUGIN_RELEASEVERSION
COPY custom-plugin.json /opt/spinnaker/plugins/plugins.json
RUN wget -O VerificationPlugin-v1.0.1-SNAPSHOT.zip -c https://github.com/OpsMx/Customplugins/releases/download/${CUSTOMPLUGIN_RELEASEVERSION}/VerificationPlugin-v1.0.1-SNAPSHOT.zip \
    && wget -O TestVerificationPlugin-v1.0.1-SNAPSHOT.zip -c https://github.com/OpsMx/Customplugins/releases/download/${CUSTOMPLUGIN_RELEASEVERSION}/TestVerificationPlugin-v1.0.1-SNAPSHOT.zip \
    && wget -O policyPlugin-v1.0.1-SNAPSHOT.zip -c https://github.com/OpsMx/Customplugins/releases/download/${CUSTOMPLUGIN_RELEASEVERSION}/policyPlugin-v1.0.1-SNAPSHOT.zip \
    && wget -O ApprovalStagePlugin-v1.0.1-SNAPSHOT.zip -c https://github.com/OpsMx/Customplugins/releases/download/${CUSTOMPLUGIN_RELEASEVERSION}/ApprovalStagePlugin-v1.0.1-SNAPSHOT.zip
RUN mv VerificationPlugin-v1.0.1-SNAPSHOT.zip /opt/spinnaker/plugins/ \
    && mv TestVerificationPlugin-v1.0.1-SNAPSHOT.zip /opt/spinnaker/plugins/ \
    && mv policyPlugin-v1.0.1-SNAPSHOT.zip /opt/spinnaker/plugins/ \
    && mv ApprovalStagePlugin-v1.0.1-SNAPSHOT.zip /opt/spinnaker/plugins/ 
    
RUN sed -i 's/"VERIFICATION_SHASUM"/'\""$(sha512sum /opt/spinnaker/plugins/VerificationPlugin-v1.0.1-SNAPSHOT.zip | awk '{print $1}')"\"'/g' /opt/spinnaker/plugins/plugins.json \
    && sed -i 's/"TESTVERIFICATION_SHASUM"/'\""$(sha512sum /opt/spinnaker/plugins/TestVerificationPlugin-v1.0.1-SNAPSHOT.zip | awk '{print $1}')"\"'/g' /opt/spinnaker/plugins/plugins.json \
    && sed -i 's/"POLICY_SHASUM"/'\""$(sha512sum /opt/spinnaker/plugins/policyPlugin-v1.0.1-SNAPSHOT.zip | awk '{print $1}')"\"'/g' /opt/spinnaker/plugins/plugins.json \
    && sed -i 's/"APPROVAL_SHASUM"/'\""$(sha512sum /opt/spinnaker/plugins/ApprovalStagePlugin-v1.0.1-SNAPSHOT.zip | awk '{print $1}')"\"'/g' /opt/spinnaker/plugins/plugins.json
RUN chown -R spinnaker:spinnaker /opt/spinnaker
RUN chmod -R 777 /opt/gate/plugins/
RUN chown -R spinnaker:spinnaker /opt/
USER spinnaker
CMD ["/opt/gate/bin/gate"]
