function changeLanguage(language) {
    document.cookie = `lang=${language};path=/`;
    updateTextContent(language);
    showLoader();
    setTimeout(() => {
        initRedoc();
        hideLoader();
    }, 100);
}

function updateTextContent(lang) {
    fetch(`/api/locales`, {
        headers: { 'Accept-Language': lang }
    })
        .then(response => response.json())
        .then(messages => {
            document.getElementById('api-docs-title').textContent = messages['api-docs.title'];
            document.getElementById('api-docs-topbar-title').textContent = messages['api-docs.topbar.title'];

            const apiSelect = document.getElementById('api-select');
            apiSelect.options[0].textContent = messages['api-docs.api.ai'];

            const languageSelect = document.getElementById('language-select');
            languageSelect.options[0].textContent = messages['api-docs.language.english'];
            languageSelect.options[1].textContent = messages['api-docs.language.turkish'];

            const themeToggle = document.getElementById('theme-toggle');
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
    updateTextContent(lang);
});