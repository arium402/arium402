// 헤더 관련 JavaScript

// 마이페이지로 이동
function goToMyPage() {
    window.open('/counselor/mypage', '_blank');
}

// 로그아웃
function logout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        location.href = './logout';
    }
}