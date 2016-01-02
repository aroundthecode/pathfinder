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

