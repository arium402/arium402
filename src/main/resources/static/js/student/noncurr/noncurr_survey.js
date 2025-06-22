function goToList() {
	if (confirm('목록으로 돌아가시겠습니까?')) {
		alert('신청내역 페이지로 이동합니다.');
		location.href = /*[[@{/student/noncurr/addcheck}]]*/ '/student/noncurr/addcheck';
	}
}

// 폼 제출 처리
document.getElementById('satisfactionSurvey').addEventListener('submit', function(e) {
	e.preventDefault();
  
	if (confirm('만족도 조사를 제출하시겠습니까?')) {
		alert('만족도 조사가 완료되었습니다!');
		location.href = /*[[@{/student/noncurr/addcheck}]]*/ '/student/noncurr/addcheck';
	}
});