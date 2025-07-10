// Force browser to reload script - prevent caching
const scriptVersion = new Date().getTime();

document.addEventListener('DOMContentLoaded', function() {
    // Helper function to ensure proper date parsing
    function parseDate(dateString) {
        // Handle different date formats
        if (!dateString) return null;
        
        // Try to parse the date
        const parts = dateString.split(/[-\/]/);
        if (parts.length === 3) {
            // Check if format is DD/MM/YYYY
            if (parts[0].length === 2 && parts[1].length === 2 && parts[2].length === 4) {
                return new Date(parts[2], parts[1] - 1, parts[0]);
            }
            // Otherwise assume YYYY-MM-DD
            return new Date(parts[0], parts[1] - 1, parts[2]);
        }
        // Default parsing
        return new Date(dateString);
    }
    
    // Form validation
    const form = document.querySelector('.needs-validation');
    
    if (form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            
            // Additional custom validation
            const email = document.getElementById('email');
            if (email) {
                const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
                if (email.value && !emailPattern.test(email.value)) {
                    email.setCustomValidity('Please enter a valid email address');
                    event.preventDefault();
                } else {
                    email.setCustomValidity('');
                }
            }
            
            const mobile = document.getElementById('mobile');
            if (mobile) {
                const phonePattern = /^[0-9]{10,15}$/;
                if (mobile.value && !phonePattern.test(mobile.value)) {
                    mobile.setCustomValidity('Please enter a valid phone number (10-15 digits)');
                    event.preventDefault();
                } else {
                    mobile.setCustomValidity('');
                }
            }
            
            const birthday = document.getElementById('birthday');
            if (birthday && birthday.value) {
                const birthDate = parseDate(birthday.value);
                const today = new Date();
                
                // Calculate age more precisely
                let age = today.getFullYear() - birthDate.getFullYear();
                const monthDiff = today.getMonth() - birthDate.getMonth();
                
                // Adjust age if birthday hasn't occurred yet this year
                if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                    age--;
                }
                
                if (birthDate > today) {
                    birthday.setCustomValidity('Birthday cannot be in the future');
                    event.preventDefault();
                } else if (age < 16) {
                    birthday.setCustomValidity('You must be at least 16 years old');
                    event.preventDefault();
                } else {
                    birthday.setCustomValidity('');
                }
            }
            
            const licenceDate = document.getElementById('licenceDate');
            if (licenceDate && licenceDate.value) {
                const licDate = parseDate(licenceDate.value);
                const today = new Date();
                
                // Debug information
                console.log('License Date:', licDate);
                console.log('Today:', today);
                console.log('Max Future Date:', new Date(today.getFullYear() + 5, today.getMonth(), today.getDate()));
                
                // Allow future dates but limit to 5 years
                const maxFutureDate = new Date(today);
                maxFutureDate.setFullYear(today.getFullYear() + 5);
                
                if (licDate > maxFutureDate) {
                    console.log('Error: License date too far in future');
                    licenceDate.setCustomValidity('License date cannot be more than 5 years in the future');
                    event.preventDefault();
                } else if (birthday && birthday.value) {
                    // Check if license date is after person turns 16
                    const birthDate = parseDate(birthday.value);
                    const minLicenseDate = new Date(birthDate);
                    minLicenseDate.setFullYear(birthDate.getFullYear() + 16);
                    
                    console.log('Birth Date:', birthDate);
                    console.log('Min License Date (16 years after birth):', minLicenseDate);
                    
                    if (licDate < minLicenseDate) {
                        console.log('Error: License date before turning 16');
                        licenceDate.setCustomValidity('License date cannot be before you turn 16');
                        event.preventDefault();
                    } else {
                        console.log('License date validation passed');
                        licenceDate.setCustomValidity('');
                    }
                } else {
                    console.log('No birthday provided, skipping age check');
                    licenceDate.setCustomValidity('');
                }
            }
            
            form.classList.add('was-validated');
        });
        
        // Clear custom validity when input changes
        const inputs = form.querySelectorAll('input');
        inputs.forEach(input => {
            input.addEventListener('input', function() {
                this.setCustomValidity('');
            });
        });
    }
}); 