FROM snowdreamtech/frps
WORKDIR /frps
COPY frps.ini /frps
EXPOSE 20545
EXPOSE 27609
ENTRYPOINT frps -c /frps/frps.ini