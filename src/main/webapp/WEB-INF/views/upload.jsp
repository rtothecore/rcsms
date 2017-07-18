<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Upload File Request Page</title>
</head>
<link rel="stylesheet" type="text/css" href="./resources/css/common.css">
<link rel="stylesheet" type="text/css" href="./resources/css/jquery.mloading.css">

<script src="./resources/js/jquery-3.1.1.min.js" charset="utf-8"></script>
<script src="./resources/js/jquery.mloading.js"></script>
<script src="http://malsup.github.com/jquery.form.js"></script>
<script src="https://apis.google.com/js/client.js?onload=load" async defer></script>

<script charset='utf-8'>
function load() {
	 gapi.load('client', function(){
		 gapi.client.setApiKey('AIzaSyD3NFSJbOhjUGJlW1cQdCopPqcOkSnUNWA');      
		 gapi.client.load('urlshortener', 'v1',function(){
			 setTimeout("getContracts()", 250);
			 
			 // ajax file upload
			 $('form').ajaxForm({
				 success: function() {
					 getContracts();
					 $("body").mLoading('hide');
				 },
			     complete: function(xhr) {
				 }
			 });
		 });		 
	 });
}

function display(data) {
	var json = "<h4>Ajax Response</h4><pre>"
			+ JSON.stringify(data, null, 4) + "</pre>";
	$('#feedback').html(json);
}

function createShortURL(longURL, shortURL) {
	var request = gapi.client.urlshortener.url.insert({
	    'resource' : {
	        'longUrl' : longURL
	    }
	});
	request.execute(function(response) {
	    if (response.id != null) {        
	        console.log(response.id);
	        shortURL = response.id;
	    } else {
	        console.log("error: creating short url");
	    }
	});
}

function getContracts() {
	$("body").mLoading();
	
	$.ajax({
		type: 'POST',
		contentType: "application/json",
		url:'./getContracts',
		dataType: 'json',
		success: function(data){
			console.log("SUCCESS: ", data);
			display(data);
			
			$("#contractTb tr").remove();
			
			for(var i in data) {
				var tr = document.createElement("tr");
				
				var td = document.createElement("td");
				var rowNum = data[i].idx;
				td.setAttribute("id", "rowNum" + rowNum);
				td.innerHTML = rowNum;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].title;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].buyDate;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].cCount;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].cCode;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].dateCode;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].cDesc;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].cName;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].phone;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].email;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].fee;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].cDesc2;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].startDate;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].endDate;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].company;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].carName;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].feeType;
				tr.append(td);
				
				td = document.createElement("td");
				td.innerHTML = data[i].regDate;
				tr.append(td);
				
				td = document.createElement("td");
				aTag = document.createElement("a");
				var contractLink = "http://hanainfo.kr:8080/rcsms/" + data[i].cCode;
				aTag.setAttribute("href", contractLink);
				aTag.innerHTML = "예약내역link";
				td.append(aTag);
				tr.append(td);
				
				td = document.createElement("td");
				btn_link = document.createElement("button");
				btn_link.setAttribute("id", data[i].cCode);
				btn_link.innerHTML = "링크복사";
				btn_link.onclick = function() {
					var linkStr = "http://hanainfo.kr:8080/rcsms/" + this.id;
					
					var request = gapi.client.urlshortener.url.insert({
					    'resource' : {
					        'longUrl' : linkStr
					    }
					});
					request.execute(function(response) {
					    if (response.id != null) {        
					        console.log(response.id);
					        copyToClipboard(response.id);
					    } else {
					        console.log("error: creating short url");
					    }
					});
				}
				td.append(btn_link);
				tr.append(td);
				
				if(checkFrontName(i, data[i].cName, data)) {
					var tdRowSpan = checkDuplName(i, data[i].cName, data);
					
					td = document.createElement("td");
					td.setAttribute("rowspan", tdRowSpan);
					
					if('N' == data[i].sendSMS) {
						var button = document.createElement("button");
						button.setAttribute("id", data[i].cCode);
						button.setAttribute("data-rowNum", data[i].idx);
						button.innerHTML = data[i].cName + "님께 SMS 전송";
						button.onclick = function() {
							sendSMS(this.getAttribute("data-rowNum"), data);				
						};
						td.append(button);
					} else {
						td.innerHTML = "SMS 전송완료";
					}
					
					tr.append(td);	
				}
				
				$("#contractTb").append(tr);				
			}
			$("body").mLoading('hide');
		},
		error: function (e) {
			console.log("ERROR: ", e);
			display(e);
        }
	});
}

function sendSMS(dataIdx, data) {
	var ctrData = {};
	ctrData["idx"] = dataIdx;
	ctrData["cName"] = data[dataIdx - data[0].idx].cName;
	ctrData["phone"] = data[dataIdx - data[0].idx].phone;
	ctrData["cDesc"] = data[dataIdx - data[0].idx].cDesc;
	
	//console.log(ctrData);
	$.ajax({
		type: 'POST',
		contentType: "application/json",
		url:'./sendSMS',
		data: JSON.stringify(ctrData),
		dataType: 'json',
		success: function(data){
			console.log("SUCCESS: ", data);
			display(data);
			if(data) {
				alert("SMS 전송완료!");	
			} else {
				alert("SMS 전송실패!");
			}
		},
		error: function (e) {
			console.log("ERROR: ", e);
			display(e);
			alert("SMS 전송에러발생!");
        }
	});
}

function checkFrontName(dataIdx, cName, data) {
	if(0 == dataIdx) {
		return true;
	}
	
	if(data[dataIdx-1].cName == cName) {
		return false;
	}
	
	return true;
}

function checkDuplName(dataIdx, cName, data) {
	
	if(dataIdx == data.length-1) {
		return 1;
	}
	
	dataIdx++;
	duplicatedNameCount = 1;
	
	while(data[dataIdx].cName == cName) {
		duplicatedNameCount++;
		dataIdx++;
		
		if(dataIdx > data.length-1) {
			break;
		}
	}
	
	return duplicatedNameCount;
}

function copyToClipboard(text) {
    window.prompt("Copy to clipboard: Ctrl+C, Enter", text);
}

function goWithUpload() {
	$("body").mLoading({
		  text:"Uploading...",
	});
}

</script>

<body style="overflow-y: auto;">
	<form id="target" method="POST" action="uploadFile" enctype="multipart/form-data">
		File to upload: <input type="file" name="file">
		<input type="submit" value="Upload" onclick="goWithUpload()">
	</form>	
	
	<!-- 
	<input id="btn_sa" type="button" value="Select All" onclick="selectAll()">
	<input id="btn_del" type="button" value="Delete Selected" onclick="deleteSelectedPhoto()">
	 -->
	<hr>
	
	<div>
		<table id="contractTb" border=1>
		</table>
	</div>
</body>
</html>