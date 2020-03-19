var root = document.body

// state
var Server = {
    config: {}, //will be loaded from server
    loadConfig: function() {
        return m.request({
            method: "GET",
            url: "/info/config",
            withCredentials: true,
        })
        .then(function(result) {
            console.log(result)
            Server.config = result
        })
    },
}

var Cluster = {
    status: {}, //will be loaded from server
    loadStatus: function() {
        return m.request({
            method: "GET",
            url: "/status",
            withCredentials: true,
         })
         .then(function(result) {
            console.log(result)
            Cluster.status = result
         })
    },
    subscribe: function() {
        var socket = new WebSocket("ws://" + location.host + "/status/feed");
        socket.onopen = function() {
            console.log("Connected to the web socket");
        };
        socket.onmessage =function(message) {
            var status = JSON.parse(message.data)

            Cluster.status = status
            m.redraw();
        };
    }
}

// views
var Title = {
    view: function() {
        return m("h1",
                        m("a", { href: "/", style: "text-decoration: none; color:white" }, [
                                m("img", {src: "assets/zeebe-icon.svg", height: 40, width: 40, style: "padding-right: 10px" }),
                                "zeebe Cockpit"
                        ])
                )
    }
}

var Url = {
    oninit: Server.loadConfig,
    view: function() {
       if(!Server.config.contactPoint){
            return ""
       } else {
            return "Cluster @ " + Server.config.contactPoint + " "
       }
    }
}

var ConnectionStatus = {
    oninit: Cluster.loadStatus,
    view: function() {
        if (Cluster.status.connected) {
            return m("i", {class: "material-icons-outlined"}, "cloud")
        } else {
            return  m("i", {class: "material-icons-outlined"}, "cloud_off")
        }

    }
}

var UrlAndConnectionStatus = {
    view: function() {
        return  m("div", {style: "position:absolute; top:30px;right: 10px"}, [
                    m(Url),
                    m(ConnectionStatus),
                ])
    }
}


var Header = {
    view: function() {
        return m("div", { class: "header" }, [
                   m(Title),
                   m(UrlAndConnectionStatus)
               ])
    }
}

var fallbackToQuestionMark = function (supplier) {
    if (Cluster.status.connected) {
        return supplier();
    } else {
        return "?"
    }
}

var NodeStatus = {
    view: function() {
        return m("div", {class: "column"}, [
            m("h3", "Node Status"),
            m("div", "Cluster Size: " + fallbackToQuestionMark(() => Cluster.status.nodeStatus.clusterSize)),
            m("div", "Live Nodes: " + fallbackToQuestionMark(() => Cluster.status.nodeStatus.liveNodes)),
            m("div", "Dead Nodes: " + fallbackToQuestionMark(() => Cluster.status.nodeStatus.missingNodes)),
         ])
    }
}

var PartitionStatus = {
    view: function() {
        return m("div", {class: "column"}, [
            m("h3", "Partition Status"),
            m("div", "Partitions: " + fallbackToQuestionMark(() => Cluster.status.partitionStatus.partitionCount)),
            m("div", "Overreplicated: " + fallbackToQuestionMark(() => Cluster.status.partitionStatus.partitionCountByReplicationStatus.OVERREPLICATED)),
            m("div", "Ideal: " + fallbackToQuestionMark(() => Cluster.status.partitionStatus.partitionCountByReplicationStatus.IDEAL)),
            m("div", "Underreplicated: " + fallbackToQuestionMark(() => Cluster.status.partitionStatus.partitionCountByReplicationStatus.UNDERREPLICATED)),
            m("div", "Missing: " + fallbackToQuestionMark(() => Cluster.status.partitionStatus.partitionCountByReplicationStatus.MISSING)),
         ])
    }
}

var ClusterOverview = {
    view: function() {
        return m("div", { class: "clusterOverview" }, [
            m("h2", "Cluster Overview"),
            m("div", {class: "row"}, [
                m(NodeStatus),
                m(PartitionStatus),
            ])
         ])
    }
}

var Cockpit = {
    oninit: Cluster.subscribe,
    view: function() {
       return [
                m(Header),
                m(ClusterOverview)
              ]
    }
}

m.mount(root, Cockpit)
