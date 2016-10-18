var types = ["COMPILE", "PROVIDED", "RUNTIME", "TEST", "SYSTEM", "IMPORT"];
var s = new sigma();

/*
s.settings({
        labelAlignment: 'center',
        defaultLabelSize: 8,
        labelSizeRatio: 0.5,
        edgeColor: 'default',
        defaultEdgeColor: '#ff0000',
        minNodeSize:8,
        maxNodeSize:15,
        maxNodeLabelLineLength:12,
        labelThreshold: 15
    });
 */   
s.settings({
        labelAlignment: 'right',
        defaultLabelSize: 10,
        //labelSizeRatio: 0.5,
        edgeColor: 'default',
        defaultEdgeColor: '#ff0000',
        minNodeSize:6,
        maxNodeSize:6,
        maxNodeLabelLineLength:100,
        labelThreshold: 1
    });
    
s.refresh();

var depmngGrid;

// UI init
$(function() {

    //hide cypher boxes
    $('#toggle-cypher_all').hide();
    $('#toggle-cypher_search').hide();

    //Init auto-arragne node
    var frListener = sigma.layouts.fruchtermanReingold.configure(s, {
        iterations: 500,
        easing: 'quadraticInOut',
        duration: 800
        });
    //frListener.bind('start stop interpolate', function(e) {
    //  console.log(e.type);
    //});
    
    var dagreListener = sigma.layouts.dagre.configure(s, {
      directed: true, // take edge direction into account
      multigraph: true,
      rankdir: 'RL',  // Direction for rank nodes. Can be TB, BT, LR, or RL,
      compound: true,           // where T = top, B = bottom, L = left, and R = right.
      easing: 'quadraticInOut', // animation transition function
      duration: 1000,   // animation duration
      // nodes : s.graph.nodes().slice(0,30), // subset of nodes
      boundingBox: {minX: 10, maxX: 90, minY: 10, maxY:90} // constrain layout bounds ; object or true (all current positions of the given nodes)
    });
    // Bind the events:
    //dagreListener.bind('start stop interpolate', function(e) {
    //  console.log(e.type);
    //});

    //Auto-aggange button setup
    $("#autoarrangebutton").click(function() {
    	sigma.layouts.fruchtermanReingold.start(s);
    });
    $("#autoarrangebutton2").click(function() {
        sigma.layouts.dagre.start(s);
    });

    depmngGrid = $("#depmng_grid").jqGrid({
            datatype: "jsonstring",
            datastr: new Array(),
            colModel:[
                { index: 'Id', name: 'Id', label: 'Id', width:1, hidden:true,key: true },
                { index: 'Name', name: 'Name', label: 'Name', width: 300,sorttype:"string" },
                { index: 'Num', name: 'Num', label: 'Num', width: 30 , align: 'center'},
                { index: 'Version', name: 'Version', label: 'Version', width: 50 , align: 'right'},
                { index: 'Scope', name: 'Scope', label: 'Scope', width: 50 , align: 'center' },
                { index: 'UsedBy', name: 'UsedBy', label: 'UsedBy', width: 300 },
                {name:'enbl', index:'enbl', width: 30, align:'center',
                 formatter:'checkbox', editoptions:{value:'1:0'},
                 formatoptions:{disabled:false}},
                { name:"level", hidden:true }
            ],
            width:"770",
            //hoverrows:false,
            viewrecords:false,
            gridview:true,
            height:480,//"auto",
            sortname:"Name",
            sortorder:"asc",
            loadonce:true,
            rowNum:1000,
            scrollrows:true,
            // enable tree grid
            treeGrid:true,
            // which column is expandable
            ExpandColumn:"Name",
            // datatype
            treedatatype:"json",
            // the model used
            treeGridModel:"nested",
            // configuration of the data comming from server
            treeReader : {
                level_field: "level",
                left_field:"lft",
                right_field: "rgt",
                leaf_field: "isLeaf",
                expanded_field: "expanded",
            },
            pager:"#depmng_grid_pager"
        });
     
    $("#depmng_modal").dialog({'modal':true,'minWidth':850, 'minHeight':600,'draggable':false, 'autoOpen': false,"buttons":[] });
    $("#tabs").tabs();
    //Dependency Management button setup
    $("#depmngbut").click(function() {
        $("#depmng_modal").dialog( "open" );
        dependencyManagement();
        
    });

    $("#crawler_modal").dialog({modal: true,'minWidth':600, 'minHeight':100,'maxHeight':400,'autoOpen': false,buttons: { Ok: function() {refreshGraph();$( this ).dialog( "close" ); } }  }); ;
    $("#crawler_modal_err").hide();

    dialogLoading = $("#loadingScreen").dialog({
        autoOpen: false,    // set this to false so we can manually open it
        dialogClass: "loadingScreenWindow",
        closeOnEscape: false,
        draggable: false,
        width: 300,
        minHeight: 60, 
        modal: true,
        buttons: {},
        resizable: false,
        open: function() {
            // scrollbar fix for IE
            $('body').css('overflow','hidden');
        },
        close: function() {
            // reset overflow
            $('body').css('overflow','auto');
        }
    });

    dialogUpload = $( "#dialog-upload" ).dialog({
      autoOpen: false,
      closeOnEscape: true,
      height: 200,
      width: 300,
      modal: true,
      resizable: false,
      buttons: {
        "Upload": function() { 
            $("#dataupload").submit();
         },
        Cancel: function() {
          $( this ).dialog( "close" );
        }
      },
    });

    $("#dataupload").submit(function(event){
          //disable the default form submission
          event.preventDefault();
         
          //grab all form data  
          var formData = new FormData($(this)[0]);
            
          $.ajax({
            url: '/node/uploadmp',
            type: 'POST',
            data: formData,
            async: false,
            cache: false,
            contentType: false,
            processData: false,
            beforeSend: function(){ dialogUpload.dialog( "close" ); waitingDialog({}); },
            error: function (jqXHR, textStatus, errorThrown) {alert("Error invoking upload:"+errorThrown);},
            success: function(){  refreshGraph(); },
            complete: function(){ closeWaitingDialog(); }
          });
         
          return false;
        });

    dialogTruncate = $( "#dialog-confirm" ).dialog({
      autoOpen: false,
      resizable: false,
      height: "auto",
      width: 400,
      modal: true,
      buttons: {
        "Delete all items": function() {
            $.ajax(pfurl + "/node/truncate",{
              type: "POST",
              beforeSend: function(){ waitingDialog({}); },
              error: function (jqXHR, textStatus, errorThrown) {alert("Error invoking truncate:"+errorThrown);},
              success: function(){ $( "#dialog-confirm" ).dialog( "close" );refreshGraph(); },
              complete: function(){ closeWaitingDialog(); }
            });     
        },
        Cancel: function() {
          $( this ).dialog( "close" );
        }
      }
    });

    // search dept spinner
    $( "#searchDepth" ).spinner({
      spin: function( event, ui ) {
        if ( ui.value > 10 ) {
          $( this ).spinner( "value", 10 );
          return false;
        } else if ( ui.value < 1 ) {
          $( this ).spinner( "value", 1 );
          return false;
        }
      }
    });

    //Accordion Init
    $("#accordion").accordion({
        heightStyle: "fill"
    });

});

