/*
onClick="javascript:openwin('pic.jsp','',320,150);"
�������ڵ��ø����ں���ʱ����ʹ��window.opener.xxx();
Ҳ����ʹ��window.opener.text1.value="xxx";�����ø�����������
ʹ�ÿ���ˢ�¸�window.opener.reFresh();ҳ��

����window.open �����´��ڵ���� 
����'page.html' �������ڵ��ļ����� 
����'newwindow' �������ڵ����֣������ļ��������Ǳ��룬���ÿ�''���棻 
����height=100 ���ڸ߶ȣ� 
����width=400 ���ڿ�ȣ� 
����top=0 ���ھ�����Ļ�Ϸ�������ֵ�� 
����left=0 ���ھ�����Ļ��������ֵ�� 
����toolbar=no �Ƿ���ʾ��������yesΪ��ʾ�� 
����menubar��scrollbars ��ʾ�˵����͹������� 
����resizable=no �Ƿ�����ı䴰�ڴ�С��yesΪ���� 
����location=no �Ƿ���ʾ��ַ����yesΪ���� 
����status=no �Ƿ���ʾ״̬���ڵ���Ϣ��ͨ�����ļ��Ѿ��򿪣���yesΪ����
�������ֵ������ڷ���
2. confirm, prompt
if(window.confirm("ȷ��ɾ���˼�¼?")){   ֻ��һ����ʾ���
if(prompt("����������","�ޱ���")==false){    ����һ��������������
 
3. showModalDialog window.showModalDialog("child.html",window,"dialogWidth:335px;status:no;dialogHeight:300px")
status:�Ƿ���ʾ״̬���ڵ���Ϣ

*/
function openWin(url,winName,width,height) {
	xposition=0; yposition=0;
	if ((parseInt(navigator.appVersion) >= 4 ))	{
		xposition = (screen.width - width) / 2;
		yposition = (screen.height - height) / 2;
	}
	theproperty= "width=" + width + ","
	+ "height=" + height + ","
	+ "location=0,"
	+ "menubar=0,"
	+ "resizable=1,"
	+ "scrollbars=0,"
	+ "status=0,"
	+ "titlebar=0,"
	+ "toolbar=0,"
	+ "hotkeys=0,"
	+ "screenx=" + xposition + "," //????????Netscape
	+ "screeny=" + yposition + "," //????????Netscape
	+ "left=" + xposition + "," //IE
	+ "top=" + yposition; //IE
	newwin=window.open('','',theproperty );
	url=url;//????  
	newwin.location.href =url;
}

//���й������ĵ�������
function openWinBar(url,winName,width,height){
	xposition=0; yposition=0;
	if ((parseInt(navigator.appVersion) >= 4 ))	{
		xposition = (screen.width - width) / 2;
		yposition = (screen.height - height) / 2;
	}
	theproperty= "width=" + width + ","
	+ "height=" + height + ","
	+ "location=0,"
	+ "menubar=0,"
	+ "resizable=1,"
	+ "scrollbars=1,"
	+ "status=0,"
	+ "titlebar=0,"
	+ "toolbar=0,"
	+ "hotkeys=0,"
	+ "screenx=" + xposition + "," //????????Netscape
	+ "screeny=" + yposition + "," //????????Netscape
	+ "left=" + xposition + "," //IE
	+ "top=" + yposition; //IE
	newwin=window.open('','',theproperty );
	url=url;//????  
	newwin.location.href =url;
}