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
    document.cookie = `theme=${newTheme};path=/`;
    applyTheme(newTheme);
    showLoader();
    setTimeout(() => {
        initRedoc();
        hideLoader();
    }, 100);
}

document.addEventListener('DOMContentLoaded', () => {
    const theme = getCookie('theme') || 'light';
    applyTheme(theme);
});