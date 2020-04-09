var root = document.body

var myChart = {};

var ChartUpdater = {
    update: function() {
        if (myChart) {
            if (Cluster.status) {

                var labels = [];
                var totalPartitionCount = [];
                var leaderPartitionCount = [];

                Cluster.status.nodeStatus.brokerStats.forEach(brokerStat => {
                    labels.push("Broker" + brokerStat.nodeId);
                    totalPartitionCount.push(brokerStat.leaderPartitionCount + brokerStat.followerPartitionCount);
                    leaderPartitionCount.push(brokerStat.leaderPartitionCount);
                });

                myChart.data.labels = labels;
                myChart.data.datasets[0].data = totalPartitionCount;
                myChart.data.datasets[1].data = leaderPartitionCount;

            } else {
                myChart.data.labels =[];
                myChart.data.datasets[0].data = [];
                myChart.data.datasets[1].data = [];
            }
            myChart.update();
        }
    }
}

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
            ChartUpdater.update();
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
            ChartUpdater.update();
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

var NodeDetailRenderer = {
    nodeDetail: function(brokerStat) {
        return m("li", "Broker" + brokerStat.nodeId + ": " + brokerStat.leaderPartitionCount + " partitions as leader, " + brokerStat.followerPartitionCount + " partitions as follower" )
    }
}

var NodeDetails = {
    view: function() {
        return m("div", {class: "column"}, [
            m("h3", "Node Details"),
            m("ul", Cluster.status.nodeStatus.brokerStats.map(NodeDetailRenderer.nodeDetail)),
            m("div", {class: "chartContainer"},
                m("canvas", {id:"myChart", width:"400", height:"400"})
            )
         ])
    }
}

var PartitionRendererHelper = {
    leaderBox: function(id) {
        return m("div", {class: "leaderBox"},  "L" + id);
    },
    followerBox: function(id) {
         return m("div", {class: "followerBox"}, "F" + id);
    },
    replicationStatusIndicator: function(replicationStatus) {
        var result;

        switch(replicationStatus) {
          case "IDEAL":
          case "OVERREPLICATED":
            result = m("div", {class: "greenDot"}, "G");
            break;
          case "UNDERREPLICATED":
            result = m("div", {class: "yellowDot"}, "Y");
            break;
          case "MISSING":
            result = m("div", {class: "yellowDot"} , "R");
            break;
          default:
            result = m("div", "?");
        }

        return result;
    }
}

var PartitionDetailRenderer = {
    partitionDetail: function(partitionStat) {
        return m("div", [
            m("div",{class: "blockRow"},   "Partition" + partitionStat.partitionId),
            PartitionRendererHelper.replicationStatusIndicator(partitionStat.replicationStatus),
            m("div", {class: "blockRow"}, partitionStat.leaders.map(PartitionRendererHelper.leaderBox) ),
            m("div", {class: "blockRow"}, partitionStat.followers.map(PartitionRendererHelper.followerBox) )
            ])
    }
}


var PartitionDetails = {
    view: function() {
        return m("div", {class: "column"}, [
            m("h3", "Partition Details"),
            m("div", Cluster.status.partitionStatus.partitionStats.map(PartitionDetailRenderer.partitionDetail))
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
            ]),
            m("div", {class: "row"}, [
                            m(NodeDetails),
                            m(PartitionDetails),
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
