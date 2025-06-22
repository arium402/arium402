// 샘플 F&Q 데이터 (분류별)
const faqDetailData = {
    '비교과-프로그램 안내': [
        {
            id: 1,
            question: "비교과 프로그램은 어떻게 신청하나요?",
            answer: "비교과 프로그램 신청은 학교 홈페이지 로그인 후 '비교과' 메뉴에서 신청하실 수 있습니다. 신청 기간은 각 프로그램별로 상이하므로 공지사항을 확인해주세요."
        },
        {
            id: 2,
            question: "비교과 프로그램 참여 시 마일리지를 받을 수 있나요?",
            answer: "네, 비교과 프로그램 참여 시 마일리지가 지급됩니다. 프로그램 종류에 따라 지급되는 마일리지가 다르며, 수료증 발급 시에도 추가 마일리지가 지급됩니다."
        },
        {
            id: 3,
            question: "비교과 프로그램 수강료는 얼마인가요?",
            answer: "대부분의 비교과 프로그램은 무료로 제공됩니다. 단, 자격증 취득 과정이나 특별 프로그램의 경우 별도의 수강료가 있을 수 있으니 각 프로그램 안내를 확인해주세요."
        },
        {
            id: 4,
            question: "비교과 프로그램 신청 후 취소가 가능한가요?",
            answer: "프로그램 시작 3일 전까지는 취소가 가능합니다. 취소 시에는 '마이페이지 > 신청내역'에서 직접 취소하거나 학생지원센터로 연락주시기 바랍니다."
        },
        {
            id: 5,
            question: "온라인 비교과 프로그램도 있나요?",
            answer: "네, 코로나19 이후 많은 비교과 프로그램이 온라인으로 진행되고 있습니다. 온라인 프로그램은 실시간 화상강의 또는 녹화강의 형태로 제공되며, 참여 방법은 프로그램별 안내사항을 확인해주세요."
        }
    ],
    '상담-심리상담': [
        {
            id: 1,
            question: "심리상담은 어떻게 신청하나요?",
            answer: "심리상담은 학생상담센터 홈페이지 또는 전화(02-0000-0000)로 신청하실 수 있습니다. 온라인 신청 시 상담 희망 날짜와 시간을 선택하여 예약하시면 됩니다."
        },
        {
            id: 2,
            question: "심리상담 비용은 얼마인가요?",
            answer: "재학생 대상 심리상담은 무료로 제공됩니다. 개인상담, 집단상담 모두 무료이며, 심리검사도 기본적인 검사는 무료로 진행됩니다."
        },
        {
            id: 3,
            question: "상담 내용이 외부에 공개되나요?",
            answer: "상담 내용은 철저히 비밀이 보장됩니다. 상담사는 윤리강령에 따라 상담 내용을 외부에 누설하지 않으며, 내담자의 동의 없이는 어떠한 정보도 공개되지 않습니다."
        }
    ],
    '기타-시설이용': [
        {
            id: 1,
            question: "도서관 이용시간은 어떻게 되나요?",
            answer: "중앙도서관은 평일 09:00-22:00, 토요일 09:00-18:00, 일요일 10:00-17:00에 이용 가능합니다. 시험기간에는 연장 운영하며, 공휴일에는 휴관합니다."
        },
        {
            id: 2,
            question: "학생식당 운영시간과 메뉴는 어디서 확인할 수 있나요?",
            answer: "학생식당은 평일 11:30-14:00, 17:00-19:00에 운영됩니다. 주간 메뉴는 학교 홈페이지 공지사항 또는 학생식당 게시판에서 확인하실 수 있습니다."
        }
    ]
};

// URL 파라미터에서 분류 정보 가져오기 (실제로는 URL에서 파싱)
let currentCategory = '비교과';
let currentSubCategory = '프로그램 안내';
let currentKey = `${currentCategory}-${currentSubCategory}`;

