function addTryItButton() {
    const endpointSections = document.querySelectorAll('.sc-dcJsrY.kNjBFu');

    endpointSections.forEach(section => {
        const firstDiv = section.querySelector('.sc-dkmUuB.iwMnZw');
        const endpointElement = firstDiv.querySelector('.sc-ejfMa-d.jgeKJH');

        if (endpointElement) {
            const tryItButtonTemplate = document.getElementById('try-it-template');
            const tryItButtonClone = tryItButtonTemplate.content.cloneNode(true);
            const tryItButtonContainer = tryItButtonClone.querySelector('.try-it-button-container');
            const tryItFormContainer = tryItButtonClone.querySelector('.try-it-form-container');

            firstDiv.insertAdjacentElement('afterend', tryItButtonContainer);
            tryItButtonContainer.insertAdjacentElement('afterend', tryItFormContainer);

            const tryItButton = tryItButtonContainer.querySelector('.try-it-button');

            tryItButton.addEventListener('click', () => {
                tryItFormContainer.style.display = tryItFormContainer.style.display === 'none' ? 'block' : 'none';
                populateForm(section, tryItFormContainer.querySelector('form'));
            });

            const method = section.querySelector('.http-verb').textContent.trim().toUpperCase();
            const endpoint = section.querySelector('.sc-ejfMa-d.jgeKJH').textContent.trim();
            const form = tryItFormContainer.querySelector('form');
            form.setAttribute('data-method', method);
            form.setAttribute('data-endpoint', endpoint);

            setupFormSubmission(form);
        }
    });
}

function setupFormSubmission(form) {
    form.addEventListener('submit', event => {
        event.preventDefault();
        const method = form.getAttribute('data-method');
        const url = form.getAttribute('data-endpoint');
        const headersTextarea = form.querySelector('textarea.headers');
        const pathParamsTextarea = form.querySelector('textarea.path-params');
        const queryTextarea = form.querySelector('textarea.query-params');
        const bodyTextarea = form.querySelector('textarea.body');
        const responseContainer = form.querySelector('.response-content');

        let headers = {};
        if (headersTextarea.value) {
            headersTextarea.value.split('\n').forEach(line => {
                const [key, value] = line.split(':').map(part => part.trim());
                if (key && value) {
                    headers[key] = value;
                }
            });
        }

        if (!headers['Content-Type'] && method !== 'GET' && method !== 'HEAD') {
            headers['Content-Type'] = 'application/json';
        }

        if (!headers['Accept']) {
            headers['Accept'] = 'application/json';
        }

        let pathParams = {};
        if (pathParamsTextarea && pathParamsTextarea.value) {
            pathParamsTextarea.value.split('\n').forEach(line => {
                const [key, value] = line.split('=').map(part => part.trim());
                if (key && value) {
                    pathParams[key] = value;
                }
            });
        }

        let queryParams = '';
        if (queryTextarea && queryTextarea.value) {
            queryTextarea.value.split('\n').forEach(line => {
                const [key, value] = line.split('=').map(part => part.trim());
                if (key && value) {
                    queryParams += `${encodeURIComponent(key)}=${encodeURIComponent(value)}&`;
                }
            });
            queryParams = queryParams.slice(0, -1);
        }

        let requestUrl = url;
        if (Object.keys(pathParams).length > 0) {
            Object.keys(pathParams).forEach(param => {
                requestUrl = requestUrl.replace(`{${param}}`, pathParams[param]);
            });
        }
        if (queryParams) {
            requestUrl += `?${queryParams}`;
        }

        fetch(requestUrl, {
            method: method,
            headers: headers,
            body: method !== 'GET' && method !== 'HEAD' ? bodyTextarea.value : null
        })
            .then(response => response.text())
            .then(data => {
                responseContainer.textContent = data;
            })
            .catch(error => {
                console.error('Error:', error);
                responseContainer.textContent = 'Error: ' + error.message;
            });
    });
}

