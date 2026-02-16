// 문의게시판 페이지 전용 JavaScript

let currentPage = 1;
const totalPages = 2;

// 문의 상세보기 (답변 달기)
function openQnA(id) {
    alert(`문의 ${id}번 상세보기 페이지로 이동합니다. 여기서 답변을 작성할 수 있습니다.`);
    // window.location.href = `/inquiry/detail/${id}`;
}

// 검색
function searchQnA() {
    const searchTerm = document.getElementById('searchInput').value;
    if (searchTerm.trim()) {
        alert(`'${searchTerm}' 검색 결과를 불러옵니다.`);
        // 실제 검색 로직 구현
    }
}

// 정렬
function sortQnA() {
    const sortType = document.getElementById('sortSelect').value;
    alert(`${sortType}으로 정렬합니다.`);
    // 실제 정렬 로직 구현
}

// 페이지 이동
function goToPage(page) {
    if (page < 1 || page > totalPages) return;
    
    currentPage = page;
    updatePagination();
    
    alert(`${page}페이지로 이동합니다.`);
    // 실제 페이지 로드 로직 구현
}

function changePage(direction) {
    const newPage = currentPage + direction;
    goToPage(newPage);
}

function updatePagination() {
    // 모든 페이지 버튼에서 active 클래스 제거
    document.querySelectorAll('.pagination-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    // 현재 페이지 버튼에 active 클래스 추가
    const pageButtons = document.querySelectorAll('.pagination-btn[onclick*="goToPage"]');
    if (pageButtons[currentPage - 1]) {
        pageButtons[currentPage - 1].classList.add('active');
    }

    // 이전/다음 버튼 활성화/비활성화
    document.getElementById('prevBtn').disabled = currentPage === 1;
    document.getElementById('nextBtn').disabled = currentPage === totalPages;
}

// DOM 로드 완료 후 실행
document.addEventListener('DOMContentLoaded', function() {
    // 검색 input에서 엔터키 처리
    document.getElementById('searchInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchQnA();
        }
    });

    // 초기 페이지네이션 설정
    updatePagination();
});