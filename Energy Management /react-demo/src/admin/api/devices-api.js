import {HOST} from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";

const ENDPOINT = {
    devices: '/devices'
};

function getAllDevices(callback) {
    let request = new Request(HOST.backend_api + ENDPOINT.devices, {
        method: 'GET',
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem("token") }
    });
    RestApiClient.performRequest(request, callback);
}

function createDevice(device, callback) {
    let request = new Request(HOST.backend_api + ENDPOINT.devices, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem("token")
        },
        body: JSON.stringify(device)
    });
    RestApiClient.performRequest(request, callback);
}

function updateDevice(device, callback) {
    let request = new Request(HOST.backend_api + ENDPOINT.devices + "/" + device.id, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem("token")
        },
        body: JSON.stringify(device)
    });
    RestApiClient.performRequest(request, callback);
}

function deleteDevice(id, callback) {
    let request = new Request(HOST.backend_api + ENDPOINT.devices + "/" + id, {
        method: 'DELETE',
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem("token") }
    });
    RestApiClient.performRequest(request, callback);
}

export {
    getAllDevices,
    createDevice,
    updateDevice,
    deleteDevice
};