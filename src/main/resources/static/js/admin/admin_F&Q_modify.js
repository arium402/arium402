// 소분류 데이터
const subCategoryData = {
    'extracurricular': [
        { value: 'program', text: '프로그램 안내' },
        { value: 'certificate', text: '자격증 취득' },
        { value: 'language', text: '어학 프로그램' },
        { value: 'career', text: '취업 프로그램' },
        { value: 'volunteer', text: '봉사활동' }
    ],
    'counseling': [
        { value: 'psychology', text: '심리' },
        { value: 'anonymous', text: '익명' },
        { value: 'crisis', text: '위기' },
        { value: 'career', text: '진로/취업' },
        { value: 'learning', text: '학습컨설팅' }
    ],
    'etc': [
        { value: 'facility', text: '시설이용' },
        { value: 'mileage', text: '마일리지' },
        { value: 'dormitory', text: '기숙사' },
        { value: 'scholarship', text: '장학금' },
        { value: 'system', text: '시스템 이용' }
    ]
};

// 대분류 선택 시 소분류 업데이트
function updateSubCategory() {
    const mainCategory = document.getElementById('mainCategory').value;
    const subCategorySelect = document.getElementById('subCategory');
    
    // 소분류 초기화
    subCategorySelect.innerHTML = '<option value="">소분류를 선택하세요</option>';
    
    if (mainCategory && subCategoryData[mainCategory]) {
        subCategoryData[mainCategory].forEach(item => {
            const option = document.createElement('option');
            option.value = item.value;
            option.textContent = item.text;
            subCategorySelect.appendChild(option);
        });
        subCategorySelect.disabled = false;
    } else {
        subCategorySelect.innerHTML = '<option value="">대분류를 먼저 선택하세요</option>';
        subCategorySelect.disabled = true;
    }
}

// F&Q 수정
function updateFAQ() {
    const mainCategory = document.getElementById('mainCategory').value;
    const subCategory = document.getElementById('subCategory').value;
    const visibility = document.querySelector('input[name="visibility"]:checked').value;
    const question = document.getElementById('questionInput').value.trim();
    const answer = document.getElementById('answerInput').value.trim();

    // 유효성 검사
    if (!mainCategory) {
        alert('대분류를 선택해주세요.');
        return;
    }

    if (!subCategory) {
        alert('소분류를 선택해주세요.');
        return;
    }

    if (!question) {
        alert('질문을 입력해주세요.');
        document.getElementById('questionInput').focus();
        return;
    }

    if (!answer) {
        alert('답변을 입력해주세요.');
        document.getElementById('answerInput').focus();
        return;
    }

    // 수정 확인
    if (confirm('수정하시겠습니까?')) {
        // 수정 처리
        alert('F&Q가 성공적으로 수정되었습니다!\n\n' +
              `대분류: ${document.getElementById('mainCategory').options[document.getElementById('mainCategory').selectedIndex].text}\n` +
              `소분류: ${document.getElementById('subCategory').options[document.getElementById('subCategory').selectedIndex].text}\n` +
              `공개여부: ${visibility === 'public' ? '공개' : '비공개'}\n` +
              `질문: ${question.substring(0, 50)}${question.length > 50 ? '...' : ''}`);
        
        // 실제로는 서버로 데이터 전송 후 목록 페이지로 이동
        // window.location.href = 'faq-management.html';
    }
}

// 수정 취소
function cancelModification() {
    if (confirm('수정을 취소하시겠습니까?\n\n변경사항이 모두 삭제됩니다.')) {
        alert('수정이 취소되었습니다.');
        // 실제로는 이전 페이지로 이동
        // window.location.href = 'faq-management.html';
    }
}

// 현재 날짜 설정
function setCurrentDate() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    const currentDate = `${year}.${month}.${day}`;
    
    // 등록일은 과거 날짜로 설정 (예시)
    document.getElementById('registrationDate').textContent = '2024.12.15';
    // 수정일은 오늘 날짜로 설정
    document.getElementById('modificationDate').textContent = currentDate;
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 게시판 관리 > F&Q 관리 메뉴 활성화
    const boardManagementMenu = document.querySelector('.nav-item:last-child .main-menu');
    const faqSubmenu = document.querySelector('.nav-item:last-child .sub-menu');
    
    boardManagementMenu.classList.add('active');
    faqSubmenu.style.maxHeight = '200px';
    
    // 현재 날짜 설정
    setCurrentDate();
    
    // 기존 데이터가 있는 상태이므로 소분류를 활성화하고 옵션들 추가
    const mainCategory = document.getElementById('mainCategory').value;
    const subCategorySelect = document.getElementById('subCategory');
    
    if (mainCategory && subCategoryData[mainCategory]) {
        // 기존 선택된 옵션 제외하고 나머지 옵션들 추가
        subCategoryData[mainCategory].forEach(item => {
            if (item.value !== 'program') { // 이미 선택된 것 제외
                const option = document.createElement('option');
                option.value = item.value;
                option.textContent = item.text;
                subCategorySelect.appendChild(option);
            }
        });
        subCategorySelect.disabled = false;
    }
});
