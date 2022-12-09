# syntax=docker/dockerfile:1
FROM --platform=linux/amd64 ubuntu:rolling
RUN apt-get update \
    && apt-get install -y \
    zip \
    unzip
#    && rm -rf /var/lib/apt/lists/*

COPY zulu11.60.19-ca-jdk11.0.17-linux_x64.tar.gz /zulu11.60.19-ca-jdk11.0.17-linux_x64.tar.gz
RUN tar -xzf /zulu11.60.19-ca-jdk11.0.17-linux_x64.tar.gz -C /opt && cd /opt && mv zulu11.60.19-ca-jdk11.0.17-linux_x64 jdk-11.0.17
COPY gradle-7.6-bin.zip /gradle-7.6-bin.zip
RUN unzip /gradle-7.6-bin.zip -d /opt

ENV JAVA_HOME=/opt/jdk-11.0.17
ENV PATH=$JAVA_HOME/bin:/opt/gradle-7.6/bin:$PATH
RUN mkdir gradle-demo && cd /gradle-demo && gradle init --type java-library --dsl kotlin
RUN cd /gradle-demo && gradle wrapper --status
RUN cd /gradle-demo && gradle build

ENTRYPOINT ["/bin/zsh"]
