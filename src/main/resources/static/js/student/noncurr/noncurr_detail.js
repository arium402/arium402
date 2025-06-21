function goBack() {
	if (confirm('목록으로 돌아가시겠습니까?')) {
		// 목록 페이지로 이동 로직
		alert('목록 페이지로 이동합니다.');
	}
}

function applyProgram() {
	alert('프로그램 신청 페이지로 이동합니다.');
	location.href = /*[[@{/student/noncurr/add}]]*/ '/student/noncurr/add';
}

function downloadFile(filename) {
	alert(`${filename} 파일을 다운로드합니다.`);
	// 실제 다운로드 로직
}

// 차트 인스턴스를 전역으로 관리
let learningChart, myChart;

// 차트 생성
document.addEventListener('DOMContentLoaded', function() {

	// 학습역량 지수 차트
	const learningCtx = document.getElementById('learningCompetencyChart').getContext('2d');
	learningChart = new Chart(learningCtx, {
		type: 'bar',
		data: {
			labels: ['협력적 소통역량', '융합적 탐구역량', '창의적 혁신역량', '글로벌 리더역량'],
			datasets: [{
				label: '핵심역량 지수',
				data: [15000, 24000, 0, 0],
				backgroundColor: [
					'#40E0D0',
					'#32CD32', 
					'#D3D3D3',
					'#D3D3D3'
				],
				borderWidth: 0,
				barThickness: 45
			}]
		},
		options: {
			responsive: true,
			maintainAspectRatio: false,
			resizeDelay: 0,
			plugins: {
				legend: {
					display: false
				}
			},
			scales: {
				y: {
					beginAtZero: true,
					max: 40000,
					ticks: {
						callback: function(value) {
							return value >= 1000 ? (value/1000) + 'k' : value;
						}
					}
				},
				x: {
					ticks: {
						maxRotation: 0,
						font: {
							size: 9
						}
					}
				}
			}
		}
	});

	// 나의 역량 지수 차트
	const myCtx = document.getElementById('myCompetencyChart').getContext('2d');
	myChart = new Chart(myCtx, {
		type: 'bar',
		data: {
			labels: ['협력적 소통역량', '융합적 탐구역량', '창의적 혁신역량', '글로벌 리더역량'],
			datasets: [
				{
					label: '현재 역량',
					data: [8400, 1680, 16800, 23520],
					backgroundColor: '#87CEEB',
					borderWidth: 0,
					barThickness: 45
				},
				{
					label: '향상 예상',
					data: [6600, 0, 0, 0],
					backgroundColor: '#D3D3D3',
					borderWidth: 0,
					barThickness: 45
				}
			]
		},
		options: {
			responsive: true,
			maintainAspectRatio: false,
			resizeDelay: 0,
			plugins: {
				legend: {
					display: false
				}
			},
			scales: {
				x: {
					stacked: true,
					ticks: {
						maxRotation: 0,
						font: {
							size: 9
						}
					}
				},
				y: {
					stacked: true,
					beginAtZero: true,
					max: 30000,
					ticks: {
						callback: function(value) {
							return value >= 1000 ? (value/1000) + 'k' : value;
						}
					}
				}
			}
		}
	});
});

// 윈도우 리사이즈 이벤트 처리
window.addEventListener('resize', function() {
	// 차트 리사이즈 처리
	setTimeout(() => {
		if (learningChart) {
			learningChart.resize();
		}
		if (myChart) {
			myChart.resize();
		}
	}, 100);
});