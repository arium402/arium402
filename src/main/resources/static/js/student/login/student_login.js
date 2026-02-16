// DOM이 준비되면 로그인 실패 여부 확인
window.addEventListener('DOMContentLoaded', function () {
    
	//현재 페이지 url의 쿼리 파라미터 가져옴
	const params = new URLSearchParams(window.location.search);
	
	//로그인 실패 여부에 따라 알림창 표시
    if (params.get('error') === 'bad') {
        alert('아이디 또는 비밀번호가 틀렸습니다.');
    } else if (params.get('error') === 'forbidden') {
        alert('접근 권한이 없습니다.');
    } else if (params.get('error') !== null) {
        alert('로그인에 실패했습니다. 다시 시도해주세요.');
    }

	//로그인 탭 자동 전환: url 파라미터 type값 확인
	  const type = params.get('type');
	  if (type === 'counselor') { //counselor면 상담사 탭으로 전환
	      switchTab('counselor');
	  } else {
	      switchTab('student'); // 기본값으로 학생 탭
	  }
	  
	// 뒤로가기 후 URL에 쿼리 파라미터가 남아있으면
	// 알림이 계속 뜰 수 있으므로 쿼리 파라미터 제거 (URL만 변경, 페이지 리로드는 안 함)
    const url = new URL(window.location);
    url.search = '';
    window.history.replaceState({}, document.title, url.toString());
});

function switchTab(tabName) {
    // 1) 모든 탭 버튼에서 'active' 클래스 제거 → 모두 비활성화 상태로 만듦
    document.querySelectorAll('.tab-button').forEach(btn => {
        btn.classList.remove('active');
    });

	// 2) 모든 탭 콘텐츠(실제 로그인 폼 등)에서 'active' 클래스 제거 → 모두 숨김 처리
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });

	// 3) 파라미터로 받은 탭 이름을 이용해 활성화할 버튼과 콘텐츠를 선택
	// 예: tabName이 'counselor'면 .tab-button[onclick*="counselor"], id='counselor-tab' 선택
    const targetBtn = document.querySelector(`.tab-button[onclick*="${tabName}"]`);
    const targetContent = document.getElementById(`${tabName}-tab`);

	// 4) 해당 버튼과 콘텐츠에 'active' 클래스를 추가 → 활성화 및 보임 처리
    if (targetBtn) targetBtn.classList.add('active');
    if (targetContent) targetContent.classList.add('active');
}

// 팝업창 참조를 저장할 전역 변수
let currentPopup = null;


// 기존 팝업창을 닫고 새 팝업창을 여는 공통 함수
function openSinglePopup(url, windowName, windowFeatures) {
    // 기존에 열린 팝업창이 있으면 닫기
    if (currentPopup && !currentPopup.closed) {
        currentPopup.close();
    }
    
    // 새 팝업창 열기
    currentPopup = window.open(url, windowName, windowFeatures);
    
    // 팝업창이 닫혔을 때 참조 초기화
    if (currentPopup) {
        currentPopup.addEventListener('beforeunload', function() {
            currentPopup = null;
        });
    }
    
    return currentPopup;
}

// 팝업창 열기 함수들
function openFindStudentId() {
    const url = '/student/find_student_id'; // 실제 파일 경로로 수정 필요
    const windowName = 'findStudentId';
    const windowFeatures = 'width=500,height=650,scrollbars=yes,resizable=yes,location=no,menubar=no,toolbar=no,status=no';
            
    openSinglePopup(url, windowName, windowFeatures);
}

function openFindCounselorId() {
    const url = '/student/find_counselor_id'; // 실제 파일 경로로 수정 필요
    const windowName = 'findCounselorId';
    const windowFeatures = 'width=500,height=650,scrollbars=yes,resizable=yes,location=no,menubar=no,toolbar=no,status=no';
            
    openSinglePopup(url, windowName, windowFeatures);
}

function openFindStudentPassword() {
    const url = '/student/find_student_password'; // 실제 파일 경로로 수정 필요
    const windowName = 'findStudentPassword';
    const windowFeatures = 'width=500,height=700,scrollbars=yes,resizable=yes,location=no,menubar=no,toolbar=no,status=no';
            
    openSinglePopup(url, windowName, windowFeatures);
}

function openFindCounselorPassword() {
    const url = '/student/find_counselor_password'; // 실제 파일 경로로 수정 필요
    const windowName = 'findCounselorPassword';
    const windowFeatures = 'width=500,height=700,scrollbars=yes,resizable=yes,location=no,menubar=no,toolbar=no,status=no';
            
    openSinglePopup(url, windowName, windowFeatures);
}

function openChangePassword() {
    const url = '/student/change_password'; // 실제 파일 경로로 수정 필요
    const windowName = 'changePassword';
    const windowFeatures = 'width=500,height=750,scrollbars=yes,resizable=yes,location=no,menubar=no,toolbar=no,status=no';
            
    openSinglePopup(url, windowName, windowFeatures);
}