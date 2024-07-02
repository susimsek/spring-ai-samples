function changeLanguage(lang) {
    setCookie('lang', lang, 7);
    updateTextContent(lang);
    const apiSelect = document.getElementById('api-select');
    const selectedApi = apiSelect ? apiSelect.value : '/v3/api-docs';
    showLoader();
    setTimeout(() => {
        initRedoc(selectedApi);
    }, 100);
}

function updateTextContent(lang) {
    fetch(`/api/v1/locales`, {
        headers: { 'Accept-Language': lang }
    })
        .then(response => response.json())
        .then(messages => {
            document.getElementById('api-docs-title').textContent = messages['api-docs.title'];
            document.getElementById('api-docs-topbar-title').textContent = messages['api-docs.topbar.title'];

            // Update language selection options
            const languageDropdownContent = document.querySelector('.language-dropdown-content');
            languageDropdownContent.querySelector('a[onclick="changeLanguage(\'en\')"]').textContent = messages['api-docs.language.english'];
            languageDropdownContent.querySelector('a[onclick="changeLanguage(\'tr\')"]').textContent = messages['api-docs.language.turkish'];

            // Update theme selection options
            themeToggle.querySelector('.sun').title = messages['api-docs.theme.light'];
            themeToggle.querySelector('.moon').title = messages['api-docs.theme.dark'];
        }).catch((error) => {
        console.error('Error fetching localization messages:', error);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const langCookie = getCookie('lang');
    const lang = langCookie || navigator.language || 'en';
    document.documentElement.lang = lang;
    const languageSelect = document.getElementById('language-select');
    if (languageSelect) {
        languageSelect.value = lang.split('-')[0];
    }
    updateTextContent(lang);
});