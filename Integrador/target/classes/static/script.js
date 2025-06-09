//Login
const container = document.getElementById("container");
const registerBtn = document.getElementById("register");
const loginBtn = document.getElementById("login");

console.log("Container element:", container);
console.log("Register button:", registerBtn);
console.log("Login button:", loginBtn);

if (container && registerBtn && loginBtn) {
  registerBtn.addEventListener("click", () => {
    container.classList.add("active");
    console.log("Register button clicked. 'active' class added.");
  });

  loginBtn.addEventListener("click", () => {
    container.classList.remove("active");
    console.log("Login button clicked. 'active' class removed.");
  });
  console.log("Panel toggle JavaScript loaded and listeners attached.");
} else {
  console.error("ERROR: One or more elements for panel toggle not found. Check HTML IDs.");
}

//Modal
const modal = document.getElementById('myModal');
const closeButton = document.querySelector('.close-button');
const modalMessage = document.getElementById('modalMessage');
const modalActionButton = document.getElementById('modalActionButton');

let currentRedirectUrl = null;

console.log("Modal element:", modal);
console.log("Close button:", closeButton);
console.log("Modal message element:", modalMessage);
console.log("Modal action button:", modalActionButton);


if (modal && closeButton && modalMessage && modalActionButton) {
    console.log("Modal elements found. Initializing modal functions.");

    function showModal(message, isSuccess, redirectAfterClose) {
        console.log("showModal called with message:", message);
        modalMessage.textContent = message;
        if (isSuccess) {
            modalMessage.classList.remove('error-message');
            modalMessage.classList.add('success-message');
        } else {
            modalMessage.classList.remove('success-message');
            modalMessage.classList.add('error-message');
        }
        modal.classList.remove('modal-hidden');
        modal.style.display = 'flex';
        currentRedirectUrl = redirectAfterClose;
    }

    function closeModal() {
        console.log("closeModal called.");
        modal.classList.add('modal-hidden');
        modal.style.display = 'none';
        if (currentRedirectUrl) {
            console.log("Redirecting to:", currentRedirectUrl);
            window.location.href = currentRedirectUrl;
        } else {
            console.log("No redirect URL. Staying on current page.");
        }
    }

    closeButton.addEventListener('click', closeModal);
    modalActionButton.addEventListener('click', closeModal);

    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            console.log("Clicked outside modal. Closing.");
            closeModal();
        }
    });
} else {
    console.error("ERROR: One or more modal elements not found. Modal functionality may be broken. Check HTML IDs/Classes.");
}

//Registro
const registerForm = document.getElementById('registerForm');
const registerEmailInput = document.getElementById('registerEmail');
const registerPasswordInput = document.getElementById('registerPassword');

if (registerForm && registerEmailInput && registerPasswordInput) {
    console.log("Register form elements found. Attaching submit listener.");
    registerForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const email = registerEmailInput.value;
        const password = registerPasswordInput.value;

        console.log("Attempting AJAX registration for:", email);

        try {
            const response = await fetch('/register-ajax', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    registerEmail: email,
                    registerPassword: password
                })
            });

            const data = await response.json();
            console.log("AJAX response received:", data);

            if (data.success) {
                registerEmailInput.value = '';
                registerPasswordInput.value = '';
                console.log("Registration successful. Fields cleared.");
            }
            showModal(data.message, data.success, null);

        } catch (error) {
            console.error('Error during AJAX registration:', error);
            showModal('Hubo un error al procesar tu solicitud. Inténtalo de nuevo.', false, null);
        }
    });
} else {
    console.error("ERROR: Register form or its inputs not found. AJAX registration disabled.");
}