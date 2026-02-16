function goToList() {
	if (confirm('목록으로 돌아가시겠습니까?')) {
		alert('신청내역 페이지로 이동합니다.');
		location.href = '/student/noncurr/addcheck';
	}
}

// ✅ 사이드바 비교과 메뉴 활성화 함수 (detail 페이지 방식 참고)
function activateNoncurrSidebar() {
    // 비교과 신청내역 링크 찾기 (survey는 addcheck에서 파생된 페이지)
    const noncurrAddcheckLink = document.querySelector('.sidebar a[href="/student/noncurr/addcheck"]');
    if (noncurrAddcheckLink) {
        // 링크 활성화
        noncurrAddcheckLink.classList.add('active');
        
        // 부모 li 활성화
        const li = noncurrAddcheckLink.closest('li');
        if (li) li.classList.add('active');
        
        // 서브메뉴 열기
        const submenu = noncurrAddcheckLink.closest('.submenu');
        if (submenu) {
            const parentLi = submenu.closest('li');
            if (parentLi) {
                parentLi.classList.add('active');
                const submenuEl = parentLi.querySelector('.submenu');
                if (submenuEl) submenuEl.style.display = 'block';
            }
        }
    }
    
    console.log('비교과 사이드바 메뉴 활성화 완료');
}

// ✅ 모든 문항 체크 여부 검증 함수
function validateAllQuestions() {
    let isValid = true;
    let missingCounts = {
        section1: 0,
        section2: 0,
        section3: 0
    };
    
    // Section 1 검증 (종합 만족도)
    const section1Questions = document.querySelectorAll('.section1-question');
    const section1Names = [...new Set(Array.from(section1Questions).map(input => input.name))];
    
    section1Names.forEach(name => {
        const checkedInputs = document.querySelectorAll(`input[name="${name}"]:checked`);
        if (checkedInputs.length === 0) {
            missingCounts.section1++;
            isValid = false;
        }
    });
    
    // Section 2 검증 (프로그램 내용)
    const section2Questions = document.querySelectorAll('.section2-question');
    const section2Names = [...new Set(Array.from(section2Questions).map(input => input.name))];
    
    section2Names.forEach(name => {
        const checkedInputs = document.querySelectorAll(`input[name="${name}"]:checked`);
        if (checkedInputs.length === 0) {
            missingCounts.section2++;
            isValid = false;
        }
    });
    
    // Section 3 검증 (강사)
    const section3Questions = document.querySelectorAll('.section3-question');
    const section3Names = [...new Set(Array.from(section3Questions).map(input => input.name))];
    
    section3Names.forEach(name => {
        const checkedInputs = document.querySelectorAll(`input[name="${name}"]:checked`);
        if (checkedInputs.length === 0) {
            missingCounts.section3++;
            isValid = false;
        }
    });
    
    // 검증 결과 메시지 생성
    if (!isValid) {
        let errorMessage = '다음 섹션의 문항들을 모두 체크해주세요:\n\n';
        
        if (missingCounts.section1 > 0) {
            errorMessage += `• 프로그램 종합 만족도: ${missingCounts.section1}개 문항 미응답\n`;
        }
        if (missingCounts.section2 > 0) {
            errorMessage += `• 프로그램 내용: ${missingCounts.section2}개 문항 미응답\n`;
        }
        if (missingCounts.section3 > 0) {
            errorMessage += `• 프로그램 강사: ${missingCounts.section3}개 문항 미응답\n`;
        }
        
        alert(errorMessage);
        return false;
    }
    
    return true;
}

// ✅ 실제 만족도 조사 제출 함수 (API 호출 버전)
function submitSatisfactionSurvey() {
    // URL에서 프로그램 ID 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const prgId = urlParams.get('prgId');
    
    if (!prgId) {
        alert('프로그램 정보를 찾을 수 없습니다.');
        return;
    }
    
    // 폼 데이터 수집
    const formData = new FormData(document.getElementById('satisfactionSurvey'));
    const surveyData = {};
    
    // FormData를 객체로 변환
    for (let [key, value] of formData.entries()) {
        surveyData[key] = parseInt(value);
    }
    
    console.log('제출할 만족도 조사 데이터:', surveyData);
    
    // API 호출
    fetch(`/api/student/noncurr/submit-survey?prgId=${prgId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(surveyData)
    })
    .then(response => response.json())
    .then(data => {
        alert(data.message);
        if (data.success) {
            // 성공시 신청내역 페이지로 이동
            location.href = '/student/noncurr/addcheck';
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('만족도 조사 제출 중 오류가 발생했습니다.');
    });
}

// ✅ 실시간 진행률 표시 (선택사항)
function updateProgress() {
    const totalQuestions = document.querySelectorAll('input[type="radio"]').length / 5; // 5점 척도이므로 5로 나눔
    const answeredQuestions = document.querySelectorAll('input[type="radio"]:checked').length;
    
    const progress = Math.round((answeredQuestions / totalQuestions) * 100);
    
    console.log(`진행률: ${progress}% (${answeredQuestions}/${totalQuestions})`);
}

// ✅ 페이지 로드시 실행
document.addEventListener('DOMContentLoaded', function() {
    console.log('만족도 조사 페이지 로드 완료');
    
    // ✅ 비교과 프로그램 사이드바 메뉴 강제 활성화 (detail 페이지 방식)
    activateNoncurrSidebar();
    
    // 라디오 버튼 변경시 진행률 업데이트
    const radioButtons = document.querySelectorAll('input[type="radio"]');
    radioButtons.forEach(radio => {
        radio.addEventListener('change', updateProgress);
    });
    
    // 초기 진행률 계산
    updateProgress();
});

// ✅ 폼 제출 처리 (검증 로직 추가)
document.getElementById('satisfactionSurvey').addEventListener('submit', function(e) {
	e.preventDefault();
    
    // 모든 문항 체크 여부 검증
    if (!validateAllQuestions()) {
        return; // 검증 실패시 제출 중단
    }
  
	if (confirm('만족도 조사를 제출하시겠습니까?')) {
        // ✅ 실제 제출 로직 (나중에 API 호출로 변경)
        submitSatisfactionSurvey();
	}
});