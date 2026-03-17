function performRequest(request, callback) {
    fetch(request)
        .then(function (response) {
            response.text().then(text => {
                let data = text;

                try {
                    data = JSON.parse(text);
                } catch (err) {
                    console.log("Raspunsul nu este JSON, folosim text simplu:", text);
                }

                if (response.ok) {
                    callback(data, response.status, null);
                } else {
                    callback(null, response.status, data);
                }
            });
        })
        .catch(function (err) {
            callback(null, 1, err)
        });
}

module.exports = {
    performRequest
};