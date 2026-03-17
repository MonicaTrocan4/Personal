import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { Card, CardHeader, CardBody, CardFooter, Input, Button } from 'reactstrap';
import ChatAPI from './api/chat-api';

const ChatWindow = ({ currentUser, chatPartner, onClose, isVisible }) => {

    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState("");

    const clientRef = useRef(null);
    const messagesEndRef = useRef(null);

    useEffect(() => {
        if (isVisible && currentUser && chatPartner) {
            ChatAPI.getHistory(currentUser.id, chatPartner.id)
                .then(data => setMessages(data))
                .catch(err => console.error("Error loading history", err));

            const client = new Client({
                webSocketFactory: () => new SockJS('http://localhost/ws'),
                reconnectDelay: 5000,
                heartbeatIncoming: 4000,
                heartbeatOutgoing: 4000,
                onConnect: (frame) => {
                    console.log('Chat Connected: ' + frame);

                    client.subscribe('/topic/messages', (response) => {
                        const incomingMsg = JSON.parse(response.body);
                        handleIncomingMessage(incomingMsg);
                    });
                },
                onStompError: (frame) => {
                    console.error('Broker reported error: ' + frame.headers['message']);
                    console.error('Additional details: ' + frame.body);
                }
            });

            client.activate();
            clientRef.current = client;
        }

        return () => {
            if (clientRef.current) {
                clientRef.current.deactivate();
            }
        };
    }, [isVisible, chatPartner]);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    const handleIncomingMessage = (msg) => {
        const myId = currentUser.id;
        const partnerId = chatPartner.id;

        const receivedFromPartner = (msg.senderId === partnerId && msg.receiverId === myId);
        const sentByMe = (msg.senderId === myId && msg.receiverId === partnerId);

        if (receivedFromPartner || sentByMe) {
            setMessages(prev => {
                if (prev.some(m => m.id === msg.id)) return prev;
                return [...prev, msg];
            });
        }
    };

    const handleSend = () => {
        if (!newMessage.trim()) return;

        ChatAPI.sendMessage(currentUser.id, chatPartner.id, newMessage)
            .then(() => {
                setNewMessage("");
            })
            .catch(err => console.error("Failed to send", err));
    };

    if (!isVisible) return null;

    return (
        <Card style={{
            position: 'fixed',
            bottom: '20px',
            right: '20px',
            width: '350px',
            height: '450px',
            zIndex: 1000,
            boxShadow: '0 4px 8px rgba(0,0,0,0.2)',
            display: 'flex',
            flexDirection: 'column'
        }}>
            <CardHeader className="bg-primary text-white d-flex justify-content-between align-items-center">
                <span style={{fontWeight: 'bold'}}>Chat with {chatPartner.name || "User"}</span>
                <Button close onClick={onClose} className="text-white" />
            </CardHeader>

            <CardBody style={{ flex: 1, overflowY: 'auto', backgroundColor: '#f8f9fa', padding: '10px' }}>
                {messages.map((msg, index) => {
                    const isMe = msg.senderId === currentUser.id;
                    return (
                        <div key={index} style={{
                            display: 'flex',
                            justifyContent: isMe ? 'flex-end' : 'flex-start',
                            marginBottom: '10px'
                        }}>
                            <div style={{
                                maxWidth: '75%',
                                padding: '8px 12px',
                                borderRadius: '15px',
                                backgroundColor: isMe ? '#007bff' : '#e9ecef',
                                color: isMe ? '#fff' : '#333',
                                borderBottomRightRadius: isMe ? '0' : '15px',
                                borderBottomLeftRadius: isMe ? '15px' : '0'
                            }}>
                                {msg.content}
                                <div style={{ fontSize: '0.7rem', opacity: 0.7, textAlign: 'right', marginTop: '4px' }}>
                                    {msg.timestamp ? new Date(msg.timestamp).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}) : ''}
                                </div>
                            </div>
                        </div>
                    );
                })}
                <div ref={messagesEndRef} />
            </CardBody>

            <CardFooter className="d-flex">
                <Input
                    type="text"
                    placeholder="Type a message..."
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleSend()}
                />
                <Button color="primary" className="ms-2" onClick={handleSend}>Send</Button>
            </CardFooter>
        </Card>
    );
};

export default ChatWindow;