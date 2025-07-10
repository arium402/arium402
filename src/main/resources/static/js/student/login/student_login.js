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


/*function switchTab(tabName) {
         // 모든 탭 버튼 비활성화
         document.querySelectorAll('.tab-button').forEach(btn => {
             btn.classList.remove('active');
         });
         
         // 모든 탭 컨텐츠 숨기기
         document.querySelectorAll('.tab-content').forEach(content => {
             content.classList.remove('active');
         });
         
         // 선택된 탭 활성화
         event.target.classList.add('active');
         document.getElementById(tabName + '-tab').classList.add('active');
     }
*/