// 기존 submitForm 함수를 완전히 이것으로 교체하세요
function submitForm() {
    // JSON 형태로 데이터 구성
    const data = {
        prgNm: document.getElementById('prgNm').value,
        prgDesc: document.getElementById('prgDesc').value,
        recruitStart: document.getElementById('recruitStart').value,
        recruitEnd: document.getElementById('recruitEnd').value,
        prgStDt: document.getElementById('prgStDt').value,
        prgEndDt: document.getElementById('prgEndDt').value,
        maxCnt: parseInt(document.getElementById('maxCnt').value),
        department: document.getElementById('department').value,
        contact: document.getElementById('contact').value,
        surveyDt: document.getElementById('surveyDt').value,
        mlgDefScore: parseInt(document.getElementById('mlgDefScore').value),
        competencies: []
    };
    
    // 핵심역량 수집
    document.querySelectorAll('input[name="competencies"]:checked').forEach(checkbox => {
        data.competencies.push(parseInt(checkbox.value));
    });
    
    console.log('전송할 데이터:', data);
    
    // 버튼 비활성화
    const submitBtn = document.querySelector('.btn-register');
    const originalText = submitBtn.innerHTML;
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="material-symbols-outlined">hourglass_empty</span> 등록 중...';
    
    // JSON으로 전송
    fetch('/api/admin/noncurr/json', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('비교과 프로그램이 성공적으로 등록되었습니다.');
            goToList();
        } else {
            alert('등록 실패: ' + (data.message || '알 수 없는 오류'));
        }
    })
    .catch(error => {
        console.error('등록 오류:', error);
        alert('등록 중 오류가 발생했습니다.');
    })
    .finally(() => {
        // 버튼 복원
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalText;
    });
    
    // form의 기본 제출 막기
    return false;
}