var types = ["COMPILE", "PROVIDED", "RUNTIME", "TEST", "SYSTEM", "IMPORT"];

var s = new sigma();

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

    //Dependency Management button setup
    $("#depmngbut").click(function() {
        dependencyManagement();
    });

    $.fn.tabbedDialog = function () {
        this.tabs();
        this.dialog({'modal':true,'minWidth':800, 'minHeight':600,'draggable':true});
        this.find('.ui-tab-dialog-close').append($('a.ui-dialog-titlebar-close'));
        this.find('.ui-tab-dialog-close').css({'position':'absolute','right':'0', 'top':'23px'});
        this.find('.ui-tab-dialog-close > a').css({'float':'none','padding':'0'});
        var tabul = this.find('ul:first');
        this.parent().addClass('ui-tabs').prepend(tabul).draggable('option','handle',tabul); 
        this.siblings('.ui-dialog-titlebar').remove();
        tabul.addClass('ui-dialog-titlebar');
    }
   
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
    //console.log(e);

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
    
    $('#depmng_modal').tabbedDialog();
    //console.log(deps);

    var id = 0;
    var dpmngxml = "&lt;dependencyManagement&gt;\n"
    for (var d in deps) {
        //console.log(deps[d])
        var list = deps[d].list;
        gridData.push( { Id:id, Name:d, Num:list.length, Version:"", Scope:"",UsedBy:"",enbl:"0", isLeaf:false, loaded:true , expanded:false, parent:"",level:"0"} );
        
        for (var j = 0; j<list.length; j++) {
            dd = deps[d].list[j];
            gridData.push( {Id:id+"_"+j, Name:d, Num:"", Version:dd.ver, Scope:dd.type, UsedBy:dd.by ,enbl:(j==0?"1":"0"),isLeaf:true, loaded:true, expanded:true, parent:id,level:"1"} );
            if(j==0){
                ddd = d.split(":");
                dpmngxml += "\t&lt;dependency&gt;\n"
                dpmngxml += "\t\t&lt;groupId&gt;"+ddd[0]+"&lt;/groupId&gt;\n"
                dpmngxml += "\t\t&lt;artifactId&gt;"+ddd[1]+"&lt;/artifactId&gt;\n"
                dpmngxml += "\t\t&lt;version&gt;"+dd.ver+"&lt;/version&gt;\n"
                if(ddd[2]!="jar"){
                    dpmngxml += "\t\t&lt;type&gt;"+ddd[2]+"&lt;/type&gt;\n"
                }
                if(ddd[3]!= undefined && ddd[3]!=""){
                    dpmngxml += "\t\t&lt;classifier&gt;"+ddd[3]+"&lt;/classifier&gt;\n"
                }
                dpmngxml += "\t&lt;/dependency&gt;\n"
            }
        }
        id++;
    };
     dpmngxml += "&lt;/dependencyManagement&gt;"
     $("#depmng_conf_xml").html(dpmngxml);
     
    //console.log( gridData );
/*
    $("#depmng_grid").jqGrid({
                datatype: "jsonstring",
                datastr: gridData,
                colNames:["Id","Num","Name","Version","Scope","UsedBy","Use"],
                colModel:[
                    { index: 'Id', name: 'Id', label: 'Id', width: 300, width:1, hidden:true,key: true,sorttype:"int" },
                    { index: 'Name', name: 'Name', label: 'Name', width: 300,sorttype:"string" },
                    { index: 'Num', name: 'Num', label: 'Num', width: 10 , align: 'center',sorttype:"int"},
                    { index: 'Version', name: 'Version', label: 'Version', width: 50 , align: 'right',sorttype:"string"},
                    { index: 'Scope', name: 'Scope', label: 'Scope', width: 50 , align: 'center',sorttype:"string" },
                    { index: 'UsedBy', name: 'UsedBy', label: 'UsedBy', width: 300 ,sorttype:"string"},
                    {name:'enbl', index:'enbl', width: 30, align:'center',
                     formatter:'checkbox', editoptions:{value:'1:0'},
                     formatoptions:{disabled:false}}
                ],
                height: 480,
                gridview: true,
                rowNum: 1000,

                hoverrows:false,
                viewrecords:false,
                scrollrows:true,
                rowList: [100,200,500],
                pager: "#depmng_grid_pager",
                sortname: 'Num',
                treeGrid: true,
                treeGridModel: 'nested',
                treedatatype: "local",
                ExpandColumn: 'Name',
                treeReader:{
                    level_field:"level",
                    leaf_field:"isLeaf",
                    expanded_field:"expanded",
                    loaded:"loaded",
                    icon_field:"icon"
                },
                sortorder:"asc",
                jsonReader: {
                    repeatitems: false,
                    root: function (obj) { return obj; },
                    page: function (obj) { return 1; },
                    total: function (obj) { return 1; },
                    records: function (obj) { return obj.length; }
                }
            });
    */
    $("#depmng_grid").jqGrid({
            datatype: "jsonstring",
                datastr: gridData,
                colModel:[
                    { index: 'Id', name: 'Id', label: 'Id', width:1, hidden:true,key: true,sorttype:"int" },
                    { index: 'Name', name: 'Name', label: 'Name', width: 300,sorttype:"string" },
                    { index: 'Num', name: 'Num', label: 'Num', width: 10 , align: 'center',sorttype:"int"},
                    { index: 'Version', name: 'Version', label: 'Version', width: 50 , align: 'right',sorttype:"string"},
                    { index: 'Scope', name: 'Scope', label: 'Scope', width: 50 , align: 'center',sorttype:"string" },
                    { index: 'UsedBy', name: 'UsedBy', label: 'UsedBy', width: 300 ,sorttype:"string"},
                    {name:'enbl', index:'enbl', width: 30, align:'center',
                     formatter:'checkbox', editoptions:{value:'1:0'},
                     formatoptions:{disabled:false}},
                    {
                        "name":"level",
                        "hidden":true
                    }
                ],
                "width":"770",
                "hoverrows":false,
                "viewrecords":false,
                "gridview":true,
                "height":480,//"auto",
                "sortname":"Num",
                "loadonce":true,
                "rowNum":100,
                "scrollrows":true,
                // enable tree grid
                "treeGrid":true,
                // which column is expandable
                "ExpandColumn":"Name",
                // datatype
                "treedatatype":"json",
                // the model used
                "treeGridModel":"nested",
                // configuration of the data comming from server
                "treeReader":{
                    "left_field":"lft",
                    "right_field":"rgt",
                    "level_field":"level",
                    "leaf_field":"isLeaf",
                    "expanded_field":"expanded",
                    "loaded":"loaded",
                    "icon_field":"icon"
                },
                "sortorder":"asc",
                "pager":"#depmng_grid_pager"
            }); 

}

