fetch('/api/routes')
    .then(r => r.json())
    .then(routes => {
        const container = document.getElementById('cards');
        container.innerHTML = '';

        if (routes.length === 0) {
            container.innerHTML = '<p class="empty">No se encontraron endpoints registrados.</p>';
            return;
        }

        routes.forEach((route, idx) => {
            const formId   = 'form-'   + idx;
            const resultId = 'result-' + idx;

            // Badges de @RequestParam
            const paramBadges = route.params.map(p =>
                `<span class="badge badge-param">@RequestParam "${p.name}"</span>`
            ).join('');

            // Inputs para cada param
            const paramInputs = route.params.map(p => `
        <div class="input-group">
          <label>${p.name}</label>
          <input data-param="${p.name}" value="${p.default}" placeholder="${p.name}">
        </div>
      `).join('');

            const card = document.createElement('div');
            card.className = 'controller-card';
            card.innerHTML = `
        <div class="controller-header">
          <span class="badge badge-ctrl">@RestController</span>
          <span class="controller-name">${route.path}</span>
        </div>
        <div class="endpoint">
          <div class="endpoint-header">
            <span class="badge badge-get">GET</span>
            <span class="badge badge-mapping">@GetMapping</span>
            <span class="endpoint-path">${route.path}</span>
          </div>
          ${paramBadges ? `<div class="param-badges">${paramBadges}</div>` : ''}
          <div class="exec-area">
            <div class="param-inputs" id="${formId}">${paramInputs}</div>
            <button class="btn-exec"
              onclick="execEndpoint('${route.path}', '${formId}', '${resultId}')">
              ▶ Execute
            </button>
          </div>
          <div class="result-box" id="${resultId}"></div>
        </div>
      `;

            container.appendChild(card);
        });
    })
    .catch(err => {
        document.getElementById('cards').innerHTML =
            `<p class="empty">Error cargando rutas: ${err.message}</p>`;
    });

function execEndpoint(path, formId, resultId) {
    const form      = document.getElementById(formId);
    const resultBox = document.getElementById(resultId);
    let url = path;

    if (form) {
        const inputs = form.querySelectorAll('input[data-param]');
        const pairs  = [];
        inputs.forEach(inp => {
            pairs.push(encodeURIComponent(inp.dataset.param) + '=' + encodeURIComponent(inp.value));
        });
        if (pairs.length > 0) url += '?' + pairs.join('&');
    }

    resultBox.style.display = 'block';
    resultBox.className = 'result-box';
    resultBox.textContent = 'Loading...';

    fetch(url)
        .then(r => r.text())
        .then(data => {
            resultBox.textContent = '✔ ' + data;
        })
        .catch(err => {
            resultBox.className = 'result-box error';
            resultBox.textContent = '✘ ' + err.message;
        });
}