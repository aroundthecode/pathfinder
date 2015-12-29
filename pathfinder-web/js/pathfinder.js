var s = new sigma();
    // UI init
    $(function() {

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


    var n4juser = "neo4j";
    var n4jpassword =  "asdf10";
    var n4jurl = "http://localhost:8686";
    var n4jendopint= "/db/data/cypher";

    var n4jcypher= "match n-[r]->n2 with n, [type(r), n2] as relative return { root: n, relatives: collect(relative) }"
    doCypher(n4jcypher);


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
                      //type: 'curvedArrow',
                      label: rel[0].toLowerCase().toLowerCase()
                    });
                    }

                  });
                  
                });
          console.log("refreshing...")
           s.refresh();
          $( "#forceatlasbutton" ).click();
          
          }
    );
  }

  function doCypherAll(){
    var g = $("#filterG").val();
    var a = $("#filterA").val();
    var p = $("#filterP").val();
    var c = $("#filterC").val();
    var v = $("#filterV").val();

    var query= "match n-[r]->n2 with n, [type(r), n2] as relative";
    query += " where n.groupId =~ \""+g+"\""
    query += " and n.artifactId =~ \""+a+"\""
    query += " and n.packaging =~ \""+p+"\""
    query += " and n.classifier =~ \""+c+"\""
    query += " and n.version =~ \""+v+"\""
    query += " return { root: n, relatives: collect(relative) }";

    $("#cypher_all").val(query);
    doCypher(query);
  }