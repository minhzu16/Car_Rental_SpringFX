"use strict";

import * as model from "./model.js";
import { SERVER_URL } from "./config.js";

const form = document.querySelector(".form");
const alert = document.querySelector(".alert");
const togglePasswordBtn = document.getElementById("togglePassword");

const showAlert = async function (message, flag = true) {
  alert.textContent = message;
  alert.classList.toggle("alert-hide");
  alert.classList.toggle(`${flag ? "alert-success" : "alert-danger"}`);

  setTimeout(() => {
    alert.classList.toggle("alert-hide");
    alert.classList.toggle(`${flag ? "alert-success" : "alert-danger"}`);
    alert.textContent = "";
  }, 3000);
};

// Toggle password visibility
togglePasswordBtn.addEventListener("click", function() {
  const passwordInput = document.getElementById("password");
  const icon = this.querySelector("i");
  
  if (passwordInput.type === "password") {
    passwordInput.type = "text";
    icon.classList.remove("fa-eye");
    icon.classList.add("fa-eye-slash");
  } else {
    passwordInput.type = "password";
    icon.classList.remove("fa-eye-slash");
    icon.classList.add("fa-eye");
  }
});

// Form submission
(() => {
  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const flag = form.checkValidity();

    form.classList.add("was-validated");

    if (!flag) {
      e.stopPropagation();
      return;
    }

    const formData = new FormData(form);
    const loginData = {};

    [...formData.entries()].forEach(
      (entry) => (loginData[entry[0]] = entry[1])
    );
    
    // Show loading state
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalBtnText = submitBtn.innerHTML;
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Signing in...';
    
    try {
      const res = await model.signIn(loginData);

      if (!res) {
        showAlert("Wrong email or password", false);
        form.classList.remove("was-validated");
        return;
      }

      showAlert("Login successful! Redirecting...", true);
      setTimeout(() => {
        window.location = `${SERVER_URL}/`;
      }, 1000);
    } catch (error) {
      showAlert("An error occurred. Please try again.", false);
    } finally {
      // Restore button state
      submitBtn.disabled = false;
      submitBtn.innerHTML = originalBtnText;
    }
  });
})();

document.querySelector(".sign-up").addEventListener("click", () => {
  window.location = `${SERVER_URL}/createAccount`;
});

// Set footer content
document.querySelector("footer").innerHTML = `
  Copyrights &copy; ${new Date().getFullYear()} Amr Yasser, Marwan Khaled, and Begad Wael
`;
