"use strict";

import { SERVER_URL } from "./config.js";
import * as model from "./model.js";

const form = document.querySelector(".form");
const alert = document.querySelector(".alert");
const togglePasswordBtn = document.getElementById("togglePassword");
const toggleConfirmPasswordBtn = document.getElementById("toggleConfirmPassword");

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

// Toggle confirm password visibility
toggleConfirmPasswordBtn.addEventListener("click", function() {
  const passwordInput = document.getElementById("confirmPassword");
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
    const regData = {};

    [...formData.entries()].forEach((entry) => (regData[entry[0]] = entry[1]));

    // Show loading state
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalBtnText = submitBtn.innerHTML;
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...';

    try {
      if (regData.password !== regData.confirmPassword) {
        showAlert("Passwords do not match", false);
        form.classList.remove("was-validated");
        return;
      }

      if (regData.password.length < 8) {
        showAlert("Password must be at least 8 characters", false);
        form.classList.remove("was-validated");
        return;
      }

      if ((await checkMail(regData.email)) === 400) {
        showAlert("Invalid email address", false);
        form.classList.remove("was-validated");
        return;
      }

      const res = await model.register(regData);

      if (!res) {
        showAlert("Registration failed. Please try again.", false);
        return;
      }

      showAlert("Registration successful! Redirecting to login page...");

      setTimeout(() => {
        window.location = `${SERVER_URL}/logIn`;
      }, 2500);
    } catch (error) {
      showAlert("An error occurred. Please try again.", false);
    } finally {
      // Restore button state
      submitBtn.disabled = false;
      submitBtn.innerHTML = originalBtnText;
    }
  });
})();

const checkMail = async (mail) => {
  try {
    const requestOptions = {
      method: "GET",
      redirect: "follow",
    };

    const res = await fetch(
      `https://api.mailcheck.ai/email/${mail}`,
      requestOptions
    );

    const data = await res.json();
    return data.status;
  } catch (error) {
    console.error("Error checking email:", error);
    return 200; // Return 200 to allow registration to proceed if the email check service fails
  }
};

document.querySelector("footer").innerHTML = `
  Copyrights &copy; ${new Date().getFullYear()} Amr Yasser, Marwan Khaled, and Begad Wael
`;

document
  .querySelector(".btn-outline-primary")
  .addEventListener("click", () => (window.location = `${SERVER_URL}/`));
