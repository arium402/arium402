function updateProgress() {
	const form = document.getElementById('diagnosisForm');
	const questions = form.querySelectorAll('input[type="radio"]');
	const questionNames = new Set();
  
	questions.forEach(question => {
		questionNames.add(question.name);
	});
  
	let answeredQuestions = 0;
	questionNames.forEach(name => {
		const checked = form.querySelector(`input[name="${name}"]:checked`);
		if (checked) {
			answeredQuestions++;
		}
	});

	const totalQuestions = questionNames.size;
	const percentage = Math.round((answeredQuestions / totalQuestions) * 100);

	// 진행률 텍스트 업데이트
	document.getElementById('progressText').textContent = `진행률: ${answeredQuestions}/${totalQuestions} (${percentage}%)`;

	// 진행률 바 업데이트
	document.getElementById('progressBar').style.width = `${percentage}%`;
}

function showResults() {
	// 모든 문항이 답변되었는지 확인
	const form = document.getElementById('diagnosisForm');
	const questions = form.querySelectorAll('input[type="radio"]');
	const questionNames = new Set();
  
	questions.forEach(question => {
		questionNames.add(question.name);
	});

	let answeredQuestions = 0;
	let firstUnansweredQuestion = null;

	// 문항 번호 순서대로 정렬하여 확인
	const sortedQuestionNames = Array.from(questionNames).sort((a, b) => {
		const numA = parseInt(a.substring(1));
		const numB = parseInt(b.substring(1));
		return numA - numB;
	});

	sortedQuestionNames.forEach(name => {
		const checked = form.querySelector(`input[name="${name}"]:checked`);
		if (checked) {
			answeredQuestions++;
		}
		else if (!firstUnansweredQuestion) {
			firstUnansweredQuestion = name;
		}
	});

	if (answeredQuestions < questionNames.size) {
		alert(`모든 문항에 답변해주세요. (${answeredQuestions}/${questionNames.size} 완료)`);

		// 첫 번째 미완성 문항으로 스크롤 이동
		if (firstUnansweredQuestion) {
			const firstRadio = form.querySelector(`input[name="${firstUnansweredQuestion}"]`);
			if (firstRadio) {
				const questionItem = firstRadio.closest('.question-item');
				if (questionItem) {
					// 해당 문항 강조 효과
					questionItem.style.border = '3px solid #ff6b6b';
					questionItem.style.backgroundColor = '#fff5f5';

					// scrollIntoView를 사용해서 안정적인 스크롤
					setTimeout(() => {
						questionItem.scrollIntoView({ 
							behavior: 'smooth', 
							block: 'center',  // 화면 중앙에 위치
							inline: 'nearest' 
						});
					}, 100); // alert 창이 닫힌 후 스크롤

					// 3초 후 강조 효과 제거
					setTimeout(() => {
						questionItem.style.border = '1px solid #e0e0e0';
						questionItem.style.backgroundColor = '#fafafa';
					}, 3000);
				}
			}
		}
		return;
	}
	
	const answers = [];
	const checked = form.querySelectorAll('input[type="radio"]:checked');
	
	checked.forEach(radio => {
		const value = radio.value;
		const parts = value.split('_');
		const qstId = parseInt(parts[0]);
		const ansScore = parseInt(parts[1]);
		
		answers.push({
			qstId: qstId,
			ansScore: ansScore
		});
	});
	
	console.log('전송할 데이터:', answers); // 디버깅용
	
	// 서버로 데이터 전송
	fetch('/student/competence/submit', {
		method: 'POST',
		headers : {"content-type" : "application/json"},
		body: JSON.stringify(answers)
	})
	.then(response => response.text())
	.then(data => {
		console.log('서버 응답:', data);
		
		if (data.startsWith("ERROR:")){
			alert('저장 중 오류가 발생했습니다: ' + data.message);
		}
		else {
			// 모든 문항이 답변되었으면 결과 페이지로 이동
			alert('진단이 완료되었습니다. 결과 페이지로 이동합니다.');
			location.href = /*[[@{/student/competence/chart}]]*/ `/student/competence/chart?evalId=${data.evalId}`;
		}
	}).catch(error => {
		console.error("Error : ", error);
		alert('서버 오류가 발생했습니다.');
	});
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
	// 라디오 버튼 변경 시 진행률 업데이트
	const form = document.getElementById('diagnosisForm');
	form.addEventListener('change', updateProgress);
  
	// 초기 진행률 설정
	updateProgress();
});