function applyProgram(programId) {
	// 프로그램별 신청 처리
	const programNames = {
		1: '진로탐색 및 자기계발 워크샵',
		2: '창의적 사고력 개발 프로그램',
		3: '리더십 스킬 향상 세미나',
		4: '심리상담 기초 이론과 실습',
		5: '취업 전략 및 면접 스킬 특강',
		6: '창업 아이디어 발굴 워크샵',
		7: '데이터 분석 기초 교육',
		8: '글로벌 커뮤니케이션 스킬'
	};

	const programName = programNames[programId] || '프로그램';

	// 신청완료 상태인 프로그램 (3번)
	if (programId === 3) {
		alert(`${programName} 상세페이지로 이동합니다.`);
		location.href = /*[[@{/student/noncurr/detail}]]*/ '/student/noncurr/detail';
		return;
	}

	alert(`${programName} 상세페이지로 이동합니다.`);
	location.href = /*[[@{/student/noncurr/detail}]]*/ '/student/noncurr/detail';
}