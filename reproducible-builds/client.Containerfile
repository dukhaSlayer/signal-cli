FROM docker.io/rust:1.94.1-slim-trixie@sha256:c6a474d7164ea2455e09b60a759b1edca38db7373c5689c1dae31780de4e71ac
ENV SOURCE_DATE_EPOCH=1767225600
ENV LANG=C.UTF-8
ENV LC_CTYPE=en_US.UTF-8
COPY --chmod=0700 reproducible-builds/entrypoint.sh /usr/local/bin/entrypoint.sh
WORKDIR /signal-cli
ENTRYPOINT [ "/usr/local/bin/entrypoint.sh", "client" ]
