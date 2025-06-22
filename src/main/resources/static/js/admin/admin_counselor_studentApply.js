// 샘플 데이터
const applicationData = [
    {
        id: 1,
        studentId: '2021001234',
        studentName: '김학생',
        category: '심리',
        counselor: '김상담',
        date: '2025-06-20 14:00',
        status: '진행',
        phone: '010-1234-5678',
        email: 'kim@student.ac.kr',
        content: '최근 학업 스트레스와 대인관계 문제로 상담을 받고 싶습니다.',
        memo: ''
    },
    {
        id: 2,
        studentId: '2020005678',
        studentName: '이학생',
        category: '진로/취업',
        counselor: '이상담',
        date: '2025-06-18 10:30',
        status: '완료',
        phone: '010-2345-6789',
        email: 'lee@student.ac.kr',
        content: '진로 선택과 취업 준비에 대한 전문적인 조언이 필요합니다.',
        memo: '상담 완료, 만족도 높음'
    },
    {
        id: 3,
        studentId: '2022009876',
        studentName: '박학생',
        category: '학습 컨설팅',
        counselor: '박상담',
        date: '2025-06-22 15:30',
        status: '진행',
        phone: '010-3456-7890',
        email: 'park@student.ac.kr',
        content: '효율적인 학습 방법과 시간 관리에 대해 상담받고 싶습니다.',
        memo: ''
    },
    {
        id: 4,
        studentId: '2019003456',
        studentName: '최학생',
        category: '위기',
        counselor: '최상담',
        date: '2025-06-15 09:00',
        status: '취소',
        phone: '010-4567-8901',
        email: 'choi@student.ac.kr',
        content: '긴급한 심리적 지원이 필요한 상황입니다.',
        memo: '다른 전문기관 연계'
    },
    {
        id: 5,
        studentId: '2021007890',
        studentName: '정학생',
        category: '익명',
        counselor: '정상담',
        date: '2025-06-25 16:00',
        status: '진행',
        phone: '010-5678-9012',
        email: 'jung@student.ac.kr',
        content: '개인적인 문제로 익명 상담을 희망합니다.',
        memo: ''
    },
    {
        id: 6,
        studentId: '2020001111',
        studentName: '강학생',
        category: '심리',
        counselor: '김상담',
        date: '2025-06-19 11:00',
        status: '완료',
        phone: '010-1111-2222',
        email: 'kang@student.ac.kr',
        content: '불안감과 우울감에 대한 상담을 받고 싶습니다.',
        memo: '정기 상담 권유함'
    },
    {
        id: 7,
        studentId: '2022002222',
        studentName: '윤학생',
        category: '진로/취업',
        counselor: '이상담',
        date: '2025-06-28 13:30',
        status: '진행',
        phone: '010-2222-3333',
        email: 'yoon@student.ac.kr',
        content: '전공 분야 취업 전략에 대해 상담받고 싶습니다.',
        memo: ''
    },
    {
        id: 8,
        studentId: '2021003333',
        studentName: '임학생',
        category: '학습 컨설팅',
        counselor: '박상담',
        date: '2025-06-17 14:30',
        status: '완료',
        phone: '010-3333-4444',
        email: 'lim@student.ac.kr',
        content: '성적 향상을 위한 학습 전략을 세우고 싶습니다.',
        memo: '학습 계획표 제공함'
    },
    {
        id: 9,
        studentId: '2020004444',
        studentName: '장학생',
        category: '위기',
        counselor: '최상담',
        date: '2025-06-30 10:00',
        status: '진행',
        phone: '010-4444-5555',
        email: 'jang@student.ac.kr',
        content: '최근 개인적인 문제로 인해 심리적으로 매우 힘든 상황입니다.',
        memo: ''
    },
    {
        id: 10,
        studentId: '2019005555',
        studentName: '신학생',
        category: '익명',
        counselor: '정상담',
        date: '2025-06-14 15:00',
        status: '취소',
        phone: '010-5555-6666',
        email: 'shin@student.ac.kr',
        content: '익명으로 상담을 받고 싶습니다.',
        memo: '학생 개인 사정으로 취소'
    },
    {
        id: 11,
        studentId: '2022006666',
        studentName: '오학생',
        category: '심리',
        counselor: '김상담',
        date: '2025-07-02 09:30',
        status: '진행',
        phone: '010-6666-7777',
        email: 'oh@student.ac.kr',
        content: '학업 스트레스 관리에 대한 상담을 받고 싶습니다.',
        memo: ''
    },
    {
        id: 12,
        studentId: '2021007777',
        studentName: '한학생',
        category: '진로/취업',
        counselor: '이상담',
        date: '2025-06-16 16:30',
        status: '완료',
        phone: '010-7777-8888',
        email: 'han@student.ac.kr',
        content: '졸업 후 취업 준비에 대한 조언을 구하고 싶습니다.',
        memo: '상담 완료, 추가 자료 제공함'
    }
];

let currentPage = 1;
let itemsPerPage = 10;
let filteredData = [...applicationData];
let currentApplication = null;


