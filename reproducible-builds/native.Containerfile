FROM container-registry.oracle.com/graalvm/native-image:25.0.2@sha256:4c0d5919f6840d89721274eb8cf81962faa2f870b816967e6732e2a151b150d8
ENV SOURCE_DATE_EPOCH=1767225600
ENV LANG=C.UTF-8
ENV LC_CTYPE=en_US.UTF-8
COPY --chmod=0700 reproducible-builds/entrypoint.sh /usr/local/bin/entrypoint.sh
WORKDIR /signal-cli
ENTRYPOINT [ "/usr/local/bin/entrypoint.sh", "native" ]
