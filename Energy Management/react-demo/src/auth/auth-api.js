import {HOST} from '../commons/hosts';
import RestApiClient from "../commons/api/rest-client";

const endpoint = {
    login: '/auth/login',
    register: '/auth/register'
};

function login(user, callback) {
    let request = new Request(HOST.backend_api + endpoint.login, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });

    console.log("URL Request: " + request.url);
    RestApiClient.performRequest(request, callback);
}

function register(user, callback) {
    let request = new Request(HOST.backend_api + endpoint.register, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });

    console.log("URL Request: " + request.url);
    RestApiClient.performRequest(request, callback);
}

export {
    login,
    register
};