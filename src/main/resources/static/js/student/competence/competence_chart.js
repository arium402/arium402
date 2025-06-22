// 전역 변수
let chart = null;

// 샘플 데이터 (실제로는 서버에서 받아올 데이터)
const competencyData = {
	global: {
		name: '글로컬 리더역량',
		color: 'rgba(75, 192, 192, 0.3)',
		borderColor: 'rgba(75, 192, 192, 1)',
		subcategories: ['리더십', '외국어능력', '글로컬 시민역량'],
		scores: [85, 72, 78], // 세부 능력 점수
		avgScore: 78 // 평균 점수
	},
	communication: {
		name: '협력적 소통역량',
		color: 'rgba(255, 99, 132, 0.3)',
		borderColor: 'rgba(255, 99, 132, 1)',
		subcategories: ['의사소통능력', '인성능력', '대인관계능력'],
		scores: [90, 88, 82],
		avgScore: 87
	},
	innovation: {
		name: '창의적 혁신역량',
		color: 'rgba(255, 206, 86, 0.3)',
		borderColor: 'rgba(255, 206, 86, 1)',
		subcategories: ['창의력', '자원활용능력', '자기관리능력'],
		scores: [75, 80, 85],
		avgScore: 80
	},
	inquiry: {
		name: '융합적 탐구역량',
		color: 'rgba(54, 162, 235, 0.3)',
		borderColor: 'rgba(54, 162, 235, 1)',
		subcategories: ['종합적 사고력', '지식탐구능력', '문제해결능력'],
		scores: [88, 85, 90],
		avgScore: 88
	}
};

// 고정된 차트 라벨 (12개 세부 능력)
const chartLabels = [
	'리더십', '외국어능력', '글로컬 시민역량',
	'의사소통능력', '인성능력', '대인관계능력', 
	'창의력', '자원활용능력', '자기관리능력',
	'종합적 사고력', '지식탐구능력', '문제해결능력'
];

// 차트 생성 함수
function createChart(datasets, title) {
	const ctx = document.getElementById('competencyChart');
  
	if (chart) {
		chart.destroy();
	}

	chart = new Chart(ctx, {
		type: 'radar',
		data: {
			labels: chartLabels, // 12개 세부 능력으로 고정
			datasets: datasets
		},
		options: {
			responsive: true,
			maintainAspectRatio: false,
			plugins: {
				legend: {
					display: false
				},
				tooltip: {
					backgroundColor: 'rgba(0,0,0,0.8)',
					titleColor: 'white',
					bodyColor: 'white',
					callbacks: {
						label: function(context) {
							if (context.parsed.r > 0) {
								return context.dataset.label + ': ' + context.parsed.r + '점';
							}
							return null;
						},
						filter: function(tooltipItem) {
							return tooltipItem.parsed.r > 0;
						}
					}
				}
			},
			scales: {
				r: {
					beginAtZero: true,
					max: 100,
					min: 0,
					ticks: {
						stepSize: 20,
						font: {
							size: 10
						},
						color: '#666'
					},
					grid: {
						color: 'rgba(0,0,0,0.1)'
					},
					pointLabels: {
						font: {
							size: 11,
							weight: '600'
						},
						color: '#333'
					}
				}
			},
			animation: {
				duration: 300,
				easing: 'easeOutQuart'
			}
		}
	});
}

// 각 역량별 데이터를 12개 축에 맞게 변환
function getCompetencyDataForChart(competencyKey) {
	const data = Array(12).fill(0); // 12개 축 모두 0으로 초기화
	const competency = competencyData[competencyKey];
  
	// 각 역량의 시작 인덱스
	const startIndex = {
		'global': 0,      // 0, 1, 2
		'communication': 3, // 3, 4, 5
		'innovation': 6,   // 6, 7, 8
		'inquiry': 9       // 9, 10, 11
	};

	const start = startIndex[competencyKey];
	competency.scores.forEach((score, index) => {
		data[start + index] = score;
	});

	return data;
}

// 전체 차트 표시 (4개 역량 모두 겹쳐서)
function showAllChart() {
	const datasets = [];
  
	Object.keys(competencyData).forEach(key => {
		const competency = competencyData[key];
		const chartData = getCompetencyDataForChart(key);
		
		datasets.push({
			label: competency.name,
			data: chartData,
			backgroundColor: competency.color,
			borderColor: competency.borderColor,
			borderWidth: 2,
			pointBackgroundColor: competency.borderColor,
			pointBorderColor: '#fff',
			pointBorderWidth: 2,
			pointRadius: 3,
			fill: true
		});
	});

	createChart(datasets, '전체 핵심역량 현황');
	updateLegend(datasets);
}

// 범례 업데이트
function updateLegend(datasets) {
	const legendContainer = document.getElementById('chartLegend');
	legendContainer.innerHTML = '';

	datasets.forEach(dataset => {
		const legendItem = document.createElement('div');
		legendItem.className = 'legend-item';
		
		const colorBox = document.createElement('div');
		colorBox.className = 'legend-color';
		colorBox.style.backgroundColor = dataset.borderColor;
		
		const labelText = document.createElement('span');
		labelText.textContent = dataset.label;

		legendItem.appendChild(colorBox);
		legendItem.appendChild(labelText);
		legendContainer.appendChild(legendItem);
	});
}

// 점수 요약 업데이트
function updateScoreSummary() {
	const summaryContainer = document.getElementById('scoreSummary');
	summaryContainer.innerHTML = '';
  
	// 모든 역량의 평균 점수 표시
	Object.keys(competencyData).forEach(key => {
		const data = competencyData[key];
    
		const scoreCard = document.createElement('div');
		scoreCard.className = 'score-card';
		scoreCard.innerHTML = `
			<div class="score-card-title">${data.name}</div>
			<div class="score-card-value">${data.avgScore}</div>
			<div class="score-card-total">/ 100점</div>
		`;
		summaryContainer.appendChild(scoreCard);
	});
}

// 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', function() {
	// 핵심역량 메뉴 열기
	const competencyMenu = document.querySelector('.sidebar ul li:nth-child(3)');
	if (competencyMenu) {
		competencyMenu.classList.add('active');
	}
  
	// 초기 차트 로드
	showAllChart();
	updateScoreSummary();
});