function populateForm(section, form) {
    if (!form) {
        console.error('Form not found');
        return;
    }

    // Populate headers
    const headersTextarea = form.querySelector('textarea.headers');
    if (headersTextarea) {
        const headersSection = Array.from(section.querySelectorAll('h5')).find(header => header.textContent.includes('header Parameters'));
        if (headersSection) {
            const headersTable = headersSection.nextElementSibling;
            const headersRows = headersTable.querySelectorAll('tr');
            let headersContent = '';
            headersRows.forEach(row => {
                const keyElement = row.querySelector('.property-name');
                const valueElement = row.querySelector('.sc-ddjGPC.lmPAIU.fGykvj') || row.querySelector('.sc-ddjGPC.lmPAIU');
                if (keyElement && valueElement) {
                    const key = keyElement.textContent.trim();
                    const value = valueElement.textContent.replace(/"/g, '').trim();  // Remove double quotes
                    headersContent += `${key}: ${value}\n`;
                }
            });
            headersTextarea.value = headersContent;
        }
    }

    // Populate query parameters
    const queryParamsTextarea = form.querySelector('textarea.query-params');
    if (queryParamsTextarea) {
        const querySection = Array.from(section.querySelectorAll('h5')).find(header => header.textContent.includes('query Parameters'));
        if (querySection) {
            const queryTable = querySection.nextElementSibling;
            const queryRows = queryTable.querySelectorAll('tr');
            let queryContent = '';
            queryRows.forEach(row => {
                const keyElement = row.querySelector('.property-name');
                const exampleElement = Array.from(row.querySelectorAll('span')).find(span => span.textContent.includes('Example:'));
                const defaultElement = Array.from(row.querySelectorAll('span')).find(span => span.textContent.includes('Default:'));
                if (keyElement) {
                    const key = keyElement.textContent.trim();
                    let value = '';
                    if (exampleElement) {
                        const valueElement = exampleElement.nextElementSibling;
                        if (valueElement) {
                            value = valueElement.textContent.split('=')[1].replace(/"/g, '').trim();
                        }
                    } else if (defaultElement) {
                        const valueElement = defaultElement.nextElementSibling;
                        if (valueElement) {
                            value = valueElement.textContent.replace(/Default:/, '').replace(/"/g, '').trim();
                        }
                    }
                    queryContent += `${key}=${value}\n`;
                }
            });
            queryParamsTextarea.value = queryContent;
        }
    }

    // Populate path parameters
    const pathParamsTextarea = form.querySelector('textarea.path-params');
    if (pathParamsTextarea) {
        const pathSection = Array.from(section.querySelectorAll('h5')).find(header => header.textContent.includes('path Parameters'));
        if (pathSection) {
            const pathTable = pathSection.nextElementSibling;
            const pathRows = pathTable.querySelectorAll('tr');
            let pathContent = '';
            pathRows.forEach(row => {
                const keyElement = row.querySelector('.property-name');
                const exampleElement = Array.from(row.querySelectorAll('span')).find(span => span.textContent.includes('Example:'));
                const defaultElement = Array.from(row.querySelectorAll('span')).find(span => span.textContent.includes('Default:'));
                if (keyElement) {
                    const key = keyElement.textContent.trim();
                    let value = '';
                    if (exampleElement) {
                        value = exampleElement.nextElementSibling ? exampleElement.nextElementSibling.textContent.split('=')[1].replace(/"/g, '').trim() : '';
                    } else if (defaultElement) {
                        value = defaultElement.nextElementSibling ? defaultElement.nextElementSibling.textContent.replace(/Default:/, '').replace(/"/g, '').trim() : '';
                    }
                    pathContent += `${key}=${value}\n`;
                }
            });
            pathParamsTextarea.value = pathContent;
        }
    }

    // Populate body
    const bodyTextarea = form.querySelector('textarea.body');
    if (bodyTextarea) {
        const requestSampleSection = Array.from(section.querySelectorAll('h3')).find(header => header.textContent.includes('Request samples'));
        if (requestSampleSection) {
            const sampleContainer = requestSampleSection.nextElementSibling;
            const sampleCode = sampleContainer.querySelector('code');
            if (sampleCode) {
                bodyTextarea.value = sampleCode.textContent;
            }
        }
    }
}