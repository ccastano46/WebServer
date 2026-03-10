function sendRequest(path) {
    // Collect all inputs for this path
    var inputs = document.querySelectorAll('[id^="input-' + path.replace(/\//g, '_') + '-"]');
    var url = path;
    var params = [];

    inputs.forEach(function(input) {
        var paramName = input.id.split('-').pop();
        var value = input.value.trim() || input.placeholder;
        params.push(paramName + '=' + encodeURIComponent(value));
    });

    if (params.length > 0) url += '?' + params.join('&');

    var resultBox = document.getElementById('result-' + path.replace(/\//g, '_'));
    resultBox.textContent = 'Loading...';
    resultBox.className = 'result-box visible';

    fetch(url)
        .then(function(res) { return res.text(); })
        .then(function(data) {
            resultBox.textContent = 'Response: ' + data;
        })
        .catch(function(err) {
            resultBox.textContent = 'Error: ' + err.message;
        });
}