// F&Q 리스트 렌더링
function renderFAQList() {
    const faqList = document.getElementById('faqList');
    const faqCategoryInfo = document.getElementById('faqCategoryInfo');
    
    // 제목 업데이트
    faqCategoryInfo.textContent = `${currentCategory} > ${currentSubCategory}`;
    
    // 해당 분류의 F&Q 데이터 가져오기
    const faqItems = faqDetailData[currentKey] || [];
    
    if (faqItems.length === 0) {
        faqList.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-question-circle"></i>
                <h3>등록된 F&Q가 없습니다</h3>
                <p>현재 이 분류에 등록된 자주묻는질문이 없습니다.<br>새로운 F&Q를 등록해보세요.</p>
            </div>
        `;
        return;
    }
    
    let html = '';
    faqItems.forEach((item, index) => {
        html += `
            <div class="preview-qa-item">
                <div class="preview-question" onclick="togglePreviewAnswer(${index})">
                    <div>
                        <i class="fas fa-question-circle"></i>
                        ${item.question}
                    </div>
                    <i class="fas fa-chevron-down preview-toggle-icon"></i>
                </div>
                <div class="preview-answer">
                    <div class="preview-answer-content">
                        ${item.answer.replace(/\n/g, '<br>')}
                    </div>
                </div>
            </div>
        `;
    });
    
    faqList.innerHTML = html;
}

// F&Q 토글 기능 (미리보기와 동일)
function togglePreviewAnswer(index) {
    const allQuestions = document.querySelectorAll('.preview-question');
    const allAnswers = document.querySelectorAll('.preview-answer');
    const currentQuestion = allQuestions[index];
    const currentAnswer = allAnswers[index];
    
    // 모든 다른 항목들을 닫기
    allQuestions.forEach((q, i) => {
        if (i !== index) {
            q.classList.remove('active');
            allAnswers[i].classList.remove('active');
        }
    });
    
    // 현재 항목 토글
    currentQuestion.classList.toggle('active');
    currentAnswer.classList.toggle('active');
}

// F&Q 페이지 삭제
function deleteFAQPage() {
    if (confirm('정말로 이 F&Q 페이지를 삭제하시겠습니까?\n\n삭제된 데이터는 복구할 수 없습니다.')) {
        if (confirm('삭제를 진행하시겠습니까?')) {
            // 실제로는 서버에 삭제 요청을 보냄
            alert('F&Q 페이지가 성공적으로 삭제되었습니다.');
            window.location.href = 'faq-management-updated.html';
        }
    }
}

// F&Q 페이지 수정
function editFAQPage() {
    alert('F&Q 페이지 수정 화면으로 이동합니다.');
    // 실제로는 수정 페이지로 이동
    // window.location.href = `faq-page-edit.html?category=${encodeURIComponent(currentCategory)}&subCategory=${encodeURIComponent(currentSubCategory)}`;
}

// 목록으로 돌아가기
function goToFAQList() {
    if (confirm('목록으로 돌아가시겠습니까?')) {
        window.location.href = 'faq-management-updated.html';
    }
}

// URL 파라미터에서 분류 정보 설정 (실제 구현 시 사용)
function setCurrentCategoryFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    const category = urlParams.get('category');
    const subCategory = urlParams.get('subCategory');
    
    if (category && subCategory) {
        currentCategory = category;
        currentSubCategory = subCategory;
        currentKey = `${currentCategory}-${currentSubCategory}`;
    }
}

// 다른 분류 예시 보기 (개발용)
function showDifferentCategory(category, subCategory) {
    currentCategory = category;
    currentSubCategory = subCategory;
    currentKey = `${currentCategory}-${currentSubCategory}`;
    renderFAQList();
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 게시판 관리 > F&Q 관리 메뉴 활성화
    const boardManagementMenu = document.querySelector('.nav-item:last-child .main-menu');
    const faqSubmenu = document.querySelector('.nav-item:last-child .sub-menu');
    
    boardManagementMenu.classList.add('active');
    faqSubmenu.style.maxHeight = '200px';
    
    // URL에서 분류 정보 설정
    setCurrentCategoryFromURL();
    
    // F&Q 리스트 렌더링
    renderFAQList();
});

// 개발용: 다른 분류 테스트
window.showDifferentCategory = showDifferentCategory;
