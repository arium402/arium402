const modifyBanner = function(idx){
	location.href = `/admin/bannerModify?idx=${idx}`;
}

const deleteBanner = function(idx) {
	if(confirm("정말 삭제하시겠습니까?")){
		fetch("/admin/bannerDelete", {
		    method: "POST",
		    headers: {
		        "Content-Type": "application/json"
		    },
		    body: JSON.stringify({ bnr_no: idx })
		})
		.then(response => {
			console.log(response);
			if (response.ok) {
				alert("삭제 완료되었습니다.");
				location.href = "/admin/banner-list";
			} else if (response.status == 604) {
				alert("서버 오류로 인해 게시글 삭제에 실패하였습니다.");
				return false;
			}
		})
		.catch(error => {
		    console.error('Error:', error);
		});	
	}
}

document.addEventListener("DOMContentLoaded", function () {
	const rows = document.querySelectorAll("#counselorTable tbody tr");

	const sidebarModal = document.getElementById("sidebarModal");
	const modalContent = document.getElementById("modalContent");
	const closeSidebar = document.getElementById("closeSidebar");
	
	rows.forEach(row => {
	    const cells = row.querySelectorAll("td");

	    // 두 번째 <td> 요소에 클릭 이벤트를 추가
	    if (cells.length > 1) {
	        cells[1].addEventListener("click", function () {
	            rows.forEach(r => r.classList.remove("selected")); // 수정: r.parentElement에서 r로 변경
	            row.classList.add("selected"); // 수정: row.parentElement에서 row로 변경
	            
				
				const bnrNo = row.dataset.bnr;

				fetch("/admin/selectBannerInfo", {
				    method: "POST",
				    headers: {
				        "Content-Type": "application/json"
				    },
				    body: JSON.stringify({ bnr_no: bnrNo })
				})
				.then(response => response.json())
				.then(data => {
					const image = "http://172.30.1.16:20080" + data.file_path;
					console.log(data.file_contents)
					const details = `
		                <h3>배너 내용</h3>
		                <label>배너 번호</label>
		                <input type="text" readonly value="${cells[0].textContent}" readonly>
		                <label>제목</label>
		                <input type="text" readonly value="${cells[1].textContent}" readonly>
		                <label>게시순서</label>
		                <input type="text" readonly value="${cells[2].textContent}">
		                <label>게시 여부</label>
		                <input type="text" readonly value="${cells[3].textContent}">
		                <label>등록일</label>
		                <input type="text" readonly value="${cells[4].textContent}">
		                <label>내용</label>
		                <textarea readonly>${data.file_contents ? data.file_contents : '내용 없음'} </textarea>
		                
		                <label>이미지</label>
		                <div>
						<img src="${image}" style="max-width:100%;">
						</div>
		                <button onclick="modifyBanner(${bnrNo})">수정</button>
		            `;
					
					closeSidebar.style.display = "flex";
		            modalContent.innerHTML = details;
		            sidebarModal.classList.add("open");
					
				})
				.catch(error => {
				    console.error('Error:', error);
				});
	        });
	    }
	});

	closeSidebar.addEventListener("click", function () {
	    closeSidebar.style.display = "none";
	    sidebarModal.classList.remove("open");
	});
});