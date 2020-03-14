var root = document.body

var Server = {
    config: {},
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

var Header = {
    oninit: Server.loadConfig,
    view: function() {
        return m("div", { style: "Font-family: IBM Plex Sans,sans-serif; font-weight: 780; background-color:#1c3e73; padding: 10px; color:white;" }, [
                   m("h1", { style : "font-size: 2 rem; margin:0px"},
                         m("a", { href: "/mithril.html", style: "text-decoration: none; color:white" }, [
                                m("img", {src: "assets/zeebe-icon.svg", height: 40, width: 40, style: "padding-right: 10px" }),
                                "zeebe Cockpit"
                         ])
                   ),
                   m("div", {style: "position:absolute; top:30px;right: 10px"}, "Cluster @ " + Server.config.contactPoint)
               ])
    }
}

m.mount(root, Header)
