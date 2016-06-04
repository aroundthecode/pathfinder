
var nrnd = function NRNDraw(data){
    s.graph.clear();
    //console.log(data);
    $.each(data, function(i, row) {
    	//console.log(i,row);
    	var n1 = row.n1
    	var n2 = row.n2
    	var rel   = row.r

    	if (!s.graph.nodes(n1.uniqueId)) {
            s.graph.addNode({
                id: n1.uniqueId,
                label: n1.uniqueId,
                x: Math.random(),
                y: Math.random(),
                size: 1,
                color: getColor(n1.version),
                border_color: '#00f',
                border_size: 1
            });
        }

        if (!s.graph.nodes(n2.uniqueId)) {
            s.graph.addNode({
                id: n2.uniqueId,
                label: n2.uniqueId,
                x: Math.random(),
                y: Math.random(),
                size: 1,
                color: getColor(n2.version),
                border_color: '#00f',
                border_size: 1
            });
        }

        var relUniqueId = n1.uniqueId + "-" + rel.toLowerCase() + "-" + n2.uniqueId;
        if (!s.graph.edges(relUniqueId)) {
            s.graph.addEdge({
                id: relUniqueId,
                // Reference extremities:
                source: n1.uniqueId,
                target: n2.uniqueId,
                color: '000',
                type: 'arrow',
                label: rel.toLowerCase()
            });
        }

    });
    s.refresh();
    $("#autoarrangebutton").click();
}

function filterAll(gn1,an1,pn1,cn1,vn1,gn2,an2,pn2,cn2,vn2){

	var url = "/query/filterall?";
	url += "gn1="+gn1;
	url += "&an1="+an1;
	url += "&pn1="+pn1;
	url += "&cn1="+cn1;
	url += "&vn1="+vn1;

	url += "&gn2="+gn2;
	url += "&an2="+an2;
	url += "&pn2="+pn2;
	url += "&cn2="+cn2;
	url += "&vn2="+vn2;

	$.ajax(url,{
      type: "GET",
      //beforeSend: function(){ waitingDialog({}); },
      error: function (jqXHR, textStatus, errorThrown) {alert("Error invoking filterAll:"+errorThrown);},
      success: nrnd,
      //complete: function(){ closeWaitingDialog(); },
      dataType: "json"
    });

}