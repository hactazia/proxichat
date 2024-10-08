# Proximity-Based Voice Chat for Any Game

[![NodeJS](https://img.shields.io/badge/NodeJS-339933?logo=nodedotjs&logoColor=white)](https://github.com/nodejs)
[![Express](https://img.shields.io/badge/Express-000000?logo=express&logoColor=white)](https://github.com/expressjs/express)
[![Socket.IO](https://img.shields.io/badge/Socket.IO-010101?logo=socketdotio&logoColor=white)](https://github.com/socketio/socket.io)
[![WebRTC](https://img.shields.io/badge/WebRTC-333333?logo=webrtc&logoColor=white)](https://webrtc.org)
[![Simple Peer](https://img.shields.io/badge/Simple%20Peer-FF4088?logo=webrtc&logoColor=white)](https://github.com/feross/simple-peer)
[![Web Audio API](https://img.shields.io/badge/Web%20Audio%20API-000000?logo=webaudio&logoColor=white)](https://github.com/WebAudio/web-audio-api)
[![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)](https://www.docker.com)
[![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=white)](https://www.typescriptlang.org)
[![Json Web Token](https://img.shields.io/badge/Json%20Web%20Token-000000?logo=jsonwebtokens&logoColor=white)](https://jwt.io)
[![Spigot](https://img.shields.io/badge/Spigot-00AA00?logo=spigotmc&logoColor=white)](https://www.spigotmc.org)

## Description
>
> Project based on [proxi-chat by BGoodes](https://github.com/BGoodes/proxi-chat).

This project aims to add proximity-based voice chat to any game by bringing a web-based platform.

## Installation

### For Server

1. Clone the repository.
2. Go to `server/` folder.
3. Then install and run the app: `npm install` & `npm run dev`.
4. Change `.env.example` to `.env` and fill in the necessary information.
5. To start the server, run `npm run dev`.

### For Minecraft Plugin

1. Clone the repository.
2. Go to `minecraft-plugin/` folder.
3. With idea, open the project, `package` projet with Maven.
4. Copy the jar file in the `target` folder to your server's plugin folder.
5. After first running, change the config in `config.yml` in the plugin folder.
6. Add in config file, the [generated token](#for-make-a-token) at `server_token`.
7. Restart the server.

### For Make a Token

1. Go to `server/` folder.
2. Go on `scripts/generateToken.js` and read cotent to understand how to use.
3. Run `npm run token`.

## Build And Run With Docker

1. Clone the repository.
2. Change `.env.example` (in `server/` folder) to `.env` (in root folder) and fill in the necessary information.
3. Run the following commands:

```bash
docker build -t proxichat/server ./server/
docker compose up
```

## License

This project is licensed under the MIT License. See the [LICENCE](/LICENCE.txt) file for details.
