<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <meta charset="utf-8">
    <title>예약완료 페이지</title>
  </head>
  <body>
    <p>일련번호 : ${object.idx}</p>
    <p>타이틀 : ${object.title}</p>
    <p>구입일자 : ${object.buyDate}</p>
    <p>코드 : ${object.cCode}</p>

    <div>
    예약내용 : ${object.cDesc}
    </div>

    <div>
    <p>구입자 : ${object.cName}</p>
    <p>연락처 : ${object.phone}</p>
    <p>이메일 : ${object.email}</p>
    <p>등록일 : ${object.regDate}</p>
    <p>인수반납 : ${object.cDesc2}</p>
    </div>    
  </body>
</html>
