// 샘플 데이터
const applicantData = [
    {
        id: 1,
        studentId: '2021001234',
        name: '김학생',
        department: '컴퓨터과학과',
        completion: 'Y',
        satisfaction: 'Y',
        paymentStatus: 'waiting'
    },
    {
        id: 2,
        studentId: '2020005678',
        name: '이학생',
        department: '경영학과',
        completion: 'Y',
        satisfaction: 'Y',
        paymentStatus: 'completed'
    },
    {
        id: 3,
        studentId: '2022009876',
        name: '박학생',
        department: '영어영문학과',
        completion: 'Y',
        satisfaction: 'N',
        paymentStatus: 'disabled'
    },
    {
        id: 4,
        studentId: '2019003456',
        name: '최학생',
        department: '법학과',
        completion: 'N',
        satisfaction: 'Y',
        paymentStatus: 'disabled'
    },
    {
        id: 5,
        studentId: '2021007890',
        name: '정학생',
        department: '교육학과',
        completion: 'Y',
        satisfaction: 'Y',
        paymentStatus: 'waiting'
    },
    {
        id: 6,
        studentId: '2020001111',
        name: '강학생',
        department: '심리학과',
        completion: 'Y',
        satisfaction: 'Y',
        paymentStatus: 'completed'
    },
    {
        id: 7,
        studentId: '2022002222',
        name: '윤학생',
        department: '사회복지학과',
        completion: 'N',
        satisfaction: 'N',
        paymentStatus: 'disabled'
    },
    {
        id: 8,
        studentId: '2021003333',
        name: '임학생',
        department: '간호학과',
        completion: 'Y',
        satisfaction: 'Y',
        paymentStatus: 'waiting'
    },
    {
        id: 9,
        studentId: '2020004444',
        name: '장학생',
        department: '행정학과',
        completion: 'Y',
        satisfaction: 'Y',
        paymentStatus: 'waiting'
    },
    {
        id: 10,
        studentId: '2019005555',
        name: '신학생',
        department: '농학과',
        completion: 'Y',
        satisfaction: 'N',
        paymentStatus: 'disabled'
    },
    {
        id: 11,
        studentId: '2022006666',
        name: '오학생',
        department: '미디어영상학과',
        completion: 'Y',
        satisfaction: 'Y',
        paymentStatus: 'completed'
    },
    {
        id: 12,
        studentId: '2021007777',
        name: '한학생',
        department: '정보통계학과',
        completion: 'Y',
        satisfaction: 'Y',
        paymentStatus: 'waiting'
    }
];


// Y/N 배지 렌더링
function getYNBadge(value) {
    if (value === 'Y') {
        return '<span class="yn-badge yes">Y</span>';
    } else {
        return '<span class="yn-badge no">N</span>';
    }
}

// 지급 버튼 렌더링
function getPaymentButton(applicant) {
    if (applicant.completion === 'Y' && applicant.satisfaction === 'Y') {
        if (applicant.paymentStatus === 'waiting') {
            return `<button class="payment-btn waiting" onclick="changePaymentStatus(${applicant.id})">지급대기</button>`;
        } else if (applicant.paymentStatus === 'completed') {
            return `<button class="payment-btn completed">완료</button>`;
        }
    }
    return '<button class="payment-btn disabled">-</button>';
}

// 테이블 렌더링
function renderTable() {
    const tableBody = document.getElementById('applicantTableBody');
    tableBody.innerHTML = '';
    
    applicantData.forEach((applicant, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="col-no">${index + 1}</td>
            <td class="col-student-id">${applicant.studentId}</td>
            <td class="col-name">${applicant.name}</td>
            <td class="col-department">${applicant.department}</td>
            <td class="col-completion">${getYNBadge(applicant.completion)}</td>
            <td class="col-satisfaction">${getYNBadge(applicant.satisfaction)}</td>
            <td class="col-payment">${getPaymentButton(applicant)}</td>
        `;
        tableBody.appendChild(row);
    });
}

// 개별 지급 상태 변경
function changePaymentStatus(applicantId) {
    const applicant = applicantData.find(a => a.id === applicantId);
    if (applicant && applicant.paymentStatus === 'waiting') {
        applicant.paymentStatus = 'completed';
        renderTable();
    }
}

// 전체 지급
function bulkPayment() {
    let changedCount = 0;
    applicantData.forEach(applicant => {
        if (applicant.completion === 'Y' && applicant.satisfaction === 'Y' && applicant.paymentStatus === 'waiting') {
            applicant.paymentStatus = 'completed';
            changedCount++;
        }
    });
    
    if (changedCount > 0) {
        alert(`${changedCount}명의 마일리지가 지급되었습니다.`);
        renderTable();
    } else {
        alert('지급 대기 중인 대상자가 없습니다.');
    }
}

// 목록으로 돌아가기
function goToList() {
    // 여기서 실제로는 이전 페이지로 이동하는 로직을 구현
    if (confirm('목록으로 돌아가시겠습니까?')) {
        // window.location.href = 'mileage-payment.html';
        alert('목록 페이지로 이동합니다.');
    }
}

// 초기 테이블 렌더링
document.addEventListener('DOMContentLoaded', function() {
    renderTable();
});
