// 헤더 관련 JavaScript

// 마이페이지로 이동
function goToMyPage() {
    window.open('/counselor/mypage', '_blank');
}

//상담사 로그아웃 확인 후 진행
function confirmLogout() {
    const confirmed = confirm("로그아웃 하시겠습니까?");
    if (confirmed) {
        const form = document.getElementById("logoutForm");
        if (form) form.submit();
    }
}