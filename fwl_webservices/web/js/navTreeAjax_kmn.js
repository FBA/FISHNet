/**
 * Kearon McNicol's simple ajax code.
 * First function is for the basic ajax request class
 * Other methods send and handle various requests needed on the page.
 */


/**
 * Ajax request class
 */
function createRequestObject(){

    var req;

    if(window.XMLHttpRequest){
        //For Firefox, Safari, Opera
        req = new XMLHttpRequest();
    }
    else if(window.ActiveXObject){
        //For IE 5+
        req = new ActiveXObject("Microsoft.XMLHTTP");
    }
    else{
        //Error for an old browser
        alert('Apologies, this application makes extensive use of Javascript (otherwise known as EMCEA).\n\
                You either have javascript disabled\n\
                or your browser is not IE 5 or higher, or Firefox or Safari or Opera');
    }

    return req;
}


/*
 * methods for handling the navigation lists drop-down box.
 */

//Make the method specific XMLHttpRequest Object
var listsReq = createRequestObject();

//make ajax call
function requestLists(method, url){
    if(method == 'get' || method == 'GET'){
        listsReq.open(method,url);
        listsReq.onreadystatechange = handleListsResponse;
        listsReq.send(null);
    }
}
//deal with ajax response
function handleListsResponse(){

    //lists request
    if(listsReq.readyState == 4 && listsReq.status == 200){
        var response = listsReq.responseText;
        if(response){
            populateTreeSelection(listsReq.responseXML.documentElement);
        }
    }
}

//populate the drop-down list
function populateTreeSelection(xmlList){
    var treeSelector = document.getElementById("treeSelector");

    //clear the selection list
    for (var count = treeSelector.options.length-1; count >-1; count--){
        treeSelector.options[count] = null;
    }

    //get the relevant xml elements in an array liike format
    var trees = xmlList.getElementsByTagName('tree');

    //add the request to select a tree to the drop down
    defaultItem = new Option("Select navigation tree");
    treeSelector.options[treeSelector.length] = defaultItem;

    //cycle round the xml elements, populating the drop down as we go
    var textValue;
    var startPoint;
    for (var x = 0; x < trees.length; x ++){
        textValue = GetInnerText(trees[x]);
        startPoint = trees[x].getAttribute("start");
        optionItem = new Option( textValue, startPoint, false, false);
        treeSelector.options[treeSelector.length] = optionItem;
    }


}
// returns the node text value
function GetInnerText (node)
{
    return (node.textContent || node.innerText || node.text) ;
}


//variable that will allow drop-down to pass list selection to the tree.
var selectedTree = "fwl_novice";

//handle drop down selection
function changeTree(dropdown){
    var index = dropdown.selectedIndex;
    var treeName = dropdown[index].text;
    var startPoint = dropdown[index].value;

    if (treeName != "Select navigation tree"){
        document.getElementById("test").innerHTML = treeName + " " + startPoint;
        selectedTree=treeName;
        initialiseTree_kmn();
    }
}

/**
 * tree functions - these build on dhtmlx code
 * note - baseUrl_js has to be set in the jsp page
 */
var sppTree;
function initialiseTree_kmn(){
    var divToPlaceTree = "sppTreeDiv";

    //make sure we have a clean start
    sppTree = new dhtmlXTreeObject(divToPlaceTree,"100%","100%",0); //in case this is the first time
    sppTree.destructor(); //clear tree and clean memory

    //now set up the tree
    sppTree = new dhtmlXTreeObject(divToPlaceTree,"100%","100%",0);
    sppTree.setImagePath(baseUrl_js + "js/dhtmlx/imgs/csh_scbrblue/");
    sppTree.setXMLAutoLoading(baseUrl_js + "SppNav/tree/"+selectedTree);
    sppTree.loadXML(baseUrl_js + "SppNav/tree/"+selectedTree);
    sppTree.attachEvent("onSelect",nodeSelection);

}

//process the selection of an item
function nodeSelection(nodeId){
    requestItemInfo('GET', baseUrl_js + "SppNav/item/" + nodeId);
}

/*
 * methods for handling tree selection
 */

//Make the method specific XMLHttpRequest Object
var itemReq = createRequestObject();

//make ajax call
function requestItemInfo(method, url){
    if(method == 'get' || method == 'GET'){
        itemReq.open(method,url);
        itemReq.onreadystatechange = handleItemInfo;
        itemReq.send(null);
    }
}
//deal with ajax response
function handleItemInfo(){

    //lists request
    if(itemReq.readyState == 4 && itemReq.status == 200){
        var response = itemReq.responseText;
        if(response){
         itemXml = itemReq.responseXML.documentElement;
         taxon = itemXml.getElementsByTagName('taxon')[0];
         nav_parent = itemXml.getElementsByTagName('nav_parent');
         children = itemXml.getElementsByTagName('no_children');
         tree = itemXml.getElementsByTagName('nav_list');
            document.getElementById("selectedNode").innerHTML = "Selection: "+taxon.getAttribute("taxon_id")+ " - " +taxon.textContent;
        }
    }
}
