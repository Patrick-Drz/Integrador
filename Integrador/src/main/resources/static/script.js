document.addEventListener('DOMContentLoaded', () => {
    const container = document.getElementById("container");
    const registerBtn = document.getElementById("register");
    const loginBtn = document.getElementById("login");

    if (container && registerBtn && loginBtn) {
        registerBtn.addEventListener("click", () => container.classList.add("active"));
        loginBtn.addEventListener("click", () => container.classList.remove("active"));
    }

    const modal = document.getElementById('myModal');
    const closeButton = document.querySelector('#myModal .close-button');
    const modalMessage = document.getElementById('modalMessage');
    const modalActionButton = document.getElementById('modalActionButton');
    let currentRedirectUrl = null;

    window.showModal = function(message, isSuccess, redirectAfterClose = null) {
        if (!modal || !modalMessage) return;
        modalMessage.textContent = message;
        modalMessage.className = '';
        modalMessage.classList.add(isSuccess ? 'success-message' : 'error-message');
        modal.style.display = 'flex';
        currentRedirectUrl = redirectAfterClose;
    }

    function closeModal() {
        if (!modal) return;
        modal.style.display = 'none';
        if (currentRedirectUrl) {
            window.location.href = currentRedirectUrl;
        }
    }

    if (modal && closeButton && modalActionButton) {
        closeButton.addEventListener('click', closeModal);
        modalActionButton.addEventListener('click', closeModal);
        window.addEventListener('click', (event) => { if (event.target === modal) closeModal(); });
    }

    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const formData = new FormData(registerForm);
            const body = new URLSearchParams(formData);
            try {
                const response = await fetch('/register-ajax', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: body });
                if (!response.ok && !response.headers.get('content-type')?.includes('application/json')) {
                     showModal('Error del servidor al registrar.', false, null); return;
                }
                const data = await response.json();
                if (data.success) registerForm.reset();
                showModal(data.message, data.success, null);
            } catch (error) {
                 if (error instanceof SyntaxError) showModal('Respuesta inesperada del servidor.', false, null);
                 else showModal('Error de conexión al registrar.', false, null);
            }
        });
    }


    document.querySelectorAll('.add-to-cart-form').forEach(form => {
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            const formData = new FormData(form);
            const body = new URLSearchParams(formData);
            const url = '/user/carrito/add';

            try {
                const response = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: body });

                if (response.status === 403 || response.status === 401) {
                    showModal("Primero debes iniciar sesión para realizar esta acción.", false, null);
                    return;
                }
                if (response.ok) { 
                    const data = await response.json();
                    showModal(data.message, data.success, null);
                } else {
                     showModal('Error del servidor al añadir.', false, null);
                }
            } catch (error) {
                showModal("Primero debes iniciar sesión para realizar esta acción.", false, null);
            }
        });
    });

    document.querySelectorAll('.remove-form').forEach(form => {
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            const itemId = form.querySelector('input[name="itemId"]').value;
            const url = form.getAttribute('action');

            try {
                const response = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: new URLSearchParams({ itemId: itemId }) });

                if (response.status === 403 || response.status === 401) {
                    showModal("Debes iniciar sesión para modificar tu carrito.", false, '/login');
                    return;
                }
                 if (!response.ok && !response.headers.get('content-type')?.includes('application/json')) {
                     showModal('Error del servidor al eliminar.', false, null); return;
                }
                const data = await response.json();
                if (data.success) {
                    const itemRow = document.getElementById('item-row-' + itemId);
                    if (itemRow) itemRow.remove();
                } else {
                    showModal(data.message, false, null);
                }
            } catch (error) {
                if (error instanceof SyntaxError) showModal('Respuesta inesperada del servidor.', false, null);
                 else showModal('Error al eliminar el ítem.', false, null);
            }
        });
    });

    document.querySelectorAll('.update-form').forEach(form => {
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            const itemId = form.querySelector('input[name="itemId"]').value;
            const quantity = form.querySelector('input[name="quantity"]').value;
            const url = form.getAttribute('action');

            try {
                const response = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: new URLSearchParams({ itemId: itemId, quantity: quantity }) });

                if (response.status === 403 || response.status === 401) {
                    showModal("Debes iniciar sesión para modificar tu carrito.", false, '/login');
                    return;
                }
                if (!response.ok && !response.headers.get('content-type')?.includes('application/json')) {
                     showModal('Error del servidor al actualizar.', false, null); return;
                }
                const data = await response.json();
                if (data.success) window.location.reload();
                else showModal(data.message, false, null);
            } catch (error) {
                if (error instanceof SyntaxError) showModal('Respuesta inesperada del servidor.', false, null);
                 else showModal('Error al actualizar el carrito.', false, null);
            }
        });
    });

    const procesarPagoForm = document.getElementById('procesarPagoForm');
    if (procesarPagoForm) {
        procesarPagoForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const url = '/user/carrito/procesarPagoAjax';
            try {
                const response = await fetch(url, { method: 'POST' });

                if (response.status === 403 || response.status === 401) {
                    showModal("Debes iniciar sesión para proceder al pago.", false, '/login');
                    return;
                }
                if (!response.ok && !response.headers.get('content-type')?.includes('application/json')) {
                     showModal('Error del servidor al pagar.', false, null); return;
                }
                const data = await response.json();
                if (data.success) {
                    const carritoContent = document.getElementById('carritoContent');
                    if (carritoContent) carritoContent.innerHTML = '<div class="alert alert-info text-center mt-5"><p>Tu carrito está vacío.</p></div>';
                }
                showModal(data.message, data.success, null);
            } catch (error) {
                 if (error instanceof SyntaxError) showModal('Respuesta inesperada del servidor.', false, null);
                 else showModal('Error al procesar el pago.', false, null);
            }
        });
    }

    const aulaForm = document.getElementById('aulaForm');
     if (aulaForm) {
        aulaForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const formData = new FormData(aulaForm);
            const body = new URLSearchParams(formData);
            const url = '/user/aula/save';

            try {
                const response = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: body });

                if (response.status === 403 || response.status === 401) {
                    showModal("Debes iniciar sesión para guardar tu ubicación.", false, '/login');
                    return;
                }
                 if (response.redirected || response.ok) {
                     window.location.href = response.redirected ? response.url : window.location.href;
                     return;
                 } else {
                     showModal('Error del servidor al guardar ubicación.', false, null); return;
                 }
            } catch (error) {
                 showModal('Error al guardar la ubicación.', false, null);
            }
        });
    }

    const reclamacionForm = document.getElementById('reclamacionForm');
    if (reclamacionForm) {
        reclamacionForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const formData = new FormData(reclamacionForm);
            const jsonData = Object.fromEntries(formData.entries());
            const url = '/user/reclamacion/enviar';

            try {
                const response = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(jsonData) });

                if (response.status === 403 || response.status === 401) {
                    showModal("Primero debes iniciar sesión para realizar esta acción.", false, null);
                    const modalBootstrap = bootstrap.Modal.getInstance(document.getElementById('reclamacionModal'));
                    if(modalBootstrap) modalBootstrap.hide();
                    return;
                }

                if (response.ok) {
                    const data = await response.json();
                    const modalBootstrap = bootstrap.Modal.getInstance(document.getElementById('reclamacionModal'));
                    if (modalBootstrap) modalBootstrap.hide();
                    if (data.success) reclamacionForm.reset();
                    showModal(data.message, data.success, null);
                } else {
                    showModal('Error del servidor al enviar reclamación.', false, null);
                }
            } catch (error) {
                showModal("Primero debes iniciar sesión para realizar esta acción.", false, null);
                const modalBootstrap = bootstrap.Modal.getInstance(document.getElementById('reclamacionModal'));
                if(modalBootstrap) modalBootstrap.hide();
            }
        });
    }
});

    const contactoForm = document.getElementById('contactoForm');
    if (contactoForm) {
        const contactoMessageDiv = document.getElementById('contactoMessage');

        contactoForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            if (contactoMessageDiv) contactoMessageDiv.textContent = '';

            const formData = new FormData(contactoForm);
            const jsonData = Object.fromEntries(formData.entries());

            try {
                const response = await fetch('/user/contacto/enviar', { 
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', },
                    body: JSON.stringify(jsonData),
                });

                if (response.status === 403 || response.status === 401) {
                    showModal("Debes iniciar sesión para enviar un mensaje.", false, '/login');
                    return;
                }

                const data = await response.json();
                
                if (contactoMessageDiv) {
                    contactoMessageDiv.textContent = data.message;
                    contactoMessageDiv.className = data.success ? 'mt-3 text-center text-success' : 'mt-3 text-center text-danger';
                }
                
                if (data.success) {
                    contactoForm.reset();
                }

            } catch (error) {
                if (contactoMessageDiv) {
                    contactoMessageDiv.textContent = 'Error de conexión.';
                    contactoMessageDiv.className = 'mt-3 text-center text-danger';
                }
            }
        });
    }