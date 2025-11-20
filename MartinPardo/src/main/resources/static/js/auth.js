(() => {
    const TOKEN_KEY = 'jwtToken';
    const AUTH_EVENT = 'auth:changed';

    function getToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function setToken(token) {
        if (token) {
            localStorage.setItem(TOKEN_KEY, token);
        }
        notificarCambio(true);
    }

    function clearToken() {
        localStorage.removeItem(TOKEN_KEY);
        notificarCambio(false);
    }

    function isAuthenticated() {
        return Boolean(getToken());
    }

    function notificarCambio(isLoggedIn) {
        document.dispatchEvent(new CustomEvent(AUTH_EVENT, { detail: { isLoggedIn } }));
    }

    async function authFetch(resource, options = {}) {
        const token = getToken();
        const headers = new Headers(options.headers || {});

        if (token) {
            headers.set('Authorization', `Bearer ${token}`);
        }

        const fetchOptions = {
            ...options,
            headers
        };

        const response = await fetch(resource, fetchOptions);

        if (response.status === 401 || response.status === 403) {
            clearToken();
            throw new Error('Sesión expirada. Por favor inicia sesión nuevamente.');
        }

        return response;
    }

    async function login({ username, password }) {
        const response = await fetch('/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            const mensaje = await extraerMensaje(response);
            throw new Error(mensaje || 'No se pudo iniciar sesión.');
        }

        const data = await response.json();
        if (!data?.token) {
            throw new Error('Respuesta inválida del servidor.');
        }

        setToken(data.token);
        return data;
    }

    async function logout() {
        try {
            await authFetch('/auth/logout', { method: 'POST' });
        } catch (error) {
            console.warn('Error al cerrar sesión:', error);
        } finally {
            clearToken();
        }
    }

    function requireAuth(opciones = {}) {
        if (!isAuthenticated()) {
            const { redirectUrl = 'index.html', preserveHash = false } = opciones;
            const params = new URLSearchParams({ login: 'required' });
            const target = new URL(redirectUrl, window.location.origin);
            target.search = params.toString();
            if (preserveHash && window.location.hash) {
                target.hash = window.location.hash;
            }
            window.location.replace(target.toString());
        }
    }

    function onAuthChange(callback) {
        if (typeof callback !== 'function') {
            return () => {};
        }
        const handler = event => callback(event.detail?.isLoggedIn ?? false);
        document.addEventListener(AUTH_EVENT, handler);
        return () => document.removeEventListener(AUTH_EVENT, handler);
    }

    async function extraerMensaje(response) {
        try {
            const data = await response.json();
            return data?.message || data?.error || '';
        } catch {
            try {
                return await response.text();
            } catch {
                return '';
            }
        }
    }

    window.Auth = {
        getToken,
        setToken,
        clearToken,
        isAuthenticated,
        authFetch,
        login,
        logout,
        requireAuth,
        onAuthChange
    };
})();




