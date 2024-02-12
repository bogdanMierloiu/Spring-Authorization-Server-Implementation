function togglePasswordVisibility(inputId, eyeIconId, eyeSlashIconId) {
    const passwordInput = document.querySelector(`#${inputId}`);
    const eyeIcon = document.getElementById(eyeIconId);
    const eyeSlashIcon = document.getElementById(eyeSlashIconId);

    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        eyeIcon.classList.add('hide');
        eyeSlashIcon.classList.add('show');
        eyeIcon.classList.remove('show');
        eyeSlashIcon.classList.remove('hide');
    } else {
        passwordInput.type = 'password';
        eyeIcon.classList.add('show');
        eyeSlashIcon.classList.remove('show');
        eyeSlashIcon.classList.add('hide');
        eyeIcon.classList.remove('hide');
    }
}

