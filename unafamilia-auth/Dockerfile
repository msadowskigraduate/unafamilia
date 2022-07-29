FROM node:18.7.0-alpine

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm ci --only=production

COPY . .
EXPOSE 9005
CMD ["node", "app.js"]
