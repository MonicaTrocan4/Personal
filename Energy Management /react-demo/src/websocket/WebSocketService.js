import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const WEBSOCKET_URL = "http://localhost/ws";

class WebSocketService {
    constructor() {
        this.client = null;
        this.onMessageReceived = null;
    }

    connect(onMessageReceived) {
        this.onMessageReceived = onMessageReceived;

        this.client = new Client({
            webSocketFactory: () => new SockJS(WEBSOCKET_URL),

            onConnect: () => {
                console.log("Connected to WebSocket");

                this.client.subscribe('/topic/alerts', (message) => {
                    if (message.body) {
                        this.onMessageReceived(message.body);
                    }
                });
            },

            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            },

            reconnectDelay: 5000,
        });

        this.client.activate();
    }

    disconnect() {
        if (this.client) {
            this.client.deactivate();
            console.log("Disconnected");
        }
    }
}

const webSocketServiceInstance = new WebSocketService();
export default webSocketServiceInstance;