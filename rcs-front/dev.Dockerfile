FROM node:lts-alpine3.12

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories
RUN apk add --no-cache alpine-sdk python3 make gcc g++
WORKDIR /dev
COPY ["./app/package.json","./ws-scrcpy/package.json","./package.json","./yarn.lock","/dev/"]
RUN yarn


EXPOSE 3000,8000
CMD yarn workspace app start