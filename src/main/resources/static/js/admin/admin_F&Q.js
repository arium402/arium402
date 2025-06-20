// 샘플 페이지 목록 데이터
const pageData = [
    { id: 1, mainCategory: "비교과", subCategory: "프로그램 안내", registrationDate: "2025.06.15" },
    { id: 2, mainCategory: "상담", subCategory: "심리상담", registrationDate: "2025.06.14" },
    { id: 3, mainCategory: "기타", subCategory: "시설이용", registrationDate: "2025.06.13" },
    { id: 4, mainCategory: "비교과", subCategory: "자격증 취득", registrationDate: "2025.06.12" },
    { id: 5, mainCategory: "상담", subCategory: "진로상담", registrationDate: "2025.06.11" }
];

// 샘플 F&Q 데이터
const faqData = [
    { id: 1, title: "비교과 프로그램 신청 방법이 궁금합니다", category: "비교과", subCategory: "프로그램 신청", registrationDate: "2025.06.15", visibility: "public" },
    { id: 2, title: "상담 예약은 어떻게 하나요?", category: "상담", subCategory: "예약 안내", registrationDate: "2025.06.14", visibility: "public" },
    { id: 3, title: "마일리지 적립 기준을 알고 싶습니다", category: "기타", subCategory: "마일리지", registrationDate: "2025.06.13", visibility: "public" },
    { id: 4, title: "토익 특강 일정이 언제인가요?", category: "비교과", subCategory: "어학 프로그램", registrationDate: "2025.06.12", visibility: "private" },
    { id: 5, title: "심리상담 이용 절차가 궁금합니다", category: "상담", subCategory: "심리상담", registrationDate: "2025.06.11", visibility: "public" },
    { id: 6, title: "비교과 수료증은 어디서 발급받나요?", category: "비교과", subCategory: "수료증 발급", registrationDate: "2025.06.10", visibility: "public" },
    { id: 7, title: "익명상담도 가능한가요?", category: "상담", subCategory: "익명상담", registrationDate: "2025.06.09", visibility: "public" },
    { id: 8, title: "도서관 이용시간을 알고 싶습니다", category: "기타", subCategory: "시설이용", registrationDate: "2025.06.08", visibility: "private" }
];

let currentCategory = 'all';
let currentPagePage = 1;
let currentFAQPage = 1;
const itemsPerPage = 5;

// 분류 탭 전환 기능
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        // 탭 버튼 활성화 상태 변경
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        this.classList.add('active');
        
        // 현재 카테고리 설정 및 페이지 리셋
        currentCategory = this.dataset.category;
        currentFAQPage = 1;
        
        // F&Q 테이블 업데이트
        updateFAQTable();
    });
});

