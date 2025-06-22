// F&Q 페이지 전용 JavaScript

// 탭 전환
function showTab(tabName) {
    // 모든 탭 버튼에서 active 클래스 제거
    document.querySelectorAll('.tab-button').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // 모든 FAQ 콘텐츠에서 active 클래스 제거
    document.querySelectorAll('.faq-content').forEach(content => {
        content.classList.remove('active');
    });
    
    // 클릭된 탭 버튼에 active 클래스 추가
    event.target.classList.add('active');
    
    // 해당 탭 콘텐츠에 active 클래스 추가
    document.getElementById(tabName).classList.add('active');
    
    // 검색어 초기화
    document.getElementById('searchInput').value = '';
}

// FAQ 아코디언 토글
function toggleAnswer(questionElement) {
    const answer = questionElement.nextElementSibling;
    const isActive = questionElement.classList.contains('active');
    const currentTab = questionElement.closest('.faq-content');
    
    // 현재 탭의 모든 질문에서 active 클래스 제거
    currentTab.querySelectorAll('.faq-question').forEach(q => {
        q.classList.remove('active');
        q.nextElementSibling.classList.remove('active');
    });
    
    // 클릭된 질문이 활성화되지 않았다면 활성화
    if (!isActive) {
        questionElement.classList.add('active');
        answer.classList.add('active');
    }
}

// FAQ 검색
function searchFAQ() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const currentTab = document.querySelector('.faq-content.active');
    const faqItems = currentTab.querySelectorAll('.faq-item');
    let hasResults = false;
    
    faqItems.forEach(item => {
        const questionText = item.querySelector('.question-text').textContent.toLowerCase();
        const answerText = item.querySelector('.answer-content').textContent.toLowerCase();
        
        if (questionText.includes(searchTerm) || answerText.includes(searchTerm)) {
            item.style.display = 'block';
            hasResults = true;
        } else {
            item.style.display = 'none';
        }
    });
    
    // 검색 결과가 없을 때 메시지 표시
    let noResultsMsg = currentTab.querySelector('.no-results');
    if (!hasResults && searchTerm) {
        if (!noResultsMsg) {
            noResultsMsg = document.createElement('div');
            noResultsMsg.className = 'no-results';
            noResultsMsg.textContent = '검색 결과가 없습니다.';
            currentTab.appendChild(noResultsMsg);
        }
        noResultsMsg.style.display = 'block';
    } else if (noResultsMsg) {
        noResultsMsg.style.display = 'none';
    }
}

// DOM 로드 완료 후 실행
document.addEventListener('DOMContentLoaded', function() {
    // 검색창에서 엔터키 입력 시 검색 실행
    document.getElementById('searchInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchFAQ();
        }
    });
});