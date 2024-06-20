<<<<<<< HEAD
usernameRef.addEventListener('input', () => {
    if (isUsernameValid()) {
        message_alert.style.visibility = 'hidden';
=======
usernameRef.addEventListener('input',  () => {
    if (!isUsernameValid()) {
        messageRef.style.visibility = 'hidden';
>>>>>>> 96d9ec025cdefe7dd03878e166dc42cba9564c69
        usernameRef.style.cssText = 'border-color: red; background-color: #FFB6C1';
    } else {
        usernameRef.style.cssText = 'border-color: green; background-color: #90EE90';
    }
});

passwordRef.addEventListener('input',  () => {
    if (!isPasswordValid()) {
        messageRef.style.visibility = 'hidden';
        passwordRef.style.cssText = 'border-color: red; background-color: #FFB6C1';
    } else {
        passwordRef.style.cssText = 'border-color: green; background-color: #90EE90';
    }
});