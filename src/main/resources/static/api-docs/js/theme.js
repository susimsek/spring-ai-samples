const themeToggle = document.getElementById('theme-toggle');

function applyTheme(theme) {
    document.body.className = `theme-${theme}`;
    // Change theme icon based on theme
    if (theme === 'dark') {
        themeToggle.classList.add('theme-dark');
    } else {
        themeToggle.classList.remove('theme-dark');
    }
}

function toggleTheme() {
    const currentTheme = document.body.classList.contains('theme-dark') ? 'dark' : 'light';
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    setCookie('theme', newTheme, 7);
    applyTheme(newTheme);
    const apiSelect = document.getElementById('api-select');
    const selectedApi = apiSelect ? apiSelect.value : '/v3/api-docs';
    showLoader();
    setTimeout(() => {
        initRedoc(selectedApi);
        hideLoader();
    }, 100);
}

document.addEventListener('DOMContentLoaded', () => {
    const theme = getCookie('theme') || 'light';
    applyTheme(theme);
});