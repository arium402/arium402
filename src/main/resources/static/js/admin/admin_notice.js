// 샘플 공지사항 데이터
const noticeData = [
    { 
        id: 1, 
        title: "2025년 상담 프로그램 운영 안내", 
        category: "counseling", 
        isPublic: "Y", 
        isFixed: "Y", 
        registerDate: "2025.06.15",
        content: "2025년 상담 프로그램 운영에 대해 안내드립니다..."
    },
    { 
        id: 2, 
        title: "토익 특강 신청 안내", 
        category: "extracurricular", 
        isPublic: "Y", 
        isFixed: "N", 
        registerDate: "2025.06.14",
        content: "토익 특강 신청에 대해 안내드립니다..."
    },
    { 
        id: 3, 
        title: "시스템 점검 안내", 
        category: "other", 
        isPublic: "Y", 
        isFixed: "N", 
        registerDate: "2025.06.13",
        content: "시스템 점검에 대해 안내드립니다..."
    },
    { 
        id: 4, 
        title: "심리상담 운영시간 변경 안내", 
        category: "counseling", 
        isPublic: "N", 
        isFixed: "N", 
        registerDate: "2025.06.12",
        content: "심리상담 운영시간 변경에 대해 안내드립니다..."
    },
    { 
        id: 5, 
        title: "창업 특강 개최 안내", 
        category: "extracurricular", 
        isPublic: "Y", 
        isFixed: "Y", 
        registerDate: "2025.06.11",
        content: "창업 특강 개최에 대해 안내드립니다..."
    },
    { 
        id: 6, 
        title: "학사 일정 안내", 
        category: "other", 
        isPublic: "Y", 
        isFixed: "N", 
        registerDate: "2025.06.10",
        content: "학사 일정에 대해 안내드립니다..."
    },
    { 
        id: 7, 
        title: "온라인 상담 시스템 도입 안내", 
        category: "counseling", 
        isPublic: "Y", 
        isFixed: "N", 
        registerDate: "2025.06.09",
        content: "온라인 상담 시스템 도입에 대해 안내드립니다..."
    },
    { 
        id: 8, 
        title: "취업 특강 프로그램 안내", 
        category: "extracurricular", 
        isPublic: "N", 
        isFixed: "N", 
        registerDate: "2025.06.08",
        content: "취업 특강 프로그램에 대해 안내드립니다..."
    }
];

let currentCategory = 'all';
let currentVisibility = 'all';
let currentPage = 1;
const itemsPerPage = 5;


// 카테고리 탭 전환 기능
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        // 탭 버튼 활성화 상태 변경
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        this.classList.add('active');
        
        // 현재 카테고리 설정 및 페이지 리셋
        currentCategory = this.dataset.category;
        currentPage = 1;
        
        // 테이블 업데이트
        updateTable();
    });
});

// 공개상태 필터 변경
document.getElementById('visibilityFilter').addEventListener('change', function() {
    currentVisibility = this.value;
    currentPage = 1;
    updateTable();
});

// 테이블 업데이트 함수
function updateTable() {
    const tbody = document.getElementById('noticeTableBody');
    let filteredData = noticeData;

    // 카테고리 필터링
    if (currentCategory !== 'all') {
        filteredData = filteredData.filter(notice => notice.category === currentCategory);
    }

    // 공개상태 필터링
    if (currentVisibility === 'public') {
        filteredData = filteredData.filter(notice => notice.isPublic === 'Y');
    } else if (currentVisibility === 'private') {
        filteredData = filteredData.filter(notice => notice.isPublic === 'N');
    }

    // 페이지네이션 적용
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedData = filteredData.slice(startIndex, endIndex);

    // 테이블 생성
    tbody.innerHTML = '';
    paginatedData.forEach(notice => {
        const row = document.createElement('tr');
        
        const categoryClass = `category-${notice.category}`;
        const categoryText = getCategoryText(notice.category);
        const publicClass = notice.isPublic === 'Y' ? 'status-y' : 'status-n';
        const fixedClass = notice.isFixed === 'Y' ? 'status-y' : 'status-n';
        
        row.innerHTML = `
            <td>${notice.id}</td>
            <td class="notice-title">${notice.title}</td>
            <td><span class="category-badge ${categoryClass}">${categoryText}</span></td>
            <td><span class="status-badge ${publicClass}">${notice.isPublic}</span></td>
            <td><span class="status-badge ${fixedClass}">${notice.isFixed}</span></td>
            <td>${notice.registerDate}</td>
        `;
        
        // 행 클릭 이벤트 추가 (상세 페이지로 이동)
        row.addEventListener('click', () => openDetailPage(notice));
        
        tbody.appendChild(row);
    });

    // 페이지네이션 업데이트
    updatePagination(filteredData.length);
}

// 카테고리 텍스트 반환
function getCategoryText(category) {
    switch(category) {
        case 'extracurricular': return '비교과';
        case 'counseling': return '상담';
        case 'other': return '기타';
        default: return '기타';
    }
}

// 페이지네이션 업데이트 함수
function updatePagination(totalItems) {
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const pagination = document.querySelector('.pagination');
    const startItem = (currentPage - 1) * itemsPerPage + 1;
    const endItem = Math.min(currentPage * itemsPerPage, totalItems);
    
    // 페이지네이션 버튼 재생성
    pagination.innerHTML = `
        <button class="pagination-btn" id="prevBtn" onclick="changePage(-1)" ${currentPage === 1 ? 'disabled' : ''}>‹</button>
    `;

    // 페이지 번호 버튼들
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        pagination.innerHTML += `
            <button class="pagination-btn ${i === currentPage ? 'active' : ''}" onclick="goToPage(${i})">${i}</button>
        `;
    }

    // 다음 버튼
    pagination.innerHTML += `
        <button class="pagination-btn" id="nextBtn" onclick="changePage(1)" ${currentPage === totalPages ? 'disabled' : ''}>›</button>
    `;

    // 페이지 정보 업데이트
    document.getElementById('paginationInfo').textContent = 
        `총 ${totalItems}건 중 ${startItem}-${endItem}건 표시`;
}

// 페이지 변경 함수
function changePage(direction) {
    let filteredData = noticeData;
    
    if (currentCategory !== 'all') {
        filteredData = filteredData.filter(notice => notice.category === currentCategory);
    }
    
    if (currentVisibility === 'public') {
        filteredData = filteredData.filter(notice => notice.isPublic === 'Y');
    } else if (currentVisibility === 'private') {
        filteredData = filteredData.filter(notice => notice.isPublic === 'N');
    }

    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    const newPage = currentPage + direction;

    if (newPage >= 1 && newPage <= totalPages) {
        currentPage = newPage;
        updateTable();
    }
}

// 특정 페이지로 이동
function goToPage(page) {
    currentPage = page;
    updateTable();
}

// 검색 수행
function performSearch() {
    const searchType = document.getElementById('searchType').value;
    const searchValue = document.getElementById('searchInput').value.trim();
    
    if (searchValue) {
        alert(`${searchType === 'title' ? '제목' : '내용'}에서 "${searchValue}" 검색`);
        // 실제 검색 로직 구현
    }
}

// 등록 페이지 열기
function openRegisterPage() {
    alert('공지사항 등록 페이지로 이동합니다.');
    // 실제로는 등록 페이지로 이동
}

// 상세 페이지 열기
function openDetailPage(notice) {
    // 상세 페이지로 이동 (실제로는 별도 HTML 파일)
    alert(`"${notice.title}" 상세 페이지로 이동합니다.`);
    // window.location.href = 'notice-detail.html?id=' + notice.id;
}
