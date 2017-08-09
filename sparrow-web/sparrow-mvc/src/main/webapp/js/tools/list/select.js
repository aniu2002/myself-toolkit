function PopLayer(){
  this.bindSource=null;
  this.dataProvider=null;
  this.mainLayer=null;
  this.container=null;
  this.tableCon=null;
  this.lastClickRow=null;
  this.defaultClassName="defaultRowClass";
  this.clickClassName="clickStyle";
  this.overClassName="overStyle";
  this.outClassName="defaultRowClass";
  this.cellClassName="cellStyle";
  this.mainLayerWidth=0;
  this.singal=0;
}

PopLayer.mainLayerClassName="mainLayer";
PopLayer.mainTableClassName="";
PopLayer.containerClassName="";
/*************
   this.dataProvider=[{title:'df'}]  必须字段
  
   setDataProvider();
   bind();
   create();
    
***************/
PopLayer.calculateOffset=function(field,attr)
{
      var offset=0;
      
	  while(field)
	  {
           offset+=field[attr];
	       field=field.offsetParent;
      }
	 
    return offset;
}

PopLayer.createFunction=function(obj,strFunc,args)
{
       if(!obj) obj=window;
       
	   return function() {
            obj[strFunc].apply(obj,args);
        }
}

PopLayer.prototype={
	init:function(){
		 var body1 = document.getElementsByTagName("body")[0];
		  this.mainLayer=document.createElement("DIV");
		  this.mainLayer.className=PopLayer.mainLayerClassName;
		  
		  this.tableCon=document.createElement("TABLE");
		  this.container=document.createElement("TBODY");
		  //this.tableCon.className=PopLayer.mainTableClassName;
		  this.container.className=PopLayer.containerClassName;
		 
		  this.tableCon.border=0;
		  this.tableCon.cellPadding=0;
		  this.tableCon.cellSpacing=0;
		  this.tableCon.align="center";
		 // this.tableCon.width="100%";
		 // this.initData(this.container);	  
	      this.tableCon.appendChild(this.container);
		  this.mainLayer.appendChild(this.tableCon);
		  this.mainLayer.onmouseover=PopLayer.createFunction(this,'setSingal',[1]);
	      this.mainLayer.onmouseout=PopLayer.createFunction(this,'setSingal',[0]);
		 // this.mainLayer.className="mainLayer";		
		  body1.appendChild(this.mainLayer);  
		  
	},setSingal:function(si){
		this.singal=si;
	},setDataProvider:function(data){
	      this.dataProvider=data;
	},bind:function(sourceid){
		if(typeof(sourceid)=="string")
		{
			this.bindSource=document.getElementById(sourceid);
		}else if(typeof(sourceid)=="object")
		  this.bindSource=sourceid;
		else
		{
		  alert("Bind source error!");
		  return;
		}
		//this.initMainLayerOffset(this.bindSource);
		
	},initMainLayerOffset:function(srcElement){
		
	  var end = srcElement.offsetHeight;
	  var width=srcElement.offsetWidth;
      var left=PopLayer.calculateOffset(srcElement,"offsetLeft");
  	  var top=PopLayer.calculateOffset(srcElement,"offsetTop");

		
	  this.bindSource.onclick=PopLayer.createFunction(this,'showLayer',[]);
	  this.bindSource.onkeypress=PopLayer.createFunction(this,'hideLayer',[]);
	  this.bindSource.onblur=PopLayer.createFunction(this,'hideLayer',[]);
      //this.menu_div.style.border="black 1px solid";
	  if(this.mainLayer==null){
	      this.init();
      }
	  
	  this.mainLayer.style.display="none";
      this.mainLayer.style.left=left+1+"px";
      this.mainLayer.style.top=top+end+"px";
	  this.mainLayer.style.width=width-2+"px";
	  this.mainLayerWidth=width-2;
	  this.tableCon.width= this.mainLayerWidth-17;
	},initData:function(container){
		if(this.dataProvider.length>0)
		{
			for(var i=0;i<this.dataProvider.length;i++)
			{
			    var row=this.createRow(this.dataProvider[i]);	
				container.appendChild(row);
			}
		}else
		{
			return;
		}
	},createRow:function(rowObjPara){
		var row=document.createElement("TR");
		var cell=this.createCell(rowObjPara.title);
		
		row.onmouseover=PopLayer.createFunction(this,'onmouseover',[row]);
		row.onmouseout=PopLayer.createFunction(this,'onmouseout',[row]);
		row.onclick=PopLayer.createFunction(this,'onclick',[row,rowObjPara.title]);
		//row.oncdblick=PopLayer.createFunction(this,'ondbclick',[row]);
		
		row.appendChild(cell);
		row.height=17;
		row.className=this.defaultClassName;
		return row;		
	},createCell:function(txt){
		var cell=document.createElement("TD");
		var span=document.createElement("SPAN");
		var nodeText=document.createTextNode(txt);
		span.appendChild(nodeText);
		cell.className=this.cellClassName;
		cell.appendChild(span);
		return cell;
	},create:function(){  // for people apply
		this.init(); // 初始化各个控件 div table tbody
		this.initData(this.container); //在容器中装载数据 tr td textnode
		this.initMainLayerOffset(this.bindSource); //根据bindsource设置div显示位置
		
	},onmouseover:function(row){
		row.className= this.overClassName;
	   //if(this.lastOverRow!=null)
		 //  this.lastOverRow=this.defaultClassName; 
	},onmouseout:function(row){
	    row.className=this.outClassName;
	},onclick:function(row,value){
		this.lastClickRow=row;
		this.singal=0;
		this.bindSource.value=value;
		this.hideLayer();
	},ondbclick:function(row){
	},showLayer:function(){
		if(this.mainLayer!=null)
		{
		  this.mainLayer.style.display="block";
		}
	},hideLayer:function(){
		if(this.singal==1) return;
	    if(this.mainLayer!=null)
		  this.mainLayer.style.display="none";
	},toString:function(){
	    return " PopLayer - > "; 
	}	
}