document.addEventListener('DOMContentLoaded', () => {
    const langCookie = getCookie('lang');
    const lang = langCookie || navigator.language || 'en';
    document.documentElement.lang = lang;

    const languageSelect = document.getElementById('language-select');
    if (languageSelect) {
        languageSelect.value = lang.split('-')[0];
    }

    const themeSelect = document.getElementById('theme-select');
    const theme = getCookie('theme') || 'light';
    if (themeSelect) {
        applyTheme(theme);
        themeSelect.value = theme;
    }

    // Ensure the theme is applied correctly after page load
    setTimeout(() => {
        const apiSelect = document.getElementById('api-select');
        const selectedApi = apiSelect ? apiSelect.value : '/v3/api-docs';
        initRedoc(selectedApi);
    }, 100);

    updateTextContent(lang);
});

function changeLanguage(lang) {
    setCookie('lang', lang, 7);
    updateTextContent(lang);
    const apiSelect = document.getElementById('api-select');
    const selectedApi = apiSelect ? apiSelect.value : '/v3/api-docs';
    showLoader();
    setTimeout(() => {
        initRedoc(selectedApi);
        hideLoader();
    }, 100);
}

function changeApi() {
    const apiSelect = document.getElementById('api-select');
    const selectedApi = apiSelect ? apiSelect.value : '/v3/api-docs';
    showLoader();
    setTimeout(() => {
        initRedoc(selectedApi);
        hideLoader();
    }, 100);
}

function initRedoc(spec) {
    showLoader();
    const lang = getCookie('lang') || 'en';
    const containerParent = document.getElementById('redoc-container-parent');
    const oldContainer = document.getElementById('redoc-container');

    // Create a new container for Redoc
    const newContainer = document.createElement('div');
    newContainer.id = 'redoc-container';
    newContainer.className = 'content container';

    // Remove the old container if it exists
    if (oldContainer) {
        containerParent.removeChild(oldContainer);
    }

    // Append the new container
    containerParent.appendChild(newContainer);

    fetch(spec, {
        headers: { 'Accept-Language': lang }
    })
        .then(response => response.json())
        .then(spec => {
            // Ensure the container is ready before initializing Redoc
            requestAnimationFrame(() => {
                Redoc.init(spec, {
                    scrollYOffset: 50,
                    theme: getSelectedRedocTheme(),
                    hideDownloadButton: true,
                    hideLoading: true
                }, newContainer);
                hideLoader();
            });
        })
        .catch((error) => {
            console.error('Error initializing Redoc:', error);
            hideLoader();
        });
}

function getSelectedRedocTheme() {
    const selectedTheme = document.body.classList.contains('theme-dark') ? 'dark' : 'light';
    if (selectedTheme === 'dark') {
        return {
            colors: {
                primary: { main: '#6DB33F' },
                success: { main: '#6DB33F' },
                text: { primary: '#ffffff', secondary: '#b0bec5' },
                background: { primary: '#1a202c', secondary: '#2d3748' }
            },
            sidebar: { backgroundColor: '#2d3748', textColor: '#ffffff' },
            rightPanel: { backgroundColor: '#2d3748', textColor: '#ffffff' },
            typography: { fontSize: '14px', fontFamily: 'Roboto, sans-serif' }
        };
    } else {
        return {
            colors: {
                primary: { main: '#6DB33F' },
                success: { main: '#6DB33F' },
                text: { primary: '#000000', secondary: '#555555' },
                background: { primary: '#ffffff', secondary: '#f4f4f4' }
            },
            sidebar: { backgroundColor: '#ffffff', textColor: '#000000' },
            rightPanel: { backgroundColor: '#ffffff', textColor: '#000000' },
            typography: { fontSize: '14px', fontFamily: 'Roboto, sans-serif' }
        };
    }
}