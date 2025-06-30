document.getElementById('counselorlogin').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const button = document.getElementById('login-btn');
    button.classList.add('loading');
    button.textContent = '로그인 중...';
    
	counselorlogin.submit();
    // 실제 로그인 로직은 여기에 구현
    /* setTimeout(() => {
        // 로그인 성공 시 관리자 페이지로 이동
        window.location.href = '/counselor/dashboard';
    }, 1500);
	*/
});

// Enter 키 처리
document.addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        document.getElementById('login-btn').dispatchEvent(new Event('submit'));
    }
});
