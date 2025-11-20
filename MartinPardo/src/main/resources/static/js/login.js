document.addEventListener('DOMContentLoaded', () => {
    const protectedContent = document.getElementById('protectedContent');
    const loginSection = document.getElementById('loginSection');
    const logoutButtons = document.querySelectorAll('[data-action="logout"]');
    const loginForm = document.getElementById('loginForm');
    const loginAlert = document.getElementById('loginAlert');
    const statusBadge = document.getElementById('sessionStatusBadge');

    function mostrarContenidoAutenticado() {
        protectedContent?.classList.remove('d-none');
        loginSection?.classList.add('d-none');
        actualizarBadge(true);
    }

    function mostrarFormularioLogin() {
        protectedContent?.classList.add('d-none');
        loginSection?.classList.remove('d-none');
        actualizarBadge(false);
        limpiarFormulario();
    }

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

    function limpiarFormulario() {
        if (!loginForm) {
            return;
        }
        loginForm.reset();
        if (loginAlert) {
            loginAlert.innerHTML = '';
        }
    }

    function mostrarMensaje(mensaje, tipo = 'danger') {
        if (!loginAlert) {
            return;
        }
        loginAlert.innerHTML = `
            <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
            </div>
        `;
    }

    if (Auth.isAuthenticated()) {
        mostrarContenidoAutenticado();
    } else {
        mostrarFormularioLogin();
    }

    const params = new URLSearchParams(window.location.search);
    if (params.get('login') === 'required') {
        mostrarMensaje('Necesitas iniciar sesión para continuar.', 'warning');
    }

    const unsubscribe = Auth.onAuthChange(isLoggedIn => {
        if (isLoggedIn) {
            mostrarContenidoAutenticado();
        } else {
            mostrarFormularioLogin();
        }
    });

    loginForm?.addEventListener('submit', async event => {
        event.preventDefault();
        const formData = new FormData(loginForm);
        const username = formData.get('username')?.toString().trim();
        const password = formData.get('password')?.toString().trim();

        if (!username || !password) {
            mostrarMensaje('Ingresá usuario y contraseña.');
            return;
        }

        loginForm.classList.add('was-validated');
        loginForm.querySelector('button[type="submit"]')?.setAttribute('disabled', 'disabled');

        try {
            await Auth.login({ username, password });
            const hash = window.location.hash;
            const redirectHash = hash || '#';
            window.history.replaceState({}, '', `${window.location.pathname}`);
            if (redirectHash && redirectHash !== '#') {
                window.location.hash = redirectHash;
            }
        } catch (error) {
            console.error(error);
            mostrarMensaje(error.message || 'No se pudo iniciar sesión.');
        } finally {
            loginForm.querySelector('button[type="submit"]')?.removeAttribute('disabled');
        }
    });

    logoutButtons.forEach(button => {
        button.addEventListener('click', async event => {
            event.preventDefault();
            await Auth.logout();
        });
    });

    window.addEventListener('beforeunload', () => {
        unsubscribe();
    });
});

