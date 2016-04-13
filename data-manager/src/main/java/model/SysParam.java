package model;
public class SysParam implements java.io.Serializable{
private static final long serialVersionUID = 2012544467454708230L;
private Integer sys_param_id;
private String kind;
private String elem;
private String param;
public Integer getSysParamId(){
return sys_param_id;
}
public void setSysParamId(Integer sys_param_id){
this.sys_param_id=sys_param_id;
}
public String getKind(){
return kind;
}
public void setKind(String kind){
this.kind=kind;
}
public String getElem(){
return elem;
}
public void setElem(String elem){
this.elem=elem;
}
public String getParam(){
return param;
}
public void setParam(String param){
this.param=param;
}
}