// 상태 배지 렌더링
function getStatusBadge(status) {
    let badgeClass = 'status-badge ';
    switch (status) {
        case '진행':
            badgeClass += 'status-progress';
            break;
        case '완료':
            badgeClass += 'status-completed';
            break;
        case '취소':
            badgeClass += 'status-cancelled';
            break;
        default:
            badgeClass += 'status-progress';
    }
    return `<span class="${badgeClass}">${status}</span>`;
}

// 테이블 렌더링
function renderTable() {
    const tableBody = document.getElementById('applicationTableBody');
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const pageData = filteredData.slice(startIndex, endIndex);

    tableBody.innerHTML = '';
    
    pageData.forEach((application, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="col-no">${startIndex + index + 1}</td>
            <td class="col-student-id">${application.studentId}</td>
            <td class="col-student-name">${application.studentName}</td>
            <td class="col-category">${application.category}</td>
            <td class="col-counselor">${application.counselor}</td>
            <td class="col-date">${application.date}</td>
            <td class="col-status">${getStatusBadge(application.status)}</td>
        `;
        
        // 행 클릭 이벤트 추가
        row.addEventListener('click', () => openDetailModal(application));
        row.style.cursor = 'pointer';
        
        tableBody.appendChild(row);
    });

    updatePagination();
}

// 페이지네이션 업데이트
function updatePagination() {
    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    const paginationContainer = document.getElementById('pagination');
    const startItem = (currentPage - 1) * itemsPerPage + 1;
    const endItem = Math.min(currentPage * itemsPerPage, filteredData.length);
    
    // 페이지네이션 버튼 생성
    let paginationHTML = '';
    
    // 이전 버튼
    paginationHTML += `<button class="pagination-btn ${currentPage === 1 ? 'disabled' : ''}" onclick="goToPage(${currentPage - 1})">
        <i class="fas fa-angle-left"></i>
    </button>`;
    
    // 페이지 번호 버튼들
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `<button class="pagination-btn ${i === currentPage ? 'active' : ''}" onclick="goToPage(${i})">${i}</button>`;
    }
    
    // 다음 버튼
    paginationHTML += `<button class="pagination-btn ${currentPage === totalPages ? 'disabled' : ''}" onclick="goToPage(${currentPage + 1})">
        <i class="fas fa-angle-right"></i>
    </button>`;
    
    paginationContainer.innerHTML = paginationHTML;
    
    // 페이지 정보 업데이트
    document.getElementById('paginationInfo').textContent = 
        `총 ${filteredData.length}건 중 ${startItem}-${endItem}건 표시`;
}

// 페이지 이동
function goToPage(page) {
    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    if (page >= 1 && page <= totalPages && page !== currentPage) {
        currentPage = page;
        renderTable();
    }
}

// 검색 기능
function searchApplications() {
    const searchType = document.getElementById('searchType').value;
    const searchValue = document.getElementById('searchInput').value.trim().toLowerCase();
    const counselingType = document.getElementById('counselingType').value;
    const statusType = document.getElementById('statusType').value;

    filteredData = applicationData.filter(application => {
        // 상담분류 필터
        let typeMatch = true;
        if (counselingType) {
            typeMatch = application.category.includes(counselingType);
        }

        // 현황분류 필터
        let statusMatch = true;
        if (statusType) {
            statusMatch = application.status === statusType;
        }

        // 검색어 필터
        let searchMatch = true;
        if (searchValue) {
            switch (searchType) {
                case 'student_id':
                    searchMatch = application.studentId.toLowerCase().includes(searchValue);
                    break;
                case 'student_name':
                    searchMatch = application.studentName.toLowerCase().includes(searchValue);
                    break;
                case 'counselor':
                    searchMatch = application.counselor.toLowerCase().includes(searchValue);
                    break;
            }
        }

        return typeMatch && statusMatch && searchMatch;
    });

    currentPage = 1;
    renderTable();
}

// 상세보기 모달 열기
function openDetailModal(application) {
    currentApplication = application;
    
    // 기본 정보 채우기
    document.getElementById('modalStudentId').textContent = application.studentId;
    document.getElementById('modalStudentName').textContent = application.studentName;
    document.getElementById('modalPhone').textContent = application.phone || '-';
    document.getElementById('modalEmail').textContent = application.email || '-';
    document.getElementById('modalCategory').textContent = application.category;
    document.getElementById('modalCounselor').textContent = application.counselor;
    document.getElementById('modalDateTime').textContent = application.date;
    document.getElementById('modalCurrentStatus').innerHTML = getStatusBadge(application.status);
    document.getElementById('modalContent').textContent = application.content || '상담 요청 내용이 없습니다.';
    
    document.getElementById('detailModal').classList.add('show');
    document.body.style.overflow = 'hidden';
}

// 상세보기 모달 닫기
function closeDetailModal() {
    document.getElementById('detailModal').classList.remove('show');
    document.body.style.overflow = 'auto';
    currentApplication = null;
}

// 모달 외부 클릭 시 닫기
document.getElementById('detailModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeDetailModal();
    }
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeDetailModal();
    }
});
document.getElementById('counselingType').addEventListener('change', searchApplications);
document.getElementById('statusType').addEventListener('change', searchApplications);

// 검색 입력 시 엔터키로 검색
document.getElementById('searchInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        searchApplications();
    }
});

// 초기 테이블 렌더링
document.addEventListener('DOMContentLoaded', function() {
    renderTable();
});