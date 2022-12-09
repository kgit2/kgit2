# syntax=docker/dockerfile:1
FROM nginx:latest as nginx
RUN apt-get update \
    && apt-get install -y \
    git \
    fcgiwrap \
    apache2-utils \
#    procps \
#    telnet \
#    lsof \
    && rm -rf /var/lib/apt/lists/*
RUN cd /usr/share/nginx/html \
    && mkdir git \
    && cd git \
    && htpasswd -b -c .htpasswd kgit2 kgit2 \
    && git clone https://github.com/kgit2/test_repo.git --bare test_repo.git \
    && mkdir logs && cd logs \
    && touch access.log error.log
COPY git.conf /etc/nginx/conf.d/git.conf
COPY 10-fastcgi-wrap.sh /docker-entrypoint.d/10-fastcgi-wrap.sh
RUN chmod +x /docker-entrypoint.d/10-fastcgi-wrap.sh
EXPOSE 8080
