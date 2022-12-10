# syntax=docker/dockerfile:1
FROM ubuntu:rolling
RUN apt-get update \
    && apt-get install -y \
    git \
    libssl-dev \
    pkg-config \
    zlib1g-dev \
    libc6-dev \
    clang \
    make \
    cmake \
    vim \
    wget \
    curl \
    tar \
    gzip \
    zsh
#    && rm -rf /var/lib/apt/lists/*

#RUN apt-get install -y zip unzip
#
#COPY zulu11.60.19-ca-jdk11.0.17-linux_x64.tar.gz /zulu11.60.19-ca-jdk11.0.17-linux_x64.tar.gz
#RUN tar -xzf /zulu11.60.19-ca-jdk11.0.17-linux_x64.tar.gz -C /opt && cd /opt && mv zulu11.60.19-ca-jdk11.0.17-linux_x64 jdk-11.0.17
#COPY gradle-7.6-bin.zip /gradle-7.6-bin.zip
#RUN unzip /gradle-7.6-bin.zip -d /opt
#
#COPY .zshrc /root/.zshrc
#SHELL ["/bin/zsh", "-c"]
#
#ENV HTTP_PROXY=http://host.docker.internal:6152
#ENV HTTPS_PROXY=http://host.docker.internal:6152
#ENV JAVA_HOME=/opt/jdk-11.0.17
#ENV PATH=$JAVA_HOME/bin:/opt/gradle-7.6/bin:$PATH
#RUN git clone https://github.com/kgit2/kgit2.git \
#    && cd kgit2 \
#    && git checkout -b develop-test origin/develop-test
#
##RUN cd kgit2/lib && gradle -d --no-daemon generateDef

ENTRYPOINT ["/bin/zsh"]
