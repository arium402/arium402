

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
