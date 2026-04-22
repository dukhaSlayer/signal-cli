ARG ZULU_TAG="25.0.2-jdk@sha256:9582df6c4415d9c770eb5ff8fce426ebba53631149c9eb083ee126568d32fab3"
ARG SOURCE_DATE_EPOCH="1767225600"

FROM docker.io/azul/zulu-openjdk:$ZULU_TAG
ENV SOURCE_DATE_EPOCH=$SOURCE_DATE_EPOCH
ENV LANG=C.UTF-8
ENV LC_CTYPE=en_US.UTF-8
RUN SNAPSHOT="$(date -u -d "@$SOURCE_DATE_EPOCH" +%Y%m%dT%H%M%SZ)" \
    && apt install -y make asciidoc-base --update --snapshot "$SNAPSHOT"
COPY --chmod=0700 reproducible-builds/entrypoint.sh /usr/local/bin/entrypoint.sh
WORKDIR /signal-cli
ENTRYPOINT [ "/usr/local/bin/entrypoint.sh", "build" ]
