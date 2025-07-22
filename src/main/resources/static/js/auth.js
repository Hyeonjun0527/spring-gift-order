function switchTab(tabName) {
    document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.form-section').forEach(section => section.classList.remove('active'));
    
    event.target.classList.add('active');
    document.getElementById(tabName + '-section').classList.add('active');
    
    hideMessages();
}

function showError(message) {
    const errorDiv = document.getElementById('error-message');
    const successDiv = document.getElementById('success-message');
    
    successDiv.style.display = 'none';
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
}

function showSuccess(message) {
    const errorDiv = document.getElementById('error-message');
    const successDiv = document.getElementById('success-message');
    
    errorDiv.style.display = 'none';
    successDiv.textContent = message;
    successDiv.style.display = 'block';
}

function hideMessages() {
    document.getElementById('error-message').style.display = 'none';
    document.getElementById('success-message').style.display = 'none';
}

function setLoading(formId, isLoading) {
    const form = document.getElementById(formId);
    const button = form.querySelector('.submit-button');
    
    if (isLoading) {
        form.classList.add('loading');
        button.disabled = true;
        button.textContent = '처리 중...';
    } else {
        form.classList.remove('loading');
        button.disabled = false;
        button.textContent = formId === 'login-form' ? '로그인' : '회원가입';
    }
}

function validatePasswordMatch() {
    const password = document.getElementById('register-password').value;
    const confirmPassword = document.getElementById('register-password-confirm').value;
    
    if (password !== confirmPassword) {
        showError('비밀번호가 일치하지 않습니다.');
        return false;
    }
    return true;
}

document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('register-form');
    const loginForm = document.getElementById('login-form');

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        if (!validatePasswordMatch()) {
            return;
        }
        
        const email = document.getElementById('register-email').value;
        const password = document.getElementById('register-password').value;

        setLoading('register-form', true);
        hideMessages();

        try {
            const response = await fetch('/api/members/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });
            
            if (response.ok) {
                showSuccess('회원가입이 완료되었습니다! 로그인해주세요.');
                registerForm.reset();
                
                setTimeout(() => {
                    switchTab('login');
                    document.getElementById('login-email').value = email;
                }, 2000);
            } else {
                const errorData = await response.json();
                showError(errorData.detail || '회원가입에 실패했습니다. 다시 시도해주세요.');
            }
        } catch (error) {
            console.error('Register error:', error);
            showError('네트워크 오류가 발생했습니다. 다시 시도해주세요.');
        } finally {
            setLoading('register-form', false);
        }
    });

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;

        setLoading('login-form', true);
        hideMessages();

        try {
            const response = await fetch('/api/members/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('accessToken', data.token);
                showSuccess('로그인 성공! 메인 페이지로 이동합니다.');
                
                setTimeout(() => {
                    window.location.href = '/';
                }, 1000);
            } else {
                const errorData = await response.json();
                showError(errorData.detail || '이메일 또는 비밀번호를 확인해주세요.');
            }
        } catch (error) {
            console.error('Login error:', error);
            showError('네트워크 오류가 발생했습니다. 다시 시도해주세요.');
        } finally {
            setLoading('login-form', false);
        }
    });

    document.getElementById('register-password-confirm').addEventListener('input', () => {
        const password = document.getElementById('register-password').value;
        const confirmPassword = document.getElementById('register-password-confirm').value;
        const confirmInput = document.getElementById('register-password-confirm');
        
        if (confirmPassword && password !== confirmPassword) {
            confirmInput.style.borderColor = '#dc3545';
        } else {
            confirmInput.style.borderColor = '#e9ecef';
        }
    });
}); 