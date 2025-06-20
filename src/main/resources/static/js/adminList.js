document.addEventListener("DOMContentLoaded", function () {
    const rows = document.querySelectorAll("#counselorTable tbody tr");
    const sidebarModal = document.getElementById("sidebarModal");
    const modalContent = document.getElementById("modalContent");

    rows.forEach(row => {
        row.addEventListener("click", function () {
			
			rows.forEach(r => r.classList.remove("selected"));
            row.classList.add("selected");
            const cells = row.querySelectorAll("td"); 
            const details = `
            	<img src="/images/face.jpg" style="width:250px; height:300px">
                
                <button>관리자 정보 수정</button>
            `;
			closeSidebar.style.display="flex";
            modalContent.innerHTML = details;
            sidebarModal.classList.add("open");
        });
    });

});
