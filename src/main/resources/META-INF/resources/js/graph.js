// not sure why this needs to be in a different file. I tried putting these methods into cockpit.js, but it wouldn't work

function waitForElement(selector) {
  return new Promise(function(resolve, reject) {
    var element = document.querySelector(selector);

    if(element) {
      resolve(element);
      return;
    }

    var observer = new MutationObserver(function(mutations) {
      mutations.forEach(function(mutation) {
        var nodes = Array.from(mutation.addedNodes);
        for(var node of nodes) {
          if(node.matches && node.matches(selector)) {
            observer.disconnect();
            resolve(node);
            return;
          }
        };
      });
    });

    observer.observe(document.documentElement, { childList: true, subtree: true });
  });
}

waitForElement("#myChart").then(function(element) {

    var ctx = document.getElementById('myChart').getContext('2d');

    //Defined in cockpit.js
    myChart = new Chart(ctx, {
        type: 'radar',
        data: {
            labels: [],
            datasets: [
            {
                label: "Partitions (Total)",
                data: [],
                backgroundColor: 'rgba(28, 62,115, 0.2)'
            },
            {
                label: "Partitions (Leader)",
                data: [],
                backgroundColor: 'rgba(255, 62,115, 0.2)'
            },
            ]
        },
        options: {
            scale: {
                ticks: {
                    beginAtZero: true,
                    precision: 0,
                    fontSize: 20,
                }
            }
        }
    });

});