s.addCamera('cam1'),
    s.addRenderer({
        container: document.getElementById('graph'),
        type: 'canvas',
        camera: 'cam1'
    });

function waitingDialog(waiting) { 
        dialogLoading.html(waiting.message && '' != waiting.message ? waiting.message : 'Please wait...');
        dialogLoading.dialog('option', 'title', waiting.title && '' != waiting.title ? waiting.title : 'Invoking Pathfinder Maven Crawler.');
        dialogLoading.dialog('open');
    }
function closeWaitingDialog() {
    dialogLoading.dialog('close');
}



var sc = function successCrawl(data){
    console.log(data.exception);
    console.log(data.response);
    console.log(data.return);

    if(data.return==0){
        $("#crawler_modal_status").html("<span style='color:green;'>Success</span>");
        $("#crawler_modal_err").hide();
    }
    else{
        $("#crawler_modal_status").html("<span style='color:red;'>Error</span>");
        $("#crawler_modal_elog").html(data.exception);
        if(data.exception.length>0){
            $("#crawler_modal_err").show();
        }
    }

    $("#crawler_modal_log").html(data.response);

    $("#crawler_modal").dialog( "open" );
}

function crawlNode(e){
    crawl(e.data.node.id);
}

function checkCrawlInput(val,error){
    if( $(val).val()==""){
        alert("Crawl field "+error+" cannot be empty");
        return false;
    }
    return true
}

