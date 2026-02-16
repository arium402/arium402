// DOM이 준비되면 로그인 실패 여부 확인
window.addEventListener('DOMContentLoaded', function () {
    const params = new URLSearchParams(window.location.search);
    
    if (params.get('error') === 'bad') {
        alert('아이디 또는 비밀번호가 틀렸습니다.');
    } else if (params.get('error') === 'forbidden') {
        alert('접근 권한이 없습니다.');
	}else if (params.get('error') !== null) {
		alert('로그인에 실패했습니다. 다시 시도해주세요.');
	}
	
    // URL 파라미터 제거
    const url = new URL(window.location);
    url.search = '';
    window.history.replaceState({}, document.title, url.toString());
});


document.getElementById('adminLoginForm').addEventListener('submit', function(e) {
   // e.preventDefault();
    
    const button = document.getElementById('loginButton');
    button.classList.add('loading');
    button.textContent = '로그인 중...';
    
    // 실제 로그인 로직은 여기에 구현
    setTimeout(() => {
        // 로그인 성공 시 관리자 페이지로 이동
        window.location.href = '/admin/dashboard';
    }, 1500);
});

// Enter 키 처리: 버튼 대신 form 제출로
document.addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        document.getElementById('adminLoginForm').submit(); // form 직접 submit
    }
});