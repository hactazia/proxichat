import { createSocket, Socket } from "node:dgram";
import NetManager, { getPort } from "./NetManager";

export default class UdpController {

    socket: Socket;

    constructor(private netManager: NetManager) {
        this.socket = createSocket('udp4');
    }

    init() {
        this.socket.on('message', this.onMessage.bind(this));

        this.socket.on('error', (err) => {
            console.error(`[UDP] Socket error:\n${err.stack}`);
            this.socket.close();
        });

        this.socket.on('listening', () => {
            const address = this.socket.address();
            console.log(`[UDP] Server listening on ${address.address}:${address.port}`);
        });

        this.socket.on('connect', () => {
            console.log('[UDP] Socket connected');
        });
    }

    async listen() {
        await new Promise<void>(resolve => this.socket.bind(getPort(), () => resolve()));
        this.netManager.main.emit('udp_ready', getPort());
        console.log(`Server listening on port udp://172.0.0.1:${getPort()}`);
    }

    onMessage(msg: Buffer, rinfo: { address: string, port: number }) {
        var json = null;
        console.log(`[UDP] Received ${msg.length} bytes from ${rinfo.address}:${rinfo.port}:`, msg.toString().substring(0, 200));
        try { json = JSON.parse(msg.toString()); } catch (e) { 
            console.log('[UDP] Failed to parse JSON:', e);
        }
        this.netManager.main.emit('net_message', {
            callback: (data: Buffer | string | object) => {
                if (typeof data === 'string') this.socket.send(Buffer.from(data), rinfo.port, rinfo.address);
                else if (Buffer.isBuffer(data)) this.socket.send(data, rinfo.port, rinfo.address);
                else this.socket.send(Buffer.from(JSON.stringify(data)), rinfo.port, rinfo.address);
            },
            message: msg,
            json: json,
            address: rinfo.address,
            port: rinfo.port
        });
    }
}