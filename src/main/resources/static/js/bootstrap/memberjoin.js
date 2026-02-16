//아이디 중복체크 => jpa로 로드받은 값을 핸들링 - get통신
function ajax_idck(uid){
	if(uid==""){
		alert("아이디를 입력하세요");
		frm.uid.focus();
	}else{
		var http,result;
		http = new XMLHttpRequest();
		http.onreadystatechange = function(){
			if(http.readyState==4 && http.status==200){
				result = this.response;
				if(result=="true"){
					alert("해당 아이디는 사용할 수 없습니다.");
					frm.uid.value="";
					frm.uid.focus();	
				}else{
					alert("사용가능한 아이디입니다.");
					frm.uid.readOnly=true;
					
					//중복체크 여부를 확인하기 위한 hidden value 핸들링
					document.getElementById("checklist").value="ok";
				}
			}
		}
		http.open("GET","./idcheck?uid="+uid,true);
		http.send(); //post통신이면 여기에 value 전송
	}
}

function member_joinok(){
	var ck = document.getElementById("checklist").value;
	if(ck != "ok"){
		alert("아이디 중복확인을 해주세요");
	}else if(ck=="ok"){
		if(frm.utel.value.length < 11 || frm.utel.value.lenth > 11){
			alert("올바른 연락처를 입력하세요");
		}else if(frm.upost.value==""){
			alert("주소를 입력하세요");
		}else{
			frm.method="post";
			frm.action="./member_join";
			frm.enctype="multipart/form-data"; //이미지 보낼땐 필수
			frm.submit();
		}
	}
}

function member_modifyok(){
	frm.method="post";
	frm.action="./member_modifyok";
	frm.enctype="multipart/form-data"; //이미지 보낼땐 필수
	frm.submit();
}