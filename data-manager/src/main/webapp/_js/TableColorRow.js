﻿//使用时必须将<script language="JavaScript" src="TableColorRow.js"><-/script>放在页面尾部
//table上引用样式 id="TableColorRow" border="1" bordercolor="#daebff" style="border-collapse:collapse"
//为了防止javascript变量名冲突，公用组件尽量使用特殊定义
var TableColorRow = new Object;
TableColorRow.js_tr_align="center";
TableColorRow._color_line_over="#ecfbd4";      /* 鼠标经过时的背景色 */
TableColorRow._color_line_odd="#FFFFFF";       /* 第一行的背景色 */ 
TableColorRow._color_line_even="#E2EBED";      /* 第二行的背景色 */
TableColorRow._color_line_click="#bce774";     /* 鼠标选择时的背景色 */ 
TableColorRow._hasElements="";
TableColorRow._css_table=new Object;
TableColorRow._css_tr_arr=new Object;

TableColorRow.init = function(){
	var hasTableColor=false;
    var _css_tableArr=document.getElementsByTagName("table");
	for(var z=0;z<_css_tableArr.length;z++){
	    if(_css_tableArr[z].id.indexOf("TableColorRow")==-1){
	    	continue;
	    }
	    hasTableColor=true;
        TableColorRow._hasElements="hasElements";
		this._css_table=_css_tableArr[z];
		this._css_tr_arr=this._css_table.getElementsByTagName("tr");
		this._css_table.style.cursor="pointer";
		this._css_table.border="1";
		this._css_table.borderColor="#BBDDE5";
		this._css_table.style.borderCollapse="collapse";
		for (i=0;i<this._css_tr_arr.length;i++) {  
			this._css_tr_arr[i].style.backgroundColor = (i%2==1)?TableColorRow._color_line_odd:TableColorRow._color_line_even;  
			this._css_tr_arr[i].style.height="25px";
			this._css_tr_arr[i].align=TableColorRow.js_tr_align;
		} 
		var _select_tr=null;  
		var _tamp_color=null;
		for(var i=1;i<this._css_tr_arr.length;i++) { 
			this._css_tr_arr[i].onmouseover=function(){
				_tamp_color=this.style.backgroundColor;
				this.style.backgroundColor =TableColorRow._color_line_over;
				//如果已经存在选定的行,则每次把选定行设置背景色
				if(_select_tr!=null){
					_select_tr.style.backgroundColor =TableColorRow._color_line_click; 
				}
			}
			this._css_tr_arr[i].onmouseout=function(){
				this.style.backgroundColor=_tamp_color;
			}
			this._css_tr_arr[i].onclick=function(){
				_tamp_color=this.style.backgroundColor; 
				_tr_color=this.style.backgroundColor; 
				//如果已经存在选定的行,则上次把选定行背景色设为空
				if(_select_tr!=null){
					//先得到当前事件时选择的行号，然后找出它应该被填充的颜色
					var _temp_index=_select_tr.sectionRowIndex;
					var _temp_color=(_temp_index%2>0)?TableColorRow._color_line_odd:TableColorRow._color_line_even;
					_select_tr.style.backgroundColor = _temp_color; 
				}          
				_select_tr=this;
				this.style.backgroundColor=TableColorRow._color_line_click; 
			} 
		}
	}
	if(!hasTableColor){
		  alert("can't find TableColorRow!");
	}
};
//执行window.load。如果是IE则执行attachEvent，Mozilla/Firefox则执行addEventListener
if (document.all){
	window.attachEvent('onload',TableColorRow.init)
}
else{
	window.addEventListener('load',TableColorRow.init,false);
}