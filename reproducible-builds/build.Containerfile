ARG ZULU_TAG="25.0.2-jdk@sha256:9582df6c4415d9c770eb5ff8fce426ebba53631149c9eb083ee126568d32fab3"

FROM docker.io/azul/zulu-openjdk:$ZULU_TAG
ENV SOURCE_DATE_EPOCH=1767225600
ENV LANG=C.UTF-8
ENV LC_CTYPE=en_US.UTF-8
ARG SNAPSHOT=20260101T000000Z
RUN echo "deb http://snapshot.ubuntu.com/ubuntu/${SNAPSHOT}/ jammy main" > /etc/apt/sources.list \
    && echo "deb http://snapshot.ubuntu.com/ubuntu/${SNAPSHOT}/ jammy universe" >> /etc/apt/sources.list
RUN apt update && apt install -y make asciidoc-base
COPY --chmod=0700 reproducible-builds/entrypoint.sh /usr/local/bin/entrypoint.sh
WORKDIR /signal-cli
ENTRYPOINT [ "/usr/local/bin/entrypoint.sh", "build" ]
