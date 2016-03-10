var types = ["COMPILE", "PROVIDED", "RUNTIME", "TEST", "SYSTEM", "IMPORT"];

var s = new sigma();
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

    //Auto-aggange button setup
    $("#autoarrangebutton").click(function() {
        sigma.layouts.fruchtermanReingold.start(s);
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

    

 /*   $.fn.tabbedDialog = function () {
        this.tabs();
        this;
        this.find('.ui-tab-dialog-close').append($('a.ui-dialog-titlebar-close'));
        this.find('.ui-tab-dialog-close').css({'position':'absolute','right':'0', 'top':'23px'});
        this.find('.ui-tab-dialog-close > a').css({'float':'none','padding':'0'});
        var tabul = this.find('ul:first');
        this.parent().addClass('ui-tabs').prepend(tabul).draggable('option','handle',tabul); 
        this.siblings('.ui-dialog-titlebar').remove();
        tabul.addClass('ui-dialog-titlebar');
        
    }
*/   
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

function refreshGraph(){
    s.refresh();
}

function crawl(e) {
    console.log(e.data.node.id);

    $.ajax({
      type: "POST",
      url: pfurl + "/" + crawlerpath,
      data: e.data.node.id,
      success: refreshGraph,
      dataType: "string"
    });


}

s.bind('doubleClickNode',crawl);

//populate with full data first
doCypherAll();

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

function doCypher(query) {

    sigma.neo4j.send({
            url: n4jurl,
            user: n4juser,
            password: n4jpassword
        },
        n4jendopint,
        "POST",
        '{"query" : ' + JSON.stringify(query) + '}',
        function(res) {
            s.graph.clear();
            //console.log(res);
            
            $.each(res.data, function(i, node) {

                for (k = 0; k < node.length; k++) {
                    n = node[k].root.data;
                    //console.log(n);

                    //if source not exist, create
                    if (!s.graph.nodes(n.uniqueId)) {
                        s.graph.addNode({
                            id: n.uniqueId,
                            label: n.uniqueId,
                            x: Math.random(),
                            y: Math.random(),
                            size: 1,
                            color: getColor(n.version),
                            border_color: '#00f',
                            border_size: 1
                        });
                    }

                    $.each(node[k].relatives, function(i, rel) {
                        //console.log(rel);
                        //console.log(n.uniqueId + "->" + rel[1].data.uniqueId);

                        //if target not exist, create
                        if (!s.graph.nodes(rel[1].data.uniqueId)) {
                            s.graph.addNode({
                                id: rel[1].data.uniqueId,
                                label: rel[1].data.uniqueId,
                                x: Math.random(),
                                y: Math.random(),
                                size: 1,
                                color: getColor(rel[1].data.version),
                                border_color: '#00f',
                                border_size: 1
                            });
                        }

                        var relUniqueId = n.uniqueId + "-" + rel[0].toLowerCase() + "-" + rel[1].data.uniqueId;
                        if (!s.graph.edges(relUniqueId)) {
                            s.graph.addEdge({
                                id: relUniqueId,
                                // Reference extremities:
                                source: n.uniqueId,
                                target: rel[1].data.uniqueId,
                                color: '000',
                                type: 'arrow',
                                label: rel[0].toLowerCase()
                            });
                        }

                    });
                } //for-k
            });
            //console.log("refreshing...")
            s.refresh();
            updateLabel("[" + s.graph.nodes().length + "] Nodes</br>[" + s.graph.edges().length + "] Edges");
            $("#autoarrangebutton").click();

        }
    );
}

function getFilterValue(id) {
    var o = $(id).val();
    if (o == undefined || o == "") {
        o = ".*";
        $(id).val(o);
    }
    return o;
}

function getSearchValue(field, id) {
    var o = $(id).val();
    if (o == undefined || o == "") {
        return "";
    }
    return field + ":'" + o + "' ,";
}

function getSearchWhereClause(idx){

    var whereclause = "";

    whereclause += " WHERE n"+idx+".groupId =~ \"" + getFilterValue("#filterG") + "\"";
    whereclause += " AND n"+idx+".artifactId =~ \"" + getFilterValue("#filterA") + "\"";
    whereclause += " AND n"+idx+".packaging =~ \"" + getFilterValue("#filterP") + "\"";
    whereclause += " AND n"+idx+".classifier =~ \"" + getFilterValue("#filterC") + "\"";
    whereclause += " AND n"+idx+".version =~ \"" + getFilterValue("#filterV") + "\"";
    idx++;
    whereclause += " AND n"+idx+".groupId =~ \"" + getFilterValue("#filterG2") + "\"";
    whereclause += " AND n"+idx+".artifactId =~ \"" + getFilterValue("#filterA2") + "\"";
    whereclause += " AND n"+idx+".packaging =~ \"" + getFilterValue("#filterP2") + "\"";
    whereclause += " AND n"+idx+".classifier =~ \"" + getFilterValue("#filterC2") + "\"";
    whereclause += " AND n"+idx+".version =~ \"" + getFilterValue("#filterV2") + "\"";
    return whereclause;
}

function doCypherSearch() {

    var depth = parseInt($('#searchDepth').val());
    var chain = "-[r1]->(n2)"
    //fist section stays the same
    var fixedQuery = ""
    fixedQuery += "MATCH (n1:Artifact { ";
    fixedQuery += getSearchValue('groupId', '#searchG');
    fixedQuery += getSearchValue('artifactId', '#searchA');
    fixedQuery += getSearchValue('packaging', '#searchP');
    fixedQuery += getSearchValue('version', '#searchV');
    fixedQuery += "classifier: '" + $('#searchC').val() + "'";

    
    var query = "";
    query += fixedQuery;
    query += " })"+chain+" with n1 as node, [type(r1), n2] as relative";
    query += getSearchWhereClause(1);
    query += " RETURN { root: node, relatives: collect(relative) }"

    for(var i = 2 ; i <= depth; i++){
        chain += "-[r"+i+"]->(n"+(i+1)+")"

        query += " UNION "
        query += fixedQuery
        query += " })"+chain+" with n"+i+" as node, [type(r"+i+"), n"+(i+1)+"] as relative";
        query += getSearchWhereClause(2);
        query += " RETURN { root: node, relatives: collect(relative) }"
    }
    

    $("#cypher_search").val(query);
    doCypher(query);

}

function doCypherAll() {

    var query = "MATCH n-[r]->n2 with n, [type(r), n2] as relative";

    query += " WHERE n2.groupId =~ \"" + getFilterValue("#filterG") + "\""
    query += " AND n2.artifactId =~ \"" + getFilterValue("#filterA") + "\""
    query += " AND n2.packaging =~ \"" + getFilterValue("#filterP") + "\""
    query += " AND n2.classifier =~ \"" + getFilterValue("#filterC") + "\""
    query += " AND n2.version =~ \"" + getFilterValue("#filterV") + "\""

    query += " AND n.groupId =~ \"" + getFilterValue("#filterG2") + "\""
    query += " AND n.artifactId =~ \"" + getFilterValue("#filterA2") + "\""
    query += " AND n.packaging =~ \"" + getFilterValue("#filterP2") + "\""
    query += " AND n.classifier =~ \"" + getFilterValue("#filterC2") + "\""
    query += " AND n.version =~ \"" + getFilterValue("#filterV2") + "\""

    query += " return { root: n, relatives: collect(relative) }";

    $("#cypher_all").val(query);
    doCypher(query);
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