function crawlForm(){
    var e = $("#crawlG").val();
    e += ":" + $("#crawlA").val();
    e += ":" + $("#crawlP").val();
    e += ":" + $("#crawlC").val();
    e += ":" + $("#crawlV").val();
    if( 
        checkCrawlInput("#crawlG","GroupId") &&
        checkCrawlInput("#crawlA","ArtifactId") &&
        checkCrawlInput("#crawlP","Packaging") &&
        checkCrawlInput("#crawlV","Version")
     ){
        crawl(e);
    }
}

function crawl(e) {
    console.log(e);
    $.ajax(pfurl + "/" + crawlerpath,{
      type: "POST",
      data: e,
      beforeSend: function(){ waitingDialog({}); },
      error: function (jqXHR, textStatus, errorThrown) {alert("Error invoking crawler:"+errorThrown);},
      success: sc,
      complete: function(){ closeWaitingDialog(); },
      dataType: "json"
    });


}

function fillCrawlForm(e){
    var val = e.data.node.id.split(":")
    var i = 0;
    $("#crawlG").val(val[i++]);
    $("#crawlA").val(val[i++]);
    $("#crawlP").val(val[i++]);
    $("#crawlC").val(val[i++]);
    $("#crawlV").val(val[i++]);
}

s.bind('doubleClickNode',crawlNode);
s.bind('clickNode',fillCrawlForm);






// Instanciate the ActiveState plugin:
var activeState = sigma.plugins.activeState(s);
var keyboard = sigma.plugins.keyboard(s, s.renderers[0]);

// Initialize the Select plugin:
var select = sigma.plugins.select(s, activeState);
select.bindKeyboard(keyboard);

// Initialize the dragNodes plugin:
var dragListener = sigma.plugins.dragNodes(s, s.renderers[0], activeState);

// Initialize the lasso plugin:
var lasso = new sigma.plugins.lasso(s, s.renderers[0], {
  'strokeStyle': 'rgb(236, 81, 72)',
  'lineWidth': 2,
  'fillWhileDrawing': true,
  'fillStyle': 'rgba(236, 81, 72, 0.2)',
  'cursor': 'crosshair'
});
select.bindLasso(lasso);
//lasso.activate();

//"spacebar" + "s" keys pressed binding for the lasso tool
keyboard.bind('32+83', function() {
  if (lasso.isActive) {
    lasso.deactivate();
  } else {
    lasso.activate();
  }
});

// Listen for selectedNodes event
lasso.bind('selectedNodes', function (event) {
  setTimeout(function() {
    lasso.deactivate();
    s.refresh({ skipIdexation: true });
  }, 0);
});



//populate with full data first
refreshGraph()

//Change node color depending on version
function getColor(v) {
    out = '#00BDFC';

    if (v.search(/SNAPSHOT/i) != -1) {
        out = '#FF4E03';
    } else if (v.replace(/[0-9]*(\.[0-9]*)*/g, '').length > 0) {
        out = '#FFD103';
    }
    return out;
}
//Change edge color depending on type

function getEdgeColor(type) {
    out = '#000000';
    if (type == "provided"	) {
        out = '#ffc107'; //yellow
    } else if (type == "runtime") {
        out = '#f44336'; //red
    } else if (type == "test") {
        out = '#2196f3'; //blue
    } else if (type == "system") {
        out = '#607d8b'; //gray
    } else if (type == "import") {
        out = '#9c27b0'; //purple
    } else if (type == "parent") {
        out = '#009688'; //green
    }
    return out;
}



function updateLabel(txt){
    $("#graph-label").html(txt);
}

