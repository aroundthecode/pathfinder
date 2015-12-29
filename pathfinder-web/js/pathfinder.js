var n4juser = "neo4j";
var n4jpassword =  "asdf10";
var n4jurl = "http://localhost:8686";
var n4jendopint= "/db/data/cypher";
var s = new sigma();

    // UI init
    $(function() {
      $( '#toggle-cypher_all' ).hide();
      var frListener = sigma.layouts.fruchtermanReingold.configure(s, {
  iterations: 500,
  easing: 'quadraticInOut',
  duration: 800
});

// Bind the events:
frListener.bind('start stop interpolate', function(e) {
  console.log(e.type);
});


      $( "#forceatlasbutton" ).click(function(){
          sigma.layouts.fruchtermanReingold.start(s);
        });
      $( "#accordion" ).accordion({
        heightStyle: "fill"
      });
      
  });
    // Let's first initialize sigma:
    
    //s.settings.defaultNodeType='square';


    s.addCamera('cam1'),
    s.addRenderer({
      container: document.getElementById('graph'),
      type: 'canvas',
      camera: 'cam1'
      }
    );




    //populate with full data first
    doCypherAll();

  function getColor(v){
    out = '#00BDFC';

    if(v.search(/SNAPSHOT/i)!=-1){
      out = '#FF4E03';
      
    }
    else if( v.replace(/[0-9]*(\.[0-9]*)*/g,'').length > 0 ){
      out = '#FFD103';
      
    }
    return out;
  }

  function doCypher(query){

    sigma.neo4j.send(
    { url: n4jurl, user: n4juser, password: n4jpassword },
    n4jendopint,
    "POST",
    '{"query" : '+JSON.stringify(query)+'}',
    function(res) {
                s.graph.clear();
                //console.log(res);
               
                $.each(res.data, function(i, node) {
                  n = node[0].root.data;
                  //console.log(n);

                  //if source not exist, create
                  if(! s.graph.nodes(n.uniqueId)){
                    s.graph.addNode({
                    id:n.uniqueId, 
                    label: n.uniqueId,
                    x: Math.random(),
                    y: Math.random(),
                    size: 1,
                    color: getColor(n.version),
                    border_color: '#00f',
                    border_size: 1
                  });
                  }
                  
                  $.each(node[0].relatives, function(i, rel) {
                    //console.log(rel);
                    //console.log(n.uniqueId + "->" + rel[1].data.uniqueId);

                    //if target not exist, create
                  if(! s.graph.nodes(rel[1].data.uniqueId)){
                    s.graph.addNode({
                    id:rel[1].data.uniqueId, 
                    label: rel[1].data.uniqueId,
                    x: Math.random(),
                    y: Math.random(),
                    size: 1,
                    color: getColor(rel[1].data.version),
                    border_color: '#00f',
                    border_size: 1
                    });
                  }

                    if( ! s.graph.edges(n.uniqueId+"-"+rel[1].data.uniqueId)){ 
                    s.graph.addEdge({
                      id: n.uniqueId+"-"+rel[1].data.uniqueId,
                      // Reference extremities:
                      source: n.uniqueId,
                      target: rel[1].data.uniqueId,
                      color: '000',
                      type: 'arrow',
                      label: rel[0].toLowerCase().toLowerCase()
                    });
                    }

                  });
                  
                });
          console.log("refreshing...")
           s.refresh();
           updateLabel();
          $( "#forceatlasbutton" ).click();
          
          }
    );
  }

  function getFilterValue(id){
    var o = $(id).val();
    if(o==undefined || o==""){
      o=".*";
      $(id).val(o);
    }
    return o;
  }

  function doCypherAll(){

    var query= "match n-[r]->n2 with n, [type(r), n2] as relative";

    query += " where n2.groupId =~ \"" + getFilterValue("#filterG") + "\""
    query += " and n2.artifactId =~ \"" + getFilterValue("#filterA") + "\""
    query += " and n2.packaging =~ \"" + getFilterValue("#filterP") + "\""
    query += " and n2.classifier =~ \"" + getFilterValue("#filterC") + "\""
    query += " and n2.version =~ \"" + getFilterValue("#filterV") + "\""

    query += " and n.groupId =~ \"" + getFilterValue("#filterG2") + "\""
    query += " and n.artifactId =~ \"" + getFilterValue("#filterA2") + "\""
    query += " and n.packaging =~ \"" + getFilterValue("#filterP2") + "\""
    query += " and n.classifier =~ \"" + getFilterValue("#filterC2") + "\""
    query += " and n.version =~ \"" + getFilterValue("#filterV2") + "\""

    query += " return { root: n, relatives: collect(relative) }";

    $("#cypher_all").val(query);
    doCypher(query);
  }

  function updateLabel(){
    $("#graph-label").html("["+s.graph.nodes().length + "] Nodes</br>["+s.graph.edges().length + "] Edges");
  }