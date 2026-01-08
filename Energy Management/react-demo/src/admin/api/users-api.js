import {HOST} from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";

const ENDPOINT = {
    users: '/people'
};

function getAllUsers(callback) {
    let request = new Request(HOST.backend_api + ENDPOINT.users, {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("token")
        }
    });
    RestApiClient.performRequest(request, callback);
}

function updateUser(user, callback) {
    let request = new Request(HOST.backend_api + ENDPOINT.users + "/" + user.id, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem("token")
        },
        body: JSON.stringify(user)
    });
    RestApiClient.performRequest(request, callback);
}

function deleteUser(id, callback) {
    let request = new Request(HOST.backend_api + ENDPOINT.users + "/" + id, {
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("token")
        }
    });
    RestApiClient.performRequest(request, callback);
}

export {
    getAllUsers,
    updateUser,
    deleteUser
};