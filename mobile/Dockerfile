FROM node:22-alpine AS builder
WORKDIR /app

COPY mobile/package*.json ./
RUN npm install

COPY mobile/ .

RUN npx expo export --platform web

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY mobile/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
