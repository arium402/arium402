// 사이드바 토글 기능
document.getElementById('sidebarToggle').addEventListener('click', function() {
    const sidebar = document.getElementById('layoutSidenav_nav');
    const content = document.getElementById('layoutSidenav_content');
    
    if (window.innerWidth <= 768) {
        sidebar.classList.toggle('show');
    } else {
        sidebar.classList.toggle('collapsed');
        content.classList.toggle('expanded');
    }
});

// 윈도우 리사이즈 시 클래스 정리
window.addEventListener('resize', function() {
    const sidebar = document.getElementById('layoutSidenav_nav');
    const content = document.getElementById('layoutSidenav_content');
    
    if (window.innerWidth > 768) {
        sidebar.classList.remove('show');
    } else {
        sidebar.classList.remove('collapsed');
        content.classList.remove('expanded');
    }
});

// 모달 열기
function openEditModal() {
    const modal = document.getElementById('editModal');
    const currentStatus = document.getElementById('counselorStatus').textContent.trim();
    const statusSelect = document.getElementById('statusSelect');
    
    // 현재 정보를 모달에 표시
    document.getElementById('modalName').value = document.getElementById('counselorName').textContent;
    document.getElementById('modalEmpNo').value = document.getElementById('counselorEmpNo').textContent;
    document.getElementById('modalField').value = document.getElementById('counselorField').textContent;
    document.getElementById('modalPhone').value = document.getElementById('counselorPhone').textContent;
    document.getElementById('modalEmail').value = document.getElementById('counselorEmail').textContent;
    
    // 현재 상태에 따라 셀렉트 박스 설정
    if (currentStatus === '재직') {
        statusSelect.value = 'active';
    } else {
        statusSelect.value = 'inactive';
    }
    
    modal.style.display = 'block';
}

// 모달 닫기
function closeEditModal() {
    const modal = document.getElementById('editModal');
    modal.style.display = 'none';
}

// 상태 저장
function saveStatus() {
    const statusSelect = document.getElementById('statusSelect');
    const selectedValue = statusSelect.value;
    const statusElement = document.getElementById('counselorStatus');
    const statusBadge = statusElement.parentElement.querySelector('.status-badge');
    
    // 상태 업데이트
    if (selectedValue === 'active') {
        statusElement.textContent = '재직';
        statusBadge.className = 'status-badge active';
    } else {
        statusElement.textContent = '퇴사';
        statusBadge.className = 'status-badge inactive';
    }
    
    // 수정일 업데이트
    const today = new Date();
    const formattedDate = today.toISOString().split('T')[0];
    document.getElementById('counselorModDate').textContent = formattedDate;
    
    // 모달 닫기
    closeEditModal();
    
    // 성공 알림
    alert('재직현황이 성공적으로 수정되었습니다.');
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    const modal = document.getElementById('editModal');
    if (event.target === modal) {
        closeEditModal();
    }
}

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeEditModal();
    }
});

// 페이지 로드 시 URL 파라미터로 상담사 정보 로드
document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const counselorId = urlParams.get('id');
    
    loadCounselorData(counselorId);
});

// 상담사 데이터 로드 함수
function loadCounselorData(id) {
    // 샘플 데이터
    const counselorData = {
        1: {
            name: '김상담',
            empNo: 'EMP001',
            field: '심리상담',
            phone: '010-1234-5678',
            email: 'kim@example.com',
            status: '재직',
            regDate: '2024-01-15',
            modDate: '2024-06-10'
        },
        2: {
            name: '이상담',
            empNo: 'EMP002',
            field: '진로상담',
            phone: '010-2345-6789',
            email: 'lee@example.com',
            status: '재직',
            regDate: '2024-02-20',
            modDate: '2024-05-15'
        },
        3: {
            name: '박상담',
            empNo: 'EMP003',
            field: '학습컨설팅',
            phone: '010-3456-7890',
            email: 'park@example.com',
            status: '퇴사',
            regDate: '2023-12-10',
            modDate: '2024-03-20'
        }
    };

    const data = counselorData[id] || counselorData[1];

    // 데이터를 화면에 표시
    document.getElementById('counselorName').textContent = data.name;
    document.getElementById('counselorEmpNo').textContent = data.empNo;
    document.getElementById('counselorField').textContent = data.field;
    document.getElementById('counselorPhone').textContent = data.phone;
    document.getElementById('counselorEmail').textContent = data.email;
    document.getElementById('counselorRegDate').textContent = data.regDate;
    document.getElementById('counselorModDate').textContent = data.modDate;

    // 상태 표시
    const statusElement = document.getElementById('counselorStatus');
    const statusBadge = statusElement.parentElement.querySelector('.status-badge');
    
    statusElement.textContent = data.status;
    if (data.status === '재직') {
        statusBadge.className = 'status-badge active';
    } else {
        statusBadge.className = 'status-badge inactive';
    }
}
