// 마이페이지 JavaScript

// 메인으로 돌아가기
function goToMain() {
    window.location.href = '/counselor/dashboard';
}

// 마이페이지 (현재 페이지)
function goToMyPage() {
    alert('현재 마이페이지입니다.');
}

// 프로필 수정 모달 열기
function editProfile() {
    const modal = document.getElementById('profileModal');
    modal.classList.add('show');
    document.body.style.overflow = 'hidden'; // 스크롤 방지
}

// 모달 닫기
function closeModal() {
    const modal = document.getElementById('profileModal');
    modal.classList.remove('show');
    document.body.style.overflow = 'auto'; // 스크롤 복원
}

// 프로필 저장
function saveProfile() {
    // 폼 데이터 가져오기
    const name = document.getElementById('edit-name').value;
    const phone = document.getElementById('edit-phone').value;
    const email = document.getElementById('edit-email').value;

    // 유효성 검사
    if (!name.trim()) {
        alert('이름을 입력해주세요.');
        return;
    }
    
    if (!phone.trim()) {
        alert('전화번호를 입력해주세요.');
        return;
    }
    
    if (!email.trim()) {
        alert('이메일을 입력해주세요.');
        return;
    }

    // 이메일 형식 검사
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        alert('올바른 이메일 형식을 입력해주세요.');
        return;
    }

    // 전화번호 형식 검사 (간단한 검사)
    const phoneRegex = /^01[0-9]-\d{4}-\d{4}$/;
    if (!phoneRegex.test(phone)) {
        alert('올바른 전화번호 형식을 입력해주세요. (예: 010-1234-5678)');
        return;
    }

    // 화면에 표시된 정보 업데이트
    document.getElementById('display-name').textContent = name;
    document.getElementById('display-phone').textContent = phone;
    document.getElementById('display-email').textContent = email;

    // 헤더의 사용자 이름도 업데이트 (헤더가 있는 경우)
    const userNameElement = document.querySelector('.user-name');
    if (userNameElement) {
        userNameElement.textContent = name + ' 상담사';
    }

    // 모달 닫기
    closeModal();
    
    // 성공 메시지
    alert('프로필이 성공적으로 수정되었습니다.');
}

// DOM 로드 완료 후 실행
document.addEventListener('DOMContentLoaded', function() {
    // 모달 외부 클릭 시 닫기
    const profileModal = document.getElementById('profileModal');
    if (profileModal) {
        profileModal.addEventListener('click', function(e) {
            if (e.target === this) {
                closeModal();
            }
        });
    }

    // ESC 키로 모달 닫기
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeModal();
        }
    });

    // 사이드바 메뉴 클릭 핸들링
    const menuLinks = document.querySelectorAll('.sidebar-menu a:not(.main-category)');


    // 전화번호 입력 자동 포맷팅
    const phoneInput = document.getElementById('edit-phone');
    if (phoneInput) {
        phoneInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/[^\d]/g, '');
            if (value.length >= 3 && value.length <= 7) {
                value = value.replace(/(\d{3})(\d{1,4})/, '$1-$2');
            } else if (value.length > 7) {
                value = value.replace(/(\d{3})(\d{4})(\d{1,4})/, '$1-$2-$3');
            }
            e.target.value = value;
        });
    }
});