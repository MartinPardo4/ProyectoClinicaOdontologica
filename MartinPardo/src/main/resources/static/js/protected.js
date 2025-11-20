document.addEventListener('DOMContentLoaded', () => {
    Auth.requireAuth({ preserveHash: true });

    const logoutButtons = document.querySelectorAll('[data-action="logout"]');
    const statusBadge = document.getElementById('sessionStatusBadge');

    function actualizarBadge(isLoggedIn) {
        if (!statusBadge) {
            return;
        }
        if (isLoggedIn) {
            statusBadge.textContent = 'Sesión activa';
            statusBadge.classList.remove('bg-secondary');
            statusBadge.classList.add('bg-success');
        } else {
            statusBadge.textContent = 'Sesión requerida';
            statusBadge.classList.remove('bg-success');
            statusBadge.classList.add('bg-secondary');
        }
    }

    actualizarBadge(Auth.isAuthenticated());

    logoutButtons.forEach(button => {
        button.addEventListener('click', async event => {
            event.preventDefault();
            await Auth.logout();
            window.location.replace('index.html?login=required');
        });
    });

    Auth.onAuthChange(isLoggedIn => {
        actualizarBadge(isLoggedIn);
        if (!isLoggedIn) {
            window.location.replace('index.html?login=required');
        }
    });
});




