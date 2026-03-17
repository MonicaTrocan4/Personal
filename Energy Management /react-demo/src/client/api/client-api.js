import {HOST} from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";

const ENDPOINT = {
    devices: '/devices'
};

function getMyDevices(callback) {
    const userId = localStorage.getItem("userId");
    const token = localStorage.getItem("token");

    if (!userId || !token) {
        console.error("User ID sau Token lipsa!");
        return;
    }

    const url = HOST.backend_api + ENDPOINT.devices + '/users/' + userId;

    console.log("Fetching my devices from URL:", url);

    let request = new Request(url, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    });

    RestApiClient.performRequest(request, callback);
}

export {
    getMyDevices
};