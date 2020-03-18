var root = document.body

var Title = {
    view: function() {
        return m("h1", { style : "font-size: 2 rem; margin:0px"},
                        m("a", { href: "/", style: "text-decoration: none; color:white" }, [
                                m("img", {src: "assets/zeebe-icon.svg", height: 40, width: 40, style: "padding-right: 10px" }),
                                "zeebe Cockpit"
                        ])
                )
    }
}

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
    oninit: Cluster.subscribe,
    view: function() {
        return m("div", { style: "Font-family: IBM Plex Sans,sans-serif; font-weight: 780; background-color:#1c3e73; padding: 10px; color:white;" }, [
                   m(Title),
                   m(UrlAndConnectionStatus)
               ])
    }
}

m.mount(root, Header)
