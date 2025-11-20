window.addEventListener('load', () => {
    if (!window.Auth) {
        console.error('Módulo de autenticación no disponible.');
        return;
    }

    const PACIENTE_API = '/pacientes';
    const ODONTOLOGO_API = '/odontologos';
    const TURNO_API = '/turnos';

    const pacienteState = new Map();
    const odontologoState = new Map();
    const turnoState = new Map();

    // Pacientes - elementos
    const pacienteForm = document.getElementById('pacienteForm');
    const pacienteTableBody = document.getElementById('pacienteTableBody');
    const pacienteReloadBtn = document.getElementById('pacientesReloadBtn');
    const pacienteSearchBtn = document.getElementById('pacienteSearchBtn');
    const pacienteSearchEmail = document.getElementById('pacienteSearchEmail');
    const pacienteFormTitle = document.getElementById('pacienteFormTitle');
    const pacienteSubmitBtn = document.getElementById('pacienteSubmitBtn');
    const pacienteCancelEditBtn = document.getElementById('pacienteCancelEdit');
    const pacienteAlert = document.getElementById('pacienteAlert');

    // Odontólogos - elementos
    const odontologoForm = document.getElementById('odontologoForm');
    const odontologoTableBody = document.getElementById('odontologoTableBody');
    const odontologoReloadBtn = document.getElementById('odontologosReloadBtn');
    const odontologoSearchBtn = document.getElementById('odontologoSearchBtn');
    const odontologoSearchMatricula = document.getElementById('odontologoSearchMatricula');
    const odontologoFormTitle = document.getElementById('odontologoFormTitle');
    const odontologoSubmitBtn = document.getElementById('odontologoSubmitBtn');
    const odontologoCancelEditBtn = document.getElementById('odontologoCancelEdit');
    const odontologoAlert = document.getElementById('odontologoAlert');

    // Turnos - elementos
    const turnoForm = document.getElementById('turnoForm');
    const turnoTableBody = document.getElementById('turnoTableBody');
    const turnoReloadBtn = document.getElementById('turnosReloadBtn');
    const turnoFormTitle = document.getElementById('turnoFormTitle');
    const turnoSubmitBtn = document.getElementById('turnoSubmitBtn');
    const turnoCancelEditBtn = document.getElementById('turnoCancelEdit');
    const turnoAlert = document.getElementById('turnoAlert');
    const turnoFechaInput = turnoForm?.querySelector('[name="fecha"]');
    const turnoPacienteSelect = turnoForm?.querySelector('[name="pacienteId"]');
    const turnoOdontologoSelect = turnoForm?.querySelector('[name="odontologoId"]');

    inicializarTabs();
    inicializarPacientes();
    inicializarOdontologos();
    inicializarTurnos();

    cargarPacientes();
    cargarOdontologos();
    cargarTurnos();

    function inicializarTabs() {
        const tabsTriggerList = document.querySelectorAll('#managementTabs button[data-bs-toggle="pill"]');
        const hash = window.location.hash;

        if (hash) {
            const button = document.querySelector(`#managementTabs button[data-bs-target="${hash}"]`);
            if (button && window.bootstrap?.Tab) {
                new bootstrap.Tab(button).show();
            }
        }

        tabsTriggerList.forEach(tab => {
            tab.addEventListener('shown.bs.tab', event => {
                history.replaceState(null, '', `${window.location.pathname}${event.target.dataset.bsTarget}`);
            });
        });
    }

    function inicializarPacientes() {
        pacienteForm?.addEventListener('submit', manejarEnvioPaciente);
        pacienteReloadBtn?.addEventListener('click', () => cargarPacientes(true));
        pacienteCancelEditBtn?.addEventListener('click', () => reiniciarFormularioPacientes());
        pacienteSearchBtn?.addEventListener('click', manejarBusquedaPaciente);
        pacienteSearchEmail?.addEventListener('keyup', evento => {
            if (evento.key === 'Enter') {
                manejarBusquedaPaciente(evento);
            }
        });

        pacienteTableBody?.addEventListener('click', evento => {
            const boton = evento.target.closest('button[data-action]');
            if (!boton) {
                return;
            }

            const accion = boton.dataset.action;
            const id = Number(boton.dataset.id);

            if (!id) {
                return;
            }

            if (accion === 'edit') {
                const paciente = pacienteState.get(id);
                if (paciente) {
                    completarFormularioPaciente(paciente);
                }
            } else if (accion === 'delete') {
                eliminarPaciente(id);
            }
        });
    }

    function inicializarOdontologos() {
        odontologoForm?.addEventListener('submit', manejarEnvioOdontologo);
        odontologoReloadBtn?.addEventListener('click', () => cargarOdontologos(true));
        odontologoCancelEditBtn?.addEventListener('click', () => reiniciarFormularioOdontologos());
        odontologoSearchBtn?.addEventListener('click', manejarBusquedaOdontologo);
        odontologoSearchMatricula?.addEventListener('keyup', evento => {
            if (evento.key === 'Enter') {
                manejarBusquedaOdontologo(evento);
            }
        });

        odontologoTableBody?.addEventListener('click', evento => {
            const boton = evento.target.closest('button[data-action]');
            if (!boton) {
                return;
            }

            const accion = boton.dataset.action;
            const id = Number(boton.dataset.id);

            if (!id) {
                return;
            }

            if (accion === 'edit') {
                const odontologo = odontologoState.get(id);
                if (odontologo) {
                    completarFormularioOdontologo(odontologo);
                }
            } else if (accion === 'delete') {
                eliminarOdontologo(id);
            }
        });
    }

    function inicializarTurnos() {
        turnoForm?.addEventListener('submit', manejarEnvioTurno);
        turnoReloadBtn?.addEventListener('click', () => cargarTurnos(true));
        turnoCancelEditBtn?.addEventListener('click', () => reiniciarFormularioTurnos());

        turnoTableBody?.addEventListener('click', evento => {
            const boton = evento.target.closest('button[data-action]');
            if (!boton) {
                return;
            }

            const accion = boton.dataset.action;
            const id = Number(boton.dataset.id);

            if (!id) {
                return;
            }

            if (accion === 'edit') {
                const turno = turnoState.get(id);
                if (turno) {
                    completarFormularioTurno(turno);
                }
            } else if (accion === 'delete') {
                eliminarTurno(id);
            }
        });
    }

    async function cargarPacientes(mostrarMensaje = false) {
        if (!pacienteTableBody) {
            return;
        }
        try {
            const respuesta = await Auth.authFetch(PACIENTE_API);
            if (!respuesta.ok) {
                const mensaje = await respuesta.text();
                throw new Error(mensaje || 'No se pudieron obtener los pacientes.');
            }
            const data = await respuesta.json();
            const pacientes = Array.isArray(data) ? data : [];
            pacienteState.clear();
            pacienteTableBody.innerHTML = '';

            if (!pacientes.length) {
                pacienteTableBody.innerHTML = `
                    <tr>
                        <td colspan="7" class="text-center text-muted py-4">
                            No hay pacientes registrados.
                        </td>
                    </tr>
                `;
            } else {
                pacientes.forEach(paciente => {
                    if (paciente && paciente.id != null) {
                        pacienteState.set(Number(paciente.id), paciente);
                    }
                    const domicilio = paciente?.domicilio || {};
                    const fila = document.createElement('tr');
                    fila.innerHTML = `
                        <td class="text-muted">${paciente?.id ?? '-'}</td>
                        <td>${formatearTexto(paciente?.nombre)}</td>
                        <td>${formatearTexto(paciente?.apellido)}</td>
                        <td>${paciente?.email ?? '-'}</td>
                        <td>${paciente?.numeroContacto ?? '-'}</td>
                        <td>${formatearFecha(paciente?.fechaIngreso)}</td>
                        <td>
                            <div class="d-flex gap-2 justify-content-end">
                                <button class="btn btn-sm btn-outline-primary" data-action="edit" data-id="${paciente?.id}">
                                    Editar
                                </button>
                                <button class="btn btn-sm btn-outline-danger" data-action="delete" data-id="${paciente?.id}">
                                    Eliminar
                                </button>
                            </div>
                        </td>
                    `;
                    fila.title = [
                        domicilio.calle && `Calle: ${domicilio.calle} ${domicilio.numero ?? ''}`.trim(),
                        domicilio.localidad && `Localidad: ${domicilio.localidad}`,
                        domicilio.provincia && `Provincia: ${domicilio.provincia}`
                    ].filter(Boolean).join(' • ');
                    pacienteTableBody.appendChild(fila);
                });
            }
            actualizarOpcionesTurnoSelects();
            renderizarTurnos();
            if (mostrarMensaje) {
                mostrarAlerta(pacienteAlert, 'Lista de pacientes actualizada.');
            }
        } catch (error) {
            console.error(error);
            mostrarAlerta(pacienteAlert, error.message, 'danger');
        }
    }

    async function manejarEnvioPaciente(evento) {
        evento.preventDefault();
        if (!pacienteForm) {
            return;
        }

        const datosFormulario = new FormData(pacienteForm);
        const id = datosFormulario.get('id');
        const payload = construirPayloadPaciente(datosFormulario);

        if (!payload) {
            mostrarAlerta(pacienteAlert, 'Por favor completa los campos obligatorios.', 'warning');
            return;
    }

        const esActualizacion = Boolean(id);
        const url = esActualizacion ? `${PACIENTE_API}/${id}` : PACIENTE_API;
        const metodo = esActualizacion ? 'PUT' : 'POST';

        try {
            const respuesta = await Auth.authFetch(url, {
                method: metodo,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!respuesta.ok) {
                const mensaje = await respuesta.text();
                throw new Error(mensaje || 'No se pudo guardar el paciente. Verifica los datos e intenta nuevamente.');
            }

            const mensaje = esActualizacion ? 'Paciente actualizado con éxito.' : 'Paciente creado con éxito.';
            mostrarAlerta(pacienteAlert, mensaje);
            reiniciarFormularioPacientes();
            await cargarPacientes();
        } catch (error) {
            console.error(error);
            mostrarAlerta(pacienteAlert, error.message, 'danger');
        }
    }

    function construirPayloadPaciente(formData) {
        const nombre = formData.get('nombre')?.trim();
        const apellido = formData.get('apellido')?.trim();
        const email = formData.get('email')?.trim();
        const fechaIngreso = formData.get('fechaIngreso');
        const contactoRaw = formData.get('numeroContacto');
        const domicilioCalle = formData.get('domicilioCalle')?.trim();
        const domicilioNumeroRaw = formData.get('domicilioNumero');
        const domicilioLocalidad = formData.get('domicilioLocalidad')?.trim();
        const domicilioProvincia = formData.get('domicilioProvincia')?.trim();

        if (!nombre || !apellido || !email || !fechaIngreso || !domicilioCalle || !domicilioLocalidad || !domicilioProvincia) {
            return null;
        }

        const payload = {
            nombre,
            apellido,
            email,
            fechaIngreso,
            numeroContacto: contactoRaw ? Number(contactoRaw) : null,
            domicilio: {
                calle: domicilioCalle,
                numero: domicilioNumeroRaw ? Number(domicilioNumeroRaw) : null,
                localidad: domicilioLocalidad,
                provincia: domicilioProvincia
            }
        };

        const id = formData.get('id');
        if (id) {
            payload.id = Number(id);
        }

        const domicilioId = formData.get('domicilioId');
        if (domicilioId) {
            payload.domicilio.id = Number(domicilioId);
        }

        return payload;
    }

    function completarFormularioPaciente(paciente) {
        if (!pacienteForm) {
            return;
        }

        pacienteForm.reset();
        pacienteForm.querySelector('[name="id"]').value = paciente?.id ?? '';
        pacienteForm.querySelector('[name="nombre"]').value = paciente?.nombre ?? '';
        pacienteForm.querySelector('[name="apellido"]').value = paciente?.apellido ?? '';
        pacienteForm.querySelector('[name="email"]').value = paciente?.email ?? '';
        pacienteForm.querySelector('[name="numeroContacto"]').value = paciente?.numeroContacto ?? '';
        pacienteForm.querySelector('[name="fechaIngreso"]').value = paciente?.fechaIngreso ?? '';

        const domicilio = paciente?.domicilio || {};
        pacienteForm.querySelector('[name="domicilioId"]').value = domicilio.id ?? '';
        pacienteForm.querySelector('[name="domicilioCalle"]').value = domicilio.calle ?? '';
        pacienteForm.querySelector('[name="domicilioNumero"]').value = domicilio.numero ?? '';
        pacienteForm.querySelector('[name="domicilioLocalidad"]').value = domicilio.localidad ?? '';
        pacienteForm.querySelector('[name="domicilioProvincia"]').value = domicilio.provincia ?? '';

        if (pacienteFormTitle) {
            pacienteFormTitle.textContent = 'Editar paciente';
        }
        if (pacienteSubmitBtn) {
            pacienteSubmitBtn.textContent = 'Actualizar paciente';
        }
        pacienteCancelEditBtn?.classList.remove('d-none');
    }

    function reiniciarFormularioPacientes() {
        if (!pacienteForm) {
            return;
        }
        pacienteForm.reset();
        pacienteForm.querySelector('[name="id"]').value = '';
        pacienteForm.querySelector('[name="domicilioId"]').value = '';
        if (pacienteFormTitle) {
            pacienteFormTitle.textContent = 'Nuevo paciente';
        }
        if (pacienteSubmitBtn) {
            pacienteSubmitBtn.textContent = 'Guardar paciente';
        }
        pacienteCancelEditBtn?.classList.add('d-none');
    }

    async function eliminarPaciente(id) {
        if (!confirm('¿Estás seguro de eliminar este paciente?')) {
            return;
        }
        try {
            const respuesta = await Auth.authFetch(`${PACIENTE_API}/${id}`, { method: 'DELETE' });
            if (!respuesta.ok) {
                const mensaje = await respuesta.text();
                throw new Error(mensaje || 'No se pudo eliminar el paciente.');
            }
            mostrarAlerta(pacienteAlert, 'Paciente eliminado.');
            if (pacienteForm?.querySelector('[name="id"]').value == id) {
                reiniciarFormularioPacientes();
            }
            await cargarPacientes();
        } catch (error) {
            console.error(error);
            mostrarAlerta(pacienteAlert, error.message, 'danger');
        }
    }

    async function manejarBusquedaPaciente(evento) {
        evento.preventDefault();
        if (!pacienteSearchEmail) {
            return;
        }
        const email = pacienteSearchEmail.value.trim();
        if (!email) {
            mostrarAlerta(pacienteAlert, 'Ingresá un email para buscar.', 'warning');
            return;
        }

        try {
            const respuesta = await Auth.authFetch(`${PACIENTE_API}/email/${encodeURIComponent(email)}`);
            if (respuesta.status === 404) {
                mostrarAlerta(pacienteAlert, 'No se encontró un paciente con ese email.', 'warning');
                return;
            }
            if (!respuesta.ok) {
                const mensaje = await respuesta.text();
                throw new Error(mensaje || 'Ocurrió un error al buscar el paciente.');
            }
            const paciente = await respuesta.json();
            completarFormularioPaciente(paciente);
            mostrarAlerta(pacienteAlert, 'Paciente encontrado y cargado para editar.');
        } catch (error) {
            console.error(error);
            mostrarAlerta(pacienteAlert, error.message, 'danger');
        }
    }

    async function cargarOdontologos(mostrarMensaje = false) {
        if (!odontologoTableBody) {
            return;
        }
        try {
            const respuesta = await Auth.authFetch(ODONTOLOGO_API);
            if (!respuesta.ok) {
                const mensaje = await respuesta.text();
                throw new Error(mensaje || 'No se pudieron obtener los odontólogos.');
            }
            const data = await respuesta.json();
            const odontologos = Array.isArray(data) ? data : [];
            odontologoState.clear();
            odontologoTableBody.innerHTML = '';

            if (!odontologos.length) {
                odontologoTableBody.innerHTML = `
                    <tr>
                        <td colspan="5" class="text-center text-muted py-4">
                            No hay odontólogos registrados.
                        </td>
                    </tr>
                `;
            } else {
                odontologos.forEach(odontologo => {
                    if (odontologo && odontologo.id != null) {
                        odontologoState.set(Number(odontologo.id), odontologo);
                    }
                    const fila = document.createElement('tr');
                    fila.innerHTML = `
                        <td class="text-muted">${odontologo?.id ?? '-'}</td>
                        <td>${formatearTexto(odontologo?.nombre)}</td>
                        <td>${formatearTexto(odontologo?.apellido)}</td>
                        <td>${odontologo?.matricula ?? '-'}</td>
                        <td>
                            <div class="d-flex gap-2 justify-content-end">
                                <button class="btn btn-sm btn-outline-primary" data-action="edit" data-id="${odontologo?.id}">
                                    Editar
                                </button>
                                <button class="btn btn-sm btn-outline-danger" data-action="delete" data-id="${odontologo?.id}">
                                    Eliminar
                                </button>
                            </div>
                        </td>
                    `;
                    odontologoTableBody.appendChild(fila);
                });
            }
            actualizarOpcionesTurnoSelects();
            renderizarTurnos();
            if (mostrarMensaje) {
                mostrarAlerta(odontologoAlert, 'Lista de odontólogos actualizada.');
            }
        } catch (error) {
            console.error(error);
            mostrarAlerta(odontologoAlert, error.message, 'danger');
        }
    }

    async function manejarEnvioOdontologo(evento) {
        evento.preventDefault();
        if (!odontologoForm) {
            return;
        }

        const datosFormulario = new FormData(odontologoForm);
        const id = datosFormulario.get('id');
        const nombre = datosFormulario.get('nombre')?.trim();
        const apellido = datosFormulario.get('apellido')?.trim();
        const matricula = datosFormulario.get('matricula')?.trim();

        if (!nombre || !apellido || !matricula) {
            mostrarAlerta(odontologoAlert, 'Completá todos los campos para guardar el odontólogo.', 'warning');
            return;
        }

        const payload = { nombre, apellido, matricula };
        if (id) {
            payload.id = Number(id);
        }

        const esActualizacion = Boolean(id);
        const url = esActualizacion ? `${ODONTOLOGO_API}/${id}` : ODONTOLOGO_API;
        const metodo = esActualizacion ? 'PUT' : 'POST';

        try {
            const respuesta = await Auth.authFetch(url, {
                method: metodo,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!respuesta.ok) {
                const mensaje = await respuesta.text();
                throw new Error(mensaje || 'No se pudo guardar el odontólogo. Verifica los datos e intenta nuevamente.');
            }

            const mensaje = esActualizacion ? 'Odontólogo actualizado con éxito.' : 'Odontólogo creado con éxito.';
            mostrarAlerta(odontologoAlert, mensaje);
            reiniciarFormularioOdontologos();
            await cargarOdontologos();
        } catch (error) {
            console.error(error);
            mostrarAlerta(odontologoAlert, error.message, 'danger');
        }
    }

    function completarFormularioOdontologo(odontologo) {
        if (!odontologoForm) {
            return;
        }

        odontologoForm.reset();
        odontologoForm.querySelector('[name="id"]').value = odontologo?.id ?? '';
        odontologoForm.querySelector('[name="nombre"]').value = odontologo?.nombre ?? '';
        odontologoForm.querySelector('[name="apellido"]').value = odontologo?.apellido ?? '';
        odontologoForm.querySelector('[name="matricula"]').value = odontologo?.matricula ?? '';

        if (odontologoFormTitle) {
            odontologoFormTitle.textContent = 'Editar odontólogo';
        }
        if (odontologoSubmitBtn) {
            odontologoSubmitBtn.textContent = 'Actualizar odontólogo';
        }
        odontologoCancelEditBtn?.classList.remove('d-none');
    }

    function reiniciarFormularioOdontologos() {
        if (!odontologoForm) {
            return;
        }
        odontologoForm.reset();
        odontologoForm.querySelector('[name="id"]').value = '';
        if (odontologoFormTitle) {
            odontologoFormTitle.textContent = 'Nuevo odontólogo';
        }
        if (odontologoSubmitBtn) {
            odontologoSubmitBtn.textContent = 'Guardar odontólogo';
        }
        odontologoCancelEditBtn?.classList.add('d-none');
    }

    async function eliminarOdontologo(id) {
        if (!confirm('¿Estás seguro de eliminar este odontólogo?')) {
            return;
        }
        try {
            const respuesta = await Auth.authFetch(`${ODONTOLOGO_API}/${id}`, { method: 'DELETE' });
            if (!respuesta.ok) {
                const mensaje = await respuesta.text();
                throw new Error(mensaje || 'No se pudo eliminar el odontólogo.');
            }
            mostrarAlerta(odontologoAlert, 'Odontólogo eliminado.');
            if (odontologoForm?.querySelector('[name="id"]').value == id) {
                reiniciarFormularioOdontologos();
            }
            await cargarOdontologos();
        } catch (error) {
            console.error(error);
            mostrarAlerta(odontologoAlert, error.message, 'danger');
        }
    }

    async function manejarBusquedaOdontologo(evento) {
        evento.preventDefault();
        if (!odontologoSearchMatricula) {
            return;
        }
        const matricula = odontologoSearchMatricula.value.trim();
        if (!matricula) {
            mostrarAlerta(odontologoAlert, 'Ingresá una matrícula para buscar.', 'warning');
            return;
        }

        try {
            const respuesta = await Auth.authFetch(`${ODONTOLOGO_API}/matricula/${encodeURIComponent(matricula)}`);
            if (respuesta.status === 404) {
                mostrarAlerta(odontologoAlert, 'No se encontró un odontólogo con esa matrícula.', 'warning');
                return;
            }
            if (!respuesta.ok) {
                const mensaje = await respuesta.text();
                throw new Error(mensaje || 'Ocurrió un error al buscar el odontólogo.');
            }
            const odontologo = await respuesta.json();
            completarFormularioOdontologo(odontologo);
            mostrarAlerta(odontologoAlert, 'Odontólogo encontrado y cargado para editar.');
        } catch (error) {
            console.error(error);
            mostrarAlerta(odontologoAlert, error.message, 'danger');
        }
    }

    async function cargarTurnos(mostrarMensaje = false) {
        if (!turnoTableBody) {
            return;
        }
        try {
            const respuesta = await Auth.authFetch(TURNO_API);
            if (!respuesta.ok) {
                const mensaje = await respuesta.text();
                throw new Error(mensaje || 'No se pudieron obtener los turnos.');
            }
            const data = await respuesta.json();
            const turnos = Array.isArray(data) ? data : [];
            turnoState.clear();
            turnos.forEach(turno => {
                if (turno && turno.id != null) {
                    turnoState.set(Number(turno.id), turno);
                }
            });
            renderizarTurnos();
            if (mostrarMensaje) {
                mostrarAlerta(turnoAlert, 'Lista de turnos actualizada.');
            }
        } catch (error) {
            console.error(error);
            mostrarAlerta(turnoAlert, error.message, 'danger');
        }
    }

    function renderizarTurnos() {
        if (!turnoTableBody) {
            return;
        }
        turnoTableBody.innerHTML = '';
        const turnos = Array.from(turnoState.values());

        if (!turnos.length) {
            turnoTableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center text-muted py-4">
                        No hay turnos registrados.
                    </td>
                </tr>
            `;
            return;
        }

        turnos.sort((a, b) => {
            const fechaA = a?.fecha ? new Date(a.fecha).getTime() : 0;
            const fechaB = b?.fecha ? new Date(b.fecha).getTime() : 0;
            return fechaA - fechaB;
        });

        turnos.forEach(turno => {
            const fila = document.createElement('tr');
            fila.innerHTML = `
                <td class="text-muted">${turno?.id ?? '-'}</td>
                <td>${formatearFecha(turno?.fecha)}</td>
                <td>${obtenerNombrePaciente(turno?.pacienteId)}</td>
                <td>${obtenerNombreOdontologo(turno?.odontologoId)}</td>
                <td>
                    <div class="d-flex gap-2 justify-content-end">
                        <button class="btn btn-sm btn-outline-primary" data-action="edit" data-id="${turno?.id}">
                            Editar
                        </button>
                        <button class="btn btn-sm btn-outline-danger" data-action="delete" data-id="${turno?.id}">
                            Eliminar
                        </button>
                    </div>
                </td>
            `;
            turnoTableBody.appendChild(fila);
        });
    }

    function actualizarOpcionesTurnoSelects() {
        if (!turnoPacienteSelect || !turnoOdontologoSelect) {
            return;
        }

        const pacienteSeleccionado = turnoPacienteSelect.value;
        const odontologoSeleccionado = turnoOdontologoSelect.value;

        turnoPacienteSelect.innerHTML = '<option value="">Seleccioná un paciente</option>';
        turnoOdontologoSelect.innerHTML = '<option value="">Seleccioná un odontólogo</option>';

        const pacientesOrdenados = Array.from(pacienteState.values()).sort((a, b) => {
            const apellidoA = (a?.apellido || '').toLowerCase();
            const apellidoB = (b?.apellido || '').toLowerCase();
            return apellidoA.localeCompare(apellidoB);
        });

        pacientesOrdenados.forEach(paciente => {
            if (paciente?.id == null) {
                return;
            }
            const option = document.createElement('option');
            option.value = paciente.id;
            option.textContent = `${formatearTexto(paciente.nombre)} ${formatearTexto(paciente.apellido)} (ID ${paciente.id})`;
            turnoPacienteSelect.appendChild(option);
        });

        const odontologosOrdenados = Array.from(odontologoState.values()).sort((a, b) => {
            const apellidoA = (a?.apellido || '').toLowerCase();
            const apellidoB = (b?.apellido || '').toLowerCase();
            return apellidoA.localeCompare(apellidoB);
        });

        odontologosOrdenados.forEach(odontologo => {
            if (odontologo?.id == null) {
                return;
            }
            const option = document.createElement('option');
            option.value = odontologo.id;
            option.textContent = `${formatearTexto(odontologo.nombre)} ${formatearTexto(odontologo.apellido)} (MAT ${odontologo.matricula ?? '-'})`;
            turnoOdontologoSelect.appendChild(option);
        });

        if (pacienteSeleccionado) {
            turnoPacienteSelect.value = pacienteSeleccionado;
        }
        if (odontologoSeleccionado) {
            turnoOdontologoSelect.value = odontologoSeleccionado;
        }
    }

    async function manejarEnvioTurno(evento) {
        evento.preventDefault();
        if (!turnoForm) {
            return;
        }

        const datosFormulario = new FormData(turnoForm);
        const id = datosFormulario.get('id');
        const payload = construirPayloadTurno(datosFormulario);

        if (!payload) {
            mostrarAlerta(turnoAlert, 'Completá todos los campos antes de guardar el turno.', 'warning');
            return;
        }

        const esActualizacion = Boolean(id);
        const url = esActualizacion ? `${TURNO_API}/${id}` : TURNO_API;
        const metodo = esActualizacion ? 'PUT' : 'POST';

        try {
            const respuesta = await Auth.authFetch(url, {
                method: metodo,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!respuesta.ok) {
                const mensaje = await respuesta.text();
                throw new Error(mensaje || 'No se pudo guardar el turno. Verifica los datos e intenta nuevamente.');
            }

            const mensaje = esActualizacion ? 'Turno actualizado con éxito.' : 'Turno creado con éxito.';
            mostrarAlerta(turnoAlert, mensaje);
            reiniciarFormularioTurnos();
            await cargarTurnos();
        } catch (error) {
            console.error(error);
            mostrarAlerta(turnoAlert, error.message, 'danger');
        }
    }

    function construirPayloadTurno(formData) {
        const fecha = formData.get('fecha');
        const pacienteId = formData.get('pacienteId');
        const odontologoId = formData.get('odontologoId');

        if (!fecha || !pacienteId || !odontologoId) {
            return null;
        }

        const payload = {
            fecha,
            pacienteId: Number(pacienteId),
            odontologoId: Number(odontologoId)
        };

        const id = formData.get('id');
        if (id) {
            payload.id = Number(id);
        }

        return payload;
    }

    function completarFormularioTurno(turno) {
        if (!turnoForm) {
            return;
        }

        turnoForm.reset();
        turnoForm.querySelector('[name="id"]').value = turno?.id ?? '';
        if (turnoFechaInput) {
            turnoFechaInput.value = turno?.fecha ?? '';
        }
        if (turnoPacienteSelect) {
            turnoPacienteSelect.value = turno?.pacienteId ?? '';
        }
        if (turnoOdontologoSelect) {
            turnoOdontologoSelect.value = turno?.odontologoId ?? '';
        }

        if (turnoFormTitle) {
            turnoFormTitle.textContent = 'Editar turno';
        }
        if (turnoSubmitBtn) {
            turnoSubmitBtn.textContent = 'Actualizar turno';
        }
        turnoCancelEditBtn?.classList.remove('d-none');
    }

    function reiniciarFormularioTurnos() {
        if (!turnoForm) {
            return;
        }
        turnoForm.reset();
        turnoForm.querySelector('[name="id"]').value = '';
        if (turnoFormTitle) {
            turnoFormTitle.textContent = 'Nuevo turno';
        }
        if (turnoSubmitBtn) {
            turnoSubmitBtn.textContent = 'Guardar turno';
        }
        turnoCancelEditBtn?.classList.add('d-none');
        actualizarOpcionesTurnoSelects();
    }

    async function eliminarTurno(id) {
        if (!confirm('¿Estás seguro de eliminar este turno?')) {
            return;
        }
        try {
            const respuesta = await Auth.authFetch(`${TURNO_API}/${id}`, { method: 'DELETE' });
            if (!respuesta.ok) {
                const mensaje = await respuesta.text();
                throw new Error(mensaje || 'No se pudo eliminar el turno.');
            }
            mostrarAlerta(turnoAlert, 'Turno eliminado.');
            if (turnoForm?.querySelector('[name="id"]').value == id) {
                reiniciarFormularioTurnos();
            }
            await cargarTurnos();
        } catch (error) {
            console.error(error);
            mostrarAlerta(turnoAlert, error.message, 'danger');
        }
    }

    function obtenerNombrePaciente(id) {
        if (id == null) {
            return '-';
        }
        const paciente = pacienteState.get(Number(id));
        if (!paciente) {
            return `Paciente #${id}`;
        }
        return `${formatearTexto(paciente.nombre)} ${formatearTexto(paciente.apellido)} (ID ${paciente.id})`;
    }

    function obtenerNombreOdontologo(id) {
        if (id == null) {
            return '-';
        }
        const odontologo = odontologoState.get(Number(id));
        if (!odontologo) {
            return `Odontólogo #${id}`;
        }
        return `${formatearTexto(odontologo.nombre)} ${formatearTexto(odontologo.apellido)} (MAT ${odontologo.matricula ?? '-'})`;
    }

    function formatearTexto(valor) {
        if (!valor) {
            return '-';
        }
        return valor.toString().trim();
    }

    function formatearFecha(fecha) {
        if (!fecha) {
            return '-';
        }
        try {
            const date = new Date(fecha);
            if (Number.isNaN(date.getTime())) {
                return fecha;
            }
            return date.toISOString().split('T')[0];
        } catch {
            return fecha;
        }
    }

    function mostrarAlerta(contenedor, mensaje, tipo = 'success') {
        if (!contenedor) {
            return;
        }
        contenedor.innerHTML = `
            <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
            </div>
        `;

        setTimeout(() => {
            const alerta = contenedor.querySelector('.alert');
            if (alerta && alerta.classList.contains('show')) {
                alerta.classList.remove('show');
                alerta.classList.add('fade');
                setTimeout(() => {
                    contenedor.innerHTML = '';
                }, 200);
            }
        }, 4000);
    }
});
