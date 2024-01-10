//assigning variables 
const form = document.getElementById('form');
const username = document.getElementById('username');
const email = document.getElementById('email');
const password = document.getElementById('password');
const confirmpassword = document.getElementById('password2');


form.addEventListener('submit', e => {
    e.preventDefault();
    checkInputs();
});


//function to check inputs
function checkInputs () {
    //trim to remove whitespace
    const usernameValue = username.value.trim();
    const emailValue = email.value.trim();
    const passwordValue = password.value.trim();
    const confirmpasswordValue = confirmpassword.value.trim();

    if (usernameValue === '') { //=== means identical to
        //show error
        setErrorFor(username, 'Username cannot be blank');
    } else { setSuccessFor(username); }

    if (emailValue === '') {
        setErrorFor(email, 'Email cannot be blank');
    } else if (!isEmail(emailValue)) {  
        setErrorFor(email, 'Email is not valid');
    } else {setSuccessFor(email); }

    if (passwordValue === '') {
        setErrorFor(password, 'Password cannot be blank');
    } else if (!isPassword(passwordValue)) {
        setErrorFor(password, 'Password must contain at least one number, one uppercase and lowercase letter, and at least 8 or more characters');
    } else { setSuccessFor(password); }

    if (confirmpasswordValue === '') {
        setErrorFor(confirmpassword, 'Confirm Password cannot be blank');
    } else if (confirmpasswordValue !== passwordValue) {
        setErrorFor(confirmpassword, 'Passwords do not match');
    } else { setSuccessFor(confirmpassword); }
}

function setErrorFor(input, message) {
    const formControl = input.parentElement; // .form-control
    const small = formControl.querySelector('small');

    //add error message inside small
    small.innerText = message; //innerText is used to set the text content of the specified node

    //add error class
    formControl.className = 'form-control error';
    //.className is used to get or set the class name of an element
}

function setSuccessFor(input) {
    const formControl = input.parentElement; // .form-control
    formControl.className = 'form-control success';
} //parentElement is used to get the parent of an element in the DOM tree
//formControl is the parent of the input element

//function to check email
function isEmail(email) {
    return /^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$/.test(email);
}

//function to check password
function isPassword(password) { //password must contain at least one number, one uppercase and lowercase letter, and at least 8 or more characters
    return /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,20}$/.test(password);
} 
//10.JAN.2024   Lesson learnt 
//1. when assigning variables, use const instead of var 
//- make sure that parameters document.getElementById('username') is the same as ID in the html file