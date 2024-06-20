usernameRef.addEventListener('input', () => {
    if (isUsernameValid()) {
        message_alert.style.visibility = 'hidden';
        usernameRef.style.cssText = 'border-color: red; background-color: #FFB6C1';
    } else {
        usernameRef.style.cssText = 'border-color: green; background-color: #90EE90';
    }
});

passwordRef.addEventListener('input',  () => {
    if (!isPasswordValid()) {
        message_alert.style.visibility = 'hidden';
        passwordRef.style.cssText = 'border-color: red; background-color: #FFB6C1';
    } else {
        passwordRef.style.cssText = 'border-color: green; background-color: #90EE90';
    }
});