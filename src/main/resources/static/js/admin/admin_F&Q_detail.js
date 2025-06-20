
// 소분류 옵션 업데이트 함수
function updateSubCategories() {
    const mainCategory = document.getElementById('mainCategory').value;
    const subCategory = document.getElementById('subCategory');
    
    // 기존 옵션 제거
    subCategory.innerHTML = '';
    
    let options = [];
    
    switch(mainCategory) {
        case 'extracurricular':
            options = [
                { value: 'program', text: '프로그램 안내' },
                { value: 'application', text: '신청 방법' },
                { value: 'certificate', text: '수료증 발급' },
                { value: 'language', text: '어학 프로그램' },
                { value: 'qualification', text: '자격증 취득' }
            ];
            break;
        case 'counseling':
            options = [
                { value: 'psychological', text: '심리상담' },
                { value: 'career', text: '진로상담' },
                { value: 'academic', text: '학업상담' },
                { value: 'reservation', text: '예약 안내' },
                { value: 'anonymous', text: '익명상담' }
            ];
            break;
        case 'etc':
            options = [
                { value: 'facility', text: '시설이용' },
                { value: 'mileage', text: '마일리지' },
                { value: 'scholarship', text: '장학금' },
                { value: 'dormitory', text: '기숙사' },
                { value: 'general', text: '일반문의' }
            ];
            break;
    }
    
    options.forEach(option => {
        const optionElement = document.createElement('option');
        optionElement.value = option.value;
        optionElement.textContent = option.text;
        subCategory.appendChild(optionElement);
    });
}

// 대분류 변경 이벤트 (현재는 disabled 상태이므로 실제로는 동작하지 않음)
document.getElementById('mainCategory').addEventListener('change', updateSubCategories);

// F&Q 수정 함수
function editFAQ() {
    if (confirm('F&Q를 수정하시겠습니까?')) {
        alert('F&Q 수정 페이지로 이동합니다.');
        // 실제로는 수정 페이지로 이동
        // window.location.href = 'faq-edit.html?id=1';
    }
}

// 목록으로 돌아가기 함수
function goToList() {
    if (confirm('목록으로 돌아가시겠습니까?')) {
        window.location.href = 'faq-management.html';
    }
}

// URL 파라미터에서 F&Q ID 가져와서 데이터 로드 (실제 구현 시 사용)
function loadFAQData() {
    const urlParams = new URLSearchParams(window.location.search);
    const faqId = urlParams.get('id');
    
    if (faqId) {
        // 실제로는 서버에서 해당 ID의 F&Q 데이터를 가져옴
        // 여기서는 샘플 데이터로 대체
        console.log('Loading FAQ data for ID:', faqId);
    }
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 게시판 관리 > F&Q 관리 메뉴 활성화
    const boardManagementMenu = document.querySelector('.nav-item:last-child .main-menu');
    const faqSubmenu = document.querySelector('.nav-item:last-child .sub-menu');
    
    boardManagementMenu.classList.add('active');
    faqSubmenu.style.maxHeight = '200px';
    
    // 소분류 옵션 초기화
    updateSubCategories();
    
    // F&Q 데이터 로드
    loadFAQData();
});