// 페이지 목록 테이블 업데이트 함수
function updatePageTable() {
    const tbody = document.getElementById('pageTableBody');
    const startIndex = (currentPagePage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedData = pageData.slice(startIndex, endIndex);

    tbody.innerHTML = '';
    paginatedData.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.id}</td>
            <td class="category">${item.mainCategory}</td>
            <td class="category">${item.subCategory}</td>
            <td class="date">${item.registrationDate}</td>
        `;
        
        // 행 클릭 이벤트 추가 - 상세 페이지로 이동
        row.addEventListener('click', () => {
            // URL 파라미터로 분류 정보 전달
            const detailURL = `faq-detail.html?category=${encodeURIComponent(item.mainCategory)}&subCategory=${encodeURIComponent(item.subCategory)}`;
            window.location.href = detailURL;
        });
        
        tbody.appendChild(row);
    });

    // 페이지네이션 업데이트
    updatePagePagination(pageData.length);
}

// F&Q 목록 테이블 업데이트 함수
function updateFAQTable() {
    const tbody = document.getElementById('faqTableBody');
    const visibilityFilter = document.getElementById('visibilityFilter').value;
    let filteredData = faqData;

    // 카테고리 필터링
    if (currentCategory !== 'all') {
        const categoryMap = {
            'extracurricular': '비교과',
            'counseling': '상담',
            'etc': '기타'
        };
        filteredData = faqData.filter(item => item.category === categoryMap[currentCategory]);
    }

    // 공개/비공개 필터링
    if (visibilityFilter !== 'all') {
        filteredData = filteredData.filter(item => item.visibility === visibilityFilter);
    }

    // 페이지네이션 적용
    const startIndex = (currentFAQPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedData = filteredData.slice(startIndex, endIndex);

    tbody.innerHTML = '';
    paginatedData.forEach(item => {
        const row = document.createElement('tr');
        const visibilityText = item.visibility === 'public' ? '공개' : '비공개';
        const visibilityClass = item.visibility === 'public' ? 'public' : 'private';
        
        row.innerHTML = `
            <td>${item.id}</td>
            <td class="title">${item.title}</td>
            <td class="category">${item.category}</td>
            <td class="category">${item.subCategory}</td>
            <td class="visibility ${visibilityClass}">${visibilityText}</td>
            <td class="date">${item.registrationDate}</td>
        `;
        
        // 행 클릭 이벤트 추가
        row.addEventListener('click', () => {
            alert(`F&Q 상세: ${item.title}`);
        });
        
        tbody.appendChild(row);
    });

    // 페이지네이션 업데이트
    updateFAQPagination(filteredData.length);
}

// 페이지 목록 페이지네이션 업데이트
function updatePagePagination(totalItems) {
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const pagination = document.getElementById('pagePagination');
    const startItem = (currentPagePage - 1) * itemsPerPage + 1;
    const endItem = Math.min(currentPagePage * itemsPerPage, totalItems);
    
    pagination.innerHTML = `
        <button class="pagination-btn" onclick="changePagePage(-1)" ${currentPagePage === 1 ? 'disabled' : ''}>‹</button>
    `;

    const startPage = Math.max(1, currentPagePage - 2);
    const endPage = Math.min(totalPages, currentPagePage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        pagination.innerHTML += `
            <button class="pagination-btn ${i === currentPagePage ? 'active' : ''}" onclick="goToPagePage(${i})">${i}</button>
        `;
    }

    pagination.innerHTML += `
        <button class="pagination-btn" onclick="changePagePage(1)" ${currentPagePage === totalPages ? 'disabled' : ''}>›</button>
    `;

    document.getElementById('pagePaginationInfo').textContent = 
        `총 ${totalItems}건 중 ${startItem}-${endItem}건 표시`;
}

// F&Q 목록 페이지네이션 업데이트
function updateFAQPagination(totalItems) {
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const pagination = document.getElementById('faqPagination');
    const startItem = (currentFAQPage - 1) * itemsPerPage + 1;
    const endItem = Math.min(currentFAQPage * itemsPerPage, totalItems);
    
    pagination.innerHTML = `
        <button class="pagination-btn" onclick="changeFAQPage(-1)" ${currentFAQPage === 1 ? 'disabled' : ''}>‹</button>
    `;

    const startPage = Math.max(1, currentFAQPage - 2);
    const endPage = Math.min(totalPages, currentFAQPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        pagination.innerHTML += `
            <button class="pagination-btn ${i === currentFAQPage ? 'active' : ''}" onclick="goToFAQPage(${i})">${i}</button>
        `;
    }

    pagination.innerHTML += `
        <button class="pagination-btn" onclick="changeFAQPage(1)" ${currentFAQPage === totalPages ? 'disabled' : ''}>›</button>
    `;

    document.getElementById('faqPaginationInfo').textContent = 
        `총 ${totalItems}건 중 ${startItem}-${endItem}건 표시`;
}

// 페이지 목록 페이지 변경
function changePagePage(direction) {
    const totalPages = Math.ceil(pageData.length / itemsPerPage);
    const newPage = currentPagePage + direction;

    if (newPage >= 1 && newPage <= totalPages) {
        currentPagePage = newPage;
        updatePageTable();
    }
}

function goToPagePage(page) {
    currentPagePage = page;
    updatePageTable();
}

// F&Q 목록 페이지 변경
function changeFAQPage(direction) {
    let filteredData = faqData;
    const visibilityFilter = document.getElementById('visibilityFilter').value;

    if (currentCategory !== 'all') {
        const categoryMap = {
            'extracurricular': '비교과',
            'counseling': '상담',
            'etc': '기타'
        };
        filteredData = faqData.filter(item => item.category === categoryMap[currentCategory]);
    }

    if (visibilityFilter !== 'all') {
        filteredData = filteredData.filter(item => item.visibility === visibilityFilter);
    }

    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    const newPage = currentFAQPage + direction;

    if (newPage >= 1 && newPage <= totalPages) {
        currentFAQPage = newPage;
        updateFAQTable();
    }
}

function goToFAQPage(page) {
    currentFAQPage = page;
    updateFAQTable();
}

// 검색 기능
function searchFAQ() {
    const searchType = document.getElementById('searchType').value;
    const searchInput = document.getElementById('searchInput').value;
    
    if (searchInput.trim()) {
        alert(`${searchType === 'title' ? '제목' : '내용'}에서 "${searchInput}" 검색 실행`);
    } else {
        alert('검색어를 입력해주세요.');
    }
}

// 페이지 등록 기능 - 페이지 등록 화면으로 이동
function registerPage() {
    window.location.href = 'faq-page-register.html';
}

// F&Q 등록 기능
function registerFAQ() {
    alert('개별 F&Q 등록 화면으로 이동합니다.');
    // 실제로는 F&Q 개별 등록 페이지로 이동
    // window.location.href = 'faq-item-register.html';
}

// 공개/비공개 필터 이벤트
document.getElementById('visibilityFilter').addEventListener('change', function() {
    currentFAQPage = 1;
    updateFAQTable();
});

// 검색 입력 시 엔터키 처리
document.getElementById('searchInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        searchFAQ();
    }
});

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 게시판 관리 메뉴가 활성화된 상태로 시작
    const boardManagementMenu = document.querySelector('.nav-item:last-child .main-menu');
    const faqSubmenu = document.querySelector('.nav-item:last-child .sub-menu');
    
    boardManagementMenu.classList.add('active');
    faqSubmenu.style.maxHeight = '200px';
    
    // 테이블 초기화
    updatePageTable();
    updateFAQTable();
});