function dependencyManagement(){
    var e = s.graph.edges();
    //console.log(e.length);

    // { src:"ids" ,list:[version:v , usedby:"idd"] }
    var deps = new Array();
    var gridData = new Array();

    for (var i = e.length - 1; i >= 0; i--) {
       
        var srcs     = e[i].source.split(":");
        var dst     = e[i].target;
        var type    = e[i].label;

        var srcId = srcs[0] + ":" + srcs[1] + ":" + srcs[2] + ":" + srcs[3];
        if( deps[srcId] == undefined ){
            deps[srcId] = { "list" : [ {"ver":srcs[4] , "by":dst, "type":type} ] };
        }
        else{
            var list = deps[srcId].list;
            list[ list.length ] = {"ver":srcs[4] , "by":dst, "type":type} ;
            deps[srcId].list = list;
        }
        //gridData[i] = {'id':srcs[0] + ":" +srcs[1] + ":" + srcs[2] + ":" + srcs[3], 'Version':srcs[4], 'UsedBy':dst, 'Scope':type };
    }
    
    //$('#depmng_modal').tabbedDialog();
    //console.log(deps);
    gridData.push( { Id:"0", Name:"Dependencies", Num:e.length, Version:"", Scope:"",UsedBy:"",enbl:"0",hidden:true, isLeaf:false, loaded:true , expanded:true, parent:"",level:0,lft:1,rgt:(2*e.length)} );
    var id = 1;    
    for (var d in deps) {
        var list = deps[d].list;
        var lft_parent = (++id);
        var rgt_parent = lft_parent + 2*list.length +1; 
        gridData.push( { Id:id, Name:d, Num:list.length, Version:"", Scope:"",UsedBy:"",enbl:"0", isLeaf:false, loaded:true , expanded:(list.length>1), parent:"0",level:"1",lft:lft_parent,rgt:rgt_parent} );

        var red="";
        for (var j = 0; j<list.length; j++) {
            dd = deps[d].list[j];
            id=lft_parent + (j+1)
            var lft_leaf = id;
            var rgt_leaf = lft_leaf + 1;
            if(list.length > j+1){
                if(deps[d].list[j].ver != deps[d].list[j+1].ver){
                    red = "style='color:red;'"
                }
            }

            gridData.push( {Id:lft_leaf, Name:"<span " + red +">"+d+"</span>", Num:"", Version:dd.ver, Scope:dd.type, UsedBy:dd.by ,enbl:(j==0?"1":"0"),isLeaf:true, loaded:true, expanded:false, parent:id,level:"2",lft:lft_leaf,rgt:rgt_leaf} );
            id++;
            //console.log(d,red);
        }
        id++;
    };

    if( depmngGrid.get(0).p.treeGrid ) {
        depmngGrid.get(0).addJSONData({
            total: 1,
            page: 1,
            records: gridData.length,
            rows: gridData
        });
    }
    else {
        depmngGrid.jqGrid('setGridParam', {
            datatype: 'local',
            data: gridData,
            rowNum: gridData.length
        });
    }

}

function refreshDepMngMaven(){
    var rows = depmngGrid.jqGrid('getRowData');

    var dpmngxml = "&lt;dependencyManagement&gt;\n";
    dpmngxml += "\t&lt;dependencies&gt;\n"
    for(r in rows){
        console.log(rows[r]);
        if(rows[r].enbl=="1"){
        ddd = rows[r].Name.split(":");
                dpmngxml += "\t\t&lt;dependency&gt;\n"
                dpmngxml += "\t\t\t&lt;groupId&gt;"+ddd[0]+"&lt;/groupId&gt;\n"
                dpmngxml += "\t\t\t&lt;artifactId&gt;"+ddd[1]+"&lt;/artifactId&gt;\n"
                dpmngxml += "\t\t\t&lt;version&gt;"+rows[r].Version+"&lt;/version&gt;\n"
                if(ddd[2]!="jar"){
                    dpmngxml += "\t\t\t&lt;type&gt;"+ddd[2]+"&lt;/type&gt;\n"
                }
                if(ddd[3]!= undefined && ddd[3]!=""){
                    dpmngxml += "\t\t\t&lt;classifier&gt;"+ddd[3]+"&lt;/classifier&gt;\n"
                }
                dpmngxml += "\t\t&lt;/dependency&gt;\n"
        }
    }
    dpmngxml += "\t&lt;/dependencies&gt;\n"
     dpmngxml += "&lt;/dependencyManagement&gt;"
     $("#depmng_conf_xml").html(dpmngxml);

}

function jsonOpen(){
   dialogUpload.dialog("open");
}

function jsonDown(){
    document.location = pfurl + "/node/download";
}

function jsonTruncate(){
    dialogTruncate.dialog("open");
}

