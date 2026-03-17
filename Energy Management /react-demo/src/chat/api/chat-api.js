const HOST = "http://localhost";

const ChatAPI = {
    sendMessage: (senderId, receiverId, content) => {
        const token = localStorage.getItem("token");

        const message = {
            senderId: senderId,
            receiverId: receiverId,
            content: content,
            timestamp: null,
            isRead: false
        };

        return fetch(`${HOST}/chat/send`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(message)
        });
    },

    getHistory: (userId1, userId2) => {
        const token = localStorage.getItem("token");

        return fetch(`${HOST}/chat/history/${userId1}/${userId2}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        }).then(response => {
            if(response.ok) return response.json();
            return [];
        });
    }
};

export default ChatAPI;